@ECHO OFF
@echo author 李大发
@echo 这个.bat文件下载地址: https://github.com/actor20170211030627/MyAndroidFrameWork/tree/master/captures
color 2a
SET userName=C:\Users\%username%\.android
CD %userName%
C:
keytool -list -v -storepass android -keystore debug.keystore

@echo 1.获取项目发布版秘钥信息,请输入项目.jks秘钥存放地址,示例:F:\Android\YouProject\youProject.jks
set /p jksAddress=

@echo.
@ECHO 2.发布版秘钥,需要自己输入秘钥口令:
keytool -list -v -keystore %jksAddress%
pause