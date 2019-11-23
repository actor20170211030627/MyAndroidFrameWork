package com.ly.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;
import com.ly.sample.R;
import com.ly.sample.service.CheckUpdateService;
import com.ly.sample.utils.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 主页
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019-9-6 on 14:22
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    private Intent checkUpdateIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Glide.with(this).load(Global.girl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(iv);

        tvVersion.setText(getStringFormat("VersionName: %s(VersionCode: %d)",
                AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));

        startService(checkUpdateIntent = new Intent(this, CheckUpdateService.class));
    }

    @OnClick({R.id.btn_internet, R.id.btn_bottom_sheet, R.id.btn_viewpager_fragment,
            R.id.btn_is_empty, R.id.btn_switch, R.id.btn_custom_ratingbar, R.id.btn_nine_grid_view,
            R.id.btn_quick_search_bar, R.id.btn_custom_keyboard_view, R.id.btn_other, R.id.tv_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_internet://网络&图片
                startActivity(new Intent(this, NetWorkAndImageActivity.class), iv);
                break;
            case R.id.btn_bottom_sheet://从底部弹出的Dialog & DialogFragment等
                startActivity(new Intent(this, BottomSheetDialogActivity.class), view);
                break;
            case R.id.btn_viewpager_fragment://ViewPager & Fragment多层嵌套
                startActivity(new Intent(this, ViewPagerAndFragmentActivity.class), view);
                break;
            case R.id.btn_is_empty://判空
                startActivity(new Intent(this, IsEmptyActivity.class), view);
                break;
            case R.id.btn_switch://切换
                startActivity(new Intent(this, SwitcherActivity.class), view);
                break;
            case R.id.btn_custom_ratingbar://自定义View
                startActivity(new Intent(this, CustomRatingBarActivity.class), view);
                break;
            case R.id.btn_nine_grid_view://九宫格
                startActivity(new Intent(this, NineGridViewActivity.class));
                break;
            case R.id.btn_quick_search_bar://快速查找条
                startActivity(new Intent(this, QuickSearchBarActivity.class));
                break;
            case R.id.btn_custom_keyboard_view://自定义KeyBoardView
                startActivity(new Intent(this, CustomKeyboardViewActivity.class));
                break;
            case R.id.btn_other://线程, 权限, SPUtils, EventBus
                startActivity(new Intent(this, OtherActivity.class));
                break;
            case R.id.tv_test://Test Only
                startActivity(new Intent(this, TestActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(checkUpdateIntent);
    }
}
