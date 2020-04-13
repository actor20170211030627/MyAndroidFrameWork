package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.actor.myandroidframework.R;

import java.util.Arrays;
import java.util.List;

/**
 * Description: 常用的Spinner选择布局,这是一个组合控件.
 *              注意:本控件的item数据类型只有String, 没有做其余复杂扩展.
 * Author     : 李大发
 * Date       : 2019/7/10 on 17:22
 *
 * 全部属性都是isl开头:
 * 1.左侧红点显示类型, 默认visible
 * @see R.styleable#ItemSpinnerLayout_islRedStarVisiable //visible/invisible/gone
 * 2.左侧提示文字
 * @see R.styleable#ItemSpinnerLayout_islItemName        //请选择语言：
 * 3.右侧spinner的填充内容, 写在:values/arrays.xml里面<string-array name="">
 * @see R.styleable#ItemSpinnerLayout_islEntries         //@array/xxx
 * 4.marginTop, 默认1dp
 * @see R.styleable#ItemSpinnerLayout_islMarginTop       //1dp
 * 5.自定义布局, 注意必须有默认控件的类型和id
 * @see R.styleable#ItemSpinnerLayout_islCustomLayout    //R.layout.xxx
 *
 * @version 1.0
 * @version 1.0.1 自定义属性, 增加方法:
 *                  @see #setMarginTop(int)
 */
public class ItemSpinnerLayout extends LinearLayout {

    private TextView         tvRedStar, tvItem;
    private AppCompatSpinner spinner;
    private Space            spaceMarginTop;
    private float            density;//px = dp * density;

    public ItemSpinnerLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ItemSpinnerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ItemSpinnerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ItemSpinnerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        density = getResources().getDisplayMetrics().density;
        int layoutId = R.layout.item_spinner_layout;
        if (attrs == null) {
            inflate(context, layoutId);
        } else {
            //读取自定义属性值
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemSpinnerLayout);
            //左侧红点是否显示
            int redStarVisiable = typedArray.getInt(R.styleable.ItemSpinnerLayout_islRedStarVisiable, 0);
            //左侧TextView的值
            String itemName = typedArray.getString(R.styleable.ItemSpinnerLayout_islItemName);
            //marginTop, 默认1dp
            int marginTop = typedArray.getDimensionPixelSize(R.styleable.ItemSpinnerLayout_islMarginTop, (int) density);
            //如果有数据源, 获取数据源并加载
            CharSequence[] entries = typedArray.getTextArray(R.styleable.ItemSpinnerLayout_islEntries);
//        int resourceId = typedArray.getResourceId(R.styleable.ItemSpinnerLayout_islEntries, 0);
//        if (resourceId != 0) {
//            String[] entries = context.getResources().getStringArray(resourceId);
//            setDatas(entries);
//        }
            //Item自定义View
            int resourceId = typedArray.getResourceId(R.styleable.ItemSpinnerLayout_islCustomLayout, layoutId);
            typedArray.recycle();

            inflate(context, resourceId);
            getTextViewRedStar().setVisibility(redStarVisiable * 4);//设置红点是否显示
            if (itemName != null) getTextViewItem().setText(itemName);
            setMarginTop(marginTop);
            if (entries != null) setDatas(entries);
        }
    }
    protected void inflate(Context context, @LayoutRes int resource) {
        View view = View.inflate(context, R.layout.item_spinner_layout, this);
        spaceMarginTop = view.findViewById(R.id.space_margin_top_for_isl);
        tvRedStar = view.findViewById(R.id.tv_red_star_for_isl);
        tvItem = view.findViewById(R.id.tv_item_for_isl);
        spinner = view.findViewById(R.id.spinner_for_isl);
    }

    /**
     * 获取红点
     *
     * @return
     */
    public TextView getTextViewRedStar() {
        return tvRedStar;
    }

    /**
     * 返回Item的TextView
     *
     * @return
     */
    public TextView getTextViewItem() {
        return tvItem;
    }

    /**
     * 设置marginTop, 单位dp
     */
    public void setMarginTopDp(int dp) {
        setMarginTop((int) (dp * density + 0.5));
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
     * 设置数据, 填充Spinner
     * @param datas 传入CharSequence[] 或 String[]
     */
    public void setDatas(CharSequence[] datas) {
        if (datas != null) {
            List<CharSequence> list = Arrays.asList(datas);
            setDatasCharsequence(list);
        }
    }

    /**
     * 设置数据, 填充Spinner
     */
    public void setDatasCharsequence(List<CharSequence> datas) {
        if (datas != null) {
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, datas);
            adapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    /**
     * 设置数据, 填充Spinner
     */
    public void setDatas(List<String> datas) {
        if (datas != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, datas);
            adapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    /**
     * 直接返回该条目的值.(item只有TextView的时候有效,否则返回该item的地址值)
     * @return
     */
    public String getSelectedItemText() {
        if (spinner != null && spinner.getSelectedItem() != null) {
            return spinner.getSelectedItem().toString();
        } else return null;//adapter = null的时候,会空指针
    }

    /**
     * 获取选中的哪一项
     * 注意:如果adapter = null,返回-1
     * @return
     */
    public int getSelectedItemPosition() {
        return spinner.getSelectedItemPosition();
    }

    /**
     * 选中某一项
     * @param position 必须 >= 0
     */
    public void setSelectedItemPosition(@IntRange(from = 0) int position) {
        if (position >= 0 && position < spinner.getCount()) {
            spinner.setSelection(position);
        }
    }

    /**
     * 判断是否选择的最后一个Item
     */
    public boolean isSelectedLastPosition() {
        return getSelectedItemPosition() == spinner.getCount() - 1;
    }
}
