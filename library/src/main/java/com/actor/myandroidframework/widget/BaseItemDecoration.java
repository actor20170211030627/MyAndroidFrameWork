package com.actor.myandroidframework.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Description:设置{@link RecyclerView}间距. Decoration:装饰物 <br />
 * Author     : ldf <br />
 * Date       : 2019/7/21 on 16:31 <br />
 *
 *  <br />
 * 设置{@link RecyclerView}的{@link LinearLayoutManager}&{@link GridLayoutManager}&{@link StaggeredGridLayoutManager}的水平/垂直间距, 示例用法: <br />
 * int dp20 = UiUtils.dp2px(20); <br />
 * recyclerView.{@link RecyclerView#addItemDecoration(RecyclerView.ItemDecoration) addItemDecoration(new BaseItemDecoration(dp20, dp20))};
 * @version 1.0
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
        if (layoutManager instanceof GridLayoutManager) {//网格布局,不能先判断LinearLayoutManager
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int orientation = gridLayoutManager.getOrientation();
            int spanCount = gridLayoutManager.getSpanCount();//一共多少行/列
            if (orientation == OrientationHelper.HORIZONTAL) {//水平方向(Grid左右滑动)
                int row = position % spanCount; // item在第几行,从0开始
                float min = verticalSpacing / spanCount;//最小基数(有可能除出来=0, 所以这儿用float)
                outRect.left = position < spanCount ? 0 : (int) horizontalSpacing;//第一列的都没有left
                outRect.top = (int) (row * min);
                outRect.bottom = (int) (min * (spanCount - row - 1));
            } else if (orientation == OrientationHelper.VERTICAL) {//垂直方向(Grid上下滑动,默认)
                int column = position % spanCount; // item在第几列,从0开始
                float min = horizontalSpacing / spanCount;//最小基数
//                outRect.left = column * horizontalSpacing / spanCount; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f / spanCount) * spacing)
                outRect.left = (int) (column * min);
                outRect.right = (int) (min * (spanCount - column - 1));
                outRect.top = position < spanCount ? 0 : (int) verticalSpacing;//第一行的都没有top
            }
        } else if (layoutManager instanceof LinearLayoutManager) {//线性布局
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();
            if (orientation == OrientationHelper.HORIZONTAL) {//水平方向(List水平滑动)
                outRect.left = position == 0 ? 0 : (int) horizontalSpacing;
            } else if (orientation == OrientationHelper.VERTICAL) {//垂直方向(List上下滑动,默认)
                outRect.top = position == 0 ? 0 : (int) verticalSpacing;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {//瀑布流
            StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) layoutManager;
            int orientation = sglm.getOrientation();
            int spanCount = sglm.getSpanCount();
            if (orientation == OrientationHelper.HORIZONTAL) {//水平方向(Grid左右滑动)
                int row = position % spanCount; // item在第几行,从0开始
                float min = verticalSpacing / spanCount;//最小基数
                outRect.left = position < spanCount ? 0 : (int) horizontalSpacing;//第一列的都没有left
                outRect.top = (int) (row * min);
                outRect.bottom = (int) (min * (spanCount - row - 1));
            } else if (orientation == OrientationHelper.VERTICAL) {//垂直方向(Grid上下滑动,默认)
                int column = position % spanCount; // item在第几列,从0开始
                float min = horizontalSpacing / spanCount;//最小基数
                outRect.left = (int) (column * min);
                outRect.right = (int) (min * (spanCount - column - 1));
                outRect.top = position < spanCount ? 0 : (int) verticalSpacing;//第一行的都没有top
//                outRect.left = column * horizontalSpacing / spanCount; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            }
        }
//            if (includeEdge) {
//                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
//                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
//
//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
//                outRect.bottom = spacing; // item bottom
//            } else {
//                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
//                if (position >= spanCount) {
//                    outRect.top = spacing; // item top
//                }
//            }
    }
}
