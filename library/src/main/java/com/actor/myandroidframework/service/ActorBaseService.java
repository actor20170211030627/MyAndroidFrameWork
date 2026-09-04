package com.actor.myandroidframework.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NotificationUtils;
import com.blankj.utilcode.util.Utils;

/**
 * Description: Service 基类 <br/>
 * Android 8.0 系统不允许后台应用创建后台服务。<br/>
 * 因此，Android 8.0 引入了一种全新的方法，即 context.startForegroundService()，以在前台启动新服务。<br/>
 * 在系统创建服务后，应用有五秒的时间来调用该服务的 startForeground() 方法以显示新服务的用户可见通知。<br/>
 * 如果应用在此时间限制内未调用 startForeground()，则系统将停止服务并声明此应用为 ANR <br/>
 * {@link null 注意:} startForegroundService 方式需要添加权限: <br/>
 * &lt;uses-permission android:name="android.permission.FOREGROUND_SERVICE" /&gt;
 * <br/>
 * <br/>
 * Author     : ldf
 * Date       : 2020/3/16 on 16:40
 *
 * @version 1.0
 *
 * @see com.blankj.utilcode.util.ServiceUtils
 */
public abstract class ActorBaseService extends Service {

    /** 是否以 startForegroundService 的方式启动 */
    public static final String IS_START_FOREGROUND_SERVICE = "IS_START_FOREGROUND_SERVICE";

    /** channel id: “通知类型”的id, 每一种通知🔔用同一个 id, 相同 channelId 的通知会合并 */
    protected           String channelId                   = toString();
    /** channel name: 给这个通知频道取个名字 */
    protected           String channelName                 = AppUtils.getAppName();
    /** 通知显示的标题 */
    protected           String contentTitle                = AppUtils.getAppName();
    /** 通知子标题 */
    protected           String contentText                 = "服务运行中...";
    /** 必须设置 smallIcon */
    protected           int    smallIcon                   = AppUtils.getAppIconId();
    /** 本质上是为了确保任务队列安全而设计的计数器 */
    protected           int    startId                     = -1;

