package com.actor.myandroidframework.dialog;

import android.app.Dialog;

/**
 * description: show / dismiss Dialog able
 *
 * @author : 李大发
 * date       : 2020/6/13 on 21:08
 * @version 1.0
 */
public interface ShowLoadingDialogAble {

    Dialog getLoadingDialog(boolean cancelable);

    void showLoadingDialog();

    void dismissLoadingDialog();
}
