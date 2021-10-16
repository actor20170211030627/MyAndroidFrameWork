package com.actor.myandroidframework.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

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
        if (needInitViewBinding) {
            viewBinding = ViewBindingUtils.initViewBinding(this, inflater, container);
            if (viewBinding != null) {
                return viewBinding.getRoot();
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 替换掉 Butterknife 的 @OnClick() 点击事件注解. 如果点击事件很多, 写起来很麻烦.
     * 注意: Fragment 的xml中如果写 android:onClick="onViewClicked" 会调用到Activity的这个点击事件去...
     *       所以Fragment 中请在{@link #onViewCreated(View, Bundle)}方法中手动调用这个方法添加点击事件!
     * @param ids 要设置点击事件的view的id
     */
    protected void setOnClickListeners(@IdRes int... ids) {
        if (viewBinding != null && ids != null && ids.length > 0) {
            View root = viewBinding.getRoot();
            for (int id : ids) {
                root.findViewById(id).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onViewClicked(v);
                    }
                });
            }
        }
    }

    /**
     * 就是和 ButterKnife 生成的点击事件一样的名称
     * //https://github.com/JakeWharton/butterknife 可用于生成onViewClicked()方法 switch-case 的点击事件
     * compileOnly 'com.jakewharton:butterknife:10.2.3'
     * //annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'
     * @param view
     */
    public void onViewClicked(View view) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinding = null;
    }
}
