package com.actor.sample.info;

import androidx.annotation.NonNull;

import com.hjq.http.annotation.HttpRename;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestBodyStrategy;
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
public class EasyHttpUploadFileInfo implements IRequestApi, IRequestType {

    @HttpRename("file")
    File file;

    String path = "picture/solution";
    String type = "solution";

    public EasyHttpUploadFileInfo(String filePath) {
        this.file = new File(filePath);
    }

    @NonNull
    @Override
    public String getApi() {
        return "http://47.109.77.248/newPat/file/upload";
    }

    @NonNull
    @Override
    public IRequestBodyStrategy getBodyType() {
        return RequestBodyType.FORM;
    }
}
