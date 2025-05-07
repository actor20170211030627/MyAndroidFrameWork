package com.actor.myandroidframework.adapter_recyclerview;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.dingmouren.layoutmanagergroup.picker.PickerLayoutManager;

/**
 * description: WheelView 效果的LayoutManager <br />
 * 修复bug: https://github.com/DingMouRen/LayoutManagerGroup/issues/16
 *
 * @author : ldf
 * date       : 2025/5/6 on 15
 * @version 1.0
 */
public class PickerLayoutManager2 extends PickerLayoutManager {

    protected int mItemCount2 = -1;

    /**
     * @param orientation 滚动方向: {@link PickerLayoutManager#VERTICAL}, {@link PickerLayoutManager#HORIZONTAL}
     * @param reverseLayout 是否反向布局
     */
    public PickerLayoutManager2(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    /**
     * @param orientation 滚动方向: {@link PickerLayoutManager#VERTICAL}, {@link PickerLayoutManager#HORIZONTAL}
     * @param reverseLayout 是否反向布局
     * @param itemCount 显示几个item, 例: 3
     * @param scale 非中间item, 缩放比例, 例: [0.4f ~ 1f]
     * @param isAlpha 非中间item是否透明
     */
    public PickerLayoutManager2(Context context, RecyclerView recyclerView, int orientation, boolean reverseLayout, int itemCount, float scale, boolean isAlpha) {
        super(context, recyclerView, orientation, reverseLayout, itemCount, scale, isAlpha);
        this.mItemCount2 = itemCount;
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        if (this.mItemCount2 != 0) {
            return false;
        }
        return super.isAutoMeasureEnabled();
    }
}
