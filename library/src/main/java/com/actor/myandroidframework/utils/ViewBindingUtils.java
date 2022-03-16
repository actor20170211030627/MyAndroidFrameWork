package com.actor.myandroidframework.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * description: ViewBinding工具类, 可参考: <a href="https://blog.csdn.net/c10WTiybQ1Ye3/article/details/112690188" target="_blank">博客</a> <br />
 *
 * <pre>
 * //https://github.com/JakeWharton/butterknife 可用于生成onViewClicked(View view)方法
 * compileOnly 'com.jakewharton:butterknife:10.2.3'
 * //annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'
 *
 * ViewBinding缺点:
 *  1.不能替换掉 Butterknife 的 @OnClick() 点击事件注解. 如果点击事件很多, 写起来很麻烦.
 *    不过可以在xml中的view写: android:onClick="onViewClicked", 然后{@link android.app.Activity Activity}中的 {@link com.actor.myandroidframework.activity.ViewBindingActivity#onViewClicked(View) onViewClicked(View)}用 ButterKnife 生成.
 *  2.★注意★: {@link Fragment Fragment} 的xml中如果写 onViewClicked 会调用到Activity的这个点击事件去...
 *          Fragment中只能手动设置点击事件: viewBinding.viewXxx.setOnClickListener(this::onViewClicked);
 *          或者继承{@link com.actor.myandroidframework.fragment.ViewBindingFragment ViewBindingFragment}并重写{@link com.actor.myandroidframework.fragment.ViewBindingFragment#onViewClicked(View) onViewClicked(View)}方法
 *  3.如果xml中View太多, viewBinding写起来太麻烦, 没有 ButterKnife 好用...
 * </pre>
 *
 * @author : ldf
 * date       : 2021/9/2 on 16
 * @version 1.0
 */
public class ViewBindingUtils {

    /**
     * 从Activity中获取viewBinding
     */
    @Nullable
    public static <VB extends ViewBinding> VB initViewBinding(AppCompatActivity activity) {
        Class<? extends AppCompatActivity> aClass = activity.getClass();
        Type type = aClass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<VB> cls = (Class<VB>) ((ParameterizedType) type).getActualTypeArguments()[0];
            try {
                Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
                return (VB) inflate.invoke(null, activity.getLayoutInflater());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                if (ConfigUtils.IS_APP_DEBUG && e instanceof NoSuchMethodException) {
                    LogUtils.formatError("%s 的 viewBinding 初始化失败,\n" +
                                    "泛型类型为: %s.\n" +
                                    "是混淆代码后在 %s 中对 viewBinding 没有直接的引用造成的!\n" +
                                    "请在onCreate()中至少添加一条引用代码, 例: viewBinding.getRoot();",
                            aClass.getName(), cls.getName(), aClass.getSimpleName());
                }
            }
        } //else LogUtils.error("没有写泛型");
        return null;
    }

    /**
     * 从Fragment中获取viewBinding
     */
    @Nullable
    public static <VB extends ViewBinding> VB initViewBinding(@NonNull Fragment fragment, @NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        Class<? extends Fragment> aClass = fragment.getClass();
        Type type = aClass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<VB> cls = (Class<VB>) ((ParameterizedType) type).getActualTypeArguments()[0];
            try {
                Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                return (VB) inflate.invoke(null, inflater, container, false);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                if (ConfigUtils.IS_APP_DEBUG && e instanceof NoSuchMethodException) {
                    LogUtils.formatError("%s(是一个Fragment) 的 viewBinding 初始化失败,\n" +
                                    "泛型类型为: %s.\n" +
                                    "是混淆代码后在 %s 中对 viewBinding 没有直接的引用造成的!\n" +
                                    "请在onViewCreated()中至少添加一条引用代码, 例: viewBinding.getRoot();",
                            aClass.getName(), cls.getName(), aClass.getSimpleName());
                }
            }
        } //else LogUtils.error("没有写泛型");
        return null;
    }

    /**
     * ViewBinding在RecyclerView中使用:
     *
     * @NonNull
     * @Override
     * public YourAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     *     XxxViewBinding binding = XxxViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
     *     return new MyViewHolder(binding);
     * }
     *
     * @Override
     * public void onBindViewHolder(@NonNull YourAdapter.MyViewHolder holder, int position) {
     *     holder.binding.tvResult.setText("Hello!");
     * }
     *
     * static class MyViewHolder extends RecyclerView.ViewHolder {
     *     XxxViewBinding binding;
     *     public ViewHolder(@NonNull XxxViewBinding binding) {
     *         super(binding.getRoot());
     *         this.binding = binding;
     *     }
     * }
     */


    /**
     * 在Dialog的'构造方法'中使用:
     * XxxBinding inflate = XxxBinding.inflate(getLayoutInflater());
     * setContentView(inflate.getRoot());
     * btn = inflate.btn;
     * ...
     */
}
