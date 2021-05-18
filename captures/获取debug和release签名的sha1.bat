@ECHO OFF
@echo author ldf
@echo 这个.bat文件下载地址: https://gitee.com/actor20170211030627/MyAndroidFrameWork/tree/master/captures

color 2a

@echo.
@echo.
@echo *******************************************
@echo *******************************************
@echo 1.获取 debug 版本秘钥信息
SET userName=C:\Users\%username%\.android
CD %userName%
C:
keytool -list -v -storepass android -keystore debug.keystore


@echo 2.获取 release 发布版本秘钥信息,请输入项目.jks秘钥存放地址,示例:F:\Android\YouProject\youProject.jks

set /p jksAddress=

@echo.
@ECHO 3.release 发布版秘钥,需要自己输入秘钥口令:
keytool -list -v -keystore %jksAddress%
pause