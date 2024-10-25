package com.actor.myandroidframework.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.shape.view.ShapeImageView;

/**
 * description: 有播放动画的ImageView, 使用示例:
 * <pre>
 * &lt;com.actor.myandroidframework.widget.AnimationDrawableImageView
 *     android:id="@+id/adiv_audio"
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     android:background="@drawable/icon_animatable"   //这是一个&lt;animation-list
 *     android:src="@drawable/icon_animatable" /&gt;    //这是一个&lt;animation-list
 *                                                  //background 和 src 应该只设置其中1个.
 * </pre>
 *
 * @author : ldf
 * @date   : 2024/10/24 on 17
 */
public class AnimationDrawableImageView extends ShapeImageView {

    //stop()后, 动画是否要重置到第1帧
    protected boolean isReset2Frame0AfterStop = true;

    public AnimationDrawableImageView(@NonNull Context context) {
        super(context);
    }

    public AnimationDrawableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimationDrawableImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 播放动画
     */
    public void startPlayAnim() {
        Drawable drawable = getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
            animationDrawable.start();
        }
        Drawable background = getBackground();
        if (background instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) background;
            animationDrawable.start();
        }
    }

    /**
     * 停止动画
     */
    public void stopPlayAnim() {
        Drawable drawable = getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
            animationDrawable.stop();
            reset2Frame0(animationDrawable);
        }
        Drawable background = getBackground();
        if (background instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) background;
            animationDrawable.stop();
            reset2Frame0(animationDrawable);
        }
    }

    public void setReset2Frame0AfterStop(boolean isReset2Frame0AfterStop) {
        this.isReset2Frame0AfterStop = isReset2Frame0AfterStop;
    }

    /**
     * 重置到第1帧
     */
    public void reset2Frame0(AnimationDrawable animationDrawable) {
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
//            setImageDrawable(drawable);
//            setImageDrawable(animationDrawable);
//        }
    }
}
