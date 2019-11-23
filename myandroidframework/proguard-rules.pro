# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

##---------Begin: proguard configuration for ButterKnife---------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
##----------End: proguard configuration for ButterKnife----------

##-------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Application classes that will be serialized/deserialized over Gson
##不要混淆Bean类(★★★示例:★★★)
#-keep class com.package.xxx.info.** { *; }
##---------------End: proguard configuration for Gson  ----------

##-----------------BaseRecyclerViewAdapterHelper-----------------
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}
##-----------------BaseRecyclerViewAdapterHelper-----------------

##-----Begin: proguard configuration for EasySwipeMenuLayout-----
-keep class com.guanaj.easyswipemenulibrary.State
##------End: proguard configuration for EasySwipeMenuLayout------

##---------------Begin: proguard configuration for okhttputils---
#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}
##---------------End: proguard configuration for okhttputils-----

##----------Begin: proguard configuration for Retrofit-----------
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# okhttp
# 混淆见上方okhttp
##----------End: proguard configuration for Retrofit-------------

##---------------Begin: proguard configuration for fastjson------
-keepattributes Signature
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.*{*;}
##不混淆Fastjson的Bean类(★★★下面这个路径要自己改★★★)
#-keep class com.package.xxx.info.** { *; }
##---------------End: proguard configuration for fastjson--------

##------------Begin: proguard configuration for Glide------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
##-------------End: proguard configuration for Glide-------------

##------------Begin: proguard configuration for Album------------
-dontwarn com.yanzhenjie.album.**
-dontwarn com.yanzhenjie.mediascanner.**
##------------End: proguard configuration for Album--------------

##------------Begin: proguard configuration for banner-----------
-keep class com.youth.banner.** {
    *;
 }
##------------End: proguard configuration for banner-------------

##-------Begin: proguard configuration for AndroidUtilCode-------
-dontwarn com.blankj.utilcode.**
##-------End: proguard configuration for AndroidUtilCode---------

##--------Begin: proguard configuration for ProgressManager------
-keep class me.jessyan.progressmanager.** { *; }
-keep interface me.jessyan.progressmanager.** { *; }
##--------End: proguard configuration for ProgressManager--------

