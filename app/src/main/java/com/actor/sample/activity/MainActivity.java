package com.actor.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actor.sample.R;
import com.actor.sample.databinding.ActivityMainBinding;
import com.actor.sample.utils.CheckUpdateUtils;
import com.actor.sample.utils.Global;
import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;

/**
 * Description: 主页
 * Author     : ldf
 * Date       : 2019-9-6 on 14:22
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tvVersion = viewBinding.tvVersion;

        Glide.with(this).load(Global.girl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(viewBinding.iv);

        AppUtils.AppInfo appInfo = AppUtils.getAppInfo();
        tvVersion.setText(getStringFormat("VersionName: %s(VersionCode: %d)", appInfo.getVersionName(), appInfo.getVersionCode()));

        //检查更新
        new CheckUpdateUtils().check(this);
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_internet://网络&图片
                startActivity(new Intent(this, NetWorkAndImageActivity.class), viewBinding.iv);
                break;
            case R.id.btn_shared_element://元素共享跳转
                startActivity(new Intent(this, SharedElementActivity.class), view);
                break;
            case R.id.btn_bottom_sheet://从底部弹出的Dialog & DialogFragment等
                startActivity(new Intent(this, BottomSheetDialogActivity.class), view);
                break;
            case R.id.btn_glide:        //Glide使用
                startActivity(GlideExampleActivity.class);
                break;
            case R.id.btn_popup_window://PopupWindow测试
                startActivity(PopupWindowTestActivity.class);
                break;
            case R.id.btn_viewpager_about://ViewPager多层嵌套
                startActivity(ViewPagerAndFragmentActivity.class);
                break;
            case R.id.btn_select_file://文件选择
                startActivity(new Intent(this, SelectFileActivity.class), view);
                break;
            case R.id.btn_is_empty://判空
                startActivity(new Intent(this, IsEmptyActivity.class), view);
                break;
            case R.id.btn_chat://聊天
                startActivity(ChatActivity.class);
                break;
            case R.id.btn_third://第三方登录/分享
                startActivity(new Intent(this, ThirdActivity.class), view);
                break;
            case R.id.btn_baidu://百度定位/地图
                startActivity(new Intent(this, BaiDuMapActivity.class), view);
                break;
            case R.id.btn_gaode://高德定位/地图
                startActivity(new Intent(this, GaoDeMapActivity.class), view);
                break;
            case R.id.btn_jpush://极光推送
                startActivity(new Intent(this, JPushActivity.class), view);
                break;
            case R.id.btn_database://数据库(GreenDao)
                startActivity(new Intent(this, DatabaseActivity.class), view);
                break;
            case R.id.btn_recycler_view:  //RecyclerView测试
                startActivity(RecyclerViewTestActivity.class);
                break;
            case R.id.btn_wheel_view:  //WheelView测试
                startActivity(WheelViewTestActivity.class);
                break;
            case R.id.btn_switch://切换
                startActivity(new Intent(this, SwitcherActivity.class), view);
                break;
            case R.id.btn_custom_view://自定义View
                startActivity(new Intent(this, CustomViewActivity.class), view);
                break;
            case R.id.btn_custom_ratingbar://自定义RatingBar
                startActivity(new Intent(this, RatingBarActivity.class), view);
                break;
            case R.id.btn_custom_line_view://线条LineView
                startActivity(new Intent(this, LineViewActivity.class), view);
                break;
            case R.id.btn_scrollable_text_view://可滚动TextView
                startActivity(new Intent(this, ScrollableTextViewActivity.class), view);
                break;
            case R.id.btn_font:         //Font字体设置
                startActivity(FontSetActivity.class);
                break;
            case R.id.btn_part_image_view://ShowPartImageView
                startActivity(ShowPartImageViewActivity.class);
                break;
            case R.id.btn_webview://WebView
                startActivity(WebViewActivity.class);
                break;
            case R.id.btn_count_down_timer://BaseCountDownTimer测试
                startActivity(new Intent(this, BaseCountDownTimerActivity.class), view);
                break;
            case R.id.btn_audio_media://音频录制&播放,视频播放
                startActivity(new Intent(this, AudioMediaActivity.class), view);
                break;
            case R.id.btn_other://线程, 权限, SPUtils, EventBus
                startActivity(OtherActivity.class);
                break;
            default:
                break;
        }
    }
}
