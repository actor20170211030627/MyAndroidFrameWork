package com.actor.myandroidframework.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

/**
 * description: 自动计算子类高度的ViewPager <br />
 * Author     : ldf <br />
 * date       : 2022/3/5 on 16
 * @version 1.0
 * TODO: 2024/2/29 高度计算不对, 见拍拍拍错题上传时
 */
public class AutoCaculateHeightViewpager extends ScrollableViewPager {

    protected SparseIntArray heights = new SparseIntArray();
    protected boolean isShowHighestChild$onlyShowChildHeight = false;
    protected boolean skipTabLayout = true;
    //Viewpager 里是否有TabLayout
    protected boolean hasTabLayout = false;

    public AutoCaculateHeightViewpager(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public AutoCaculateHeightViewpager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        //滑动监听
        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                //如果最高页面的高度
                if (isShowHighestChild$onlyShowChildHeight) {
                    int currentHeight = layoutParams.height;
                    //最高child的高度
                    int hightestChild = currentHeight;
                    for (int i = 0; i < heights.size(); i++) {
                        int hChild = heights.get(i);
                        if (hChild > hightestChild) {
                            hightestChild = hChild;
                        }
                    }
                    //如果现在不是最高高度
                    if (hightestChild != currentHeight) {
                        layoutParams.height = hightestChild;
                        setLayoutParams(layoutParams);
//                        LogUtils.errorFormat("设置最高高度, position = %d, currentHeight = %d, hightestChild = %d", position, currentHeight, hightestChild);
                    }
                } else {
                    //如果显示相应页面自己的实际高度
                    int height = heights.get(position);
//                    LogUtils.errorFormat("position = %d, height = %d", position, height);
                    if (height > 0) {
                        layoutParams.height = height;
                        setLayoutParams(layoutParams);
                    }
                }
            }
        });
    }

    //初始化&页面加载数据后&每次切换: 都要走 onMeasure 方法
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //最高child的高度
        int hightestChild = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
//            LogUtils.error(child);
            if (skipTabLayout && child instanceof TabLayout) {
                hasTabLayout = true;
                continue;
            }
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int height = child.getMeasuredHeight();
            if (height > hightestChild) {
                hightestChild = height;
            }
//            LogUtils.errorFormat("i = %d, h = %d", i, height);
            heights.put(hasTabLayout ? i - 1 : i, height);
        }
        //如果是显示最高child的高度
        if (isShowHighestChild$onlyShowChildHeight) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(hightestChild, MeasureSpec.EXACTLY);
        } else {
            int currentItem = getCurrentItem();
            if (currentItem == 0) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(heights.get(0), MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置高度显示模式
     * @param isShowHightestChild$onlyShowChildHeight 是否显示最高child的高度: <br />
     *        true:  统一显示几个页面中最高页面的高度(矮的页面由于实际没有这么高, 底部会留空白) <br />
     *        false: 切换到相应页面时, 显示相应页面自己的实际高度
     */
    public void setShownHeightMode(boolean isShowHightestChild$onlyShowChildHeight) {
        this.isShowHighestChild$onlyShowChildHeight = isShowHightestChild$onlyShowChildHeight;
    }

    /**
     * @return 是否显示几个页面中最高页面的高度
     */
    public boolean getShownHeightMode() {
        return isShowHighestChild$onlyShowChildHeight;
    }

    /**
     * ∵ViewPager 里可以直接在xml中填充 TabLayout, ∴设置是否跳过计算ViewPager里的 TabLayout 的高度
     * @param skipTabLayout 是否跳过, 默认true
     */
    public void setSkipTabLayout(boolean skipTabLayout) {
        this.skipTabLayout = skipTabLayout;
    }
}
