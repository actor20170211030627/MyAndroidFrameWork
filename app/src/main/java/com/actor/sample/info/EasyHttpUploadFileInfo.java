package com.actor.sample.info;

import androidx.annotation.NonNull;

import com.hjq.http.annotation.HttpHeader;
import com.hjq.http.annotation.HttpRename;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestBodyStrategy;
import com.hjq.http.config.IRequestHost;
import com.hjq.http.config.IRequestType;
import com.hjq.http.model.RequestBodyType;

import java.io.File;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2024/3/11 on 18
 * @version 1.0
 */
public class EasyHttpUploadFileInfo implements IRequestHost, IRequestApi, IRequestType {

    @HttpHeader
    public int appUseSys = 1;
    @HttpHeader
    public String Authorization = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMzg4ODg4ODg4OCIsImlhdCI6MTcwOTk4MjcxOSwiZXhwIjoxNzEyNTc0NzE5fQ.TfKyK2hGeaO2FaHjgLblwY6Vzn4BVwWsHR9BgXriZug";

    public String path = "picture/solution";
    public String type = "solution";

    @HttpRename("file")
    public File file;

    /**
     * @param filePath 注意: if有读取sd卡权限, 可传入绝对路径 or 沙盒路径 <br />
     *                 if没有读取sd卡权限, 就不能传入绝对路径, 要传入沙盒路径.
     */
    public EasyHttpUploadFileInfo(String filePath) {
        this.file = new File(filePath);
    }

    //自定义host, 不是统一的host
    @NonNull
    @Override
    public String getHost() {
        return "http://47.109.77.248:8201/";
//        return "https://api.apiopen.top/";
    }

    @NonNull
    @Override
    public String getApi() {
        return "newPat/file/upload";
//        return "api/uploadFile";
    }

    @NonNull
    @Override
    public IRequestBodyStrategy getBodyType() {
        return RequestBodyType.FORM;
    }
}
