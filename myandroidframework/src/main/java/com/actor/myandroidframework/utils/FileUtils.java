package com.actor.myandroidframework.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.IntentUtils;

import java.io.File;

/**
 * Description: 文件帮助类
 * 注意添加权限:
 * <!--模拟器中sdcard中创建文件夹的权限-->
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 * <!--允许挂载和反挂载文件系统可移动存储-->
 * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
 *
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2018/7/29 on 19:12
 * @version 1.0
 */
public class FileUtils {

    private static Context context = ConfigUtils.APPLICATION;

    /**
     * 根据文件大小自动转为以B,KB, MB, GB
     * @param sizeBytes
     */
    public static String formatFileSize(long sizeBytes) {
        return Formatter.formatFileSize(context, sizeBytes);
    }

    /**
     * 根据文件大小自动转为以B,KB, MB, GB. 尽量生成更短的数字
     * @param sizeBytes
     */
    public static String formatShortFileSize(long sizeBytes) {
        return Formatter.formatShortFileSize(context, sizeBytes);
    }

    /**
     * 根据后缀, 返回是否是图片格式
     * https://baike.baidu.com/item/%E5%9B%BE%E7%89%87%E6%A0%BC%E5%BC%8F/381122?fr=aladdin
     * @see com.blankj.utilcode.util.ImageUtils#isImage(String)
     *
     * @param fileNameOrPath 文件名/文件路径(都要包含后缀)
     */
    public static boolean isPicture(String fileNameOrPath) {
        if (fileNameOrPath == null) return false;
        String file = fileNameOrPath.toLowerCase();
        return file.endsWith(".jpg") || file.endsWith(".jpeg") || file.endsWith(".png") ||
                file.endsWith(".bmp") || file.endsWith(".gif") || file.endsWith(".ico") ||
                file.endsWith(".webp") || file.endsWith(".tga") || file.endsWith(".tif");
    }

    /**
     * @param fileNameOrPath 文件名称/路径
     * @return 是否是视频
     */
    public static boolean isVideo(String fileNameOrPath) {
        if (fileNameOrPath == null) return false;
        String file = fileNameOrPath.toLowerCase();
        return file.endsWith(".mp4") || file.endsWith(".avi") || file.endsWith(".3gp") ||
                file.endsWith(".rmvb") || file.endsWith(".m3u8") || file.endsWith(".rm") ||
                file.endsWith(".wmv") || file.endsWith(".flv");
    }

    /**
     * @param fileNameOrPath 文件名称/路劲
     * @return 是否是文档, 可用腾讯x5内核打开
     */
    public static boolean isDocument(String fileNameOrPath) {
        if (fileNameOrPath == null) return false;
        String file = fileNameOrPath.toLowerCase();
        return file.endsWith(".txt") || file.endsWith(".pdf") || file.endsWith(".doc") ||
                file.endsWith(".docx") || file.endsWith(".xls") || file.endsWith(".xlsx");
    }

    /**
     * 获取包最后一个字段,示例:包名=com.google.example,则返回:example
     * @see com.blankj.utilcode.util.FileUtils#getFileExtension(String)
     */
    public static String getPackageLastName() {
        String packageName = context.getPackageName();//获取包名
        if (packageName.contains(".")) {
            String[] split = packageName.split("\\.");
            return split[split.length - 1];
        }
        return packageName;
    }

    /**
     * @param url 下载链接
     * @return 从下载链接中解析出文件名
     */
    public static String getFileNameFromUrl(String url) {
        if (TextUtils.isEmpty(url) || !url.contains("/")) return url;
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 缓存目录, 系统会自动清理这里面的内容
     * @return /data/data/包名/cache
     */
    public static File getCacheDir() {
        return context.getCacheDir();
    }

    /**
     * @return /data/data/包名/files
     */
    public static File getFilesDir() {
        return context.getFilesDir();
    }

    /**
     * 设置->应用->应用详情->清除数据
     * SDCard/Android/data/包名/files/    卸载后这个文件夹删除
     */
    public static File getExternalFilesDir() {
        return context.getExternalFilesDir(null);
    }

    /**
     * 设置->应用->应用详情里面的->清除缓存
     * SDCard/Android/data/包名/cache/    卸载后这个文件夹删除
     */
    public static File getExternalCacheDir() {
        return context.getExternalCacheDir();
    }

    /**
     * 返回应用程序特定缓存目录的绝对路径, 为存储缓存的代码而设计的文件系统。
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static File getCodeCacheDir() {
        return context.getCodeCacheDir();
    }

    /**
     * 获取外部文件夹: SD卡/项目名
     * 初始化Excel表格, 高版本必须要写sd卡权限, 否则报错
     * @return /storage/emulated/0/lastname
     * @deprecated 读写外部SD卡, 需要申请权限, 麻烦
     */
//    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    @Deprecated
    public static String getExternalStorageDir() {
        String packageLastName = getPackageLastName();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + packageLastName;
        File file = new File(path);
        if (!file.exists()) file.mkdir();
        return path;
    }

    /**
     * 获取文件外部文件夹路径,如果文件夹不存在,会创建.
     * @param fileName 示例传入:getClass().getSimpleName() + 1.jpg
     * @deprecated 读写外部SD卡, 需要申请权限, 麻烦
     */
    @Deprecated
    public static String getExternalStoragePath(@NonNull String fileName) {
        if (TextUtils.isEmpty(fileName)) return getExternalStorageDir();
        return getExternalStorageDir() + File.separator + fileName;
    }

    /**
     * 分享文件, 调用系统分享
     * @param filePath 文件路径
     */
    public static void shareFile(Context context, String filePath) {
        Intent sendIntent = IntentUtils.getShareImageIntent("", filePath);
        sendIntent.setType("*/*");
        context.startActivity(Intent.createChooser(sendIntent, "请选择需要分享的应用程序"));
    }
}
