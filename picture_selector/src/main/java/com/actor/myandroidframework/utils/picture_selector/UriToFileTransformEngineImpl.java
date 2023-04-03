package com.actor.myandroidframework.utils.picture_selector;

import android.content.Context;

import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.utils.SandboxTransformUtils;

/**
 * description: 沙盒文件路径转换
 *
 * @author : ldf
 * date       : 2023/4/1 on 20
 * @version 1.0
 */
public class UriToFileTransformEngineImpl implements UriToFileTransformEngine {
    @Override
    public void onUriToFileAsyncTransform(Context context, String srcPath, String mineType, OnKeyValueResultCallbackListener call) {
        if (call != null) {
            call.onCallback(srcPath, SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType));
        }
    }
}
