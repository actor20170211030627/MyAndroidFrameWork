package com.actor.myandroidframework.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.actor.myandroidframework.utils.LogUtils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexLine;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

/**
 * Description:设置{@link RecyclerView}间距. Decoration:装饰物 <br />
 * Author     : ldf <br />
 * Date       : 2019/7/21 on 16:31 <br />
 *
 *  <br />
 * 设置{@link RecyclerView}的 LayoutManager 的水平/垂直间距:
 * <ul>
 *     <li>{@link LinearLayoutManager}</li>
 *     <li>{@link GridLayoutManager}</li>
 *     <li>{@link StaggeredGridLayoutManager}</li>
 *     <li>{@link FlexboxLayoutManager}</li>
 * </ul>
 * 示例用法:
 * <pre>
 *     int dp20 = UiUtils.dp2px(20);
 *     recyclerView.{@link RecyclerView#addItemDecoration(RecyclerView.ItemDecoration) addItemDecoration(new BaseItemDecoration(dp20, dp20))};
 * </pre>
 */
public class BaseItemDecoration extends RecyclerView.ItemDecoration {

    protected float horizontalSpacing;
    protected float verticalSpacing;

    protected boolean loggable = false;
    protected Boolean hasFlexboxLayoutManager = null;

    /**
     * @param horizontalSpacing RecyclerView 的水平间距
     * @param verticalSpacing RecyclerView 的垂直间距
     */
    public BaseItemDecoration(float horizontalSpacing, float verticalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
    }

