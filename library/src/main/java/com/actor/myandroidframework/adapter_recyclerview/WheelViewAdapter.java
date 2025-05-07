package com.actor.myandroidframework.adapter_recyclerview;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.dingmouren.layoutmanagergroup.picker.PickerLayoutManager;

import java.util.Collection;
import java.util.List;

/**
 * description: 可以滚动选择的Adapter, 仿 WheelView <br />
 * {@link 注意:}
 * <ol>
 *     <li>添加依赖: <br />
 *         //https://github.com/DingMouRen/LayoutManagerGroup RecyclerView的Item自动居中效果 <br />
 *         implementation 'com.github.DingMouRen:LayoutManagerGroup:1e6f4f96eb'
 *     </li>
 *     <li>
 *         RecyclerView布局可参考 <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/res/layout/activity_wheel_view_test.xml" target="_blank">activity_wheel_view_test.xml</a> <br />
 *         垂直滚动item布局可参考 <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/res/layout/item_wheel_view_vertical.xml" target="_blank">item_wheel_view_vertical.xml</a> <br />
 *         水平滚动item布局可参考 <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/res/layout/item_wheel_view_horizontal.xml" target="_blank">item_wheel_view_horizontal.xml</a>
 *     </li>
 *     <li>继承本类, 然后重写 {@link #convert(BaseViewHolder, Object)} 方法和 {@link #convert(BaseViewHolder, Object, List)}方法[可选], 可参考: <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/adapter/MyWheelViewTestAdapter.java" target="_blank">MyWheelViewTestAdapter.java</li>
 *     <li>先 {@link RecyclerView#setAdapter(RecyclerView.Adapter) recyclerView.setAdapter(WheelViewAdapter)} 之后, 再调用 {@link WheelViewAdapter#setList(Collection) wheelViewAdapter.setList(Collection)} 方法, 可参考 <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/WheelViewTestActivity.java" target="_blank">WheelViewTestActivity.java</a></li>
 *     <li>关于item封面, item之间的横线等问题, 可参考 <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/res/layout/activity_wheel_view_test.xml" target="_blank">activity_wheel_view_test.xml</a> 自己画.</li>
 * </ol>
 *
 * @author : ldf
 * date       : 2025/2/8 on 17
 * @version 1.0
 */
