package com.actor.myandroidframework.utils;

import android.app.Application;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;

/**
 * Description: 整个项目所需的资源配置 <br />
 * Author     : ldf <br />
 * Date       : 2019/11/21 on 09:43
 *
 * @version 1.0
 */
public class ConfigUtils {

    public static final Application APPLICATION = Utils.getApp();

    /**
     * 当我们没在AndroidManifest.xml中设置其 debuggable="true" 属性时: <br />
     * <code>&lt;application android:debuggable="true" tools:ignore="HardcodedDebugMode"</code> <br />
     * <ol>
     *     <li>运行                              :  这种方式打包时其debug属性为true</li>
     *     <li>Build->Generate Signed APK release: 这种方式打包时其debug属性为法false</li>
     * </ol>
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     */
    public static final boolean IS_APP_DEBUG = AppUtils.isAppDebug();

    /**
     * 状态栏高度
     */
    public static final int STATUS_BAR_HEIGHT = BarUtils.getStatusBarHeight();

    /**
     * App 的屏幕宽度, 和屏幕宽度不是一个概念
     */
    public static final int APP_SCREEN_WIDTH = ScreenUtils.getAppScreenWidth();

    protected static final String EXCEPTION_FOR_ActorApplication = "EXCEPTION_FOR_ActorApplication";


    /**
     * 配置轮子哥的Log日志
     */
    public static void initAllKits(Application application) {
        //配置轮子哥的Log日志
        LogUtils.getConfig()
                .setLogSwitch(IS_APP_DEBUG)//是否能输出日志到 控制台/文件
                .setBorderSwitch(true)//是否打印边框
                .setConsoleSwitch(true)//是否能输出到 控制台
                .setLogHeadSwitch(true)//是否打印头(哪个文件哪一行, 点击能跳转相应文件)
                .setSingleTagSwitch(false)//日志是否在控制台开始位置输出, 默认true
                .setLog2FileSwitch(false);//是否能输出到 文件, 默认false

        //配置轮子哥吐司
        ToasterUtils.init(application);
    }

    /**
     * 如果是debug环境, 就不捕获异常, 直接打印在控制台
     * @see #getCrashExceptionInfoAndClear() 获取崩溃信息, 并清空
     * @param onCrashListener app崩溃监听
     */
    public static void initDefaultUncaughtExceptionHandler(boolean isDebugMode, @Nullable CrashUtils.OnCrashListener onCrashListener) {
        if (!isDebugMode) {
            CrashUtils.init(new CrashUtils.OnCrashListener() {
                @Override
                public void onCrash(CrashUtils.CrashInfo crashInfo) {
                    if (crashInfo == null) return;
                    //SPUtils 在发生异常的时候, 存储会回滚. 所以这儿用 CacheDiskUtils
                    String exception = MMKVUtils.getString(EXCEPTION_FOR_ActorApplication);
                    if (exception == null) {
                        MMKVUtils.putString(EXCEPTION_FOR_ActorApplication, crashInfo.toString());
                    } else {
                        if (exception.length() > 2 << 16) exception = "";//131 072
                        MMKVUtils.putString(EXCEPTION_FOR_ActorApplication, exception.concat("\n\n\n").concat(crashInfo.toString()));
                    }
                    if (onCrashListener != null) onCrashListener.onCrash(crashInfo);
                }
            });
        }
    }

    /**
     * 获取崩溃信息, 并清空信息
     */
    public static String getCrashExceptionInfoAndClear() {
        String string = MMKVUtils.getString(EXCEPTION_FOR_ActorApplication);
        MMKVUtils.remove(EXCEPTION_FOR_ActorApplication);
        return string;
    }

}
