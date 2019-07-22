package com.actor.myandroidframework.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

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
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/5/30 on 19:09
 * @version 1.0
 * TODO 有些方法有待优化
 */
public class PermissionRequestUtils {

//        com.blankj.utilcode.util.PermissionUtils

    public static void requestPermission(Context context, PermissionCallBack callBack, List<String> permissions) {
        if (permissions != null && permissions.size() > 0) {
            String[] permis = new String[permissions.size()];
            for (int i = 0; i < permissions.size(); i++) {
                permis[i] = permissions.get(i);
            }
            requestPermission(context, callBack, permis);
        }
    }

    /**
     * 申请单/多个权限
     * 示例:{@link com.yanzhenjie.permission.runtime.Permission#CAMERA}
     * @param permission
     */
    public static void requestPermission(Context context, PermissionCallBack callBack, String... permission) {
        AndPermission.with(context)
                .runtime()
                .permission(permission)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if (callBack != null) callBack.onSuccessful(data);
                    }
                })
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
//                .rationale(new Rationale<List<String>>() {
//                    @Override
//                    public void showRationale(Context context, List<String> data, RequestExecutor executor) {
//                        // 这里的对话框可以自定义，只要调用executor.execute();就可以。
//                        AndPermission.rationaleDialog(context, data).show();
//                        executor.execute();//继续申请
//                    }
//                })
                .start();
    }

    /**
     * 申请单/多个权限数组
     * 示例:{@link com.yanzhenjie.permission.runtime.Permission.Group#CAMERA}
     */
    public static void requestPermissionGroup(Context context, PermissionCallBack callBack, String[]... permissions) {
        // 申请多个权限。
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if (callBack != null) callBack.onSuccessful(data);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if (callBack != null) callBack.onFailure(data);
//                        if (hasAlwaysDeniedPermission(context, data)) {
//                        }
                    }
                })
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
//                .rationale(new Rationale<List<String>>() {
//                    @Override
//                    public void showRationale(Context context, List<String> data, RequestExecutor executor) {
//                        // 这里的对话框可以自定义，只要调用executor.execute();就可以。
//                        AndPermission.rationaleDialog(context, data).show();
//                        executor.execute();//继续申请
//                    }
//                })
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

    public interface PermissionCallBack{
        void onSuccessful(@NonNull List<String> deniedPermissions);
        void onFailure(@NonNull List<String> deniedPermissions);
    }

//    private static void logError(String msg) {
//        LogUtils.Error(msg, false);
//    }

    //////////////////////////////////////////////////////////////
    //共享私有文件
    //////////////////////////////////////////////////////////////
    /**
     * 使用AndPermission也需要在manifest.xml中添加android.permission.REQUEST_INSTALL_PACKAGES权限，
     * 并且调用AndPermission的安装代码之前需要保证App拥有外部存储设备读写权限。
     */
    public static void installApk(Context context, String apkPath) {//?
//        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        Uri uri = AndPermission.getFileUri(context, new File(apkPath));
//        intent.setDataAndType(uri, "application/vnd.android.package-archive");
//        context.startActivity(intent);

        AndPermission.with(context)
                .install()
                .file(new File(apkPath))
//                .onGranted(new Action<File>() {
//                    @Override
//                    public void onAction(File data) {
//                    }
//                })
//                .onDenied(new Action<File>() {
//                    @Override
//                    public void onAction(File data) {
//                    }
//                })
//                .rationale(new Rationale<File>() {
//                    @Override
//                    public void showRationale(Context c, File f, RequestExecutor e) {
//                        // 没有权限会调用该访问，开发者可以在这里弹窗告知用户无权限。
//                        // 启动设置：e.execute();
//                        // 取消启动：e.cancel();

                          //当showRationale()被回调后说明没有REQUEST_INSTALL_PACKAGES权限，
                // 此时开发者必须回调RequestExecutor#execute()来启动设置
                // 或者RequestExecutor#cancel()来取消启动设置，否则将不会回调onGranted()
                // 或者onDenied()中的任何一个，也就是说AndPermission将不会有任何响应
//                    }
//                })
                .start();
    }
}
