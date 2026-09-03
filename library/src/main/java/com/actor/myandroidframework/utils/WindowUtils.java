package com.actor.myandroidframework.utils;

import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.StyleRes;
import androidx.core.view.GravityCompat;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;

/**
 * description: Window工具类
 * company    :
 *
 * @author : ldf
 * date       : 2026/9/2 on 17
 * @version 1.0
 */
public class WindowUtils {

    /**
     * 设置Widow宽度
     * @param width 宽度, 单位px
     */
    public static boolean setWidth(Window window, int width) {
        if (window == null) return false;
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = width;
        window.setAttributes(attributes);
        return true;
    }

    /**
     * 设置Widow高度
     * @param height 高度, 单位px
     */
    public static boolean setHeight(Window window, int height) {
        if (window == null) return false;
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.height = height;
        window.setAttributes(attributes);
        return true;
    }

    /**
     * 设置 Window 根据 {@link Gravity} 不同而设置的偏移量
     * @param xOffset x方向偏移量
     * @param yOffset y方向偏移量
     *                <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *                    <tr>
     *                        <th align="center">Gravity</th>
     *                        <th align="center">xOffset 的作用<br />相对距离</th>
     *                        <th align="center">yOffset 的作用<br />相对距离</th>
     *                        <th align="center">说明</th>
     *                    </tr>
     *                    <tr>
     *                        <td>{@link Gravity#CENTER}</td>
     *                        <td>窗口<b>左边</b>⇆<b>屏幕左边</b></td>
     *                        <td align="center">窗口<b>垂直中心</b><br />⇅<br /><b>屏幕垂直中心</b>的y方向距离</td>
     *                    </tr>
     *                    <tr>
     *                        <td>{@link Gravity#LEFT}</td>
     *                        <td>窗口<b>左边</b>⇆<b>屏幕左边</b></td>
     *                        <td align="center">窗口<b>垂直中心</b><br />⇅<br /><b>屏幕垂直中心</b>的y方向距离</td>
     *                        <td>xOffset 负值无效</td>
     *                    </tr>
     *                    <tr>
     *                        <td>{@link Gravity#TOP}</td>
     *                        <td>窗口<b>左边</b>⇆<b>屏幕左边</b></td>
     *                        <td align="center">窗口<b>顶边</b><br />⇅<br /><b>屏幕顶边</b>的距离</td>
     *                        <td>yOffset 负值无效</td>
     *                    </tr>
     *                    <tr>
     *                        <td>{@link Gravity#RIGHT}</td>
     *                        <td>窗口<b>右边</b>⇆<b>屏幕右边</b></td>
     *                        <td align="center">窗口<b>垂直中心</b><br />⇅<br /><b>屏幕垂直中心</b>的y方向距离</td>
     *                        <td>xOffset 负值无效</td>
     *                    </tr>
     *                    <tr>
     *                        <td>{@link Gravity#BOTTOM}</td>
     *                        <td>窗口<b>左边</b>⇆<b>屏幕左边</b></td>
     *                        <td align="center">窗口<b>底边</b><br />⇅<br /><b>屏幕底边</b>的距离</td>
     *                        <td>yOffset 负值无效</td>
     *                    </tr>
     *                </table>
     */
    public static boolean setOffset(Window window, int xOffset, int yOffset) {
        if (window == null) return false;
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = xOffset;
        attributes.y = yOffset;
        window.setAttributes(attributes);
        return true;
    }

    /**
     * 设置重心
     * @param gravity 重心: <br />
     *        &emsp;&emsp; {@link Gravity#CENTER}(默认), {@link Gravity#LEFT}, {@link Gravity#TOP}, {@link Gravity#RIGHT}, {@link Gravity#BOTTOM} <br />
     *        &emsp;&emsp; {@link GravityCompat#START}, {@link GravityCompat#END}
     */
    public static boolean setGravity(Window window, int gravity) {
        if (window == null) return false;
        window.setGravity(gravity);
        return true;
    }

