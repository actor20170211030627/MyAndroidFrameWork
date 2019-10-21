package com.actor.myandroidframework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * Description: Spinner功能增加
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/10/20 on 16:21
 *
 * @version 1.0 增加重复选中的监听
 * TODO 设置字体颜色/大小/居中
 */
@SuppressLint("AppCompatCustomView")
public class BaseSpinner extends Spinner {

    public BaseSpinner(Context context) {
        super(context);
    }

    public BaseSpinner(Context context, int mode) {
        super(context, mode);
    }

    public BaseSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public BaseSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, defStyleRes, mode, popupTheme);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        OnItemSelectedListener2 listener = (OnItemSelectedListener2) getOnItemSelectedListener();
        if (listener == null) return;
        boolean sameSelected = position == getSelectedItemPosition();
        if (sameSelected) {
            listener.onItemReSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        OnItemSelectedListener2 listener = (OnItemSelectedListener2) getOnItemSelectedListener();
        if (listener == null) return;
        boolean sameSelected = position == getSelectedItemPosition();
        if (sameSelected) {
            listener.onItemReSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    /**
     * @deprecated 重复选中时不会回调, 用这个:
     * @see #setOnItemSelectedListener(OnItemSelectedListener2)
     */
    @Deprecated
    @Override
    public void setOnItemSelectedListener(@Nullable OnItemSelectedListener listener) {
        super.setOnItemSelectedListener(listener);
    }

    /**
     * item选中监听(增加重复选中的监听)
     */
//    @Override
    public void setOnItemSelectedListener(@Nullable OnItemSelectedListener2 listener) {
        super.setOnItemSelectedListener(listener);
    }

    public interface OnItemSelectedListener2 extends OnItemSelectedListener {

        //再次选择了同一个item
        default void onItemReSelected(AdapterView<?> parent, View view, int position, long id){
        }

        //Adapter为空的时候就会调用到这个方法
        @Override
        default void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
