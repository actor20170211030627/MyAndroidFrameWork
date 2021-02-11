package com.actor.sample.activity;

import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.actor.sample.MyApplication;
import com.blankj.utilcode.util.CacheDiskUtils;

/**
 * Description: 基类
 * Date       : 2019/8/24 on 11:25
 *
 * @version 1.0
 */
public class BaseActivity extends ActorBaseActivity {

    //硬盘缓存
    protected CacheDiskUtils aCache = MyApplication.instance.aCache;

    protected void onSharedElementBacked(int oldPosition, int currentPosition) {
//        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
//                recyclerView.requestLayout();
//                startPostponedEnterTransition();//开始延时的共享动画
//                return true;
//            }
//        });
    }

    //可自定义一些你想要的其它方法...
}
