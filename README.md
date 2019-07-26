# MyAndroidFrameWork
## 安卓常用组件&框架, 不用每次都搭建框架

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

集成框架包括:
<ol>
    <li>
        compileOnly 'com.android.support:appcompat-v7:28.0.0' <br/>
        compileOnly 'com.android.support:design:28.0.0' <br/>
        implementation 'com.android.support.constraint:constraint-layout:1.1.3'//约束布局 <br/> <br/>
    </li>
    <li>
        //https://github.com/google/gson <br/>
        //不混淆Bean类. 示例: <br/>
        //-keep class com.package.xxx.info.** { *; } <br/>
        //api 'com.google.code.gson:gson:2.8.5'//AndroidUtilCode已经依赖了Gson <br/> <br/>
    </li>
    <li>
        //https://github.com/CymChad/BaseRecyclerViewAdapterHelper <br/>
        api 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.46' <br/> <br/>
    </li>
    <li>
        //https://github.com/hongyangAndroid/okhttputils <br/>
        api 'com.zhy:okhttputils:2.6.2' <br/>
        api 'com.squareup.okhttp3:logging-interceptor:3.11.0'//okhttp官方Log拦截器,版本要和okhttp3一致 <br/> <br/>
    </li>
    <li>
        //https://github.com/square/retrofit <br/>
        api 'com.squareup.retrofit2:retrofit:2.5.0' <br/>
        //https://github.com/square/retrofit/tree/master/retrofit-converters/gson <br/>
        api 'com.squareup.retrofit2:converter-gson:2.5.0' <br/> <br/>
    </li>
    <li>
        //https://github.com/bumptech/glide <br/>
        api 'com.github.bumptech.glide:glide:4.9.0' <br/>
        //annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'//未集成,可手动集成 <br/> <br/>
    </li>
    <li>
        //https://github.com/yanzhenjie/Album <br/>
        api 'com.yanzhenjie:album:2.1.3'//图片选择 <br/> <br/>
    </li>
    <li>
        //https://github.com/Blankj/AndroidUtilCode <br/>
        api 'com.blankj:utilcode:1.24.7'//许多工具 <br/> <br/>
    </li>
    <li>
        //https://github.com/laobie/StatusBarUtil <br/>
        api 'com.jaeger.statusbarutil:library:1.5.1'//修改状态栏颜色&透明度等 <br/> <br/>
    </li>
    <li>
        //https://github.com/H07000223/FlycoDialog_Master <br/>
        api 'com.flyco.dialog:FlycoDialog_Lib:1.3.2@aar'//各种Dialog & Popup <br/> <br/>
    </li>
    <li>
        //https://github.com/yanzhenjie/AndPermission <br/>
        api 'com.yanzhenjie.permission:support:2.0.1'//严振杰权限 <br/> <br/>
    </li>
    <li>
        //https://github.com/RuffianZhong/RWidgetHelper <br/>
        api 'com.ruffian.library:RWidgetHelper:1.1.0'//圆角,边框,Gradient背景渐变,控件State各个状态UI样式 <br/> <br/>
    </li>
</ol>


## How to
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

    dependencies {
            implementation 'com.github.actor20170211030627:MyAndroidFrameWork:last_version'
    }

## License
 Apache 2.0.
