package com.actor.myandroidframework.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.MyOkhttpUtils.MyOkHttpUtils;
import com.actor.myandroidframework.utils.TextUtil;
import com.actor.myandroidframework.utils.ToastUtils;
import com.actor.myandroidframework.dialog.LoadingDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
     * @param view 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivity(Intent intent, @NonNull View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
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
     * @param view 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivityForResult(Intent intent, int requestCode, @NonNull View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
            startActivityForResult(intent, requestCode, compat.toBundle());
        } else startActivityForResult(intent, requestCode);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pre_enter, R.anim.next_exit);
    }


    //返回String区=============================================
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
        return String.format(Locale.getDefault(), format, args);
    }

    public static String getText(Object obj){
        return TextUtil.getText(obj);
    }


    //判空区=============================================
    protected boolean isEmpty(Object... obj) {
        return !isNoEmpty(obj);
    }

    protected boolean isEmpty(Object obj, CharSequence notify) {
        return !isNoEmpty(obj, notify);
    }

    /**
     * @param objs 参数的类型为:
     * <ol>
     *      <li>{@link android.widget.TextView}</li>
     *      <li>{@link android.support.design.widget.TextInputLayout}</li>
     *      <li>{@link TextUtil.GetTextAble}</li>
     *      <li>{@link CharSequence}</li>
     *      <li>{@link java.lang.reflect.Array}</li>
     *      <li>{@link java.util.Collection}</li>
     *      <li>{@link java.util.Map}</li>
     * </ol>
     * @return 都不为空,返回true
     */
    protected boolean isNoEmpty(Object... objs) {
        return TextUtil.isNoEmpty(objs);
    }

    protected boolean isNoEmpty(Object obj, CharSequence notify) {
        return TextUtil.isNoEmpty(obj, notify);
    }


    //打印日志区=============================================
    protected void logError(Object msg) {
        LogUtils.error(String.valueOf(msg), false);
    }

    protected void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }


    //toast区=============================================
    protected void toast(Object notify){
        ToastUtils.show(String.valueOf(notify));
    }


    //显示加载Diaong=============================================
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


    //Retrofit区=============================================
    protected <T> Call<T> putCall(Call<T> call) {//放入List, onDestroy的时候全部取消请求
        if (calls == null) calls = new ArrayList<>();
        calls.add(call);
        return call;
    }


    //下拉刷新 & 上拉加载更多 & 空布局区=============================================
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
     * private total = 1;
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
     *         ifPage1(items, items, Global.SIZE);
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
     * 如果'下拉刷新'列表, page=1, 清空旧数据
     * @param items 列表数据集合
     * @param size 每次加载多少条
     */
    protected boolean ifPage1(@NonNull List items, int size) {
        boolean isPage1 = items.size() < size;
        if (isPage1) items.clear();
        return isPage1;
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
