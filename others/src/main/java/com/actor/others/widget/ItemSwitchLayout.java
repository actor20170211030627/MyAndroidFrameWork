package com.actor.others.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;

import com.actor.others.R;

/**
 * Description: 常用的Switch布局,这是一个组合控件. <br/>
 * Author     : ldf <br/>
 * Date       : 2019/7/10 on 17:21 <br/>
 * <br/>
 *
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th align="center">属性attrs</th>
 *         <th align="center">示例exams</th>
 *         <th align="center">说明docs</th>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemSwitchLayout_isclRedStarVisibility isclRedStarVisibility}</td>
 *         <td nowrap="nowrap">visible/invisible/gone</td>
 *         <td>1.左侧红点显示类型, 默认visible</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemSwitchLayout_isclItemName isclItemName}</td>
 *         <td nowrap="nowrap">请选择性别：</td>
 *         <td>2.左侧提示文字</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemSwitchLayout_isclChecked isclChecked}</td>
 *         <td nowrap="nowrap">true|false</td>
 *         <td>3.Switch 是否选中</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemSwitchLayout_isclMarginTop isclMarginTop}</td>
 *         <td nowrap="nowrap">1dp</td>
 *         <td>4.marginTop, 默认1dp</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemSwitchLayout_isclContainerMinHeight isclContainerMinHeight}</td>
 *         <td>49dp</td>
 *         <td>5.主container的最小高度, 默认49dp(目前还未扩展, 设置了也没啥用...)</td>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemSwitchLayout_isclCustomLayout isclCustomLayout}</td>
 *         <td nowrap="nowrap">R.layout.xxx</td>
 *         <td>6.自定义布局, 注意必须有默认控件的类型和id. 如果要所有地方都修改layout,可把{@link R.layout#item_switch_layout item_switch_layout} copy一份到自己工程作修改, 就会加载自己工程的layout</td>
 *     </tr>
 * </table>
 * <br />
 */
public class ItemSwitchLayout extends LinearLayout {

    protected TextView     tvRedStar, tvItem;
    protected SwitchCompat switchCompat;
    protected LinearLayout llContentForIrgl;
    protected Space        spaceMarginTop;
    //px = dp * density;
    protected float        density;

    public ItemSwitchLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ItemSwitchLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ItemSwitchLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ItemSwitchLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        density = getResources().getDisplayMetrics().density;
        //可以自定义重写此布局到自己layout目录
        int layoutId = R.layout.item_switch_layout;
        int defaultContainerMinHeight = (int) getResources().getDimension(R.dimen.item_radio_group_layout_container_min_height);
        if (attrs == null) {
            inflate(context, layoutId, defaultContainerMinHeight);
        } else {
            //读取自定义属性值
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemSwitchLayout);
            //红点是否显示
            int visible = typedArray.getInt(R.styleable.ItemSwitchLayout_isclRedStarVisibility, 0);
            //左侧TextView的Text
            String irglItemName = typedArray.getString(R.styleable.ItemSwitchLayout_isclItemName);
            //marginTop, 默认1dp
            int marginTop = typedArray.getDimensionPixelSize(R.styleable.ItemSwitchLayout_isclMarginTop, (int) density);
            //是否选中
            boolean checked = typedArray.getBoolean(R.styleable.ItemSwitchLayout_isclChecked, false);
            //主container的最小高度, 默认49dp
            int containerMinHeight = typedArray.getDimensionPixelSize(R.styleable.ItemSwitchLayout_isclContainerMinHeight, defaultContainerMinHeight);
            //Item自定义View
            int resourceId = typedArray.getResourceId(R.styleable.ItemSwitchLayout_isclCustomLayout, layoutId);
            typedArray.recycle();

            inflate(context, resourceId, containerMinHeight);
            getTextViewRedStar().setVisibility(visible * INVISIBLE);//设置红点是否显示
            if (irglItemName != null) getTextViewItem().setText(irglItemName);
            setMarginTop(marginTop);
            setChecked(checked);
        }
        //默认白色背景
        if (getBackground() == null) {
            llContentForIrgl.setBackgroundColor(Color.WHITE);
        }
    }

    protected void inflate(Context context, @LayoutRes int resource, int containerMinHeight) {
        //给当前空的布局填充内容
        //参3:A view group that will be the parent.
        //传null表示当前布局没有父控件,大部分都传null
        //传this表示已当前相对布局为这个布局的父控件,这样做了以后,当前空的布局就有内容了
        View inflate = View.inflate(context, resource, this);
        llContentForIrgl = inflate.findViewById(R.id.ll_content_for_irgl);
        spaceMarginTop = inflate.findViewById(R.id.space_margin_top_for_irgl);
        tvRedStar = inflate.findViewById(R.id.tv_red_star_for_irgl);
        tvItem = inflate.findViewById(R.id.tv_item_for_irgl);
        switchCompat = inflate.findViewById(R.id.switch_for_isl);
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
     * @return 返回Switch
     */
    public SwitchCompat getSwitch() {
        return switchCompat;
    }

    /**
     * @param isChecked 是否选中
     */
    public void setChecked(boolean isChecked) {
        switchCompat.setChecked(isChecked);
    }

    /**
     * @return 获取是否选中
     */
    public boolean isChecked() {
        return switchCompat.isChecked();
    }

    /**
     * @param listener 设置选中监听
     */
    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        switchCompat.setOnCheckedChangeListener(listener);
    }
}
