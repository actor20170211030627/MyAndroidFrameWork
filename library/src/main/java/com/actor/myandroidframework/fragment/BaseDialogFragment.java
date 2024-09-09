package com.actor.myandroidframework.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.action.AnimAction;
import com.actor.myandroidframework.bean.OnActivityCallback;
import com.actor.myandroidframework.dialog.OnActionErrorListener;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;

/**
 * description: Dialog样式的Fragment <br />
 * DialogFragment 的出现就是为了解决 Dialog 和 Activity 生命周期不同步导致的内存泄漏问题，
 * 在 AndroidProject 曾经引入过，也经过了很多个版本的更新迭代，
 * 不过在 [10.0](https://github.com/getActivity/AndroidProject/releases/tag/10.0) 版本后就被移除了，
 * 原因是 Dialog 虽然有坑，但是 DialogFragment 也有坑，可以说解决了一个问题又引发了各种问题。
 * 先来细数我在 DialogFragment 上踩过的各种坑：
 * <ol>
 *     <li>
 *         DialogFragment 会占用 Dialog 的 Cancel 和 Dismiss 监听，为了就是在 Dialog 消失之后将自己（Fragment）从 Activity 上移除，这样的操作看起很合理，但是会引发一个问题，那么就是会导致我们原先给 Dialog 设置的 Cancel 和 Dismiss 监听被覆盖掉，间接导致我们无法使用这个监听，因为 Dialog 的监听器只能有一个观察者，而 AndroidProject 前期解决这个问题的方式是：将 Dialog 的监听器使用的观察者模式，从一对一改造成一对多，也就是一个被观察者可以有很多个观察者，由此来解决这个问题。 <br />
 *     </li>
 *     <li>
 *         DialogFragment 的显示和隐藏操作都不能在后台中进行，否则会出现一个报错 `java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState`，这个是因为 DialogFragment 的 show 和 dismiss 方法使用了 FragmentTransaction.commit 方法，这个 commit 方法会触发对 Activity 状态的检查，如果 Activity 的状态已经保存了（即已经调用了 onSaveInstanceState 方法），这个时候把 Fragment commit 到 Activity 上会抛出异常，这种场景在执行异步操作（例如请求网络）未结束前，用户手动将 App 返回到桌面，然后异步操作执行完毕，下一步就是回调异步监听器，这个时候我们的 App 已经处于后台状态，那么我们如果在监听回调中 show 或 dismiss DialogFragment，那么就会触发这个异常。AndroidProject 前期对于这个问题的解决方案是重写 DialogFragment.show 方法，加一个对 Activity 的状态判断，如果 Activity 处于后台状态，那么不去调用 super.show()，但是这样会导致一个问题，虽然解决了崩溃的问题，但是又会导致 Dialog 没显示出来，而重写 DialogFragment.dismiss 方法，直接调用 dismissAllowingStateLoss 方法，因为这个方法不会去检查 Activity 的状态。虽然这种解决方式不够完美，但却是我那个时候能想到的最好方法。 <br />
 *     </li>
 *     <li>
 *         最后一个问题是关于 DialogFragment 屏幕旋转的问题，首先 DialogFragment 是通过自身 onCreateDialog 方法来获取 Dialog 对象的，但是如果我们直接通过外层给 DialogFragment 传入 Dialog 的对象时，这样的代码逻辑貌似没有问题，但是在用户进行屏幕旋转，而刚好我们的应用没有固定屏幕方向时，DialogFragment 对象会跟随 Activity 销毁重建，因为它本身就是一个 Fragment，但是会导致之前的外层传入 Dialog 对象被回收并置空，然后再调用到 onCreateDialog 方法时，返回的是一个空对象的 Dialog，那么就会直接 DialogFragment 内部引发空指针异常，而 AndroidProject 前期解决这个问题的方案是，重写 onActivityCreated，赶在 onCreateDialog 方法调用之前，先判断 DialogFragment 对象内部持有的 Dialog 是否为空，如果是一个空对象，那么就将自己 dismissAllowingStateLoss 掉。
 *     </li>
 * </ol>
 *
 * 看过这些问题，你是不是和我一样，感觉这 DialogFragment 不是一般的坑，不过最终我放弃了使用 DialogFragment，
 * 并不是因为 DialogFragment 又出现了新问题，而是我想到了更好的方案来代替 DialogFragment，
 * 方案就是 Application.registerActivityLifecycleCallbacks，想必大家现在已经猜到我想干啥，
 * 和 DialogFragment 的作用一样，通过监听 Activity 的方式来管控 Dialog 的生命周期，但唯一不同的是，
 * 它不会出现刚刚说过 DialogFragment 的那些问题，这种方式在 AndroidProject 上迭代了几个版本过后，
 * 这期间没有发现新的问题，也没有收到别人反馈过这块的问题，证明这种方式是可行的。<br />
 * <br />
 * 参考轮轮哥历史代码: <a href="https://github.com/getActivity/AndroidProject/commit/1bca46cc88f18f3961fa33589b545366f59e5d6a#diff-023d742e41002361b48055d4593e88b5d0f0bd088e664571dab57eaea505396f">BaseDialogFragment</a>
 *
 * @author : ldf
 * date       : 2024/6/12 on 11
 * @version 1.0
 */
