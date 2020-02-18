package com.actor.myandroidframework.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * Description: GreenDao的增删改查帮助类
 *
 * 1.在Project的gradle文件中添加插件(root build.gradle)
 *   buildscript {
 *       repositories {
 *           mavenCentral() // add repository
 *       }
 *       dependencies {
 *           classpath 'org.greenrobot:greendao-gradle-plugin:3.2.2' // add plugin
 *       }
 *   }
 *
 * 2.在项目Module的gradle中添加插件(app/build.gradle)
 *   apply plugin: 'org.greenrobot.greendao' // apply plugin
 *   android {
 *       greendao {
 *         schemaVersion 1                 //指定数据库schema版本号，迁移等操作会用到
 *         daoPackage 'com.greendao.gen'   //dao的包名，包名默认是entity所在的包
 *         targetGenDir 'src/main/java'    //生成数据库文件的目录
 *       }
 *   }
 *   dependencies {
 *       //https://github.com/greenrobot/greenDAO
 *       implementation 'org.greenrobot:greendao:3.2.2' // add library
 *   }
 *
 * 3.在Application中初始化(需要先Build -> Make Project, 生成DaoMaster和DaoSession后才初始化, 否则空指针):
 *   GreenDaoUtils.init(this, isDebugMode, ItemEntityDao.class, ...);
 *
 * 获取某个Dao示例: ItemEntityDao dao = GreenDaoUtils.getDaoSession().getItemEntityDao();
 * 更多信息见文件: greenDao说明.java
 *
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/10/28 on 22:53
 *
 * @version 1.0
 * @version 1.0.1 增加一些增删改查的方法
 */
public class GreenDaoUtils {

    private static GreenDaoUtils instalce;
    private DaoMaster.OpenHelper openHelper;
    private static SQLiteDatabase       sqLiteDatabase;
    private DaoMaster            daoMaster;
    private static DaoSession           daoSession;

    /**
     * @param context application
     * @param isDebug 如果是debug模式, 数据库操作会打印日志
     * @param daoClasses 数据库表对应的实体(ItemEntity.java)的dao, 示例:
     *                   ItemEntityDao.class(由'Build -> Make Project'生成), ...
     */
    public static void init(Context context, boolean isDebug, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (instalce == null) instalce = new GreenDaoUtils(context, isDebug, daoClasses);
    }

    /**
     * 设置greenDAO
     */
    private GreenDaoUtils(Context context, boolean isDebug, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        openHelper = new /*DaoMaster.DevOpenHelper*/UpgradeAbleOpenHelper(context, "notes-db", null, daoClasses);

        //加密写法, 使用getEncryptedReadableDb()和getEncryptedWritableDb()获取加密的数据库
//        Database database = openHelper.getEncryptedWritableDb("aserbao"); //数据库加密密码为“aserbao"
        sqLiteDatabase = openHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(sqLiteDatabase);//database
        daoSession = daoMaster.newSession();

        //debug查询, 设置这两个属性就可以看到log
        QueryBuilder.LOG_SQL = isDebug;
        QueryBuilder.LOG_VALUES = isDebug;
    }

    /**
     * Description: 能数据库简单更新升级的OpenHelper
     * 不能使用默认的: {@link DaoMaster.DevOpenHelper}
     * @version 1.0
     */
    private class UpgradeAbleOpenHelper extends DaoMaster.OpenHelper {

        private Class<? extends AbstractDao<?, ?>>[] daoClasses;

        private UpgradeAbleOpenHelper(Context context, String name, Class<? extends AbstractDao<?, ?>>... daoClasses) {
            super(context, name);
            this.daoClasses = daoClasses;
        }

