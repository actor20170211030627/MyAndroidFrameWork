package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.R;
import com.hjq.shape.view.ShapeTextView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/04/18
 *    desc   : 支持限定 Drawable 大小的 TextView    added: 播放/停止动画
 * <br /> <br />
 * 示例使用:
 * <pre>
 *  &lt;com.actor.myandroidframework.widget.DrawableTextView
 *      android:layout_width="wrap_content"
 *      android:layout_height="wrap_content"
 *      android:text="测试"
 *      android:textColor="@color/white"
 *      android:textSize="15sp"
 *      android:drawablePadding="5dp"
 *      android:drawableStart="@drawable/xxx"   //可以是一个&lt;animation-list 播放动画
 *      app:drawableWidth="25dp"                        //drawable 宽度
 *      app:drawableHeight="23dp"                       //drawable 高度
 *      app:drawableIsReset2Frame0AfterAnimStop="true"  //调用stopPlayAnim()后, 是否重置到第1帧, 默认true
 *      app:shape_solidColor="@color/blue" /&gt;    //背景色等...
 *
 * {@link 注意:} if在RecyclerView 中使用动画, 会出问题, 动画停不下来! 原因还未知. 请在RecyclerView中使用 {@link AnimationDrawableImageView}
 */
public final class DrawableTextView extends ShapeTextView {

    //stop()后, 动画是否要重置到第1帧
    protected boolean isReset2Frame0AfterStop = true;

    private int mDrawableWidth;
    private int mDrawableHeight;

    public DrawableTextView(@NonNull Context context) {
        this(context, null);
    }

    public DrawableTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
        mDrawableWidth = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableWidth, 0);
        mDrawableHeight = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableHeight, 0);
        isReset2Frame0AfterStop = array.getBoolean(R.styleable.DrawableTextView_drawableIsReset2Frame0AfterAnimStop, isReset2Frame0AfterStop);
        array.recycle();

        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 大小
     */
    public void setDrawableSize(int width, int height) {
        mDrawableWidth = width;
        mDrawableHeight = height;
        if (!isAttachedToWindow()) {
            return;
        }
        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 宽度
     */
    public void setDrawableWidth(int width) {
        mDrawableWidth = width;
        if (!isAttachedToWindow()) {
            return;
        }
        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 高度
     */
    public void setDrawableHeight(int height) {
        mDrawableHeight = height;
        if (!isAttachedToWindow()) {
            return;
        }
        refreshDrawablesSize();
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        if (!isAttachedToWindow()) {
            return;
        }
        refreshDrawablesSize();
    }

    @Override
    public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelative(start, top, end, bottom);
        if (!isAttachedToWindow()) {
            return;
        }
        refreshDrawablesSize();
    }

    /**
     * 刷新 Drawable 列表大小
     */
    private void refreshDrawablesSize() {
        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
            return;
        }
        Drawable[] compoundDrawables = getCompoundDrawables();
        if (compoundDrawables[0] != null || compoundDrawables[1] != null || compoundDrawables[2] != null || compoundDrawables[3] != null) {
            super.setCompoundDrawables(limitDrawableSize(compoundDrawables[0]),
                    limitDrawableSize(compoundDrawables[1]),
                    limitDrawableSize(compoundDrawables[2]),
                    limitDrawableSize(compoundDrawables[3]));
            return;
        }
        compoundDrawables = getCompoundDrawablesRelative();
        super.setCompoundDrawablesRelative(limitDrawableSize(compoundDrawables[0]),
                limitDrawableSize(compoundDrawables[1]),
                limitDrawableSize(compoundDrawables[2]),
                limitDrawableSize(compoundDrawables[3]));
    }

    /**
     * 重新限定 Drawable 宽高
     */
    private Drawable limitDrawableSize(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
            return drawable;
        }
        drawable.setBounds(0, 0, mDrawableWidth, mDrawableHeight);
        return drawable;
    }



    /**
     * 播放四周的动画
     * android.R.styleable#TextView_drawableLeft
     * android.R.styleable#TextView_drawableTop
     * android.R.styleable#TextView_drawableRight
     * android.R.styleable#TextView_drawableBottom
     */
    public void startPlayAnim() {
        Drawable[] compoundDrawables = getCompoundDrawables();
        Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();
//        LogUtils.errorFormat("compoundDrawables(%s) = %s", compoundDrawables.toString(), Arrays.toString(compoundDrawables));
//        LogUtils.errorFormat("compoundDrawablesRelative(%s) = %s", compoundDrawablesRelative.toString(), Arrays.toString(compoundDrawablesRelative));
        for (Drawable compoundDrawable : compoundDrawables) {
            if (compoundDrawable instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) compoundDrawable;
                animationDrawable.start();
            }
        }
    }

    /**
     * 停止四周的动画
     */
    public void stopPlayAnim() {
        Drawable[] compoundDrawables = getCompoundDrawables();
        Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();
//        LogUtils.errorFormat("compoundDrawables(%s) = %s", compoundDrawables.toString(), Arrays.toString(compoundDrawables));
//        LogUtils.errorFormat("compoundDrawablesRelative(%s) = %s", compoundDrawablesRelative.toString(), Arrays.toString(compoundDrawablesRelative));
        for (int i = 0; i < compoundDrawables.length; i++) {
            Drawable compoundDrawable = compoundDrawables[i];
            if (compoundDrawable instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) compoundDrawable;
                animationDrawable.stop();
                reset2Frame0(i, animationDrawable, compoundDrawables);
//                animationDrawable.unscheduleSelf(animationDrawable);
            }
        }
