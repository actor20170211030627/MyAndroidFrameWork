package com.actor.myandroidframework.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ImageUtils;

import java.io.File;

/**
 * description: <a href="https://blog.csdn.net/fromVillageCoolBoy/article/details/116987633">Android Base64</a> <br />
 *              Base64编码是一种编码方式，可以把二进制数据编码为可见的字符数据，包含64个字符，A-Z, a-z,0-9,+,/,除此之外还有一个填充字符是后缀等号’=’。<br />
 *              由于Base64编码是六位一个字符，而一个字节占八位，所以编码的时候如果字节不是三的倍数，需要添加零值，注意，由于一个字节占六位，所以编码之后高两位会补0。<br />
 *              <b>特性</b>
 *              <ol>
 *                  <li>标准Base64编码包含64个字符A-Z, a-z,0-9,+,/,=</li>
 *                  <li>url safe的Base64编码将+,/换成-,_</li>
 *                  <li>三个字符会变成四个字符</li>
 *                  <li>解码Base64编码时，遇到=即可知道字符结束</li>
 *                  <li>每76个字符增加一个换行符</li>
 *              </ol>
 *              <b>flag</b> <br />
 *              由于Android的Base64编码是默认换行，因此在进行服务器验证的时候，会出现验证失败的情况，这是由于服务器那边的解码不支持换行符模式，所以编码时需要增加flag标志，android总共有以下几个flag。<br />
 *              <ol>
 *                  <li>{@link Base64#DEFAULT} 默认模式</li>
 *                  <li>{@link Base64#NO_PADDING} 过滤结束符=(如果有)</li>
 *                  <li>{@link Base64#NO_WRAP} 过滤掉换行符</li>
 *                  <li>{@link Base64#CRLF} 意思就是使用CR、LF这一对作为一行的结尾而不是Unix风格的LF。如果同时指定NO_WRAP则不起作用。</li>
 *                  <li>{@link Base64#URL_SAFE} 将+、/换成-、_</li>
 *                  <li>{@link Base64#NO_CLOSE} 通常与Base64OutputStream一起使用，是传递给Base64OutputStream的标志指示它不应关闭正在包装的输出流</li>
 *              </ol>
 *
 * @author : ldf
 * date       : 2023/7/1 on 23
 * @version 1.0
 */
public class Base64Utils {
    /**
     * 编码
     */
    @Nullable
    public static byte[] encode(String str) {
        return encode(str, Base64.DEFAULT);
    }
    /**
     * 编码
     * @param flags 见顶部说明
     */
    @Nullable
    public static byte[] encode(String str, int flags) {
        if (str == null) return null;
        return Base64.encode(str.getBytes(/*StandardCharsets.UTF_8*/), flags);
    }

    @Nullable
    public static String encodeToString(String str) {
        return encodeToString(str, Base64.DEFAULT);
    }
    @Nullable
    public static String encodeToString(String str, int flags) {
        if (str == null) return null;
        return encodeToString(str.getBytes(/*, StandardCharsets.UTF_8*/), flags);
    }

    @Nullable
    public static String encodeToString(byte[] bytes) {
        return encodeToString(bytes, Base64.DEFAULT);
    }
    /**
     * 可以将文件, bitmap.readBytes() 等转换成String
     */
    @Nullable
    public static String encodeToString(byte[] bytes, int flags) {

        if (bytes == null) return null;
        return Base64.encodeToString(bytes, flags);
    }
    /**
     * 将文件转成Base64字符串 (有时候调用接口上传图片会用得到.)
     */
    @Nullable
    public static String encodeToString(File file, @Nullable FileIOUtils.OnProgressUpdateListener listener) {
        byte[] bytes = FileIOUtils.readFile2BytesByStream(file, listener);
        return encodeToString(bytes);
    }



    /**
     * 解码
     * @param str 可以是普通字符串 <br />
     *            也可以是服务器返回的文件流, 然后用 FileOutputStream.write(byte[])写出到文件
     */
    @Nullable
    public static byte[] decode(String str) {
        return decode(str, Base64.DEFAULT);
    }
    /**
     * 解码
     * @param flags 见顶部说明
     */
    @Nullable
    public static byte[] decode(String str, int flags) {
        if (str == null) return null;
        return Base64.decode(str, flags);
    }
    @Nullable
    public static byte[] decode(byte[] bytes) {
        return decode(bytes, Base64.DEFAULT);
    }
    @Nullable
    public static byte[] decode(byte[] bytes, int flags) {
        if (bytes == null) return null;
        return Base64.decode(bytes, flags);
    }

    /**
     * 解码成字符串
     */
    @Nullable
    public static String decodeToString(byte[] bytes) {
        return decodeToString(bytes, Base64.DEFAULT);
    }
    @Nullable
    public static String decodeToString(byte[] bytes, int flags) {
        byte[] decode = decode(bytes, flags);
        if (decode == null) return null;
        return new String(decode/*, StandardCharsets.UTF_8*/);
    }
    @Nullable
    public static String decodeToString(String str) {
        return decodeToString(str, Base64.DEFAULT);
    }
    @Nullable
    public static String decodeToString(String str, int flags) {
        byte[] decode = decode(str, flags);
        if (decode == null) return null;
        return new String(decode/*, StandardCharsets.UTF_8*/);
    }
    /**
     * 可以解码服务器返回的图片Base64编码
     * @param str 服务器返回的图片Base64: <br />
     *           <code>String base64Str = response.body.string();</code>
     */
    @Nullable
    public static Bitmap decodeToBitmap(String str) {
        byte[] decode = decode(str);
        return ImageUtils.bytes2Bitmap(decode);
    }
}
