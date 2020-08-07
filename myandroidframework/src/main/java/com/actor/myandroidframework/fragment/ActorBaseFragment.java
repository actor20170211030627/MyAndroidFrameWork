package com.actor.myandroidframework.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.dialog.LoadingDialog;
import com.actor.myandroidframework.dialog.ShowLoadingDialogAble;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.okhttputils.MyOkHttpUtils;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Description: Fragment基类
 *     onActivityCreated : 这个Fragment所依附的Activity对象被创建成功之后，初始化数据
 *     onViewCreated : 这个Fragment所包装的View对象创建完成之后会进行的回调
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2017/5/27 on 18:22.
 * @version 1.0
 */
public abstract class ActorBaseFragment extends Fragment implements ShowLoadingDialogAble {

    protected FragmentActivity    activity;
    protected Fragment            fragment;
    protected Intent              intent;
    protected Map<String, Object> params = new LinkedHashMap<>();
    protected List<Call>          calls;
//    protected ACache              aCache = ActorApplication.instance.aCache;

    private boolean             isVisible;//是否可见
    private boolean             isPrepared;//onViewCreated已经执行完毕
    private boolean             isLazyLoaded = false;//第一次是否已经加载

    //使用newInstance()的方式返回Fragment对象
//    public static ActorBaseFragment newInstance() {
//        ActorBaseFragment fragment = new ActorBaseFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        fragment = this;
    }

    /**
     * @see #onCreate(Bundle) 之前调用, 当Fragment可见/不可见的时候
     * 使用ViewPager + Fragment, 当ViewPager切换Fragment时会回调这个方法.
     * 如果isVisibleToUser=false, "不要使用控件 & 操作UI界面"
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        logFormat("%s: isVisibleToUser = %b", getClass().getName(), isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isPrepared && isVisible && !isLazyLoaded) {
            firstTimeLoadData();
            isLazyLoaded = true;
        }
    }

    /**
     * 获取这个Fragment所依附的Activity对象
     * 初始化 & 系统恢复页面数据 & 旋转屏幕 时, 会回调这个方法
     */
    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Bundle arguments = getArguments();
