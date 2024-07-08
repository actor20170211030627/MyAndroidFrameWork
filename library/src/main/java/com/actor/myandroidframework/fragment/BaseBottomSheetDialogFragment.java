package com.actor.myandroidframework.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;

import com.actor.myandroidframework.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Description:从底部弹出的DialogFragment, 能上下拖拽滑动 <br />
 *              不要在show()方法之前获取View, 实在要这样的话, 换成其它方案(Dialog) <br />
 * <br />
 * 方法执行顺序:
 * <ol>
 *     <li>show(FragmentManager manager, String tag)</li>
 *     <li>onCreate</li>
 *     <li>onCreateDialog(Bundle savedInstanceState)</li>
 *     <li>onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)</li>
 *     <li>onViewCreated</li>
 *     <li>onStart</li>
 *     <li>mDialog.show()</li>
 *     <li>onResume</li>
 * </ol>
 *
 * 使用方法:
 * <ol>
 *     <li>写个Fragment继承本类</li>
 *     <li>重写getLayoutId方法, 返回布局id</li>
 *     <li>
 *         示例:
 * <pre>
 *     FullSheetDialogFragment dialogFragment = new FullSheetDialogFragment();//继承本类
 *     dialogFragment.setPeekHeight(100);
 *     dialogFragment.setMaxHeight(300);
 *     dialogFragment.show(getSupportFragmentManager());
 * </pre>
 *     </li>
 * </ol>
 *
 * @Author     : ldf
 * @Date       : 2019/6/13 on 14:05
 */
public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//    private String mParam1;
//    private String mParam2;

    private int                 mPeekHeight = -1;//设置首次弹出高度
    private int                 mMaxHeight;//最大高度
    private float               dimAmount = -1F;//背景灰度, [0, 1]
    private Window              mWindow;
    public boolean isDismissError = false;  //dismiss的时候, 是否出错了
    private BottomSheetBehavior bottomSheetBehavior;
    protected DialogInterface.OnDismissListener dismissListener;

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback
            = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            //禁止拖拽，
//            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                //设置为收缩状态
//                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            }

            /**
             * STATE_HIDDEN: 隐藏状态。默认是false，可通过app:behavior_hideable属性设置。
             * STATE_COLLAPSED: 折叠关闭状态。可通过app:behavior_peekHeight来设置显示的高度,peekHeight默认是0。
             * STATE_DRAGGING: 被拖拽状态
             * STATE_SETTLING: 拖拽松开之后到达终点位置（collapsed or expanded）前的状态。
             * STATE_EXPANDED: 完全展开的状态。
             */
            //这儿要设置一下, 否则下滑完成HIDDEN后, 有个Dialog样式灰色透明背景
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        //这里是拖拽中的回调，根据slideOffset可以做一些动画
        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

//    public static BaseBottomSheetDialogFragment newInstance(String param1, String param2) {
//        BaseBottomSheetDialogFragment fragment = new BaseBottomSheetDialogFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Bundle arguments = getArguments();
//        if (arguments != null) {
//            mParam1 = arguments.getString(ARG_PARAM1);
//            mParam2 = arguments.getString(ARG_PARAM2);
//        }
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog;
        if (getContext() == null) {
            dialog = super.onCreateDialog(savedInstanceState);
        } else {
            dialog = new BottomSheetDialog(getContext(), R.style.TransparentBottomSheetStyle);
        }
        return dialog;
    }

    /**
     * 设置布局, 每次show()都会调用.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container);
    }

    /**
     * 子类在这个方法中初始化, 每次show()都会调用.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 设置布局Id, 可以适配根部局是ConstraintLayout的情况
     */@LayoutRes
    public abstract int getLayoutId();

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog == null) return;
        mWindow = dialog.getWindow();
        if (mWindow == null) return;
        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//设置软键盘不自动弹出
        View bottomSheet = mWindow.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            if (mMaxHeight > 0) {//设置最大高度
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
                layoutParams.height = mMaxHeight;
            }
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
            if (mPeekHeight >= 0) bottomSheetBehavior.setPeekHeight(mPeekHeight);//设置首次弹出高度
            if (dimAmount >= 0) mWindow.setDimAmount(dimAmount);
        }
    }

    /**
     * @param peekHeight 设置show()时弹出高度
     */
    public void setPeekHeight(int peekHeight) {
        if (peekHeight >= 0) mPeekHeight = peekHeight;
    }

    /**
     * 最大高度, 如果传入{@link Integer#MAX_VALUE}, 会导致这个Fragment内的RecyclerView等不能滑动.
     */
    public void setMaxHeight(int height) {
        if (height > 0) mMaxHeight = height;
    }

    /**
     * 设置窗口后面灰色大背景的亮度[0-1]
     * @param dimAmount 昏暗的数量, 0完全透明, 1完全黑暗
     */
    public void setDimAmount(@FloatRange(from = 0, to = 1) float dimAmount) {
        if (dimAmount >= 0) this.dimAmount = dimAmount;
    }

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
        //要判断一下, 否则快速调用会报错: isAdded
        if (!isAdded()/* && manager.findFragmentByTag(tag) == null*/) {
            super.show(manager, tag);
        }
    }

    /**
     * @deprecated 跳转页面后再返回, 在'Activity/Fragment中'调用dismiss()方法有时候会报错: <br />
     * java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
     * {@link androidx.fragment.app.DialogFragment#dismissInternal(boolean)} <br />
     * 解决方法: 使用{@link #dismissAllowingStateLoss()}方法
     */
    @Deprecated
    @Override
    public void dismiss() {
        try {
            if (getFragmentManager() != null) super.dismiss();//如果子类不是在可视的状态下调用, 也会报错
//            bottomSheetBehavior.setHideable(true);
            isDismissError = false;
        } catch (Exception e) {
            isDismissError = true;
            e.printStackTrace();
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        try {
            if (getFragmentManager() != null) super.dismissAllowingStateLoss();
            isDismissError = false;
        } catch (Exception e) {
            isDismissError = true;
            e.printStackTrace();
        }
    }

    /**
     * 点击任意布局关闭, 这个应该只是隐藏, 而 dismiss() 后会回调 onDestroyView();
     */
//    public void hide() {
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//    }

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

    //调用dismiss();后会回调
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //调用dismiss();后会回调
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //调用dismiss();后会回调
    @Override
    public void onDetach() {
        super.onDetach();
    }
}
