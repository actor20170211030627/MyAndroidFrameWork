
#############################################################################
#PictureSelector 3.0
-keep class com.luck.picture.lib.** { *; }

# 如果引入了Camerax库请添加混淆
-keep class com.luck.lib.camerax.** { *; }

# 如果引入了Ucrop库请添加混淆
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }