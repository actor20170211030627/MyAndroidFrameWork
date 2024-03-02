package com.actor.myandroidframework.fragment;import android.content.Context;import android.content.Intent;import android.os.Bundle;import android.util.SparseArray;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import androidx.annotation.CallSuper;import androidx.annotation.LayoutRes;import androidx.annotation.NonNull;import androidx.annotation.Nullable;import androidx.fragment.app.Fragment;import androidx.fragment.app.FragmentActivity;import com.actor.myandroidframework.bean.OnActivityCallback;import com.actor.myandroidframework.dialog.LoadingDialog;import com.actor.myandroidframework.dialog.ShowNetWorkLoadingDialogable;import com.actor.myandroidframework.utils.BRVUtils;import com.actor.myandroidframework.utils.LogUtils;import com.actor.myandroidframework.utils.TextUtils2;import com.blankj.utilcode.util.ActivityUtils;import com.chad.library.adapter.base.BaseQuickAdapter;import com.chad.library.adapter.base.listener.OnLoadMoreListener;import java.util.List;/** * Description: Fragment基类 <br /> *              onActivityCreated : 这个Fragment所依附的Activity对象被创建成功之后，初始化数据 <br /> *              onViewCreated : 这个Fragment所包装的View对象创建完成之后会进行的回调 <br /> * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓ * Author     : ldf * Date       : 2017/5/27 on 18:22. * @version 1.0 */public abstract class ActorBaseFragment extends Fragment implements ShowNetWorkLoadingDialogable {    protected FragmentActivity  mActivity;    protected Fragment          mFragment;    /** Activity 回调集合 */    @Nullable protected SparseArray<OnActivityCallback> mActivityCallbacks;    //请求码                                         9999防止和Activity的巧合    private   int requestCodeCounter4BaseFragment = 9999;    //是否可见    private boolean             isVisibleToUser;    //onViewCreated已经执行完毕    private boolean             isActivityCreated;    //是否'第一次'可见    private boolean isFirstVisibleToUser = true;    //使用newInstance()的方式返回Fragment对象//    public static ActorBaseFragment newInstance() {//        ActorBaseFragment fragment = new ActorBaseFragment();//        Bundle args = new Bundle();//        args.putString(ARG_PARAM1, param1);//        args.putString(ARG_PARAM2, param2);//        fragment.setArguments(args);//        return fragment;//    }    /**     * @see #onAttach(Context) 之前调用, 当Fragment可见/不可见的时候     * 使用ViewPager + Fragment, 当ViewPager切换Fragment时会回调这个方法.     * 如果isVisibleToUser=false, "不要使用控件 & 操作UI界面"     */    @Deprecated    @Override    public void setUserVisibleHint(boolean isVisibleToUser) {        super.setUserVisibleHint(isVisibleToUser);//        LogUtils.errorFormat("%s: isVisibleToUser = %b", getClass().getName(), isVisibleToUser);        this.isVisibleToUser = isVisibleToUser;        if (isActivityCreated) {            //                                                 if可视, 才是第1次可视            fragmentVisibleAndActivityCreated(isVisibleToUser, isVisibleToUser && isFirstVisibleToUser);            if (isVisibleToUser) isFirstVisibleToUser = false;        }    }    /**     * fragment可见 & Activity已创建     * @param isVisibleToUser 本Fragment是否可见     * @param isFirstVisibleToUser 本Fragment是否是'第1次'可见     */    protected void fragmentVisibleAndActivityCreated(boolean isVisibleToUser, boolean isFirstVisibleToUser) {//        LogUtils.errorFormat("%s: isVisibleToUser = %b, isFirstVisibleToUser=%b", getClass().getName(), isVisibleToUser, isFirstVisibleToUser);    }    /**     * @see #onCreate(Bundle) 之前调用, 当Fragment依附到Activity上     */    @Override    public void onAttach(@NonNull Context context) {        super.onAttach(context);        mActivity = getActivity();        mFragment = this;        LogUtils.error(getClass().getName());    }    /**     * 获取这个Fragment所依附的Activity对象     * 初始化 & 系统恢复页面数据 & 旋转屏幕 时, 会回调这个方法     */    @CallSuper    @Override    public void onCreate(@Nullable Bundle savedInstanceState) {        super.onCreate(savedInstanceState);//        Bundle arguments = getArguments();//        if (arguments != null) {//            mParam1 = arguments.getString(ARG_PARAM1);//            mParam2 = arguments.getString(ARG_PARAM2);//        }    }    /**     * @see #onCreate(Bundle) 和 {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} 之间进行调用     * 使用{@link androidx.fragment.app.FragmentManager} 进行add hide show时会回调这个方法     */    @Override    public void onHiddenChanged(boolean hidden) {        super.onHiddenChanged(hidden);        LogUtils.errorFormat("%s: hidden = %b", getClass().getName(), hidden);        isVisibleToUser = !hidden;        if (isActivityCreated) {            fragmentVisibleAndActivityCreated(isVisibleToUser, isVisibleToUser && isFirstVisibleToUser);            if (isVisibleToUser) isFirstVisibleToUser = false;        }    }    //生成这个Fragment所包装的View对象    @Nullable    @Override    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {        return super.onCreateView(inflater, container, savedInstanceState);//        View view = inflater.inflate(R.layout.xxx, container, false);//        return view;    }    //这个Fragment所包装的View对象创建完成之后会进行的回调, 初始化View    @Override    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {        super.onViewCreated(/*baseView*/view, savedInstanceState);    }    /**     * @see #onViewCreated(View, Bundle) 之后      */    @Deprecated    @Override    public void onActivityCreated(@Nullable Bundle savedInstanceState) {        super.onActivityCreated(savedInstanceState);        LogUtils.error("onActivityCreated");        isActivityCreated = true;        fragmentVisibleAndActivityCreated(isVisibleToUser, isVisibleToUser && isFirstVisibleToUser);        if (isVisibleToUser) isFirstVisibleToUser = false;    }    //跳转Activity后返回, 会回调    @Override    public void onResume() {        super.onResume();        LogUtils.error("onResume");    }    /**     * “内存重启”时, onCreated, onViewCreated会有保存的数据     */    @Override    public void onSaveInstanceState(@NonNull Bundle outState) {    }    @Override    public void startActivity(Intent intent) {        super.startActivity(intent);    }    /**     * 共享元素方式跳转     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName     */    public void startActivity(Intent intent, @NonNull View... sharedElements) {        ActivityUtils.startActivity(mActivity, intent, sharedElements);    }    /**     * 请务必使用这种回调的方式, 更方便     */    public void startActivityForResult(Intent intent, OnActivityCallback callback) {        startActivityForResult(intent, null, callback);    }    public void startActivityForResult(Intent intent, @Nullable Bundle options, OnActivityCallback callback) {        if (mActivityCallbacks == null) mActivityCallbacks = new SparseArray<>(1);        mActivityCallbacks.put(++requestCodeCounter4BaseFragment, callback);        startActivityForResult(intent, requestCodeCounter4BaseFragment, options);    }    @Override    @Deprecated    public void startActivityForResult(Intent intent, int requestCode) {        super.startActivityForResult(intent, requestCode);    }    @Override    @Deprecated    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {        super.startActivityForResult(intent, requestCode, options);    }    /**     * 共享元素方式跳转     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName     */    public void startActivityForResult(Intent intent, int requestCode, @NonNull View... sharedElements) {        ActivityUtils.startActivityForResult(this, intent, requestCode, sharedElements);    }    @Deprecated    @Override    public void onActivityResult(int requestCode, int resultCode, Intent data) {        super.onActivityResult(requestCode, resultCode, data);        OnActivityCallback callback;        if (mActivityCallbacks != null && (callback = mActivityCallbacks.get(requestCode)) != null) {            callback.onActivityResult(resultCode, data);            mActivityCallbacks.remove(requestCode);        }    }    ///////////////////////////////////////////////////////////////////////////    // 返回String区    ///////////////////////////////////////////////////////////////////////////    protected String getNoNullString(Object object){        return TextUtils2.getNoNullString(object);    }    protected String getNoNullString(Object object, String defaultStr) {        return TextUtils2.getNoNullString(object, defaultStr);    }    //获取格式化后的String, 例: getStringFormat("我的姓名是%s, 我的年龄是%d", "张三", 23)    protected String getStringFormat(String format, Object... args) {        return TextUtils2.getStringFormat(format, args);    }    protected String getText(Object obj){        return TextUtils2.getText(obj);    }    ///////////////////////////////////////////////////////////////////////////    // 判空区    ///////////////////////////////////////////////////////////////////////////    /**     * 只要有一个为空, 就返回true     */    protected boolean isEmpty(@NonNull Object... objs) {        return !isNoEmpty(objs);    }    /**     * 只要有一个为空, 就返回true     * @param notify 为空时, toast 提示的内容     */    protected boolean isEmpty(Object obj, CharSequence notify) {        return !isNoEmpty(obj, notify);    }    /**     * @param objs 判断对象是否都不为空     * @return 都不为空, 返回true     */    protected boolean isNoEmpty(@NonNull Object... objs) {        return TextUtils2.isNoEmpty(objs);    }    /**     * @param obj 判断对象是否不为空     * @param notify 如果为空 & notify != null, showToast(notify);     * @return 是否不为空     */    protected boolean isNoEmpty(Object obj, CharSequence notify) {        return TextUtils2.isNoEmpty(obj, notify);    }    ///////////////////////////////////////////////////////////////////////////    // 显示加载Diaong    ///////////////////////////////////////////////////////////////////////////    private LoadingDialog netWorkLoadingDialog;    //网络请求次数.(一个页面有可能有很多个请求)    private int requestCountOfShowLoadingDialog = 0;    @Override    @Nullable    public LoadingDialog getNetWorkLoadingDialog() {        if (mActivity == null) return null;        if (netWorkLoadingDialog == null) netWorkLoadingDialog = new LoadingDialog(mActivity);        netWorkLoadingDialog.setCancelAble(true);        return netWorkLoadingDialog;    }    @Override    public int getRequestCount() {        return requestCountOfShowLoadingDialog;    }    @Override    public void setRequestCount(int requestCount) {        requestCountOfShowLoadingDialog  = requestCount;    }    ///////////////////////////////////////////////////////////////////////////    // 下拉刷新 & 上拉加载更多 & 空布局    ///////////////////////////////////////////////////////////////////////////    /**     * adapter设置空布局     * @param adapter 不能为空     */    protected void setEmptyView(BaseQuickAdapter adapter) {        BRVUtils.setEmptyView(adapter);    }    protected void setEmptyView(BaseQuickAdapter adapter, @LayoutRes int layoutId) {        BRVUtils.setEmptyView(adapter, layoutId);    }    /**     * 设置上拉加载更多 & 空布局, 示例:     * <pre> {@code     * //写在常量类里面, 比如写在 Global.java 里面.     * public static final int SIZE = 10;     * public static final String page = "page";     * public static final String size = "size";     *     * //isRefresh: 是否是下拉刷新     * private void getList(boolean isRefresh) {     *     params.clear();     *     params.put(Global.page, getPage(isRefresh, mAdapter, Global.SIZE));     *     params.put(Global.size, Global.SIZE);     *     MyOkHttpUtils.get(url, params, new BaseCallback<UserBean>(this, isRefresh) {     *         @Override     *         public void onOk(@NonNull UserBean info, int id, boolean isRefresh) {     *             swipeRefreshLayout.setRefreshing(false);     *             List<UserBean.Data> datas = info.data;     *             //如果是下拉刷新     *             if (isRefresh) {     *                 mAdapter.setNewData(datas);//设置新数据     *             } else if (datas != null) {     *                 mAdapter.addData(datas);//增加数据     *             }     *             //int total = info.totalCount;                 //⑴. total这种方式也可以     *             //setLoadMoreState(mAdapter, total);           //⑴     *             setLoadMoreState(mAdapter, datas, Global.SIZE);//⑵. 这种也可以     *         }     *     *         @Override     *         public void onError(int id, okhttp3.Call call, Exception e) {     *             super.onError(id, call, e);     *             swipeRefreshLayout.setRefreshing(false);     *             //点击"重试"时, 会调用 '上拉加载更多监听' 里的onLoadMoreRequested();回调方法     *             mAdapter.getLoadMoreModule().loadMoreFail();//加载失败     *         }     *     });     * }     *     * 1.下拉刷新:     * getList(true);     *     * 2.上拉加载:     * getList(false);     * } </pre>     *     * @param adapter      不能为空     * @param listener     不能为空     */    protected void setLoadMore$Empty(BaseQuickAdapter adapter, OnLoadMoreListener listener) {        BRVUtils.setLoadMore$Empty(adapter, listener);    }    protected void setLoadMore$Empty(@LayoutRes int emptyLayoutRes, BaseQuickAdapter adapter, OnLoadMoreListener listener) {        BRVUtils.setLoadMore$Empty(emptyLayoutRes, adapter, listener);    }    /**     * 获取'下拉刷新/上拉加载'列表page, 如果和项目逻辑不符合, 可重写此方法     * @param adapter   列表Adapter extends BaseQuickAdapter     * @param isRefresh 是否是下拉刷新     * @param size      每次加载多少条     * @return     * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">     *     <tr>     *         <th>currentSize</th>     *         <th>size</th>     *         <th>return</th>     *     </tr>     *     <tr>     *         <td>0</td>     *         <td>20</td>     *         <td>1</td>     *     </tr>     *     <tr>     *         <td>1-19</td>     *         <td>20</td>     *         <td>1</td>     *     </tr>     *     <tr>     *         <td>20-39</td>     *         <td>20</td>     *         <td>2</td>     *     </tr>     *     <tr>     *         <td>40-59</td>     *         <td>20</td>     *         <td>3</td>     *     </tr>     * </table>     */    protected int getPage(@NonNull BaseQuickAdapter adapter, boolean isRefresh, int size) {        return BRVUtils.getPage(adapter, isRefresh, size);    }    /**     * 设置加载状态     * @param list 本次从服务器返回的分页数据     * @param size 每次加载多少条     */    protected void setLoadMoreState(@NonNull BaseQuickAdapter adapter, @Nullable List<?> list, int size) {        BRVUtils.setLoadMoreState(adapter, list, size);    }    /**     * 设置加载状态     * @param total   服务器返回的数据总数(如果后端返回了total的话...)     */    protected void setLoadMoreState(@NonNull BaseQuickAdapter adapter, int total) {        BRVUtils.setLoadMoreState(adapter, total);    }    @Override    public void onDestroyView() {        super.onDestroyView();        dismissNetWorkLoadingDialog();//        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);    }    @Override    public void onDestroy() {        super.onDestroy();        if (mActivityCallbacks != null) {            mActivityCallbacks.clear();            mActivityCallbacks = null;        }    }    @Override    public void onDetach() {        super.onDetach();        mActivity = null;        mFragment = null;    }}