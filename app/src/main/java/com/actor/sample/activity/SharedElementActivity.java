package com.actor.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.actor.myandroidframework.bean.OnActivityCallback;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.sharedelement.BaseSharedElementCallback;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.adapter.SharedElementAdapter;
import com.actor.sample.databinding.ActivitySharedElementBinding;
import com.actor.sample.utils.Global;
import com.actor.sample.utils.ImageConstants;
import com.blankj.utilcode.util.GsonUtils;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

/**
 * Description: 主页->元素共享跳转
 * Author     : ldf
 * Date       : 2020/2/6 on 18:41
 */
public class SharedElementActivity extends BaseActivity<ActivitySharedElementBinding> {

    //跳转下一页面的时候回调
    private final BaseSharedElementCallback exitSharedElementCallback             = new BaseSharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            LogUtils.errorFormat("names=%s", GsonUtils.toJson(names));
            if (isClickGo2RecyclerView) {
                names.clear();
                sharedElements.clear();
                String transitionName = Global.getListTransitionName(positionRecyclerView, true);
                names.add(transitionName);
                sharedElements.put(transitionName, viewBinding.iv0);
                LogUtils.errorFormat("names=%s", GsonUtils.toJson(names));

                LogUtils.errorFormat("positionRecyclerView = %d, isClickGo2RecyclerViewPositionChanged = %b", positionRecyclerView, isClickGo2RecyclerViewPositionChanged);
                //if已经从下一页返回新的position
                if (isClickGo2RecyclerViewPositionChanged) {
                    isClickGo2RecyclerViewPositionChanged = false;
                    Glide.with(mActivity)
                            .load(ImageConstants.IMAGE_SOURCE[positionRecyclerView])
                            .dontAnimate()
//                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(viewBinding.iv0);
                }
                return;
            }
            if (isClickFromRecyclerView) {
                names.clear();
                sharedElements.clear();
                View iv = myAdapter.getViewByPosition(positionRecyclerView, R.id.iv);
                if (iv == null) return;
                String transitionName = iv.getTransitionName();
                names.add(transitionName);
                sharedElements.put(transitionName, iv);
                LogUtils.errorFormat("names=%s", GsonUtils.toJson(names));
            }
        }
        @Override
        public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, OnSharedElementsReadyListener listener) {
            super.onSharedElementsArrived(sharedElementNames, sharedElements, listener);
//            LogUtils.errorFormat("positionRecyclerView = %d, isClickGo2RecyclerViewPositionChanged = %b", positionRecyclerView, isClickGo2RecyclerViewPositionChanged);
            //也有在这儿重新加载图片
            if (isClickGo2RecyclerView) {
//                //if已经从下一页返回新的position
//                if (isClickGo2RecyclerViewPosition) {
//                    Glide.with(mActivity)
//                            .load(ImageConstants.IMAGE_SOURCE[positionRecyclerView])
//                            .dontAnimate()
////                            .placeholder(R.drawable.logo)
//                            .error(R.drawable.logo)
//                            .into(viewBinding.iv0);
//                }
            }
        }
    }.setLogPageTag("页面SharedElementActivity Exit");
    private final SharedElementAdapter      myAdapter               = new SharedElementAdapter(-1);
    private       int                       positionRecyclerView    = 0;
    private       boolean                   isClickGo2RecyclerView  = false;
    private       boolean                   isClickFromRecyclerView               = false;
    private       boolean                   isClickGo2RecyclerViewPositionChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->元素共享跳转");

        //Activity→Activity
        viewBinding.btnActivity2Activity.setOnClickListener(v -> {
            viewBinding.iv0.setTransitionName("iv");
            startActivity(new Intent(this, SharedElement2Activity.class), viewBinding.iv0);
        });

        //Activity→activity.RecyclerView
        viewBinding.btnActivity2ActivityRecyclerView.setOnClickListener(v -> {
            Integer position = getInputInt(viewBinding.etPosition);
            if (position == null) return;
            positionRecyclerView = position;
            if (false) {
                viewBinding.iv0.setTransitionName(Global.getListTransitionName(position, true));
                startActivity(SharedElementRecyclerViewActivity.getIntent(this, position, false),
                        viewBinding.iv0
                );
            } else {    //等待返回结果更新imageView
                isClickGo2RecyclerView = true;
                if (true) {
                    startActivityForResult(SharedElementRecyclerViewActivity.getIntent(this, position, true),
                            new OnActivityCallback() {
                                @Override
                                public void onActivityResult(int resultCode, @Nullable Intent data) {
                                    isClickGo2RecyclerView = false;
                                    if (resultCode == RESULT_OK && data != null) {
                                        ToasterUtils.info(data.getStringExtra(Global.CONTENT));
                                    }
                                }
                            }, exitSharedElementCallback);
                } else {
                    //这样也可以
                    viewBinding.iv0.setTransitionName(Global.getListTransitionName(position, true));
                    startActivityForResult(SharedElementRecyclerViewActivity.getIntent(this, position, true),
                            new OnActivityCallback() {
                                @Override
                                public void onActivityResult(int resultCode, @Nullable Intent data) {
                                    isClickGo2RecyclerView = false;
                                    if (resultCode == RESULT_OK && data != null) {
                                        Glide.with(mActivity)
                                                .load(ImageConstants.IMAGE_SOURCE[positionRecyclerView])
                                                .dontAnimate()
//                                                .placeholder(R.drawable.logo)
                                                .error(R.drawable.logo)
                                                .into(viewBinding.iv0);
                                        ToasterUtils.info(data.getStringExtra(Global.CONTENT));
                                    }
                                }
                            }, viewBinding.iv0);    //传入view
                }
            }
        });

        Glide.with(this).load(Global.girl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(viewBinding.iv0);
        viewBinding.tv0.setText(getStringFormat("position[0~%d]:", ImageConstants.IMAGE_SOURCE.length - 1));

        //进入页面的时候会回调
        setEnterSharedElementCallback(new BaseSharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
            }
        }.setLogPageTag("页面SharedElementActivity Enter"));

        myAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            isClickFromRecyclerView = true;
            positionRecyclerView = position;
            startActivityForResult(ViewPagerActivity.getIntent(this, position, true), new OnActivityCallback() {
                        @Override
                        public void onActivityResult(int resultCode, @Nullable Intent data) {
                            isClickFromRecyclerView = false;
                            if (resultCode == RESULT_OK && data != null) {
                                ToasterUtils.info(data.getStringExtra(Global.CONTENT));
                            }
                        }
                    }, exitSharedElementCallback);
        });
        viewBinding.recyclerView.setAdapter(myAdapter);
    }


    /**
     * 6.重写方法
     */
    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        super.onActivityReenter(requestCode, data);
        LogUtils.errorFormat("requestCode=%d, data=%s", requestCode, data);
        if (data == null) return;
        int position = data.getIntExtra(Global.POSITION, 0);
        LogUtils.errorFormat("position=%d", position);
        if (position != positionRecyclerView) {
            positionRecyclerView = position;

            //if点击跳转RecyclerView
            if (isClickGo2RecyclerView) {
                isClickGo2RecyclerViewPositionChanged = true;
            }

            //if要更新RecyclerView
            if (isClickFromRecyclerView) {
                viewBinding.recyclerView.scrollToPosition(position);
//                viewBinding.recyclerView.smoothScrollToPosition(position);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LogUtils.errorFormat("requestCode=%d, resultCode=%d, data=%s", requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
