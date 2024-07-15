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
 *     android:scrollbars="vertical"            //垂直滚动条
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
        /**
         * android:scrollbars="vertical" + 以下方法: 不活动的时候隐藏，活动的时候显示
         */
//        setScrollbarFadingEnabled(true);

        /**
         * 不活动的时候隐藏，活动的时候也隐藏..., = android:scrollbars="none"
         * 设置了没啥用?
         */
//        setVerticalScrollBarEnabled(true);

//        int scrollBarSize = getScrollBarSize();
//        int verticalScrollbarWidth = getVerticalScrollbarWidth();
//        int horizontalScrollbarHeight = getHorizontalScrollbarHeight();
//        LogUtils.errorFormat("scrollBarSize=%d, verticalScrollbarWidth=%d, horizontalScrollbarHeight=%d",
//                scrollBarSize, verticalScrollbarWidth, horizontalScrollbarHeight);

        //设置滑动方法
        setMovementMethod(ScrollingMovementMethod.getInstance());
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
