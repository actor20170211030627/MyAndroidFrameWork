package com.actor.myandroidframework.utils.MyOkhttpUtils;

import android.support.annotation.Nullable;

import com.actor.myandroidframework.utils.FileUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Description: 下载文件
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/4/17 on 18:10
 */
public abstract class GetFileCallback extends BaseCallback<File> {

    private String downloadPath;//目标文件存储的文件夹路径

    private String fileName;//目标文件存储的文件名

    /**
     * 传入 "文件存储路径" & "文件名"
     * @param downloadPath 文件存储路径, 可以为空
     * @param fileName 文件名, 可以为空
     */
    public GetFileCallback(Object tag, @Nullable String downloadPath, @Nullable String fileName) {
        super(tag);
        if (downloadPath == null) downloadPath = FileUtils.getExternalStorageDir();
        this.downloadPath = downloadPath;
        this.fileName = fileName;
    }

    @Override
    public void onBefore(Request request, int id) {
        if (fileName == null) {
            List<String> strings = request.url().pathSegments();
            logError("下面开始下载文件, 从pathSegments(路径片段)中获取下载下载的文件名");
//            request.url().url().toString();
            String url = request.url().toString();
            logError("url =" + url);
            for (int i = 0; i < strings.size(); i++) {
                logError(strings.get(i));
            }
            fileName = strings.get(strings.size() - 1);
//            fileName = FileUtils.getFileNameFromUrl(url);
        }
        super.onBefore(request, id);
    }

    @Override
    public void inProgress(float progress, long total, int id) {//进度条
        super.inProgress(progress, total, id);
//        logFormat("下载文件: progress=%f, total=%d, id=%d", progress, total, id);
    }

    @Override
    public File parseNetworkResponse(Response response, int id) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();

            long sum = 0;

            File dir = new File(downloadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            fos = new FileOutputStream(file);
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

        } finally {
            try {
                response.body().close();
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }

        }
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
//    public static String getFileNameFromUrl(String url) {
//        if (TextUtils.isEmpty(url)) return null;
//        return url.substring(url.lastIndexOf("/") + 1);
//    }
}