//        setCompoundDrawables(null, null, null, null);
//        if (compoundDrawables[0] != null || compoundDrawables[1] != null || compoundDrawables[2] != null || compoundDrawables[3] != null) {
//            LogUtils.errorFormat("compoundDrawables[0]=%s", compoundDrawables[0]);
//            setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
////            setCompoundDrawablesRelative(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
//        }

        for (int i = 0; i < compoundDrawablesRelative.length; i++) {
            Drawable drawable = compoundDrawablesRelative[i];
            if (drawable instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                animationDrawable.stop();
                reset2Frame0(i, animationDrawable, compoundDrawablesRelative);
//                animationDrawable.unscheduleSelf(animationDrawable);
            }
        }
//        setCompoundDrawablesRelative(null, null, null, null);
//        if (compoundDrawablesRelative[0] != null || compoundDrawablesRelative[1] != null || compoundDrawablesRelative[2] != null || compoundDrawablesRelative[3] != null) {
//            LogUtils.errorFormat("compoundDrawablesRelative[0]=%s", compoundDrawablesRelative[0]);
//            setCompoundDrawablesRelative(compoundDrawablesRelative[0], compoundDrawablesRelative[1], compoundDrawablesRelative[2], compoundDrawablesRelative[3]);
//        }
    }

    public void setReset2Frame0AfterStop(boolean isReset2Frame0AfterStop) {
        this.isReset2Frame0AfterStop = isReset2Frame0AfterStop;
    }

    /**
     * 重置到第1帧
     */
    public void reset2Frame0(int position, AnimationDrawable animationDrawable, Drawable[] compoundDrawables) {
        if (!isReset2Frame0AfterStop) return;
        //同1个引用, 无效
//        setImageDrawable(animationDrawable);
//        setBackground(animationDrawable);

        //有效1 (0~2ms)
        animationDrawable.setVisible(true, true);

        //android.graphics.drawable.BitmapDrawable
//        Drawable current = animationDrawable.getCurrent();

        //有效2 (0~2ms) (不用判断参数index是否越界, 但if越界的话, 会被置为空白)
//        int numberOfFrames = animationDrawable.getNumberOfFrames();
//        if (numberOfFrames > 0) {
//            boolean b = animationDrawable.selectDrawable(0);
//        }

        //有效3 (0~4ms) (需判断是否越界)
//        int numberOfFrames = animationDrawable.getNumberOfFrames();
//        if (numberOfFrames > 0) {
//            Drawable drawable = animationDrawable.getFrame(0);
//            switch (position) {
//                case 0:
//                    setCompoundDrawables(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
//                    break;
//                case 1:
//                    setCompoundDrawables(compoundDrawables[0], drawable, compoundDrawables[2], compoundDrawables[3]);
//                    break;
//                case 2:
//                    setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], drawable, compoundDrawables[3]);
//                    break;
//                default:
//                    setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], drawable);
//                    break;
//            }
//            setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
//        }
    }
}