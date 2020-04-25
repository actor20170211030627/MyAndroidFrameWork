package com.actor.myandroidframework.activity;

import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.dialog.LoadingDialog;
import com.actor.myandroidframework.utils.BaseSharedElementCallback;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtil;
import com.actor.myandroidframework.utils.easyhttp.EasyHttpUtils;
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
 * Description: Activity基类
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2017/5/27 on 12:45.
 * @version 1.0
 */
public class ActorBaseActivity extends AppCompatActivity {

//    protected FrameLayout  flContent;//主要内容的帧布局
//    protected LinearLayout llLoading;//加载中的布局
//    protected TextView     tvLoading;//例:正在加载中,请稍后...
//    protected LinearLayout llEmpty;  //没数据
//    protected ACache       aCache = ConfigUtils.APPLICATION.aCache;

    protected Activity                  activity;
    protected Intent                    intent;
    protected Map<String, Object>       params = new LinkedHashMap<>();
    protected List<Call>                calls;
    protected BaseSharedElementCallback sharedElementCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        logError(getClass().getName());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置屏幕朝向,在setContentView之前
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //如果A界面跳B界面是元素共享方式, 且返回A界面时要更新A界面的共享元素位置
            if (getIntent().getBooleanExtra(BaseSharedElementCallback.EXTRA_IS_NEED_UPDATE_POSITION, false)) {
                postponeEnterTransition();//延时动画
                sharedElementCallback = new BaseSharedElementCallback(false, this::sharedElementPositionChanged);
                setEnterSharedElementCallback(sharedElementCallback);
            }
        }
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

    ///////////////////////////////////////////////////////////////////////////
    // 界面跳转
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    /**
     * 共享元素方式跳转, 示例:
     * GitHub:
     * https://github.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/SharedElementActivity.java
     * 码云:
     * https://gitee.com/actor2017/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/SharedElementActivity.java
     * @param isNeedUpdatePosition A界面跳转B界面再返回后, 是否需要更新A界面的position.
     *                             例: A界面: RecyclerView <--> B界面: ViewPager, 返回后更新A界面共享元素position
     *                             如果true, A界面需要重写2个方法:
     *                                  @see #sharedElementPositionChanged(int, int)
     *                                  @see #onSharedElementBacked(int, int)
     *                             如果true, B界面需要重写方法:
     *                                  @see #sharedElementPositionChanged(int, int)
     *                                  @see #onBackPressedSharedElement(Intent, int, int)//在super.onBackPressed();前调用
     *                                  @see #startPostponedEnterTransition()//共享元素准备完后(图片加载完后), 开始延时共享动画
     *                                        //fragment中: getActivity().startPostponedEnterTransition();//开始延时共享动画
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivity(Intent intent, boolean isNeedUpdatePosition, @NonNull View... sharedElements) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && sharedElements.length > 0) {
            if (isNeedUpdatePosition) {
                intent.putExtra(BaseSharedElementCallback.EXTRA_IS_NEED_UPDATE_POSITION, true);
                if (sharedElementCallback == null) {
                    sharedElementCallback = new BaseSharedElementCallback(true, new BaseSharedElementCallback.OnSharedElementPositionChangedListener() {
                        @Override
                        public View onSharedElementPositionChanged(int oldPosition, int currentPosition) {
                            return sharedElementPositionChanged(oldPosition, currentPosition);
                        }
                    });
                    //设置A界面跳转到B界面的元素共享动画回调, 用于A界面position更新
                    setExitSharedElementCallback(sharedElementCallback);
                }
            }
        }
//      //单个共享元素方式跳转, 如果是图片的话跳转到下个页面有可能变形
//        ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        ActivityUtils.startActivity(this, intent, sharedElements);
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
    public void startActivityForResult(Intent intent, int requestCode, boolean isNeedUpdatePosition, @NonNull View... sharedElements) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && sharedElements.length > 0) {
            if (isNeedUpdatePosition) {
                intent.putExtra(BaseSharedElementCallback.EXTRA_IS_NEED_UPDATE_POSITION, true);
                if (sharedElementCallback == null) {
                    sharedElementCallback = new BaseSharedElementCallback(true, new BaseSharedElementCallback.OnSharedElementPositionChangedListener() {
                        @Override
                        public View onSharedElementPositionChanged(int oldPosition, int currentPosition) {
                            return sharedElementPositionChanged(oldPosition, currentPosition);
                        }
                    });
                    //设置A界面跳转到B界面的元素共享动画回调, 用于A界面position更新
                    setExitSharedElementCallback(sharedElementCallback);
                }
            }
        }
//      //单个共享元素方式跳转, 如果是图片的话跳转到下个页面有可能变形
//        ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        ActivityUtils.startActivityForResult(this, intent, requestCode, sharedElements);
    }

    /**
     * B界面返回A界面
     * @param requestCode
     * @param data B界面setResult(RESULT_OK, data);返回的值, 即使A界面startActivity, 只要B界面setResult有值, 都能收到
     */
    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        super.onActivityReenter(requestCode, data);
        if (data == null) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int oldPosition = data.getIntExtra(BaseSharedElementCallback.EXTRA_START_POSITION, 0);
            int currentPosition = data.getIntExtra(BaseSharedElementCallback.EXTRA_CURRENT_POSITION, 0);
            if (sharedElementCallback != null) {
                sharedElementCallback.set(true, oldPosition, currentPosition);
            }
            if (oldPosition != currentPosition) {
                onSharedElementBacked(oldPosition, currentPosition);
            }
        }
    }

    /**
     * RecyclerView <--> ViewPager, 共享元素跳转
     *      在A界面的回调中, 当B界面返回A界面时, 用于获取A界面currentPosition位置的共享元素
     *      在B界面的回调中, 当B界面返回A页面时, 需要先获取B界面currentPosition位置的共享元素
     * @param oldPosition 跳转前在A界面的position
     * @param currentPosition 跳转后在B界面的position
     * @return 返回新position位置的共享元素View, 注意这个View要设置transitionName
     */
    protected @NonNull View sharedElementPositionChanged(int oldPosition, int currentPosition) {
        //A界面返回示例:
//        return recyclerView.findViewHolderForAdapterPosition(currentPosition).itemView.findViewById(R.id.image_view);
        //B界面返回示例:
//        return fragment.getSharedElementView();
        return null;
    }

    /***
     * B界面返回A界面, 且position发生了改变. A界面重写此方法, 更新共享元素位置
     * @param oldPosition
     * @param currentPosition
     */
    protected void onSharedElementBacked(int oldPosition, int currentPosition) {
//        recyclerView.scrollToPosition(currentPosition);
//        postponeEnterTransition();//延时动画
//        recyclerView.post(this::startPostponedEnterTransition);//开始延时共享动画
    }

    /**
     * 共享元素跳转, B界面返回A界面时, super.onBackPressed();之前调用这个方法
     * @param intent 用于返回A界面值的intent
     * @param oldPosition 从A界面跳过来时的position
     * @param currentPosition B界面现在的position, 用于A界面元素共享动画跳转到这个位置
     */
    protected void onBackPressedSharedElement(Intent intent, int oldPosition, int currentPosition) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (sharedElementCallback != null) {
                sharedElementCallback.set(true, oldPosition, currentPosition);
                intent.putExtra(BaseSharedElementCallback.EXTRA_START_POSITION, oldPosition);
                intent.putExtra(BaseSharedElementCallback.EXTRA_CURRENT_POSITION, currentPosition);
            }
        }
        setResult(RESULT_OK, intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pre_enter, R.anim.next_exit);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 打开Service
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public ComponentName startService(Intent intent) {
        return super.startService(intent);
    }

    /**
     * 启动前台服务
     * Android 8.0 之后调用这个方法, 必须满足以下2个条件:
     * 1.添加权限: <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
     * 2.在Service中必须调用 {@link android.app.Service#startForeground(int, Notification)}
     *   可继承或参考 {@link com.actor.myandroidframework.service.BaseService}
     */
    @Override
    public ComponentName startForegroundService(Intent service) {
        //如果不判断, 低版本会崩溃
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return super.startForegroundService(service);
        } else return startService(service);
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

    protected String getText(Object obj){
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
        ToastUtils.showShort(String.valueOf(notify));
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
        EasyHttpUtils.cancelSubscription(this);//取消网络请求
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
