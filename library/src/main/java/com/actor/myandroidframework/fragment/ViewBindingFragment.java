package com.actor.myandroidframework.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.ViewBindingUtils;

/**
 * description: 可以初始化 ViewBinding 的Fragment
 * 注意, 如果使用viewBinding, 需要在模块的build.gradle中添加:
 * android {
 *     ...
 *     buildFeatures {
 *       //使用viewBinding
 *       viewBinding = true
 *     }
 * }
 *
 * @author : ldf
 * date       : 2021/9/2 on 18
 * @version 1.0
 */
public class ViewBindingFragment<VB extends ViewBinding> extends ActorBaseFragment {

    /**
     * 是否自动初始化viewBinding, 默认true
     * 如果不初始化viewBinding:
     *      1.子类不用传VB类型的泛型
     *      2.调用 super.{@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} 方法之前设置: needInitViewBinding = false;
     */
    protected boolean needInitViewBinding = true;
    /**
     * 注意: 如果你的 XxxFragment<VB> 类中没有使用 viewBinding 这个变量,
     *       那么在混淆代码后会变成 XxxFragment<Object>, 会导致 viewBinding 初始化失败!
     */
    protected VB      viewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (loggable) LogUtils.error(this);
        return initViewBinding$setContentView(inflater, container, savedInstanceState);
    }

    /**
     * 初始化ViewBinding & setContentView()
     */
    protected View initViewBinding$setContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (needInitViewBinding) {
            viewBinding = ViewBindingUtils.initViewBinding(this.getClass(), inflater, container);
            if (viewBinding != null) {
                return viewBinding.getRoot();
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinding = null;
    }
}
