package com.actor.myandroidframework.sharedelement;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;

/**
 * description: 元素共享跳转工具类
 * company    :
 *
 * @author : ldf
 * date       : 2021/10/16 on 17
 * @version 1.0
 */
public class SharedElementUtils {

    public static BaseSharedElementCallback getSharedElementCallback(AppCompatActivity activity, @NonNull SharedElementAble sharedElementAble) {
        //如果A界面跳B界面是元素共享方式, 且返回A界面时要更新A界面的共享元素位置
        if (activity.getIntent().getBooleanExtra(BaseSharedElementCallback.EXTRA_IS_NEED_UPDATE_POSITION, false)) {
            activity.postponeEnterTransition();//延时动画
            BaseSharedElementCallback sharedElementCallback = new BaseSharedElementCallback(false, new BaseSharedElementCallback.OnSharedElementPositionChangedListener() {
                @Override
                public View onSharedElementPositionChanged(int oldPosition, int currentPosition) {
                    return sharedElementAble.sharedElementPositionChanged(oldPosition, currentPosition);
                }
            });
            activity.setEnterSharedElementCallback(sharedElementCallback);
            return sharedElementCallback;
        }
        return null;
    }

    /**
     * 共享元素方式跳转, 示例:
     * https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/SharedElementActivity.java
     *
     * @param isNeedUpdatePosition A界面跳转B界面再返回后, 是否需要更新A界面的position.
     *                             例: A界面: RecyclerView <--> B界面: ViewPager, 返回后更新A界面共享元素position
     *                             如果true, A界面需要重写2个方法:
     *                                  @see SharedElementAble#sharedElementPositionChanged(int, int)
     *                                  @see SharedElementAble#onSharedElementBacked(int, int)
     *                             如果true, B界面需要重写方法:
     *                                  @see SharedElementAble#sharedElementPositionChanged(int, int)
     *                                  @see com.actor.myandroidframework.activity.ActorBaseActivity#onBackPressedSharedElement(Intent, int, int)//在super.onBackPressed();前调用
     *                                  @see AppCompatActivity#startPostponedEnterTransition()//共享元素准备完后(图片加载完后), 开始延时共享动画
     *                                        //fragment中: getActivity().startPostponedEnterTransition();//开始延时共享动画
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public static void startActivity(AppCompatActivity activity,
                                     @NonNull SharedElementAble sharedElementAble,
                                     Intent intent,
                                     boolean isNeedUpdatePosition,
                                     BaseSharedElementCallback sharedElementCallback,
                                     @NonNull View... sharedElements) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && sharedElements.length > 0) {
            if (isNeedUpdatePosition) {
                intent.putExtra(BaseSharedElementCallback.EXTRA_IS_NEED_UPDATE_POSITION, true);
                if (sharedElementCallback == null) {
                    sharedElementCallback = new BaseSharedElementCallback(true, new BaseSharedElementCallback.OnSharedElementPositionChangedListener() {
                        @Override
                        public View onSharedElementPositionChanged(int oldPosition, int currentPosition) {
                            return sharedElementAble.sharedElementPositionChanged(oldPosition, currentPosition);
                        }
                    });
                    //设置A界面跳转到B界面的元素共享动画回调, 用于A界面position更新
                    activity.setExitSharedElementCallback(sharedElementCallback);
                }
            }
        }
//      //单个共享元素方式跳转
//        ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        ActivityUtils.startActivity(activity, intent, sharedElements);
    }

    /**
     * 共享元素方式跳转
     *
     * @param sharedElements 共享元素, 需要在xml或者java文件中设置TransitionName
     */
    public static void startActivityForResult(AppCompatActivity activity,
                                              @NonNull SharedElementAble sharedElementAble,
                                              Intent intent,
                                              int requestCode,
                                              boolean isNeedUpdatePosition,
                                              BaseSharedElementCallback sharedElementCallback,
                                              @NonNull View... sharedElements) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && sharedElements.length > 0) {
            if (isNeedUpdatePosition) {
                intent.putExtra(BaseSharedElementCallback.EXTRA_IS_NEED_UPDATE_POSITION, true);
                if (sharedElementCallback == null) {
                    sharedElementCallback = new BaseSharedElementCallback(true, new BaseSharedElementCallback.OnSharedElementPositionChangedListener() {
                        @Override
                        public View onSharedElementPositionChanged(int oldPosition, int currentPosition) {
                            return sharedElementAble.sharedElementPositionChanged(oldPosition, currentPosition);
                        }
                    });
                    //设置A界面跳转到B界面的元素共享动画回调, 用于A界面position更新
                    activity.setExitSharedElementCallback(sharedElementCallback);
                }
            }
        }
//      //单个共享元素方式跳转
//        ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        ActivityUtils.startActivityForResult(activity, intent, requestCode, sharedElements);
    }

    /**
     * B界面返回A界面
     *
     * @param data B界面setResult(RESULT_OK, data);返回的值, 即使A界面startActivity, 只要B界面setResult有值, 都能收到
     */
    public static void onActivityReenter(@NonNull SharedElementAble sharedElementAble,
                                         BaseSharedElementCallback sharedElementCallback,
                                         Intent data) {
        if (data == null) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int oldPosition = data.getIntExtra(BaseSharedElementCallback.EXTRA_START_POSITION, 0);
            int currentPosition = data.getIntExtra(BaseSharedElementCallback.EXTRA_CURRENT_POSITION, 0);
            if (sharedElementCallback != null) {
                sharedElementCallback.set(true, oldPosition, currentPosition);
            }
            if (oldPosition != currentPosition) {
                sharedElementAble.onSharedElementBacked(oldPosition, currentPosition);
            }
        }
    }
}