    /**
     * 设置动画
     * @param windowAnimations Window显示/隐藏动画
     */
    public static boolean setWindowAnimations(Window window, @StyleRes int windowAnimations) {
        if (window == null) return false;
        window.setWindowAnimations(windowAnimations);
        return true;
    }

    /**
     * 设置Window所有内容后面，覆盖一层半透明的黑色遮罩（调光效果）。
     * @param isDimEnable 是否变暗
     */
    public static boolean setDimEnable(Window window, boolean isDimEnable) {
        if (window == null) return false;
        if (isDimEnable) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        return true;
    }

    /**
     * 设置点击穿透
     * @param isClickThrough 是否点击穿透
     */
    public static boolean setClickThrough(Window window, boolean isClickThrough) {
        if (window == null) return false;
        if (isClickThrough) {
            //将允许对话框外的事件被发送到后面的视图
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
//            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            /**
             * 允许对话框在被触摸时接收到外部的触摸事件, 示例代码:
             * window.getDecorView().setOnTouchListener((v, event) -> {
             *     if (event.getAction() == MotionEvent.ACTION_OUTSIDE) { }
             *     return false;
             * });
             */
            window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.clearFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        }
        return true;
    }

    /**
     * 判断Window是否显示状态栏
     */
    public static boolean isStatusBarVisible(Window window) {
        if (window == null) return false;
        View decorView = window.getDecorView();

        // 1. 先检查系统 UI 标志位（快速判断是否明确隐藏）
        int visibility = decorView.getSystemUiVisibility();
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) return false; // 状态栏明确隐藏

