package com.actor.sample.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.actor.database.greendao.GreenDaoUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.others.widget.ItemRadioGroupLayout;
import com.actor.others.widget.ItemTextInputLayout;
import com.actor.sample.R;
import com.actor.sample.adapter.DatabaseAdapter;
import com.actor.sample.database.ItemEntity;
import com.actor.sample.databinding.ActivityDatabaseBinding;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.greendao.gen.ItemEntityDao;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.ConfirmPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;

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

    private ItemTextInputLayout  itilName;
    private ItemRadioGroupLayout<String> irglSex;
    private ItemTextInputLayout  itilIdcard;
    private ItemTextInputLayout  itilKey;
    private ItemTextInputLayout  itilValue;
    private RecyclerView         recyclerView;

    private static final ItemEntityDao   DAO = GreenDaoUtils.getDaoSession().getItemEntityDao();
    private              DatabaseAdapter myAdapter;
    private              int             deletePosition;
    private              ConfirmPopupView deletePopupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->数据库(GreenDao)");
        itilName = viewBinding.itilName;
        irglSex = viewBinding.irglSex;
        itilIdcard = viewBinding.itilIdcard;
        itilKey = viewBinding.itilKey;
        itilValue = viewBinding.itilValue;
        recyclerView = viewBinding.recyclerView;

        myAdapter = new DatabaseAdapter(R.layout.item_data_base_person);
        //item click
        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ItemEntity person = myAdapter.getItem(position);
                itilName.setText(person.getName());
                irglSex.setCheckedPosition(person.getSex());
                itilIdcard.setText(person.getIdCard());
                Map<String, Object> params = person.getParams();
                if (params != null && !params.isEmpty()) {
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        itilKey.setText(entry.getKey());
                        String value = entry.getValue() == null ? null : entry.getValue() + "";
                        itilValue.setText(value);
                        break;
                    }
                }
            }
        });
        //item child click
        myAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tv_delete) {//delete
                    deletePosition = position;
                    getDeleteDialog().show();
                }
            }
        });
        recyclerView.setAdapter(myAdapter);
    }

//    @OnClick({R.id.btn_add, R.id.btn_update, R.id.btn_query, R.id.btn_query_all})
    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add://增, 身份证不为空, 其余的根据自己需求判断
                if (isNoEmpty(itilIdcard)) {
                    String idCard = getText(itilIdcard);
//                    if (!RegexUtils.isIDCard15(idCard) && !RegexUtils.isIDCard18(idCard)) {
//                        ToasterUtils.warning("请输入正确的身份证(idcard error)!");
//                        return;
//                    }

                    //根据身份证查询, query by idCard
                    ItemEntity person = GreenDaoUtils.queryUnique(DAO, ItemEntityDao.Properties.IdCard.eq(idCard));
                    if (person == null) {
                        String key = getText(itilKey);
                        Map<String, Object> params = null;
                        if (!TextUtils.isEmpty(key)) {
                            params = new LinkedHashMap<>(1);
                            String value = getText(itilValue);
                            params.put(key, value);
                        }
                        person = new ItemEntity(getText(itilName), idCard,  //name, idCard
                                Calendar.getInstance().getTime(),           //time
                                irglSex.getCheckedPosition(),               //sex
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
                if (isNoEmpty(itilIdcard)) {
                    String idCard = getText(itilIdcard);
                    ItemEntity person = GreenDaoUtils.queryUnique(DAO, ItemEntityDao.Properties.IdCard.eq(idCard));
                    if (person != null) {
                        person.setName(getText(itilName));
                        person.setSex(irglSex.getCheckedPosition());
                        String key = getText(itilKey);
                        if (!TextUtils.isEmpty(key)) {
                            Map<String, Object> params = person.getParams();
                            if (params == null) {
                                params = new LinkedHashMap<>();
                                person.setParams(params);
                            }
                            params.put(key, getText(itilValue));
                        }
                        GreenDaoUtils.update(DAO, person);
                        queryAll();
                    } else {
                        ToasterUtils.error("未找到身份证对应的人(no idcard found)!");
                    }
                }
                break;
            case R.id.btn_query://根据身份证查找, query by idCard
                if (isNoEmpty(itilIdcard)) {
                    String idCard = getText(itilIdcard);
                    ItemEntity person = GreenDaoUtils.queryUnique(DAO, ItemEntityDao.Properties.IdCard.eq(idCard));
                    myAdapter.setNewData(null);
                    if (person != null) {
                        myAdapter.addData(person);
                    } else {
                        ToasterUtils.error("未找到身份证对应的人(no idcard found)!");
                    }
                    myAdapter.notifyDataSetChanged();
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
        if (persons != null) {
            myAdapter.replaceData(persons);
        }
    }

    private BasePopupView getDeleteDialog() {
        deletePopupView = new XPopup.Builder(this)
                .asConfirm("删除delete",
                        getStringFormat("确定删除 %s 吗?", myAdapter.getItem(deletePosition).getName()),
                        "取消No", "确定Yes", new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                ItemEntity person = myAdapter.getItem(deletePosition);
                                GreenDaoUtils.delete(DAO, person);
                                myAdapter.remove(deletePosition);
                                deletePopupView.dismiss();
                            }
                        }, null, false);
        return deletePopupView;
    }
}
