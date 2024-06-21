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
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.CenterListPopupView;

/**
 * Description: 主页
 * Author     : ldf
 * Date       : 2019-9-6 on 14:22
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private CenterListPopupView viewPagerDialog;

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
        viewPagerDialog = new XPopup.Builder(this).asCenterList("选择查看哪种?",
                new String[]{"1.ViewPager多层嵌套", "2.ViewPager高度自适应"}, (position, text) -> {
            switch (position) {
                case 0:
                    //ViewPager多层嵌套
                    startActivity(new Intent(mActivity, ViewPagerAndFragmentActivity.class));
                    break;
                case 1:
                    //ViewPager高度自适应
                    startActivity(new Intent(mActivity, ViewPagerHeightAutoCaculateActivity.class));
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_internet://网络&图片
                startActivity(new Intent(this, NetWorkAndImageActivity.class), false, null, null, viewBinding.iv);
                break;
            case R.id.btn_shared_element://元素共享跳转
                startActivity(new Intent(this, SharedElementActivity.class), false, null, null, view);
                break;
            case R.id.btn_bottom_sheet://从底部弹出的Dialog & DialogFragment等
                startActivity(new Intent(this, BottomSheetDialogActivity.class), false, null, null, view);
                break;
            case R.id.btn_popup_window://PopupWindow测试
                startActivity(PopupWindowTestActivity.class);
                break;
            case R.id.btn_viewpager_about:
                //ViewPager多层嵌套 & 高度自适应
                viewPagerDialog.show();
                break;
            case R.id.btn_select_file://文件选择
                startActivity(new Intent(this, SelectFileActivity.class), false, null, null, view);
                break;
            case R.id.btn_is_empty://判空
                startActivity(new Intent(this, IsEmptyActivity.class), false, null, null, view);
                break;
            case R.id.btn_chat://聊天
                startActivity(new Intent(mActivity, ChatActivity.class));
                break;
            case R.id.btn_third://第三方登录/分享
                startActivity(new Intent(this, ThirdActivity.class), false, null, null, view);
                break;
            case R.id.btn_baidu://百度定位/地图
                startActivity(new Intent(this, BaiDuMapActivity.class), false, null, null, view);
                break;
            case R.id.btn_gaode://高德定位/地图
                startActivity(new Intent(this, GaoDeMapActivity.class), false, null, null, view);
                break;
            case R.id.btn_jpush://极光推送
                startActivity(new Intent(this, JPushActivity.class), false, null, null, view);
                break;
            case R.id.btn_database://数据库(GreenDao)
                startActivity(new Intent(this, DatabaseActivity.class), false, null, null, view);
                break;
            case R.id.btn_switch://切换
                startActivity(new Intent(this, SwitcherActivity.class), false, null, null, view);
                break;
            case R.id.btn_custom_view://自定义View
                startActivity(new Intent(this, CustomViewActivity.class), false, null, null, view);
                break;
            case R.id.btn_custom_ratingbar://自定义RatingBar
                startActivity(new Intent(this, RatingBarActivity.class), false, null, null, view);
                break;
            case R.id.btn_custom_line_view://线条LineView
                startActivity(new Intent(this, LineViewActivity.class), false, null, null, view);
                break;
            case R.id.btn_view_scale://View缩放
                startActivity(new Intent(this, ScaleViewActivity.class), false, null, null, view);
                break;
            case R.id.btn_nine_grid_view://九宫格
                startActivity(new Intent(this, NineGridViewActivity.class));
                break;
            case R.id.btn_quick_search_bar://快速查找条
                startActivity(new Intent(this, QuickSearchBarActivity.class));
                break;
            case R.id.btn_webview://WebView
                startActivity(new Intent(this, WebViewActivity.class));
                break;
            case R.id.btn_count_down_timer://BaseCountDownTimer测试
                startActivity(new Intent(this, BaseCountDownTimerActivity.class), false, null, null, view);
                break;
            case R.id.btn_audio_media://音频录制&播放,视频播放
                startActivity(new Intent(this, AudioMediaActivity.class), false, null, null, view);
                break;
            case R.id.btn_other://线程, 权限, SPUtils, EventBus
                startActivity(new Intent(this, OtherActivity.class));
                break;
            default:
                break;
        }
    }
}
