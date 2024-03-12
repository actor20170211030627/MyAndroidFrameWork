package com.actor.myandroidframework.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.UriUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;

/**
 * Description: 文件帮助类 <br />
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓ <br />
 * Author     : ldf <br />
 * Date       : 2018/7/29 on 19:12
 * @version 1.0
 * @see PathUtils PathUtils: 更多获取路径的方式
 */
public class FileUtils {

    ///////////////////////////////////////////////////////////////////////////
    // 1.格式化
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 根据文件大小自动转为以B, KB, MB, GB
     * @param sizeBytes 文件大小
     */
    public static String formatFileSize(long sizeBytes) {
        return Formatter.formatFileSize(Utils.getApp(), sizeBytes);
    }

    /**
     * 根据文件大小自动转为以B, KB, MB, GB. 尽量生成更短的数字
     * @param sizeBytes 文件大小
     */
    public static String formatShortFileSize(long sizeBytes) {
        return Formatter.formatShortFileSize(Utils.getApp(), sizeBytes);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 2.判断文件格式
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 根据后缀, 返回是否是图片格式
     * https://baike.baidu.com/item/%E5%9B%BE%E7%89%87%E6%A0%BC%E5%BC%8F/381122?fr=aladdin
     * @see com.blankj.utilcode.util.ImageUtils.ImageType
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


    ///////////////////////////////////////////////////////////////////////////
    // 3.获取内部存储路径, 不需要SD卡读写权限
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 缓存目录, 系统会自动清理这里面的内容
     * @return /data/data/package/cache
     *
     * @deprecated 使用: {@link PathUtils}
     * 1.获取Internalmul, 卸载后这些文件夹都会删除
     * @see PathUtils#getInternalAppCachePath()         /data/data/package/cache, 缓存目录
     * @see PathUtils#getInternalAppCodeCacheDir()      /data/data/package/code_cache, 为存储缓存的代码而设计的文件系统
     * @see PathUtils#getInternalAppDataPath()          /data/data/package, 这个app对应的存储目录
     * @see PathUtils#getInternalAppDbPath(String)      /data/data/package/databases/name, 数据库
     * @see PathUtils#getInternalAppDbsPath()           /data/data/package/databases, 数据库根目录
     * @see PathUtils#getInternalAppFilesPath()         /data/data/package/files, 文件目录
     * @see PathUtils#getInternalAppNoBackupFilesPath() /data/data/package/no_backup, 备份
     * @see PathUtils#getInternalAppSpPath()            /data/data/package/shared_prefs, SP目录
     */
    @Deprecated
    public static String getInternalAppCachePath() {
        return PathUtils.getInternalAppCachePath();
    }


    ///////////////////////////////////////////////////////////////////////////
    // 4.获取App对应的外部SD卡路径, 不需要SD卡读写权限
    ///////////////////////////////////////////////////////////////////////////
    /**
     * App对应的外部SD卡缓存目录
     * @return /storage/emulated/0/Android/data/package/cache, 如果没挂载SD卡, 返回""
     *
     * @deprecated 使用: {@link PathUtils}
     * 1.卸载后这些文件夹都会删除
     * @see PathUtils#getExternalAppAlarmsPath()        /storage/emulated/0/Android/data/package/files/Alarms, app"提醒铃声"的标准目录
     * @see PathUtils#getExternalAppCachePath()         /data/data/package/cache, "缓存"目录
     * @see PathUtils#getExternalAppDataPath()          /storage/emulated/0/Android/data/package, 这个app对应的存储目录
     * @see PathUtils#getExternalAppDcimPath()          /storage/emulated/0/Android/data/package/files/DCIM, 这个app对应"相机拍摄照片和视频"的标准目录
     * @see PathUtils#getExternalAppDocumentsPath()     /storage/emulated/0/Android/data/package/files/Documents, 这个app对应"文档"的标准目录
     * @see PathUtils#getExternalAppDownloadPath()      /storage/emulated/0/Android/data/package/files/Download, 这个app对应"下载"的标准目录
     * @see PathUtils#getExternalAppFilesPath()         /storage/emulated/0/Android/data/package/files, 这个app对应"文件"的标准目录
     * @see PathUtils#getExternalAppMoviesPath()        /storage/emulated/0/Android/data/package/files/Movies, 这个app对应"电影"的标准目录
     * @see PathUtils#getExternalAppMusicPath()         /storage/emulated/0/Android/data/package/files/Music, 这个app对应"音频"的标准目录
     * @see PathUtils#getExternalAppNotificationsPath() /storage/emulated/0/Android/data/package/files/Notifications, 这个app对应"通知铃声"的标准目录
     * @see PathUtils#getExternalAppObbPath()           /storage/emulated/0/Android/obb/package, 这个app对应"游戏相关数据包"的标准目录
     * @see PathUtils#getExternalAppPicturesPath()      /storage/emulated/0/Android/data/package/files/Pictures, 这个app对应"图片"的标准目录
     * @see PathUtils#getExternalAppPodcastsPath()      /storage/emulated/0/Android/data/package/files/Podcasts, 这个app对应"播客"的标准目录
     * @see PathUtils#getExternalAppRingtonesPath()     /storage/emulated/0/Android/data/package/files/Ringtones, 这个app对应"铃声"的标准目录
     */
    @Deprecated
    public static String getExternalAppCachePath() {
        return PathUtils.getExternalAppCachePath();
    }


    ///////////////////////////////////////////////////////////////////////////
    // 5.获取外部SD卡对应的路径, 需要SD卡读写权限!!!
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 获取SD卡"图片"的标准目录, 需要SD卡读写权限
     * <!--读文件权限-->
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
     * <!--写文件权限-->
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     * <!--允许挂载和反挂载文件系统可移动存储-->
     * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
     *
     * @return /storage/emulated/0/Pictures
     *
     * @deprecated 使用: {@link PathUtils}
     * @see PathUtils#getExternalAlarmsPath()        /storage/emulated/0/Alarms, SD卡"提醒铃声"存放的标准目录
     * @see PathUtils#getExternalDcimPath()          /storage/emulated/0/DCIM, SD卡"相机拍摄照片和视频"的标准目录
     * @see PathUtils#getExternalDocumentsPath()     /storage/emulated/0/Documents, SD卡"文档"的标准目录
     * @see PathUtils#getExternalDownloadsPath()     /storage/emulated/0/Download, SD卡"下载"的标准目录
     * @see PathUtils#getExternalMoviesPath()        /storage/emulated/0/Movies, SD卡"电影"的标准目录
     * @see PathUtils#getExternalMusicPath()         /storage/emulated/0/Music, SD卡"音频"的标准目录
     * @see PathUtils#getExternalNotificationsPath() /storage/emulated/0/Notifications, SD卡"通知铃声"的标准目录
     * @see PathUtils#getExternalPicturesPath()      /storage/emulated/0/Pictures, SD卡"图片"的标准目录
     * @see PathUtils#getExternalPodcastsPath()      /storage/emulated/0/Podcasts, SD卡"播客"的标准目录
     * @see PathUtils#getExternalRingtonesPath()     /storage/emulated/0/Ringtones, SD卡"铃声"的标准目录
     * @see PathUtils#getExternalStoragePath()       /storage/emulated/0, SD卡根目录
     */
    @Deprecated
    public static String getExternalPicturesPath() {
        return PathUtils.getExternalPicturesPath();
    }


    ///////////////////////////////////////////////////////////////////////////
    // 6.优先获取外部SD卡对应的路径, 不需要SD卡读写权限
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 优先获取外部SD卡"缓存"的标准目录, 如果没挂载SD卡, 就获取内部"缓存"的标准目录
     * @return /storage/emulated/0/Android/data/package/cache, /data/data/package/cache
     *
     * @deprecated 使用: {@link PathUtils}
     * @see PathUtils#getAppDataPathExternalFirst() /storage/emulated/0/Android/data/package, /data/data/package, 优先获取外部SD卡"app文件存放"的标准目录
     * @see PathUtils#getFilesPathExternalFirst()   /storage/emulated/0/Android/data/package/files, /data/data/package/files, 优先获取外部SD卡"文件"的标准目录
     * @see PathUtils#getCachePathExternalFirst()   /storage/emulated/0/Android/data/package/cache, /data/data/package/cache, 优先获取外部SD卡"缓存"的标准目录
     */
    @Deprecated
    public static String getCachePathExternalFirst() {
        return PathUtils.getCachePathExternalFirst();
    }

    /**
     * 从下载url中解析出文件名
     * @param url 下载url
     * @return 返回一个不为空的文件名, 例: Xxx.png
     */
    @NonNull
    public static String getFileNameFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            //http://www.xxx.com/xxx.0.7.zip?a=b&c=d
            //http://www.xxx.com/s?a=a & b=b & c=c
            if (url.contains("?")) url = url.substring(0, url.lastIndexOf("?"));
            if (url.contains("/")) url = url.substring(url.lastIndexOf("/") + 1);
        }
        //如果上方获取的url还是空的
        if (TextUtils.isEmpty(url)) url = String.valueOf(System.currentTimeMillis());
        return url;
    }

    /**
     * <a href="https://developer.android.google.cn/training/secure-file-sharing?hl=zh-cn">分享文件 _ Android 开发者</a> <br />
     * 分享文件, 调用系统分享
     * @param filePath 文件路径
     */
    public static void shareFile(@NonNull Context context, @NonNull String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return;
        Uri fileUri = UriUtils.file2Uri(file);

//        String mimeType = getMimeType(filePath);
//        mimeType = "*/*";
        Intent shareIntent = IntentUtils.getShareImageIntent(fileUri);
//        shareIntent.setType(mimeType);
//        if (mimeType != null) {
//            LogUtils.errorFormat("mimeType = %s", mimeType);
//            Uri data = shareIntent.getData();
//            shareIntent.setDataAndType(data, mimeType);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //对目标应用临时授权该Uri所代表的文件
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
//        context.grantUriPermission("dest package", fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(shareIntent , "请选择需要分享的应用程序")
                .addFlags((context instanceof Activity) ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    /**
     * <a href="https://stackoverflow.com/questions/8589645/how-to-determine-mime-type-of-file-in-android">获取文件的MimeType</a>
     * @param url 文件路径或任何合适的URL
     */
    @Nullable
    public static String getMimeType(String url) {
        File file = new File(url);
        if (!file.exists()) return null;
        Uri fileUri = UriUtils.file2Uri(file);
        String mimeType = Utils.getApp().getContentResolver().getType(fileUri);
        if (mimeType != null) return mimeType;

        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return mimeType;
    }
}
