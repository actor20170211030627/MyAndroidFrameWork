## MyAndroidFrameWork
> <a href="https://github.com/actor20170211030627/MyAndroidFrameWork"><s>Github(网速慢,不再维护 Deprecated)</s></a> <br/>
> <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork">Gitee码云(国内网速更快)</a> <br/>
> 注意: 这是 <font color='red' size='6'><b>Androidx</b></font> 版,
  非Androidx版本在&nbsp;
  <a style="font-size:23px" href="./README-1.4.1.md"><s>这儿 </s></a>
  &nbsp;(非Androidx版本不再维护) <br/>
>> Androidx版本能很好向下兼容, 包括jar包依赖也能自动转换, 所以建议转换成Androidx.


## 安卓常用组件&框架(懒得每次都搭架子...)
### 1.集成框架包括如下列表, 具体见 <a href="myandroidframework/build.gradle">build.gradle</a>:
<pre>
<ol><li>//https://github.com/google/gson converter-gson 已经依赖了Gson2.8.5
//不混淆解析类, 示例: -keep class com.package.xxx.info.** { *; }
//api 'com.google.code.gson:gson:2.8.5'
</li>
<li>//https://github.com/square/retrofit
api 'com.squareup.retrofit2:retrofit:2.9.0'
//https://github.com/square/retrofit/tree/master/retrofit-converters/gson
api 'com.squareup.retrofit2:converter-gson:2.9.0'
</li>
<li>//https://github.com/bumptech/glide
api 'com.github.bumptech.glide:glide:4.12.0'
annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
</li>
<li>//https://github.com/greenrobot/EventBus
api 'org.greenrobot:eventbus:3.2.0'
</li>
<li>//https://github.com/Blankj/AndroidUtilCode 许多工具
api 'com.blankj:utilcodex:1.29.0'
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
<li>//https://github.com/LuckSiege/PictureSelector 选择图片(裁剪,压缩)、视频、音频
api 'com.github.LuckSiege.PictureSelector:picture_library:v2.6.0'
</li>
<li>//https://github.com/laobie/StatusBarUtil 修改状态栏颜色&透明度等
api 'com.jaeger.statusbarutil:library:1.5.1'
</li>
<li>//https://github.com/yanzhenjie/AndPermission 严振杰权限
api 'com.yanzhenjie:permission:2.0.3'
</li>
<li>//https://github.com/Bigkoo/Android-PickerView 时间选择器等等等
api 'com.contrarywind:Android-PickerView:4.1.9'
</li>
<li>//https://github.com/li-xiaojun/XPopup 各种Dialog & Popup
api 'com.lxj:xpopup:2.2.13'
</li>
<li>//https://github.com/square/okhttp
api "com.squareup.okhttp3:okhttp:4.9.0"
</li>
<li>//https://github.com/hongyangAndroid/okhttputils 张鸿洋的okhttp
api 'com.zhy:okhttputils:2.6.2'
</li></ol></pre>

## 2.一些控件和工具类等
**2.1.RecyclerView的Adapter**
<pre>
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/adapter_recyclerview/AddAudioAdapter.java">AddAudioAdapter(选择添加音频)</a>
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/adapter_recyclerview/AddPicAdapter.java">AddPicAdapter(选择添加图片)</a>
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/adapter_recyclerview/AddVideoAdapter.java">AddVideoAdapter(选择添加视频)</a>
</pre>

**2.2.ViewPager的Adapter**
<pre>
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/adapter_viewpager/BaseFragmentPagerAdapter.java">BaseFragmentPagerAdapter</a>
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/adapter_viewpager/BaseFragmentStatePagerAdapter.java">BaseFragmentStatePagerAdapter</a>
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/adapter_viewpager/BasePagerAdapter.java">BasePagerAdapter</a>
</pre>

**2.3.Dialog(继承对应Dialog并自定义界面, 不用再关注style等. )**
<pre>
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/dialog/BaseAlertDialogV7.java">BaseAlertDialogV7</a> (V7包AlertDialog简单封装)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/dialog/BaseBottomDialog.java">BaseBottomDialog</a> (从底部弹出并停留底部)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/dialog/BaseBottomSheetDialog.java">BaseBottomSheetDialog</a> (从底部弹出并停留底部, 可二次滑动)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/dialog/BaseDialog.java">BaseDialog</a> (Dialog简单封装)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/dialog/BaseLeftDialog.java">BaseLeftDialog</a> (从左侧弹出的Dialog)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/dialog/BaseRightDialog.java">BaseRightDialog</a> (从右侧弹出的Dialog)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/dialog/BaseTopDialog.java">BaseTopDialog</a> (从顶部弹出的Dialog)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/dialog/LoadingDialog.java">LoadingDialog</a> (加载Dialog, 耗时操作时可显示这个Dialog)
</pre>