    /**
     * 用于 {@link androidx.core.app.NotificationManagerCompat#notify(int, Notification)} 使用, {@link null 注意: 不能为0} <br />
     * 如果你的应用只有一个或一组需要统一管理的前台服务通知，所有服务都可以使用这个 ID。<br />
     * 如果需要区分多个服务：if 同时运行多个前台服务，并且希望它们各自显示不同的通知，那么你需要为每个服务分配不同的 ID。
     */
    protected           int    id                          = 1;
    /** 点击通知打开Activity的时候的请求码 */
    protected           int    requestCode                 = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.error(getClass().getName());
    }

    /**
     * 每次通过 {@link android.content.Context#startService(Intent)} 或 {@link android.content.Context#startForegroundService(Intent)} 启动服务时，都会调用这方法 <br />
     * 因为服务是单例的，if服务已经在运行，不会再调用 {@link #onCreate()}，但 此方法 依然会被调用，用于处理新的启动请求。
     * @param intent 启动服务时传入的意图，携带了调用方传递的数据, 当以下两个条件被满足时，intent = null:
     *               <ol>
     *                   <li>这方法返回值是 {@link #START_STICKY} 或 {@link #START_STICKY_COMPATIBILITY}</li>
     *                   <li>服务之前被系统因内存不足而杀死，系统自动重启了该服务，但没有待处理的 Intent 可以重新投递</li>
     *               </ol>
     * @param flags 系统附加的启动标志，通常用于判断服务的启动状态。常见值：
     *              <ol>
     *                  <li>{@link #START_FLAG_REDELIVERY}: 系统在服务被杀死后，重新传递之前的 Intent(对应 {@link #START_REDELIVER_INTENT} 返回值)</li>
     *                  <li>{@link #START_FLAG_RETRY}: 服务在启动后、onStartCommand 调用前被系统杀死，系统正在尝试重试。</li>
     *              </ol>
     * @param startId 本次启动请求的唯一标识符（自增整数）。主要用于配合 {@link #stopSelf(int)} 使用：<br />
     *                如果你想在任务完成后停止服务，但担心新的启动请求覆盖旧任务，
     *                可以调用 {@link #stopSelf(int)} 传入 startId，它只会在“本次启动对应的任务”处理完后才停止服务，
     *                不会误停新来的任务。
     * @return 告诉系统：当服务因系统资源不足等原因被杀死后，应该如何恢复。
     *         <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
     *             <tr>
     *                 <th align="center">参数</th>
     *                 <th align="center">说明</th>
     *             </tr>
     *             <tr>
     *                  <td>{@link #START_STICKY_COMPATIBILITY}</td>
     *                 <td>
     *                     类似于 START_STICKY，但兼容性版本，不保证服务一定会重启。<br />
     *                     一般不建议使用，通常由系统框架内部使用。
     *                 </td>
     *             </tr>
     *             <tr>
     *                  <td>{@link #START_STICKY}(默认)</td>
     *                 <td>
     *                     服务被杀死后，系统会自动重启服务，但 onStartCommand() 会收到一个 null 的 Intent。<br />
     *                     需要持续运行但不需要处理特定指令的服务（如音乐播放器、一直监听传感器状态的服务）。
     *                 </td>
     *             </tr>
     *             <tr>
     *                  <td>{@link #START_NOT_STICKY}</td>
     *                 <td>
     *                     服务被杀死后，系统不会自动重启它。<br />
     *                     执行一次性、可随时中断的任务（如定时下载、上传）。如果任务中断，用户或应用可以随时重新发起请求。
     *                 </td>
     *             </tr>
     *             <tr>
     *                  <td>{@link #START_REDELIVER_INTENT}</td>
     *                 <td>
     *                     服务被杀死后，系统会自动重启服务，并重新传递最后一次收到的 Intent。<br />
     *                     执行重要且必须完成的任务（如文件下载、上传）。系统会确保这个 Intent 被处理完成。
     *                 </td>
     *             </tr>
     *         </table>
     */
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        this.startId = startId;
        if (intent != null) {
            boolean foreground = intent.getBooleanExtra(IS_START_FOREGROUND_SERVICE, false);
            //如果是以 startForegroundService 的方式启动
            if (foreground) fitForegroundService();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;    // 此服务不提供绑定
    }

    /**
     * 适配后台Service, 示例写法, 可重写此方法
     */
    protected void fitForegroundService() {
        //从 Android 8.0（API 26）开始，系统对后台服务增加了限制。系统会给予该服务一个相当于 ANR 的时间（约5秒） 来完成向“前台服务”的转换。
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return;
        Notification notification = createNotification();
        startForeground(id, notification);
    }

    protected Notification createNotification() {
        // 创建通知的 Intent
        Intent notifyIntent = new Intent();
        try {
            //点击后打开你的 Activity, 否则点击了没反应
            notifyIntent = new Intent(this, Class.forName(ActivityUtils.getLauncherActivity()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE, (Bundle) null
        );

        return NotificationUtils.getNotification(new NotificationUtils.ChannelConfig(channelId, channelName, NotificationUtils.IMPORTANCE_DEFAULT),
                new Utils.Consumer<NotificationCompat.Builder>() {
                    @Override
                    public void accept(NotificationCompat.Builder builder) {
                        builder.setContentTitle(contentTitle)
                                .setContentText(contentText)
                                .setSmallIcon(smallIcon)
                                .setContentIntent(pendingIntent)
//                                .setAllowSystemGeneratedContextualActions()
                        ;
                    }
                }
        );
    }

    //停止自己
//    @Override
//    public final void stopSelf() {
//        stopSelf(-1);
//        stopSelf(startId);    //只停止这一次启动（startId）对应的任务，如果后续有新的 startId 进来了，请保持服务继续运行”。系统内部会判断 startId 是否是最新的，如果不是则忽略停止请求。
//    }
}
