package com.actor.myandroidframework.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.FloatRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

/**
 * https://www.jianshu.com/p/7fcec871ea36
 * java.lang.IllegalStateException: Fragment does not have a view
 * 在BottomSheetDialog中使用ViewPager会报这个错误。错误的原因没有深究，提供一个简单的解决方案就是不要再onCreateDialog()
 * 中创建Dialog，转到onCreateView中创建View，就不会报这个异常。
 * 但是这么干又会带来第一个问题里面提到的，背景变成了白色。如果直接在onCreateView()或者onViewCreated()里面调用
 * ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
 * 会报空指针异常，这时需要在onActivityCreated（）中调用这个方法，就可以避免这个问题。汗，一趟走过来，问题还真是不少。
 * 参考：http://www.voidcn.com/article/p-vtgwgqnn-nq.html
 *
 * 示例使用:
 * BaseBottomSheetDialog baseBottomDialog = new BaseBottomSheetDialog(this) {
 *     @Override
 *     protected int getLayoutResId() {
 *         return R.layout.dialog_bottom_sheet;
 *     }
 * };
 * Button btn = baseBottomDialog.findViewById(R.id.button0);
 * baseBottomDialog.setPeekHeight(dp2px(322));
 * baseBottomDialog.setMaxHeight(dp2px(322))
 * baseBottomDialog.setDimAmount(0.2F);
 * baseBottomDialog.show();
 *
 * Description: 从底部弹出的Dialog, 能上下拖拽滑动
 * Author     : ldf
 * Date       : 2019/6/12 on 21:31
 * @version 1.0
 */
public abstract class BaseBottomSheetDialog extends BottomSheetDialog {

    protected      int                  mPeekHeight;//设置首次弹出高度
    protected      int                  mMaxHeight;//最大高度
    protected Window              mWindow;
    protected BottomSheetBehavior bottomSheetBehavior;

    protected final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback
            = new BottomSheetBehavior.BottomSheetCallback() {

        //这里是bottomSheet 状态的改变
        @Override
        public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
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
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            //这里是拖拽中的回调，根据slideOffset可以做一些动画
        }
    };

    public BaseBottomSheetDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public BaseBottomSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
        init();
    }

    protected BaseBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    protected void init() {
        mWindow = getWindow();
        int layoutResId = getLayoutResId();
        if (layoutResId != 0) setContentView(layoutResId);
    }

    /**
     * 设置你自定义Dialog的layout
     * 注意: 布局里面根部局不能是ConstraintLayout, 否则不适配, 原因未知
     *       要在外面套一层布局, 比如LinearLayout
     */
    protected abstract @LayoutRes int getLayoutResId();

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        //内容的背景设置成透明, 默认白色
        ((View) view.getParent()).setBackgroundResource(android.R.color.transparent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.error(this.getClass().getName());
        if (getBottomSheetBehavior() != null) {
            bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
        }
//        findViewById()//可以初始化控件等
    }

    /**
     * 设置首次弹出高度
     */
    public void setPeekHeight(int peekHeight) {
        if (peekHeight >= 0) {
            mPeekHeight = peekHeight;
            if (getBottomSheetBehavior() != null) {
                bottomSheetBehavior.setPeekHeight(mPeekHeight);
            }
        }
    }

    /**
     * 最大高度
     */
    public void setMaxHeight(int height) {
        if (height > 0) mMaxHeight = height;
    }

    /**
     * 设置窗口后面灰色大背景的亮度[0-1]
     * @param dimAmount 昏暗的数量
     */
    public void setDimAmount(@FloatRange(from = 0, to = 1) float dimAmount) {
        mWindow.setDimAmount(dimAmount);
    }

    /**
     * 设置隐藏时是否应跳过折叠状态app:behavior_skipCollapsed
     * 膨胀一次后。将此设置为true没有效果，除非sheet是可隐藏的(hideable).
     * 下滑时, 是否跳过折叠状态, 一滑到底. Behavior.setHideable=true才生效
     */
    public void setSkipCollapsed(boolean skipCollapsed) {
        bottomSheetBehavior.setSkipCollapsed(skipCollapsed);
    }

    /**
     * 如果是布局, 相当于布局中的 app:behavior_hideable="true"
     * 当我们拖拽下拉的时候，bottom sheet是否能全部隐藏.
     * 如果=false, 当下拉时, 滑动到弹出高度, 再往下滑没有用. 点击外部可以隐藏
     */
    public void setHideable(boolean hideable) {
        if (bottomSheetBehavior != null) bottomSheetBehavior.setHideable(hideable);
    }

    @Override
    public void show() {
        super.show();
        //需要在onCreate() 中对宽高进行设置，或者 show() 之后进行设置，否则高度设置不生效
        if (mMaxHeight > 0) {
            mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, mMaxHeight);
            mWindow.setGravity(Gravity.BOTTOM);
        }
    }

    //??
    public void setBatterSwipeDismiss(boolean enabled) {
        if (enabled) {
        }
    }

    private BottomSheetBehavior getBottomSheetBehavior() {
        if (bottomSheetBehavior == null) {
            View view = mWindow.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            // setContentView() 没有调用
            if (view == null) return null;
            bottomSheetBehavior = BottomSheetBehavior.from(view);
        }
        return bottomSheetBehavior;
    }

    //调用dismiss();后会回调
//    @Override
//    public void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//    }
}
