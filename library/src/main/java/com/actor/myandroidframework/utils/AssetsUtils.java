package com.actor.myandroidframework.utils;

import android.app.Application;
import android.content.res.AssetManager;
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
 * Description: 资产目录工具, 可以在Application的时候就开始拷贝文件 <br />
 * Author     : ldf <br />
 * Date       : 2018/1/8 on 18:08
 * @version 1.0
 */
public class AssetsUtils {

    protected static final Application CONTEXT = ConfigUtils.APPLICATION;

    /**
     * 拷贝文件到File目录: /data/data/package/files
     * @return 文件路径 or null
     */
    @Nullable
    public static String copyFile2InternalFilesDir(boolean isCover, @NonNull String assetPath) {
        return copyFile2Dir(isCover, assetPath, PathUtils.getInternalAppFilesPath());
    }

    /**
     * 拷贝文件到数据库目录: /data/data/package/databases
     * @param dbNames 数据库名称, 例: address.db, users.db3 等...
     * @return 文件路径 or null
     */
    @Nullable
    public static String copyFile2InternalDbsDir(boolean isCover, @NonNull String dbNames) {
        return copyFile2Dir(isCover, dbNames, PathUtils.getInternalAppDbsPath());
    }

    /**
     * assets/xxx.txt => /data/data/package/files/xxx.txt, 把文件copy到files文件夹里
     * @param isCover 当本地已经存在相同文件的时候,是否覆盖
     * @param assetPath 文件在assets目录下的路径, 示例: xxx.txt(assets/xxx.txt) 或 test/xxx.txt(assets/test/xxx.txt)
     * @param distPath 目的地路径, 例: CONTEXT.getFilesDir().getAbsolutePath()
     * @return 文件路径 or null
     */
    @Nullable
    public static String copyFile2Dir(boolean isCover, @NonNull String assetPath, String distPath) {
        if (TextUtils.isEmpty(assetPath)) return null;
        File file = new File(distPath, assetPath);
        if (file.exists() && !isCover) return file.getAbsolutePath();
        boolean isSuccess = ResourceUtils.copyFileFromAssets(assetPath, file.getAbsolutePath());
        if (isSuccess) return file.getAbsolutePath();
        return null;
    }

    /**
     * 读取成String
     * @param assetPath 文件在assets目录下的路径, 示例: china_city_data.json, xxx.txt
     * @param charsetName 编码格式, 可传null, 例: UTF-8, {@link java.nio.charset.StandardCharsets}
     */
    public static String readAssets2String(String assetPath, @Nullable String charsetName) {
        return ResourceUtils.readAssets2String(assetPath, charsetName);
    }

    /**
     * 一行一行地读, 返回List
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
        try {
            return CONTEXT.getAssets().list(assetsDirName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将
     * @param assetPath 文件在assets目录下的路径, 示例: china_city_data.json, xxx.txt
     * @return 返回一个输入流, 注意: 要关流
     */
    public static InputStream open(String assetPath) throws IOException {
        //参2: 读取成哪种流, 默认: ACCESS_STREAMING
        return CONTEXT.getAssets().open(assetPath, AssetManager.ACCESS_STREAMING);
    }
}
