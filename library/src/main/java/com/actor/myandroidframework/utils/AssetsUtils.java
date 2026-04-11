package com.actor.myandroidframework.utils;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Description: 资产目录工具, 右击main → New → Folder → Assets Folder, 会在"src/main" 目录下创建文件夹: assets <br />
 * Author     : ldf <br />
 * Date       : 2018/1/8 on 18:08
 * @version 1.0
 */
public class AssetsUtils {

    protected static final Application CONTEXT = ConfigUtils.APPLICATION;

    /**
     * 将'assets'里的文件拷贝到File目录: /data/data/package/files
     * @param assetPath 文件在assets目录下的路径, 示例: xxx.txt(assets/xxx.txt),  fonts/Sofia.otf(assets/fonts/Sofia.otf)
     * @return 文件路径 or null
     */
    @Nullable
    public static String copyFile2InternalFilesDir(boolean isCover, @NonNull String assetPath) {
        return copyFile2Dir(isCover, assetPath, PathUtils.getInternalAppFilesPath());
    }

    /**
     * 将'assets'里的文件拷贝到数据库目录: /data/data/package/databases
     * @param dbPath 数据库在'assets'里的路径, 例: xxx.db(assets/xxx.db),  dbs/users.db3(assets/dbs/users.db3)
     * @return 文件路径 or null
     */
    @Nullable
    public static String copyFile2InternalDbsDir(boolean isCover, @NonNull String dbPath) {
        return copyFile2Dir(isCover, dbPath, PathUtils.getInternalAppDbsPath());
    }

    /**
     * 将'assets'里的文件拷贝到指定文件夹
     * @param isCover 当指定文件夹里已经存在相同名称文件的时候, 是否覆盖
     * @param assetPath 文件在'assets'目录下的路径, 示例: xxx.txt(assets/xxx.txt),  fonts/Sofia.otf(assets/fonts/Sofia.otf)
     * @param distPath 目的地路径, 例: CONTEXT.getFilesDir().getAbsolutePath()
     * @return 文件路径 or null
     */
    @Nullable
    public static String copyFile2Dir(boolean isCover, @NonNull String assetPath, String distPath) {
        if (TextUtils.isEmpty(assetPath) || TextUtils.isEmpty(distPath)) return null;
        File file = new File(distPath, assetPath);
        if (file.exists() && !isCover) return file.getAbsolutePath();
        boolean isSuccess = ResourceUtils.copyFileFromAssets(assetPath, file.getAbsolutePath());
        if (isSuccess) return file.getAbsolutePath();
        return null;
    }

    /**
     * 将'assets'里的文件读取成String
     * @param assetPath 文件在assets目录下的路径, 示例: china_city_data.json, xxx.txt
     * @param charsetName 编码格式, 可传null, 例: UTF-8, {@link java.nio.charset.StandardCharsets}
     */
    public static String readAssets2String(String assetPath, @Nullable String charsetName) {
        return ResourceUtils.readAssets2String(assetPath, charsetName);
    }

    /**
     * 将'assets'里的文件一行一行地读, 返回List
     * @param assetsPath 文件在assets目录下的路径, 示例: china_city_data.json, xxx.txt
     * @param charsetName 编码格式
     */
    public static List<String> readAssets2List(final String assetsPath, final String charsetName) {
        return ResourceUtils.readAssets2List(assetsPath, charsetName);
    }

    /**
     * 获取assets/文件夹 里的所有文件/文件夹(如果文件夹里没文件, 获取不到这个文件夹), 无序的
     * @param assetsDirName assets/目录下文件夹名称, 示例: emoji: 读取 assets/emoji/ 文件夹里的所有文件/文件夹
     *                                                      "": 直接读取 assets 文件夹里的所有文件/文件夹
     * @return 所有文件/夹 名称
     */
    public static String[] getFiles(String assetsDirName) {
        if (assetsDirName == null) return null;
        try {
            return CONTEXT.getAssets().list(assetsDirName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将'assets'里的文件读取成流
     * @param assetPath 文件在assets目录下的路径, 示例: china_city_data.json, xxx.txt
     * @return 返回一个输入流, 注意: 要关流
     */
    public static InputStream open(@NonNull String assetPath) throws IOException {
        //参2: 读取成哪种流, 默认: ACCESS_STREAMING
        return CONTEXT.getAssets().open(assetPath, AssetManager.ACCESS_STREAMING);
    }


    /** Assets转Bitmap
     * @param assetsPath 文件在assets目录下的路径, 示例: xxx.png, pics/xxx1.jpg
     */
    @Nullable
    public static Bitmap toBitmap(@NonNull String assetsPath) {
        try {
            InputStream is = open(assetsPath);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
