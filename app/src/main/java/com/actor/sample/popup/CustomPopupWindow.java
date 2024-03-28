package com.actor.sample.popup;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.action.AnimAction;
import com.actor.myandroidframework.popupwindow.BasePopupWindow;

/**
 * description: 自定义 PopupWindow 示例
 * company    :
 *
 * @author : ldf
 * date       : 2024/3/28 on 17
 * @version 1.0
 */
public class CustomPopupWindow extends BasePopupWindow {

    public CustomPopupWindow(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        TextView view = new TextView(context);
        view.setText("我是小弹窗哦!!!");
        view.setTextSize(20);
        view.setBackgroundColor(Color.RED);

        setWidth(300);  //瞎设一个宽度
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(view);
        setAnimationStyle(AnimAction.ANIM_LEFT_SLIDE);
    }
}