        // 3. 低版本兼容, 从 Android 6.0(API Level 23) 开始，可以使用 WindowInsets 判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //if DecorView 尚未完全附着（attach）到窗口上, insets = null
            WindowInsets insets = decorView.getRootWindowInsets();
            // 2. 使用 WindowInsets 精确检测, Android 11+ (API Level 30) 有官方 API 直接判断可见性
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 直接调用 isVisible 判断状态栏是否可见（这是最准确的方式）
                if (insets != null) return insets.isVisible(WindowInsets.Type.statusBars());
            } else {
                // 如果没设置隐藏标志，但顶部系统窗口插入高度 > 0，说明状态栏占用了空间（通常代表可见）
                if (insets != null) return insets.getSystemWindowInsetTop() > 0;
            }
        }
        // 检查窗口标志（适用于较早的隐藏方式）
        boolean flagNotFullscreen = (window.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0;
        // 注意：如果应用了 SYSTEM_UI_FLAG_IMMERSIVE_STICKY，通常也同时设置了 FULLSCREEN，但以防万一，也可以单独判断，但通常不必要。
        // 状态栏可见的条件：没有任何隐藏标志生效
        return flagNotFullscreen;
    }

    /**
     * 判断Window是否显示导航栏
     */
    public static boolean isNavigationBarVisible(Window window) {
        if (window == null) return false;
        //如果它为 true，直接返回
        boolean sdkResult = BarUtils.isNavBarVisible(window);
        if (sdkResult) return true;
        //上方方法经常误判
        View decorView = window.getDecorView();
        int uiFlags = decorView.getSystemUiVisibility();

        // 如果明确设置了 HIDE_NAVIGATION，则肯定不可见
        if ((uiFlags & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0) return false;

        // 从 Android 6.0(API Level 23) 开始，可以使用 WindowInsets 判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //if DecorView 尚未完全附着（attach）到窗口上, insets = null
            WindowInsets insets = decorView.getRootWindowInsets();
            // Android 11+ (API Level 30) 有官方 API 直接判断可见性
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (insets != null) return insets.isVisible(WindowInsets.Type.navigationBars());
            } else {
                // 兼容低版本：如果底部系统窗口高度 > 0，且没有隐藏标志，则认为可见
                if (insets != null) {
                    int bottomInset = insets.getSystemWindowInsetBottom();
                    if (bottomInset > 0) return true;
                }
            }
        }
        return false;
    }

    /**
     * 将Window 画进 状态栏 & 导航栏, 在 {@link Window.Callback#onAttachedToWindow()} 之后调用
     * @param isDrawIntoStatusBar 是否画进状态栏
     * @param isHideStatusBar 是否隐藏状态栏, 只有 isDrawIntoStatusBar = true 的时候才管用
     * @param isDrawIntoNavigationBar 是否画进导航栏
     * @param isHideNavigationBar 是否隐藏导航栏, 只有 isDrawIntoNavigationBar = true 的时候才管用
     * @param yOffset window 框口当前设置的y轴方向偏移
     * @return
     */
    public static boolean drawIntoStatusBarAndNavigationBar(Window window,
                                                            boolean isDrawIntoStatusBar,
                                                            boolean isHideStatusBar,
                                                            boolean isDrawIntoNavigationBar,
                                                            boolean isHideNavigationBar,
                                                            int yOffset) {
        if (window == null) return false;
        //window.DecorView(com.android.internal.policy.DecorView extends FrameLayout) -> LinearLayout -> [ViewStub, FrameLayout -> 自己写的View], ∴frameLayout is FrameLayout
        ViewGroup frameLayout = window.findViewById(android.R.id.content);

        // 基础环境初始化（清空干扰标志，设置必需标志）
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        // 必须添加下面这一行，让窗口可以覆盖到导航栏后方
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        // 必须添加，使 setStatusBarColor 生效
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // 2. 窗口布局始终覆盖状态栏和导航栏（因为浮动窗口下它们会联动，索性都覆盖）
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        //隐藏系统栏
        if (isDrawIntoStatusBar && isHideStatusBar) {
            visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        if (isDrawIntoNavigationBar && isHideNavigationBar) {
            visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        // 强制刷新
        window.getDecorView().setSystemUiVisibility(visibility);
//        window.getDecorView().requestApplyInsets();

        // ---------- 4. 设置系统栏颜色 ----------
        if (isDrawIntoStatusBar) {
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            TypedValue tv = new TypedValue();
            ConfigUtils.APPLICATION.getTheme().resolveAttribute(android.R.attr.statusBarColor, tv, true);
            window.setStatusBarColor(tv.data);
        }

        if (isDrawIntoNavigationBar) {
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            TypedValue tv = new TypedValue();
            ConfigUtils.APPLICATION.getTheme().resolveAttribute(android.R.attr.navigationBarColor, tv, true);
            window.setNavigationBarColor(tv.data);
        }

        WindowManager.LayoutParams params = window.getAttributes();
        boolean drawIntoStatusBar = isDrawIntoStatusBar || !isStatusBarVisible(window);
        boolean drawIntoNavigationBar = (isDrawIntoNavigationBar || !isNavigationBarVisible(window)) && ScreenUtils.isPortrait();
        if (window.getAttributes().height == WindowManager.LayoutParams.MATCH_PARENT) {
            int top = drawIntoStatusBar ? 0 : BarUtils.getStatusBarHeight();
            int bottom = drawIntoNavigationBar ? 0 : BarUtils.getNavBarHeight();
            // 设置内边距，将内容“挤出”系统栏区域
            frameLayout.setPadding(0, top, 0, bottom);
            params.y = yOffset;
        } else {
            frameLayout.setPadding(0, 0, 0, 0);
            if ((window.getAttributes().gravity  & Gravity.TOP) == Gravity.TOP) {
                params.y = drawIntoStatusBar ? 0 : yOffset + BarUtils.getStatusBarHeight();
            } else if ((window.getAttributes().gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                params.y = drawIntoNavigationBar ? 0 : yOffset + BarUtils.getNavBarHeight();
            } else params.y = yOffset;
        }
        window.setAttributes(params);
        return false;
    }
}
