package com.actor.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actor.sample.R;
import com.actor.sample.utils.CheckUpdateUtils;
import com.actor.sample.utils.Global;
import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 主页
 * Author     : ldf
 * Date       : 2019-9-6 on 14:22
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Glide.with(this).load(Global.girl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(iv);

        AppUtils.AppInfo appInfo = AppUtils.getAppInfo();
        tvVersion.setText(getStringFormat("VersionName: %s(VersionCode: %d)", appInfo.getVersionName(), appInfo.getVersionCode()));

        //检查更新
        new CheckUpdateUtils().check(this);
    }

    @OnClick({R.id.btn_internet, R.id.btn_shared_element, R.id.btn_bottom_sheet,
            R.id.btn_viewpager_fragment, R.id.btn_select_file, R.id.btn_is_empty, R.id.btn_third,
            R.id.btn_baidu, R.id.btn_gaode, R.id.btn_jpush, R.id.btn_database, R.id.btn_switch, R.id.btn_custom_view,
            R.id.btn_custom_ratingbar, R.id.btn_nine_grid_view, R.id.btn_quick_search_bar, R.id.btn_webview,
            R.id.btn_other})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_internet://网络&图片
                startActivity(new Intent(this, NetWorkAndImageActivity.class), false, iv);
                break;
            case R.id.btn_shared_element://元素共享跳转
                startActivity(new Intent(this, SharedElementActivity.class), false, view);
                break;
            case R.id.btn_bottom_sheet://从底部弹出的Dialog & DialogFragment等
                startActivity(new Intent(this, BottomSheetDialogActivity.class), false, view);
                break;
            case R.id.btn_viewpager_fragment://ViewPager & Fragment多层嵌套
                startActivity(new Intent(this, ViewPagerAndFragmentActivity.class), false, view);
                break;
            case R.id.btn_select_file://文件选择
                startActivity(new Intent(this, SelectFileActivity.class), false, view);
                break;
            case R.id.btn_is_empty://判空
                startActivity(new Intent(this, IsEmptyActivity.class), false, view);
                break;
            case R.id.btn_third://第三方登录/分享
                startActivity(new Intent(this, ThirdActivity.class), false, view);
                break;
            case R.id.btn_baidu://百度定位/地图
                startActivity(new Intent(this, BaiDuMapActivity.class), false, view);
                break;
            case R.id.btn_gaode://高德定位/地图
                toast("暂未实现");
                break;
            case R.id.btn_jpush://极光推送
                startActivity(new Intent(this, JPushActivity.class), false, view);
                break;
                case R.id.btn_database://数据库(GreenDao)
                startActivity(new Intent(this, DatabaseActivity.class), false, view);
                break;
            case R.id.btn_switch://切换
                startActivity(new Intent(this, SwitcherActivity.class), false, view);
                break;
            case R.id.btn_custom_view://自定义View
                startActivity(new Intent(this, CustomViewActivity.class), false, view);
                break;
            case R.id.btn_custom_ratingbar://自定义RatingBar
                startActivity(new Intent(this, RatingBarActivity.class), false, view);
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
            case R.id.btn_other://线程, 权限, SPUtils, EventBus
                startActivity(new Intent(this, OtherActivity.class));
                break;
            default:
                break;
        }
    }
}
