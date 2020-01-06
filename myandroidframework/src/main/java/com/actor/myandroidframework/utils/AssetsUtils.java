package com.actor.myandroidframework.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 资产目录工具, 可以在Application的时候就开始拷贝.把文件传到/data/data/包名/files/
 * Author     : 李大发
 * Date       : 2018/1/8 on 18:08
 * @version 1.0
 */

public class AssetsUtils {

    /**
     * 把文件传到/data/data/包名/files/xxx.xxx
     * @param isCover 当本地已经存在相同文件的时候,是否覆盖
     * @param assetPath 文件在assets目录下的路径, 示例: BuSuanZi.txt 或 test/xxx.xxx(assets的test文件夹下)
     */
    public static void copyFile2FilesDir(Context context, boolean isCover, String assetPath) {
        File file = new File(context.getFilesDir().getAbsolutePath(), assetPath);
//        file.getPath():           /data/data/com.actor.test/files/BuSuanZi.txt
//        file.getAbsolutePath():   /data/data/com.actor.test/files/BuSuanZi.txt
//        file.getCanonicalPath():  /data/data/com.actor.test/files/BuSuanZi.txt
        if (file.exists()) {
            if (!isCover) return;
        }
        InputStream is = null;
        FileOutputStream fos = null;
        //获取资产管理器,从资产目录(asset)读取数据库文件, 然后写入到本地路径中
        AssetManager assets = context.getAssets();
        try {
            is = assets.open(assetPath);
            fos = new FileOutputStream(file);
            int len;
            byte[] arr = new byte[1024*8];
            while ((len = is.read(arr)) != -1){
                fos.write(arr, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取成String
     * @param assetPath 文件在assets目录下的路径, 示例: china_city_data.json
     */
    public static synchronized String getString(Context context, String assetPath) {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader bf = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = context.getAssets().open(assetPath);
            isr = new InputStreamReader(is);
            bf = new BufferedReader(isr);
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (isr != null) isr.close();
                if (bf != null) bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 读取表情配置文件
     * @param context
     * @return
     */
    public static List<String> getEmojiFile(Context context) {
        try {
            List<String> list = new ArrayList<>();
            InputStream in = context.getResources().getAssets().open("emoji");//emoji是一个文件,不是文件夹
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取assets下所有的文件/文件夹(如果文件夹里没文件, 获取不到这个文件夹)
     * @return 所有文件/夹 名称
     */
    public static String[] getFiles(Context context) {
        try {
            return context.getAssets().list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取assets/文件夹 里的所有文件, 无序的
     * @param context
     * @param assetsDirName assets/文件夹名称
     * @return 所有文件/夹 名称
     */
    public static String[] getFilesFromDir(Context context, String assetsDirName) {
        try {
            return context.getAssets().list(assetsDirName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
