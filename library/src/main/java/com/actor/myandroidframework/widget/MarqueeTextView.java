package com.actor.myandroidframework.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.hjq.shape.view.ShapeTextView;

/**
 * Description: 跑马灯, 向左滚动 <br />
 * 示例写法:
 * <pre>
 *  &lt;com.actor.myandroidframework.widget.MarqueeTextView
 *      android:layout_width="wrap_content"
 *      android:layout_height="wrap_content"
 *      android:text="测试测试测试测试测试测试测试测试测试测试测试测试测试测试"
 *      android:textColor="@color/white" /&gt;
 *
 *      //不用额外写以下属性:
 *      <s>android:ellipsize="marquee"</s>
 *      <s>android:focusable="true"</s>
 *      <s>android:focusableInTouchMode="true"</s>
 *      <s>android:marqueeRepeatLimit="marquee_forever"</s> //滚动次数, int值(系统默认3次, 但本控件修改为默认无限滚动)
 *      <s>android:singleLine="true"</s>
 * </pre>
 *
 * @Author     : 李大发
 * @Date       : 2017/1/12 on 20:10
 * @version 1.0
 */
public class MarqueeTextView extends ShapeTextView {

    protected static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

    public MarqueeTextView(Context context) {
        super(context);
        init(context, null);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setSingleLine(true);
        if (attrs != null) {
            //找不到, 报错
//            TypedArray t = context.obtainStyledAttributes(attrs, android.R.styleable.TextView);
//            boolean hasValue = t.hasValue(android.R.styleable.TextView_marqueeRepeatLimit);
//            t.recycle();
            int marqueeRepeatLimit = attrs.getAttributeIntValue(NAMESPACE, "marqueeRepeatLimit", Integer.MIN_VALUE);
            if (marqueeRepeatLimit != Integer.MIN_VALUE) {
                setMarqueeRepeatLimit(marqueeRepeatLimit);
            } else {    //无限滚动
                setMarqueeRepeatLimit(-1);
            }
        }
    }

    /**
     * 强制返回true, 让此TextView永远有焦点, 解决了多个跑马灯, 只有一个跑起来的bug
     */
    @Override
    public boolean isFocused() {
        return true;
    }

    /**
     * @param marqueeLimit 设置跑马灯重复次数, -1无限
     */
    @Override
    public void setMarqueeRepeatLimit(int marqueeLimit) {
        super.setMarqueeRepeatLimit(marqueeLimit);
    }
}
