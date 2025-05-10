package com.actor.myandroidframework.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.bean.OnActivityCallback;
import com.actor.myandroidframework.dialog.LoadingDialog;
import com.actor.myandroidframework.dialog.ShowNetWorkLoadingDialogable;
import com.actor.myandroidframework.service.ActorBaseService;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.sharedelement.BaseSharedElementCallback;
import com.actor.myandroidframework.utils.sharedelement.SharedElementUtils;

/**
 * Description: Activity基类
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : ldf
 * Date       : 2017/5/27 on 12:45.
 *
 * @version 1.0
 */
public class ActorBaseActivity extends AppCompatActivity implements ShowNetWorkLoadingDialogable {

    //在网络请求中传入LifecycleOwner, ∴用AppCompatActivity
    protected AppCompatActivity         mActivity;

    /** Activity 回调集合 */
    @Nullable protected SparseArray<OnActivityCallback> mActivityCallbacks;
    private   int                             requestCodeCounter4BaseActivity = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        LogUtils.error(getClass().getName());
        /**
         * 不要在这儿设置屏幕朝向
         * if清单文件没设置方向, 而这儿代码设置了竖屏, 但是用户打开了'自动旋转'且拿着的设备是横屏,
         * 就会造成屏幕从横屏->竖屏, 页面重新创建, 就有可能会出现bug.
         * 应该自己在清单文件中设置屏幕朝向!
         */
//        setRequestedOrientation(getScreenOrientation());
    }


    ///////////////////////////////////////////////////////////////////////////
    // 界面跳转
    ///////////////////////////////////////////////////////////////////////////
    public void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(this, clazz));
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(getNextEnterAnim(), getPreExitAnim());
    }

    @Override
    @Deprecated
    public void startActivity(Intent intent, @Nullable Bundle options) {
        super.startActivity(intent, options);
    }

    /**
     * 共享元素方式跳转
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivity(Intent intent, @NonNull View... sharedElements) {
        SharedElementUtils.startActivity(this, intent, sharedElements);
    }


    ///////////////////////////////////////////////////////////////////////////
    // startActivityForResult()
    ///////////////////////////////////////////////////////////////////////////
    @Override
    @Deprecated
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }
    @Override
    @Deprecated
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(getNextEnterAnim(), getPreExitAnim());
    }

    /**
     * 请务必使用这种回调的方式, 更方便
     * @param callback 下一页返回的值回调
     */
    public void startActivityForResult(Intent intent, OnActivityCallback callback) {
        if (mActivityCallbacks == null) mActivityCallbacks = new SparseArray<>(1);
        mActivityCallbacks.put(++requestCodeCounter4BaseActivity, callback);
        startActivityForResult(intent, requestCodeCounter4BaseActivity);
    }

    /**
     * 共享元素方式跳转
     * @param intent 跳转B页面的Intent
     * @param callback 下一页返回的值回调
     * @param exitSharedElementCallback 共享元素跳转回调
     */
    public void startActivityForResult(Intent intent, OnActivityCallback callback,
                                       @NonNull BaseSharedElementCallback exitSharedElementCallback) {
        if (mActivityCallbacks == null) mActivityCallbacks = new SparseArray<>(1);
        mActivityCallbacks.put(++requestCodeCounter4BaseActivity, callback);
        SharedElementUtils.startActivityForResult(this, intent, requestCodeCounter4BaseActivity, exitSharedElementCallback);
    }

    /**
     * 共享元素方式跳转
     * @param intent 跳转B页面的Intent
     * @param callback 下一页返回的值回调
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivityForResult(Intent intent, OnActivityCallback callback, @Nullable View... sharedElements) {
        if (mActivityCallbacks == null) mActivityCallbacks = new SparseArray<>(1);
        mActivityCallbacks.put(++requestCodeCounter4BaseActivity, callback);
        SharedElementUtils.startActivityForResult(this, intent, requestCodeCounter4BaseActivity, sharedElements);
    }

    /**
     * 跳转页面, 等待返回数据(Google新用法) <br />
     * <pre>
     *     ActivityResultLauncher<Intent> launcher = registerForActivityResult(callback);
     *     launcher.launch(intent, options);   //参2可选, 跳转页面
     * </pre>
     * @param callback 回调
     */
    @NonNull
    public ActivityResultLauncher<Intent> registerForActivityResult(
            @NonNull ActivityResultCallback<ActivityResult> callback) {
        return registerForActivityResultImpl(new ActivityResultContracts.StartActivityForResult(), callback);
    }

    /**
     * Google新用法 <br />
     * @param contract 请求类型:
     *                 <ol>
     *                     <li>{@link ActivityResultContracts.StartActivityForResult} 跳转页面, 等待返回数据</li>
     *                     <li>{@link ActivityResultContracts.StartIntentSenderForResult}</li>
     *                     <li>{@link ActivityResultContracts.RequestMultiplePermissions} 请求多个权限</li>
     *                     <li>{@link ActivityResultContracts.RequestPermission} 请求权限</li>
     *                     <li>{@link ActivityResultContracts.TakePicturePreview} 拍照预览</li>
     *                     <li>{@link ActivityResultContracts.TakePicture} 拍照</li>
     *                     <li>{@link ActivityResultContracts.TakeVideo} 拍视频</li>
     *                     <li>{@link ActivityResultContracts.PickContact} 选择联系人</li>
     *                     <li>{@link ActivityResultContracts.GetContent} 获取特定类型的文件</li>
     *                     <li>{@link ActivityResultContracts.GetMultipleContents} 获取特定类型的多个文件</li>
     *                     <li>{@link ActivityResultContracts.OpenDocument} 选择文档</li>
     *                     <li>{@link ActivityResultContracts.OpenMultipleDocuments} 选择多个文档</li>
     *                     <li>{@link ActivityResultContracts.OpenDocumentTree} 打开文档文件夹列表</li>
     *                     <li>{@link ActivityResultContracts.CreateDocument} 创建文档</li>
     *                     <li>也可以自定义...</li>
     *                 </ol>
     * @param callback 回调
     * @param <I> 输入类型
     * @param <O> 输出类型
     */
    @NonNull