    /**
     * @param outRect Rect to receive the output.//Rect(矩形)接收输出。
     * @param view The child view to decorate.//RecyclerView 中的 Item的View
     * @param parent RecyclerView this ItemDecoration is decorating.//RecyclerView 本身
     * @param state The current state of RecyclerView.//状态
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        //网格布局,不能先判断LinearLayoutManager
        if (layoutManager instanceof GridLayoutManager) {
            dealGridLayoutManagerItemOffsets((GridLayoutManager) layoutManager, outRect, view, parent, state);
            return;
        }

        //线性布局
        if (layoutManager instanceof LinearLayoutManager) {
            dealLinearLayoutManagerItemOffsets((LinearLayoutManager) layoutManager, outRect, view, parent, state);
            return;
        }

        //瀑布流
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            dealStaggeredGridLayoutManagerItemOffsets((StaggeredGridLayoutManager) layoutManager, outRect, view, parent, state);
            return;
        }

        if (hasFlexboxLayoutManager() && layoutManager instanceof FlexboxLayoutManager) {
            /**
             * if你使用了这个FlexboxLayoutManager, 你需要添加依赖:
             * //https://github.com/google/flexbox-layout
             * implementation 'com.google.android.flexbox:flexbox:3.0.0'
             *
             * @see com.google.android.flexbox.FlexboxItemDecoration
             */
            dealFlexboxLayoutManagerItemOffsets((FlexboxLayoutManager) layoutManager, outRect, view, parent, state);
            return;
        }

        LogUtils.errorFormat("%s 还未适配间隙的设定!", layoutManager);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    /**
     * @param glm GridLayoutManager
     * @param outRect Rect to receive the output.//Rect(矩形)接收输出。
     * @param view The child view to decorate.//RecyclerView 中的 Item的View
     * @param parent RecyclerView this ItemDecoration is decorating.//RecyclerView 本身
     * @param state The current state of RecyclerView.//状态
     */
    protected void dealGridLayoutManagerItemOffsets(GridLayoutManager glm, @NonNull Rect outRect,
                                                    @NonNull View view, @NonNull RecyclerView parent,
                                                    @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int orientation = glm.getOrientation();
        int spanCount = glm.getSpanCount();     //垂直: 一共多少列,  水平: 一共多少行
        int itemCount = glm.getItemCount();     //多少条数据
        GridLayoutManager.SpanSizeLookup spanSizeLookup = glm.getSpanSizeLookup();
        //item元素在GridLayoutManager中占据的格子数量‌。默认占据1个格子。
        int spanSize = spanSizeLookup.getSpanSize(position);

        if (loggable) {
            LogUtils.error("///////////////////////////////////////////////////////////////////////////");
            LogUtils.errorFormat("position = %d, spanCount = %d, spanSize = %d", position, spanCount, spanSize);

            //当前item开始位置的Index, 以spanCount个数计算, 取值范围[0~spanCount)
//            int spanIndex = spanSizeLookup.getSpanIndex(position, spanCount);
//            //在第几行
//            int spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount);
//            LogUtils.errorFormat("spanIndex = %d, spanGroupIndex = %d", spanIndex, spanGroupIndex);
        }

        if (spanCount <= 0) {
            LogUtils.errorFormat("spanCount = %d: 非法设置! spanCount表示每一行/每一列 item元素个数", spanCount);
            return;
        }
        if (spanSize <= 0 || spanSize > spanCount) {
            LogUtils.errorFormat("position = %d 位置的 spanSize = %d: 非法设置! spanSize表示item元素在GridLayoutManager中占据的格子数量, 取值范围[1~spanCount(=%d)], 默认=1", position, spanSize, spanCount);
            return;
        }

        //每1行/每1列 开始item的position
        int startPosition = 0;
        for (int i = 0, spanSizeCount = 0; i <= position; i++) {
            int spanSizeI = spanSizeLookup.getSpanSize(i);
            spanSizeCount += spanSizeI;
            if (spanSizeCount > spanCount) {
                // 换行，记录新的起始位置
                startPosition = i;
                spanSizeCount = spanSizeI;
            }
            if (loggable) {
                LogUtils.errorFormat("i = %d, spanSizeCount=%d, startPosition=%d", i, spanSizeCount, startPosition);
            }
        }
        //每1行/每1列 item个数
        int rowColumnItemCount = 0;
        for (int i = startPosition, spanSizeCount = 0; i < startPosition + spanCount; i++) {
            int spanSizeI = i >= itemCount ? 1 : spanSizeLookup.getSpanSize(i);
            spanSizeCount += spanSizeI;
            if (spanSizeCount > spanCount) break;
            rowColumnItemCount ++;
        }

        if (loggable) {
            LogUtils.errorFormat("startPosition=%d, rowColumnItemCount=%d", startPosition, rowColumnItemCount);
        }

        //垂直方向(Grid上下滑动,默认)
        if (orientation == OrientationHelper.VERTICAL) {
            int column = position - startPosition; // item在第几列,从0开始
            float min = horizontalSpacing / rowColumnItemCount;//最小基数
//            outRect.left = column * horizontalSpacing / rowColumnItemCount; // column * ((1f / rowColumnItemCount) * spacing)
//            outRect.right = spacing - (column + 1) * spacing / rowColumnItemCount; // spacing - (column + 1) * ((1f / rowColumnItemCount) * spacing)
            outRect.left = (int) (column * min);
            outRect.right = (int) (min * (rowColumnItemCount - column - 1));
            outRect.top = position < rowColumnItemCount ? 0 : (int) verticalSpacing;//第一行的都没有top
            return;
        }
        //水平方向(Grid左右滑动)
        if (orientation == OrientationHelper.HORIZONTAL) {
            int row = position - startPosition; // item在第几行,从0开始
            float min = verticalSpacing / rowColumnItemCount;//最小基数(有可能除出来=0, 所以这儿用float)
            outRect.left = position < rowColumnItemCount ? 0 : (int) horizontalSpacing;//第一列的都没有left
            outRect.top = (int) (row * min);
            outRect.bottom = (int) (min * (rowColumnItemCount - row - 1));
        }
    }

    /**
     * @param llm LinearLayoutManager
     * @param outRect Rect to receive the output.//Rect(矩形)接收输出。
     * @param view The child view to decorate.//RecyclerView 中的 Item的View
     * @param parent RecyclerView this ItemDecoration is decorating.//RecyclerView 本身
     * @param state The current state of RecyclerView.//状态
     */
    protected void dealLinearLayoutManagerItemOffsets(LinearLayoutManager llm, @NonNull Rect outRect,
                                                      @NonNull View view, @NonNull RecyclerView parent,
                                                      @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int orientation = llm.getOrientation();
        if (orientation == OrientationHelper.VERTICAL) {            //垂直方向(List上下滑动,默认)
            outRect.top = position == 0 ? 0 : (int) verticalSpacing;
        } else if (orientation == OrientationHelper.HORIZONTAL) {   //水平方向(List水平滑动)
            outRect.left = position == 0 ? 0 : (int) horizontalSpacing;
        }
    }

    /**
     * @param sglm StaggeredGridLayoutManager
     * @param outRect Rect to receive the output.//Rect(矩形)接收输出。
     * @param view The child view to decorate.//RecyclerView 中的 Item的View
     * @param parent RecyclerView this ItemDecoration is decorating.//RecyclerView 本身
     * @param state The current state of RecyclerView.//状态
     */
    protected void dealStaggeredGridLayoutManagerItemOffsets(StaggeredGridLayoutManager sglm,
                                                             @NonNull Rect outRect, @NonNull View view,
                                                             @NonNull RecyclerView parent,
                                                             @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int orientation = sglm.getOrientation();
        int spanCount = sglm.getSpanCount();
        //垂直方向(Grid上下滑动,默认)
        if (orientation == OrientationHelper.VERTICAL) {
            int column = position % spanCount; // item在第几列,从0开始
            float min = horizontalSpacing / spanCount;//最小基数
            outRect.left = (int) (column * min);
            outRect.right = (int) (min * (spanCount - column - 1));
            outRect.top = position < spanCount ? 0 : (int) verticalSpacing;//第一行的都没有top
//            outRect.left = column * horizontalSpacing / spanCount; // column * ((1f / spanCount) * spacing)
//            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            return;
        }
        //水平方向(Grid左右滑动)
        if (orientation == OrientationHelper.HORIZONTAL) {
            int row = position % spanCount; // item在第几行,从0开始
            float min = verticalSpacing / spanCount;//最小基数
            outRect.left = position < spanCount ? 0 : (int) horizontalSpacing;//第一列的都没有left
            outRect.top = (int) (row * min);
            outRect.bottom = (int) (min * (spanCount - row - 1));
        }
    }

    /**
     * @param fblm FlexboxLayoutManager
     * @param outRect Rect to receive the output.//Rect(矩形)接收输出。
     * @param view The child view to decorate.//RecyclerView 中的 Item的View
     * @param parent RecyclerView this ItemDecoration is decorating.//RecyclerView 本身
     * @param state The current state of RecyclerView.//状态
     */
    protected void dealFlexboxLayoutManagerItemOffsets(FlexboxLayoutManager fblm,
                                                       @NonNull Rect outRect, @NonNull View view,
                                                       @NonNull RecyclerView parent,
                                                       @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int flexDirection = fblm.getFlexDirection();

        if (loggable) {
            LogUtils.error("///////////////////////////////////////////////////////////////////////////");
            int width = fblm.getWidth();    //RecyclerView的宽度
            int height = fblm.getHeight();  //RecyclerView的高度
            int itemWidth = fblm.getDecoratedMeasuredWidth(view);   //119
            int itemHeight = fblm.getDecoratedMeasuredHeight(view); //78
            int sumOfCrossSize = fblm.getSumOfCrossSize();  //1279
            int topOffset = fblm.getTopDecorationHeight(view);      //一直= 0
            LogUtils.errorFormat("position=%d, recyclerView.width = %d, recyclerView.height=%d, itemWidth=%d, itemHeight=%d, sumOfCrossSize=%d, topOffset=%d", position, width, height, itemWidth, itemHeight, sumOfCrossSize, topOffset);
        }

        //position在第几行 (错, 往上滑的时候是装满元素的)
        List<FlexLine> flexLines = fblm.getFlexLines();                 //0
//        List<FlexLine> flexLinesInternal = fblm.getFlexLinesInternal(); //0 与上1行的区别是这里面没有过滤可能有的空行

        //计算item所在行是第几行
        int itemLinePps = 0;
        for (int i = 0, currentCount = 0; i < flexLines.size(); i++) {
            int itemsInLine = flexLines.get(i).getItemCount();
            currentCount += itemsInLine;
            if (position < currentCount) break;
            itemLinePps ++;
            if (loggable) {
                LogUtils.errorFormat("第 %d 行, currentCount = %d, itemsInLine = %d, itemLinePps = %d",
                        i, currentCount, itemsInLine, itemLinePps);
            }
        }

        /**
         * 垂直排布, 水平方向滑动
         * TODO: 2025/4/30 这种"垂直排布, 水平方向滑动"方式显示有问题, 原因未知!
         */
        if (flexDirection == FlexDirection.COLUMN || flexDirection == FlexDirection.COLUMN_REVERSE) {
            outRect.left = itemLinePps == 0 ? 0 : (int) horizontalSpacing;
            if (itemLinePps > 0) {
                //上一行的信息(不是position所在行)
                FlexLine flexLine = flexLines.get(itemLinePps - 1);
                if (loggable) {
                    //                                                    上1行第0个元素的position    上1行有多少个元素
                    LogUtils.errorFormat("FirstIndex=%d, ItemCount=%d", flexLine.getFirstIndex(), flexLine.getItemCount());
                }

                //每1行/每1列 开始item的position
                int startPosition = flexLine.getFirstIndex() + flexLine.getItemCount();
                //判断是否是每列第1个Item
                outRect.top = startPosition == position ? 0 : (int) verticalSpacing;
            } else {
                //第0列
                outRect.top = position == 0 ? 0 : (int) verticalSpacing;
            }
            if (loggable) {
                LogUtils.errorFormat("verticalSpacing=%f, outRect=%s", verticalSpacing, outRect);
            }
            return;
        }
        //水平排布, 垂直方向滑动
        if (flexDirection == FlexDirection.ROW || flexDirection == FlexDirection.ROW_REVERSE) {
            if (itemLinePps > 0) {
                //上一行的信息(不是position所在行)
                FlexLine flexLine = flexLines.get(itemLinePps - 1);
                if (loggable) {
                    //                                                    上1行第0个元素的position    上1行有多少个元素
                    LogUtils.errorFormat("FirstIndex=%d, ItemCount=%d", flexLine.getFirstIndex(), flexLine.getItemCount());
                }

                //每1行/每1列 开始item的position
                int startPosition = flexLine.getFirstIndex() + flexLine.getItemCount();
                //判断是否是每行第1个Item
                outRect.left = startPosition == position ? 0 : (int) horizontalSpacing;
            } else {
                //第0行
                outRect.left = position == 0 ? 0 : (int) horizontalSpacing;
            }
            outRect.top = itemLinePps == 0 ? 0 : (int) verticalSpacing;
            if (loggable) {
                LogUtils.errorFormat("verticalSpacing=%f, outRect=%s", verticalSpacing, outRect);
            }
        }
    }

    protected boolean hasFlexboxLayoutManager() {
        if (hasFlexboxLayoutManager != null) return hasFlexboxLayoutManager;
        try {
            Class.forName("com.google.android.flexbox.FlexboxLayoutManager");
            return hasFlexboxLayoutManager = true;
        } catch (ClassNotFoundException e) {
            LogUtils.error("没有FlexboxLayoutManager:", e);
            return hasFlexboxLayoutManager = false;
        }
    }

    /**
     * 是否可以打印额外日志
     */
    public void setLoggable(boolean loggable) {
        this.loggable = loggable;
    }
}
