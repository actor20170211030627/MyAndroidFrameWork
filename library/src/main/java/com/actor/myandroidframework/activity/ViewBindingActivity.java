package com.actor.myandroidframework.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.actor.myandroidframework.utils.ViewBindingUtils;

/**
 * description: 可以初始化 ViewBinding 的Activity <br />
 * 注意, 如果使用viewBinding, 需要在模块的build.gradle中添加:
 * <pre>
 *     android {
 *         ...
 *         buildFeatures {
 *           //使用viewBinding
 *           viewBinding = true
 *         }
 *     }
 * </pre>
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
}
