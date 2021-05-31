package com.actor.myandroidframework.utils;

import android.app.Application;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
     * 拷贝文件到File目录: /data/data/com.package.name(包名)/files
     */
    public static void copyFile2FilesDir(boolean isCover, @NonNull String assetPath, @Nullable OnListener<String> listener) {
        //distPath: /data/data/com.actor.test/files
        copyFile2Dir(isCover, assetPath, CONTEXT.getFilesDir().getAbsolutePath(), listener);
    }

    /**
     * 拷贝文件到数据库目录: /data/data/com.package.name(包名)/databases
     * @param dbNames 数据库名称, 例: address.db, users.db3 等...
     */
    public static void copyFile2getDatabaseDir(boolean isCover, @NonNull String dbNames, @Nullable OnListener<String> listener) {
        //distPath: /data/0/com.actor.test/databases
        copyFile2Dir(isCover, dbNames, CONTEXT.getDatabasePath(dbNames).getParent(), listener);
    }

    /**
     * assets/xxx.txt => /data/data/com.package.name(包名)/files/xxx.txt, 把文件copy到files文件夹里
     * @param isCover 当本地已经存在相同文件的时候,是否覆盖
     * @param assetPath 文件在assets目录下的路径, 示例: xxx.txt(assets/xxx.txt) 或 test/xxx.txt(assets/test/xxx.txt)
     * @param distPath 目的地路径, 例: CONTEXT.getFilesDir().getAbsolutePath()
     * @param listener 拷贝监听
     */
    public static void copyFile2Dir(boolean isCover, @NonNull String assetPath, String distPath, @Nullable OnListener<String> listener) {
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
                try (InputStream is = assets.open(assetPath); FileOutputStream fos = new FileOutputStream(file)) {
                    int len;
                    byte[] arr = new byte[1024 * 8];
                    while ((len = is.read(arr)) != -1) {
                        fos.write(arr, 0, len);
                    }
                    return file.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
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
    public static void readAssets2String(String assetPath, @Nullable OnListener<String> listener) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                try (InputStream is = CONTEXT.getAssets().open(assetPath); InputStreamReader isr = new InputStreamReader(is); BufferedReader bf = new BufferedReader(isr)) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bf.readLine()) != null) {
                        sb.append(line);
                    }
                    return sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
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
        /**
         * @param result 拷贝成功路径
         */
        void onComplated(T result);

        /**
         * 拷贝失败
         */
        void onFail(Throwable t);
    }
}
