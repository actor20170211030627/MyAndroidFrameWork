package com.actor.sample.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.actor.myandroidframework.utils.database.GreenDaoUtils;
import com.actor.myandroidframework.widget.ItemRadioGroupLayout;
import com.actor.myandroidframework.widget.ItemTextInputLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.greendao.gen.ItemEntityDao;
import com.actor.sample.R;
import com.actor.sample.database.ItemEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 主页->数据库
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2020/2/18 on 11:15
 */
public class DatabaseActivity extends BaseActivity {

    @BindView(R.id.itil_name)
    ItemTextInputLayout  itilName;
    @BindView(R.id.irgl_sex)
    ItemRadioGroupLayout irglSex;
    @BindView(R.id.itil_idcard)
    ItemTextInputLayout  itilIdcard;
    @BindView(R.id.recycler_view)
    RecyclerView         recyclerView;

    private static final ItemEntityDao    DAO   = GreenDaoUtils.getDaoSession().getItemEntityDao();
    private              List<ItemEntity> items = new ArrayList<>();
    private              MyAdapter        myAdapter;
    private              NormalDialog     deleteDialog;
    private              int              deletePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        ButterKnife.bind(this);
        setTitle("主页->数据库(GreenDao)");
        myAdapter = new MyAdapter(R.layout.item_data_base_person, items);
        //item click
        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ItemEntity person = items.get(position);
                itilName.setText(person.getName());
                irglSex.setCheckedPosition(person.getSex());
                itilIdcard.setText(person.getIdCard());
            }
        });
        //item child click
        myAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
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

    @OnClick({R.id.btn_add, R.id.btn_update, R.id.btn_query, R.id.btn_query_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add://增, 身份证不为空, 其余的根据自己需求判断
                if (isNoEmpty(itilIdcard)) {
                    String idCard = getText(itilIdcard);
//                    if (!RegexUtils.isIDCard15(idCard) && RegexUtils.isIDCard18(idCard)) {
//                        toast("请输入正确的身份证(idcard error)!");
//                        return;
//                    }

                    //根据身份证查询, query by idCard
                    ItemEntity person = GreenDaoUtils.queryUnique(DAO, ItemEntityDao.Properties.IdCard.eq(idCard));
                    if (person == null) {
                        person = new ItemEntity(getText(itilName), idCard,  //name, idCard
                                Calendar.getInstance().getTime(),           //time
                                irglSex.getCheckedPosition());              //sex
                        long insertId = GreenDaoUtils.insert(DAO, person);
                        person.setId(insertId);
                        myAdapter.addData(0, person);
                    } else {
                        toast("这个身份证已经存入数据库(idcard already added)!");
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
                        GreenDaoUtils.update(DAO, person);
                        queryAll();
                    } else {
                        toast("未找到身份证对应的人(no idcard found)!");
                    }
                }
                break;
            case R.id.btn_query://根据身份证查找, query by idCard
                if (isNoEmpty(itilIdcard)) {
                    String idCard = getText(itilIdcard);
                    ItemEntity person = GreenDaoUtils.queryUnique(DAO, ItemEntityDao.Properties.IdCard.eq(idCard));
                    items.clear();
                    if (person != null) {
                        items.add(person);
                    } else {
                        toast("未找到身份证对应的人(no idcard found)!");
                    }
                    myAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_query_all://查找全部
                queryAll();
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

    private class MyAdapter extends BaseQuickAdapter<ItemEntity, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<ItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, ItemEntity item) {
            helper.addOnClickListener(R.id.tv_delete)
                    .setText(R.id.tv_name, "Name姓名: " + item.getName())
                    .setText(R.id.tv_sex, "Sex性别: " + item.getSexStr())
                    .setText(R.id.tv_id_card, "身份证IdCard: " + item.getIdCard());
        }
    }

    private NormalDialog getDeleteDialog() {
        if (deleteDialog == null) {
            deleteDialog = new NormalDialog(this).title("删除delete").btnText("取消No", "确定Yes");
            deleteDialog.setOnBtnClickL(null, new OnBtnClickL() {//param1 取消, param2 确认
                @Override
                public void onBtnClick() {//yes
                    ItemEntity person = items.get(deletePosition);
                    GreenDaoUtils.delete(DAO, person);
                    myAdapter.remove(deletePosition);
                    deleteDialog.dismiss();
                }
            });
            deleteDialog.content(getStringFormat("确定删除 %s 吗?", items.get(deletePosition).getName()));
        }
        return deleteDialog;
    }
}
