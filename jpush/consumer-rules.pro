
#############################################################################
## EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

# https://docs.jiguang.cn/jpush/client/Android/android_guide#%E9%9B%86%E6%88%90-jpush-android-sdk-%E7%9A%84%E6%B7%B7%E6%B7%86
# 集成 JPush Android SDK 的混淆
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.service.JPushMessageService { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }
