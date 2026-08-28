package com.actor.database.greendao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Description: GreenDao的增删改查帮助类
 * <pre>
 * 1.在Project的gradle文件中添加插件(root build.gradle)
 *   buildscript {
 *       repositories {
 *           mavenCentral() // add repository, greenDAO需要
 *           maven { url "https://jitpack.io" } //GreenDaoUpgradeHelper数据库升级需要
 *       }
 *       dependencies {
 *           classpath 'org.greenrobot:greendao-gradle-plugin:3.3.0' // add plugin
 *       }
 *   }
 *
 * 2.在项目Module的gradle中添加插件(app/build.gradle)
 *   apply plugin: 'org.greenrobot.greendao' // apply plugin
 *   android {
 *       greendao {
 *         schemaVersion 1                 //指定数据库schema版本号，迁移等操作会用到
 *         /**
 *          * 表实体对应的dao生成文件的包名(例: <a href = "https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/greendao/gen/ItemEntityDao.java" target="_blank">ItemEntityDao.java</a> 这个生成文件的包名)，包名默认是entity所在的包。
 *          * (请务必使用此包名, 否则这个Utils会找不到 {@link DaoMaster} 和 {@link DaoSession})。
 *          * (如果你实在不用这个包名, 那么可以将本Utils拷贝到自己项目, 使用拷贝的Utils也可以!)。
 *          * /
 *         daoPackage 'com.greendao.gen'
 *         targetGenDir 'src/main/java'    //生成数据库文件的目录(请确保是这个目录, 否则本工具类将无法使用)
 *       }
 *   }
 *   dependencies {
 *       //https://github.com/greenrobot/greenDAO
 *       implementation 'org.greenrobot:greendao:3.3.0' // add library
 *
 *       //https://github.com/yuweiguocn/GreenDaoUpgradeHelper greenDAO数据库升级
 *       implementation 'io.github.yuweiguocn:GreenDaoUpgradeHelper:v2.2.1'
 *
 *       //★如果你的数据库是加密的, 那么需要再添加2个依赖:
 *       //https://github.com/sqlcipher/android-database-sqlcipher 数据库加密
 *       implementation "net.zetetic:android-database-sqlcipher:4.4.3@aar"
 *       implementation "androidx.sqlite:sqlite:2.1.0"
 *    }
 *
 * 3.写一个你想要存储到 GreenDao 的实体类, 示例: <a href = "https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/database/ItemEntity.java" target="_blank">ItemEntity.java</a>
 *
 * 4.Build -> Make Project, 生成 {@link DaoMaster}, {@link DaoSession}, 以及实体对应的Dao(例: <a href = "https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/greendao/gen/ItemEntityDao.java" target="_blank">ItemEntityDao.java</a>)
 *
 * 5.在Application中初始化
 *   {@link GreenDaoUtils}.{@link #init(Context, boolean, String, String, boolean, DaoMaster.OpenHelper, Class[])};
 *
 * 6.获取某个Dao示例:
 *   XxxDao dao = {@link GreenDaoUtils}.{@link #getDaoSession()}.{@link DaoSession getXxxDao()};//XxxDao是生成的文件
 *
 * 7.数据库操作使用示例: <a href = "https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/DatabaseActivity.java" target="_blank">DatabaseActivity.java</a>
 *
 * 8.数据库 删/改/查 的条件查询, 从 {@link WhereCondition} 的一些方法开始:
 *   {@link Property} userName = XxxDao.Properties.UserName;
 *   {@link WhereCondition} whereCondition = userName.eq("张三");    // 删/改/查 的人的姓名 = "张三"
 * </pre>
 *
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th align="center">{@link Property} 的方法method</th>
 *         <th align="center">返回值return</th>
 *         <th align="center">说明doc</th>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#eq(Object)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>相等</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#notEq(Object)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>不相等</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#like(String) Property#like(String value)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>
 *             模糊查询, value要夹在2个%中间, 例: Properties.FirstName.like("%李%"), 查询FirstName包含李的人 <br />
 *             {@link null 注意:} value查询前应该先判空, value不能=null, 否则崩溃
 *         </td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link Property#between(Object, Object)}</td>
 *         <td nowrap="nowrap">{@link WhereCondition}</td>
 *         <td>在...之间</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#in(Object...)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>在给出的value的范围内的符合项</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#in(Collection)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>在给出的value的范围内的符合项</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#notIn(Object...)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>不在给出的value的范围内的符合项</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#notIn(Collection)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>不在给出的value的范围内的符合项</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#gt(Object)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>大于</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#lt(Object)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>小于</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#ge(Object)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>大于等于</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#le(Object)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>小于等于</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#isNull()}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>为空</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#isNotNull()}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>不为空</td>
 *     </tr>
 * </table>
 *
 *
 * <br />
 * <br />
 *   //2. QueryBuilder 的方法:
 *   <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *       <tr>
 *           <th align="center">方法method</th>
 *           <th align="center">返回值return</th>
 *           <th align="center">说明doc</th>
 *       </tr>
 *       <tr>
 *           <td>{@link AbstractDao#queryBuilder()}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>获取 QueryBuilder</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#or(WhereCondition, WhereCondition, WhereCondition...)}</td>
 *           <td nowrap="nowrap"><b>{@link WhereCondition}</b></td>
 *           <td>
 *               或者, {@link null 注意:}
 *               <ol>
 *                   <li>返回的 {@link WhereCondition} 应该放入 <code>where()</code> 里面, 否则查询的时候这些or语句不会执行, 例: {@link QueryBuilder#where(WhereCondition, WhereCondition...) queryBuilder.where(queryBuilder.or(cond, cond, condMore))}</li>
 *                   <li>或者应该直接调用 {@link QueryBuilder#whereOr(WhereCondition, WhereCondition, WhereCondition...)}</li>
 *               </ol>
 *           </td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#whereOr(WhereCondition, WhereCondition, WhereCondition...)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>或者</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#and(WhereCondition, WhereCondition, WhereCondition...)}</td>
 *           <td><b>{@link WhereCondition}</b></td>
 *           <td>
 *               并且, {@link null 注意:} <br />
 *               返回的 {@link WhereCondition} 应该放入 <code>where()</code> 里面, 否则查询的时候这些and语句不会执行, 例: {@link QueryBuilder#where(WhereCondition, WhereCondition...) queryBuilder.where(queryBuilder.and(cond, cond, condMore))}
 *           </td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#orderAsc(Property...)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>升序(正序, 小->大)</td>
 *       </tr>
 *       <tr>
 *           <td nowrap="nowrap">{@link QueryBuilder#orderDesc(Property...)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>降序(倒序, 大->小)</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#limit(int)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>限制查询返回的条数</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#offset(int)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>设置数据返回偏向后移值, 一般结合limit使用, 例: limit(3).offset(2): 结果[1,2,3] => [3,4,5]</td>
 *       </tr>
 *       <tr>
 *           <td>QueryBuilder.xxx</td>
 *           <td>{@link null ?}</td>
 *           <td>其它不常用方法参考:<br />{@link #queryBuilder(AbstractDao)}</td>
 *       </tr>
 *   </table>
 *
 *
 * <br />
 * <br />
 * 9.更多信息参考: <a href="https://www.jianshu.com/p/53083f782ea2" target="_blank">简书</a>
 * <br />
 * Author     : ldf
 * Date       : 2019/10/28 on 22:53
 *
 * @version 1.0
 * @version 1.0.1 增加一些增删改查的方法
 */
public class GreenDaoUtils {

    //String, DaoMaster, DaoSession
    protected static final List<Object> daoMasterSessionList = new ArrayList<>(3);

    /**
     * 初始化数据库, 多数据库支持
     * @param context application
     * @param isDebug 如果是debug模式, 数据库操作会打印日志
     * @param dbName 数据库名称(没有就创建在/data/data/package/databases,有就增删改查), 例: my_database.db3 <br />
     *               或读取已有数据库例: my_database.db, my_database.db3...<br />
     *               ★★★注意: 读取已有数据库时, 要保证在: context.getDatabasePath() 这个目录下已有数据库★★★
     * @param dbPassword 数据库密码, 如果数据库没加密就传null
     *                   <ul>
     *                       <li>1.如果数据库加密了, 需要添加依赖, 见顶部说明!!!</li>
     *                       <li>2.这个依赖可运行在构架: armeabi-v7a, x86, x86_64, and arm64_v8a, 所以要在gradle中的abiFilters中添加相应构架, 否则运行会崩溃.</li>
     *                       <li>3.如果数据库加密了, PC端需要<a href="https://github.com/sqlitebrowser/sqlitebrowser">sqlitebrowser</a>打开加密的数据库.</li>
     *                   </ul>
     * @param needWrite 数据库是否需要写入(增删改)
     * @param openHelper 这儿主要用于升级数据库的时候, 可传null有默认处理
     * @param daoClasses 数据库表对应的实体的dao, 用于升级, 可传入多个 XxxDao.class, 示例:
     *                   <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/greendao/gen/ItemEntityDao.java" target="_blank">ItemEntityDao.class</a>(由'Build -> Make Project'生成)<br />
     *                   ★★★注意: 如果只是从 my_database.db, my_database.db3... 等数据库文件读取数据,
     *                             即数据库不用升级, 可不用传这个参数★★★
     */
    @SafeVarargs
    public static void init(@NonNull Context context, boolean isDebug, @NonNull String dbName,
                            @Nullable String dbPassword, boolean needWrite,
                            @Nullable DaoMaster.OpenHelper openHelper,
                            @Nullable Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (TextUtils.isEmpty(dbName)) return;
        if (closeDatabase(dbName)) {
            Log.e(GreenDaoUtils.class.getName(), dbName + ": 数据库重复初始化!!!");
        }
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        if (openHelper == null) openHelper = new UpgradeAbleOpenHelper(context, dbName, null, daoClasses);
        Database database;
        if (TextUtils.isEmpty(dbPassword)) {
            database = needWrite ? openHelper.getWritableDb() : openHelper.getReadableDb();
        } else {
            database = needWrite ? openHelper.getEncryptedWritableDb(dbPassword) : openHelper.getEncryptedReadableDb(dbPassword);
        }
        //该数据库连接属于 DaoMaster，而多个 DaoSession 指向的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(database);
        daoMasterSessionList.add(dbName);
        daoMasterSessionList.add(daoMaster);
        daoMasterSessionList.add((DaoSession) null);

        //打印log
        QueryBuilder.LOG_SQL = isDebug;
        QueryBuilder.LOG_VALUES = isDebug;
    }



    ///////////////////////////////////////////////////////////////////////////
    // 静态方法区
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 获取DaoSession会话
     * @return 默认返回List中第1个
     */
    @NonNull
    public static DaoSession getDaoSession() {
        if (daoMasterSessionList.size() >= 3) {
            return Objects.requireNonNull(getDaoSession((String) daoMasterSessionList.get(0)));
        }
        throw new IllegalStateException("你还没有初始化任何数据库!!!");
    }

    /**
     * 获取DaoSession会话 <br />
     * 初始化DaoSession方式:
     * <ol>
     *     <li>{@link IdentityScopeType#None}: 这样初始化时 {@link DaoSession#xxxDaoConfig}{@link org.greenrobot.greendao.internal.DaoConfig#identityScope .identityScope} = null, 完全没有对象缓存 Map</li>
     *     <li>
     *         {@link IdentityScopeType#Session}: 这样初始化时 {@link DaoSession#xxxDaoConfig}{@link org.greenrobot.greendao.internal.DaoConfig#identityScope .identityScope} =
     *         {@link org.greenrobot.greendao.identityscope.IdentityScopeLong IdentityScopeLong} / {@link org.greenrobot.greendao.identityscope.IdentityScopeObject IdentityScopeObject},
     *         这2种对象里面都有Map, 查询出来的实体对象会放进这个 Map 做一级缓存。会造成以下问题:
     *         <ol>
     *             <li>页面退出 Activity，如果这个 DaoSession 还被引用，这个 Map 会持有大量实体对象，造成内存泄漏。退出页面时需要手动调用 {@link DaoSession#clear()} 清空这个缓存 Map。</li>
     *             <li>虽然查询更快(优先从Map取缓存), 但如果查询结果对象属性改变，未持久化它，再次做query, 不会从数据库查询，只是缓存中的结果。会导致与数据库表数据不一致。</li>
     *             <li>if实体中有1个不映射到数据库的变量<code>boolean a = false;</code> 当把这个值改成<code>true</code>, 再次查询的时候会从Map取缓存, 这个值还是<code>true</code>, 未被重新初始化。</li>
     *         </ol>
     *     </li>
     * </ol>
     * @param dbName 数据库名称
     * @return if 数据库不存在, return null
     */
    @Nullable
    public static DaoSession getDaoSession(@NonNull String dbName) {
        if (TextUtils.isEmpty(dbName)) return null;
        for (int i = 0; i < daoMasterSessionList.size(); i += 3) {
            if (dbName.equals(daoMasterSessionList.get(i))) {
                Object obj = daoMasterSessionList.get(i + 2);
                if (obj instanceof DaoSession) return (DaoSession) obj;
                daoMasterSessionList.set(i + 2, obj = ((DaoMaster) daoMasterSessionList.get(i + 1)).newSession(IdentityScopeType.None));
                return (DaoSession) obj;
            }
        }
        return null;
    }

    /**
     * 关闭数据库 <br />
     * {@link null 注意:} 确保不再使用数据库了再关闭, 否则不用关闭
     * @param dbName 数据库名称
     * @return 是否关闭 & 从list删除成功
     */
    public static boolean closeDatabase(@NonNull String dbName) {
        if (TextUtils.isEmpty(dbName)) return false;
        for (int i = 0; i < daoMasterSessionList.size(); i += 3) {
            if (dbName.equals(daoMasterSessionList.get(i))) {
                Object obj = daoMasterSessionList.remove(i + 2);
                if (obj instanceof DaoSession) ((DaoSession) obj).clear();
                DaoMaster daoMaster = (DaoMaster) daoMasterSessionList.remove(i + 1);
                daoMaster.getDatabase().close();
//                openHelper1.close();   //这样也可以, 就是 DaoMaster 中同一个db对象
                daoMasterSessionList.remove(i);
                return true;
            }
        }
        return false;
    }

    public static List<Object> getDaoMasterSessionList() {
        return daoMasterSessionList;
    }



    ///////////////////////////////////////////////////////////////////////////
    // 增
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 增  <b>(会回写数据的id)</b>
     * @param dao 具体实体对应的dao, 例: GreenDaoUtils.getDaoSession().getXxxDao();
     * @param entity 具体实体
     * @param <T> 实体, 不能传null
     * @param <K> 实体的id类型
     * @return 返回插入的实体在数据库行数id: rowId
     */
    public static <T, K> long insert(@NonNull AbstractDao<T, K> dao, @NonNull T entity) {
        return dao.insert(entity);
    }

    /**
     * 增, 批量插入 <b>(默认会回写数据的id)</b>
     * @param dao 具体实体对应的dao
     * @param entities 具体实体, 不能传null
     */
    public static <T, K> void insertInTx(@NonNull AbstractDao<T, K> dao, @NonNull T... entities) {
        dao.insertInTx(entities);
    }

    /**
     * 增, 批量插入  <b>(默认会回写数据的id)</b>
     * @param dao 具体实体对应的dao
     * @param entities 具体实体
     */
    public static <T, K> void insertInTx(@NonNull AbstractDao<T, K> dao, @NonNull Iterable<T> entities) {
        //                         参2: 插入数据库后, 是否把数据库生成的自增主键，回填到你内存里的 Java 实体对象上。if你不修改生成的dao的这个方法返回值的话, 默认=true
        dao.insertInTx(entities/*, dao.isEntityUpdateable()*/);
    }

    /**
     * 增, 存在则替换，不存在则插入  <b>(会回写数据的id)</b>
     * @param dao 具体实体对应的dao
     * @param entity 具体实体
     * @return 返回插入/替换的实体在数据库行数id: rowId
     */
    public static <T, K> long insertOrReplace(@NonNull AbstractDao<T, K> dao, @NonNull T entity) {
        return dao.insertOrReplace(entity);
    }

    /**
     * 增, 存在则替换，不存在则插入  <b>(默认会回写数据的id)</b>
     * @param dao 具体实体对应的dao
     * @param entities 具体实体
     */
    public static <T, K> void insertOrReplaceInTx(@NonNull AbstractDao<T, K> dao, @NonNull T... entities) {
        //                                  参2: 插入数据库后, 是否把数据库生成的自增主键，回填到你内存里的 Java 实体对象上。if你不修改生成的dao的这个方法返回值的话, 默认=true
        dao.insertOrReplaceInTx(entities/*, dao.isEntityUpdateable()*/);
    }

    /**
     * 增, 存在则替换，不存在则插入  <b>(默认会回写数据的id)</b>
     * @param dao 具体实体对应的dao
     * @param entities 具体实体
     */
    public static <T, K> void insertOrReplaceInTx(@NonNull AbstractDao<T, K> dao, @NonNull Iterable<T> entities) {
        //                                  参2: 插入数据库后, 是否把数据库生成的自增主键，回填到你内存里的 Java 实体对象上。if你不修改生成的dao的这个方法返回值的话, 默认=true
        dao.insertOrReplaceInTx(entities/*, dao.isEntityUpdateable()*/);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 删
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 删, 会调用 {@link #deleteByKey(AbstractDao, Object)} 方法, 如果entity的主键key=null, 会报错:
     * {@link org.greenrobot.greendao.DaoException DaoException("Entity has no key")} <br />
     * 如果根据主键未找到对应的实体, 就不做操作(不报错)
     *
     * @param dao 具体实体对应的dao
     * @param entity 具体实体
     */
    public static <T, K> void delete(@NonNull AbstractDao<T, K> dao, @NonNull T entity) {
        dao.delete(entity);
    }

    /**
     * 删
     * @param dao 具体实体对应的dao
     * @param cond 查询条件, 示例: XxxDao.Properties.Id.eq(id)
     * @param condMore 更多查询条件, 没有就不要传, 不能传null
     */
    public static <T, K> void delete(@NonNull AbstractDao<T, K> dao, @NonNull WhereCondition cond, @NonNull WhereCondition... condMore) {
        delete(queryBuilder(dao).where(cond, condMore));
    }

    /**
     * 删
     * @param queryBuilder 示例: {@link #queryBuilder(AbstractDao)}
     */
    public static <T> void delete(@NonNull QueryBuilder<T> queryBuilder) {
        queryBuilder.buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删, 根据主键id, <b>if id=null会报错. if没找到这个主键对应的实体, 就不删除(不会报错)</b>
     * @param dao 具体实体对应的dao
     * @param key 主键id
     */
    public static <T, K> void deleteByKey(@NonNull AbstractDao<T, K> dao, @NonNull K key) {
        dao.deleteByKey(key);
    }

    /**
     * 批量删除数据
     * @param dao 具体实体对应的dao
     * @param entities 具体实体
     */
    public static <T, K> void deleteInTx(@NonNull AbstractDao<T, K> dao, @NonNull T... entities) {
        dao.deleteInTx(entities);
    }
    public static <T, K> void deleteInTx(@NonNull AbstractDao<T, K> dao, @NonNull Iterable<T> entities) {
        dao.deleteInTx(entities);
    }

    /**
     * 通过多个主键批量删除数据
     * @param dao 具体实体对应的dao
     * @param keys 具体实体的ids...
     */
    public static <T, K> void deleteByKeyInTx(@NonNull AbstractDao<T, K> dao, @NonNull K... keys) {
        dao.deleteByKeyInTx(keys);
    }
    public static <T, K> void deleteByKeyInTx(@NonNull AbstractDao<T, K> dao, @NonNull Iterable<K> keys) {
        dao.deleteByKeyInTx(keys);
    }

    /**
     * 全部删除
     * @param dao 具体实体对应的dao
     */
    public static <T, K> void deleteAll(@NonNull AbstractDao<T, K> dao) {
        dao.deleteAll();
    }



    ///////////////////////////////////////////////////////////////////////////
    // 改(更新)
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 改(更新)
     * @param dao 具体实体对应的dao
     * @param entity 具体实体
     */
    public static <T, K> void update(@NonNull AbstractDao<T, K> dao, @NonNull T entity) {
        dao.update(entity);
    }

    /**
     * 批量更改(更新)
     * @param dao 具体实体对应的dao
     * @param entities 具体实体
     */
    public static <T, K> void updateInTx(@NonNull AbstractDao<T, K> dao, @NonNull T... entities) {
        dao.updateInTx(entities);
    }
    public static <T, K> void updateInTx(@NonNull AbstractDao<T, K> dao, @NonNull Iterable<T> entities) {
        dao.updateInTx(entities);
    }

    /**
     * 改 or 插入, 表里存在就更新, 不存在就插入
     * @param dao 具体实体对应的dao
     * @param entity 具体实体
     */
    public static <T, K> void updateOrInsert(@NonNull AbstractDao<T, K> dao, @NonNull T entity) {
        dao.save(entity);
    }

    /**
     * 改 or 插入, 表里存在就更新, 不存在就插入
     * @param dao 具体实体对应的dao
     * @param entities 具体实体
     */
    public static <T, K> void updateOrInsertInTx(@NonNull AbstractDao<T, K> dao, @NonNull T... entities) {
        dao.saveInTx(entities);
    }
    public static <T, K> void updateOrInsertInTx(@NonNull AbstractDao<T, K> dao, @NonNull Iterable<T> entities) {
        dao.saveInTx(entities);
    }

    /**
     * 从数据库重新加载值来重置实体的所有本地更改的属性。
     * @param dao 具体实体对应的dao
     * @param entity 具体实体
     */
    public static <T, K> void refresh(@NonNull AbstractDao<T, K> dao, @NonNull T entity) {
        dao.refresh(entity);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 查询
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 查询一条数据, 如果有多条数据, 会报错:
     *      org.greenrobot.greendao.DaoException: Expected unique result, but count was 66
     * @param dao 具体实体对应的dao
     * @param cond 查询条件, 示例: XxxDao.Properties.Id.eq(id)
     * @param condMore 更多查询条件, 没有就不要传, 不能传null
     * @return 查询到的实体
     */
    public static <T, K> T queryUnique(@NonNull AbstractDao<T, K> dao, @NonNull WhereCondition cond, @NonNull WhereCondition... condMore) {
        return queryBuilder(dao).where(cond, condMore).build().unique();
    }

    /**
     * 根据主键查询
     * @param dao 具体实体对应的dao
     * @param key 主键
     * @return 查询到的实体, if没找到就返回 null
     */
    @Nullable
    public static <T, K> T queryByKey(@NonNull AbstractDao<T, K> dao, @NonNull K key) {
        return dao.load(key);
    }

    /**
     * 根据表行号查询
     * @param dao 具体实体对应的dao
     * @param rowId 表行号
     * @return 查询到的实体, if没找到就返回 null
     */
    @Nullable
    public static <T, K> T queryByRowId(@NonNull AbstractDao<T, K> dao, @IntRange(from = 0) long rowId) {
        return dao.loadByRowId(rowId);
    }

    /**
     * 查询总数
     * @param dao 具体实体对应的dao
     * @param cond 查询条件, 示例: XxxDao.Properties.Id.eq(id)
     * @param condMore 更多查询条件, 没有就不要传, 不能传null
     * @return 查询到的总数
     */
    public static <T, K> long queryCount(@NonNull AbstractDao<T, K> dao, @NonNull WhereCondition cond, @NonNull WhereCondition... condMore) {
        return queryBuilder(dao).where(cond, condMore).count();
    }

    /**
     * 查询总数, 不区分查询条件
     * @param dao 具体实体对应的dao
     * @return 查询到的总数
     */
    public static <T, K> long queryCount(@NonNull AbstractDao<T, K> dao) {
        return dao.count();
//        return queryBuilder(dao).count(); //if有查询条件, 才使用这个
    }

    /**
     * 分页查询
     * @param dao 具体实体对应的dao
     * @param page 第几页, 从1开始
     * @param size 每页多少个数据
     * @param conds 查询条件, 示例: XxxDao.Properties.Id.eq(id), 没有就不要传, 不能传null
     * @return
     */
    public static <T, K> List<T> queryPage(@NonNull AbstractDao<T, K> dao, @IntRange(from = 1) int page, @IntRange(from = 1) int size, @NonNull WhereCondition... conds) {
        if (conds == null || conds.length == 0) return queryBuilder(dao).offset((page - 1) * size).limit(size).list();
        return queryBuilder(dao)
                .where(conds[0], Arrays.copyOfRange(conds, 1, conds.length))
                .offset((page - 1) * size)
                .limit(size)
                .list();
    }

    /**
     * 查询列表
     * @param dao 具体实体对应的dao
     * @param cond 查询条件, 示例: XxxDao.Properties.Id.eq(id)
     * @param condMore 更多查询条件, 没有就不要传, 不能传null
     * @return 查询到的实体列表
     */
    public static <T, K> List<T> queryList(@NonNull AbstractDao<T, K> dao, @NonNull WhereCondition cond, @NonNull WhereCondition... condMore) {
        return queryBuilder(dao).where(cond, condMore)/*.build()*/.list();//不要build()也一样
    }

    /**
     * 查询最大数值, 如果多行拥有相同最大数值(e.g VersionCode)，会抛异常 <br />
     * e.g. WHERE VersionCode = (SELECT MAX(VersionCode) FROM table_name)
     * @param dao 具体实体对应的dao
     * @param properties 查询条件, 例: XxxDao.Properties.VersionCode(查询最新版本号)
     * @return 查询到的实体列表
     */
    public static <T, K> T queryMaxUnique(@NonNull AbstractDao<T, K> dao, @NonNull Property... properties) {
        return queryBuilder(dao).orderDesc(properties).limit(1).unique();
    }

    /**
     * 查询最尐数值, 如果多行拥有相同尐数值(e.g VersionCode)，会抛异常 <br />
     * e.g. WHERE VersionCode = (SELECT MIN(VersionCode) FROM table_name)
     * @param dao 具体实体对应的dao
     * @param properties 查询条件, 例: XxxDao.Properties.VersionCode(查询最新版本号)
     * @return 查询到的实体列表
     */
    public static <T, K> T queryMinUnique(@NonNull AbstractDao<T, K> dao, @NonNull Property... properties) {
        return queryBuilder(dao).orderAsc(properties).limit(1).unique();
    }

    /**
     * 自定义sql语句/参数, 查询
     * @param dao 具体实体对应的dao
     * @param where sql语句, 前面已经加了"SELECT * FROM tb_name", 所以这儿从'WHERE'开始写. <br />
     *              例: WHERE VERSION_CODE = (SELECT MAX(VERSION_CODE) FROM TABLENAME) <br />
     *              或: WHERE _id = ?
     * @param selectionArg 查询参数, 没有就不传
     * @return 查询到的 列表
     */
    public static <T, K> List<T> queryRaw(@NonNull AbstractDao<T, K> dao, @NonNull String where, @NonNull String... selectionArg) {
        return dao.queryRaw(where, selectionArg);
    }

    /**
     * 自定义sql语句/参数, 查询
     * @param dao 具体实体对应的dao
     * @param where sql语句, 前面已经加了"SELECT * FROM tb_name", 所以这儿从'WHERE'开始写. <br />
     *              例: WHERE VERSION_CODE = (SELECT MAX(VERSION_CODE) FROM TABLENAME) <br />
     *              或: WHERE _id = ?
     * @param selectionArg 查询参数
     * @return 查询到的 实体{@link Query#unique()} / 列表{@link Query#list()}
     */
    public static <T, K> Query<T> queryRawCreate(@NonNull AbstractDao<T, K> dao, @NonNull String where, @NonNull Object... selectionArg) {
        return dao.queryRawCreate(where, selectionArg);
    }

    /**
     * 获取QueryBuilder, 自定义自由条件查询
     * @param dao 具体实体对应的dao
     * @return QueryBuilder
     */
    public static <T, K> QueryBuilder<T> queryBuilder(@NonNull AbstractDao<T, K> dao) {
        return  dao.queryBuilder()
//                .where(cond, condMore)    //where 查询
//                .where(queryBuilder.or(cond, cond, condMore)) //or 的多个拼接, 注意 or 要放在 where 里面, 否则 or 条件并不会执行
//                .whereOr(cond, cond, condMore)                //就是 or, 但是不用自己放进 where 里面
//                .where(queryBuilder.and(cond, cond, condMore))//and 条件, 同理要放进 where 里面!
//                .orderAsc(properties)                         //顺序
//                .orderDesc(properties)                        //逆序
//                .offset(page)                                 //分页查询 ≧ 0
//                .limit(size)                                  //每页数量 > 0
//                .distinct()                                   //`SELECT DISTINCT T.*`，去重。整行完全相同才去重；例如在执行联接时。
//                .unique()                                     //返回单个对象, 0 条→null；≥2 条抛 DaoException
//                .uniqueOrThrow()                              //0 条 / ≥2 条，全部抛异常，不会返回 null。
//                .build()                                      //返回Query<T>
//                .buildDelete()                                //返回 DeleteQuery<T>, 删除
//                .list()                                       //返回实体列表

                //以下2个方法一般不要调用!
//                .or(cond, cond, condMore)                     //return WhereCondition, 返回的结果必须放进 where 里面!!
//                .and(cond, cond, condMore)                    //return WhereCondition, 返回的结果必须放进 where 里面!!

                //不常用方法
//                .buildCount()                                 //返回 CountQuery, 即: SELECT COUNT(*) FROM xxx WHERE ..., 只统计行数，不返回实体。
//                .buildCursor()                                //返回 CursorQuery, 不直接查实体对象，直接拿 Android 原生 Cursor。
//                .join(Class<J> destinationEntityClass, Property destinationProperty)  //返回 Join<T, J>, 多表查询
//                .join(Property sourceProperty, Class<J> destinationEntityClass)       //返回 Join<T, J>, 多表查询
//                .join(Property sourceProperty, Class<J> destinationEntityClass, Property destinationProperty)
//                .join(Join<?, T> sourceJoin, Property sourceProperty, Class<J> destinationEntityClass, Property destinationProperty)
//                .listIterator()                   //返回 CloseableListIterator<T>，迭代器遍历结果集；底层数据全部加载到内存，和 list () 一样全部读出来。
//                .listLazy()                       //返回 LazyList<T>, 懒加载（缓存），查询时**一次性把全部 Cursor 数据读到内存**，只是包装成懒接口；关闭数据库也还能使用；内部持有全部实体。
//                .listLazyUncached()               //返回 LazyList<T>, 真正懒加载，不缓存。游标不关闭，访问才逐行生成实体。⚠️重要：必须调用`.close()`关闭 LazyList，否则 Cursor 泄漏！不能跨线程，不能关闭数据库后访问。
//                .orderCustom(Property property, String customOrderForProperty)    //对某一列做自定义排序，传入排序片段。
//                .orderRaw(String rawOrder)        //例: "VERSION_CODE DESC, _id ASC"。 原生 SQL 排序片段，直接拼到 `ORDER BY` 后面。⚠️直接拼接 SQL 字符串，不要传入用户输入，防注入；可以写函数、多字段排序。
//                .preferLocalizedStringOrder()     //设置字符串排序的 Collate 排序规则 = "COLLATE LOCALIZED"(本地化排序)
//                .rx()                             //返回 RxQuery<T>
//                .rxPlain()                        //返回 RxQuery<T>
//                .stringOrderCollation(String stringOrderCollation)  //设置字符串排序的 Collate 排序规则，比如 "COLLATE NOCASE"、"COLLATE RTRIM"。
                ;
    }

    /**
     * 查找全部
     * @param dao 具体实体对应的dao
     * @return 查询到实体列表, 如果没有查到, 返回一个空的List
     */
    @NonNull
    public static <T, K> List<T> queryAll(@NonNull AbstractDao<T, K> dao) {
        return dao.loadAll();
//        return dao.queryBuilder().list();//if有查询条件才用这个
    }



    /**
     * 自定义sql语句/参数, 查询
     * @param string sql语句, 前面已经加了"SELECT * FROM tb_name WHERE", 所以这儿写WHERE后面的sql, 例:
     *               <ul>
     *                   <li>(<code>"VERSION_CODE = (SELECT MAX(VERSION_CODE) FROM XxxDao.TABLENAME)"</code>)</li>
     *                   <li>(<code>XxxDao.Properties.Id.columnName + " = ?", 123</code>)</li>
     *                   <li>
     *                       (<code>"TRIM(" + XxxDao.Properties.Name.columnName + ") = ''"</code>)<br />
     *                       等价于: <a href=''><code>TRIM(NAME) = ''</code></a> //这儿的NAME是表字段
     *                   </li>
     *                   <li>
     *                       {@link null 错误示范:}<br />
     *                       (<code>"TRIM(?) = ''", XxxDao.Properties.Name.columnName</code>)<br />
     *                       等价于: <a href=''><code>TRIM('NAME') = ''</code></a><br />
     *                       //这儿的 <code>NAME</code> 是<b>字符串常量</b>, 不是表字段名, 验证: 比如将这儿的参2随意写个值都不会报错<br />
     *                       //相当于 <code>bool equals = "NAME".equals("")</code> 这个判断条件永远=false, 这判断条件是废了
     *                   </li>
     *               </ul>
     * @param values 查询参数(例上方的id=123), 没有就不填. {@link null 注意: 绝对不能用来填列名 / 表名 / SQL函数名, 只能填值（字面量）}
     * @return 查询到的 实体{@link Query#unique()} / 列表{@link Query#list()}
     */
    public static WhereCondition getStringCondition(@NonNull String string, @Nullable Object... values) {
        if (values == null || values.length == 0) return new WhereCondition.StringCondition(string);
        return new WhereCondition.StringCondition(string, values);
    }

    public static <T, K> Database getDatabase(@NonNull AbstractDao<T, K> dao) {
        return dao.getDatabase();
    }



    public static <T, K> org.greenrobot.greendao.rx.RxDao<T, K> rx(@NonNull AbstractDao<T, K> dao) {
        return dao.rx();
    }

    public static <T, K> org.greenrobot.greendao.rx.RxDao<T, K> rxPlain(@NonNull AbstractDao<T, K> dao) {
        return dao.rxPlain();
    }
}
