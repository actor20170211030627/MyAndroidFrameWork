package com.actor.myandroidframework.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;
import com.yanzhenjie.permission.runtime.PermissionRequest;

import java.io.File;
import java.util.List;

/**
 * AndPermission是一个权限管理框架，包括但不仅限于运行时权限，覆盖绘制权限，显示通知权限和访问通知权限等。
 *
 * 请求运行时权限
 * 共享私有文件
 * 请求安装未知来源Apk
 * 请求在其他App顶部绘制
 * 请求显示通知
 * 请求访问通知
 * 提交问题或者贡献代码：https://github.com/yanzhenjie/AndPermission
 * https://yanzhenjie.com/AndPermission/cn/
 * Author     : 李大发
 * Date       : 2019/5/30 on 19:09
 * @version 1.0
 * @version 1.0.1 增加 {{@link #hasAlwaysDeniedPermission(Context, String...)}} 方法
 *
 * 也可使用 blankj 的权限:
 * @see com.blankj.utilcode.util.PermissionUtils
 */
public class PermissionRequestUtils {

    private Rationale mRationale = new Rationale<List<String>>() {
        @Override
        public void showRationale(Context context, List<String> data, RequestExecutor executor) {
            // 这里使用一个Dialog询问用户是否继续授权。
            // 这里的对话框可以自定义，只要调用executor.execute();就可以。
//            AndPermission.rationaleDialog(context, data).show();//这个版本的Dialog被去掉了...

            // 如果用户继续：
            executor.execute();

            // 如果用户中断：
            executor.cancel();
        }
    };

    public static void requestPermission(Context context, PermissionCallBack callBack, String... permission) {
        requestPermission(context, null, callBack, permission);
    }

