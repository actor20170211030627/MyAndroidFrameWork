package com.actor.myandroidframework.utils;

import android.view.LayoutInflater;
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
 * description: ViewBinding工具类 https://blog.csdn.net/c10WTiybQ1Ye3/article/details/112690188
 *
 * //https://github.com/JakeWharton/butterknife 可用于生成onViewClicked()方法 switch-case 的点击事件
 * compileOnly 'com.jakewharton:butterknife:10.2.3'
 * //annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'
 *
 * ViewBinding缺点:
 *  1.不能替换掉 Butterknife 的 @OnClick() 点击事件注解. 如果点击事件很多, 写起来很麻烦.
 *    不过可以在xml中的view写: android:onClick="onViewClicked", 然后Activity中的 onViewClicked(View view)用 ButterKnife 生成.
 *  2.注意: Fragment 的xml中如果写 onViewClicked 会调用到Activity的这个点击事件去...
 *          Fragment中只能手动设置点击事件: viewBinding.viewXxx.setOnClickListener(this::onViewClicked);...
 *  3.如果xml中View太多, viewBinding写起来太麻烦, 没有 ButterKnife 好用...
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
        Type type = activity.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<VB> cls = (Class<VB>) ((ParameterizedType) type).getActualTypeArguments()[0];
            try {
                Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
                VB viewBinding = (VB) inflate.invoke(null, activity.getLayoutInflater());
                if (viewBinding != null) {
                    activity.setContentView(viewBinding.getRoot());
                }
                return viewBinding;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 从Fragment中获取viewBinding
     */
    @Nullable
    public static <VB extends ViewBinding> VB initViewBinding(@NonNull Fragment fragment, @NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        Type type = fragment.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<VB> cls = (Class<VB>) ((ParameterizedType) type).getActualTypeArguments()[0];
            try {
                Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                return (VB) inflate.invoke(null, inflater, container, false);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
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
}
