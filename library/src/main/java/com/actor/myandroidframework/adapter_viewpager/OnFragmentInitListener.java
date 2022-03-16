package com.actor.myandroidframework.adapter_viewpager;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * description: Fragment初始化完成监听. 因为在Activity中有可能网络请求回来之后, Fragment还没有初始化完成, 所以弄了这个监听 <br />
 * Author     : ldf <br />
 * date       : 2022/3/16 on 15
 *
 * @version 1.0
 */
public interface OnFragmentInitListener {

    /**
     * Fragment初始化完成
     * @param container fragment的容器
     * @param position 第几个fragment
     * @param fragment fragment
     */
    void onFragmentInited(@NonNull ViewGroup container, int position, @NonNull Fragment fragment);
}
