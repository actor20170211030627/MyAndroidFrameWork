package com.actor.myandroidframework.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.actor.myandroidframework.utils.ViewBindingUtils;

/**
 * description: 可以初始化 ViewBinding 的Activity
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
public class ViewBindingActivity<VB extends ViewBinding> extends ActorBaseActivity {

    /**
     * 是否自动初始化viewBinding, 默认true
     * 如果不初始化viewBinding:
     *      1.子类不用传VB类型的泛型
     *      2.调用 super.onCreate(savedInstanceState); 方法之前, 设置: needInitViewBinding = false;
     */
    protected boolean                   needInitViewBinding = true;
    /**
     * 注意: 如果你的 XxxActivity<VB> 类中没有使用 viewBinding 这个变量,
     *       那么在混淆代码后会变成 XxxActivity<Object>, 会导致 viewBinding 初始化失败!
     */
    protected VB                        viewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewBinding$setContentView();
    }

    /**
     * 初始化ViewBinding & setContentView()
     */
    protected void initViewBinding$setContentView() {
        if (needInitViewBinding) {
            viewBinding = ViewBindingUtils.initViewBinding(this);
            if (viewBinding != null) {
                setContentView(viewBinding.getRoot());
            }
        }
    }

    /**
     * 替换掉 Butterknife 的 @OnClick() 点击事件注解. 如果点击事件很多, 写起来很麻烦. <br />
     * 1.Activity中可以在xml中的view写: <br />
     *   &emsp; android:onClick="onViewClicked" <br />
     * 2.然后Activity中的 重写 {@link #onViewClicked(View view)} (用 ButterKnife 生成).
     * @param ids 要设置点击事件的view的id
     *
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
}
