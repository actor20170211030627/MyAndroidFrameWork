package com.actor.myandroidframework.utils;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.FontRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * description: 字体工具类 <br />
 * 经过测试:
 * <table>
 *     <tr>
 *         <th>№</th>
 *         <th>方法</th>
 *         <th>效果</th>
 *     </tr>
 *     <tr>
 *         <td>1</td>
 *         <td>{@link #getFontFromResource(int)}</td>
 *         <td>{@link TextView#setTypeface(Typeface, int) tv.setTypeface(typeface, Typeface.BOLD);} 3种字体的加粗都有问题</td>
 *     </tr>
 *     <tr>
 *         <td>2</td>
 *         <td>{@link #getFontFromResource(int, boolean, boolean)}</td>
 *         <td>{@link TextView#setTypeface(Typeface)} 效果非常好</td>
 *     </tr>
 *     <tr>
 *         <td>3</td>
 *         <td>{@link #getFontFromResource(int, int, boolean)}</td>
 *         <td>{@link TextView#setTypeface(Typeface)} 效果非常好</td>
 *     </tr>
 *     <tr>
 *         <td>4</td>
 *         <td>{@link #createFromAsset(String)}</td>
 *         <td>{@link TextView#setTypeface(Typeface, int) tv.setTypeface(typeface, Typeface.BOLD);} 3种字体的加粗都有问题</td>
 *     </tr>
 *     <tr>
 *         <td>5</td>
 *         <td>{@link #createFromAsset(String, boolean, boolean)}</td>
 *         <td>{@link TextView#setTypeface(Typeface)} 效果非常好</td>
 *     </tr>
 *     <tr>
 *         <td>6</td>
 *         <td>{@link #createFromAsset(String, int, boolean)}</td>
 *         <td>{@link TextView#setTypeface(Typeface)} 效果非常好</td>
 *     </tr>
 * </table>
 *
 * @author : ldf
 * date       : 2024/11/26 on 14
 */
public class FontUtils {

    //下方常数是字体字重(粗细), 从小到大
    public static final int WEIGHT_THIN = 100;          //细
    public static final int WEIGHT_EXTRA_LIGHT = 200;   //
    public static final int WEIGHT_LIGHT = 300;         //
    public static final int WEIGHT_NORMAL = 400;        //正常
    public static final int WEIGHT_MEDIUM = 500;        //中等
    public static final int WEIGHT_SEMI_BOLD = 600;     //半粗
    public static final int WEIGHT_BOLD = 700;          //粗
    public static final int WEIGHT_EXTRA_BOLD = 800;    //超粗
    public static final int WEIGHT_Black = 900;         //黑体

    protected static final String PACKAGE_NAME = ConfigUtils.APPLICATION.getPackageName();

    /**
     * Cache for Typeface objects dynamically loaded from assets.
     */
    protected static final LruCache<String, Typeface> sTypefaceCache = new LruCache<>(16);

    /**
     * 获取字体
     * @param fontResId R.font.my_custom_font
     * @return
     */
    @Nullable
    public static Typeface getFontFromResource(@FontRes int fontResId) {
        Typeface typeface = findFromCache(fontResId, false, false);
        if (typeface != null) return typeface;
        //                                   Android 8.0(Oreo), Api 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return ConfigUtils.APPLICATION.getResources().getFont(fontResId);
        }
        return ResourcesCompat.getFont(ConfigUtils.APPLICATION, fontResId);
    }

    /**
     * 获取字体
     * @param fontResId R.font.my_custom_font
     * @param isBold 是否加粗
     * @param isItalic 是否是斜体
     * @return
     */
    @Nullable
    public static Typeface getFontFromResource(@FontRes int fontResId, boolean isBold, boolean isItalic) {
        //if在xml中使用过style对应字体, 第1次调用本方法就能找到, 但是找到的字体返回后没有style
//        @SuppressLint("RestrictedApi")
//        Typeface typeface = TypefaceCompat.findFromCache(ConfigUtils.APPLICATION.getResources(), fontResId, style);

        Typeface typeface = findFromCache(fontResId, isBold, isItalic);
        if (typeface != null) return typeface;

        if (true) {
            return getFontFromResource(fontResId, isBold ? WEIGHT_BOLD : WEIGHT_NORMAL, isItalic);
        }

        int style = (isBold ? Typeface.BOLD : Typeface.NORMAL) + (isItalic ? Typeface.ITALIC : Typeface.NORMAL);
        //有问题, 字体不能加粗, 不能斜体
        @SuppressLint("RestrictedApi")
        Typeface typeface2 = TypefaceCompat.createFromResourcesFontFile(ConfigUtils.APPLICATION,
                ConfigUtils.APPLICATION.getResources(), fontResId, "", style);
        return typeface2;
    }

    /**
     * 获取字体
     * @param fontResId R.font.my_custom_font
     * @param weight 字重（粗细程度）[1~1000], 默认400: 看{@link Typeface#create(Typeface, int, boolean)}这个方法的参2
     * @param isItalic 是否是斜体
     * @return
     */
    @NonNull
    public static Typeface getFontFromResource(@FontRes int fontResId,
                                               @IntRange(from = 1, to = 1000) int weight,
                                               boolean isItalic) {
        Typeface typeface = findFromCache(fontResId, weight, isItalic);
        if (typeface != null) return typeface;

        typeface = getFontFromResource(fontResId);
        //                                     Android 9(Pie), Api 28
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Typeface.create(typeface, weight, isItalic);
        }

        int style = (weight >= WEIGHT_BOLD ? Typeface.BOLD : Typeface.NORMAL) + (isItalic ? Typeface.ITALIC : Typeface.NORMAL);
        //有问题, 找到的字体是系统默认字体
        return TypefaceCompat.create(ConfigUtils.APPLICATION, typeface, style);


//        @SuppressLint("RestrictedApi")
//        FontsContractCompat.FontInfo fontInfo = new FontsContractCompat.FontInfo(Uri.EMPTY, 1, 1, true, 1);
//        FontsContractCompat.FontInfo[] fonts = { fontInfo };
//        @SuppressLint("RestrictedApi")
//        //              @Nullable CancellationSignal cancellationSignal, @NonNull FontInfo[] fonts, int style
//        Typeface fromFontInfo = TypefaceCompat.createFromFontInfo(ConfigUtils.APPLICATION, null, fonts, style);
//        return fromFontInfo;
    }

    /**
     * 获取字体
     * @param path src/main/assets 下的目录, 例: fonts/Xxx.ttf, fonts/Xxx.otf
     * @return
     */
    @Nullable
    public static Typeface createFromAsset(@NonNull String path) {
        if (TextUtils.isEmpty(path)) return null;
        Typeface typeface = findFromCache(path, false, false);
        if (typeface != null) return typeface;

        return Typeface.createFromAsset(ConfigUtils.APPLICATION.getAssets(), path);
    }

    /**
     * 获取字体
     * @param path src/main/assets 下的目录, 例: fonts/Xxx.ttf, fonts/Xxx.otf
     * @param isBold 是否加粗
     * @param isItalic 是否是斜体
     * @return
     */
    @Nullable
    public static Typeface createFromAsset(@NonNull String path, boolean isBold, boolean isItalic) {
        if (TextUtils.isEmpty(path)) return null;
        Typeface typeface = findFromCache(path, isBold, isItalic);
        if (typeface != null) return typeface;
        return createFromAsset(path, isBold ? WEIGHT_BOLD : WEIGHT_NORMAL, isItalic);

    }
    /**
     * 获取字体
     * @param path src/main/assets 下的目录, 例: fonts/Xxx.ttf, fonts/Xxx.otf
     * @param weight 字重（粗细程度）[1~1000], 默认400: 看{@link Typeface#create(Typeface, int, boolean)}这个方法的参2
     * @param isItalic 是否是斜体
     * @return
     */
    @Nullable
    public static Typeface createFromAsset(@NonNull String path,
                                           @IntRange(from = 1, to = 1000) int weight,
                                           boolean isItalic) {
        if (TextUtils.isEmpty(path)) return null;
        Typeface typeface = findFromCache(path, weight, isItalic);
        if (typeface != null) return typeface;

        //                                     Android 9(Pie), Api 28
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            typeface = createFromAsset(path);
            return Typeface.create(typeface, weight, isItalic);
        }


        /**
         * {@link 注意:} <b>if低版本手机(小于 Android 9)</b>, 返回的有些字体设置后不一定有效果, 例:
         * <table>
         *     <tr>
         *         <th>№</th>
         *         <th>字体</th>
         *         <th>weight(字重)</th>
         *         <th>italic(斜体)</th>
         *         <th>weight&italic</th>
         *     </tr>
         *     <tr>
         *         <td>1</td>
         *         <td>AlimamaFangYuanTiVF-Thin.ttf</td>
         *         <td>无效</td>
         *         <td>无效</td>
         *         <td>无效</td>
         *     </tr>
         *     <tr>
         *         <td>2</td>
         *         <td>FontAwesome.ttf</td>
         *         <td>有效</td>
         *         <td>有效</td>
         *         <td>有效</td>
         *     </tr>
         *     <tr>
         *         <td>3</td>
         *         <td>Sofia.otf</td>
         *         <td>数字,字母,符号无效, 中文有效</td>
         *         <td>←</td>
         *         <td>←</td>
         *     </tr>
         * </table>
         */
        //                                   Android 8.0(Oreo), Api 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Typeface.Builder builder = new Typeface.Builder(ConfigUtils.APPLICATION.getAssets(), path);

//            FontVariationAxis wght = new FontVariationAxis("wght", weight);    //字体粗细, 100梯度?
//            FontVariationAxis wdth = new FontVariationAxis("wdth", 100);    //字体宽度
//            FontVariationAxis slnt = new FontVariationAxis("slnt", 20); //Set the font slant to 20 degrees and ask for italic style.
//            FontVariationAxis ital = new FontVariationAxis("ital", isItalic ? 1f : 0f);
//            builder.setFontVariationSettings(new FontVariationAxis[]{wght, slnt, ital});

//            String variationSettings = TextUtils2.getStringFormat("'wght' %d, 'slnt' 20, 'ital' %d", weight, isItalic ? 1 : 0);
//            builder.setFontVariationSettings(variationSettings);

            builder.setWeight(weight);  // Tell the system that this is a bold font.
            builder.setItalic(isItalic);// Tell the system that this is an italic style font.
            builder.setFallback("SANS_SERIF");  //设置备用字体. if传入的字体不可用, 就返回备用的字体

            //TTC（TrueType Collection）, 后缀： *.ttc, 字体资源文件是字体集合时指定其中一个，如果不是可以不调用，调用0也行
//            builder.setTtcIndex(0);

            return builder.build();
        }

        return null;
    }

    /**
     * 从缓存中读取
     * @param id
     * @return
     * @see {@link TypefaceCompat#findFromCache(Resources, int, int)}
     */
    @Nullable
    protected static Typeface findFromCache(int id, @IntRange(from = 1, to = 1000) int weight, boolean isItalic) {
        return sTypefaceCache.get(createResourceUid(id, weight, isItalic));
    }

    /**
     * 从缓存中读取
     * @param id
     * @return
     */
    @Nullable
    protected static Typeface findFromCache(int id, boolean isBold, boolean isItalic) {
        return findFromCache(id, isBold ? WEIGHT_BOLD : WEIGHT_NORMAL, isItalic);
    }

    /**
     * 从缓存中读取
     * @param path
     * @return
     * @see {@link TypefaceCompat#findFromCache(Resources, int, int)}
     */
    @Nullable
    protected static Typeface findFromCache(String path, @IntRange(from = 1, to = 1000) int weight, boolean isItalic) {
        return sTypefaceCache.get(createResourceUid(path, weight, isItalic));
    }

    /**
     * 从缓存中读取
     * @return
     */
    @Nullable
    protected static Typeface findFromCache(String path, boolean isBold, boolean isItalic) {
        return findFromCache(path, isBold ? WEIGHT_BOLD : WEIGHT_NORMAL, isItalic);
    }

    /**
     * Create a unique id for a given Resource and id.
     *
     * @param id a resource id
     * @return Unique id for a given resource and id.
     */
    protected static String createResourceUid(int id, @IntRange(from = 1, to = 1000) int weight, boolean isItalic) {
        return PACKAGE_NAME + "-" + id + "-" + weight+ "-" + isItalic;
    }

    /**
     * Create a unique id for a given Resource and id.
     *
     * @return Unique id for a given resource and id.
     */
    protected static String createResourceUid(String path, @IntRange(from = 1, to = 1000) int weight, boolean isItalic) {
        return PACKAGE_NAME + "-" + path + "-" + weight+ "-" + isItalic;
    }

    /**
     * @see TypefaceCompat#clearCache()
     */
    public static void clearCache() {
//        @SuppressLint({"RestrictedApi", "VisibleForTests"})
//        TypefaceCompat.clearCache();
        sTypefaceCache.evictAll();
    }

    /**
     * 反射获取字体映射
     */
    @Nullable
    public static Map<String, Typeface> getAllFonts() {
        try {
            Field field = Typeface.class.getDeclaredField("sSystemFontMap");
            field.setAccessible(true);
            Map<String, Typeface> fontMap = (Map<String, Typeface>) field.get(null);
            if (fontMap != null) {
//                for (String fontName : fontMap.keySet()) {
//                    LogUtils.errorFormat("Font Name: %s", fontName);
//                }
                return fontMap;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
            LogUtils.error("获取字体列表失败:", e);
        }
        return null;
    }

    /**
     * 设置默认字体, 还有另外1种方式:
     * <pre>
     *  <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
     *      <item name="android:fontFamily">@font/my_custom_font</item>
     *  </style>
     *  <application android:theme="@style/AppTheme">
     * </pre>
     * @see Typeface.SANS_SERIF
     * @param typeface 字体
     */
    public static void setDefaultFont(Typeface typeface) {
        LogUtils.errorFormat("defaultFont: %s", Typeface.DEFAULT);
        if (typeface == null) return;
        try {
            //
            /**
             * Android系统默认字体
             * @see Typeface#DEFAULT_FAMILY
             */
            Field field = Typeface.class.getDeclaredField("sans-serif");
//            Field field = Typeface.class.getDeclaredField("SANS_SERIF");
            field.setAccessible(true);
            field.set(null, typeface);
        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
            LogUtils.error("设置默认字体失败:", e);
        }
    }
}
