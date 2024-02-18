package com.actor.myandroidframework.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.greendao.gen.DaoMaster;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;

/**
 * description: 能数据库简单更新升级的OpenHelper
 * 不能使用默认的: {@link DaoMaster.DevOpenHelper}
 *
 * @author : ldf
 * date       : 2024/2/18 on 14
 * @version 1.0
 */
public class UpgradeAbleOpenHelper extends DaoMaster.OpenHelper {

    @Nullable
    protected Class<? extends AbstractDao<?, ?>>[] daoClasses;

    @SafeVarargs
    protected UpgradeAbleOpenHelper(@NonNull Context context, @NonNull String name, @Nullable Class<? extends AbstractDao<?, ?>>... daoClasses) {
        super(context, name);
        this.daoClasses = daoClasses;
    }

    @SafeVarargs
    protected UpgradeAbleOpenHelper(@NonNull Context context, @NonNull String name, SQLiteDatabase.CursorFactory factory, @Nullable Class<? extends AbstractDao<?, ?>>... daoClasses) {
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

        if (daoClasses != null && daoClasses.length > 0) {
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
}
