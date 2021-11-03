package com.actor.sample.activity;

import androidx.annotation.LayoutRes;
import androidx.viewbinding.ViewBinding;

import com.actor.myandroidframework.activity.ViewBindingActivity;
import com.actor.sample.MyApplication;
import com.blankj.utilcode.util.CacheDiskUtils;

/**
 * Description: 基类
 * Date       : 2019/8/24 on 11:25
 *
 * @version 1.0
 */
public class BaseActivity<VB extends ViewBinding> extends ViewBindingActivity<VB> {

//    protected FrameLayout  flContent;//主要内容的帧布局
//    protected LinearLayout llEmpty;  //没数据

    //硬盘缓存
    protected CacheDiskUtils aCache = MyApplication.instance.aCache;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
//        View baseView = getLayoutInflater().inflate(R.layout.activity_base, null);//加载基类布局
//        flContent = baseView.findViewById(R.id.fl_content);
//        llEmpty = findViewById(R.id.ll_empty);
//        View childView = getLayoutInflater().inflate(layoutResID, null);//加载子类布局
//        flContent.addView(childView);//将子布局添加到空的帧布局
//        super.setContentView(baseView);
        super.setContentView(layoutResID);
    }
    //是否显示empty图片
//    protected void showEmpty(boolean isShow) {
//        llEmpty.setVisibility(isShow ? View.VISIBLE : View.GONE);
//    }



    @Override
    public void onSharedElementBacked(int oldPosition, int currentPosition) {
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
