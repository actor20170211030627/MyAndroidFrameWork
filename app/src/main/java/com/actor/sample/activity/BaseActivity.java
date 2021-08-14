package com.actor.sample.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.actor.sample.MyApplication;
import com.blankj.utilcode.util.CacheDiskUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Description: 基类
 * Date       : 2019/8/24 on 11:25
 *
 * @version 1.0
 */
public class BaseActivity<VB extends ViewBinding> extends ActorBaseActivity {

    /**
     * 如果不传入泛型, viewBinding = null;
     */
    protected VB viewBinding;

    //硬盘缓存
    protected CacheDiskUtils aCache = MyApplication.instance.aCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<VB> cls = (Class<VB>) ((ParameterizedType) type).getActualTypeArguments()[0];
            try {
                Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
                viewBinding = (VB) inflate.invoke(null, getLayoutInflater());
                setContentView(viewBinding.getRoot());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
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

    //可自定义一些你想要的其它方法...
}
