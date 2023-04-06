package com.actor.picture_selector.utils;

import android.content.Context;
import android.net.Uri;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.utils.DateUtils;

import java.io.File;
import java.util.ArrayList;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * description: 图片压缩 <br />
 * 注意: 需要压缩图片, 请添加依赖! <br /> <br />
 *
 * // 图片压缩 (按需引入): 如果你选择图片/拍照后, 需要使用压缩图片功能, 需要添加下面这行依赖 <br />
 * implementation 'io.github.lucksiege:compress:v3.10.9' <br />
 * <br />
 * 具体更多的压缩API见: <a href="https://github.com/Curzibn/Luban" target="_blank">https://github.com/Curzibn/Luban</a>
 * <br />
 * @author : ldf
 * date       : 2023/4/1 on 13
 * @version 1.0
 */
public class CompressFileEngineImpl implements CompressFileEngine {
    @Override
    public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
        Luban.with(context).load(source).ignoreBy(300).setRenameListener(new OnRenameListener() {
            @Override
            public String rename(String filePath) {
                int indexOf = filePath.lastIndexOf(".");
                String postfix = indexOf != -1 ? filePath.substring(indexOf) : ".jpg";
                return DateUtils.getCreateFileName("CMP_") + postfix;
            }
        }).filter(new CompressionPredicate() {
            @Override
            public boolean apply(String path) {
                if (PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)) {
                    return true;
                }
                return !PictureMimeType.isUrlHasGif(path);
            }
        }).setCompressListener(new OnNewCompressListener() {
            @Override
            public void onStart() {
            }
            @Override
            public void onSuccess(String source, File compressFile) {
                if (call != null) {
                    call.onCallback(source, compressFile.getAbsolutePath());
                }
            }
            @Override
            public void onError(String source, Throwable e) {
                if (call != null) {
                    call.onCallback(source, null);
                }
            }
        }).launch();
    }
}
