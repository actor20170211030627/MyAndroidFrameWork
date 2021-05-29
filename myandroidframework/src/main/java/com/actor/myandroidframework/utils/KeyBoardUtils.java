package com.actor.myandroidframework.utils;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Description: 键盘工具类 https://blog.csdn.net/mynameishuangshuai/article/details/51567357
 *
 * 在 代码 或 AndroidManifest.xml 中设置: android:windowSoftInputMode="adjustResize|stateHidden"
 *
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : ldf
 * Date       : 2017/5/23 on 20:27
 * LastUpdate : 2019/04/18
 * Version    : v1.0.1
 * @deprecated 请使用更好的工具类: {@link com.blankj.utilcode.util.KeyboardUtils}
 */
@Deprecated
public class KeyBoardUtils {

    /**
     * 软键盘弹出时,别把布局顶上去 android:windowSoftInputMode="adjustPan|stateHidden"
     * 在onCreate方法的setContentView方法之前调用(之后也可以)
     * LinearLayout:Title会被顶出外面
     * ScrollView/RecyclerView/ListView:Title会被顶出外面
     */
    public static void keybordNoCoverView(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /**
     * 软键盘弹出时,布局不动/自动调整
     * 在onCreate方法的setContentView方法之前调用(之后也可以吧)
     * LinearLayout:如果EditText在下方,会被键盘遮盖
     * ScrollView/RecyclerView/ListView:EditText会弹到键盘上方,Title不会被弹出
     * 清单文件示例:android:windowSoftInputMode="stateHidden|adjustResize"
     */
    public static void keybordNotCoverView(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * 软键盘弹出时,布局不动.没什么用?
     * 在onCreate方法的setContentView方法之前调用(之后也可以吧)
     * LinearLayout:如果EditText在下方,会被键盘遮盖
     * ScrollView/RecyclerView/ListView:和上面一致
     */
    private static void keybordNotCoverView2(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    public static void otherMethod(Activity activity) {
        //0:未指定
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
        //0:没有指定状态，系统将选择一个合适的状态或依赖于主题的设置
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        //1:软键盘将一直保持在上一个activity里的状态，无论是隐藏还是显示?
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        //2:用户选择activity时，软键盘总是被隐藏
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //3:当该Activity主窗口获取焦点时，软键盘也总是被隐藏的
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //4:软键盘通常是可见的
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        //5:用户选择activity时，软键盘总是显示的状态
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //15:
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);
        //16:该Activity总是调整屏幕的大小以便留出软键盘的空间(布局不顶上去?)
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //32:当前窗口的内容将自动移动以便当前焦点从不被键盘覆盖和用户能总是看到输入内容的部分(布局顶上去)
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //48:键盘弹出时,布局不变化
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        //240:
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
        //256:
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION);
        //512:
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED);
    }
}
