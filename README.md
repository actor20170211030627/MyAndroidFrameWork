## MyAndroidFrameWork
> <a href="https://github.com/actor20170211030627/MyAndroidFrameWork">Github</a> <br/>
> <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork">Gitee码云(国内网速更快)</a> <br/>
> 注意: 这是 <font color='red' size='6'><b>Androidx</b></font> 版,
  非Androidx版本在&nbsp;
  <a style="font-size:23px" href="./README-1.4.1.md"><s>这儿 </s></a>
  &nbsp;(非Androidx版本不再维护) <br/>
>> Androidx版本能很好向下兼容, 包括jar包依赖也能自动转换, 所以建议转换成Androidx.


## 安卓常用组件&框架(懒得每次都搭架子...)
### 1.集成框架包括如下列表, 具体见 <a href="https://github.com/actor20170211030627/MyAndroidFrameWork/blob/master/myandroidframework/build.gradle">build.gradle</a>:
<ol>
    <li>
        //https://github.com/google/gson converter-gson 已经依赖了Gson2.8.5 <br/>
        //不混淆解析类, 示例: -keep class com.package.xxx.info.** { *; } <br/>
        //api 'com.google.code.gson:gson:2.8.5' <br/> <br/>
    </li>
    <li>
        //https://github.com/square/retrofit <br/>
        api 'com.squareup.retrofit2:retrofit:2.9.0' <br/>
        //https://github.com/square/retrofit/tree/master/retrofit-converters/gson <br/>
        api 'com.squareup.retrofit2:converter-gson:2.9.0' <br/> <br/>
    </li>
    <li>
        //https://github.com/bumptech/glide <br/>
        api 'com.github.bumptech.glide:glide:4.11.0' <br/>
        annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0' <br/> <br/>
    </li>
    <li>
        //https://github.com/greenrobot/EventBus <br/>
        api 'org.greenrobot:eventbus:3.2.0' <br/> <br/>
    </li>
    <li>
        //https://github.com/Blankj/AndroidUtilCode 许多工具 <br/>
        api 'com.blankj:utilcodex:1.29.0' <br/> <br/>
    </li>
    <li>
        //https://github.com/JessYanCoding/AndroidAutoSize 极低成本的 Android 屏幕适配方案 <br/>
        api 'me.jessyan:autosize:1.2.1' <br/> <br/>
    </li>
    <li>
        //https://github.com/CymChad/BaseRecyclerViewAdapterHelper <br/>
        api 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.50' <br/> <br/>
    </li>
    <li>
        //https://github.com/anzaizai/EasySwipeMenuLayout 侧滑删除 <br/>
        api 'com.github.anzaizai:EasySwipeMenuLayout:1.1.4' <br/> <br/>
    </li>
    <li>
        //https://github.com/LuckSiege/PictureSelector 选择图片(裁剪,压缩)、视频、音频 <br/>
        api 'com.github.LuckSiege.PictureSelector:picture_library:v2.6.0' <br/> <br/>
    </li>
    <li>
        //https://github.com/laobie/StatusBarUtil 修改状态栏颜色&透明度等 <br/>
        api 'com.jaeger.statusbarutil:library:1.5.1' <br/> <br/>
    </li>
    <li>
        //https://github.com/yanzhenjie/AndPermission 严振杰权限 <br/>
        api 'com.yanzhenjie:permission:2.0.3' <br/> <br/>
    </li>
    <li>
        //https://github.com/Bigkoo/Android-PickerView 时间选择器等等等 <br/>
        api 'com.contrarywind:Android-PickerView:4.1.9' <br/> <br/>
    </li>
    <li>
        //https://github.com/li-xiaojun/XPopup 各种Dialog & Popup <br/>
        api 'com.lxj:xpopup:2.2.13' <br/> <br/>
    </li>
    <li>
        //https://github.com/square/okhttp <br/>
        api "com.squareup.okhttp3:okhttp:4.9.0" <br/> <br/>
    </li>
    <li>
        //https://github.com/hongyangAndroid/okhttputils 张鸿洋的okhttp <br/>
        api 'com.zhy:okhttputils:2.6.2' <br/> <br/>
    </li>
</ol>

## 2.Screenshot
<img src="captures/BaseTextSwitcher_And_BaseViewSwitcher.gif" width=35%></img>
<img src="captures/BaseBottomSheetDialogFragment.gif" width=35%></img> <br/>
<img src="captures/QuickSearchBar.gif" width=35%></img>
<img src="captures/BaseRatingBar.gif" width=35%></img>

## 3.Sample
<a href="https://github.com/actor20170211030627/MyAndroidFrameWork/raw/master/app/build/outputs/apk/debug/app-debug.apk">download apk</a> or scan qrcode:  <br/>
<img src="captures/qrcode.png" width=35%></img>

## 4.minSdkVersion [![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)
    如果您项目的minSdkVersion小于19, 集成后可能会报错: Manifest merger failed with multiple errors, see logs

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

**Step 2.** Add the dependency, the last version(最新版本):
[![](https://jitpack.io/v/actor20170211030627/MyAndroidFrameWork.svg)](https://jitpack.io/#actor20170211030627/MyAndroidFrameWork)

    android {
      ...
      compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
      }
    }

    dependencies {
            implementation 'com.github.actor20170211030627.MyAndroidFrameWork:myandroidframework:last version'
    }

## 6.1. 需要在自己项目中集成<code>constraint</code>包, 否则报错
    implementation 'androidx.constraintlayout:constraintlayout:version xxx'//约束布局, 版本version>=1.1.3

## 6.2. ButterKnife没有集成, 如果使用, 需要自己集成
    //https://github.com/JakeWharton/butterknife
    implementation 'com.jakewharton:butterknife:10.2.3'
    annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'

## 7.<code>AndroidManifest.xml</code>合并清单文件报错
    1.如果你的清单文件中的 allowBackup = false, 那么需要添加一句: tools:replace="android:allowBackup", 示例:
    <application
        android:allowBackup="false"
        tools:replace="android:allowBackup"

    2.如果报错: AndroidManifest.xml:15:5-134:19: AAPT: error: attribute android:requestLegacyExternalStorage not found.
      需要将 compileSdkVersion 升级到 29

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
1.写一个 Application extends ActorApplication, 然后重写方法, 可参考: <a href="https://github.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/MyApplication.java">MyApplication</a>  (非必须继承, ActorApplication里有一些配置, 可把配置代码copy到自己Application)
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
</pre>

## 10.已移除<kbd>REQUEST_INSTALL_PACKAGES</kbd>权限,如果使用的是2.0.0版本并且需要安装app, 请自行添加这个权限.<br/>&emsp;有问题请升级到最新版本: [![](https://jitpack.io/v/actor20170211030627/MyAndroidFrameWork.svg)](https://jitpack.io/#actor20170211030627/MyAndroidFrameWork), 或提交 <a href="https://github.com/actor20170211030627/MyAndroidFrameWork/issues">issues</a>, 或发邮箱: <a href="mailto:1455198886@qq.com">email</a>

## 11.License
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