//        if (arguments != null) {
//            mParam1 = arguments.getString(ARG_PARAM1);
//            mParam2 = arguments.getString(ARG_PARAM2);
//        }
    }

    /**
     * @see #onCreate(Bundle) 和 {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} 之间进行调用
     * 使用{@link android.support.v4.app.FragmentManager} 进行add hide show时会回调这个方法
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        logFormat("%s: hidden = %b", getClass().getName(), hidden);
        isVisible = !hidden;
        if (isPrepared && isVisible && !isLazyLoaded) {
            firstTimeLoadData();
            isLazyLoaded = true;
        }
    }

    //生成这个Fragment所包装的View对象
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
//        View view = inflater.inflate(R.layout.xxx, container, false);
//        return view;
    }

    //这个Fragment所包装的View对象创建完成之后会进行的回调, 初始化View
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(/*baseView*/view, savedInstanceState);
    }

    /**
     * @see #onViewCreated(View, Bundle) 之后
      */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        if (isVisible && !isLazyLoaded) {
            firstTimeLoadData();
            isLazyLoaded = true;
        }
    }

    /**
     * 第一次加载数据(可用于Fragment第一次可见的时候再请求网络)
     */
    protected void firstTimeLoadData() {
    }

    //跳转Activity后返回, 会回调
    @Override
    public void onResume() {
        super.onResume();
        logError(getClass().getName());
    }

    /**
     * “内存重启”时, onCreated, onViewCreated会有保存的数据
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (activity != null) {
            activity.overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
        }
    }

    /**
     * 共享元素方式跳转
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivity(Intent intent, @NonNull View... sharedElements) {
        ActivityUtils.startActivity(activity, intent, sharedElements);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (activity != null) {
            activity.overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
        }
    }

    /**
     * 共享元素方式跳转
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivityForResult(Intent intent, int requestCode, @NonNull View... sharedElements) {
        ActivityUtils.startActivityForResult(this, intent, requestCode, sharedElements);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logFormat("onActivityResult: requestCode=%d, resultCode=%d, data=%s", requestCode, resultCode, data);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 返回String区
    ///////////////////////////////////////////////////////////////////////////
    protected String getNoNullString(String text){
        return TextUtils2.getNoNullString(text);
    }

    protected String getNoNullString(String text, String defaultStr) {
        return TextUtils2.getNoNullString(text, defaultStr);
    }

    //获取格式化后的String, 例: getStringFormat("我的姓名是%s, 我的年龄是%d", "张三", 23)
    protected String getStringFormat(String format, Object... args) {
        return TextUtils2.getStringFormat(format, args);
    }

    protected String getText(Object obj){
        return TextUtils2.getText(obj);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 判空区
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 只要有一个为空, 就返回true
     */
    protected boolean isEmpty(@NonNull Object... objs) {
        return !isNoEmpty(objs);
    }

    /**
     * 只要有一个为空, 就返回true
     * @param notify 为空时, toast 提示的内容
     */
    protected boolean isEmpty(Object obj, CharSequence notify) {
        return !isNoEmpty(obj, notify);
    }

    /**
     * @param objs 判断对象是否都不为空
     * @return 都不为空, 返回true
     */
    protected boolean isNoEmpty(@NonNull Object... objs) {
        return TextUtils2.isNoEmpty(objs);
    }

    /**
     * @param obj 判断对象是否不为空
     *            1.如果是 EditText/TextInputLayout, 且输入为空, 就将光标定位到相应的EditText且弹出系统键盘.
     *            2.如果是 {@link TextUtils2.GetTextAble}
     *              且 {@link TextUtils2.GetTextAble#getEditText()}!=null
     *              且 {@link TextUtils2.GetTextAble#keyboardShowAbleIfEditText()},
     *              且 输入为空, 就将光标定位到相应的EditText且弹出系统键盘
     *            obj 包括如下类型:
     * <ol>
     *      <li>{@link CharSequence}</li>
     *      <li>{@link java.lang.reflect.Array}</li>
     *      <li>{@link java.util.Collection Collection(包括: List, Set, Queue)}</li>
     *      <li>{@link java.util.Map}</li>
     *      <li>{@link android.widget.TextView}</li>
     *      <li>{@link com.actor.myandroidframework.utils.TextUtils2.GetTextAble}</li>
     *      <li>{@link android.support.design.widget.TextInputLayout}</li>
     *      <li>{@link android.util.SparseArray}</li>
     *      <li>{@link android.util.SparseBooleanArray}</li>
     *      <li>{@link android.util.SparseIntArray}</li>
     *      <li>{@link android.util.SparseLongArray}</li>
     *      <li>{@link android.support.v4.util.SparseArrayCompat}</li>
     *      <li>{@link Object#toString()}</li>
     * </ol>
     * @param notify 如果为空 & notify != null, toast(notify);
     * @return 是否不为空
     */
    protected boolean isNoEmpty(Object obj, CharSequence notify) {
        return TextUtils2.isNoEmpty(obj, notify);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 打印日志区
    ///////////////////////////////////////////////////////////////////////////
    protected void logError(Object msg) {
        LogUtils.error(String.valueOf(msg), false);
    }

    /**
     * 打印格式化后的字符串
     */
    protected void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }

    ///////////////////////////////////////////////////////////////////////////
    // toast区
    ///////////////////////////////////////////////////////////////////////////
    protected void toast(@StringRes int resId) {
        ToastUtils.showShort(resId);
    }

    protected void toast(Object notify){
        ToastUtils.showShort(String.valueOf(notify));
    }


    ///////////////////////////////////////////////////////////////////////////
    // 显示加载Diaong
    ///////////////////////////////////////////////////////////////////////////
    private LoadingDialog loadingDialog;
    @Override
    public void showLoadingDialog() {
        showLoadingDialog(true);
    }

    public void showLoadingDialog(boolean cancelable) {
        LoadingDialog dialog = getLoadingDialog(cancelable);
        if (dialog != null) dialog.show();
    }

    @Override
    public @Nullable LoadingDialog getLoadingDialog(boolean cancelable) {
        if (activity == null) return null;//onCreate()前调用会出问题
        if (loadingDialog == null) loadingDialog = new LoadingDialog(activity);
        loadingDialog.setCancelAble(cancelable);
        return loadingDialog;
    }

    //隐藏加载Diaong
    @Override
    public void dismissLoadingDialog() {
        if (loadingDialog != null) loadingDialog.dismiss();
    }


    ///////////////////////////////////////////////////////////////////////////
    // Retrofit区
    ///////////////////////////////////////////////////////////////////////////
    protected <T> Call<T> putCall(Call<T> call) {//放入List, onDestroy的时候全部取消请求
        if (calls == null) calls = new ArrayList<>();
        calls.add(call);
        return call;
    }


    ///////////////////////////////////////////////////////////////////////////
    // 下拉刷新 & 上拉加载更多 & 空布局
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 设置空布局
     * @param adapter 不能为空
     * @param recyclerView 不能为空
     */
    protected void setEmptyView(BaseQuickAdapter adapter, RecyclerView recyclerView) {
        setEmptyView(R.layout.layout_for_empty, adapter, recyclerView);
    }

    protected void setEmptyView(@LayoutRes int layoutId, BaseQuickAdapter adapter, RecyclerView recyclerView) {
        adapter.setEmptyView(layoutId, recyclerView);
    }

    /**
     * 设置上拉加载更多 & 空布局, 示例:
     *
     * //写在常量类里面, 比如写在 Global.java 里面.
     * public static final int SIZE = 10;
     * public static final String page = "page";
     * public static final String size = "size";
     *
     * //isRefresh: 是否是下拉刷新
     * private void getList(boolean isRefresh) {
     *     params.clear();
     *     params.put(Global.page, getPage(isRefresh, myAdapter, Global.SIZE));
     *     params.put(Global.size, Global.SIZE);
     *     MyOkHttpUtils.get(url, params, new BaseCallback<UserBean>(this, isRefresh) {
     *         @Override
     *         public void onOk(@NonNull UserBean info, int id) {
     *             dismissLoadingDialog();
     *             int total = info.totalCount;
     *             List<UserBean.Data> datas = info.data;
     *             if (datas != null) {
     *                 //如果是下拉刷新
     *                 if (requestIsRefresh) {
     *                     myAdapter.setNewData(datas);//设置新数据
     *                 } else {
     *                     myAdapter.addData(datas);//增加数据
     *                 }
     *             }
     *             if (myAdapter.getData().size() < total) {
     *                 myAdapter.loadMoreComplete();//加载完成
     *             } else myAdapter.loadMoreEnd();//已经没有数据了
     *         }
     *
     *         @Override
     *         public void onError(int id, okhttp3.Call call, Exception e) {
     *             super.onError(id, call, e);
     *
     *             //点击"重试"时, 会调用 '上拉加载更多监听' 里的onLoadMoreRequested();回调方法
     *             myAdapter.loadMoreFail();//加载失败
     *         }
     *     });
     * }
     *
     * 1.下拉刷新:
     * getList(true);
     *
     * 2.上拉加载:
     * getList(false);
     *
     * @param adapter      不能为空
     * @param recyclerView 不能为空
     * @param listener     不能为空
     */
    protected void setLoadMore$Empty(BaseQuickAdapter adapter, RecyclerView recyclerView, BaseQuickAdapter.RequestLoadMoreListener listener) {
        setLoadMore$Empty(R.layout.layout_for_empty, adapter, recyclerView, listener);
    }

    protected void setLoadMore$Empty(@LayoutRes int layoutId, BaseQuickAdapter adapter, RecyclerView recyclerView, BaseQuickAdapter.RequestLoadMoreListener listener) {
        adapter.setOnLoadMoreListener(listener, recyclerView);//上拉加载更多
        adapter.setEmptyView(layoutId, recyclerView);//空布局
    }

    /**
     * 获取'下拉刷新/上拉加载'列表page, 如果和项目逻辑不符合, 可重写此方法
     * @param isRefresh 是否是下拉刷新
     * @param adapter   列表Adapter extends BaseQuickAdapter
     * @param size      每次加载多少条
     */
    protected int getPage(boolean isRefresh, @NonNull BaseQuickAdapter adapter, int size) {
        if (isRefresh) return 1;
        return adapter.getData().size() / size + 1;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissLoadingDialog();
        MyOkHttpUtils.cancelTag(this);//取消网络请求
        if (calls != null && calls.size() > 0) {//取消Retrofit的网络请求
            for (Call call : calls) {
                if (call != null) call.cancel();
            }
            calls.clear();
        }
        calls = null;
//        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        fragment = null;
        params.clear();//如果你的Fragment在这儿报空指针, 说明你的Fragment第一次没有被彻底回收, 应该从List/Map中移除!
        params = null;
    }
}
