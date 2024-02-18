package com.actor.myandroidframework.utils.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.Collection;
import java.util.List;

/**
 * Description: GreenDao的增删改查帮助类
 * <pre> {@code
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
 *          * dao的包名，包名默认是entity所在的包。
 *          * (请务必使用此包名, 否则这个Utils会找不到DaoMaster和DaoSession)。
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
 *       //★如果你的数据库是加密的, 那么需要再添加一个依赖:
 *       //https://github.com/sqlcipher/android-database-sqlcipher 数据库加密
 *       implementation "net.zetetic:android-database-sqlcipher:4.4.3@aar"
 *       implementation "androidx.sqlite:sqlite:2.1.0"
 *    }
 *
 * 3.写一个你想要存储到 GreenDao 的实体类, 示例: } <a href = "https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/database/ItemEntity.java" target="_blank">ItemEntity.java</a>
 *
 * {@code
 * 4.Build -> Make Project, 生成 DaoMaster.java, DaoSession.java, ItemEntityDao.java
 *
 * 5.在Application中初始化
 *   GreenDaoUtils.init(this, isDebugMode, ItemEntityDao.class);//参数3可传入多个 Dao.class
 *
 * 6.获取某个Dao示例:
 *   ItemEntityDao dao = GreenDaoUtils.getDaoSession().getItemEntityDao();//ItemEntityDao是生成的文件
 *
 * 7.数据库操作使用示例: } <a href = "https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/DatabaseActivity.java" target="_blank">DatabaseActivity.java</a>
 *
 * 8.数据库 删/改/查 的条件查询, 从 {@link WhereCondition} 的一些方法开始:
 *   ItemEntityDao.Properties properties = ItemEntityDao.Properties;
 *   {@link Property} userName = properties.userName;
 *   {@link WhereCondition} = userName.eq("张三");    //当 删/改/查 的人的姓名是"张三"的时候
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
 *         <td>{@link Property#like(String)}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>模糊查询, string要用夹在%key%中间, 例: Properties.FirstName.like("%doris%"), 查询FristName包含doris的人</td>
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
 *         <td>{@link Property#isNull()}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>为空</td>
 *     </tr>
 *     <tr>
 *         <td>{@link Property#isNotNull()}</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>不为空</td>
 *     </tr>
 *     <tr>
 *         <td>Property.xxx</td>
 *         <td>{@link WhereCondition}</td>
 *         <td>其余方法...</td>
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
 *           <td nowrap="nowrap">{@link QueryBuilder}</td>
 *           <td>获取 QueryBuilder</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#or(WhereCondition, WhereCondition, WhereCondition...)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>或者</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#whereOr(WhereCondition, WhereCondition, WhereCondition...)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>或者</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#and(WhereCondition, WhereCondition, WhereCondition...)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>并且</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#orderAsc(Property...)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>升序(正序, 小->大)</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#orderDesc(Property...)}</td>
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
 *           <td>设置数据返回偏向后移值, 结合limit使用, 例: limit(3), offset(2): 结果[1,2,3] => [3,4,5]</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#join(Property, Class)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>多表查询</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#join(Class, Property)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>多表查询</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#join(Property, Class, Property)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>多表查询</td>
 *       </tr>
 *       <tr>
 *           <td>{@link QueryBuilder#join(Join, Property, Class, Property)}</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>多表查询</td>
 *       </tr>
 *       <tr>
 *           <td>QueryBuilder.xxx</td>
 *           <td>{@link QueryBuilder}</td>
 *           <td>其它方法...</td>
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

    protected static GreenDaoUtils        instance;
    protected        DaoMaster.OpenHelper openHelper;
    protected static Database             database;
    protected        DaoMaster            daoMaster;
    protected static DaoSession           daoSession;

    /**
     * @param context application
     * @param isDebug 如果是debug模式, 数据库操作会打印日志
     * @param dbName 数据库名称(没有就创建,有就增删改查), 例: my_database.db3
     * @param dbPassword 数据库密码, 如果没有就传null
     *                   <ol>
     *                       <li>如果数据库加密了, 需要添加依赖, 见顶部说明!!!</li>
     *                       <li>这个依赖可运行在构架: armeabi-v7a, x86, x86_64, and arm64_v8a, 所以要在gradle中的abiFilters中添加相应构架, 否则运行会崩溃.</li>
     *                       <li>如果数据库加密了, PC端需要<a href="https://github.com/sqlitebrowser/sqlitebrowser">sqlitebrowser</a>打开加密的数据库.</li>
     *                   </ol>
     *
     * @param daoClasses 数据库表对应的实体(ItemEntity.java)的dao, 示例: <br />
     *                   ItemEntityDao.class(由'Build -> Make Project'生成), ... <br />
     *                   ★★★注意: 如果只是从 my_database.db, my_database.db3... 等数据库文件读取数据,
     *                             即数据库不用升级, 可不用传这个参数★★★
     */
    @SafeVarargs
    public static void init(@NonNull Context context, boolean isDebug, @NonNull String dbName,
                            @Nullable String dbPassword, @Nullable Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (instance == null) instance = new GreenDaoUtils(context, isDebug, dbName, dbPassword, daoClasses);
    }

    /**
     * 设置greenDAO
     * @param context application
     * @param isDebug 如果是debug模式, 数据库操作会打印日志
     * @param dbName 数据库名称(没有就创建,有就增删改查), 例: my_database.db3
     *               或读取已有数据库例: my_database.db, my_database.db3...
     *               ★★★注意: 读取已有数据库时, 要保证这个已有数据库在这个目录下: context.getDatabasePath()★★★
     * @param dbPassword 数据库密码, 如果没有就传null
     * @param daoClasses 数据库表对应的实体(ItemEntity.java)的dao, 用于升级, 示例:
     *                   ItemEntityDao.class(由'Build -> Make Project'生成), ...
     *                   ★★★注意: 如果只是从 my_database.db, my_database.db3... 等数据库文件读取数据,
     *                             即数据库不用升级, 可不用传这个参数★★★
     */
    @SafeVarargs
    protected GreenDaoUtils(@NonNull Context context, boolean isDebug, @NonNull String dbName,
                            @Nullable String dbPassword, @Nullable Class<? extends AbstractDao<?, ?>>... daoClasses) {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        openHelper = new UpgradeAbleOpenHelper(context, dbName, null, daoClasses);

        if (dbPassword == null) {
            database = openHelper.getWritableDb();
        } else {
            //加密的数据库, 使用getEncryptedReadableDb()和getEncryptedWritableDb()获取加密的数据库
            database = openHelper.getEncryptedWritableDb(dbPassword);
        }
        //该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(database);//database
        daoSession = daoMaster.newSession();

        //debug查询, 设置这两个属性就可以看到log
        QueryBuilder.LOG_SQL = isDebug;
        QueryBuilder.LOG_VALUES = isDebug;
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
    public static Database getDatabase() {
        return database;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 增
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 增
     * @param dao 具体实体对应的dao, 例: GreenDaoUtils.getDaoSession().getItemEntityDao();
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
     */
    @SafeVarargs
    public static <T, K> void insertInTx(AbstractDao<T, K> dao, T... entities) {
        dao.insertInTx(entities);
    }

    /**
     * 增, 批量插入
     * @param dao 具体实体对应的dao
     * @param entities 具体实体
     * @param <T> 实体
     * @param <K> 实体的id类型
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
     * 如果根据主键未找到对应的实体, 就不做操作(不报错)
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
    public static <T, K> void delete(AbstractDao<T, K> dao, @NonNull WhereCondition cond, WhereCondition... condMore) {
        dao.queryBuilder().where(cond, condMore).buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删, 根据主键id, 如果没找到这个主键对应的实体, 就不删除(不会报错).
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
    @SafeVarargs
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
    @SafeVarargs
    public static <T, K> void deleteByKeyInTx(AbstractDao<T, K> dao, K... ids) {
        dao.deleteByKeyInTx(ids);
    }

    /**
     * 通过多个主键批量删除数据
     * @param dao 具体实体对应的dao
     */
    public static <T, K> void deleteAll(AbstractDao<T, K> dao) {
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
    public static <T, K> T queryUnique(AbstractDao<T, K> dao, @NonNull WhereCondition cond,
                                       WhereCondition... condMore) {
        return queryBuilder(dao, cond, condMore).build().unique();
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
    public static <T, K> long queryCount(AbstractDao<T, K> dao, @NonNull WhereCondition cond,
                                         WhereCondition... condMore) {
        return queryBuilder(dao, cond, condMore).count();
    }

    /**
     * 查询总数, 不区分查询条件
     * @param dao 具体实体对应的dao
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 查询到的总数
     */
    public static <T, K> long queryCount(AbstractDao<T, K> dao) {
        return dao.queryBuilder().count();
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
    public static <T, K> List<T> queryList(AbstractDao<T, K> dao, @NonNull WhereCondition cond,
                                           WhereCondition... condMore) {
        return queryBuilder(dao, cond, condMore)/*.build()*/.list();//不要build()也一样
    }

    /**
     * 自定义sql语句/参数, 查询
     * @param dao 具体实体对应的dao
     * @param where sql语句, 前面已经加了"SELECT * FROM tb_name", 所以这儿从'WHERE'开始写.
     *              例: WHERE versionCode = (SELECT MAX(versionCode) FROM tb_name)
     *              或: WHERE id = ?
     * @param selectionArg 查询参数
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 查询到的 实体{@link Query#unique()} / 列表{@link Query#list()}
     */
    public static <T, K> Query<T> queryRawCreate(AbstractDao<T, K> dao, String where, Object... selectionArg) {
        return dao.queryRawCreate(where, selectionArg);
    }

    /**
     * 自定义sql语句/参数, 查询
     * @param dao 具体实体对应的dao
     * @param where sql语句, 前面已经加了"SELECT * FROM tb_name", 所以这儿从'WHERE'开始写.
     *              例: WHERE versionCode = (SELECT MAX(versionCode) FROM tb_name)
     *              或: WHERE id = ?
     * @param selectionArg 查询参数
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 查询到的 列表
     */
    public static <T, K> List<T> queryRaw(AbstractDao<T, K> dao, String where, String... selectionArg) {
        return dao.queryRaw(where, selectionArg);
    }

    /**
     * 自定义sql语句/参数, 查询
     * @param dao 具体实体对应的dao
     * @param string sql语句, 前面已经加了"SELECT * FROM tb_name WHERE", 所以这儿写WHERE后面的sql.
     *               例: versionCode = (SELECT MAX(versionCode) FROM tb_name)
     *               或: id = ?
     * @param values 查询参数
     * @param <T>  实体
     * @param <K>  实体的id类型
     * @return 查询到的 实体{@link Query#unique()} / 列表{@link Query#list()}
     */
    public static <T, K> Query<T> queryStringCondition(AbstractDao<T, K> dao, String string, Object... values) {
        return queryBuilder(dao, new WhereCondition.StringCondition(string, values)).build();
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
    public static <T, K> QueryBuilder<T> queryBuilder(AbstractDao<T, K> dao, @NonNull WhereCondition cond,
                                                      WhereCondition... condMore) {
        return dao.queryBuilder().where(cond, condMore)
//                .orderAsc()
//                .orderDesc()
//                .limit()
                //...
                ;
    }

    /**
     * 查找全部
     * @param dao 具体实体对应的dao
     * @param <T> 实体
     * @param <K> 实体的id类型
     * @return 查询到实体列表, 如果没有查到, 返回一个空的List
     */
    @NonNull
    public static <T, K> List<T> queryAll(AbstractDao<T, K> dao) {
        return dao.loadAll();
//        return dao.queryBuilder().list();//一样的
    }
}