public class BaseDialogFragment extends AppCompatDialogFragment {

    protected FragmentActivity mActivity;
    protected Fragment mFragment;

    /** Activity 回调集合 */
    @Nullable protected SparseArray<OnActivityCallback> mActivityCallbacks;
    //请求码                                         9999防止和Activity的巧合
    private   int requestCodeCounter4BaseFragment = 9999;

    //Widow宽度
    protected int windowWidth = WindowManager.LayoutParams.MATCH_PARENT;
    protected int windowHeight = WindowManager.LayoutParams.WRAP_CONTENT;
    protected int gravity = Gravity.CENTER;
    protected int windowAnimations = AnimAction.ANIM_DEFAULT;
    protected float dimAmount = 0.6f;
    //Dialog弹起后, 状态栏是否变暗
    protected boolean isStatusBarDimmed = true;
    protected boolean isClickThrough = false;
    protected OnActionErrorListener onShowErrorListener;  //show()的时候, 出错回调
    protected OnActionErrorListener onDismissErrorListener;  //dismiss()的时候, 出错回调
    protected DialogInterface.OnDismissListener dismissListener;

    /**
     * @param fragmentManager 如果在Activity中, 传入:getSupportFragmentManager()
     *                        如果是Fragment中, 传入:getChildFragmentManager()
     */
    public void show(@NonNull FragmentManager fragmentManager) {
        show(fragmentManager, getClass().getName());
    }

    //第2个参数tag的作用: fragmentManager.findFragmentByTag(tag); 恢复的时候会调用
    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
//        boolean added = isAdded();
//        boolean cancelable = isCancelable();
//        boolean detached = isDetached();
//        boolean hidden = isHidden();
//        boolean inLayout = isInLayout();
//        boolean removing = isRemoving();
//        boolean resumed = isResumed();
//        boolean stateSaved = isStateSaved();
//        boolean visible = isVisible();
//        LogUtils.errorFormat("added=%b, cancelable=%b, detached=%b, hidden=%b,  inLayout=%b,  removing=%b,  resumed=%b,  stateSaved=%b,  visible=%b",
//                added, cancelable, detached, hidden, inLayout, removing, resumed, stateSaved, visible
//        );
        try {
            //要判断一下, 否则快速调用会报错: isAdded
            if (!isAdded()/* && manager.findFragmentByTag(tag) == null*/) {
                super.show(manager, tag);
            }
        } catch (Exception e) {
            if (onShowErrorListener == null) {
                e.printStackTrace();
            } else {
                onShowErrorListener.onActionError(e);
            }
        }
    }


    /**
     * @see #show(FragmentManager, String) 之后调用
     * @see #onCreate(Bundle) 之前调用, 当Fragment依附到Activity上
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        mFragment = this;
        LogUtils.error(getClass().getName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //不会调用这个方法
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.errorFormat("%s: hidden = %b", getClass().getName(), hidden);
    }

    //可返回自定义Dialog
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        /**
         * 高度全屏 要素1: 这个 DialogFragment 所依附的 Activity的ActionBar要确保false: <item name="windowActionBar">false</item>, 否则顶部高度全屏不了
         * 下方注释的代码设置了都无用
         */
