package com.actor.myandroidframework.utils;

import android.app.Application;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Description: 资产目录工具, 可以在Application的时候就开始拷贝文件
 * Author     : ldf
 * Date       : 2018/1/8 on 18:08
 * @version 1.0
 */
public class AssetsUtils {

    protected static final Application CONTEXT = ConfigUtils.APPLICATION;

    /**
     * 拷贝文件到 /data/data/com.package.name(包名)/files 目录
     */
    public static void copyFile2FilesDir(boolean isCover, @NonNull String assetPath, OnListener<String> listener) {
        //distPath: /data/data/com.actor.test/files
        copyFile2Dir(isCover, assetPath, CONTEXT.getFilesDir().getAbsolutePath(), listener);
    }

    /**
     * assets/xxx.txt => /data/data/com.package.name(包名)/files/xxx.txt, 把文件copy到files文件夹里
     * @param isCover 当本地已经存在相同文件的时候,是否覆盖
     * @param assetPath 文件在assets目录下的路径, 示例: xxx.txt(assets/xxx.txt) 或 test/xxx.txt(assets/test/xxx.txt)
     * @param distPath 目的地路径, 例: CONTEXT.getFilesDir().getAbsolutePath()
     * @param listener 拷贝监听
     */
    public static void copyFile2Dir(boolean isCover, @NonNull String assetPath, String distPath, OnListener<String> listener) {
        if (assetPath == null) {
            if (listener != null) listener.onFail(new NullPointerException("需要copy的文件, assetPath = null"));
            return;
        }
        File file = new File(distPath, assetPath);
        if (file.exists()) {
            if (!isCover) {
                if (listener != null) listener.onComplated(file.getAbsolutePath());
                return;
            }
        }
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                //获取资产管理器,从资产目录(asset)读取数据库文件
                AssetManager assets = CONTEXT.getAssets();
                InputStream is = assets.open(assetPath);
                FileOutputStream fos = new FileOutputStream(file);
                int len;
                byte[] arr = new byte[1024*8];
                while ((len = is.read(arr)) != -1){
                    fos.write(arr, 0, len);
                }
                if (is != null) is.close();
                if (fos != null) fos.close();
                return file.getAbsolutePath();
            }
            @Override
            public void onSuccess(String result) {
                if (listener != null) listener.onComplated(result);
            }
            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                if (listener != null) listener.onFail(t);
            }
        });
    }

    /**
     * 读取成String
     * @param assetPath 文件在assets目录下的路径, 示例: china_city_data.json, xxx.txt
     */
    public static void readAssets2String(String assetPath, OnListener<String> listener) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                InputStream is = CONTEXT.getAssets().open(assetPath);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bf = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                }
                if (is != null) is.close();
                if (isr != null) isr.close();
                if (bf != null) bf.close();
                return sb.toString();
            }
            @Override
            public void onSuccess(String result) {
                if (listener != null) listener.onComplated(result);
            }
            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                if (listener != null) listener.onFail(t);
            }
        });
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

    public interface OnListener<T> {

        void onComplated(T result);

        default void onFail(Throwable t) {
            LogUtils.e(t);
        }
    }
}
