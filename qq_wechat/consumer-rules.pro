
#############################################################################
## tencent QQ登录, 分享
# 不混淆open sdk, 避免有些调用（如js）找不到类或方法
-keep class com.tencent.connect.** {*;}
-keep class com.tencent.open.** {*;}
-keep class com.tencent.tauth.** {*;}
#-keep class * extends android.app.Dialog


#############################################################################
## 微信登录分享等 https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
-keep class com.tencent.mm.opensdk.** {
    *;
}
-keep class com.tencent.wxop.** {
    *;
}
-keep class com.tencent.mm.sdk.** {
    *;
}