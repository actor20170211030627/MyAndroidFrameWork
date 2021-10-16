package com.actor.sample.fragment;

import androidx.viewbinding.ViewBinding;

import com.actor.myandroidframework.fragment.ViewBindingFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Description: Fragment基类
 * Author     : ldf
 * Date       : 2019-9-6 on 15:59
 *
 * @version 1.0
 */
public class BaseFragment<VB extends ViewBinding> extends ViewBindingFragment<VB> {

    //Retrofit感觉一点都不好用,太死板
    @Deprecated
    protected List<Call<?>> calls;


    ///////////////////////////////////////////////////////////////////////////
    // Retrofit区
    ///////////////////////////////////////////////////////////////////////////
    @Deprecated //Retrofit感觉一点都不好用,太死板
    protected <T> Call<T> putCall(Call<T> call) {//放入List, onDestroy的时候全部取消请求
        if (calls == null) calls = new ArrayList<>();
        calls.add(call);
        return call;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (calls != null && !calls.isEmpty()) {//取消Retrofit的网络请求
            for (Call<?> call : calls) {
                if (call != null) call.cancel();
            }
            calls.clear();
        }
        calls = null;
    }

    //可自定义一些你想要的其它方法
}
