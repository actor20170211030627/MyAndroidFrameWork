package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
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
 *      ...
 *      android:drawablePadding="5dp"
 *      android:drawableStart="@drawable/xxx"   //可以是一个&lt;animation-list 播放动画
 *      android:drawableEnd="@drawable/xxx"     //可以是一个&lt;animation-list 播放动画
 *      ...
 *      app:dtvWidth="25dp"                     //四周Drawable 宽度(默认图片宽度)
 *      app:dtvHeight="23dp"                    //四周Drawable 高度(默认图片高度)
 *      app:dtvStartWidth="25dp"                //← 左侧Drawable 宽度
 *      app:dtvStartHeight="23dp"               //← 左侧Drawable 高度
 *      app:dtvTopWidth="25dp"                  //↑
 *      app:dtvTopHeight="23dp"                 //
 *      app:dtvEndWidth="25dp"                  //→
 *      app:dtvEndHeight="23dp"                 //
 *      app:dtvBottomWidth="25dp"               //↓
 *      app:dtvBottomHeight="23dp"              //
 *      app:drawableIsReset2Frame0AfterAnimStop="true"  //调用stopPlayAnim()后, 是否重置到第1帧, 默认true
 *      ...
 *      app:shape_solidColor="@color/blue" /&gt;    //背景色等...
 *
 * @see AnimationDrawableImageView
 */
public class DrawableTextView extends ShapeTextView {

    //stop()后, 动画是否要重置到第1帧
    protected boolean isReset2Frame0AfterStop = true;

    protected final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

//    protected int mDrawableWidth;
//    protected int mDrawableHeight;
    protected int mDrawableStartWidth, mDrawableStartHeight;
    protected int mDrawableTopWidth, mDrawableTopHeight;
    protected int mDrawableEndWidth, mDrawableEndHeight;
    protected int mDrawableBottomWidth, mDrawableBottomHeight;

    public DrawableTextView(@NonNull Context context) {
        super(context);
        init(context, null, android.R.attr.textViewStyle);
    }

    public DrawableTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, android.R.attr.textViewStyle);
    }

    public DrawableTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    protected void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
        //all
        int mDrawableWidth = array.getDimensionPixelSize(R.styleable.DrawableTextView_dtvWidth, WRAP_CONTENT);
        int mDrawableHeight = array.getDimensionPixelSize(R.styleable.DrawableTextView_dtvHeight, WRAP_CONTENT);
        //←
        mDrawableStartWidth = array.getDimensionPixelSize(R.styleable.DrawableTextView_dtvStartWidth, mDrawableWidth);
        mDrawableStartHeight = array.getDimensionPixelSize(R.styleable.DrawableTextView_dtvStartHeight, mDrawableHeight);
        //↑
        mDrawableTopWidth = array.getDimensionPixelSize(R.styleable.DrawableTextView_dtvTopWidth, mDrawableWidth);
        mDrawableTopHeight = array.getDimensionPixelSize(R.styleable.DrawableTextView_dtvTopHeight, mDrawableHeight);
        //→
        mDrawableEndWidth = array.getDimensionPixelSize(R.styleable.DrawableTextView_dtvEndWidth, mDrawableWidth);
        mDrawableEndHeight = array.getDimensionPixelSize(R.styleable.DrawableTextView_dtvEndHeight, mDrawableHeight);
        //↓
        mDrawableBottomWidth = array.getDimensionPixelSize(R.styleable.DrawableTextView_dtvBottomWidth, mDrawableWidth);
        mDrawableBottomHeight = array.getDimensionPixelSize(R.styleable.DrawableTextView_dtvBottomHeight, mDrawableHeight);
        //if播放Animation动画, 播放完成后是否重置到第0帧
        isReset2Frame0AfterStop = array.getBoolean(R.styleable.DrawableTextView_dtvIsReset2Frame0AfterAnimStop, isReset2Frame0AfterStop);
        array.recycle();

        refreshDrawablesSize();
    }

    /**
     * 限定四周 Drawable 大小
     */
    public void setDrawableSizeAll(@IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        mDrawableStartWidth = mDrawableTopWidth= mDrawableEndWidth = mDrawableBottomWidth = width;
        mDrawableStartHeight = mDrawableTopHeight = mDrawableEndHeight = mDrawableBottomHeight = height;
        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 大小
     * @param gravity 位置: Gravity.START 等...
     */
    public void setDrawableSize(int gravity, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            mDrawableStartWidth = width;
            mDrawableStartHeight = height;
        } else if ((gravity & Gravity.TOP) == Gravity.TOP) {
            mDrawableTopWidth = width;
            mDrawableTopHeight = height;
        } else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            mDrawableEndWidth = width;
            mDrawableEndHeight = height;
        } else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            mDrawableBottomWidth = width;
            mDrawableBottomHeight = height;
        } else return;
        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 宽度
     * @param gravity 位置: Gravity.START 等...
     */
    public void setDrawableWidth(int gravity, @IntRange(from = 0) int width) {
        if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            mDrawableStartWidth = width;
        } else if ((gravity & Gravity.TOP) == Gravity.TOP) {
            mDrawableTopWidth = width;
        } else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            mDrawableEndWidth = width;
        } else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            mDrawableBottomWidth = width;
        } else return;
        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 高度
     * @param gravity 位置: Gravity.START 等...
     */
    public void setDrawableHeight(int gravity, @IntRange(from = 0) int height) {
        if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            mDrawableStartHeight = height;
        } else if ((gravity & Gravity.TOP) == Gravity.TOP) {
            mDrawableTopHeight = height;
        } else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            mDrawableEndHeight = height;
        } else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            mDrawableBottomHeight = height;
        } else return;
        refreshDrawablesSize();
    }

    /**
     * 这方法在构造方法调用 super.() 的时候会被调用, 即: 值还没有被初始化就已经调用了这方法
     */
    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelative(start, top, end, bottom);
    }

    /**
     * 刷新 Drawable 列表大小
     */
    protected void refreshDrawablesSize() {
//        if (!isAttachedToWindow()) {
//            LogUtils.error("isAttachedToWindow() = false");
//            return;
//        }
        //在 super.setCompoundDrawables 之前调用, 否则 super.setCompoundDrawables 之后再调用值会被覆盖
        Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();
        Drawable[] compoundDrawables = getCompoundDrawables();
        if (compoundDrawables[0] != null || compoundDrawables[1] != null || compoundDrawables[2] != null || compoundDrawables[3] != null) {
            super.setCompoundDrawables(limitDrawableSize(0, compoundDrawables[0]),
                    limitDrawableSize(1, compoundDrawables[1]),
                    limitDrawableSize(2, compoundDrawables[2]),
                    limitDrawableSize(3, compoundDrawables[3]));
        }
        if (compoundDrawablesRelative[0] != null || compoundDrawablesRelative[1] != null || compoundDrawablesRelative[2] != null || compoundDrawablesRelative[3] != null) {
            super.setCompoundDrawablesRelative(limitDrawableSize(0, compoundDrawablesRelative[0]),
                    limitDrawableSize(1, compoundDrawablesRelative[1]),
                    limitDrawableSize(2, compoundDrawablesRelative[2]),
                    limitDrawableSize(3, compoundDrawablesRelative[3]));
        }
    }

    /**
     * 重新限定 Drawable 宽高
     */
    protected Drawable limitDrawableSize(int position, Drawable drawable) {
        if (drawable == null) return null;
        int width, height;
        if (position == 0) {
            width = mDrawableStartWidth;
            height = mDrawableStartHeight;
        } else if (position == 1) {
            width = mDrawableTopWidth;
            height = mDrawableTopHeight;
        } else if (position == 2) {
            width = mDrawableEndWidth;
            height = mDrawableEndHeight;
        } else {
            width = mDrawableBottomWidth;
            height = mDrawableBottomHeight;
        }
        if (width == WRAP_CONTENT) width = drawable.getIntrinsicWidth();
        if (height == WRAP_CONTENT) height = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, width, height);
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
        //left, top, right, bottom 方向的
        Drawable[] compoundDrawables = getCompoundDrawables();
        //start, top, end, bottom 方向的
        Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();
        Drawable background = getBackground();
