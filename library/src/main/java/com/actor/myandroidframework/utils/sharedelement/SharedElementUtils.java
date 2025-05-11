package com.actor.myandroidframework.utils.sharedelement;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.ActivityUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * description: 元素共享跳转工具类, 可参考<a href="https://blog.csdn.net/Gaga246/article/details/126936866" target="_blank">Android Activity共享元素动画分析</a>,
 *              <a href="https://blog.csdn.net/u013077428/article/details/126484571" target="_blank">android共享元素动画在Android10以上异常的终极解决方案</a>, <br />
 *              {@link null 注意：}Android5.0才开始支持共享元素动画
 * <ul>
 *     <li>
 *         所谓Activity共享元素动画，就是从ActivityA跳转到ActivityB,
 *         通过控制某些元素(View)从ActivityA开始帧的位置跳转到ActivityB 结束帧的位置，应用过度动画。
 *     </li>
 *     <li>
 *         其动画核心是使用的 {@link Transition} 记录共享元素的开始帧、结束帧，
 *         然后使用 {@link TransitionManager} 过度动画管理类调用
 *         {@link TransitionManager#beginDelayedTransition(ViewGroup)} 方法, 应用过度动画。
 *     </li>
 *     <li>
 *         {@link TransitionManager}有两个比较重要的类{@link Scene}(场景)和{@link Transition}(过渡)
 *     </li>
 * </ul>
 * <br />
 *
 * {@link null 一、页面A 跳转 页面B, 然后再返回的流程:}
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th nowrap="nowrap">页面名</th>
 *         <th align="center">方法</th>
 *         <th>说明</th>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link Activity#setExitSharedElementCallback(SharedElementCallback) setExitSharedElementCallback(SharedElementCallback)}</td>
 *         <td>A跳B前的回调, 需要填充共享元素({@link null 如果你A界面的共享元素view&transitionName一直不变, 可不用写这个监听})</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link Activity#onCreate(Bundle) onCreate(Bundle)}</td>
 *         <td>进入B页面并走了onCreate()方法</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td nowrap="nowrap">{@link Activity#setEnterSharedElementCallback(SharedElementCallback) setEnterSharedElementCallback(SharedElementCallback)}</td>
 *         <td>在B页面, 需要填充共享元素({@link null 如果你B界面的共享元素view&transitionName一直不变, 可不用写这个监听})</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link Activity#onBackPressed() onBackPressed()}</td>
 *         <td>在B页面按了返回({@link null if共享元素view或transitionName发生了改变, 可将改变信息返回给上一页})</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link Activity#setEnterSharedElementCallback(SharedElementCallback) setEnterSharedElementCallback(SharedElementCallback)}</td>
 *         <td>在B页面, 需要填充"返回的"共享元素({@link null 如果你B界面的共享元素view&transitionName一直不变, 可不用写这个监听})</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link Activity#onActivityReenter(int, Intent) onActivityReenter(int, Intent)}</td>
 *         <td>返回A页面后, 先回调这个方法. 你可以从这儿获取第2页面返回的数据({@link null if页面B的transitionName发生了改变, 导致A页面元素需要更新, 可从这儿获取改变的信息})</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link Activity#setExitSharedElementCallback(SharedElementCallback) setExitSharedElementCallback(SharedElementCallback)}</td>
 *         <td>B返回A后的回调, 需要填充共享元素({@link null 如果你A界面的共享元素view&transitionName一直不变, 可不用写这个监听})</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link Activity#onActivityResult(int, int, Intent) onActivityResult(int, int, Intent)}</td>
 *         <td>再走A页面的这个方法(可从这儿接受从B返回的其它信息)</td>
 *     </tr>
 * </table>
 *
 * @author : ldf
 * date       : 2021/10/16 on 17
 * @version 1.0
 */
public class SharedElementUtils {

    //额外数据
    protected static Object instantExtra;

    /**
     * 在目标Activity中，清除目标 Activity 或 Fragment 的进入&退出过渡动画。<br />
     * 默认有1个渐变动画, 清除后感觉体验不好. <br />
     * 可以在 onCreate 方法调用的setContentView()方法之后调用本方法.
     */
    public static void cleanTransitionInDestinationActivity(Window window) {
        if (window == null) return;
        //清除当前 Activity 或 Fragment 的进入过渡动画。传入 null 时，意味着取消默认的进入过渡动画效果，这样在 Activity 或 Fragment 启动时不会有预设的进入动画。
        window.setEnterTransition((Transition) null);
        //清除当前 Activity 或 Fragment 的退出过渡动画。传入 null 会取消默认的退出动画效果，在 Activity 或 Fragment 关闭时不会有预设的退出动画。
        window.setExitTransition((Transition) null);

        //不要清除共享动画, 本来就在用, 清除了看起来卡顿!
//        window.setSharedElementEnterTransition((Transition) null);
//        window.setSharedElementExitTransition((Transition) null);
    }


    /**
     * 设置共享元素动画进入时的Transition
     * @param transition 过渡动画, 例:
     * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *     <tr>
     *         <th>类型</th>
     *         <th align="center">说明</th>
     *     </tr>
     *     <tr>
     *         <td nowrap="nowrap">{@link android.transition.ChangeImageTransform ChangeImageTransform}</td>
     *         <td>
     *             处理图片尺寸和缩放类型（ScaleType）变化 的过渡动画类。它的作用是通过动画平滑地过渡两个界面中共享的 ImageView 的以下属性变化：<br />
     *             <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *                 <tr>
     *                     <th>处理的变化</th>
     *                     <th>动画效果</th>
     *                 </tr>
     *                 <tr>
     *                     <td>ScaleType 改变（如 centerCrop ↔ fitCenter）</td>
     *                     <td>平滑过渡不同的裁剪/缩放模式</td>
     *                 </tr>
     *                 <tr>
     *                     <td>ImageView 的矩阵变换（缩放、平移、旋转）</td>
     *                     <td>动画过渡图片的矩阵变换状态</td>
     *                 </tr>
     *                 <tr>
     *                     <td>ImageView 的尺寸变化（如宽高调整）</td>
     *                     <td>	配合 ChangeBounds 实现完整动画</td>
     *                 </tr>
     *             </table>
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>{@link android.transition.ChangeBounds ChangeBounds}</td>
     *         <td>视图的布局位置或大小发生改变时</td>
     *     </tr>
     *     <tr>
     *         <td>{@link android.transition.ChangeClipBounds ChangeClipBounds}</td>
     *         <td>视图的裁剪区域发生改变时（如圆形头像变为方形）</td>
     *     </tr>
     *     <tr>
     *         <td>{@link android.transition.ChangeTransform ChangeTransform}</td>
     *         <td>视图发生了平移、旋转或非矩阵缩放变换时</td>
     *     </tr>
     *     <tr>
     *         <td>{@link android.transition.ChangeScroll ChangeScroll}</td>
     *         <td>视图的滚动状态发生改变时</td>
     *     </tr>
     *     <tr>
     *         <td>{@link android.transition.TransitionSet TransitionSet}</td>
     *         <td>需要同时处理位置、尺寸、裁剪等多种变化</td>
     *     </tr>
     * </table>
     */
    public static void setSharedElementEnterTransition(Window window, @Nullable Transition transition) {
        if (window == null) return;
        window.setSharedElementEnterTransition(transition);
    }

    public static void setSharedElementReturnTransition(Window window, @Nullable Transition transition) {
        if (window == null) return;
        window.setSharedElementReturnTransition(transition);
    }

    /**
     * 推迟 Activity 的进入过渡动画。
     * 在默认情况下，当一个 Activity 或者 Fragment 启动时，进入过渡动画会立即开始。
     * 不过，要是你需要在过渡动画开始前完成某些异步操作（像加载图片、数据等），就可以调用 postponeEnterTransition() 来推迟动画的启动，直到你准备好。
     * 使用场景：在需要进行一些耗时操作，并且希望在操作完成后再开始过渡动画时使用。
     * 例如，当你要从网络加载一张图片作为共享元素，在图片加载完成之前不想让过渡动画开始，这时就可以调用该方法。
     */
    public static void postponeEnterTransition(Activity activity) {
        if (activity == null) return;
        ActivityCompat.postponeEnterTransition(activity);
    }

    /**
     * 推迟Fragment的进入过渡动画。
     */
    public static void postponeEnterTransition(Fragment fragment) {
        if (fragment == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.postponeEnterTransition();
        }
    }

    /**
     * 推迟 Fragment 的进入过渡动画。如果传入(2, TimeUnit.SECONDS); 表示将 fragment 的进入过渡动画推迟 2 秒。
     * 在这 2 秒内，过渡动画不会启动，直到 2 秒后或者手动调用 {@link #startPostponedEnterTransition(Fragment)}方法来启动过渡动画。
     * @param duration 推迟过渡动画的具体时长。它指定了从调用本方法开始，到过渡动画可以被启动的这段时间间隔。
     * @param timeUnit 时间单位的枚举类。该参数用于指定 duration 的时间单位，例如毫秒、秒、分钟等。例: <br />
     *                 {@link TimeUnit#MILLISECONDS}(毫秒), {@link TimeUnit#SECONDS}(秒)
     *        <br /> <br />
     */
    public static void postponeEnterTransition(Fragment fragment, long duration, @NonNull TimeUnit timeUnit) {
        if (fragment == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.postponeEnterTransition(duration, timeUnit);
        }
    }



    /**
     * 启动之前被推迟的进入过渡动画。
     * 当你调用 postponeEnterTransition() 推迟了过渡动画之后，在完成必要的操作（如数据加载、视图初始化等）后，就可以调用 startPostponedEnterTransition() 来开始过渡动画。
     */
    public static void startPostponedEnterTransition(Activity activity) {
        if (activity == null) return;
        ActivityCompat.startPostponedEnterTransition(activity);
    }

    /**
     * 启动之前被推迟的进入过渡动画。<br />
     * {@link 注意:}
     * if是在Activity中暂停的动画, 就要调用{@link #startPostponedEnterTransition(Activity)} 方法继续, 而不是调用本方法!
     */
    public static void startPostponedEnterTransition(Fragment fragment) {
        if (fragment == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.startPostponedEnterTransition();
        }
    }


    /**
     * 共享元素跳转
     * @param sharedElements 共享元素, 务必先设置transitionName
     */
    public static void startActivity(FragmentActivity activity, Intent intent, @Nullable View... sharedElements) {
        //需要重置ExitSharedElementCallback, 否则如果已经设置了的话, 会造成bug
        activity.setExitSharedElementCallback((BaseSharedElementCallback) null);
        ActivityUtils.startActivity(activity, intent, sharedElements);
    }

    /**
     * 共享元素跳转
     * @param sharedElements 共享元素, 务必先设置transitionName
     */
    public static void startActivity(@NonNull Fragment fragment, Intent intent, @Nullable View... sharedElements) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
//            fragment.setExitSharedElementCallback((BaseSharedElementCallback) null);
            //需要重置ExitSharedElementCallback, 否则如果已经设置了的话, 会造成bug
            activity.setExitSharedElementCallback((BaseSharedElementCallback) null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && activity != null && sharedElements != null && sharedElements.length > 0) {
            @SuppressWarnings("unchecked")
            Pair<View, String>[] pairs = new Pair[sharedElements.length];
            for (int i = 0; i < sharedElements.length; i++) {
                pairs[i] = Pair.create(sharedElements[i], sharedElements[i].getTransitionName());
            }
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs).toBundle();
            fragment.startActivity(intent, bundle);
        } else {
            fragment.startActivity(intent);
        }
    }



    /**
     * 共享元素方式跳转, {@link 注意:}
     * <ol>
     *     <li>if你元素共享跳转前后2个页面的共享元素views & 共享元素名称都不改变, 建议调用更简单的{@link #startActivityForResult(AppCompatActivity, Intent, int, View...)}方法.</li>
     *     <li>
     *         调用这个方法需要在页面A重写{@link Activity#onActivityReenter(int, Intent) onActivityReenter(int, Intent)} 用于获取你自己返回的元素改变信息,
     *         示例: <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/SharedElementActivity.java" target="_blank">SharedElementActivity.java</a>
     *     </li>
     * </ol>
     * @param intent 跳转B页面的Intent
     * @param requestCode 请求码
     * @param exitSharedElementCallback 共享元素跳转回调
     */
    public static void startActivityForResult(@NonNull AppCompatActivity activity,
                                              @NonNull Intent intent, int requestCode,
                                              @Nullable BaseSharedElementCallback exitSharedElementCallback) {
        activity.setExitSharedElementCallback(exitSharedElementCallback);
        if (exitSharedElementCallback == null) {
            ActivityUtils.startActivityForResult(activity, intent, requestCode);
        } else {
            //必须传个view, 否则不能触发 exitSharedElementCallback 里的方法
            Space sharedElement = new Space(activity);
            sharedElement.setTransitionName("");
            ActivityUtils.startActivityForResult(activity, intent, requestCode, sharedElement);
        }
    }

    /**
     * 元素共享跳转, {@link 注意:}
     * <ol>
     *     <li>
     *         fragment跳转activity再返回后, 会先走当前fragment对应的Activity的 {@link Activity#onActivityReenter(int, Intent) onActivityReenter(int, Intent)} 方法,
     *         然后再走Activity的 {@link Activity#onActivityResult(int, int, Intent) onActivityResult(int, int, Intent)} 方法,
     *         fragment的 {@link Fragment#onActivityResult(int, int, Intent) onActivityResult(int, int, Intent)} 方法,
     *         以及fragment的 {@link Fragment#onResume() onResume()} 方法!
     *     </li>
     *     <li>
     *         所以: 如果你要在activity返回fragment的{@link 共享动画执行完成前}获取数据并更新fragment的UI的话(比如更新元素共享图片),
     *         请在fragment的
     *         {@link BaseSharedElementCallback#onMapSharedElements(List, Map) exitSharedElementCallback.onMapSharedElements(List, Map)} <br />
     *         或 {@link BaseSharedElementCallback#onSharedElementsArrived(List, List, androidx.core.app.SharedElementCallback.OnSharedElementsReadyListener) exitSharedElementCallback.onSharedElementsArrived(List, List, OnSharedElementsReadyListener)} <br />
     *         中调用{@link #getInstantExtra() SharedElementUtils.getInstantExtra()}获取并判断数据, 然后更新Fragment里的UI, 示例: <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/fragment/SharedElementFragment.java" target="_blank">SharedElementFragment.java</a>
     *     </li>
     *     <li>if不是必须在activity返回fragment的{@link 共享动画执行完成前}更新fragment的UI的话, 可直接在 {@link com.actor.myandroidframework.bean.OnActivityCallback OnActivityCallback callback} 这个回调中获取数据再更新UI.</li>
     * </ol>
     * @param requestCode 请求码
     * @param exitSharedElementCallback 跳转前后回调, 回来后需要你自己在<code>callback</code>中获取元素改变信息
     */
    public static void startActivityForResult(@NonNull Fragment fragment,
                                              @NonNull Intent intent, int requestCode,
                                              @Nullable BaseSharedElementCallback exitSharedElementCallback) {
        //设置在fragment里面不会回调, 难道是我设置的不对??
//        fragment.setExitSharedElementCallback(exitSharedElementCallback);
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
            activity.setExitSharedElementCallback(exitSharedElementCallback);
        } else {
            LogUtils.errorFormat("%s.getActivity() = null!!!", fragment);
        }
        if (exitSharedElementCallback == null) {
            ActivityUtils.startActivityForResult(fragment, intent, requestCode);
        } else {
            //必须传个view, 否则不能触发 exitSharedElementCallback 里的方法
            Space sharedElement = new Space(fragment.getContext());
            sharedElement.setTransitionName("");
            ActivityUtils.startActivityForResult(fragment, intent, requestCode, sharedElement);
        }
    }

    /**
     * 共享元素方式跳转
     * @param intent 跳转B页面的Intent
     * @param requestCode 请求码
     * @param sharedElements 共享元素, 务必先设置transitionName
     */
    public static void startActivityForResult(@NonNull AppCompatActivity activity, @NonNull Intent intent,
                                              int requestCode, @Nullable View... sharedElements) {
        activity.setExitSharedElementCallback((BaseSharedElementCallback) null);
        ActivityUtils.startActivityForResult(activity, intent, requestCode, sharedElements);
    }

    /**
     * 共享元素方式跳转
     * @param intent 跳转B页面的Intent
     * @param requestCode 请求码
     * @param sharedElements 共享元素, 务必先设置transitionName
     */
    public static void startActivityForResult(@NonNull Fragment fragment, @NonNull Intent intent,
                                              int requestCode, View... sharedElements) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