public abstract class WheelViewAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    //当前选中item的真实position
    protected int selectedPos = -1;
    //是否无限滚动
    protected boolean isInfinityLoop = false;
    //是否可打印日志
    protected boolean loggable = false;
    //item点击是否自动滚动到中间
    protected boolean              itemClickScroll2Center = true;
    protected PickerLayoutManager2 pickerLayoutManager2;

    /**
     * @param layoutResId item布局
     * @param isInfinityLoop 是否无限循环
     * @param itemClickScroll2Center item点击是否自动滚动到中间
     */
    public WheelViewAdapter(@NonNull PickerLayoutManager2 pickerLayoutManager2, @LayoutRes int layoutResId,
                            boolean isInfinityLoop, boolean itemClickScroll2Center) {
        super(layoutResId);
        this.pickerLayoutManager2 = pickerLayoutManager2;
        this.isInfinityLoop = isInfinityLoop;
        this.itemClickScroll2Center = itemClickScroll2Center;
        //if 点击item滚动到最中间
        if (itemClickScroll2Center) {
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    if (position == selectedPos) return;
                    if (position < 0 || position >= getDefItemCount()) return;
                    RecyclerView recyclerView = getRecyclerViewOrNull();
                    if (recyclerView == null) return;
                    recyclerView.smoothScrollToPosition(position);
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(pickerLayoutManager2);
        pickerLayoutManager2.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                if (loggable) {
                    LogUtils.errorFormat("view=%s, position=%d, selectedPos=%d", view, position, selectedPos);
                }
                if (position != selectedPos) {
                    int oldPos = selectedPos;
                    selectedPos = position;
                    //快速滑动的时候更新item可能报错:
                    //java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling androidx.recyclerview.widget.RecyclerView...
                    try {
                        notifyItemChanged(oldPos, false);
                        notifyItemChanged(selectedPos, true);
                    } catch (IllegalStateException e) {
                        if (loggable) e.printStackTrace();
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public T getItem(int position) {
        int realSize = getData().size();
        if (realSize == 0) return null;
        return super.getItem(position % realSize);
    }

    @Nullable
    @Override
    public T getItemOrNull(int position) {
        int realSize = getData().size();
        if (realSize == 0) return null;
        //                             不能 % 0
        return super.getItemOrNull(position % realSize);
    }

    /**
     * 无限循环滑动
     */
    @Override
    protected int getDefItemCount() {
        return isInfinityLoop ? Integer.MAX_VALUE : super.getDefItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        //会返回{@link #LOAD_MORE_VIEW}, 导致空指针异常
//        return super.getItemViewType(position);
        return super.getDefItemViewType(position);
    }

    /**
     * 设置数据 <br />
     * {@link 注意:} 需要先 {@link RecyclerView#setAdapter(RecyclerView.Adapter)} 之后, 才调用这个方法,
     * 否则无限滚动模式的话会找不到RecyclerView, 见下方代码.
     * @param list
     */
    @Override
    public void setList(@Nullable Collection<? extends T> list) {
        super.setList(list);
        if (list == null || list.isEmpty()) {
            selectedPos = -1;
            return;
        }
        //先计算位置, 然后再滚动位置
        if (isInfinityLoop) {
            selectedPos = getDefItemCount() / 2;
            int size = list.size();
            while (selectedPos % size != 0) {
                selectedPos --;
            }
        } else {
            selectedPos = 0;
        }
        setCurrentPosition(0, false);
    }

    /**
     * 是否打印日志
     */
    public void setLoggable(boolean loggable) {
        this.loggable = loggable;
    }

    /**
     * 设置当前的position
     * @param currentPosition 当前的position: [0, data.size - 1]
     * @param isSmoothScroll 是否平滑滑动
     */
    public void setCurrentPosition(int currentPosition, boolean isSmoothScroll) {
        if (loggable) {
            LogUtils.errorFormat("currentPosition=%d, isSmoothScroll=%b", currentPosition, isSmoothScroll);
        }
        int size = getData().size();
        if (currentPosition < 0 || currentPosition > size - 1) return;
        if (isInfinityLoop) {
            int selectedPos = 0;
            RecyclerView recyclerView = getRecyclerViewOrNull();
            if (recyclerView != null) {
                selectedPos = getDefItemCount() / 2;
                while (selectedPos % size != currentPosition) {
                    selectedPos --;
                }
                if (loggable) {
                    LogUtils.errorFormat("selectedPos=%d", selectedPos);
                }
                if (isSmoothScroll) {
                    recyclerView.smoothScrollToPosition(selectedPos);
                } else {
                    recyclerView.scrollToPosition(selectedPos);
                }
            }
        } else {
            RecyclerView recyclerView = getRecyclerViewOrNull();
            if (recyclerView != null) {
                if (isSmoothScroll) {
                    recyclerView.smoothScrollToPosition(currentPosition);
                } else {
                    recyclerView.scrollToPosition(currentPosition);
                }
            }
        }
    }

    /**
     * 获取当前选中item
     */
    @Nullable
    public T getSelectedItem() {
        return getItemOrNull(selectedPos);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 重写以下1/2个方法
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected abstract void convert(@NonNull BaseViewHolder holder, T item);

    /**
     * RecyclerView滑动的时候, item选中位置 {@link #selectedPos} 会改变. if 你的item有选中&未选中状态, 请重写此方法做局部更新!
     * @param holder
     * @param item
     * @param payloads payloads.get(0) = true/false, 这个item是否是选中状态
     */
    @Override
    protected void convert(@NonNull BaseViewHolder holder, T item, @NonNull List<?> payloads) {
        super.convert(holder, item, payloads);
        if (loggable) {
            LogUtils.errorFormat("holder=%s, item=%s, payloads=%s", holder, item, GsonUtils.toJson(payloads));
        }
    }
}
