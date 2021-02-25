## MyAndroidFrameWork
<ul>
  <li><a href="https://github.com/actor20170211030627/MyAndroidFrameWork">Github</a></li>
  <li><a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork">Gitee码云(国内网速更快)</a></li>
  <li><font color='red'><b>注意: Androidx</b></font> 版本在 <a href="./README.md">这儿</a>(此版本不再维护, 建议使用Androidx版本.)</li>
</ul>

## 安卓常用组件&框架, 懒得每次都搭架子...
### 1.集成框架包括如下列表, 具体见 <a href="https://github.com/actor20170211030627/MyAndroidFrameWork/blob/e73d4aae083171becb075837f6aae071adc35ca6/myandroidframework/build.gradle">build.gradle</a>:
<pre>
<ol><li>//https://github.com/google/gson converter-gson 已经依赖了Gson2.8.2
//不混淆解析类, 示例: -keep class com.package.xxx.info.** { *; }
//api 'com.google.code.gson:gson:2.8.5'
</li>
<li>//https://github.com/square/retrofit
api 'com.squareup.retrofit2:retrofit:2.5.0'
//https://github.com/square/retrofit/tree/master/retrofit-converters/gson
api 'com.squareup.retrofit2:converter-gson:2.5.0'
</li>
<li>//https://github.com/bumptech/glide
api 'com.github.bumptech.glide:glide:4.9.0'
annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'
</li>
<li>//https://github.com/greenrobot/EventBus
api 'org.greenrobot:eventbus:3.2.0'
</li>
<li>//https://github.com/Blankj/AndroidUtilCode 许多工具
api 'com.blankj:utilcode:1.28.4'
</li>
<li>//https://github.com/JessYanCoding/AndroidAutoSize 极低成本的 Android 屏幕适配方案
api 'me.jessyan:autosize:1.2.1'
</li>
<li>//https://github.com/CymChad/BaseRecyclerViewAdapterHelper
api 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.50'
</li>
<li>//https://github.com/anzaizai/EasySwipeMenuLayout 侧滑删除
api 'com.github.anzaizai:EasySwipeMenuLayout:1.1.4'
</li>
<li>//https://github.com/yanzhenjie/Album 图片选择
api 'com.yanzhenjie:album:2.1.3'
</li>
<li>//https://github.com/laobie/StatusBarUtil 修改状态栏颜色&透明度等
api 'com.jaeger.statusbarutil:library:1.5.1'
</li>
<li>//https://github.com/yanzhenjie/AndPermission 严振杰权限
api 'com.yanzhenjie.permission:support:2.0.1'
</li>
<li>//https://github.com/Bigkoo/Android-PickerView 时间选择器等等等
api 'com.contrarywind:Android-PickerView:4.1.9'
</li>
<li>//https://github.com/H07000223/FlycoDialog_Master 各种Dialog & Popup
api 'com.flyco.dialog:FlycoDialog_Lib:1.3.2@aar'
</li>
<li>//https://github.com/square/okhttp
api "com.squareup.okhttp3:okhttp:4.7.2"
</li>
<li>//https://github.com/hongyangAndroid/okhttputils 张鸿洋的okhttp
api 'com.zhy:okhttputils:2.6.2'
</li></ol></pre>

## 2.Screenshot
<img src="captures/BaseTextSwitcher_And_BaseViewSwitcher.gif" width=35%></img>
<img src="captures/BaseBottomSheetDialogFragment.gif" width=35%></img> <br/>
<img src="captures/QuickSearchBar.gif" width=35%></img>
<img src="captures/BaseRatingBar.gif" width=35%></img>

## 3.Sample
<a href="app/build/outputs/apk/debug/app-debug.apk">download apk</a> or scan qrcode:  <br/>
<img src="captures/qrcode.png" width=35%></img>

## 4.minSdkVersion [![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
    如果您项目的minSdkVersion小于16, 集成后可能会报错: Manifest merger failed with multiple errors, see logs

## 5.How to
To get a Git project into your build:

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
<pre>
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
</pre>

**Step 2.** Add the dependency:

    android {
      ...
      compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
      }
    }

    dependencies {
            implementation 'com.github.actor20170211030627.MyAndroidFrameWork:myandroidframework:1.4.1'
    }

## 6.1. 需要在自己项目中集成<code>v7</code>包和<code>constraint</code>, 否则报错
    implementation 'com.android.support:appcompat-v7:version xxx' <br/>
    implementation 'com.android.support.constraint:constraint-layout:version xxx'//约束布局, 版本version>=1.1.3

## 6.2. ButterKnife没有集成, 如果使用, 需要自己集成
    //https://github.com/JakeWharton/butterknife
    implementation 'com.jakewharton:butterknife:8.8.1'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'

## 7.<code>AndroidManifest.xml</code>合并清单文件报错
    如果你的清单文件中的 allowBackup = false, 那么需要添加一句: tools:replace="android:allowBackup", 示例:
    <application
        android:allowBackup="false"
        tools:replace="android:allowBackup"

## 8.项目中已经添加了混淆文件, 如果需要混淆, 只需在自己项目中打开混淆配置:
<pre>
    android {
        ...
        buildTypes {
            release {
                minifyEnabled true
                ...
            }
        }
    }
</pre>

## 9.使用步骤
<pre>
1.写一个 Application extends ActorApplication, 然后重写方法, 可参考: <a href="https://github.com/actor20170211030627/MyAndroidFrameWork/blob/e73d4aae083171becb075837f6aae071adc35ca6/app/src/main/java/com/actor/sample/MyApplication.java">MyApplication</a>  (非必须继承, ActorApplication里有一些配置, 可把配置代码copy到自己Application)
2.写一个 BaseActivity extends ActorBaseActivity, 然后你的Activity 继承 BaseActivity (非必须继承)
3.写一个 BaseFragment extends ActorBaseFragment, 然后你的Fragment 继承 BaseFragment (非必须继承)
4.已经集成了 <a href="https://github.com/JessYanCoding/AndroidAutoSize">AndroidAutoSize</a>, 如果你需要使用它, 请在 AndroidManifest.xml 中填写全局设计图尺寸 (单位 dp):
  &lt;application>
    &lt;meta-data
        android:name="design_width_in_dp"
        android:value="(例)360"/>
    &lt;meta-data
        android:name="design_height_in_dp"
        android:value="(例)640"/>
  &lt;/application>
5.&lt;style name="AppTheme" parent="AppThemeForMyAndroidFrameWork"> 你的style可继承这个style (非必须继承)
6.已经添加了权限: <kbd>INTERNET</kbd>, <kbd>CAMERA</kbd>, <kbd>READ_EXTERNAL_STORAGE</kbd>, <kbd>WRITE_EXTERNAL_STORAGE</kbd>, <kbd>ACCESS_NETWORK_STATE</kbd>,  <kbd>REQUEST_INSTALL_PACKAGES</kbd>,
  可以不再添加以上权限(添加也不会报错.)
</pre>

## 10.有问题请升级到最新版本: 1.4.1, 或升级到Androidx版本, 或提交 <a href="https://github.com/actor20170211030627/MyAndroidFrameWork/issues">issues</a>, 或发邮箱: <a href="mailto:1455198886@qq.com">email</a>

## 11.License
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
