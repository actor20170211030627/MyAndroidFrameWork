@ECHO OFF
@echo author ���
@echo ���.bat�ļ����ص�ַ: https://github.com/actor20170211030627/MyAndroidFrameWork/tree/master/captures
color 2a

@echo.
@echo.
@echo *******************************************
@echo *******************************************
@echo 1.��ȡ debug �汾��Կ��Ϣ
SET userName=C:\Users\%username%\.android
CD %userName%
C:
keytool -list -v -storepass android -keystore debug.keystore

@echo 2.��ȡ release �����汾��Կ��Ϣ,��������Ŀ.jks��Կ��ŵ�ַ,ʾ��:F:\Android\YouProject\youProject.jks
set /p jksAddress=

@echo.
@ECHO 3.release ��������Կ,��Ҫ�Լ�������Կ����:
keytool -list -v -keystore %jksAddress%
pause