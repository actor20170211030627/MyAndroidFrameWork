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
 * description: ViewBinding工具类
 * company    :
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
}
