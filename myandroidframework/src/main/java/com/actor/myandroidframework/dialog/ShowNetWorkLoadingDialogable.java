package com.actor.myandroidframework.dialog;

import androidx.annotation.Nullable;

/**
 * description: show / dismiss Dialog able
 *
 * @author : ldf
 * date       : 2020/6/13 on 21:08
 * @version 1.0
 */
public interface ShowNetWorkLoadingDialogable {

    @Nullable
    LoadingDialog getNetWorkLoadingDialog();

    /**
     * @return 返回网络请求次数.(一个页面有可能有很多个请求)
     */
    int getRequestCount();

    /**
     * 设置请求次数
     */
    void setRequestCount(int requestCount);



    /**
     * 默认处理显示LoadingDialog, 如果不符合项目逻辑, 请重写本方法!
     */
    default void showNetWorkLoadingDialog() {
        //增加一次请求
        setRequestCount(getRequestCount() + 1);
//        if (getRequestCount() == 1) {
        LoadingDialog dialog = getNetWorkLoadingDialog();
        if (dialog != null && !dialog.isShowing()) dialog.show();
//        }
    }

    /**
     * 默认处理隐藏LoadingDialog, 如果不符合项目逻辑, 请重写本方法!
     */
    default void dismissNetWorkLoadingDialog() {
        //如果已经dismiss完了
        if (getRequestCount() <= 0) return;
        //次数-1
        setRequestCount(getRequestCount() - 1);
        //如果最后一次请求
        if (getRequestCount() == 0) {
            LoadingDialog dialog = getNetWorkLoadingDialog();
            if (dialog != null && dialog.isShowing()) dialog.dismiss();
        }
    }
}
