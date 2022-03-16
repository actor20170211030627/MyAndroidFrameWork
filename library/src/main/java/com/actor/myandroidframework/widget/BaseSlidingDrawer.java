package com.actor.myandroidframework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

import androidx.annotation.RequiresApi;

/**
 * Description: 抽屉布局, 防止点击穿透 <br />
 * Author     : ldf <br />
 * date       : 2019/4/11 on 09:58 <br />
 *
 * <br />
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th align="center">属性attrs</th>
 *         <th align="center">示例exams</th>
 *         <th align="center">说明docs</th>
 *     </tr>
 *     <tr>
 *         <td>{@link android.R.styleable#SlidingDrawer_allowSingleTap allowSingleTap}</td>
 *         <td>true</td>
 *         <td>1.是否可通过单击打开或关闭. 如果是false,则用户必须通过拖动, 默认true</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link android.R.styleable#SlidingDrawer_animateOnClick animateOnClick}</td>
 *         <td>true</td>
 *         <td>2.当使用者点击handle时, 指示抽屉是否应以动画打开/关闭, 默认true</td>
 *     </tr>
 *     <tr>
 *         <td>{@link android.R.styleable#SlidingDrawer_bottomOffset bottomOffset}</td>
 *         <td>0dp</td>
 *         <td>3.当抽屉关闭时, 'handle底部'距离'抽屉底部'的距离marginTop, 默认=0dp, 例:-50dp</td>
 *     </tr>
 *     <tr>
 *         <td>{@link android.R.styleable#SlidingDrawer_content content}</td>
 *         <td nowrap="nowrap">@id/content</td>
 *         <td>4.抽屉里内容的id, 必须设置</td>
 *     </tr>
 *     <tr>
 *         <td>{@link android.R.styleable#SlidingDrawer_handle handle}</td>
 *         <td>@id/handle</td>
 *         <td>5.抽屉里把手的id, 必须设置</td>
 *     </tr>
 *     <tr>
 *         <td>{@link android.R.styleable#SlidingDrawer_orientation orientation}</td>
 *         <td>vertical</td>
 *         <td>6.下方/右方, 默认vertical下方</td>
 *     </tr>
 *     <tr>
 *         <td>{@link android.R.styleable#SlidingDrawer_topOffset topOffset}</td>
 *         <td>0dp</td>
 *         <td>7.当抽屉打开时, 'handle顶部'距离'抽屉顶部'的距离, 默认=0dp, 例:200dp</td>
 *     </tr>
 * </table>
 */
public class BaseSlidingDrawer extends SlidingDrawer {

    protected OnTouchListener onTouchListener;

    public BaseSlidingDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseSlidingDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseSlidingDrawer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        super.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setClickable(isOpened());//防止点击穿透
                if (onTouchListener != null) {
                    onTouchListener.onTouch(v, event);
                }
                return false;
            }
        });
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    /**
     * 打开抽屉监听
     */
    @Override
    public void setOnDrawerOpenListener(OnDrawerOpenListener onDrawerOpenListener) {
        super.setOnDrawerOpenListener(onDrawerOpenListener);
    }

    /**
     * 关闭抽屉监听
     */
    @Override
    public void setOnDrawerCloseListener(OnDrawerCloseListener onDrawerCloseListener) {
        super.setOnDrawerCloseListener(onDrawerCloseListener);
    }

    /**
     * 抽屉滑动监听
     */
    @Override
    public void setOnDrawerScrollListener(OnDrawerScrollListener onDrawerScrollListener) {
        super.setOnDrawerScrollListener(onDrawerScrollListener);
    }

    @Override
    public void animateToggle() {
        super.animateToggle();
    }

    /**
     * 切换打开和关闭的抽屉SlidingDrawer
     */
    @Override
    public void toggle() {
        super.toggle();
    }

    /**
     * 屏蔽触摸事件
     */
    @Override
    public void lock() {
        super.lock();
    }

    /**
     * 解除屏蔽触摸事件
     */
    @Override
    public void unlock() {
        super.unlock();
    }

    /**
     * 抽屉是否已全部打开
     * @return
     */
    @Override
    public boolean isOpened() {
        return super.isOpened();
    }

    /**
     * 抽屉是否在移动
     */
    @Override
    public boolean isMoving() {
        return super.isMoving();
    }

    /**
     * 获取把手
     */
    @Override
    public View getHandle() {
        return super.getHandle();
    }

    /**
     * 获取内容
     */
    @Override
    public View getContent() {
        return super.getContent();
    }

    @Override
    public void open() {
        super.open();
    }

    @Override
    public void animateOpen() {
        //这儿要判断一下, 否则如果是打开状态, 会关闭...
        if (!isOpened()) super.animateOpen();
    }

    /**
     * 关闭时有动画
     */
    @Override
    public void animateClose() {
        super.animateClose();
    }

    /**
     * 关闭抽屉
     */
    @Override
    public void close() {
        super.close();
    }
}
