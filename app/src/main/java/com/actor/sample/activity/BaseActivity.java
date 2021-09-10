package com.actor.sample.activity;

import androidx.viewbinding.ViewBinding;

import com.actor.myandroidframework.activity.ViewBindingActivity;
import com.actor.sample.MyApplication;
import com.blankj.utilcode.util.CacheDiskUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Description: 基类
 * Date       : 2019/8/24 on 11:25
 *
 * @version 1.0
 */
public class BaseActivity<VB extends ViewBinding> extends ViewBindingActivity<VB> {

    @Deprecated //Retrofit感觉一点都不好用,太死板
    protected List<Call<?>> calls;

    //硬盘缓存
    protected CacheDiskUtils aCache = MyApplication.instance.aCache;


    ///////////////////////////////////////////////////////////////////////////
    // Retrofit区
    ///////////////////////////////////////////////////////////////////////////
    @Deprecated //Retrofit感觉一点都不好用,太死板
    protected <T> Call<T> putCall(Call<T> call) {//放入List, onDestroy的时候全部取消请求
        if (calls == null) calls = new ArrayList<>();
        calls.add(call);
        return call;
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (calls != null && !calls.isEmpty()) {//取消Retrofit的网络请求
            for (Call<?> call : calls) {
                if (call != null) call.cancel();
            }
            calls.clear();
        }
        calls = null;
    }

    //可自定义一些你想要的其它方法...
}
