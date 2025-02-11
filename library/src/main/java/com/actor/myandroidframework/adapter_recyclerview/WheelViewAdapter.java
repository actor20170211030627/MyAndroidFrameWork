package com.actor.myandroidframework.adapter_recyclerview;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.Collection;
import java.util.List;

/**
 * description: 可以滚动选择的Adapter, 仿 WheelView <br />
 * {@link 注意:}
 * <ol>
 *     <li>∵item高度可能不一致, ∴需要自己根据item的高度在xml中设置RecyclerView的高度!</li>
 *     <li>请继承本类, 然后重写 {@link #convert(BaseViewHolder, Object)} 方法和 {@link #convert(BaseViewHolder, Object, List)}方法[可选], 可参考: <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/adapter/MyWheelViewTestAdapter.java" target="_blank">MyWheelViewTestAdapter.java</li>
 *     <li>先 {@link RecyclerView#setAdapter(RecyclerView.Adapter) RecyclerView.setAdapter(WheelViewAdapter)} 之后, 再调用 {@link #setList(Collection)} 方法.</li>
 *     <li>关于item封面, item之间的横线等问题, 可参考 <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/res/layout/activity_wheel_view_test.xml" target="_blank">activity_wheel_view_test.xml</a> 自己画.</li>
 * </ol>
 *
 * @author : ldf
 * date       : 2025/2/8 on 17
 * @version 1.0
 */
public abstract class WheelViewAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    //显示几个item
    protected int showItemCount = 3;
    //当前选中item的真实position
    protected int selectedPos = -1;
    //是否无限滚动
    protected boolean isInfinityLoop = false;
    //是否可打印日志
    protected boolean loggable = false;
    //item点击是否自动滚动到中间
    protected boolean itemClickScroll2Center = true;

    /**
     * @param layoutResId item布局
     * @param showItemCount 显示的几个item
     * @param isInfinityLoop 是否无限循环
     * @param itemClickScroll2Center item点击是否自动滚动到中间
     */
    public WheelViewAdapter(@LayoutRes int layoutResId, int showItemCount, boolean isInfinityLoop, boolean itemClickScroll2Center) {
        super(layoutResId);
        this.showItemCount = showItemCount;
        this.isInfinityLoop = isInfinityLoop;
        this.itemClickScroll2Center = itemClickScroll2Center;
        //if 点击item滚动到最中间
        if (itemClickScroll2Center) {
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    if (position == selectedPos) return;
                    if (position == 0 || position == getItemCount() - 1) return;
                    RecyclerView recyclerView = getRecyclerViewOrNull();
                    if (recyclerView == null) return;
                    if (loggable) {
                        LogUtils.errorFormat("clickPosition=%d, showItemCount=%d", position, showItemCount);
                    }
                    if (position > selectedPos) {
                        recyclerView.smoothScrollToPosition(Math.min(position + showItemCount / 2, getItemCount() - 1));
                    } else {
                        recyclerView.smoothScrollToPosition(Math.max(position - showItemCount / 2, 0));
                    }
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //必须 LinearLayoutManager 才行哦
        if (!(layoutManager instanceof LinearLayoutManager)) {
            layoutManager = new LinearLayoutManager(recyclerView.getContext());
            recyclerView.setLayoutManager(layoutManager);
        }
        //设置中间停靠
        new LinearSnapHelper().attachToRecyclerView(recyclerView);
        // 添加滑动监听
        LinearLayoutManager lm = (LinearLayoutManager) layoutManager;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //可见item的个数
                int childCount = lm.getChildCount();
                if (loggable) {
                    LogUtils.errorFormat("childCount=%d", childCount);
                }

                //第1个可见item的pos
                int firstVisibleItemPosition = lm.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                if (loggable) {
                    LogUtils.errorFormat("firstVisibleItemPosition=%d, lastVisibleItemPosition=%d", firstVisibleItemPosition, lastVisibleItemPosition);
                }

                //第1个完全可见item的pos
                int firstCompletelyVisibleItemPosition = lm.findFirstCompletelyVisibleItemPosition();

                int selectedPos2 = 0;

                //if RecyclerView 的高度比 showItemCount 个item的个数高一些, 当滑动到最底部的时候, childCount - showItemCount = 1
                if (firstVisibleItemPosition != firstCompletelyVisibleItemPosition) {
                    //中间item的pos
                    selectedPos2 = firstVisibleItemPosition + childCount / 2;
                    if (loggable) {
                        LogUtils.errorFormat("childCount=%d, selectedPos=%d", childCount, selectedPos2);
                    }
                } else {
                    //可见item的Diff
                    int visibleItemDiff = lastVisibleItemPosition - firstVisibleItemPosition;
                    selectedPos2 = firstVisibleItemPosition + visibleItemDiff / 2;
                    if (loggable) {
                        LogUtils.errorFormat("visibleItemCount=%d, selectedPos=%d", visibleItemDiff + 1, selectedPos2);
                    }
                }



                if (loggable) {
                    int lastCompletelyVisibleItemPosition = lm.findLastCompletelyVisibleItemPosition();
                    LogUtils.errorFormat("firstCompletelyVisibleItemPosition=%d, lastCompletelyVisibleItemPosition=%d", firstCompletelyVisibleItemPosition, lastCompletelyVisibleItemPosition);

                    //完全可见item的Diff
                    int completelyVisibleItemDiff = lastCompletelyVisibleItemPosition - firstCompletelyVisibleItemPosition;
                    //中间item的pos
                    int selectedPos3 = firstCompletelyVisibleItemPosition + completelyVisibleItemDiff / 2;
                    LogUtils.errorFormat("completelyVisibleItemCount=%d, selectedPos=%d", completelyVisibleItemDiff + 1, selectedPos3);
                }


                if (selectedPos2 != selectedPos) {
                    int oldPos = selectedPos;
                    selectedPos = selectedPos2;
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
        //if不是无限循环, 就添加几个空白item
        int addCount = showItemCount / 2;
        if (isInfinityLoop) {
            selectedPos = 0;
            RecyclerView recyclerView = getRecyclerViewOrNull();
            if (recyclerView != null) {
                selectedPos = getDefItemCount() / 2;
                while (selectedPos % list.size() != 0) {
                    selectedPos --;
                }
                recyclerView.scrollToPosition(selectedPos - addCount);
            }
        } else {
            for (int i = 0; i < addCount; i++) {
                addData(0, (T) null);
                addData((T) null);
            }
            selectedPos = addCount;
        }
    }

    /**
     * 是否打印日志
     */
    public void setLoggable(boolean loggable) {
        this.loggable = loggable;
    }

    @Nullable
    public T getSelectedItem() {
        return getItemOrNull(selectedPos);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 重写以下1/2个方法
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected abstract void convert(@NonNull BaseViewHolder holder, @Nullable T t);

    /**
     * RecyclerView滑动的时候, item选中位置 {@link #selectedPos} 会改变. if 你的item有选中&未选中状态, 请重写此方法做局部更新!
     * @param holder
     * @param item
     * @param payloads payloads.get(0) = true/false, 这个item是否是选中状态
     */
    @Override
    protected void convert(@NonNull BaseViewHolder holder, @Nullable T item, @NonNull List<?> payloads) {
        super.convert(holder, item, payloads);
        if (loggable) {
            LogUtils.errorFormat("holder=%s, item=%s, payloads=%s", holder, item, GsonUtils.toJson(payloads));
        }
    }
}
