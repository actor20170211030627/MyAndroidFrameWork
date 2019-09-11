package com.actor.myandroidframework.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.MyOkhttpUtils.MyOkHttpUtils;
import com.actor.myandroidframework.utils.TextUtil;
import com.actor.myandroidframework.utils.ToastUtils;
import com.actor.myandroidframework.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
public abstract class ActorBaseFragment extends Fragment {

//    private   FrameLayout            flContent;  //主要内容的帧布局
//    private   LinearLayout           llLoading; //加载中的布局
//    protected TextView               tvLoading;  //例:正在加载中,请稍后...
//    private   LinearLayout           llEmpty; //没数据
    protected Activity            activity;
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


    /**
     * @see #onCreate(Bundle) 之前调用, 当Fragment可见/不可见的时候
     * 使用ViewPager + Fragment, 当ViewPager切换Fragment时会回调这个方法.
     * 如果isVisibleToUser=false, "不要使用控件 & 操作UI界面"
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        logFormat(getClass().getName().concat(": isVisibleToUser=%b"), isVisibleToUser);
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
        activity = getActivity();
        fragment = this;
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
        logFormat(getClass().getName().concat(": hidden=%b"), hidden);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        View baseView = getLayoutInflater().inflate(R.layout.activity_base, null);//加载基类布局
//        flContent = baseView.findViewById(R.id.fl_content);
//        llLoading = baseView.findViewById(R.id.ll_loading);
//        tvLoading = baseView.findViewById(R.id.tv_loading);
//        llEmpty = baseView.findViewById(R.id.ll_empty);
//        flContent.addView(view);//将子布局添加到空的帧布局
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
        if (activity != null) {
            activity.overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
        }
    }

    /**
     * 共享元素方式跳转
     * @param view 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivity(Intent intent, View view) {//TransitionName
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(activity, view, view.getTransitionName());
            startActivity(intent, compat.toBundle());
        } else startActivity(intent);
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
     * @param view 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public void startActivityForResult(Intent intent, int requestCode, @NonNull View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(activity, view, view.getTransitionName());
            startActivityForResult(intent, requestCode, compat.toBundle());
        } else startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logFormat("onActivityResult: requestCode=%d, resultCode=%d, data=%s", requestCode, resultCode, data);
    }


    //返回String区=============================================
    protected String getNoNullString(String text){
        return text == null ? "" : text;
    }

    protected String getNoNullString(String s, String defaultStr) {
        return s == null? defaultStr : s;
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
        LogUtils.Error(String.valueOf(msg), false);
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
    protected void showLoadingDialog() {
        getLoadingDialog(true).show();
    }

    protected void showLoadingDialog(boolean cancelable) {
        getLoadingDialog(cancelable).show();
    }

    protected LoadingDialog getLoadingDialog(boolean cancelable) {
        if (loadingDialog == null) loadingDialog = new LoadingDialog(activity);
        return loadingDialog;
    }

    //隐藏加载Diaong
    protected void dismissLoadingDialog() {
        if (loadingDialog != null) loadingDialog.dismiss();
    }


    //Retrofit区=============================================
    protected <T> Call<T> putCall(Call<T> call) {//放入List, onDestroy的时候全部取消请求
        if (calls == null) calls = new ArrayList<>();
        calls.add(call);
        return call;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissLoadingDialog();
        MyOkHttpUtils.cancelTag(this);
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
