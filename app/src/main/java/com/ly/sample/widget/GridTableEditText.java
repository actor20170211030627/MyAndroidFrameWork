package com.ly.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actor.myandroidframework.utils.TextUtil;
import com.ly.sample.R;

/**
 * Description: 常用的表格输入布局,这是一个组合控件.
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/7/10 on 17:20
 */
public class GridTableEditText extends LinearLayout implements TextUtil.GetTextAble {

    private              TextView  tvRedStar;
    private              TextView  tv1;
    private              EditText  et1;
    private              ImageView ivArrowRight;

    public GridTableEditText(Context context) {
        this(context,null);
    }

    public GridTableEditText(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public GridTableEditText(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置view, 并找到子view
        View inflate = View.inflate(context, R.layout.item_grid_table_edit_text, this);
        tvRedStar = inflate.findViewById(R.id.tv_red_star_for_gte);
        tv1 = inflate.findViewById(R.id.tv_item_name_for_gte);
        et1 = inflate.findViewById(R.id.et_input_for_gte);
        ivArrowRight = inflate.findViewById(R.id.iv_arrow_right_for_gte);

        if (attrs == null) return;
        //根据xml中属性, 给view赋值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GridTableEditText);
        //红点是否显示
        int redStarVisiable = typedArray.getInt(R.styleable.GridTableEditText_gteRedStarVisiable, 0);
        //EditText是否能输入
        boolean inputEnable = typedArray.getBoolean(R.styleable.GridTableEditText_gteInputEnable, true);
        //左侧TextView的Text
        String gteItemName = typedArray.getString(R.styleable.GridTableEditText_gteItemName);
        //输入框的Hint
        String gteHint = typedArray.getString(R.styleable.GridTableEditText_gteHint);
        //输入框的Text
        String gteText = typedArray.getString(R.styleable.GridTableEditText_gteText);
        //输入类型
        int gteImeOptions = typedArray.getInt(R.styleable.GridTableEditText_gteImeOptions, -1);
        //最大输入长度
        int gteMaxLength = typedArray.getInt(R.styleable.GridTableEditText_gteMaxLength, -1);
        //输入类型
        int gteInputType = typedArray.getInt(R.styleable.GridTableEditText_gteInputType, -1);
        //输入限定
        String gteDigits = typedArray.getString(R.styleable.GridTableEditText_gteDigits);
        //右侧箭头显示状态
        int arrowRightVisiable = typedArray.getInt(R.styleable.GridTableEditText_gteArrowRightVisiable, -1);

        tvRedStar.setVisibility(redStarVisiable * 4);
        if (!inputEnable) setInputEnable(false);
        if (gteItemName != null) tv1.setText(gteItemName);
        if (gteHint != null) et1.setHint(gteHint);
        if (gteText != null) setText(gteText);
        if(gteImeOptions != -1) et1.setImeOptions(gteImeOptions);
        if (gteMaxLength != -1) {
                et1.setFilters(new InputFilter[] { new InputFilter.LengthFilter(gteMaxLength)});
            }
        if (gteInputType != -1) et1.setInputType(gteInputType);
        if (!TextUtils.isEmpty(gteDigits)) {
                et1.setKeyListener(DigitsKeyListener.getInstance(gteDigits));
            }
        if (arrowRightVisiable == -1) {
            if (inputEnable) {//如果能输入
                ivArrowRight.setVisibility(GONE);//隐藏
            } else ivArrowRight.setVisibility(VISIBLE);//显示
        } else ivArrowRight.setVisibility(arrowRightVisiable * 4);//根据属性来设置显示状态
        typedArray.recycle();
    }

    /**
     * 获取红点
     * @return
     */
    public TextView getTextViewRedStar() {
        return tvRedStar;
    }

    /**
     * 返回Item的TextView
     * @return
     */
    public TextView getTextViewItem() {
        return tv1;
    }

    /**
     * 获取输入框
     * @return
     */
    @Override
    public EditText getEditText(){
        return et1;
    }

    @Override
    public Editable getText(){
        return et1.getText();
    }

    public void setText(CharSequence text) {
        et1.setText(text);
    }

    @Override
    public CharSequence getHint(){
        return et1.getHint();
    }

    public void setHint(CharSequence hilt){
        et1.setHint(hilt);
    }

    /**
     * 设置是否可输入(false的时候,可以当做TextView展示)
     * @param enable
     */
    public void setInputEnable(boolean enable) {
//        et1.setEnabled(enable);//这样不能编辑,可用于隐藏输入法,但是EditText的点击事件无反应,不能做点击事件
        et1.setFocusable(enable);
        et1.setClickable(!enable);
        et1.setFocusableInTouchMode(enable);
//        if (enable) et1.requestFocus();//把光标移动到这一个et1,但是不弹出键盘
//        et1.setCursorVisible(false);

    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener onClickListener) {
        //System.out.println(getChildAt(0));//android.widget.LinearLayout
        getChildAt(0).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) onClickListener.onClick(GridTableEditText.this);
            }
        });
        et1.setOnClickListener(new OnClickListener() {//必须要设置,否则点击EditText无效
            @Override
            public void onClick(View v) {
                if (onClickListener != null && et1.isClickable()) {
                    onClickListener.onClick(GridTableEditText.this);
                }
            }
        });
    }
}