//    @Override
    public /*final*/ <I, O> ActivityResultLauncher<I> registerForActivityResultImpl(
            @NonNull ActivityResultContract<I, O> contract,
            @NonNull ActivityResultCallback<O> callback) {
        return super.registerForActivityResult(contract, callback);
    }

    /**
     * if这个Activity中有Fragment, 那么不管这个Fragment的 onActivityResult(...)方法是否调用了 super.onActivityResult(...),
     * 最后都会回调到这儿来.
     */
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
        overridePendingTransition(getPreReEnterAnim(), getNextExitAnim());
    }

    /**
     * 进入下一页面时, 下一页(第2页)进入动画, if自定义切换动画, 请从写此方法
     */
    protected int getNextEnterAnim() { return R.anim.next_enter; }

    /**
     * 进入下一页面时, 当前页(第1页)退出动画, if自定义切换动画, 请从写此方法
     */
    protected int getPreExitAnim() { return R.anim.pre_exit; }

    /**
     * 退出当前页时, 前一页(第1页)重新进入动画, if自定义切换动画, 请从写此方法
     */
    protected int getPreReEnterAnim() { return R.anim.pre_enter; }

    /**
     * 退出当前页时, 当前页(第2页)退出动画, if自定义切换动画, 请从写此方法
     */
    protected int getNextExitAnim() { return R.anim.next_exit; }



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
     * 可继承或参考 {@link ActorBaseService}
     */
    @RequiresPermission(Manifest.permission.FOREGROUND_SERVICE)
    @Override
    public ComponentName startForegroundService(Intent service) {
        //如果不判断, 低版本会崩溃
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return super.startForegroundService(service.putExtra(ActorBaseService.IS_START_FOREGROUND_SERVICE, true));
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
    protected String getStringFormat(@StringRes int stringResId, Object... formatArgs) {
        return TextUtils2.getStringFormat(stringResId, formatArgs);
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
    // 显示加载Dialog
    ///////////////////////////////////////////////////////////////////////////
    private LoadingDialog netWorkLoadingDialog;
    //网络请求次数.(一个页面有可能有很多个请求)
    private int requestCountOfShowLoadingDialog = 0;

    @Override
    @Nullable
    public LoadingDialog getNetWorkLoadingDialog() {
        if (mActivity == null) return null;
        if (netWorkLoadingDialog == null) netWorkLoadingDialog = new LoadingDialog(mActivity);
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
        if (mActivityCallbacks != null) {
            mActivityCallbacks.clear();
            mActivityCallbacks = null;
        }
//        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }
}
