package com.actor.myandroidframework.utils.okhttputils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.PathUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Description: 下载文件
 * Author     : ldf
 * Date       : 2019/4/17 on 18:10
 */
public abstract class GetFileCallback extends BaseCallback<File> {

    //目标文件存储的文件夹路径
    protected String downloadPathForGetFile;

    //目标文件存储的文件名
    protected String fileNameForGetFile;

    public GetFileCallback(Object tag, String fileName) {
        this(tag, 0, fileName);
    }
    public GetFileCallback(Object tag, int id, String fileName) {
        this(tag, id, null, fileName);
    }
    public GetFileCallback(Object tag, @Nullable String downloadPath, String fileName) {
        this(tag, 0, downloadPath, fileName);
    }

    /**
     * 传入 "文件存储路径" & "文件名"
     * @param downloadPath 文件存储路径, 可以为空, 默认 files 文件夹
     * @param fileName 文件名, 可以为空
     */
    public GetFileCallback(Object tag, int id, @Nullable String downloadPath, String fileName) {
        super(tag, id);
        initPath(downloadPath, fileName);
    }

    /**
     * 初始化
     * @param downloadPath 文件下载路径, 例: context.getExternalFilesDir(null);
     *                                      /storage/emulated/0/Android/data/package/files
     * @param fileName 文件名称, 例: xxx.jpg
     *                 也可从url中解析, 见: {@link #getFileNameFromUrl(String)}
     */
    protected void initPath(String downloadPath, String fileName) {
        if (TextUtils.isEmpty(downloadPath)) downloadPath = PathUtils.getFilesPathExternalFirst();
        if (TextUtils.isEmpty(fileName)) fileName = String.valueOf(System.currentTimeMillis());
        downloadPathForGetFile = downloadPath;
        fileNameForGetFile = fileName;
    }

//    @Override
//    public void onBefore(Request request, int id) {
//        if (fileName == null) {
//            List<String> strings = request.url().pathSegments();
//            logError("下面开始下载文件, 从pathSegments(路径片段)中获取下载下载的文件名");
////            request.url().url().toString();
//            String url = request.url().toString();
//            logError("url =" + url);
//            for (int i = 0; i < strings.size(); i++) {
//                logError(strings.get(i));
//            }
//            fileName = strings.get(strings.size() - 1);
////            fileName = getFileNameFromUrl(url);
//        }
//        super.onBefore(request, id);
//    }

    /**
     * 下载进度
     * @param progress 进度[0, 1]
     * @param total 总大小
     * @param id 请求时传入的id, 默认0
     */
    @Override
    public void inProgress(float progress, long total, int id) {
        super.inProgress(progress, total, id);
//        logFormat("下载文件: progress=%f, total=%d, id=%d", progress, total, id);
    }

    @Override
    public File parseNetworkResponse(Response response, int id) throws IOException {
        ResponseBody body = response.body();
        if (body == null) return null;

        File dir = new File(downloadPathForGetFile);
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
        }
        File file = new File(dir, fileNameForGetFile);
        try(InputStream is = body.byteStream(); FileOutputStream fos = new FileOutputStream(file)) {
            final long total = body.contentLength();
            int len;
            long sum = 0;
            byte[] buf = new byte[2048];
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                    @Override
                    public void run() {
                        inProgress(finalSum * 1.0f / total, total, id);
                    }
                });
            }
            fos.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            body.close();
        }
    }

    /**
     * 从下载url中解析出文件名
     * @param url 下载url
     * @return 返回一个不为空的文件名
     */
    @NonNull
    public static String getFileNameFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("?")) url = url.substring(0, url.lastIndexOf("?"));
            if (url.contains("/")) url = url.substring(url.lastIndexOf("/") + 1);
        }
        if (TextUtils.isEmpty(url)) url = String.valueOf(System.currentTimeMillis());
        return url;
    }
}