**2.4.Utils工具类**
<pre>
<s><a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/album/AlbumUtils.java">AlbumUtils</a> (选图片/选视频/拍照/录视频/预览图片)</s>
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/audio/AudioUtils.java">AudioUtils</a> (录音/播放录音)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/baidu/BaiduLocationUtils.java">BaiduLocationUtils</a> (百度地图定位)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/baidu/BaiduMapUtils.java">BaiduMapUtils</a> (百度地图)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/database/GreenDaoUtils.java">GreenDaoUtils</a> (GreenDao数据库)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/gaode/GaoDeLocationUtils.java">GaoDeLocationUtils</a> (高德地图定位)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/gaode/GaoDeMapUtils.java">GaoDeMapUtils</a> (高德地图)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/gaode/GaoDeUiSettingUtils.java">GaoDeUiSettingUtils</a> (高德地图UI界面)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/gson/IntJsonDeserializer.java">IntJsonDeserializer</a> (解决Gson""转换成int报错)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/jpush/JPushUtils.java">JPushUtils</a> (极光推送)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/mpchart/BarChartUtils.java">BarChartUtils</a> (柱状图)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/mpchart/LineChartUtils.java">LineChartUtils</a> (折线图)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/okhttputils/MyOkHttpUtils.java">MyOkHttpUtils</a> (Okhttp网络请求)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/picture_selector/PictureSelectorUtils.java">PictureSelectorUtils</a> (图片/视频/音频选择和预览. 拍照/拍视频/录音频)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/retrofit/RetrofitNetwork.java">RetrofitNetwork</a> (retrofit简单封装)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/tencent/QQUtils.java">QQUtils</a> (QQ登录, 获取用户信息, 分享图文/图片/音乐/App/文件, 唤起小程序/小游戏...)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/tencent/WeChatUtils.java">WeChatUtils</a> (微信登录, 获取Token, 分享文字/图片/音乐/视频/网页/小程序/文件, 支付, 订阅消息...)
<s><a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/umeng/UMengShareUtils.java">UMengShareUtils</a> (友盟分享)</s>
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/video/VideoProcessorUtils.java">VideoProcessorUtils</a> (视频压缩)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/ClickUtils2.java">ClickUtils2</a> (防止2次点击)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/LogUtils.java">LogUtils</a> (Log简单打印)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/NotificationHelper.java">NotificationHelper</a> (通知栏)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/PermissionRequestUtils.java">PermissionRequestUtils</a> (权限)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/SPUtils.java">SPUtils</a> (SP工具)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/TextUtils2.java">TextUtils2</a> (获取Text, 判空, getStringFormat, ...)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/utils/ThreadUtils.java">ThreadUtils</a> (线程判断/切换)
</pre>

**2.5.Widget小控件**
<pre>
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/NineGridView/NineGridView.java">NineGridView</a> (九宫格)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/webview/BaseWebView.java">BaseWebView</a> (WebView简单封装)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/BaseItemDecoration.java">BaseItemDecoration</a> (RecyclerView的Item间隔)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/BaseRatingBar.java">BaseRatingBar</a> (RatingBar星星选择)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/BaseSlidingDrawer.java">BaseSlidingDrawer</a> (抽屉, 从底部或左侧拉出/收回)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/BaseSpinner.java">BaseSpinner</a> (Spinner增加一些属性)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/BaseTabLayout.java">BaseTabLayout</a> (更简单使用TabLayout)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/BaseTextSwitcher.java">BaseTextSwitcher</a> (TextView切换)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/BaseViewSwitcher.java">BaseViewSwitcher</a> (View切换)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/ItemRadioGroupLayout.java">ItemRadioGroupLayout</a> (RadioGroup选择)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/ItemSpinnerLayout.java">ItemSpinnerLayout</a> (Spinner选择)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/ItemTextInputLayout.java">ItemTextInputLayout</a> (Text输入)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/QuickSearchBar.java">QuickSearchBar</a> (快速查找条a-z)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/RatioLayout.java">RatioLayout</a> (百分比布局, 宽高百分比)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/RoundCardView.java">RoundCardView</a> (圆角CardView)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/ScrollableViewPager.java">ScrollableViewPager</a> (ViewPager是否能左右滑动)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/StatusBarHeightView.java">StatusBarHeightView</a> (状态栏占高)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/SwipeRefreshLayoutCompatViewPager.java">SwipeRefreshLayoutCompatViewPager</a> (SwipeRefreshLayout适配ViewPager里的下拉)
<a href="myandroidframework/src/main/java/com/actor/myandroidframework/widget/VoiceRecorderView.java">VoiceRecorderView</a> (低仿微信录音)
</pre>

