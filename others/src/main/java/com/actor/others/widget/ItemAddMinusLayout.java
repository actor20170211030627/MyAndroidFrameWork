package com.actor.others.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;

import com.actor.others.R;
import com.blankj.utilcode.util.ToastUtils;

/**
 * Description: 加减, 全部属性都是iaml开头
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th align="center">属性attrs</th>
 *         <th align="center">示例exams</th>
 *         <th align="center">说明docs</th>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemAddMinusLayout_iamlRedStarVisibility iamlRedStarVisibility}</td>
 *         <td nowrap="nowrap">visible/invisible/gone</td>
 *         <td>1.左侧红点显示类型, 默认visible</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemAddMinusLayout_iamlItemName iamlItemName}</td>
 *         <td nowrap="nowrap">数量:</td>
 *         <td>2.左侧提示文字</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemAddMinusLayout_iamlValue iamlValue}</td>
 *         <td nowrap="nowrap">0</td>
 *         <td>3.显示的值, 默认0</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemAddMinusLayout_iamlMaxValue iamlMaxValue}</td>
 *         <td nowrap="nowrap">1</td>
 *         <td>4.最大值, 默认1</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemAddMinusLayout_iamlMinValue iamlMinValue}</td>
 *         <td nowrap="nowrap">0</td>
 *         <td>5.最小值, 默认0</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemAddMinusLayout_iamlAlreadyMaxValueHint iamlAlreadyMaxValueHint}</td>
 *         <td nowrap="nowrap">{@link R.string#default_hint_already_max_value R.string.default_hint_already_max_value}</td>
 *         <td>6.已经是最大值了的提示, 默认: 已经是最大了</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemAddMinusLayout_iamlAlreadyMinValueHint iamlAlreadyMinValueHint}</td>
 *         <td nowrap="nowrap">{@link R.string#default_hint_already_min_value R.string.default_hint_already_min_value}</td>
 *         <td>7.已经是最小值了的提示, 默认: 已经是最小了</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemAddMinusLayout_iamlMarginTop iamlMarginTop}</td>
 *         <td nowrap="nowrap">1dp</td>
 *         <td>8.marginTop, 默认1dp</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemAddMinusLayout_iamlContainerMinHeight iamlContainerMinHeight}</td>
 *         <td nowrap="nowrap">49dp</td>
 *         <td>9.主container的最小高度, 默认49dp</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemAddMinusLayout_iamlCustomLayout iamlCustomLayout}</td>
 *         <td nowrap="nowrap">R.layout.xxx</td>
 *         <td>10.自定义布局, 注意必须有默认控件的类型和id. 如果要所有地方都修改layout,可把{@link R.layout#item_add_minus_layout} copy一份到自己工程作修改, 就会加载自己工程的layout</td>
 *     </tr>
 *  </table>
 * @author actor
 * @date   2019-8-19 on 21:10
 */
public class ItemAddMinusLayout extends LinearLayout implements View.OnClickListener {

    protected TextView     tvRedStar, tvItem, tvValue;
    protected Space        spaceMarginTop;
    protected LinearLayout llContainer;
    protected int          minValue = 0, maxValue = 1, nowValue = 0;
    protected String       hintAlreadyMaxValue, hintAlreadyMinValue;//自定义提示
    //px = dp * density;
    protected float        density;

