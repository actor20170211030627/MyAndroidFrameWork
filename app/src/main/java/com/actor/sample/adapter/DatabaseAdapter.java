package com.actor.sample.adapter;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.sample.R;
import com.actor.sample.database.ItemEntity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

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

    public DatabaseAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.tv_delete);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ItemEntity item) {
        Map<String, Object> params = item.getParams();
        String param = null;
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                param = TextUtils2.getStringFormat("参数: key=%s, value=%s", entry.getKey(), entry.getValue());
                break;
            }
        }
        helper.setText(R.id.tv_name, "Name姓名: " + item.getName())
                .setText(R.id.tv_sex, "Sex性别: " + item.getSexStr())
                .setText(R.id.tv_id_card, "身份证IdCard: " + item.getIdCard())
                .setText(R.id.tv_params, param);
    }
}
