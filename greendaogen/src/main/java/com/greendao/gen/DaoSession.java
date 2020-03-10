package com.greendao.gen;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

/**
 * 这个DaoSession的作用只起到能编译通过, 在使用中需要自己Build -> Make Project, 生成DaoSession, 否则空指针
 */
public class DaoSession extends AbstractDaoSession {

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);
    }
}