//        LogUtils.errorFormat("compoundDrawables(%s) = %s", compoundDrawables.toString(), Arrays.toString(compoundDrawables));
//        LogUtils.errorFormat("compoundDrawablesRelative(%s) = %s", compoundDrawablesRelative.toString(), Arrays.toString(compoundDrawablesRelative));
//        LogUtils.errorFormat("background = %s", background);
        for (Drawable compoundDrawable : compoundDrawables) {
            if (compoundDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) compoundDrawable).start();
            }
        }
        for (Drawable compoundDrawable : compoundDrawablesRelative) {
            if (compoundDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) compoundDrawable).start();
            }
        }
        if (background instanceof AnimationDrawable) {
            ((AnimationDrawable) background).start();
        }
    }

    /**
     * 停止四周的动画
     */
    public void stopPlayAnim() {
        Drawable[] compoundDrawables = getCompoundDrawables();
        Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();
        Drawable background = getBackground();
//        LogUtils.errorFormat("compoundDrawables(%s) = %s", compoundDrawables.toString(), Arrays.toString(compoundDrawables));
//        LogUtils.errorFormat("compoundDrawablesRelative(%s) = %s", compoundDrawablesRelative.toString(), Arrays.toString(compoundDrawablesRelative));
//        LogUtils.errorFormat("background = %s", background);
        for (int i = 0; i < compoundDrawables.length; i++) {
            Drawable compoundDrawable = compoundDrawables[i];
            if (compoundDrawable instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) compoundDrawable;
                animationDrawable.stop();
                reset2Frame0(i, animationDrawable, compoundDrawables);
//                animationDrawable.unscheduleSelf(animationDrawable);
            }
        }
        for (int i = 0; i < compoundDrawablesRelative.length; i++) {
            Drawable drawable = compoundDrawablesRelative[i];
            if (drawable instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                animationDrawable.stop();
                reset2Frame0(i, animationDrawable, compoundDrawablesRelative);
//                animationDrawable.unscheduleSelf(animationDrawable);
            }
        }
        if (background instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) background;
            animationDrawable.stop();
            reset2Frame0(0, animationDrawable, null);
        }
    }

    public void setReset2Frame0AfterStop(boolean isReset2Frame0AfterStop) {
        this.isReset2Frame0AfterStop = isReset2Frame0AfterStop;
    }

    /**
     * 重置到第1帧
     */
    public void reset2Frame0(int position, @NonNull AnimationDrawable animationDrawable, @Nullable Drawable[] compoundDrawables) {
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPlayAnim();
    }
}