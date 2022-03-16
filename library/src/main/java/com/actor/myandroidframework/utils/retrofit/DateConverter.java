package com.actor.myandroidframework.utils.retrofit;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Description: 将Retrofit请求参数Date转换成String, 如果不转, 默认调参数的toString方法 <br />
 *              使用方法: new Retrofit.Builder().addConverterFactory(DateConverter.create()) <br />
 * Author     : ldf <br />
 * Date       : 2019/6/6 on 22:26
 */
public class DateConverter implements Converter<Date, String> {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");

    public static Factory create() {
        return new DateConverterFactory();
    }

    @Nullable
    @Override
    public String convert(Date value) throws IOException {
        return SDF.format(value);
    }

    public static class DateConverterFactory extends Factory {

        //响应体转换器, 参考FastJson中的 Retrofit2ConverterFactory
        @Nullable
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return null;
        }

        //请求体转换器, 参考FastJson中的 Retrofit2ConverterFactory
        @Nullable
        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                              Annotation[] parameterAnnotations,
                                                              Annotation[] methodAnnotations,
                                                              Retrofit retrofit) {
            return null;
        }

        @Nullable
        @Override
        public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return new DateConverter();
        }
    }
}