    /**
     * 申请 '单/多' 个 '权限/权限数组'
     * @param rationale 用户往往会拒绝一些权限，而程序的继续运行又必须使用这些权限，此时我们应该友善的提示用户。
     *                  例如，当用户拒绝Permission.WRITE_EXTERNAL_STORAGE一次，
     *                  在下次请求 Permission.WRITE_EXTERNAL_STORAGE时，我们应该展示为什么需要此权限的说明，
     *                  以便用户判断是否需要授权给我们。在AndPermission 中我们可以使用Rationale。
     *                  可参考 {@link #mRationale}
     *
     * @param permission 申请的权限, 示例:
     *                   @see com.yanzhenjie.permission.runtime.Permission#CAMERA
     *                   @see com.yanzhenjie.permission.runtime.Permission.Group#STORAGE
     */
    public static void requestPermission(Context context, Rationale<List<String>> rationale,
                                         PermissionCallBack callBack, String... permission) {
        PermissionRequest permissionRequest = AndPermission.with(context)
                .runtime()
                .permission(permission);
        if (rationale != null) {
            permissionRequest.rationale(rationale);
        }
        permissionRequest.onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if (callBack != null) callBack.onGranted(data);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //这些权限被用户总是拒绝。
//                        boolean b = hasAlwaysDeniedPermission(context, data);
                        if (callBack != null) callBack.onDenied(data);
                    }
                })
                .start();
    }

    public static void requestPermissionGroup(Context context, PermissionCallBack callBack, String[]... permissions) {
        requestPermissionGroup(context, null, callBack, permissions);
    }

    /**
     * 申请单/多个权限数组
     * 示例:{@link com.yanzhenjie.permission.runtime.Permission.Group#CAMERA}
     */
    public static void requestPermissionGroup(Context context, Rationale<List<String>> rationale,
                                              PermissionCallBack callBack, String[]... permissions) {
        PermissionRequest permissionRequest = AndPermission.with(context)
                .runtime()
                .permission(permissions);
        if (rationale != null) {
            permissionRequest.rationale(rationale);
        }
        permissionRequest.onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if (callBack != null) callBack.onGranted(data);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //这些权限被用户总是拒绝。
//                        boolean b =hasAlwaysDeniedPermission(context, data);
                        if (callBack != null) callBack.onDenied(data);
                    }
                })
                .start();
    }

    /**
     * 只能在onDenied()的回调中调用
     * 用户是否勾选了不再提示并且拒绝了权限
     * @return 只要有一个被勾选, 就返回true
     */
    public static boolean hasAlwaysDeniedPermission(Context context, List<String> deniedPermissions) {
        return AndPermission.hasAlwaysDeniedPermission(context, deniedPermissions);
    }

    public static boolean hasAlwaysDeniedPermission(Context context, String... deniedPermissions) {
        return AndPermission.hasAlwaysDeniedPermission(context, deniedPermissions);
    }

    /**
     * 当我们请求某些权限失败时，我们应该提示去设置中授权某些权限，
     * 为了方便开发者，AndPermission提供了一个方法把权限字符串转为对应的提示文字。
     * @return 短信, 手机, 存储空间
     */
    public static List<String> transformText(Context context, String... permission) {
        return Permission.transformText(context, permission);
    }

    public static List<String> transformText(Context context, String[]... permission) {
        return Permission.transformText(context, permission);
    }

    public static List<String> transformText(Context context, List<String> permission) {
        return Permission.transformText(context, permission);
    }

    /**
     * 跳到系统设置,自己去设置权限
     *
     * @Override
     * protected void onActivityResult(int reqCode, int resCode, Intent data) {
     *     switch (reqCode) {
     *         case REQ_CODE_PERMISSION: {
     *             if (AndPermission.hasPermissions(this, ...)) {
     *                 // 有对应的权限
     *             } else {
     *                 // 没有对应的权限
     *             }
     *             break;
     *         }
     *     }
     * }
     */
    public static void go2Setting(Context context, int requestCode) {
        AndPermission.with(context)
                .runtime()
                .setting()
                .start(requestCode);
        //AndPermission.defaultSettingDialog(mActivity, REQUEST_CODE_SETTING).show();
    }

    /**
     * 是否有权限
     * @return
     */
    public static boolean hasPermission(Context context, String... permission) {
        return AndPermission.hasPermissions(context, permission);
    }

    /**
     * 是否有权限数组
     * @return 只要有一个权限数组没有获取到权限, 就返回false
     */
    public static boolean hasPermissionGroup(Context context, String[]... permission) {
        return AndPermission.hasPermissions(context, permission);
    }

    public interface PermissionCallBack {
        /**
         * 用户同意的权限
         */
        void onGranted(@NonNull List<String> deniedPermissions);

        /**
         * 用户拒绝的权限, 包含被用户 '总是拒绝' 的权限.
         */
        void onDenied(@NonNull List<String> deniedPermissions);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 共享私有文件
    ///////////////////////////////////////////////////////////////////////////
    /**
     * https://yanzhenjie.com/AndPermission/cn/install/
     * <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
     * 请求安装未知来源Apk, 需要在manifest.xml中添加权限
     * 并且调用AndPermission的安装代码之前需要保证App拥有外部存储设备读写权限。
     */
    public static void installApk(Context context, File file) {
        AndPermission.with(context)
                .install()
                .file(file)
                .start();
    }

    /**
     * https://yanzhenjie.com/AndPermission/cn/overlay/
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
     * 请求在其他App顶部绘制
     */
    public static void overlay(Context context, PermissionCallBack callBack) {
        AndPermission.with(context)
                .overlay()
//                .rationale()
                .onGranted(new Action<Void>() {
                    @Override
                    public void onAction(Void data) {
                        callBack.onGranted(null);
                        //显示Dialog
//                        Dialog dialog = new Dialog(this);
//                        Window window = dialog.getWindow();
//                        int overlay = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//                        int alertWindow = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//                        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? overlay : alertWindow;
//                        window.setType(type);
                    }
                })
                .onDenied(new Action<Void>() {
                    @Override
                    public void onAction(Void data) {
                        callBack.onDenied(null);
                    }
                })
                .start();
    }

    /**
     * https://yanzhenjie.com/AndPermission/cn/fileuri/
     * 共享私有文件
     */
    public static Uri getFileUri(Context context, File file) {
        return AndPermission.getFileUri(context, file);
    }

    /**
     * https://yanzhenjie.com/AndPermission/cn/notify/show.html
     * 请求显示通知
     * 从Android8.0开始，用户可以管理App显示通知的权限，该权限默认是开启的，但是部分国产机上是关闭的。
     */
    public static void notification(Context context, PermissionCallBack callBack) {
        AndPermission.with(context)
                .notification()
                .permission()
                .onGranted(new Action<Void>() {
                    @Override
                    public void onAction(Void data) {
                        if (callBack != null) {
                            callBack.onGranted(null);
                        }
                    }
                })
                .onDenied(new Action<Void>() {
                    @Override
                    public void onAction(Void data) {
                        if (callBack != null) {
                            callBack.onDenied(null);
                        }
                    }
                })
                .start();
    }

    /**
     * https://yanzhenjie.com/AndPermission/cn/notify/access.html
     * 请求访问通知
     */
    public static void notificationRequest(Context context, PermissionCallBack callBack) {
        AndPermission.with(context)
                .notification()
                .listener()
//                .rationale(new Rationale<Void>() {
//                    @Override
//                    public void showRationale(Context c, Void d, RequestExecutor e) {
//                        // 没有权限会调用该访问，开发者可以在这里弹窗告知用户无权限。
//                        // 启动设置: e.execute();
//                        // 取消启动: e.cancel();
//                    }
//                })
                .onGranted(new Action<Void>() {
                    @Override
                    public void onAction(Void data) {
                        // App可以访问通知了。
                        if (callBack != null) {
                            callBack.onGranted(null);
                        }
                    }
                })
                .onDenied(new Action<Void>() {
                    @Override
                    public void onAction(Void data) {
                        // App不能访问通知。
                        if (callBack != null) {
                            callBack.onDenied(null);
                        }
                    }
                })
                .start();
    }
}
