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
 * description: BaseRecyclerViewAdapterHelper 帮助类
 * company    : https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 *
 * @author : ldf
 * date       : 2022/7/6 on 08
 * @version 1.0
 */
public class BRVUtils {

    /**
     * adapter设置空布局
     *
     * @param adapter      不能为空
     */
    public static void setEmptyView(BaseQuickAdapter adapter) {
        setEmptyView(adapter, R.layout.layout_for_empty);
    }
    public static void setEmptyView(BaseQuickAdapter adapter, @LayoutRes int layoutId) {
        RecyclerView recyclerView = adapter.getRecyclerViewOrNull();
        if (recyclerView == null) {
            throw new IllegalStateException("需要先recyclerView.setAdapter(adapter), 才能setEmptyView()!");
        }
        adapter.setEmptyView(layoutId);
    }

    /**
     * 设置上拉加载更多 & 空布局, 示例:
     * <pre> {@code
     * //写在常量类里面, 比如写在 Global.java 里面.
     * public static final int SIZE = 10;
     * public static final String page = "page";
     * public static final String size = "size";
     *
     * //isRefresh: 是否是下拉刷新
     * private void getList(boolean isRefresh) {
     *     params.clear();
     *     params.put(Global.page, getPage(isRefresh, mAdapter, Global.SIZE));
     *     params.put(Global.size, Global.SIZE);
     *     MyOkHttpUtils.get(url, params, new BaseCallback<UserBean>(this, isRefresh) {
     *         @Override
     *         public void onOk(@NonNull UserBean info, int id, boolean isRefresh) {
     *             swipeRefreshLayout.setRefreshing(false);
     *             List<UserBean.Data> datas = info.data;
     *             //如果是下拉刷新
     *             if (isRefresh) {
     *                 mAdapter.setNewData(datas);//设置新数据
     *             } else if (datas != null) {
     *                 mAdapter.addData(datas);//增加数据
     *             }
     *             //int total = info.totalCount;                 //⑴. total这种方式也可以
     *             //setLoadMoreState(mAdapter, total);           //⑴
     *             setLoadMoreState(mAdapter, datas, Global.SIZE);//⑵. 这种也可以
     *         }
     *
     *         @Override
     *         public void onError(int id, okhttp3.Call call, Exception e) {
     *             super.onError(id, call, e);
     *             swipeRefreshLayout.setRefreshing(false);
     *             //点击"重试"时, 会调用 '上拉加载更多监听' 里的onLoadMoreRequested();回调方法
     *             mAdapter.getLoadMoreModule().loadMoreFail();//加载失败
     *         }
     *     });
     * }
     *
     * 1.下拉刷新:
     * getList(true);
     *
     * 2.上拉加载:
     * getList(false);
     * } </pre>
     *
     * @param adapter      不能为空
     * @param listener     不能为空
     */
    public static void setLoadMore$Empty(BaseQuickAdapter adapter, OnLoadMoreListener listener) {
        setLoadMore$Empty(R.layout.layout_for_empty, adapter, listener);
    }

    public static void setLoadMore$Empty(@LayoutRes int emptyLayoutRes, BaseQuickAdapter adapter, OnLoadMoreListener listener) {
        if (adapter instanceof LoadMoreModule) {
            //上拉加载更多
            adapter.getLoadMoreModule().setOnLoadMoreListener(listener);
            //空布局
            setEmptyView(adapter, emptyLayoutRes);
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
        if (isRefresh) return 1;
        int currentSize = adapter.getData().size();//目前列表数据条数
        if (currentSize < size) return 1;
        return currentSize / size + 1;
    }

    /**
     * 设置加载状态
     * @param list 本次从服务器返回的分页数据
     * @param size 每次加载多少条
     */
    public static void setLoadMoreState(@NonNull BaseQuickAdapter adapter, @Nullable List<?> list, int size) {
        //"list = null"     or     "list为空"     or     "list < size"(比如一次获取20条, 但是只返回15条, 说明服务器没有更多数据了)
        boolean isLoadMoreEnd = list == null || list.size() < size;
        if (isLoadMoreEnd) {
            adapter.getLoadMoreModule().loadMoreEnd();//已经没有数据了
        } else {
            adapter.getLoadMoreModule().loadMoreComplete();//加载完成
        }
    }

    /**
     * 设置加载状态
     * @param total   服务器返回的数据总数(如果后端返回了total的话...)
     */
    public static void setLoadMoreState(@NonNull BaseQuickAdapter adapter, int total) {
        if (adapter.getData().size() < total) {
            adapter.getLoadMoreModule().loadMoreComplete();//加载完成
        } else adapter.getLoadMoreModule().loadMoreEnd();//已经没有数据了
    }
}
