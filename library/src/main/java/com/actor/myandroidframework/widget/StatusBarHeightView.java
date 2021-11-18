package com.actor.myandroidframework.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.actor.myandroidframework.utils.ConfigUtils;

/**
 * Description: 状态栏高度, 用于占高
 * Author     : ldf
 * Date       : 2020-1-13 on 11:14
 * @version 1.0
 */
public class StatusBarHeightView extends View {

    public StatusBarHeightView(Context context) {
        super(context);
    }

    public StatusBarHeightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarHeightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatusBarHeightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //状态栏高度
        int statusBarHeight = ConfigUtils.STATUS_BAR_HEIGHT;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(statusBarHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
