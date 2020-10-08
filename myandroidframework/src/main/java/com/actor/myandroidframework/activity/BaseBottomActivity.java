package com.actor.myandroidframework.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

/**
 * Description: 从底部弹出的 非全屏 Activity
 * Author     : 李大发
 * Date       : 2019/8/22 on 00:15
 *
 * 怎么使用, 注意:
 * 1. 继承本类, 例 MyBaseBottomActivity extends BaseBottomActivity
 * 2. 要在你的Activity的清单文件中设置主题(因为即使本类设置了theme, 子类也没效果, 至少背景不透明), 示例:
 *      <activity
 *          android:name=".activity.MyBaseBottomActivity"
 *          android:theme="@style/BaseBottomActivity"/>
 *
 * @version 1.0
 */
public class BaseBottomActivity extends ActorBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
//        setTheme(R.style.BaseBottomActivity);//代码中设置无效啊??
    }

    /**
     * 设置背景透明度
     * @param trans 1.０全透明．０不透明．
     */
    protected void setDimAmount(@FloatRange(from = 0.0, to = 1.0) float trans) {
        Window window = getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
//        windowParams.alpha = trans;//这是设置界面的透明度, 不是背景
        windowParams.dimAmount = trans;//背景透明度
//        window.setDimAmount(trans);
        window.setAttributes(windowParams);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}
