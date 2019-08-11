package com.actor.myandroidframework.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.actor.myandroidframework.R;

/**
 * Description:从底部弹出的Fragment
 *
 * 方法执行顺序
 * show(FragmentManager manager, String tag)
 * onCreate
 * onCreateDialog(Bundle savedInstanceState)
 * onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
 * onViewCreated
 * onStart
 * mDialog.show()
 * onResume
 *
 * 使用方法:
 * 1.写个Fragment继承本类
 * 2.重写getLayoutId方法, 返回布局id
 * 3. 示例
 * FullSheetDialogFragment dialogFragment = new FullSheetDialogFragment();//继承本类
 * dialogFragment.setPeekHeight(100);
 * dialogFragment.setMaxHeight(300);
 * dialogFragment.show(getSupportFragmentManager());
 *
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/6/13 on 14:05
 * @version 1.0
 */
public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//    private String mParam1;
//    private String mParam2;

    private static final String TAG = "BaseBottomSheetDialogFragment";
    private int                 mPeekHeight = -1;//设置首次弹出高度
    private int                 mMaxHeight;//最大高度
    private float               dimAmount = -1F;//背景灰度, [0, 1]
    private Window              mWindow;
    private BottomSheetBehavior bottomSheetBehavior;//<FrameLayout>?//里面有一些方法
    private View                contentView;

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
        } else dialog = new BottomSheetDialog(getContext(), R.style.TransparentBottomSheetStyle);
        return dialog;
    }

    /**
     * 设置布局, 每次show()都会调用...
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return contentView = inflater.inflate(getLayoutId(), container);
    }

    /**
     * 子类在这个方法中初始化, 每次show()都会调用...
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 设置布局Id, 可以适配根部局是ConstraintLayout的情况
     */
    public abstract @LayoutRes int getLayoutId();

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        mWindow = dialog.getWindow();
        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//设置软键盘不自动弹出
        View bottomSheet = mWindow.findViewById(android.support.design.R.id.design_bottom_sheet);
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

    public void setPeekHeight(int peekHeight) {
        if (peekHeight >= 0) mPeekHeight = peekHeight;
    }

    /**
     * 最大高度
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

    public void show(FragmentManager fragmentManager) {
        //要判断一下, 否则快速调用会报错: isAdded
        if (!isAdded()) super.show(fragmentManager, TAG);
    }

    /**
     * 有时候会引起空指针问题:
     * {@link android.support.v4.app.DialogFragment#dismissInternal(boolean)}
     * 105行 getFragmentManager() = null
     */
    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击任意布局关闭, 这个应该只是隐藏, 而 dismiss() 后会回调 onDestroyView();
     */
//    public void hide() {
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//    }

    //调用dismiss();后会回调
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }

    //调用dismiss();后会回调
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }

    //调用dismiss();后会回调
//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
}
