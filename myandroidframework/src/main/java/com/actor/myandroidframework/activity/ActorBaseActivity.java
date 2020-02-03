package com.actor.myandroidframework.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.dialog.LoadingDialog;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.MyOkhttpUtils.MyOkHttpUtils;
import com.actor.myandroidframework.utils.TextUtil;
import com.actor.myandroidframework.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Description: Activity基类
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2017/5/27 on 12:45.
 * @version 1.0
 */
public class ActorBaseActivity extends AppCompatActivity {

//    private   FrameLayout  flContent;  //主要内容的帧布局
//    private   LinearLayout llLoading; //加载中的布局
//    protected TextView     tvLoading;  //例:正在加载中,请稍后...
//    private   LinearLayout llEmpty; //没数据
//    protected ACache              aCache = ActorApplication.instance.aCache;

    protected Activity            activity;
    protected Intent              intent;
    protected Map<String, Object> params = new LinkedHashMap<>();
    protected List<Call> calls;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        logError(getClass().getName());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置屏幕朝向,在setContentView之前
        //设置状态栏默认颜色,如果不要这个颜色,可在自己的onCreate中重写下面一句,自己设颜色
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.color_00CCFF));
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
//        View baseView = getLayoutInflater().inflate(R.layout.activity_base, null);//加载基类布局
//        flContent = baseView.findViewById(R.id.fl_content);
//        llLoading = baseView.findViewById(R.id.ll_loading);
//        tvLoading = findViewById(R.id.tv_loading);
//        llEmpty = findViewById(R.id.ll_empty);
//        View childView = getLayoutInflater().inflate(layoutResID, null);//加载子类布局
//        flContent.addView(childView);//将子布局添加到空的帧布局
//        super.setContentView(baseView);
        super.setContentView(layoutResID);
    }

    //是否显示加载中...
//    protected void showLoading(boolean isShow) {
//        llLoading.setVisibility(isShow ? View.VISIBLE : View.GONE);
//    }

    //设置加载中...
//    protected void setLoadingText(CharSequence loading) {
//        tvLoading.setText(loading);
//    }

    //是否显示empty图片
