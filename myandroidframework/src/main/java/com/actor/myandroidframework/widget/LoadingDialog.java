package com.actor.myandroidframework.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actor.myandroidframework.R;

/**
 * Description: 加载Dialog
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/6/28 on 16:43
 * @version 1.0
 */
public class LoadingDialog extends Dialog {

    private View        backgroundView;
    private ProgressBar progress;
    private TextView    message;
    private int cornerRadius = 15;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init(){
        //背景透明, 不然自定义view点后面有一个白色框框
        Window window = getWindow();
        if (window != null) window.setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.layout_for_loading_dialog);
        backgroundView = findViewById(R.id.progress_dialog_background_view);
        progress = findViewById(R.id.progress_dialog_progress);
        message = findViewById(R.id.progress_dialog_message);

        setCornerRadius(cornerRadius);
        setDimAmount(0.5F);
    }

    /**
     * 设置点击返回键 & 点击外部, 是否能取消dialog
     * @param cancelAble
     * @return
     */
    public LoadingDialog setCancelAble(boolean cancelAble) {
        setCancelable(cancelAble);
        setCanceledOnTouchOutside(cancelAble);
        return this;
    }

    /**
     * 设置背景透明度
     */
    public LoadingDialog setDimAmount(@FloatRange(from = 0, to = 1) float dimAmount){
        Window window = getWindow();
        if (window != null) window.setDimAmount(0.2f);//设置窗口后面灰色大背景的亮度[0-1]
        return this;
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
        backgroundView.setBackground(drawable);
        return this;
    }

    /**
     * 设置内容, 比如: 加载中...
     * @param charSequence
     * @return
     */
    public LoadingDialog setMessage(CharSequence charSequence) {
        message.setText(charSequence);
        return this;
    }

    @Override
    public void show() {
        if (TextUtils.isEmpty(message.getText())) {
            message.setVisibility(View.GONE);
        } else message.setVisibility(View.VISIBLE);
        super.show();
    }
}
