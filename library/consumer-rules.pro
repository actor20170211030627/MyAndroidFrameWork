
# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
#-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
#-dontusemixedcaseclassnames

# 不跳过非公共库的类
#-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共库的类成员
#-dontskipnonpubliclibraryclassmembers

# 混淆后产生映射文件, 包含有类名->混淆后类名的映射关系
#-verbose

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
#-dontpreverify

# 保留Annotation 和 内部类 不混淆
#-keepattributes *Annotation*, InnerClasses

# 避免混淆泛型
#-keepattributes Signature

# 抛出异常时保留代码行号
#-keepattributes SourceFile, LineNumberTable


# 抑制警告
#-ignorewarnings

# @Keep 保留@Keep注解的类以及它的属性方法不被混淆
# 注意: 如果某个json解析成的Entity里有静态class, 也需要在这个静态class上加上@Keep, 否则静态class会被混淆!!
-keep @androidx.annotation.Keep class **

# ViewBinding: 防止反射方法被混淆
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
  public static * inflate(android.view.LayoutInflater);
  public static * inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
  public static * bind(android.view.View);
}


#############################################################################
## Gson
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { <fields>; }
##不要混淆Bean类(★★★示例:★★★)
#-keep class com.package.xxx.info.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#############################################################################
## BaseRecyclerViewAdapterHelper
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}


#############################################################################
## OkHttp3 框架混淆规则
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# okio
-dontwarn okio.**
-keep class okio.**{*;}


#############################################################################
## https://github.com/getActivity/XXPermissions
-keep class com.hjq.permissions.** {*;}


#############################################################################
## Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only(fixed: DexGuard 基于 ProGuard, 报错, 先注释掉)
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule


#############################################################################
## AndroidUtilCode
-dontwarn com.blankj.utilcode.**


#############################################################################
## https://github.com/getActivity/Toaster
-keep class com.hjq.toast.** {*;}


#############################################################################
# EasyHttp 框架混淆规则
-keep class com.hjq.http.** {*;}
# 不混淆实现 OnHttpListener 接口的类
# 必须要加上此规则，否则会导致泛型解析失败
-keep public class * implements com.hjq.http.listener.OnHttpListener {
    *;
}
-keep class * extends com.hjq.http.model.ResponseClass {
    *;
}


#############################################################################
#XPopup 2.6.7
-dontwarn com.lxj.xpopup.widget.**
-keep class com.lxj.xpopup.widget.**{*;}
