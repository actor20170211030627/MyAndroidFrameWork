package com.actor.myandroidframework.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.TextUtils2;

/**
 * Description: 加载Dialog
 * Author     : 李大发
 * Date       : 2019/6/28 on 16:43
 * @version 1.0
 * todo 动态修改view 宽高 转速
 */
public class LoadingDialog extends BaseDialog {

    private View        viewBackground;
    private ProgressBar progressBar;
    private TextView    tvMessage;

    private int cornerRadius = 15;//圆角

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_for_loading_dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBackground = findViewById(R.id.progress_dialog_background_view);
        progressBar = findViewById(R.id.progress_dialog_progress);
        tvMessage = findViewById(R.id.progress_dialog_message);
        setDimAmount(0.5F);
        setCornerRadius(cornerRadius);
    }

    /**
     * 设置圆角
     */
    public LoadingDialog setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        //外部矩形弧度
        float[] outerR = new float[] {cornerRadius, cornerRadius, cornerRadius, cornerRadius,
                cornerRadius, cornerRadius, cornerRadius, cornerRadius};
        //内部矩形与外部矩形的距离
        RectF inset = new RectF(0, 0, 0, 0);
        RectShape shape = new RoundRectShape(outerR, inset, outerR);
        ShapeDrawable drawable = new ShapeDrawable(shape);
        drawable.getPaint().setColor(Color.parseColor("#b1000000"));
        viewBackground.setBackground(drawable);
        return this;
    }

    /**
     * 设置内容, 比如: 加载中...
     * @param message
     * @return
     */
    public LoadingDialog setMessage(CharSequence message) {
        if (TextUtils2.isNoEmpty(message)) {
            tvMessage.setVisibility(View.GONE);
        } else {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(message);
        }
        return this;
    }
}
