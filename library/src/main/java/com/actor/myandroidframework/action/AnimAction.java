package com.actor.myandroidframework.action;

import com.actor.myandroidframework.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/21
 *    desc   : 动画样式
 */
public interface AnimAction {

    /** 默认动画效果 */
    int ANIM_DEFAULT = -1;

    /** 没有动画效果 */
    int ANIM_EMPTY = 0;


    /** Dialog的(默认)系统动画 */
    int ANIM_DIALOG = R.style.Animation_AppCompat_Dialog;

    /** 系统Dialog动画 */
    int ANIM_DIALOG2 = R.style.Base_Animation_AppCompat_Dialog;

    /** 系统Dialog动画 */
    int ANIM_DIALOG3 = android.R.style.Animation_Dialog;

    /** 吐司动画 */
    int ANIM_TOAST = android.R.style.Animation_Toast;


    /** 左边弹出动画 */
    int ANIM_LEFT_SLIDE = R.style.AnimationLeftSlideStyle;

    /** 顶部弹出动画 */
    int ANIM_TOP_SLIDE = R.style.AnimationTopSlideStyle;

    /** 右边弹出动画 */
    int ANIM_RIGHT_SLIDE = R.style.AnimationRightSlideStyle;

    /**
     * 底部弹出动画 <br />
     * if 发现Dialog的动画是从顶部跑下来(反正不是从底部正常弹出), 需要在Dialog中设置高度: <br />
     * {@link com.actor.myandroidframework.dialog.BaseDialog#setHeight(int) BaseDialog.setHeight(int)} <br />
     * 具体原因未知...
     */
    int ANIM_BOTTOM_SLIDE = R.style.AnimationBottomSlideStyle;


    /** 缩放动画 */
    int ANIM_SCALE = R.style.AnimationScaleStyle;

    /** IOS 动画 */
    int ANIM_IOS = R.style.AnimationIOSStyle;
}