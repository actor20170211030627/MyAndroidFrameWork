package com.actor.myandroidframework.utils;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.LoadMoreModule;

import java.util.List;

/**
 * description: <a href="https://github.com/CymChad/BaseRecyclerViewAdapterHelper">BaseRecyclerViewAdapterHelper</a> 帮助类
 *
 * @author : ldf
 * @date       : 2022/7/6 on 08
 */
public class BRVUtils {

    /**
     * adapter设置空布局
     */
    public static void setEmptyView(@NonNull BaseQuickAdapter adapter) {
        setEmptyView(adapter, R.layout.layout_for_empty);
    }
    public static void setEmptyView(@NonNull BaseQuickAdapter adapter, @LayoutRes int layoutId) {
        RecyclerView recyclerView = adapter.getRecyclerViewOrNull();
        if (recyclerView == null) {
            throw new IllegalStateException("需要先recyclerView.setAdapter(adapter), 才能setEmptyView()!");
        }
        adapter.setEmptyView(layoutId);
    }

    /**
     * 设置上拉加载监听, 示例:
     * <pre>
     * //isRefresh: 是否是下拉刷新
     * private void getList(boolean isRefresh) {
     *     //网络请求:
     *     page = {@link BRVUtils#getPage(BaseQuickAdapter, boolean, int) BRVUtils.getPage(mAdapter, isRefresh, SIZE)};
     *     size = SIZE;
     *     网络请求后回调示例: {
     *         <code>@</code>Override
     *         public void onOk(@NonNull UserBean info, boolean isRefresh) {
     *             swipeRefreshLayout.setRefreshing(false);
     *             List&lt;UserBean.Data> datas = info.data;
     *             //如果是下拉刷新
     *             if (isRefresh) {
     *                 mAdapter.setList(datas);//设置新数据
     *             } else if (datas != null) {
     *                 mAdapter.addData(datas);//增加数据
     *             }
     *             //{@link null 设置加载状态, ⑴ & ⑵ 这2种方式都可以:}
     *             //int total = info.totalCount;                   //⑴.服务器返回了total
     *             //{@link BRVUtils#setLoadMoreState(BaseQuickAdapter, int) BRVUtils.setLoadMoreState(mAdapter, total)};    //⑴
     *             {@link BRVUtils#setLoadMoreState(BaseQuickAdapter, List, int) BRVUtils.setLoadMoreState(mAdapter, datas, SIZE)};//⑵.这种也可以
     *         }
     *
     *         <code>@</code>Override
     *         public void onError(int id, okhttp3.Call call, Exception e) {
     *             super.onError(id, call, e);
     *             swipeRefreshLayout.setRefreshing(false);
     *             //点击"重试"时, 会调用 '上拉加载更多监听' 里的onLoadMoreRequested();回调方法
     *             {@link BRVUtils#setLoadMoreFail(BaseQuickAdapter) BRVUtils.setLoadMoreFail(BaseQuickAdapter)}; //加载失败
     *         }
     *     });
     * }
     *
     * 1.下拉刷新:
     * getList(true);
     *
     * 2.上拉加载:
     * getList(false);
     * </pre>
     *
     * @param adapter      不能为空
     * @param listener     不能为空
     */
    public static void setOnLoadMoreListener(@NonNull BaseQuickAdapter adapter, OnLoadMoreListener listener) {
        if (adapter instanceof LoadMoreModule) {
            //上拉加载更多
            adapter.getLoadMoreModule().setOnLoadMoreListener(listener);
        } else {
            throw new IllegalStateException("BaseQuickAdapter 需要实现 LoadMoreModule 接口, 才能上拉加载更多!");
        }
    }

    /**
     * 获取'下拉刷新/上拉加载'列表page, 如果和项目逻辑不符合, 可重写此方法
     * @param adapter   列表Adapter extends BaseQuickAdapter
     * @param isRefresh 是否是下拉刷新
     * @param size      每次加载多少条
     * @return
     * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *     <tr>
     *         <th>currentSize</th>
     *         <th>size</th>
     *         <th>return</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>20</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>1-19</td>
     *         <td>20</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>20-39</td>
     *         <td>20</td>
     *         <td>2</td>
     *     </tr>
     *     <tr>
     *         <td>40-59</td>
     *         <td>20</td>
     *         <td>3</td>
     *     </tr>
     * </table>
     */
    public static int getPage(@NonNull BaseQuickAdapter adapter, boolean isRefresh, int size) {
        return getPage(adapter.getData(), isRefresh, size);
    }

