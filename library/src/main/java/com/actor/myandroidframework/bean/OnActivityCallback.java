package com.actor.myandroidframework.bean;

import android.content.Intent;

import androidx.annotation.Nullable;

/**
 * description: 跳转Activity后回调
 * company    :
 *
 * @author : ldf
 * date       : 2024/3/2 on 16
 * @version 1.0
 */
public interface OnActivityCallback {

    /**
     * 结果回调 from hjq
     * @param resultCode        结果码
     * @param data              数据
     */
    void onActivityResult(int resultCode, @Nullable Intent data);
}
