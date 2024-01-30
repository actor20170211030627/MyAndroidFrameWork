package com.actor.myandroidframework.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.dialog.LoadingDialog;
import com.actor.myandroidframework.dialog.ShowNetWorkLoadingDialogable;
import com.actor.myandroidframework.service.BaseService;
import com.actor.myandroidframework.utils.BRVUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.sharedelement.BaseSharedElementCallback;
import com.actor.myandroidframework.utils.sharedelement.SharedElementA;
import com.actor.myandroidframework.utils.sharedelement.SharedElementUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: Activity基类
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : ldf
 * Date       : 2017/5/27 on 12:45.
 *
 * @version 1.0
 */
public class ActorBaseActivity extends AppCompatActivity implements ShowNetWorkLoadingDialogable {

//    protected CacheDiskUtils aCache = ActorApplication.instance.aCache;

    //在网络请求中传入LifecycleOwner, ∴用AppCompatActivity
    protected AppCompatActivity         activity;
    protected Map<String, Object>       params = new LinkedHashMap<>();

    /** Activity 回调集合 */
    @Nullable protected SparseArray<OnActivityCallback> mActivityCallbacks;
    private   int                             requestCodeCounter4BaseActivity = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        LogUtils.error(getClass().getName());
        //设置屏幕朝向,在setContentView之前
        setRequestedOrientation(getScreenOrientation());
    }
    /**
     * 屏幕方向, 可重写此方法. 如果不设置时的默认值: SCREEN_ORIENTATION_UNSPECIFIED(未指定)
     */
    protected int getScreenOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;    //竖屏
    }


    ///////////////////////////////////////////////////////////////////////////
    // 界面跳转
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 跳转 Activity 简化版, from hjq
     */
    public void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(this, clazz));
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        //如果自定义切换动画, 请在子类调用startActivity(intent);之后, 复制↓这1行并填入动画
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    /**
     * 共享元素方式跳转
     * @param isNeedUpdatePosition A界面跳转B界面再返回后, 是否需要更新A界面的position.
     * @param sharedElementA 如果A界面需要更新position, 需要 implements SharedElementA
     * @param sharedElementCallback 共享元素跳转回调
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivity(Intent intent, boolean isNeedUpdatePosition,
                              @Nullable SharedElementA sharedElementA,
                              @Nullable BaseSharedElementCallback sharedElementCallback,
                              @NonNull View... sharedElements) {
        SharedElementUtils.startActivity(this, intent, isNeedUpdatePosition, sharedElementA, sharedElementCallback, sharedElements);
    }


    public void startActivityForResult(Intent intent, OnActivityCallback callback) {
        startActivityForResult(intent, null, callback);
    }
    public void startActivityForResult(Intent intent, @Nullable Bundle options, OnActivityCallback callback) {
        if (mActivityCallbacks == null) mActivityCallbacks = new SparseArray<>(1);
        mActivityCallbacks.put(++requestCodeCounter4BaseActivity, callback);
        startActivityForResult(intent, requestCodeCounter4BaseActivity, options);
    }
    @Override
    @Deprecated
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }
    @Override
    @Deprecated
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        //如果自定义切换动画, 请在子类调用startActivityForResult(intent, requestCode, options);之后, 复制↓这1行并填入动画
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    /**
     * 共享元素方式跳转
     * @param isNeedUpdatePosition A界面跳转B界面再返回后, 是否需要更新A界面的position.
     * @param sharedElementA 如果A界面需要更新position, 需要 {@link SharedElementA implements SharedElementA}
     * @param sharedElementCallback 共享元素跳转回调
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivityForResult(Intent intent, int requestCode, boolean isNeedUpdatePosition,
                                       @Nullable SharedElementA sharedElementA,
                                       @Nullable BaseSharedElementCallback sharedElementCallback,
                                       @NonNull View... sharedElements) {
        SharedElementUtils.startActivityForResult(this, intent, requestCode, isNeedUpdatePosition,
                sharedElementA, sharedElementCallback, sharedElements);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OnActivityCallback callback;
        if (mActivityCallbacks != null && (callback = mActivityCallbacks.get(requestCode)) != null) {
            callback.onActivityResult(resultCode, data);
            mActivityCallbacks.remove(requestCode);
        }
    }

    @Override
    public void finish() {
        super.finish();
        //如果自定义切换动画, 请在子类调用finish();之后, 复制↓这1行并填入动画
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
     * @param notify 如果为空 & notify != null, showToast(notify);
     * @return 是否不为空
     */
    protected boolean isNoEmpty(Object obj, CharSequence notify) {
        return TextUtils2.isNoEmpty(obj, notify);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 显示加载Diaong
    ///////////////////////////////////////////////////////////////////////////
    private LoadingDialog netWorkLoadingDialog;
    //网络请求次数.(一个页面有可能有很多个请求)
    private int requestCountOfShowLoadingDialog = 0;

    @Override
    @Nullable
    public LoadingDialog getNetWorkLoadingDialog() {
        if (activity == null) return null;
        if (netWorkLoadingDialog == null) netWorkLoadingDialog = new LoadingDialog(activity);
        netWorkLoadingDialog.setCancelAble(true);
        return netWorkLoadingDialog;
    }
    @Override
    public int getRequestCount() {
        return requestCountOfShowLoadingDialog;
    }
    @Override
    public void setRequestCount(int requestCount) {
        requestCountOfShowLoadingDialog  = requestCount;
    }


    ///////////////////////////////////////////////////////////////////////////
    // 下拉刷新 & 上拉加载更多 & 空布局
    ///////////////////////////////////////////////////////////////////////////
    /**
     * adapter设置空布局
     *
     * @param adapter      不能为空
     */
    protected void setEmptyView(BaseQuickAdapter adapter) {
        BRVUtils.setEmptyView(adapter);
    }
    protected void setEmptyView(BaseQuickAdapter adapter, @LayoutRes int layoutId) {
        BRVUtils.setEmptyView(adapter, layoutId);
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
    protected void setLoadMore$Empty(BaseQuickAdapter adapter, OnLoadMoreListener listener) {
        BRVUtils.setLoadMore$Empty(adapter, listener);
    }

    protected void setLoadMore$Empty(@LayoutRes int emptyLayoutRes, BaseQuickAdapter adapter, OnLoadMoreListener listener) {
        BRVUtils.setLoadMore$Empty(emptyLayoutRes, adapter, listener);
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
    protected int getPage(@NonNull BaseQuickAdapter adapter, boolean isRefresh, int size) {
        return BRVUtils.getPage(adapter, isRefresh, size);
    }

    /**
     * 设置加载状态
     * @param list 本次从服务器返回的分页数据
     * @param size 每次加载多少条
     */
    protected void setLoadMoreState(@NonNull BaseQuickAdapter adapter, @Nullable List<?> list, int size) {
        BRVUtils.setLoadMoreState(adapter, list, size);
    }

    /**
     * 设置加载状态
     * @param total   服务器返回的数据总数(如果后端返回了total的话...)
     */
    protected void setLoadMoreState(@NonNull BaseQuickAdapter adapter, int total) {
        BRVUtils.setLoadMoreState(adapter, total);
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
        LogUtils.errorFormat("分屏: onMultiWindowModeChanged: isInMultiWindowMode = %b", isInMultiWindowMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissNetWorkLoadingDialog();
        params.clear();
        params = null;
//        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }


    public interface OnActivityCallback {

        /**
         * 结果回调 from hjq
         * @param resultCode        结果码
         * @param data              数据
         */
        void onActivityResult(int resultCode, @Nullable Intent data);
    }
}
