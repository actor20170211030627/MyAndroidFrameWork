package com.actor.sample.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.bean.OnActivityCallback;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.glide.DrawableRequestListener;
import com.actor.myandroidframework.utils.sharedelement.BaseSharedElementCallback;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.activity.SharedElement2Activity;
import com.actor.sample.activity.SharedElementRecyclerViewActivity;
import com.actor.sample.activity.ViewPagerActivity;
import com.actor.sample.databinding.FragmentSharedElementBinding;
import com.actor.sample.utils.Global;
import com.actor.sample.utils.ImageConstants;
import com.blankj.utilcode.util.GsonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Map;

/**
 * Description: 元素共享中的Fragment
 * Author     : ldf
 * Date       : 2020/2/6 on 18:42
 */
public class SharedElementFragment extends BaseFragment<FragmentSharedElementBinding> {

    private final BaseSharedElementCallback exitSharedElementCallback = new BaseSharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            LogUtils.errorFormat("names = %s, sharedElements.size = %d", GsonUtils.toJson(names), sharedElements.size());
            names.clear();
            sharedElements.clear();
            LogUtils.errorFormat("Global.fragmentPosition = %d", Global.fragmentPosition);
            position2ViewPager = Global.fragmentPosition >= 0 ? Global.fragmentPosition : position2ViewPager;
            String transitionName = Global.getListTransitionName(position2ViewPager, true);
            names.add(transitionName);
            sharedElements.put(transitionName, viewBinding.iv);
            LogUtils.errorFormat("names = %s", GsonUtils.toJson(names));

            //if是从后面页面返回, 就重新加载图片
            if (Global.fragmentPosition >= 0) {
                //也可以在这儿重新加载图片
//                Glide.with(mFragment)
//                        .load(ImageConstants.IMAGE_SOURCE[position2ViewPager])
//                        .into(viewBinding.iv);
            }
        }
        @Override
        public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, OnSharedElementsReadyListener listener) {
            LogUtils.errorFormat("Global.fragmentPosition = %d", Global.fragmentPosition);
            //if是从后面页面返回, 就重新加载图片
            if (Global.fragmentPosition >= 0) {
                //打印日志
                if (!TextUtils.isEmpty(pageTag)) {
                    LogUtils.errorFormat("pageTag = %s", pageTag);
                    LogUtils.errorFormat("sharedElementNames = %s, sharedElements.size = %d, listener = %s", GsonUtils.toJson(sharedElementNames), sharedElements.size(), listener);
                }

                //重新加载返回后的新图片
                Glide.with(mFragment)
                        .load(ImageConstants.IMAGE_SOURCE[position2ViewPager])
                        .error(R.drawable.logo)
                        .listener(new DrawableRequestListener() {
                            @Override
                            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //加载完成后再进行元素共享动画
                                listener.onSharedElementsReady();
                                return super.onLoadFailed(e, model, target, isFirstResource);
                            }
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //加载完成后再进行元素共享动画
                                listener.onSharedElementsReady();
                                return super.onResourceReady(resource, model, target, dataSource, isFirstResource);
                            }
                        })
                        .into(viewBinding.iv);
            } else {
                //继续元素共享动画
                super.onSharedElementsArrived(sharedElementNames, sharedElements, listener);
            }
        }
    };
    private int                       position2ViewPager = 0;//ImageConstants中第几张图片

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.btnStartActivity.setOnClickListener(this::onViewClicked);
        viewBinding.btnActivity2ActivityRecyclerView.setOnClickListener(this::onViewClicked);
        viewBinding.btnStartActivityForResult.setOnClickListener(this::onViewClicked);

        viewBinding.tv1.setText(getStringFormat("position[0~%d]:", ImageConstants.IMAGE_SOURCE.length - 1));

        exitSharedElementCallback.setLogPageTag("Fragment A");

        Glide.with(this)
                .load(ImageConstants.IMAGE_SOURCE[position2ViewPager])
                .into(viewBinding.iv);
    }

    @Override
    public void onResume() {
        super.onResume();
        //重置
        Global.fragmentPosition = -1;
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_activity:   //点击跳转Activity
                viewBinding.iv.setTransitionName("iv");
                startActivity(new Intent(mActivity, SharedElement2Activity.class), viewBinding.iv);
                break;
            case R.id.btn_activity_2_activity_recycler_view:    //4.Fragment→activity.RecyclerView
                Integer position = getInputInt(viewBinding.etPosition);
                if (position == null) return;
                viewBinding.iv.setTransitionName(Global.getListTransitionName(position, true));
                startActivity(SharedElementRecyclerViewActivity.getIntent(mActivity, position, false),
                        viewBinding.iv
                );
                break;
            case R.id.btn_start_activity_for_result:    //5.fragment.startActivityForResult()
                if (true) {
                    startActivityForResult(ViewPagerActivity.getIntent(mActivity, position2ViewPager, true),
                            new OnActivityCallback() {
                                @Override
                                public void onActivityResult(int resultCode, @Nullable Intent data) {
                                    if (resultCode == Activity.RESULT_OK && data != null) {
                                        /**
                                         * 不要在这儿加载图片, 否则会闪动, 因为:
                                         * 1.返回的Activity的元素共享ImageView加载的是另外1张图片.
                                         * 2.动画完成后本页的ImageView会恢复已加载的图片.(开始闪动)
                                         * 3.Glide会将ImageView加载成返回的图片.(和1一样的图片)
                                         */
//                                        Glide.with(mActivity)
//                                                .load(ImageConstants.IMAGE_SOURCE[position2ViewPager])
//                                                .into(viewBinding.iv);
                                        ToasterUtils.info(data.getStringExtra(Global.CONTENT));
                                    }
                                }
                            }, exitSharedElementCallback);
                } else {
                    /**
                     * 这个方法也可以, 但下一页更改了共享元素的话, 返回后会可能找不到共享元素, 或者加载图片会闪动!
                     */
                    viewBinding.iv.setTransitionName(Global.getListTransitionName(position2ViewPager, false));
                    startActivityForResult(ViewPagerActivity.getIntent(mActivity, position2ViewPager, false),
                            new OnActivityCallback() {
                                @Override
                                public void onActivityResult(int resultCode, @Nullable Intent data) {
                                    if (resultCode == Activity.RESULT_OK && data != null) {
                                        int position = data.getIntExtra(Global.POSITION, 0);
                                        LogUtils.errorFormat("position=%d", position);
                                        ToasterUtils.info(data.getStringExtra(Global.CONTENT));
                                    }
                                }
                            }, viewBinding.iv); //传入view
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.errorFormat("requestCode=%d, resultCode=%d, data=%s", requestCode, resultCode, data);
    }

    //获取输入内容
    private Integer getInputInt(EditText editText) {
        Editable text = editText.getText();
        if (text == null) {
            ToasterUtils.warning("请输入跳转位置");
            return null;
        }
        String content = text.toString().trim();
        try {
            int position = Integer.parseInt(content);
            if (position < 0 || position >= ImageConstants.IMAGE_SOURCE.length) {
                ToasterUtils.warning("输入的整数超范围");
                return null;
            }
            return position;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            ToasterUtils.warning("请输入正确的整数");
        }
        return null;
    }
}
