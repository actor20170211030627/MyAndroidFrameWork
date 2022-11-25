package com.actor.myandroidframework.utils.sharedelement;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
// TODO: 2022/6/15 动画流程可能有问题 
public class SharedElementUtils {

    /**
     * 获取共享元素跳转回调, 一般在 onCreate(Bundle) 方法中调用
     * @param sharedElementAble Activity 需要实现这个接口
     * @return 共享元素跳转回调
     */
    public static BaseSharedElementCallback getSharedElementCallback(AppCompatActivity activity,
                                                                     @NonNull SharedElementAble sharedElementAble) {
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
     * @param intent 跳转页面的intent
     * @param isNeedUpdatePosition <pre>
     *          A界面跳转B界面再返回后, 是否需要更新A界面的position. <br />
     *          例: A界面: RecyclerView <--> B界面: ViewPager, 返回后更新A界面共享元素position <br />
     *          如果true, A界面需要重写2个方法: <br />
     *              {@link SharedElementA#sharedElementPositionChanged(int, int)} <br />
     *              {@link SharedElementA#onSharedElementBacked(int, int)} <br />
     *          如果true, B界面需要重写方法: <br />
     *              {@link SharedElementA#sharedElementPositionChanged(int, int)} <br />
     *              {@link AppCompatActivity#onBackPressed()}//在super.onBackPressed();前调用 <br />
     *              {@link AppCompatActivity#startPostponedEnterTransition()}//共享元素准备完后(图片加载完后), 开始延时共享动画 <br />
     *                  //fragment中: getActivity().startPostponedEnterTransition();//开始延时共享动画
     * </pre>
     *
     * @param sharedElementAble 如果A界面需要更新position, A界面 implements SharedElementA
     * @param sharedElementCallback 共享元素跳转回调
     * @param sharedElements 共享元素, 需要在xml或者java代码中设置TransitionName
     */
    public static void startActivity(AppCompatActivity activity,
                                     @NonNull Intent intent,
                                     boolean isNeedUpdatePosition,
                                     @Nullable SharedElementA sharedElementAble,
                                     @Nullable BaseSharedElementCallback sharedElementCallback,
                                     @NonNull View... sharedElements) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && sharedElements.length > 0) {
            if (isNeedUpdatePosition && sharedElementAble != null) {
                intent.putExtra(BaseSharedElementCallback.EXTRA_IS_NEED_UPDATE_POSITION, true);
                if (sharedElementCallback == null) {
                    sharedElementCallback = new BaseSharedElementCallback(true, new BaseSharedElementCallback.OnSharedElementPositionChangedListener() {
                        @Override
                        public View onSharedElementPositionChanged(int oldPosition, int currentPosition) {
                            return sharedElementAble.sharedElementPositionChanged(oldPosition, currentPosition);
                        }
                    });
                }
                //设置A界面跳转到B界面的元素共享动画回调, 用于A界面position更新
                activity.setExitSharedElementCallback(sharedElementCallback);
            }
        }
//      //单个共享元素方式跳转
//        ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        ActivityUtils.startActivity(activity, intent, sharedElements);
    }

    /**
     * 共享元素方式跳转, 其余参数见↑方法
     * @param requestCode 请求码
     */
    public static void startActivityForResult(AppCompatActivity activity,
                                              @NonNull Intent intent,
                                              int requestCode,
                                              boolean isNeedUpdatePosition,
                                              @Nullable SharedElementA sharedElementAble,
                                              @Nullable BaseSharedElementCallback sharedElementCallback,
                                              @NonNull View... sharedElements) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && sharedElements.length > 0) {
            if (isNeedUpdatePosition && sharedElementAble != null) {
                intent.putExtra(BaseSharedElementCallback.EXTRA_IS_NEED_UPDATE_POSITION, true);
                if (sharedElementCallback == null) {
                    sharedElementCallback = new BaseSharedElementCallback(true, new BaseSharedElementCallback.OnSharedElementPositionChangedListener() {
                        @Override
                        public View onSharedElementPositionChanged(int oldPosition, int currentPosition) {
                            return sharedElementAble.sharedElementPositionChanged(oldPosition, currentPosition);
                        }
                    });
                }
                //设置A界面跳转到B界面的元素共享动画回调, 用于A界面position更新
                activity.setExitSharedElementCallback(sharedElementCallback);
            }
        }
//      //单个共享元素方式跳转
//        ActivityOptions compat = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        ActivityUtils.startActivityForResult(activity, intent, requestCode, sharedElements);
    }

    /**
     * B界面返回A界面, A界面重写 onActivityReenter 方法
     *
     * @param data B界面setResult(RESULT_OK, data);返回的值, 即使A界面startActivity, 只要B界面setResult有值, 都能收到
     */
    public static void onActivityReenter(@NonNull SharedElementA sharedElementAble,
                                         @Nullable BaseSharedElementCallback sharedElementCallback,
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

    /**
     * 共享元素跳转, B界面返回A界面时, B界面 super.onBackPressed();之前调用这个方法
     *
     * @param sharedElementCallback 共享元素跳转回调
     * @param intent          用于返回A界面值的intent
     * @param oldPosition     从A界面跳过来时的position
     * @param currentPosition B界面现在的position, 用于A界面元素共享动画跳转到这个位置
     */
    public static void onBackPressedSharedElement(AppCompatActivity activity,
                                                  @Nullable BaseSharedElementCallback sharedElementCallback,
                                                  @Nullable Intent intent, int oldPosition, int currentPosition) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (sharedElementCallback != null) {
                sharedElementCallback.set(true, oldPosition, currentPosition);
                if (intent != null) {
                    intent.putExtra(BaseSharedElementCallback.EXTRA_START_POSITION, oldPosition);
                    intent.putExtra(BaseSharedElementCallback.EXTRA_CURRENT_POSITION, currentPosition);
                }
            }
        }
        activity.setResult(Activity.RESULT_OK, intent);
    }
}
