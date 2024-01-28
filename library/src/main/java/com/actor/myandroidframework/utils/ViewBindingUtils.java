package com.actor.myandroidframework.utils;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
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
     * 从 XxxActivity<XxxBinding> 的泛型中获取viewBinding
     */
    @Nullable
    public static <VB extends ViewBinding> VB initViewBinding(@NonNull AppCompatActivity activity) {
        Class<? extends AppCompatActivity> aClass = activity.getClass();
        Class<VB> cls = getBinding(aClass);
        if (cls == null) return null;
        try {
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
            return (VB) inflate.invoke(null, activity.getLayoutInflater());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            if (ConfigUtils.IS_APP_DEBUG && e instanceof NoSuchMethodException) {
                LogUtils.errorFormat("%s<%s> 的 viewBinding 初始化失败, 可能是混淆代码后在 %s 中对 viewBinding 没有直接的引用造成的!\n" +
                                "请在onCreate()中至少添加一条引用代码, 例: viewBinding.getRoot();",
                        aClass.getName(), cls.getSimpleName(), aClass.getSimpleName());
            }
        }
        return null;
    }

    /**
     * 从 XxxFragment<XxxBinding> 的泛型中获取viewBinding
     */
    @Nullable
    public static <VB extends ViewBinding> VB initViewBinding(@NonNull Class<? extends Fragment> fragmentClass, @NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        Class<VB> cls = getBinding(fragmentClass);
        if (cls == null) return null;
        try {
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            return (VB) inflate.invoke(null, inflater, container, false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            if (ConfigUtils.IS_APP_DEBUG && e instanceof NoSuchMethodException) {
                LogUtils.errorFormat("%s<%s> 的 viewBinding 初始化失败, 可能是混淆代码后在 %s 中对 viewBinding 没有直接的引用造成的!\n" +
                                "请在onViewCreated()中至少添加一条引用代码, 例: viewBinding.getRoot();",
                        fragmentClass.getName(), cls.getSimpleName(), fragmentClass.getSimpleName());
            }
        }
        return null;
    }

    /**
     * 从 XxxDialog<XxxBinding> 的泛型中获取viewBinding
     */
    @Nullable
    public static <VB extends ViewBinding> VB initViewBinding(@NonNull Dialog dialog) {
        Class<? extends Dialog> aClass = dialog.getClass();
        Class<VB> cls = getBinding(aClass);
        if (cls == null) return null;
        try {
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
            return (VB) inflate.invoke(null, dialog.getLayoutInflater());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            if (ConfigUtils.IS_APP_DEBUG && e instanceof NoSuchMethodException) {
                LogUtils.errorFormat("%s<%s> 的 viewBinding 初始化失败, 可能是混淆代码后在 %s 中对 viewBinding 没有直接的引用造成的!\n" +
                                "请在onCreate()中至少添加一条引用代码, 例: viewBinding.getRoot();",
                        aClass.getName(), cls.getSimpleName(), aClass.getSimpleName());
            }
        }
        return null;
    }

    /**
     * 从 RecyclerView的Adapter 中获取viewBinding, 由于ViewHolder的类型很多, 所以请自己实现, 示例: <br />
     * <pre>
     * <code>@</code>NonNull
     * <code>@</code>Override
     * public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     *     XxxBinding viewBinding = XxxBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
     *     return new YourViewHolder(viewBinding);
     * }
     * <code>@</code>Override
     * onBindViewHolder(@NonNull YourViewHolder holder, int position) {
     *     holder.viewBinding.tvResult.setText("Hello World!");
     * }
     * class YourViewHolder extends RecyclerView.ViewHolder {
     *     XxxViewBinding viewBinding;
     *     public YourViewHolder(XxxBinding viewBinding) {
     *         super(viewBinding.getRoot());
     *         this.viewBinding = viewBinding;
     *     }
     * }
     * </pre>
     */
    @Nullable
    public static <VB extends ViewBinding> VB initViewBinding(RecyclerView.ViewHolder viewHolder, @NonNull ViewGroup parent) {
        return null;
    }

    /**
     * 自定义ViewGroup 中获取viewBinding, 示例: <br />
     * <pre>
     * protected XxxBinding viewBinding;
     * public YourCustomView(Context context) {
     *     super(context);
     *     init(context, null);
     * }
     * public YourCustomView(Context context, AttributeSet attrs) {
     *     super(context, attrs);
     *     init(context, attrs);
     * }
     * //...其余构造方法等, 都调用init()方法...
     *
     * protected void init(Context context, @Nullable AttributeSet attrs) {
     *     viewBinding = XxxBinding.inflate(LayoutInflater.from(context), this, true);
     * }
     * </pre>
     */
    @Nullable
    public static <VB extends ViewBinding> VB initViewBinding(@NonNull ViewGroup viewGroup) {
        return null;
    }

    /**
     * PopupWindow 中使用viewBinding, 示例: <br />
     * <pre>
     * private XxxBinding viewBinding = XxxBinding.inflate(LayoutInflater.from(context));
     * PopupWindow popup = new PopupWindow(viewBinding.getRoot(), ...);
     * </pre>
     */
    @Nullable
    public static <VB extends ViewBinding> VB initViewBinding(@NonNull PopupWindow popupWindow) {
        return null;
    }

    /**
     * 获取类上的 ViewBinding泛型
     */
    public static <VB extends ViewBinding> Class<VB> getBinding(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArguments.length > 0
//                    && actualTypeArguments[0] instanceof ViewBinding //实际没有继承ViewBinding...
            ) {
                return (Class<VB>) actualTypeArguments[0];
            }
        } //else LogUtils.error("没有写泛型");
        return null;
    }
}
