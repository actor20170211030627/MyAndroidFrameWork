package com.actor.sample.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.database.greendao.GreenDaoUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.others.widget.ItemRadioGroupLayout;
import com.actor.sample.R;
import com.actor.sample.database.ItemEntity;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.greendao.gen.ItemEntityDao;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.ConfirmPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.Collection;
import java.util.Map;

/**
 * description: 数据库
 * company    :
 *
 * @author : ldf
 * date       : 2021/11/18 on 17
 * @version 1.0
 */
public class DatabaseAdapter extends BaseQuickAdapter<ItemEntity, BaseViewHolder> {

    private final ItemEntityDao dao;
    private       int           deletePosition;
    private ConfirmPopupView    deletePopupView;

    public DatabaseAdapter(ItemEntityDao dao) {
        super(R.layout.item_data_base_person);
        this.dao = dao;
        addChildClickViewIds(R.id.tv_delete);
        setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.tv_delete) {//delete
                    deletePosition = position;
                    getDeleteDialog().show();
                }
            }
        });
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ItemEntity item) {
        int adapterPosition = helper.getAdapterPosition();
        Map<String, Object> params = item.getParams();
        String param = null;
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                param = TextUtils2.getStringFormat("参数: key=%s, value=%s", entry.getKey(), entry.getValue());
                break;
            }
        }
        ItemRadioGroupLayout<String> irglIsMyFriend = helper
                .setText(R.id.tv_name, "Name姓名: " + item.getName())
                .setText(R.id.tv_sex, "Sex性别: " + item.getSexStr())
                .setText(R.id.tv_id_card, "身份证IdCard: " + item.getIdCard())
                .setText(R.id.tv_params, param)
                .getView(R.id.irgl_is_my_friend);
        irglIsMyFriend.setOnCheckedChangeListener(null);
        irglIsMyFriend.setCheckedPosition(item.isMyFriend ? 1 : 0);
        irglIsMyFriend.getRadioGroup().setTag(adapterPosition);
        irglIsMyFriend.setOnCheckedChangeListener((group, checkedId, position, reChecked) -> {
            int adapterPosition2 = (int) group.getTag();
            //更新数据
            getItem(adapterPosition2).isMyFriend = position == 1;
            LogUtils.json(LogUtils.E, getData());
        });
    }

    private BasePopupView getDeleteDialog() {
        return deletePopupView = new XPopup.Builder(getContext())
                .asConfirm("删除delete",
                        TextUtils2.getStringFormat("确定删除 %s 吗?", getItem(deletePosition).getName()),
                        "取消No", "确定Yes", new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                ItemEntity person = getItem(deletePosition);
                                GreenDaoUtils.delete(dao, person);
                                removeAt(deletePosition);
                                deletePopupView.dismiss();
                            }
                        }, null, false);
    }

    @Override
    public void setList(@Nullable Collection<? extends ItemEntity> list) {
        super.setList(list);
        LogUtils.json(LogUtils.E, list);
    }
}
