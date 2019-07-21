package com.actor.myandroidframework.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.text.TextUtils;

import com.actor.myandroidframework.application.ActorApplication;

import java.io.File;

/**
 * Description: 类的描述
 * Copyright  : Copyright (c) 2018
 * Company    : 重庆酷川科技有限公司 www.kuchuanyun.com
 * Author     : 李大发
 * Date       : 2018/7/29 on 19:12
 * 注意添加权限:
 * <!--模拟器中sdcard中创建文件夹的权限-->
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 * <!--允许挂载和反挂载文件系统可移动存储-->
 * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
 */

public class FileUtils {

    private static Context context = ActorApplication.instance;

    /**
     * 根据文件大小自动转为以B,KB, MB, GB
     * @param sizeBytes
     * @return
     */
    public static String formatFileSize(long sizeBytes) {
        return Formatter.formatFileSize(context, sizeBytes);
    }

    /**
     * 根据文件大小自动转为以B,KB, MB, GB. 尽量生成更短的数字
     * @param sizeBytes
     * @return
     */
    public static String formatShortFileSize(long sizeBytes) {
        return Formatter.formatShortFileSize(context, sizeBytes);
    }
	
    /**
     * 返回是否是图片格式
     * https://baike.baidu.com/item/%E5%9B%BE%E7%89%87%E6%A0%BC%E5%BC%8F/381122?fr=aladdin
     * @param fileNameOrPath 文件名/文件路径(都要包含后缀)
     * @return
     */
    public static boolean isPicture(String fileNameOrPath) {
        if (fileNameOrPath == null) return false;
        String file = fileNameOrPath.toLowerCase();
        if (file.endsWith(".jpg") || file.endsWith(".jpeg") || file.endsWith(".png") ||
                file.endsWith(".bmp") || file.endsWith(".gif") || file.endsWith(".ico") ||
                file.endsWith(".webp") || file.endsWith(".tga") || file.endsWith(".tif")) return true;
        return false;
    }

    /**
     * @param fileNameOrPath
     * @return 是否是视频
     */
    public static boolean isVideo(String fileNameOrPath) {
        if (fileNameOrPath == null) return false;
        String file = fileNameOrPath.toLowerCase();
        if (file.endsWith(".mp4") || file.endsWith(".avi") || file.endsWith(".3gp")||
                file.endsWith(".rmvb")|| file.endsWith(".m3u8")|| file.endsWith(".rm") ||
                file.endsWith(".wmv")|| file.endsWith(".flv")) return true;
        return false;
    }

    /**
     * @param fileNameOrPath
     * @return 是否是文档, 可用腾讯x5内核打开
     */
    public static boolean isDocument(String fileNameOrPath) {
        if (fileNameOrPath == null) return false;
        String file = fileNameOrPath.toLowerCase();
        if (file.endsWith(".txt") || file.endsWith(".pdf") || file.endsWith(".doc") ||
                file.endsWith(".docx") || file.endsWith(".xls") || file.endsWith(".xlsx")) return true;
        return false;
    }

    /**
     * 删除文件/文件夹
     * @param filePath
     * @param deleteSelf 如果是文件夹,是否删除自己
     */
    public static boolean delete(String filePath, boolean deleteSelf) {
        if (TextUtils.isEmpty(filePath)) return false;
        File file = new File(filePath);
        if (!file.exists()) return true;
        if (file.isFile()) {//如果是文件
            return file.delete();
        } else {//文件夹
            if (deleteSelf) {//文件夹 & 删除自己
                return delete(file);
            } else {//文件夹 & 不删除自己
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File file1 : files) {
                        if (file1.isFile()) {
                            if (!file1.delete()) return false;
                        } else if (file1.isDirectory()) {
                            if (!delete(file1)) return false;
                        }
                    }
                }
                return true;
            }
        }
    }

    /**
     * 删除文件/文件夹,自身文件夹都会被删除
     * @param file
     */
    public static boolean delete(File file) {
        if (file == null) return false;
        if (!file.exists()) return true;
        if (file.isFile()) {
            if (!file.delete()) return false;
        } else {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    if (file1.isFile()) {
                        if (!file1.delete()) return false;
                    } else if (file1.isDirectory()) {
                        if (!delete(file1)) return false;
                    }
                }
            }
            return file.delete();//删除文件夹
        }
        return true;
    }

    /**
     * 获取外部文件夹:SD卡/项目名
     */
    public static String getExternalStorageDir() {
        String packageLastName = getPackageLastName();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + packageLastName;
        File file = new File(path);
        if (!file.exists()) file.mkdir();
        return path;
    }

    /**
     * 获取包最后一个字段,示例:包名=com.google.example,则返回:example
     */
    public static String getPackageLastName() {
        String packageName = context.getPackageName();//获取包名
        String[] split = packageName.split("\\.");
        return split[split.length - 1];
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    public static String getFileNameFromUrl(String url) {
        if (TextUtils.isEmpty(url)) return null;
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 获取文件外部文件夹路径,如果文件夹不存在,会创建.
     * @param fileName 示例传入:getClass().getSimpleName() + 1.jpg
     */
    public static String getExternalStoragePath(@NonNull String fileName) {
        if (TextUtils.isEmpty(fileName)) return getExternalStorageDir();
        return getExternalStorageDir() + File.separator + fileName;
    }

    /**
     * 系统会自动清理这里面的内容/data/data/包名/cache?
     */
    public static File getCacheDir() {
        return context.getCacheDir();
    }

    /**
     * /data/data/files?
     */
    public static File getFilesDir() {
        return context.getFilesDir();
    }

    /**
     * 设置->应用->应用详情->清除数据
     * SDCard/Android/data/包名/files/卸载后这个文件夹删除
     */
    public static File getExternalFilesDir() {
        return context.getExternalFilesDir(null);
    }

    /**
     * 设置->应用->应用详情里面的->清除缓存
     * SDCard/Android/data/包名/cache/卸载后这个文件夹删除
     */
    public static File getExternalCacheDir() {
        return context.getExternalCacheDir();
    }
}
