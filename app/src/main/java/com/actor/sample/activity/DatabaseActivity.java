package com.actor.sample.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.actor.database.greendao.GreenDaoUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.adapter.DatabaseAdapter;
import com.actor.sample.database.ItemEntity;
import com.actor.sample.databinding.ActivityDatabaseBinding;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.greendao.gen.ItemEntityDao;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 主页->数据库
 * Author     : ldf
 * Date       : 2020/2/18 on 11:15
 */
public class DatabaseActivity extends BaseActivity<ActivityDatabaseBinding> {

    private static final ItemEntityDao   DAO       = GreenDaoUtils.getDaoSession().getItemEntityDao();
    private final        DatabaseAdapter myAdapter = new DatabaseAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->数据库(GreenDao)");

        //item click
        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                ItemEntity person = myAdapter.getItem(position);
                viewBinding.itilName.setText(person.getName());
                viewBinding.irglSex.setCheckedPosition(person.getSex());
                viewBinding.itilIdcard.setText(person.getIdCard());
                Map<String, Object> params = person.getParams();
                if (params != null && !params.isEmpty()) {
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        viewBinding.itilKey.setText(entry.getKey());
                        String value = entry.getValue() == null ? null : entry.getValue() + "";
                        viewBinding.itilValue.setText(value);
                        break;
                    }
                }
            }
        });
        viewBinding.recyclerView.setAdapter(myAdapter);
    }

//    @OnClick({R.id.btn_add, R.id.btn_update, R.id.btn_query, R.id.btn_query_all})
    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add://增, 身份证不为空, 其余的根据自己需求判断
                if (isNoEmpty(viewBinding.itilIdcard)) {
                    String idCard = getText(viewBinding.itilIdcard);
//                    if (!RegexUtils.isIDCard15(idCard) && !RegexUtils.isIDCard18(idCard)) {
//                        ToasterUtils.warning("请输入正确的身份证(idcard error)!");
//                        return;
//                    }

                    //根据身份证查询, query by idCard
                    ItemEntity person = GreenDaoUtils.queryUnique(DAO, ItemEntityDao.Properties.IdCard.eq(idCard));
                    if (person == null) {
                        String key = getText(viewBinding.itilKey);
                        Map<String, Object> params = null;
                        if (!TextUtils.isEmpty(key)) {
                            params = new LinkedHashMap<>(1);
                            String value = getText(viewBinding.itilValue);
                            params.put(key, value);
                        }
                        person = new ItemEntity(getText(viewBinding.itilName), idCard,  //name, idCard
                                Calendar.getInstance().getTime(),           //time
                                viewBinding.irglSex.getCheckedPosition(),               //sex
                                params);                                    //params
                        long insertId = GreenDaoUtils.insert(DAO, person);
                        person.setId(insertId);
                        myAdapter.addData(0, person);
                    } else {
                        ToasterUtils.warning("这个身份证已经存入数据库(idcard already added)!");
                    }
                }
                break;
            case R.id.btn_update://根据身份证更改, update by idCard
                if (isNoEmpty(viewBinding.itilIdcard)) {
                    String idCard = getText(viewBinding.itilIdcard);
                    ItemEntity person = GreenDaoUtils.queryUnique(DAO, ItemEntityDao.Properties.IdCard.eq(idCard));
                    if (person != null) {
                        person.setName(getText(viewBinding.itilName));
                        person.setSex(viewBinding.irglSex.getCheckedPosition());
                        String key = getText(viewBinding.itilKey);
                        if (!TextUtils.isEmpty(key)) {
                            Map<String, Object> params = person.getParams();
                            if (params == null) {
                                params = new LinkedHashMap<>();
                                person.setParams(params);
                            }
                            params.put(key, getText(viewBinding.itilValue));
                        }
                        GreenDaoUtils.update(DAO, person);
                        queryAll();
                    } else {
                        ToasterUtils.error("未找到身份证对应的人(no idcard found)!");
                    }
                }
                break;
            case R.id.btn_query://根据身份证查找, query by idCard
                if (isNoEmpty(viewBinding.itilIdcard)) {
                    String idCard = getText(viewBinding.itilIdcard);
                    ItemEntity person = GreenDaoUtils.queryUnique(DAO, ItemEntityDao.Properties.IdCard.eq(idCard));
                    myAdapter.setList(null);
                    if (person != null) {
                        myAdapter.addData(person);
                    } else {
                        ToasterUtils.error("未找到身份证对应的人(no idcard found)!");
                    }
                }
                break;
            case R.id.btn_query_all://查找全部
                queryAll();
                break;
            default:
                break;
        }
    }

    //查找全部
    private void queryAll() {
        List<ItemEntity> persons = GreenDaoUtils.queryAll(DAO);
        myAdapter.setList(persons);
    }
}
