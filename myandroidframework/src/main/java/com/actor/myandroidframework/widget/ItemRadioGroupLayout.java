package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatRadioButton;

import com.actor.myandroidframework.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Description: 常用的单选输入布局,这是一个组合控件.
 * Author     : ldf
 * Date       : 2019/7/10 on 17:21
 *
 * 1.左侧红点显示类型, 默认visible
 * @see R.styleable#ItemRadioGroupLayout_irglRedStarVisiable    //visible/invisible/gone
 * 2.左侧提示文字
 * @see R.styleable#ItemRadioGroupLayout_irglItemName           //请选择性别：
 * 3.右侧RadioButton的gravity
 * @see R.styleable#ItemRadioGroupLayout_irglGravity            //start|centerVertical
 * 4.marginTop, 默认1dp
 * @see R.styleable#ItemRadioGroupLayout_irglMarginTop          //1dp
 * 5.多个RadioButton的text, 用','分隔开(5和6这俩个属性, 只需要写一种即可)
 * @see R.styleable#ItemRadioGroupLayout_irglBtns               //"男,女,未知"
 * 6.多个RadioButton的text, 写在:values/arrays.xml里面<string-array name="sexs">
 * @see R.styleable#ItemRadioGroupLayout_irglBtnsArray          //@array/sexs
 * 7.选中第几个, 默认0
 * @see R.styleable#ItemRadioGroupLayout_irglCheckedPosition    //0
 * 8.自定义布局, 注意必须有默认控件的类型和id
 *   如果要所有地方都修改layout,可把{@link R.layout#item_radio_group_layout} copy一份到自己工程作修改, 就会加载自己工程的layout
 * @see R.styleable#ItemRadioGroupLayout_irglCustomLayout       //R.layout.xxx
 *
 * @version 1.0
 * @version 1.1 增加irglGravity属性
 * @version 1.1.1 修改方法: {@link #setCheckedPosition(int)} 增加重复选中监听
 * @version 1.1.2 增加自定义布局, 增加方法:
 *                  @see #setGravity(int)
 *                  @see #setMarginTop(int)
 *
 * @param <T> 填充RadioButton的数据类型, 见{@link #setDatas(Collection)}, 示例:
 *           @BindView(R.id.isl_spinner)
 *           ItemRadioGroupLayout<User> islSpinner;//会填充user的toString()返回值
 */
public class ItemRadioGroupLayout<T> extends LinearLayout {

    private TextView                tvRedStar, tvItem;
    private RadioGroup              radioGroup;
    private LinearLayout            llContentForIrgl;
    private Space                   spaceMarginTop;
    private float                   density;//px = dp * density;
    private OnCheckedChangeListener onCheckedChangeListener;

    public ItemRadioGroupLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ItemRadioGroupLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ItemRadioGroupLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ItemRadioGroupLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        density = getResources().getDisplayMetrics().density;
        //可以自定义重写此布局到自己layout目录
        int layoutId = R.layout.item_radio_group_layout;
        if (attrs == null) {
            inflate(context, layoutId);
        } else {
            //读取自定义属性值
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemRadioGroupLayout);
            //红点是否显示
            int visiable = typedArray.getInt(R.styleable.ItemRadioGroupLayout_irglRedStarVisiable, 0);
            //左侧TextView的Text
            String irglItemName = typedArray.getString(R.styleable.ItemRadioGroupLayout_irglItemName);
            //marginTop, 默认1dp
            int marginTop = typedArray.getDimensionPixelSize(R.styleable.ItemRadioGroupLayout_irglMarginTop, (int) density);
            //几个RadioButton的值
            String texts = typedArray.getString(R.styleable.ItemRadioGroupLayout_irglBtns);
            //如果有数据源, 获取数据源并加载
            CharSequence[] entries = typedArray.getTextArray(R.styleable.ItemRadioGroupLayout_irglBtnsArray);
            //默认选中第几个
            int irglCheckedPosition = typedArray.getInt(R.styleable.ItemRadioGroupLayout_irglCheckedPosition, 0);
            //RadioButton的居中gravity
            int gravity = typedArray.getInt(R.styleable.ItemRadioGroupLayout_irglGravity, Gravity.START | Gravity.CENTER_VERTICAL);
            //Item自定义View
            int resourceId = typedArray.getResourceId(R.styleable.ItemTextInputLayout_itilCustomLayout, layoutId);
            typedArray.recycle();

            inflate(context, resourceId);
            getTextViewRedStar().setVisibility(visiable * INVISIBLE);//设置红点是否显示
            if (irglItemName != null) getTextViewItem().setText(irglItemName);
            setMarginTop(marginTop);
            if (entries != null && entries.length > 0) {
                setDatas(entries);
            } else if (texts != null && !texts.isEmpty()) {
                String[] split = texts.split(",");
                setDatas(split);
            }
            setCheckedPosition(irglCheckedPosition);
            setGravityRadioGroup(gravity);
        }
        //默认白色背景
        if (getBackground() == null) {
            llContentForIrgl.setBackgroundColor(Color.WHITE);
        }
    }

    protected void inflate(Context context, @LayoutRes int resource) {
        //给当前空的布局填充内容
        //参3:A view group that will be the parent.
        //传null表示当前布局没有父控件,大部分都传null
        //传this表示已当前相对布局为这个布局的父控件,这样做了以后,当前空的布局就有内容了
        View inflate = View.inflate(context, resource, this);
        llContentForIrgl = inflate.findViewById(R.id.ll_content_for_irgl);
        spaceMarginTop = inflate.findViewById(R.id.space_margin_top_for_irgl);
        tvRedStar = inflate.findViewById(R.id.tv_red_star_for_irgl);
        tvItem = inflate.findViewById(R.id.tv_item_for_irgl);
        radioGroup = inflate.findViewById(R.id.rg_for_irgl);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (onCheckedChangeListener != null) onCheckedChangeListener.onCheckedChanged(group, checkedId, getCheckedPosition(), false);
            }
        });
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

    public void setDatas(CharSequence[] datas) {
        if (datas != null) {
            //Arrays.asList 返回的List是Arrays的内部类, 没有重写add等方法
            List<CharSequence> list = new ArrayList<>();
            Collections.addAll(list, datas);
            setDatas((Collection<? extends T>) list);
        }
    }

    /**
     * @param gravity 设置RadioGroup的gravity
     */
    public void setGravityRadioGroup(int gravity) {
        getRadioGroup().setGravity(gravity);
    }

    /**
     * 设置数据, 填充RadioGroup
     * @param <T> 如果数据类型 "T" 不是 CharSequence 或 String 或 @StringRes int resid,
     *            重写数据类型的toString()方法即可, RadioButton 填充的时候会调用toString()的内容
     * 注意: 每次填充的T数据类型应该一致
     */
    public void setDatas(Collection<? extends T> datas) {
        radioGroup.removeAllViews();
        if (datas != null && !datas.isEmpty()) {
            for (T data : datas) {
                addRadioButton(data);
            }
        }
    }

    /**
     * 添加一个选项
     */
    public void addRadioButton(T data) {
        AppCompatRadioButton rb = new AppCompatRadioButton(getContext());
        if (data instanceof CharSequence) {
            rb.setText((CharSequence) data);
        } else if (data instanceof Integer) {//@StringRes int resid
            rb.setText((Integer) data);
        } else {
            rb.setText(String.valueOf(data));//toString()
        }
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        radioGroup.addView(rb, radioGroup.getChildCount(), layoutParams);
    }

    /**
     * @return 获取红点
     */
    public TextView getTextViewRedStar() {
        return tvRedStar;
    }

    /**
     * @return 返回Item的TextView
     */
    public TextView getTextViewItem() {
        return tvItem;
    }

    /**
     * @return 返回RadioGroup
     */
    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    /**
     * @param position 设置选中的position
     */
    public void setCheckedPosition(@IntRange(from = 0) int position) {
        if (position < 0 || position >= radioGroup.getChildCount()) return;
        int checkedPosition = getCheckedPosition();
        AppCompatRadioButton child = (AppCompatRadioButton) radioGroup.getChildAt(position);
        child.setChecked(true);
//        radioGroup.check(R.id.rb_for_irgl);//这种方式不行, 会回调多次
        //重复选中
        if (onCheckedChangeListener != null && checkedPosition == position) {
            onCheckedChangeListener.onCheckedChanged(radioGroup, radioGroup.getCheckedRadioButtonId(), position, true);
        }
    }

    /**
     * @return 获取已选中的position, 如果没有, 返回-1
     */
    public int getCheckedPosition() {
        int childCount = radioGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AppCompatRadioButton child = (AppCompatRadioButton) radioGroup.getChildAt(i);
            if (child.isChecked()) return i;
        }
        return -1;
    }

    /**
     * 清空选中
     */
    public void clearCheck() {
        radioGroup.clearCheck();
    }

    /**
     * @param listener 设置选中监听
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        /**
         * @param group radioGroup
         * @param checkedId 选中的Checkbox的id
         * @param position 第几个position
         * @param reChecked 是否是重复选中
         */
        void onCheckedChanged(RadioGroup group, @IdRes int checkedId, int position, boolean reChecked);
    }
}
