package com.actor.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.sharedelement.BaseSharedElementCallback;
import com.actor.myandroidframework.utils.sharedelement.SharedElementUtils;
import com.actor.sample.adapter.ShareElementViewPagerAdapter;
import com.actor.sample.databinding.ActivityViewPagerBinding;
import com.actor.sample.utils.Global;
import com.actor.sample.utils.ImageConstants;
import com.blankj.utilcode.util.GsonUtils;

import java.util.List;
import java.util.Map;

/**
 * description: SharedElement 的 ViewPager示例
 * company    :
 * @author    : ldf
 * date       : 2025/5/10 on 14:23
 */
public class ViewPagerActivity extends BaseActivity<ActivityViewPagerBinding> {

    public static final String                    IS_CHANGE_TRANSITION       = "IS_CHANGE_TRANSITION";
    private final       BaseSharedElementCallback enterSharedElementCallback = new BaseSharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            LogUtils.errorFormat("names = %s", GsonUtils.toJson(names));

            View iv = myAdapter.currentFragment.getSharedElementView();
            if (iv == null) return;
            int currentItem = viewBinding.viewPager.getCurrentItem();
            String transitionName = Global.getListTransitionName(currentItem, isChangeTransition);
//            iv.setTransitionName(transitionName); //可不写这句
            names.clear();
            sharedElements.clear();
            names.add(transitionName);
            sharedElements.put(transitionName, iv);
            LogUtils.errorFormat("names = %s", GsonUtils.toJson(names));
        }
    };
    private ShareElementViewPagerAdapter myAdapter;
    private boolean isChangeTransition = true;

    public static Intent getIntent(Context context, int position, boolean isChangeTransition) {
        return new Intent(context, ViewPagerActivity.class)
                .putExtra(Global.START_POSITION, position)
                .putExtra(IS_CHANGE_TRANSITION, isChangeTransition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->元素共享跳转->ViewPager");

        //推迟 Activity 的进入过渡动画
        SharedElementUtils.postponeEnterTransition(this);

        enterSharedElementCallback.setLogPageTag("页面B");

//        setEnterSharedElementCallback(enterSharedElementCallbacks);

        setExitSharedElementCallback(new android.app.SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                LogUtils.errorFormat("names=%s", GsonUtils.toJson(names));
            }
        });

        Intent intent = getIntent();
        int startPosition = intent.getIntExtra(Global.START_POSITION, 0);
        isChangeTransition = intent.getBooleanExtra(IS_CHANGE_TRANSITION, isChangeTransition);
        myAdapter = new ShareElementViewPagerAdapter(getSupportFragmentManager(), ImageConstants.IMAGE_SOURCE.length, startPosition, isChangeTransition);
        viewBinding.viewPager.setAdapter(myAdapter);
        viewBinding.viewPager.setCurrentItem(startPosition);

        LogUtils.error("onCreate()走完");
    }

    @Override
    public void onBackPressed() {
        if (isChangeTransition) {
//        super.onBackPressed();
            int currentItem = viewBinding.viewPager.getCurrentItem();
            Global.fragmentPosition = currentItem;
            Intent intent = new Intent().putExtra(Global.CONTENT, "result ok!")
                    .putExtra(Global.POSITION, currentItem);
            SharedElementUtils.finishAfterTransition(this, enterSharedElementCallback, RESULT_OK, intent);
        } else {
            //也可以直接返回
            int currentItem = viewBinding.viewPager.getCurrentItem();
            String transitionName = Global.getListTransitionName(currentItem, isChangeTransition);

            //对当前iv重新设置transitionName, 否则page改变后可能没元素共享动画!
            View iv = myAdapter.currentFragment.getSharedElementView();
            iv.setTransitionName(transitionName);

            Intent intent = new Intent().putExtra(Global.CONTENT, "result ok!")
                    .putExtra(Global.POSITION, currentItem);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        }
    }
}
