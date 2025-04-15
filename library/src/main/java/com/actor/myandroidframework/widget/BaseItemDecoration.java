package com.actor.myandroidframework.widget;

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
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int position = parent.getChildAdapterPosition(view); // item position

        //网格布局,不能先判断LinearLayoutManager
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager glm = (GridLayoutManager) layoutManager;
            int orientation = glm.getOrientation();
            int spanCount = glm.getSpanCount();     //一共多少行/列
            int spanSize = glm.getSpanSizeLookup().getSpanSize(position);
            if (spanSize > 0) {
                spanCount = spanCount / spanSize;   //每行item数量
            }
            //水平方向(Grid左右滑动)
            if (orientation == OrientationHelper.HORIZONTAL) {
                int row = position % spanCount; // item在第几行,从0开始
                float min = verticalSpacing / spanCount;//最小基数(有可能除出来=0, 所以这儿用float)
                outRect.left = position < spanCount ? 0 : (int) horizontalSpacing;//第一列的都没有left
                outRect.top = (int) (row * min);
                outRect.bottom = (int) (min * (spanCount - row - 1));
                return;
            }
            //垂直方向(Grid上下滑动,默认)
            if (orientation == OrientationHelper.VERTICAL) {
                int column = position % spanCount; // item在第几列,从0开始
                float min = horizontalSpacing / spanCount;//最小基数
//                outRect.left = column * horizontalSpacing / spanCount; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f / spanCount) * spacing)
                outRect.left = (int) (column * min);
                outRect.right = (int) (min * (spanCount - column - 1));
                outRect.top = position < spanCount ? 0 : (int) verticalSpacing;//第一行的都没有top
            }
            return;
        }

        //线性布局
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager llm = (LinearLayoutManager) layoutManager;
            int orientation = llm.getOrientation();
            if (orientation == OrientationHelper.HORIZONTAL) {//水平方向(List水平滑动)
                outRect.left = position == 0 ? 0 : (int) horizontalSpacing;
            } else if (orientation == OrientationHelper.VERTICAL) {//垂直方向(List上下滑动,默认)
                outRect.top = position == 0 ? 0 : (int) verticalSpacing;
            }
            return;
        }

        //瀑布流
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) layoutManager;
            int orientation = sglm.getOrientation();
            int spanCount = sglm.getSpanCount();
            //水平方向(Grid左右滑动)
            if (orientation == OrientationHelper.HORIZONTAL) {
                int row = position % spanCount; // item在第几行,从0开始
                float min = verticalSpacing / spanCount;//最小基数
                outRect.left = position < spanCount ? 0 : (int) horizontalSpacing;//第一列的都没有left
                outRect.top = (int) (row * min);
                outRect.bottom = (int) (min * (spanCount - row - 1));
                return;
            }
            //垂直方向(Grid上下滑动,默认)
            if (orientation == OrientationHelper.VERTICAL) {
                int column = position % spanCount; // item在第几列,从0开始
                float min = horizontalSpacing / spanCount;//最小基数
                outRect.left = (int) (column * min);
                outRect.right = (int) (min * (spanCount - column - 1));
                outRect.top = position < spanCount ? 0 : (int) verticalSpacing;//第一行的都没有top
//                outRect.left = column * horizontalSpacing / spanCount; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            }
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
            FlexboxLayoutManager fblm = (FlexboxLayoutManager) layoutManager;
            int orientation = fblm.getFlexDirection();

//            int height = fblm.getHeight();  //每1行高度??
//            int topOffset = fblm.getTopDecorationHeight(view);  //当前Item高度Offset = 0
//            int topOffset = fblm.getDecoratedMeasuredHeight(view);//180
//            int currentRow = height <= 0 ? 0 : topOffset / height;  //当前Item第几行, 不对
//            LogUtils.errorFormat("height=%d, position=%d, topOffset=%d, currentRow=%d", height, position, topOffset, currentRow);

            //position在第几行
//            List<FlexLine> flexLines = fblm.getFlexLines();
            List<FlexLine> flexLinesInternal = fblm.getFlexLinesInternal();
//            int maxLine = fblm.getMaxLine(); // -1
//            LogUtils.errorFormat("flexLines=%d, flexLinesInternal=%d, maxLine=%d", flexLines.size(), flexLinesInternal.size(), maxLine);
            int size = flexLinesInternal.size();

            if (orientation == FlexDirection.ROW || orientation == FlexDirection.ROW_REVERSE) {//水平方向
                if (size > 0) {
                    //上一行的信息(不是position所在行)
//                    FlexLine flexLine = flexLinesInternal.get(size - 1);
                    //                                                    上1行第0个元素的position    上1行有多少个元素
//                    LogUtils.errorFormat("FirstIndex=%d, ItemCount=%d", flexLine.getFirstIndex(), flexLine.getItemCount());

                    //int mLastIndex = flexLine.mLastIndex; //访问不到, 无语
                    int mTotalCount = 0;
                    for (int i = 0; i < size; i++) {
                        mTotalCount += flexLinesInternal.get(i).getItemCount();
                    }
                    //判断是否是每行第1个Item
                    outRect.left = mTotalCount == position ? 0 : (int) horizontalSpacing;
                } else {
                    //第0行
                    outRect.left = position == 0 ? 0 : (int) horizontalSpacing;
                }
                outRect.top = size == 0 ? 0 : (int) verticalSpacing;
            } else if (orientation == FlexDirection.COLUMN || orientation == FlexDirection.COLUMN_REVERSE) {//垂直方向
                outRect.left = size == 0 ? 0 : (int) horizontalSpacing;
                if (size > 0) {
                    int mTotalCount = 0;
                    for (int i = 0; i < size; i++) {
                        mTotalCount += flexLinesInternal.get(i).getItemCount();
                    }
                    //判断是否是每列第1个Item
                    outRect.top = mTotalCount == position ? 0 : (int) verticalSpacing;
                } else {
                    //第0列
                    outRect.top = position == 0 ? 0 : (int) verticalSpacing;
                }
            }
            return;
        }

        LogUtils.errorFormat("%s 还未适配间隙的设定!", layoutManager);
    }

    protected boolean hasFlexboxLayoutManager() {
        try {
            Class.forName("com.google.android.flexbox.FlexboxLayoutManager");
            return true;
        } catch (ClassNotFoundException e) {
            LogUtils.error("没有FlexboxLayoutManager:", e);
            return false;
        }
    }
}
