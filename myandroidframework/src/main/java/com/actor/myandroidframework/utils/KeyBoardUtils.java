package com.actor.myandroidframework.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

/**
 * Description: 键盘工具类 https://blog.csdn.net/mynameishuangshuai/article/details/51567357
 *
 * 在 代码 或 AndroidManifest.xml 中设置: android:windowSoftInputMode="adjustResize|stateHidden"
 *
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2017/5/23 on 20:27
 * LastUpdate : 2019/04/18
 * Version    : v1.0.1
 * @deprecated 请使用更好的工具类: {@link com.blankj.utilcode.util.KeyboardUtils}
 */
@Deprecated
public class KeyBoardUtils {
    /**
     * 显示/隐藏软键盘
     * @param editText 必须是edittext,否则不起作用
     * @param isShow 是否显示软键盘
     */
    public static void showOrHideSoftInput(@NonNull EditText editText, boolean isShow) {
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.requestFocus();//必须设置,否则如果焦点不在这个view的话,输入法弹不出来
        if (isShow) {
            imm.showSoftInput(editText, 0);//InputMethodManager.SHOW_FORCED=2也可以
            //imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);//没试过
//            imm.showSoftInputFromInputMethod(editText.getWindowToken(), 0);//这种方法弹不出来
        } else {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//            if ( imm.isActive( ) ) {//这种也可以
//                imm.hideSoftInputFromWindow(editText.getApplicationWindowToken( ) , 0 );
//            }
//            imm.hideSoftInputFromInputMethod(editText.getWindowToken(), 0);//这种方法不能隐藏
        }
    }

    public static boolean hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 只能在Activity中
     */
    public static boolean hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            IBinder windowToken = activity.getCurrentFocus().getWindowToken();
            if (windowToken != null) {
                return imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return false;
    }

    /**
     * 切换输入法显示/隐藏,这儿其实可以不强制是EditText
     * @param context 必传
     * @param editText 可以为null
     */
    public static void toggleSoftInput(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//这种方法也可以
        if (editText != null) {
            editText.requestFocus();//可不要
            imm.toggleSoftInputFromWindow(editText.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

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