//    protected void showEmpty(boolean isShow) {
//        llEmpty.setVisibility(isShow ? View.VISIBLE : View.GONE);
//    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    /**
     * 共享元素方式跳转
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivity(Intent intent, @NonNull View... sharedElements) {
//        ActivityUtils.startActivity(this, intent, sharedElements);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && sharedElements.length > 0) {
            //单个共享元素方式跳转, 如果是图片的话跳转到下个页面有可能变形
//            ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
            int len = sharedElements.length;
            @SuppressWarnings("unchecked")
            Pair<View, String>[] pairs = new Pair[len];
            for (int i = 0; i < len; i++) {
                pairs[i] = Pair.create(sharedElements[i], sharedElements[i].getTransitionName());
            }
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
            startActivity(intent, compat.toBundle());
        } else startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    /**
     * 共享元素方式跳转
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivityForResult(Intent intent, int requestCode, @NonNull View... sharedElements) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && sharedElements.length > 0) {
            //单个共享元素方式跳转, 如果是图片的话跳转到下个页面有可能变形
//            ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
            int len = sharedElements.length;
            @SuppressWarnings("unchecked")
            Pair<View, String>[] pairs = new Pair[len];
            for (int i = 0; i < len; i++) {
                pairs[i] = Pair.create(sharedElements[i], sharedElements[i].getTransitionName());
            }
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
            startActivityForResult(intent, requestCode, compat.toBundle());
        } else startActivityForResult(intent, requestCode);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pre_enter, R.anim.next_exit);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 返回String区
    ///////////////////////////////////////////////////////////////////////////
    protected String getNoNullString(String s) {
        return s == null? "" : s;
    }

    protected String getNoNullString(String s, String defaultStr) {
        return s == null? defaultStr : s;
    }

    //"输入内容不能少于30字"示例:              输入内容不能少于%1$d字,      30
    protected String getStringRes(@StringRes int stringResId, Object... formatArgs) {
        return getString(stringResId, formatArgs);
    }

    //获取格式化后的String, 例: "我的姓名是%s, 我的年龄是%d", "张三", 23
    protected String getStringFormat(String format, Object... args) {
        return TextUtil.getStringFormat(format, args);
    }

    public static String getText(Object obj){
        return TextUtil.getText(obj);
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
        return TextUtil.isNoEmpty(objs);
    }

    /**
     * @param obj 判断对象是否不为空
     *            1.如果是 EditText/TextInputLayout, 且输入为空, 就将光标定位到相应的EditText且弹出系统键盘.
     *            2.如果是 {@link TextUtil.GetTextAble}
     *              且 {@link TextUtil.GetTextAble#getEditText()}!=null
     *              且 {@link TextUtil.GetTextAble#keyboardShowAbleIfEditText()},
     *              且 输入为空, 就将光标定位到相应的EditText且弹出系统键盘
     *            obj 包括如下类型:
     * <ol>
     *      <li>{@link CharSequence}</li>
     *      <li>{@link java.lang.reflect.Array}</li>
     *      <li>{@link java.util.Collection Collection(包括: List, Set, Queue)}</li>
     *      <li>{@link java.util.Map}</li>
     *      <li>{@link android.widget.TextView}</li>
     *      <li>{@link com.actor.myandroidframework.utils.TextUtil.GetTextAble}</li>
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
        return TextUtil.isNoEmpty(obj, notify);
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
    protected void toast(Object notify){
        ToastUtils.show(String.valueOf(notify));
    }


    ///////////////////////////////////////////////////////////////////////////
    // 显示加载Diaong
    ///////////////////////////////////////////////////////////////////////////
    private LoadingDialog loadingDialog;
    public void showLoadingDialog() {
        getLoadingDialog(true).show();
    }

    public void showLoadingDialog(boolean cancelable) {
        getLoadingDialog(cancelable).show();
    }

    protected LoadingDialog getLoadingDialog(boolean cancelable) {
        if (loadingDialog == null) loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelAble(cancelable);
        return loadingDialog;
    }

    //隐藏加载Diaong
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
     * 设置上拉加载更多 & 空布局
     * private List<Item> items = new ArrayList<>();//数据列表
     * private total;
     * getList(boolean isRefresh);
     * 1.下拉刷新:
     * getList(true);
     *
     * 2.上拉加载:
     * getList(false);
     *
     * 3.获取数据时:
     * params.put(Global.page, getPage(isRefresh, items, Global.SIZE));
     *
     * 4.获取数据成功:
     * onOk {
     *     total = data.totalCount;
     *     List rows = data.rows;
     *     if (rows != null) {
     *         ifRefreshClear(isRefresh, items);
     *         myAdapter.addData(rows);
     *     }
     *     if (items.size() < total) {
     *         myAdapter.loadMoreComplete();//加载完成
     *     } else myAdapter.loadMoreEnd();//已经没有数据了
     * }
     *
     * 5.获取数据失败:
     * onError() {
     *     myAdapter.loadMoreFail();//加载失败
     * }
     *
     * 6.获取数据失败(点击"重试"时, 会调用 '上拉加载更多' 里的onLoadMoreRequested();回调方法
     *
     * @param adapter 不能为空
     * @param recyclerView 不能为空
     * @param listener 不能为空
     */
    protected void setLoadMore$Empty(BaseQuickAdapter adapter, RecyclerView recyclerView, BaseQuickAdapter.RequestLoadMoreListener listener) {
        setLoadMore$Empty(R.layout.layout_for_empty, adapter, recyclerView, listener);
    }

    protected void setLoadMore$Empty(@LayoutRes int layoutId, BaseQuickAdapter adapter, RecyclerView recyclerView, BaseQuickAdapter.RequestLoadMoreListener listener) {
        adapter.setOnLoadMoreListener(listener, recyclerView);//上拉加载更多
        adapter.setEmptyView(layoutId, recyclerView);//空布局
    }

    /**
     * 获取'下拉刷新/上拉加载'列表page
     * @param isRefresh 是否是下拉刷新
     * @param items 列表数据集合
     * @param size 每次加载多少条
     */
    protected int getPage(boolean isRefresh, @NonNull List items, int size) {
        if (isRefresh) return 1;
        return items.size() / size + 1;
    }

    /**
     * 如果'下拉刷新'列表, 清空旧数据
     * @param items 列表数据集合
     */
    protected void ifRefreshClear(boolean isRefresh, @NonNull List items) {
        if (isRefresh) items.clear();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