## 3.Screenshot
<img src="captures/BaseTextSwitcher_And_BaseViewSwitcher.gif" width=35%></img>
<img src="captures/BaseBottomSheetDialogFragment.gif" width=35%></img> <br/>
<img src="captures/QuickSearchBar.gif" width=35%></img>
<img src="captures/BaseRatingBar.gif" width=35%></img>

## 4.Sample
<a href="app/build/outputs/apk/debug/app-debug.apk">download apk</a>

## 5.minSdkVersion [![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)
    如果您项目的minSdkVersion小于19, 集成后可能会报错: Manifest merger failed with multiple errors, see logs

## 6.How to
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
<s>Github:</s>[![](https://jitpack.io/v/actor20170211030627/MyAndroidFrameWork.svg)](https://jitpack.io/#actor20170211030627/MyAndroidFrameWork) &nbsp; Gitee: [![](https://jitpack.io/v/com.gitee.actor20170211030627/MyAndroidFrameWork.svg)](https://jitpack.io/#com.gitee.actor20170211030627/MyAndroidFrameWork)
<pre>
    android {
      ...
      compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
      }
    }

    dependencies {
            <s>implementation 'com.github.actor20170211030627.MyAndroidFrameWork:myandroidframework:github's last version'(不再维护 Deprecated)</s>
            implementation 'com.gitee.actor20170211030627.MyAndroidFrameWork:myandroidframework:gitee's last version'
    }
</pre>
## 7.1. 需要在自己项目中集成<code>constraint</code>包, 否则报错
    implementation 'androidx.constraintlayout:constraintlayout:version xxx'//约束布局, 版本version>=1.1.3

## 7.2. ButterKnife没有集成, 如果使用, 需要自己集成
    //https://github.com/JakeWharton/butterknife
    implementation 'com.jakewharton:butterknife:10.2.3'
    annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'

## 8.<code>AndroidManifest.xml</code>合并清单文件报错
    1.如果你的清单文件中的 allowBackup = false, 那么需要添加一句: tools:replace="android:allowBackup", 示例:
    <application
        android:allowBackup="false"
        tools:replace="android:allowBackup"

    2.如果报错: AndroidManifest.xml:15:5-134:19: AAPT: error: attribute android:requestLegacyExternalStorage not found.
      需要将 compileSdkVersion 升级到 29

## 9.项目中已经添加了混淆文件, 如果需要混淆, 只需在自己项目中打开混淆配置:
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

## 10.使用步骤
<pre>
1.写一个 Application extends ActorApplication, 然后重写方法, 可参考: <a href="app/src/main/java/com/actor/sample/MyApplication.java">MyApplication</a>  (非必须继承, ActorApplication里有一些配置, 可把配置代码copy到自己Application)
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

## 11.已移除 `REQUEST_INSTALL_PACKAGES`&nbsp;权限,如果使用的是2.0.0版本并且需要安装app, 请自行添加这个权限.<br/>&emsp;有问题请升级到最新版本: <s>Github:</s>[![](https://jitpack.io/v/actor20170211030627/MyAndroidFrameWork.svg)](https://jitpack.io/#actor20170211030627/MyAndroidFrameWork) Gitee: [![](https://jitpack.io/v/com.gitee.actor20170211030627/MyAndroidFrameWork.svg)](https://jitpack.io/#com.gitee.actor20170211030627/MyAndroidFrameWork), 或提交 <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/issues">issues</a>, 或发邮箱: <a href="mailto:1455198886@qq.com">email</a>

## 12.License
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