        private UpgradeAbleOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, Class<? extends AbstractDao<?, ?>>... daoClasses) {
            super(context, name, factory);
            this.daoClasses = daoClasses;
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            //默认会删除数据库, 并且重写创建数据库
//            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
//            dropAllTables(db, true);
//            onCreate(db);

            //默认super 什么都不做
//            super.onUpgrade(db, oldVersion, newVersion);

            /**
             * 升级思路：
             * 1.创建临时表TMP_,复制原来的数据库到临时表中；
             * 2.删除之前的原表；
             * 3.创建新表；
             * 4.将临时表中的数据复制到新表中，最后将TMP_表删除掉；
             */
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                @Override
                public void onCreateAllTables(Database db, boolean ifNotExists) {
                    DaoMaster.createAllTables(db, ifNotExists);
                }
                @Override
                public void onDropAllTables(Database db, boolean ifExists) {
                    DaoMaster.dropAllTables(db, ifExists);
                }
            }, daoClasses);

        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 静态方法区
    ///////////////////////////////////////////////////////////////////////////
    /**
     * @return 获取daoSession
     */
    public static DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * @return 获取数据库
     */
    public static SQLiteDatabase getSQLiteDatabase() {
        return sqLiteDatabase;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 增
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 增
     * @param dao 具体实体对应的dao
     * @param entity 具体实体
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 返回插入的实体在数据库的id
     */
    public static <T, K> long insert(AbstractDao<T, K> dao, T entity) {
        return dao.insert(entity);
    }

    /**
     * 增, 批量插入
     * @param dao 具体实体对应的dao
     * @param entities 具体实体
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 返回插入的实体在数据库的id
     */
    public static <T, K> void insertInTx(AbstractDao<T, K> dao, T... entities) {
        dao.insertInTx(entities);
    }

    /**
     * 增, 批量插入
     * @param dao 具体实体对应的dao
     * @param entities 具体实体
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 返回插入的实体在数据库的id
     */
    public static <T, K> void insertInTx(AbstractDao<T, K> dao, Iterable<T> entities) {
        dao.insertInTx(entities);
    }

    /**
     * 增, 存在则替换，不存在则插入, ★★★这条数据的id会被修改★★★
     * @param dao 具体实体对应的dao
     * @param entity 具体实体
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 返回插入的实体在数据库的id
     */
    public static <T, K> long insertOrReplace(AbstractDao<T, K> dao, T entity) {
        return dao.insertOrReplace(entity);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 删
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 删, 会调用 {@link #deleteByKey(AbstractDao, Object)} 方法, 如果entity的主键key=null, 会报错:
     * {@link org.greenrobot.greendao.DaoException DaoException("Entity has no key")}
     *
     * @param dao 具体实体对应的dao
     * @param entity 具体实体
     * @param <T> 实体
     * @param <K> 实体的id类型
     */
    public static <T, K> void delete(AbstractDao<T, K> dao, @NonNull T entity) {
        dao.delete(entity);
    }

    /**
     * 删
     * @param dao 具体实体对应的dao
     * @param cond 查询条件, 示例: ItemEntityDao.Properties.Id.eq(id)
     * @param condMore 更多查询条件
     * @param <T> 实体
     * @param <K> 实体的id类型
     */
    public static <T, K> void delete(AbstractDao<T, K> dao, WhereCondition cond, WhereCondition... condMore) {
        dao.queryBuilder().where(cond, condMore).buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删, 根据主键id
     * @param dao 具体实体对应的dao
     * @param id 主键id
     * @param <T> 实体
     * @param <K> 实体的id类型
     */
    public static <T, K> void deleteByKey(AbstractDao<T, K> dao, K id) {
        dao.deleteByKey(id);
    }

    /**
     * 批量删除数据
     * @param dao 具体实体对应的dao
     * @param entities 具体实体...
     * @param <T> 实体
     * @param <K> 实体的id类型
     */
    public static <T, K> void deleteInTx(AbstractDao<T, K> dao, T... entities) {
        dao.deleteInTx(entities);
    }

    /**
     * 通过多个主键批量删除数据
     * @param dao 具体实体对应的dao
     * @param ids 具体实体的ids...
     * @param <T> 实体
     * @param <K> 实体的id类型
     */
    public static <T, K> void deleteByKeyInTx(AbstractDao<T, K> dao, K... ids) {
        dao.deleteByKeyInTx(ids);
    }

    /**
     * 通过多个主键批量删除数据
     * @param dao 具体实体对应的dao
     */
    public static void deleteAll(AbstractDao dao) {
        dao.deleteAll();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 改(更新)
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 改(更新)
     * @param dao 具体实体对应的dao
     * @param entity 具体实体
     * @param <T> 实体
     * @param <K> 实体的id类型
     */
    public static <T, K> void update(AbstractDao<T, K> dao, T entity) {
        dao.update(entity);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 查询
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 查询一条数据, 如果有多条数据, 会报错:
     *      org.greenrobot.greendao.DaoException: Expected unique result, but count was 66
     * @param dao 具体实体对应的dao
     * @param cond 查询条件, 示例: ItemEntityDao.Properties.Id.eq(id)
     * @param condMore 更多查询条件
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 查询到的实体
     */
    public static <T, K> T queryUnique(AbstractDao<T, K> dao, WhereCondition cond,
                                       WhereCondition... condMore) {
        return dao.queryBuilder().where(cond, condMore).build().unique();
    }

    /**
     * 查询总数
     * @param dao 具体实体对应的dao
     * @param cond 查询条件, 示例: ItemEntityDao.Properties.Id.eq(id)
     * @param condMore 更多查询条件
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 查询到的总数
     */
    public static <T, K> long queryCount(AbstractDao<T, K> dao, WhereCondition cond,
                                         WhereCondition... condMore) {
        return dao.queryBuilder().where(cond, condMore).count();
    }

    /**
     * 查询列表
     * @param dao 具体实体对应的dao
     * @param cond 查询条件, 示例: ItemEntityDao.Properties.Id.eq(id)
     * @param condMore 更多查询条件
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 查询到的实体列表
     */
    public static <T, K> List<T> queryList(AbstractDao<T, K> dao, WhereCondition cond,
                                           WhereCondition... condMore) {
        return dao.queryBuilder().where(cond, condMore)/*.build()*/.list();//不要build()也一样
    }

    /**
     * 查询列表
     * @param dao 具体实体对应的dao
     * @param cond 查询条件, 示例: ItemEntityDao.Properties.Id.eq(id)
     * @param condMore 更多查询条件
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 查询到的实体列表
     */
    public static <T, K> QueryBuilder<T> queryBuilder(AbstractDao<T, K> dao, WhereCondition cond,
                                                      WhereCondition... condMore) {
        return dao.queryBuilder().where(cond, condMore);
    }

    /**
     * 查找全部
     * @param dao 具体实体对应的dao
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 查询到实体列表
     */
    public static <T, K> List<T> queryAll(AbstractDao<T, K> dao) {
        return dao.loadAll();
//        return dao.queryBuilder().list();//一样的
    }
}
