package com.actor.myandroidframework.dialog;

import android.app.Dialog;

import androidx.annotation.Nullable;

/**
 * description: show / dismiss LoadingDialog
 *
 * @author : ldf
 * date       : 2020/6/13 on 21:08
 * @version 1.0
 */
public interface ShowLoadingDialogAble {

    public final int[] _SHOW_LOADING_DIALOG_COUNT = {0};

    @Nullable
    Dialog getLoadingDialog();



    /**
     * 默认处理显示LoadingDialog, 如果不符合项目逻辑, 请重写本方法!
     */
    default void showLoadingDialog() {
        _SHOW_LOADING_DIALOG_COUNT[0] ++;
        Dialog dialog = getLoadingDialog();
        if (dialog != null && !dialog.isShowing()) dialog.show();
    }

    /**
     * 默认处理隐藏LoadingDialog, 如果不符合项目逻辑, 请重写本方法!
     */
    default void dismissLoadingDialog() {
        if (_SHOW_LOADING_DIALOG_COUNT[0] > 0) _SHOW_LOADING_DIALOG_COUNT[0] --;
        if (_SHOW_LOADING_DIALOG_COUNT[0] <= 0) {
            Dialog dialog = getLoadingDialog();
            if (dialog != null && dialog.isShowing()) dialog.dismiss();
        }
    }
}
