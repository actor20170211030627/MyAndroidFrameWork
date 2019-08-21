package com.actor.myandroidframework.activity;

import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Description: 从底部弹出的 非全屏 Activity
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/8/22 on 00:15
 *
 * @version 1.0
 */
public class BaseBottomActivity extends ActorBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(R.style.BottomSheet);//已在清单文件添加
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * 设置背景透明度, 在onStart中调用?
     */
    protected void setBackgroundTrans(@FloatRange(from = 0.0, to = 1.0) float trans) {
        Window window = getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.alpha = trans;//1.０全透明．０不透明．
        window.setAttributes(windowParams);
    }
}