    /**
     * ∵有些分页请求的数据, 不一定会填充到RecyclerView, 所以不会有adapter参数
     * @param items 所有数据列表
     * @param isRefresh 是否是下拉刷新
     * @param size      每次加载多少条
     */
    public static int getPage(@NonNull List items, boolean isRefresh, int size) {
        return getPage(items.size(), isRefresh, size);
    }

    /**
     * ∵要填充到RecyclerView的数据, 不一定来着同一个接口, 所以直接用Adapter的List不正确
     * @param itemCount item所有数量
     * @param isRefresh 是否是下拉刷新
     * @param size      每次加载多少条
     */
    public static int getPage(int itemCount, boolean isRefresh, int size) {
        if (isRefresh) return 1;
        if (itemCount < size) return 1;
        return itemCount / size + 1;
    }

    /**
     * 设置加载状态: 已经没有数据了 or 加载完成
     * @param list 本次从服务器返回的分页数据(不是全部数据, 是本次请求的数据!)
     * @param size 每次加载多少条
     */
    public static void setLoadMoreStateBySize(@NonNull BaseQuickAdapter adapter, @Nullable List<?> list, int size) {
        //"list = null"     or     "list为空"     or     "list < size"(比如一次获取20条, 但是只返回15条, 说明服务器没有更多数据了)
        setLoadMoreStateBySize(adapter, list == null ? 0 : list.size(), size);
    }

    /**
     * 设置加载状态: 已经没有数据了 or 加载完成
     * @param listSize 本次从服务器返回的分页数据条数(不是全部数据, 是本次请求的数据!) <br />
     *                 ∵要填充到RecyclerView的数据, 不一定来着同一个接口, 所以直接用Adapter的List不正确
     * @param size 每次加载多少条
     */
    public static void setLoadMoreStateBySize(@NonNull BaseQuickAdapter adapter, int listSize, int size) {
        //listSize < size (比如一次获取20条, 但是只返回15条, 说明服务器没有更多数据了)
        boolean isLoadMoreEnd = listSize < size;
        if (isLoadMoreEnd) {
            adapter.getLoadMoreModule().loadMoreEnd();//已经没有数据了
        } else {
            adapter.getLoadMoreModule().loadMoreComplete();//加载完成
        }
    }

    /**
     * 设置加载状态: 已经没有数据了 or 加载完成
     * @param total   服务器返回的数据总数(如果后端返回了total的话...)
     */
    public static void setLoadMoreStateByTotal(@NonNull BaseQuickAdapter adapter, int total) {
        setLoadMoreStateByTotal(adapter, adapter.getData().size(), total);
    }

    /**
     * 设置加载状态: 已经没有数据了 or 加载完成 <br />
     * ∵要填充到RecyclerView的数据, 不一定来着同一个接口, 所以直接用Adapter的List不正确
     * @param currentItemCount 需要判断是否还有更多数据的, 和↓下方同一接口返回的List的当前总数量
     * @param total            服务器返回的数据总数(如果后端返回了total的话...)
     */
    public static void setLoadMoreStateByTotal(@NonNull BaseQuickAdapter adapter, int currentItemCount, int total) {
        if (currentItemCount < total) {
            adapter.getLoadMoreModule().loadMoreComplete();//加载完成
        } else adapter.getLoadMoreModule().loadMoreEnd();//已经没有数据了
    }

    /**
     * 加载失败, 点击"重试"时, 会调用 '上拉加载更多监听' 里的onLoadMoreRequested();回调方法
     */
    public static void setLoadMoreFail(@NonNull BaseQuickAdapter adapter) {
        adapter.getLoadMoreModule().loadMoreFail();
    }
}