//            fragment.setExitSharedElementCallback((BaseSharedElementCallback) null);
            activity.setExitSharedElementCallback((BaseSharedElementCallback) null);
        } else {
            LogUtils.errorFormat("%s.getActivity() = null!!!", fragment);
        }
        ActivityUtils.startActivityForResult(fragment, intent, requestCode, sharedElements);
    }



    /**
     * 共享元素退出第2个Activity, 回到前1个Activity, 第2个Activity代码示例:
     * <pre>
     * <code>@</code>Override
     * public void onBackPressed() {
     *     //{@link null super.onBackPressed(); //注意: 不要再写这句, 否则返回可能看不到元素共享效果}
     *     Intent intent = new Intent().putExtra("position", position);
     *     SharedElementUtils.finishAfterTransition(this, enterSharedElementCallback, RESULT_OK, intent);
     * }
     * </pre>
     * @param activity 要退出的Activity
     * @param enterSharedElementCallback 要退出Activity的共享元素组装
     * @param resultCode 返回码: {@link Activity#RESULT_OK}, {@link Activity#RESULT_CANCELED}, {@link Activity#RESULT_FIRST_USER} 等
     * @param resultIntent if第2个Activity要返会数据回上一个Activity, 就传入intent, 否则传null
     * @param instantExtra 暂存数据, if你是从<b>fragment</b>跳到这边来, 才{@link 可能}传这个数据:
     *                     <ol>
     *                         <li>if你从Fragment跳到activity这边来,
     *                         且现在退出activity返回fragment后有数据需要在{@link 元素共享动画执行完成前}响应到Fragment(比如要在元素共享动画完成前更新Fragment中的元素共享图片),
     *                         由于返回后Fragment没有activity类似的{@link Activity#onActivityReenter(int, Intent) onActivityReenter(int, Intent)}方法可以在元素共享动画执行完成前就能获取到返回数据,
     *                         所以就需要传入这个参数. <br />
     *                         然后你再在fragment的{@link BaseSharedElementCallback#onMapSharedElements(List, Map) exitSharedElementCallback.onMapSharedElements(List, Map)} <br />
     *                         或 {@link BaseSharedElementCallback#onSharedElementsArrived(List, List, androidx.core.app.SharedElementCallback.OnSharedElementsReadyListener) exitSharedElementCallback.onSharedElementsArrived(List, List, OnSharedElementsReadyListener)} <br />
     *                         中调用 {@link #getInstantExtra() SharedElementUtils.getInstantExtra()} 方法获取并判断数据, 在动画执行完成前更新数据.
     *                         </li>
     *                         <li>if你从activity返回fragment后, fragment那边不需要在元素共享动画执行前更新数据, 这个参数传null</li>
     *                     </ol>
     *
     */
    public static void finishAfterTransition(@NonNull FragmentActivity activity,
                                             @Nullable BaseSharedElementCallback enterSharedElementCallback,
                                             int resultCode, @Nullable Intent resultIntent,
                                             @Nullable Object instantExtra) {
        activity.setEnterSharedElementCallback(enterSharedElementCallback);
        //不管intent是否=null, 都会在上1个Activity回调 onActivityResult()
        activity.setResult(resultCode, resultIntent);
        setInstantExtra(instantExtra);
        /**
         * 触发返回动画
         * @see androidx.fragment.app.FragmentActivity#supportFinishAfterTransition()
         */
        ActivityCompat.finishAfterTransition(activity);
    }

    /**
     * 获取瞬时数据
     * @param resetValue 获取数据后, 是否重置数据
     */
    @Nullable
    public static Object getInstantExtra(boolean resetValue) {
        Object instantExtra = SharedElementUtils.instantExtra;
        if (resetValue) SharedElementUtils.instantExtra = null;
        return instantExtra;
    }

    /**
     * 获取瞬时数据
     * @param resetValue 获取数据后, 是否重置数据
     * @param defaultValue 默认值. if没有没有值就返回默认值
     */
    @Nullable
    protected static <T> T getInstantExtra(boolean resetValue, @Nullable T defaultValue) {
        Object instantExtra = SharedElementUtils.instantExtra;
        if (resetValue) SharedElementUtils.instantExtra = null;
        if (instantExtra == null) return defaultValue;

        // TODO: 转换还是崩溃ClassCastException
        try {
            T result = (T) instantExtra;
            return result;
        }
        catch (ClassCastException e) {
            LogUtils.error("瞬时数据转换失败:", e);
            return defaultValue;
        }
//        catch (Error e) {
//            LogUtils.error("瞬时数据转换失败:", e);
//            return defaultValue;
//        }
//        catch (Throwable e) {
//            LogUtils.error("瞬时数据转换失败:", e);
//            return defaultValue;
//        }
    }

    protected static void setInstantExtra(@Nullable Object instantExtra) {
        SharedElementUtils.instantExtra = instantExtra;
    }
}
