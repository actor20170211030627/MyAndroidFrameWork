package com.actor.sample.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actor.myandroidframework.action.AnimAction;
import com.actor.myandroidframework.popupwindow.BasePopupWindow;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityPopupWindowTestBinding;
import com.actor.sample.popup.CustomPopupWindow;

/**
 * description: PopupWindow测试
 * company    :
 * @author    : ldf
 * date       : 2024/3/25 on 17:39
 */
public class PopupWindowTestActivity extends BaseActivity<ActivityPopupWindowTestBinding> {

    private BasePopupWindow popup;
    private CustomPopupWindow customPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("PopupWindow测试");
        viewBinding.btnSetLayout.setOnClickListener(this::setLayout);
        viewBinding.btnDismiss.setOnClickListener(v -> {
            if (popup != null) {
                popup.dismiss();
            }
        });
        viewBinding.btnSetView.setOnClickListener(this::setView);
        viewBinding.btn3.setOnClickListener(v -> {
            ToasterUtils.warning("btn3");
        });
    }


    private void setLayout(View v) {
        if (popup == null) {
            popup = new BasePopupWindow(this);
            //设置内容
            popup.setContentView(R.layout.item_text_input_layout);
            popup.getContentView().setBackgroundColor(Color.WHITE);
            //设置宽高
            popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

            //查找控件, 添加点击
            TextView tvContent = popup.findViewById(R.id.tv_item_name_for_itil);
            tvContent.setText("这是设置的内容啊!@");
            tvContent.setOnClickListener(v1 -> {
                ToasterUtils.warning("哎呦, 你干嘛?");
            });

            //设置3种able示例
            popup.setFocusable(false);
            popup.setTouchable(true);
            popup.setOutsideTouchable(false);

            //设置动画
            popup.setAnimationStyle(AnimAction.ANIM_DEFAULT);
            //设置背景透明度
            popup.setBackgroundDimAmount(0.5f);


            //设置监听
            popup.setOnDismissListener(popupWindow -> {
                LogUtils.error("onDismiss");
            });

            popup.setOnShowListener(popupWindow -> {
                LogUtils.error("onShow");
            });


            //是否覆盖附着View, 仅对 showAsDropDown(...) 生效
            popup.setOverlapAnchor(false);

            //设置Window的layout类型
//            popup.setWindowLayoutType();

//            popup.update(300, 500);
        }


        //显示在某个位置
//        popup.showAtLocation(v, Gravity.TOP | Gravity.START, 0, 250);

        //显示在某个控件正下方
        popup.showAsDropDown(v, 0, 0, Gravity.BOTTOM | Gravity.START);
    }

    private void setView(View v) {
        if (customPopup == null) {
            customPopup = new CustomPopupWindow(this);
        }

        //显示在某个位置, 参1:布局对象;参2:重心位置; 参3,4:基于重心位置的偏移
        //customPopup.showAtLocation(rlRoot, Gravity.CENTER, 0, 0);

        //显示在某个控件正下方(推荐)
        //参1:哪个控件; 参2,3:偏移
        customPopup.showAsDropDown(v, 0, 0);
    }
}