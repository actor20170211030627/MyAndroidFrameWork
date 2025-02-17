package com.actor.myandroidframework.utils.okhttputils.log;

import androidx.annotation.Nullable;

import com.hjq.http.body.WrapperRequestBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * copy & edit from:
 * https://github.com/JessYanCoding/MVPArt/tree/complete/art/src/main/java/me/jessyan/art/http/log
 */

/**
 * ================================================
 * 解析框架中的网络请求和响应结果,并以日志形式输出,调试神器
 * 可使用 {@link GlobalConfigModule.Builder#printHttpLogLevel(Level)} 控制或关闭日志
 * <p>
 * Created by JessYan on 7/1/2016.
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class RequestInterceptor implements Interceptor {

    /*FormatPrinter*/DefaultFormatPrinter mPrinter;

    //added
    protected static boolean isContainEasyHttp;

    public RequestInterceptor() {
        mPrinter = new DefaultFormatPrinter();

        //added
        try {
            Class.forName("com.hjq.http.EasyHttp");
            isContainEasyHttp = true;
        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
            isContainEasyHttp = false;
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        //打印请求信息
        if (request.body() != null && isParseable(request.body().contentType())) {
            mPrinter.printJsonRequest(request, parseParams(request));
        } else {
            mPrinter.printFileRequest(request);
        }


        long t1 = System.nanoTime();
        Response originalResponse;
        try {
            originalResponse = chain.proceed(request);
        } catch (Exception e) {
            e.printStackTrace();
//            Timber.w("Http Error: " + e);
            throw e;
        }
        long t2 = System.nanoTime();

        ResponseBody responseBody = originalResponse.body();

        //打印响应结果
        String bodyString = null;
        if (responseBody != null && isParseable(responseBody.contentType())) {
            bodyString = printResult(request, originalResponse, /*logResponse*/true);
        }

        final List<String> segmentList = request.url().encodedPathSegments();
        /**
         * edited: 会过滤敏感请求头: Authorization, Cookie, Proxy-Authorization, Set-Cookie
         * 见: {@link okhttp3.internal.Util#isSensitiveHeader(String)}
         */
//        final String header = originalResponse.headers().toString();
        final Headers headers = originalResponse.headers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.name(i));
            sb.append(": ");
            sb.append(headers.value(i));
            sb.append("\n");
        }
        String header = sb.toString();
        final int code = originalResponse.code();
        final boolean isSuccessful = originalResponse.isSuccessful();
        final String message = originalResponse.message();
        final String url = originalResponse.request().url().toString();

        if (responseBody != null && isParseable(responseBody.contentType())) {
            mPrinter.printJsonResponse(TimeUnit.NANOSECONDS.toMillis(t2 - t1), isSuccessful,
                    code, header, responseBody.contentType(), bodyString, segmentList, message, url);
        } else {
            mPrinter.printFileResponse(TimeUnit.NANOSECONDS.toMillis(t2 - t1),
                    isSuccessful, code, header, segmentList, message, url);
        }

