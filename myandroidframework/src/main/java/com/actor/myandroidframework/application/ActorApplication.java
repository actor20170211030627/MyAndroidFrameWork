package com.actor.myandroidframework.application;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.actor.myandroidframework.utils.SPUtils;

/**
 * 自定义的Application继承本类, 需要在清单文件中注册
 */
public abstract class ActorApplication extends Application/* implements Thread.UncaughtExceptionHandler*/ {

    public static ActorApplication instance;
    public        boolean          isDebugMode = false;//用于配置"正式环境"的isDebug的值,★★★注意:上线前一定要改成false★★★
    private static final String    EXCEPTION   = "EXCEPTION_FOR_ActorApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (getMode()) isDebugMode = true;//如果是"debug环境",那么值就一定是true(加判断是因为要让正式环境也可以开debug模式)
        if (!isDebugMode) {//2.如果是正式环境,在onCreate中设置默认未捕获异常线程
            Thread.setDefaultUncaughtExceptionHandler(new MyHandler());
        }
    }

    private class MyHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {//3.重写未捕获异常
            if (e != null) {
                String exception = SPUtils.getString(EXCEPTION);
                String thread = t == null ? "" : t.toString();
                if (exception == null) {
                    SPUtils.putString(EXCEPTION, thread.concat("\n\n").concat(e.toString()));
                } else {
                    if (exception.length() > 2 << 16) exception = "";//131 072
                    SPUtils.putString(EXCEPTION, exception.concat("\n\n\n").concat(thread).concat("\n\n").concat(e.toString()));
                }
            }
            onException(t, e);
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent restartIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        //定时器
//        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, restartIntent); // 2000:2秒钟后重启应用
//            System.exit(-1);
////            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 当App崩溃的时候
     * @param thread 线程
     * @param e 堆栈信息
     */
    protected abstract void onException(Thread thread, Throwable e);

    /**
     * 获取崩溃信息
     */
    public String getCrashExceptionInfo() {
        String string = SPUtils.getString(EXCEPTION);
        SPUtils.remove(EXCEPTION);
        return string;
    }

    /**
     * 当我们没在AndroidManifest.xml中设置其debug属性时:
     * 使用Eclipse运行这种方式打包时其debug属性为true,使用Eclipse导出这种方式打包时其debug属性为法false.
     * 在使用ant打包时，其值就取决于ant的打包参数是release还是debug.
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     *
     * 如果release版本也想输出日志，那么这个时候我们到 AndroidManifest.xml 中的application
     * 标签中添加属性强制设置debugable即可:
     * <application android:debuggable="true" tools:ignore="HardcodedDebugMode"
     */
    private boolean getMode(){
        try {
            ApplicationInfo info= getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) !=0 ;
        } catch (Exception e) {
            return false;
        }
    }
}