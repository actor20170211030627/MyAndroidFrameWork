## MyAndroidFrameWork
<ul>
  <li><a href="https://github.com/actor20170211030627/MyAndroidFrameWork">Github</a></li>
  <li><a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork">Gitee码云(国内网速更快)</a></li>
</ul>

## 安卓常用组件&框架, 懒得每次都搭架子...
### 1.集成框架包括如下列表, 具体见 <a href="https://github.com/actor20170211030627/MyAndroidFrameWork/blob/master/myandroidframework/build.gradle">build.gradle</a>:
<ol>
    <li>
        //https://github.com/google/gson converter-gson 已经依赖了Gson2.8.2 <br/>
        //不混淆解析类, 示例: -keep class com.package.xxx.info.** { *; } <br/>
        //api 'com.google.code.gson:gson:2.8.5' <br/> <br/>
    </li>
    <li>
        //https://github.com/square/retrofit <br/>
        api 'com.squareup.retrofit2:retrofit:2.5.0' <br/>
        //https://github.com/square/retrofit/tree/master/retrofit-converters/gson <br/>
        api 'com.squareup.retrofit2:converter-gson:2.5.0' <br/> <br/>
    </li>
    <li>
        //https://github.com/alibaba/fastjson <br/>
        //不混淆解析类, 示例: -keep class com.package.xxx.info.** { *; } <br/>
        api 'com.alibaba:fastjson:1.2.9' <br/> <br/>
    </li>
    <li>
        //https://github.com/bumptech/glide <br/>
        api 'com.github.bumptech.glide:glide:4.9.0' <br/>
        annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0' <br/> <br/>
    </li>
    <li>
        //https://github.com/greenrobot/EventBus <br/>
        api 'org.greenrobot:eventbus:3.2.0' <br/> <br/>
    </li>
    <li>
        //https://github.com/Blankj/AndroidUtilCode 许多工具 <br/>
        api 'com.blankj:utilcode:1.26.0' <br/> <br/>
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
        //https://github.com/yanzhenjie/Album 图片选择 <br/>
        api 'com.yanzhenjie:album:2.1.3' <br/> <br/>
    </li>
    <li>
        //https://github.com/laobie/StatusBarUtil 修改状态栏颜色&透明度等 <br/>
        api 'com.jaeger.statusbarutil:library:1.5.1' <br/> <br/>
    </li>
    <li>
        //https://github.com/yanzhenjie/AndPermission 严振杰权限 <br/>
        api 'com.yanzhenjie.permission:support:2.0.1' <br/> <br/>
    </li>
    <li>
        //https://github.com/Bigkoo/Android-PickerView 时间选择器等等等 <br/>
        api 'com.contrarywind:Android-PickerView:4.1.8' <br/> <br/>
    </li>
    <li>
        //https://github.com/H07000223/FlycoDialog_Master <br/>
        api 'com.flyco.dialog:FlycoDialog_Lib:1.3.2@aar'//各种Dialog & Popup <br/> <br/>
    </li>
    <li>
        //https://github.com/zhou-you/RxEasyHttp minSdkVersion 19 <br/>
        api 'com.zhouyou:rxeasyhttp:2.1.7' <br/> <br/>
    </li>
</ol>

## 2.1. 需要在自己项目中集成<code>v7</code>包和<code>constraint</code>, 否则报错
    implementation 'com.android.support:appcompat-v7:version xxx' <br/>
    implementation 'com.android.support.constraint:constraint-layout:version xxx'//约束布局

## 2.2. ButterKnife没有集成, 如果使用, 需要自己集成
    //https://github.com/JakeWharton/butterknife
    implementation 'com.jakewharton:butterknife:8.8.1'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'

## 3.Screenshot
<img src="captures/BaseTextSwitcher_And_BaseViewSwitcher.gif" width=35%></img>
<img src="captures/BaseBottomSheetDialogFragment.gif" width=35%></img> <br/>
<img src="captures/KeyboardInputEditText.gif" width=35%></img>
<img src="captures/QuickSearchBar.gif" width=35%></img> <br/>
<img src="captures/BaseRatingBar.gif" width=35%></img>

    and other features, you can download apk ↓(更多功能下载apk体验...)

## 4.Sample
<a href="https://github.com/actor20170211030627/MyAndroidFrameWork/raw/master/app/build/outputs/apk/debug/app-debug.apk">download apk</a> or scan qrcode:  <br/>
<img src="captures/qrcode.png" width=35%></img>

## 5.minSdkVersion [![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)
    如果您项目的minSdkVersion小于19, 可能会报错: Manifest merger failed with multiple errors, see logs

## 6.项目中已经添加了混淆文件, 如果需要混淆, 只需在自己项目中打开混淆配置(不过混淆后报错什么的我自己还没试过...):
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

## 7.How to
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


**Step 2.** Add the dependency, the last version:
[![](https://jitpack.io/v/actor20170211030627/MyAndroidFrameWork.svg)](https://jitpack.io/#actor20170211030627/MyAndroidFrameWork)

    android {
      ...
      compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
      }
    }

    dependencies {
            implementation 'com.github.actor20170211030627.MyAndroidFrameWork:myandroidframework:last_version'
    }

## 8.License
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