    public ItemAddMinusLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ItemAddMinusLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ItemAddMinusLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ItemAddMinusLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        density = getResources().getDisplayMetrics().density;
        //可以自定义重写此布局到自己layout目录
        int layoutId = R.layout.item_add_minus_layout;
        int defaultContainerMinHeight = (int) getResources().getDimension(R.dimen.item_add_minus_layout_container_min_height);
        if (attrs == null) {
            inflate(context, layoutId, defaultContainerMinHeight);
        } else {
            //根据xml中属性, 给view赋值
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemAddMinusLayout);
            //红点是否显示
            int redStarVisibility = typedArray.getInt(R.styleable.ItemAddMinusLayout_iamlRedStarVisibility, VISIBLE);
            //左侧TextView的Text
            String itemName = typedArray.getString(R.styleable.ItemAddMinusLayout_iamlItemName);
            //marginTop, 默认1dp
            int marginTop = typedArray.getDimensionPixelSize(R.styleable.ItemAddMinusLayout_iamlMarginTop, (int) density);
            //最小值
            minValue = typedArray.getInt(R.styleable.ItemAddMinusLayout_iamlMinValue, 0);
            //最大值
            maxValue = typedArray.getInt(R.styleable.ItemAddMinusLayout_iamlMaxValue, 1);
            //默认值
            int nowValue = typedArray.getInt(R.styleable.ItemAddMinusLayout_iamlValue, minValue);
            //已经是最大值了的提示
            hintAlreadyMaxValue = typedArray.getString(R.styleable.ItemAddMinusLayout_iamlAlreadyMaxValueHint);
            //已经是最小值了的提示
            hintAlreadyMinValue = typedArray.getString(R.styleable.ItemAddMinusLayout_iamlAlreadyMinValueHint);
            //主container的最小高度, 默认49dp
            int containerMinHeight = typedArray.getDimensionPixelSize(R.styleable.ItemAddMinusLayout_iamlContainerMinHeight, defaultContainerMinHeight);
            //Item自定义View
            int resourceId = typedArray.getResourceId(R.styleable.ItemAddMinusLayout_iamlCustomLayout, layoutId);
            typedArray.recycle();
            if (minValue > maxValue) { int temp = minValue; minValue = maxValue; maxValue = temp; }
            inflate(context, resourceId, containerMinHeight);
            setMarginTop(marginTop);
            getTextViewRedStar().setVisibility(redStarVisibility);
            getTextViewItem().setText(itemName);
            setValue(nowValue);
        }
    }

    protected void inflate(Context context, @LayoutRes int resource, int containerMinHeight) {
        //给当前空的布局填充内容
        //参3:A view group that will be the parent.
        //传null表示当前布局没有父控件,大部分都传null
        //传this表示已当前相对布局为这个布局的父控件,这样做了以后,当前空的布局就有内容了
        View view = View.inflate(context, resource, this);
        llContainer = view.findViewById(R.id.ll_content_for_iaml);
        spaceMarginTop = view.findViewById(R.id.space_margin_top_for_iaml);
        tvRedStar = view.findViewById(R.id.tv_red_star_for_iaml);
        tvItem = view.findViewById(R.id.tv_item_name_for_iaml);
        tvValue = view.findViewById(R.id.tv_value_for_iaml);
        //如果是wrap_content, 设置最小高度
        llContainer.setMinimumHeight(containerMinHeight);
        tvValue.setText(String.valueOf(nowValue));
        //减
        view.findViewById(R.id.iv_minus_for_iaml).setOnClickListener(this);
        //加
        view.findViewById(R.id.iv_add_for_iaml).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_minus_for_iaml) {  //减
            if (nowValue <= minValue) {
                String hintMim = getAlreadyMinValueHint();
                if (!TextUtils.isEmpty(hintMim)) ToastUtils.showShort(hintMim);
            } else {
                tvValue.setText(String.valueOf(-- nowValue));
            }
        } else if (v.getId() == R.id.iv_add_for_iaml) { //加
            if (nowValue >= maxValue) {
                String hintMax = getAlreadyMaxValueHint();
                if (!TextUtils.isEmpty(hintMax)) ToastUtils.showShort(hintMax);
            } else {
                tvValue.setText(String.valueOf(++ nowValue));
            }
        }
    }

    /**
     * 设置marginTop, 单位px
     */
    public void setMarginTop(@Px int px) {
        ViewGroup.LayoutParams layoutParams = spaceMarginTop.getLayoutParams();
        layoutParams.height = px;
        spaceMarginTop.setLayoutParams(layoutParams);
    }

    /**
     * 提示: 已经是最小了
     * 可在自己strings.xml中自定义: <string name="default_hint_already_min_value">xxx</string>
     */
    protected String getAlreadyMinValueHint() {
        if (hintAlreadyMinValue != null) return hintAlreadyMinValue;
        return getResources().getString(R.string.default_hint_already_min_value);
    }

    /**
     * 提示: 已经是最大了
     * 可在自己strings.xml中自定义: <string name="default_hint_already_max_value">xxx</string>
     */
    protected String getAlreadyMaxValueHint() {
        if (hintAlreadyMaxValue != null) return hintAlreadyMaxValue;
        return getResources().getString(R.string.default_hint_already_max_value);
    }

    public LinearLayout getContainer() {
        return llContainer;
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
        return tvItem;
    }

    /**
     * @return 返回现在的值
     */
    public int getValue() {
        return nowValue;
    }

    public void setMinMaxValue(int minValue, int maxValue) {
        if (minValue > maxValue) return;
        this.minValue = minValue;
        this.maxValue = maxValue;
        setValue(nowValue);
    }

    public void setValue(int value) {
        if (value < minValue) {
            nowValue = minValue;
        } else nowValue = Math.min(value, maxValue);
        tvValue.setText(String.valueOf(nowValue));
    }
}