//        if (mHandler != null)//这里可以比客户端提前一步拿到服务器返回的结果,可以做一些操作,比如token超时,重新获取
//            return mHandler.onHttpResultResponse(bodyString, chain, originalResponse);

        return originalResponse;
    }

    /**
     * 打印响应结果
     *
     * @param request     {@link Request}
     * @param response    {@link Response}
     * @param logResponse 是否打印响应结果
     * @return 解析后的响应结果
     * @throws IOException
     */
    @Nullable
    private String printResult(Request request, Response response, boolean logResponse) throws IOException {
        try {
            //读取服务器返回的结果
            ResponseBody responseBody = response.newBuilder().build().body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            //获取content的压缩类型
            String encoding = response
                    .headers()
                    .get("Content-Encoding");

            Buffer clone = buffer.clone();

            //解析response content
            return parseContent(responseBody, encoding, clone);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }


    /**
     * 解析服务器响应的内容
     *
     * @param responseBody {@link ResponseBody}
     * @param encoding     编码类型
     * @param clone        克隆后的服务器响应内容
     * @return 解析后的响应结果
     */
    private String parseContent(ResponseBody responseBody, String encoding, Buffer clone) {
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {//content 使用 gzip 压缩
            return ZipHelper.decompressForGzip(clone.readByteArray(), convertCharset(charset));//解压
        } else if (encoding != null && encoding.equalsIgnoreCase("zlib")) {//content 使用 zlib 压缩
            return ZipHelper.decompressToStringForZlib(clone.readByteArray(), convertCharset(charset));//解压
        } else {//content 没有被压缩, 或者使用其他未知压缩方式
            return clone.readString(charset);
        }
    }

    /**
     * 解析请求服务器的请求参数
     *
     * @param request {@link Request}
     * @return 解析后的请求信息
     * @throws UnsupportedEncodingException
     */
    public static String parseParams(Request request) throws UnsupportedEncodingException {
        try {
            RequestBody body = request.newBuilder().build().body();
            if (body == null) return "";
            Buffer requestbuffer = new Buffer();
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = body.contentType();
            if (contentType != null) {
                Charset charset2 = contentType.charset(charset);
                if (charset2 != null) charset = charset2;
            }
            String json = "";

            //added, 轮轮哥的包装类
            if (isContainEasyHttp) {
                if (body instanceof WrapperRequestBody) {
                    body = ((WrapperRequestBody) body).getRequestBody();
                }
            }
            //added: 含文件表单的解析
            if (body instanceof MultipartBody) {
                StringBuilder sb = new StringBuilder();
                List<MultipartBody.Part> parts = ((MultipartBody) body).parts();
                for (int i = 0; i < parts.size(); i++) {
                    MultipartBody.Part part = parts.get(i);
                    Headers headers = part.headers();
                    if (i > 0) sb.append("\n");
                    if (headers != null && headers.size() > 0) {
                        sb.append(headers.value(0));
                    }
                    RequestBody body1 = part.body();

                    /**
                     * null                     : 未设置
                     * text/plain               : type = text, subtype = plain
                     * text/plain; charset=utf-8: 纯文本。它是Content-Type的默认值。在浏览器中，这种类型的内容将直接显示在页面上，不会被解析为HTML。
                     * text/html                : 包含HTML标签的文本。在浏览器中，这种类型的内容将被解析为HTML，并且显示为网页。
                     * image/jpeg, image/png    : 图片格式, (表单图片打印太长了)
                     * audio/mpeg               : MP3格式的音频
                     * video/mp4                : mp4视频
                     * application/json         : json数据
                     * application/xml          : xml数据
                     * application/x-www-form-urlencoded: 表单数据(不含文件)
                     * application/octet-stream : 二进制流
                     * application/x-msgpack    :
                     * multipart/form-data      : 表单数据, 可包含文件
                     */
                    MediaType mediaType = body1.contentType();
//                    LogUtils.errorFormat("mediaType=%s", mediaType);
//                    long contentLength = body1.contentLength();
//                    boolean duplex = body1.isDuplex();
//                    boolean oneShot = body1.isOneShot();
//                    String s = body1.toString();
                    if (mediaType == null || isText(mediaType)
                            || isJson(mediaType) || isXml(mediaType)
//                            || isForm(mediaType)
                    ) {
                        body1.writeTo(requestbuffer);
                        sb.append(", value=\"");
                        sb.append(requestbuffer.readString(charset));
//                        requestbuffer.flush();
                        sb.append("\"");
                    }
                }
                json = sb.toString();
            } else {
                body.writeTo(requestbuffer);
//                Charset charset = Charset.forName("UTF-8");
//                MediaType contentType = body.contentType();
//                if (contentType != null) {
//                    charset = contentType.charset(charset);
//                }
                /*String */json = requestbuffer.readString(charset);
            }

            if (UrlEncoderUtils.hasUrlEncoded(json)) {
                try {
                    /**
                     * TODO: 2024/1/8 以后去官网看看作者怎么改的
                     * Edited:
                     * https://github.com/JessYanCoding/MVPArt/issues/18
                     * if自己上传服务起的String参数包含 "%u7", 就会解码错误, 导致崩溃
                     */
                    json = URLDecoder.decode(json, convertCharset(charset));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            return CharacterHandler.jsonFormat(json);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 是否可以解析
     *
     * @param mediaType {@link MediaType}
     * @return {@code true} 为可以解析
     */
    public static boolean isParseable(MediaType mediaType) {
        if (mediaType == null || mediaType.type() == null) return false;
        return isText(mediaType) || isPlain(mediaType)
                || isJson(mediaType) || isForm(mediaType)
                || isFormData(mediaType)    //added
                || isHtml(mediaType) || isXml(mediaType);
    }

    public static boolean isText(MediaType mediaType) {
        if (mediaType == null || mediaType.type() == null) return false;
        return mediaType.type().equals("text");
    }

    public static boolean isPlain(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("plain");
    }

    public static boolean isJson(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("json");
    }

    public static boolean isXml(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("xml");
    }

    public static boolean isHtml(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("html");
    }

    //edited 纯表单
    public static boolean isForm(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("x-www-form-urlencoded");
    }

    //added 带文件的表单
    public static boolean isFormData(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("form-data");
    }

    public static String convertCharset(Charset charset) {
        String s = charset.toString();
        int i = s.indexOf("[");
        if (i == -1)
            return s;
        return s.substring(i + 1, s.length() - 1);
    }

}
