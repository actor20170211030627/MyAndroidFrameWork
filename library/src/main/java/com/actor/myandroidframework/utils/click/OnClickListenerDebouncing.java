package com.actor.myandroidframework.utils.click;

import android.view.View;

import com.blankj.utilcode.util.DebouncingUtils;

/**
 * description: 防抖点击监听
 * company    :
 * @see com.blankj.utilcode.util.ClickUtils.OnDebouncingClickListener
 * @author : ldf
 * date       : 2026/9/12 on 12
 * @version 1.0
 */
public abstract class OnClickListenerDebouncing implements View.OnClickListener {

    private final long    mDuration;
    private final boolean mIsGlobal;

    public OnClickListenerDebouncing() {
        this(true, ClickUtils2.DEBOUNCING_DEFAULT_VALUE);
    }

    /**
     * @param isGlobal 是否是全局点击间隔, if true, 要等其他同样设置了防抖点击的点击间隔过了之后, 再点击才生效
     */
    public OnClickListenerDebouncing(final boolean isGlobal) {
        this(isGlobal, ClickUtils2.DEBOUNCING_DEFAULT_VALUE);
    }

    /**
     * @param duration 点击间隔, 默认200ms
     */
    public OnClickListenerDebouncing(final long duration) {
        this(true, duration);
    }

    /**
     * @param isGlobal 是否是全局点击间隔, if true, 要等其他同样设置了防抖点击的点击间隔过了之后, 再点击才生效
     * @param duration 点击间隔
     */
    public OnClickListenerDebouncing(final boolean isGlobal, final long duration) {
        mIsGlobal = isGlobal;
        mDuration = duration;
    }

    @Override
    public final void onClick(View v) {
        if (mIsGlobal) {
            if (System.currentTimeMillis() >= ClickUtils2.globalClickTime + mDuration) {
                ClickUtils2.globalClickTime = System.currentTimeMillis();
                onDebouncingClick(v);
            }
        } else {
            if (DebouncingUtils.isValid(v, mDuration)) onDebouncingClick(v);
        }
    }
    /**
     * 防抖点击回调
     * @param v
     */
    public abstract void onDebouncingClick(View v);
}