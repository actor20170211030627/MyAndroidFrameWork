package com.actor.sample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.actor.myandroidframework.fragment.ActorBaseFragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Description: Fragment基类
 * Author     : ldf
 * Date       : 2019-9-6 on 15:59
 *
 * @version 1.0
 */
public class BaseFragment<VB extends ViewBinding> extends ActorBaseFragment {

    protected VB viewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<VB> cls = (Class<VB>) ((ParameterizedType) type).getActualTypeArguments()[0];
            try {
                Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                viewBinding = (VB) inflate.invoke(null, inflater, container, false);
                return viewBinding.getRoot();
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //可自定义一些你想要的其它方法
}
