package com.actor.myandroidframework.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.dialog.LoadingDialog;
import com.actor.myandroidframework.dialog.ShowLoadingDialogable;
import com.actor.myandroidframework.service.BaseService;
import com.actor.myandroidframework.utils.BaseSharedElementCallback;
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
 * Description: Activity基类
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2017/5/27 on 12:45.
 *
 * @version 1.0
 */
public class ActorBaseActivity extends AppCompatActivity implements ShowLoadingDialogable {

//    protected FrameLayout  flContent;//主要内容的帧布局
//    protected LinearLayout llEmpty;  //没数据
//    protected CacheDiskUtils aCache = ActorApplication.instance.aCache;

    protected Activity                  activity;
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
                sharedElementCallback = new BaseSharedElementCallback(false, new BaseSharedElementCallback.OnSharedElementPositionChangedListener() {
                    @Override
                    public View onSharedElementPositionChanged(int oldPosition, int currentPosition) {
                        return sharedElementPositionChanged(oldPosition, currentPosition);
                    }
                });
                setEnterSharedElementCallback(sharedElementCallback);
            }
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
//        View baseView = getLayoutInflater().inflate(R.layout.activity_base, null);//加载基类布局
//        flContent = baseView.findViewById(R.id.fl_content);
//        llEmpty = findViewById(R.id.ll_empty);
//        View childView = getLayoutInflater().inflate(layoutResID, null);//加载子类布局
//        flContent.addView(childView);//将子布局添加到空的帧布局
//        super.setContentView(baseView);
        super.setContentView(layoutResID);
    }
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
        //如果自定义切换动画, 请重写这个方法
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    /**
     * 共享元素方式跳转, 示例:
     * GitHub:
     * https://github.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/SharedElementActivity.java
     * 码云:
     * https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/SharedElementActivity.java
     *
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
//      //单个共享元素方式跳转
//        ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        ActivityUtils.startActivity(this, intent, sharedElements);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        //如果自定义切换动画, 请重写这个方法
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    /**
     * 共享元素方式跳转
     *
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
//      //单个共享元素方式跳转
//        ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        ActivityUtils.startActivityForResult(this, intent, requestCode, sharedElements);
    }

    /**
     * B界面返回A界面
     *
     * @param requestCode
     * @param data        B界面setResult(RESULT_OK, data);返回的值, 即使A界面startActivity, 只要B界面setResult有值, 都能收到
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
     * 在A界面的回调中, 当B界面返回A界面时, 用于获取A界面currentPosition位置的共享元素
     * 在B界面的回调中, 当B界面返回A页面时, 需要先获取B界面currentPosition位置的共享元素
     *
     * @param oldPosition     跳转前在A界面的position
     * @param currentPosition 跳转后在B界面的position
     * @return 返回新position位置的共享元素View, 注意这个View要设置transitionName
     */
    protected @NonNull
    View sharedElementPositionChanged(int oldPosition, int currentPosition) {
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
     *
     * @param intent          用于返回A界面值的intent
     * @param oldPosition     从A界面跳过来时的position
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
        //如果自定义切换动画, 请重写这个方法
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
     * 可继承或参考 {@link com.actor.myandroidframework.service.BaseService}
     */
    @RequiresPermission(Manifest.permission.FOREGROUND_SERVICE)
    @Override
    public ComponentName startForegroundService(Intent service) {
        //如果不判断, 低版本会崩溃
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return super.startForegroundService(service.putExtra(BaseService.IS_START_FOREGROUND_SERVICE, true));
        } else return startService(service);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 返回String区
    ///////////////////////////////////////////////////////////////////////////
    protected String getNoNullString(Object object) {
        return TextUtils2.getNoNullString(object);
    }

    protected String getNoNullString(Object object, String defaultStr) {
        return TextUtils2.getNoNullString(object, defaultStr);
    }

    //"输入内容不能少于30字"示例: getStringRes("输入内容不能少于%1$d字", 30)
    protected String getStringRes(@StringRes int stringResId, Object... formatArgs) {
        if (formatArgs == null || formatArgs.length == 0) return getString(stringResId);
        return getString(stringResId, formatArgs);
    }

    //获取格式化后的String, 例: "我的姓名是%s, 我的年龄是%d", "张三", 23
    protected String getStringFormat(String format, Object... args) {
        return TextUtils2.getStringFormat(format, args);
    }

    protected String getText(Object obj) {
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
     *
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
     * @param obj    判断对象是否不为空
     *               1.如果是 EditText/TextInputLayout, 且输入为空, 就将光标定位到相应的EditText且弹出系统键盘.
     *               2.如果是 {@link TextUtils2.GetTextAble}
     *               且 {@link TextUtils2.GetTextAble#getEditText()}!=null
     *               且 {@link TextUtils2.GetTextAble#keyboardShowAbleIfEditText()},
     *               且 输入为空, 就将光标定位到相应的EditText且弹出系统键盘
     *               obj 包括如下类型:
     *               <ol>
     *                    <li>{@link CharSequence}</li>
     *                    <li>{@link java.lang.reflect.Array}</li>
     *                    <li>{@link java.util.Collection Collection(包括: List, Set, Queue)}</li>
     *                    <li>{@link java.util.Map}</li>
     *                    <li>{@link android.widget.TextView}</li>
     *                    <li>{@link com.actor.myandroidframework.utils.TextUtils2.GetTextAble}</li>
     *                    <li>{@link com.google.android.material.textfield.TextInputLayout}</li>
     *                    <li>{@link android.util.SparseArray}</li>
     *                    <li>{@link android.util.SparseBooleanArray}</li>
     *                    <li>{@link android.util.SparseIntArray}</li>
     *                    <li>{@link android.util.SparseLongArray}</li>
     *                    <li>{@link androidx.collection.SparseArrayCompat}</li>
     *                    <li>{@link Object#toString()}</li>
     *               </ol>
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

    protected void toast(Object notify) {
        ToastUtils.showShort(String.valueOf(notify));
    }

    //格式化toast
    protected void toastFormat(@Nullable String format, @Nullable Object... args) {
        ToastUtils.showShort(format, args);
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
        if (activity == null) return null;
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
     *
     * @param adapter      不能为空
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
     *     params.put(Global.page, getPage(isRefresh, mAdapter, Global.SIZE));
     *     params.put(Global.size, Global.SIZE);
     *     MyOkHttpUtils.get(url, params, new BaseCallback<UserBean>(this, isRefresh) {
     *         @Override
     *         public void onOk(@NonNull UserBean info, int id, boolean isRefresh) {
     *             swipeRefreshLayout.setRefreshing(false);
     *             //int total = info.totalCount;               //⑴. total这种方式也可以
     *             List<UserBean.Data> datas = info.data;
     *             //如果是下拉刷新
     *             if (isRefresh) {
     *                 mAdapter.setNewData(datas);//设置新数据
     *             } else if (datas != null) {
     *                 mAdapter.addData(datas);//增加数据
     *             }
     *             //setLoadMoreState(mAdapter, total);            //⑴
     *             setLoadMoreState(mAdapter, datas, Global.SIZE); //⑵
     *         }
     *
     *         @Override
     *         public void onError(int id, okhttp3.Call call, Exception e) {
     *             super.onError(id, call, e);
     *             swipeRefreshLayout.setRefreshing(false);
     *             //点击"重试"时, 会调用 '上拉加载更多监听' 里的onLoadMoreRequested();回调方法
     *             mAdapter.loadMoreFail();//加载失败
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
     * @return currentSize   size    return
     *              0         20       1
     *             1-19       20       1
     *            20-39       20       2
     *            40-59       20       3
     */
    protected int getPage(boolean isRefresh, @NonNull BaseQuickAdapter adapter, int size) {
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
    protected void setLoadMoreState(@NonNull BaseQuickAdapter adapter, @Nullable List<?> list, int size) {
        //"list = null"     or     "list为空"     or     "list < size"(比如一次获取20条, 但是只返回15条, 说明服务器没有更多数据了)
        boolean isLoadMoreEnd = list == null || list.size() < size;
        if (isLoadMoreEnd) {
            adapter.loadMoreEnd();//已经没有数据了
        } else {
            adapter.loadMoreComplete();//加载完成
        }
    }

    /**
     * 设置加载状态
     * @param total   服务器返回的数据总数(如果后端返回了total的话...)
     */
    protected void setLoadMoreState(@NonNull BaseQuickAdapter adapter, int total) {
        if (adapter.getData().size() < total) {
            adapter.loadMoreComplete();//加载完成
        } else adapter.loadMoreEnd();//已经没有数据了
    }


    /**
     * 1.分屏时activity的生命周期(待验证)
     * onPause()-onStop()-onMultiWindowModeChanged()-onDestroy()-onCreate()-onStart()-onResume()-onPause()
     * 2.从分屏页到自己的应用, 全屏显示自己的应用(待验证)
     * onStop()-onDestroy()-onCreate()-onStart()-onResume()-onPause()-onMultiWindowModeChanged()-onResume()​
     * 3.boolean inMultiWindowMode = isInMultiWindowMode();//App是否处于分屏模式
     */
    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        logFormat("分屏: onMultiWindowModeChanged: isInMultiWindowMode = %b", isInMultiWindowMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
        MyOkHttpUtils.cancelTag(this);//取消网络请求
        params.clear();
        params = null;
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
