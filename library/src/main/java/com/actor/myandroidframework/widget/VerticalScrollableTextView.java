package com.actor.myandroidframework.widget;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.hjq.shape.view.ShapeTextView;

/**
 * description: 可垂直滑动的TextView, 使用示例: <br />
 * <pre>
 * &lt;com.actor.myandroidframework.widget.VerticalScrollableTextView
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     android:maxLines="6"
 *     android:text="测试测试测试测试测试测试测试测试测试测试测试测试测试测试"
 *     android:textColor="@color/white"
 *     android:textSize="15sp"
 *     android:scrollbars="vertical"            //滚动条, if=vertical: 文字显示高度超过tv高度就一直显示滚动条. if不设置, 滑动的时候才显示滚动条
 *     app:shape_solidColor="@color/blue" /&gt;    //背景色
 *
 * //也可设置固定高度
 * &lt;com.actor.myandroidframework.widget.VerticalScrollableTextView
 *     android:layout_width="wrap_content"
 *     android:layout_height="100dp"
 *     ... /&gt;
 * </pre>
 *
 * @author : ldf
 * date       : 2024/7/15 on 11
 * @version 1.0
 */
public class VerticalScrollableTextView extends ShapeTextView {

    protected static final String NAMESPACE = MarqueeTextView.NAMESPACE;

    /**
     * <p>This view doesn't show scrollbars.</p>
     * {@hide}
     */
    private final int SCROLLBARS_NONE = 0x00000000;

//    /**
//     * <p>This view shows horizontal scrollbars.</p>
//     * {@hide}
//     */
//    private final int SCROLLBARS_HORIZONTAL = 0x00000100;
//
//    /**
//     * <p>This view shows vertical scrollbars.</p>
//     * {@hide}
//     */
//    private final int SCROLLBARS_VERTICAL = 0x00000200;


    public VerticalScrollableTextView(Context context) {
        super(context);
        init(context, null);
    }

    public VerticalScrollableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerticalScrollableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        //找不到, 报错
//        TypedArray t = context.obtainStyledAttributes(attrs, android.R.styleable.View_scrollbars);
//        boolean hasValue = t.hasValue(android.R.styleable.View_scrollbars);
//        t.recycle();
        int scrollbars = SCROLLBARS_NONE;
        if (attrs != null) scrollbars = attrs.getAttributeIntValue(NAMESPACE, "scrollbars", scrollbars);

        //设置滑动方法, ScrollingMovementMethod: 只支持垂直滚动！不支持水平横向滚动。
        setMovementMethod(ScrollingMovementMethod.getInstance());

        // 触发系统初始化scrollbar drawable，防止ScrollBarDrawable为null (无用)
//        awakenScrollBars(0);

        //if android:scrollbars 的值不是 none, 就默认让滚动条一直显示. != none 才可设置, 否则空指针
        if (scrollbars != SCROLLBARS_NONE) setScrollbarFadingEnabled(false);
    }

    /**
     * 设置滚动条是否能淡化。默认true: 只有手指滑动时滚动条才出现，松手一会自动隐藏
     * @param fadeScrollbars
     */
    public void setScrollbarFadingEnabled(boolean fadeScrollbars) {
        super.setScrollbarFadingEnabled(fadeScrollbars);
    }

    /**
     * Define the scrollbar fade duration, in milliseconds. 定义手指松开后，滚动条动画要花多久从可见变成透明. 淡化持续时间(毫秒)。<br />
     * 只有 <code>setScrollbarFadingEnabled(true)</code>（滚动条允许淡出）才生效 <br />
     * 属性: android:scrollbarFadeDuration="12313"
     * @param scrollBarFadeDuration 滚动条淡化持续时间
     * @see android.R.styleable#View_scrollbarFadeDuration
     * @see android.view.ViewConfiguration#getScrollBarFadeDuration()
     */
    @Override
    public void setScrollBarFadeDuration(int scrollBarFadeDuration) {
        super.setScrollBarFadeDuration(scrollBarFadeDuration);
    }

    /**
     * Defines the delay in milliseconds that a scrollbar waits before fade out. 定义滚动条在淡出前等待的延迟时间(毫秒)。<br />
     * 手指停止滑动之后，等待多久之后才开始执行淡出动画。 <br />
     * 属性: android:scrollbarDefaultDelayBeforeFade="12313"
     * @param scrollBarDefaultDelayBeforeFade the delay before scrollbars fade
     * @see android.view.ViewConfiguration#getScrollDefaultDelay()
     */
    @Override
    public void setScrollBarDefaultDelayBeforeFade(int scrollBarDefaultDelayBeforeFade) {
        super.setScrollBarDefaultDelayBeforeFade(scrollBarDefaultDelayBeforeFade);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        int lineCountBefore = getLineCount();
        super.setText(text, type);
        int lineCountAfter = getLineCount();
//        LogUtils.errorFormat("lineCountBefore=%d, lineCountAfter=%d", lineCountBefore, lineCountAfter);

        //if设置的内容比前一次的内容少, 并且前一次往上滑动了的话, 就会造成本次显示内容在最顶部, 导致可能看不见本次内容.
        if (lineCountAfter < lineCountBefore) {
            scrollTo(0, 0);
        }
    }
}
