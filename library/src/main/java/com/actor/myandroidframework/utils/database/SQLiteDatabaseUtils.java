package com.actor.myandroidframework.utils.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.AssetsUtils;
import com.actor.myandroidframework.utils.ConfigUtils;
import com.blankj.utilcode.util.ReflectUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 安卓原生SQLiteDatabase简单封装, 读取本地数据库, 并封装成实体
 * 使用:
 *  1.在Application中调用方法初始化: {@link #initDatabase(String...)}
 *                              or {@link #initDatabase(boolean, String...)}
 *                              or {@link #initDatabase(boolean, int, String)}
 *
 * Author     : ldf
 * Date       : 2017/1/19 on 11:15.
 * TODO: 2021/5/19 还有一些 增删改/查(分页) 方法未封装.
 *
 * @deprecated 使用 {@link GreenDaoUtils} 也可以读取.db文件, 所以不推荐这个工具类.
 *             使用 GreenDao 示例: https://www.jianshu.com/p/9cc0870620c1
 *                  @Entity(nameInDb = "user", createInDb = false)
 *                  public class User {
 *                      @Id
 *                      @Property(nameInDb = "id")
 *                      public Long id;
 *
 *                      @Property(nameInDb = "name")
 *                      public String name;
 *
 *                      //...
 *                  }
 */
@Deprecated
public class SQLiteDatabaseUtils {

    //存储数据库
    protected static final Map<String, SQLiteDatabase> DATABASES = new LinkedHashMap<>();

    public static void initDatabase(String... dbNames) {
        initDatabase(false, dbNames);
    }

    /**
     * 初始化数据库. APP无法直接读取assets中数据库，必须将数据库复制到APP的数据库文件存储目录
     * @param isCover 如果文件已存在, 是否覆盖
     * @param dbNames assets/目录下的数据库名称, 例: address.db, users.db3 等...
     */
    public static boolean initDatabase(boolean isCover, String... dbNames) {
        if (dbNames == null || dbNames.length == 0) {
            throw new RuntimeException("数据库名称为空!");
        }
        for (String dbName : dbNames) {
            //params1: 以只读方式打开
            boolean success = initDatabase(isCover, SQLiteDatabase.OPEN_READONLY, dbName);
            if (!success) return false;
        }
        return true;
    }

    /**
     *
     * @param isCover 如果文件已存在, 是否覆盖
     * @param flags 控制数据库访问模式
     * @param dbName assets/目录下的数据库名称, 例: address.db, users.db3 等...
     */
    public static synchronized boolean initDatabase(boolean isCover, int flags, String dbName) {
        SQLiteDatabase sqLiteDatabase = DATABASES.get(dbName);
        if (isCover || sqLiteDatabase == null) {
            //.db文件
            File databaseFile = ConfigUtils.APPLICATION.getDatabasePath(dbName);
            String databasePath = databaseFile.getParent();
            boolean success = AssetsUtils.copyFile2Dir(isCover, dbName, databasePath);
            if (success) {
                /**
                 * 参1: 数据库文件的本地路径
                 * 参2: 游标工厂
                 * 参3: 控制数据库访问模式
                 */
                sqLiteDatabase= SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), null, flags);
                DATABASES.put(dbName, sqLiteDatabase);
            }
            return success;
        }
        return true;
    }


    /**
     * 查询所有
     * @param dbName 数据库名称
     * @param tableName 表名称
     * @param entity 需要解析成什么类型, 不能传null. 注意: 这个class必须有一个"无参构造方法"
     * @return 返回一个不为null的List
     */
    @NonNull
    public static <T> List<T> findAll(String dbName, String tableName, @NonNull Class<T> entity) {
        return findCustom(dbName, "select * from " + tableName, entity);
    }

    /**
     * 自定义条件查询
     * @param dbName 数据库名称
     * @param sql 自定义查询sql, 例: select * from user where id = ?
     * @param entity 需要解析成什么类型, 不能传null. 注意: 这个class必须有一个"无参构造方法"
     * @param selectionArgs 自定义查询参数, 例: 1
     * @return
     */
    @NonNull
    public static <T> List<T> findCustom(String dbName, String sql, @NonNull Class<T> entity, @Nullable String... selectionArgs) {
        SQLiteDatabase sqLiteDatabase = getSQLiteDatabase(dbName);
        Cursor cursor = sqLiteDatabase.rawQuery(sql, selectionArgs);
        List<T> list = cursor2List(cursor, entity);
        cursor.close();
        return list;
    }

    @NonNull
    protected static SQLiteDatabase getSQLiteDatabase(String dbName) {
        SQLiteDatabase sqLiteDatabase = DATABASES.get(dbName);
        if (sqLiteDatabase == null) {
            throw new RuntimeException("数据库未初始化!");
        }
        return sqLiteDatabase;
    }

    /**
     * 将 Cursor 查到的数据, 转list数据.
     * 注意: 目前功能仅支持数据库字段和实体字段名字完全相同, 才转换.
     * @param cursor 游标
     * @param entity 需要解析成什么类型, 不能传null. 注意: 这个class必须有一个"无参构造方法"
     * @return 返回一个不为null的List
     */
    @NonNull
    protected static <T> List<T> cursor2List(@Nullable Cursor cursor, @NonNull Class<T> entity) {
        List<T> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                T t = cursor2Entity(cursor, entity);
                if (t != null) list.add(t);
            }
        }
        return list;
    }


    /**
     * 将 Cursor 查到的数据, 转实体.
     * 注意: 目前功能仅支持数据库字段和实体字段名字完全相同, 才转换.
     * @param cursor 游标
     * @param entity 需要解析成什么类型, 不能传null. 注意: 这个class必须有一个"无参构造方法"
     * @return 返回一个有可能=null的实体
     */
    @Nullable
    protected static <T> T cursor2Entity(@Nullable Cursor cursor, Class<T> entity) {
        if (cursor == null) {
            return null;
        }
        //多少行数据
        //int count = cursor.getCount();

        //多少列数据
        int columnCount = cursor.getColumnCount();
        Field[] fields = entity.getDeclaredFields();
        T t = null;
        for (int i = 0; i < columnCount; i++) {
            //这一列的名称
            String columnName = cursor.getColumnName(i);
            //查找和数据库对应的实体字段
            Field columnField = null;
            for (Field field : fields) {
                if (field.getName().equals(columnName)) {
                    columnField = field;
                    break;
                }
            }
            if (columnField == null) {
                continue;
            } else if (t == null) {
                try {
                    t = entity.newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            Class<?> fieldType = columnField.getType();
            //这一行数据的类型
            int type = cursor.getType(i);
            switch (type) {
                case Cursor.FIELD_TYPE_INTEGER:
                    if (fieldType == short.class || fieldType == Short.class) {
                        short aShort = cursor.getShort(i);
                        ReflectUtils.reflect(t).field(columnName, aShort);
                    } else if (fieldType == int.class || fieldType == Integer.class) {
                        int anInt = cursor.getInt(i);
                        ReflectUtils.reflect(t).field(columnName, anInt);
                    } else if (fieldType == long.class || fieldType == Long.class) {
                        long aLong = cursor.getLong(i);
                        ReflectUtils.reflect(t).field(columnName, aLong);
                    }
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    if (fieldType == float.class || fieldType == Float.class) {
                        float aFloat = cursor.getFloat(i);
                        ReflectUtils.reflect(t).field(columnName, aFloat);
                    } else if (fieldType == double.class || fieldType == Double.class) {
                        double aDouble = cursor.getDouble(i);
                        ReflectUtils.reflect(t).field(columnName, aDouble);
                    }
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    String string = cursor.getString(i);
                    if (fieldType == String.class) {
                        ReflectUtils.reflect(t).field(columnName, string);
                    } else if (fieldType.isEnum()) {
                        //枚举内所有值
                        Enum[] enumConstants = (Enum[]) fieldType.getEnumConstants();
                        if (enumConstants != null && enumConstants.length > 0) {
                            for (Enum enumConstant : enumConstants) {
                                if (TextUtils.equals(enumConstant.name(), string)) {
                                    ReflectUtils.reflect(t).field(columnName, enumConstant);
                                    System.out.println(1);
                                }
                            }
                        }
                    }
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    if (fieldType == byte[].class || fieldType == Byte[].class) {
                        byte[] blob = cursor.getBlob(i);
                        ReflectUtils.reflect(t).field(columnName, blob);
                    }
                    break;
                case Cursor.FIELD_TYPE_NULL:
                default:
                    break;
            }
        }
        return t;
    }
}