//        if (mActivity instanceof AppCompatActivity) {
//            ((AppCompatActivity) mActivity).getSupportActionBar().hide();
//        }
//        mActivity.setActionBar(null);
//        mActivity.getActionBar().hide();

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
//        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
//        dialog.requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
//        dialog.getActionBar().setDisplayShowTitleEnabled(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
//        View view = inflater.inflate(R.layout.xxx, container, false);
//        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                //高度全屏 要素2
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                /**
                 * 背景透明, 不然设置自定义view {@link AlertDialog#setView(View)} 后, 后面有一个白色背景
                 */
//                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams params = window.getAttributes();
                if (params != null) {
                    params.width = windowWidth;     //设置宽度, 默认全屏
                    params.height = windowHeight;   //设置高度, 默认包裹内容
                    params.x = 0;
                    params.y = 0;//相对上方的偏移,负值忽略.
                    params.dimAmount = dimAmount;
                    params.gravity = gravity;
                    params.windowAnimations = windowAnimations;
//                    window.setAttributes(attributes);
                }
                //Dialog弹起后, 状态栏是否变暗
                if (isStatusBarDimmed) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }

                if (isClickThrough) {
                    //将允许对话框外的事件被发送到后面的视图
                    window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
//                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                    /**
                     * 允许对话框在被触摸时接收到外部的触摸事件, 示例代码:
                     * window.getDecorView().setOnTouchListener((v, event) -> {
                     *     if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                     *     }
                     *     return false;
                     * });
                     */
                    window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * @see #onActivityCreated(Bundle) 之后会调用  &  跳转Activity后返回, 会回调
     */
    @Override
    public void onResume() {
        super.onResume();
        LogUtils.error(getClass().getName());
    }


    /**
     * 设置宽度 <br />
     * {@link null 注意:} if宽度&高度都设置MATCH_PARENT, 会自动全屏(包括状态栏), 非常无语...
     *                    解决方法: 宽度-1px
     */
    public BaseDialogFragment setWidth(int width) {
        this.windowWidth = width;
        return this;
    }

    /**
     * 设置宽度百分比
     * @param widthPercent 宽度百分比
     * @deprecated 使用 {@link #setWidthPercent(float, int)}
     */
    @Deprecated
    public BaseDialogFragment setWidthPercent(@FloatRange(from = 0f, to = 1f, fromInclusive = false) float widthPercent) {
        return setWidth((int) (ScreenUtils.getAppScreenWidth() * widthPercent));
    }

    /**
     * 设置宽度百分比
     * @param widthPercent 宽度百分比
     * @param maxWidth 最大宽度(像素px) (∵有些lj平板竖屏的时候, 获取的宽度实际是高度, 导致Dialog超宽..., 所以建议设置最大宽度)
     */
    public BaseDialogFragment setWidthPercent(@FloatRange(from = 0f, to = 1f, fromInclusive = false) float widthPercent, int maxWidth) {
        return setWidth(Math.min((int) (ScreenUtils.getAppScreenWidth() * widthPercent), maxWidth));
    }

    /**
     * 设置高度 <br />
     * {@link null 注意:} if宽度&高度都设置MATCH_PARENT, 会自动全屏(包括状态栏), 非常无语...
     *                    解决方法: 宽度-1px
     */
    public void setHeight(int height) {
        this.windowHeight = height;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
    }

    /**
     * 设置重心 & 动画
     * @param gravity 重心: <br />
     *        &emsp;&emsp; {@link Gravity#CENTER}(默认), {@link Gravity#LEFT}, {@link Gravity#TOP}, {@link Gravity#RIGHT}, {@link Gravity#BOTTOM} <br />
     *        &emsp;&emsp; {@link GravityCompat#START}, {@link GravityCompat#END}
     * @param windowAnimations Dialog显示/隐藏 的动画: <br />
     *        <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *             <tr>
     *                         <th align="center">动画</th>
     *                         <th align="center">说明</th>
     *             </tr>
     *             <tr> <td>{@link AnimAction#ANIM_DEFAULT}</td> <td>使用系统默认Dialog动画</td> </tr>
     *             <tr> <td>{@link AnimAction#ANIM_EMPTY}</td> <td>没有动画效果</td> </tr>
     *             <tr>
     *                 <td>{@link AnimAction}</td>
     *                 <td>更多动画见 AnimAction</td>
     *             </tr>
     *             <tr>
     *                 <td>{@link R.style#YourCustomAnim R.style.YourCustomAnim}</td>
     *                 <td>也阔以自定义动画</td>
     *             </tr>
     *        </table>
     */
    public void setGravityAndAnimation(int gravity, @StyleRes int windowAnimations) {
        this.gravity = gravity;
        this.windowAnimations = windowAnimations;
    }

    /**
     * 设置窗口后面的暗淡程度[0-1], 0最亮, 默认=0.6
     * @param dimAmount 昏暗的数量
     */
    public void setDimAmount(@FloatRange(from = 0.0f, to = 1.0f) float dimAmount) {
        this.dimAmount = dimAmount;
    }

    /**
     * Dialog弹起后, 状态栏是否变暗
     * @param isStatusBarDimmed 是否变暗, 默认=true <br />
     *                         if=true, {@link #setDimAmount(float)}才有效。<br />
     *                          if=false, {@link #setDimAmount(float)}无效, 背景会全亮
     */
    public void isStatusBarDimmed(boolean isStatusBarDimmed) {
        this.isStatusBarDimmed = isStatusBarDimmed;
    }

    /**
     * 点击弹窗外部时, 是否将点击事件透传到弹窗下，默认是false
     */
    public void isClickThrough(boolean isClickThrough) {
        this.isClickThrough = isClickThrough;
    }

    /**
     * show()的时候报错监听
     */
    public void setOnShowErrorListener(OnActionErrorListener onShowErrorListener) {
        this.onShowErrorListener = onShowErrorListener;
    }

    /**
     * dismiss()的时候报错监听
     */
    public void setOnDismissErrorListener(OnActionErrorListener onDismissErrorListener) {
        this.onDismissErrorListener = onDismissErrorListener;
    }

    /**
     * @deprecated 跳转页面后再返回, 在'Activity/Fragment中'调用dismiss()方法有时候会报错:
     * java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
     * {@link androidx.fragment.app.DialogFragment#dismissInternal(boolean, boolean)}
     * 解决方法: 使用{@link #dismissAllowingStateLoss()}方法
     */
    @Deprecated
    @Override
    public void dismiss() {
        try {
            if (getFragmentManager() != null) super.dismiss();//如果子类不是在可视的状态下调用, 也会报错
//            bottomSheetBehavior.setHideable(true);
        } catch (Exception e) {
            if (onDismissErrorListener == null) {
                e.printStackTrace();
            } else {
                onDismissErrorListener.onActionError(e);
            }
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        try {
            if (getFragmentManager() != null) super.dismissAllowingStateLoss();
        } catch (Exception e) {
            if (onDismissErrorListener == null) {
                e.printStackTrace();
            } else {
                onDismissErrorListener.onActionError(e);
            }
        }
    }

    /**
     * 消失监听
     */
    public void setOnDismissListener(@Nullable DialogInterface.OnDismissListener listener) {
        this.dismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) dismissListener.onDismiss(dialog);
    }


    /**
     * “内存重启”时, onCreated, onViewCreated会有保存的数据
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }


    /**
     * 请务必使用这种回调的方式, 更方便
     */
    public void startActivityForResult(Intent intent, OnActivityCallback callback) {
        startActivityForResult(intent, null, callback);
    }
    public void startActivityForResult(Intent intent, @Nullable Bundle options, OnActivityCallback callback) {
        if (mActivityCallbacks == null) mActivityCallbacks = new SparseArray<>(1);
        mActivityCallbacks.put(++requestCodeCounter4BaseFragment, callback);
        startActivityForResult(intent, requestCodeCounter4BaseFragment, options);
    }
    @Override
    @Deprecated
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }
    @Override
    @Deprecated
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    @Deprecated
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OnActivityCallback callback;
        if (mActivityCallbacks != null && (callback = mActivityCallbacks.get(requestCode)) != null) {
            callback.onActivityResult(resultCode, data);
            mActivityCallbacks.remove(requestCode);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActivityCallbacks != null) {
            mActivityCallbacks.clear();
            mActivityCallbacks = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        mFragment = null;
    }
}
