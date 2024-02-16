package com.actor.myandroidframework.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.ConfigUtils;

/**
 * Description: 加载Dialog, 根布局使用 ConstraintLayout 有问题, 有时候只显示灰色背景
 * Author     : ldf
 * Date       : 2019/6/28 on 16:43
 * @version 1.0
 */
public class LoadingDialog extends BaseDialog {

    private View        viewBackground;
    private ProgressBar progressBar;
    private TextView    tvMessage;

    //背景View的宽高, 宽度百分比: 102/497=0.2052313883299799
    private int bgViewWidth  = (int) (ConfigUtils.APP_SCREEN_WIDTH * 0.2052313883299799F);
    private int bgViewHeight = bgViewWidth;
    private int cornerRadius = 15;//圆角
    private Integer color = Color.parseColor("#b1000000");
    private CharSequence message;

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
        isPrintNameOnCreate = false;
        viewBackground = findViewById(R.id.progress_dialog_background_view);
        progressBar = findViewById(R.id.progress_dialog_progress);
        tvMessage = findViewById(R.id.progress_dialog_message);
        setCornerRadius(cornerRadius, color);
        setMessage(message);
        setViewBgWH(bgViewWidth, bgViewHeight);
    }

    /**
     * 设置圆角 & 圆角矩形背景颜色
     * @param cornerRadius 圆角
     * @param bgColor 背景颜色
     */
    public LoadingDialog setCornerRadius(int cornerRadius, @ColorInt Integer bgColor) {
        this.cornerRadius = cornerRadius;
        if (color != null) this.color = bgColor;

        if (viewBackground != null) {
            //外部矩形弧度
            float[] outerR = new float[] {cornerRadius, cornerRadius, cornerRadius, cornerRadius,
                    cornerRadius, cornerRadius, cornerRadius, cornerRadius};
            //内部矩形与外部矩形的距离
            RectF inset = new RectF(0, 0, 0, 0);
            RectShape shape = new RoundRectShape(outerR, inset, outerR);
            ShapeDrawable drawable = new ShapeDrawable(shape);
            drawable.getPaint().setColor(color);
            viewBackground.setBackground(drawable);
        }
        return this;
    }

    /**
     * 设置内容, 比如: 加载中...
     * @param message 提示内容
     */
    public LoadingDialog setMessage(CharSequence message) {
        this.message = message;
        if (tvMessage != null) {
            if (TextUtils.isEmpty(message)) {
                tvMessage.setVisibility(View.GONE);
            } else {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            }
        }
        return this;
    }

    /**
     * 设置背景View的宽高
     * @param viewBgWidth 宽
     * @param viewBgHeight 高
     * @return
     */
    public LoadingDialog setViewBgWH(int viewBgWidth, int viewBgHeight) {
        this.bgViewWidth = viewBgWidth;
        this.bgViewHeight = viewBgHeight;
        if (viewBackground != null) {
            ViewGroup.LayoutParams layoutParams = viewBackground.getLayoutParams();
            layoutParams.width = bgViewWidth;
            layoutParams.height = bgViewHeight;
            viewBackground.setLayoutParams(layoutParams);
        }
        return this;
    }
}
