package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.RegexFilter;
import com.actor.myandroidframework.utils.TextUtils2;

/**
 * Description: 常用的Item输入布局,这是一个组合控件. <br/>
 * Author     : ldf <br/>
 * Date       : 2019/7/10 on 17:20 <br/>
 * <br/>
 * 全部属性都是itil开头: <br/>
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th align="center">属性attrs</th>
 *         <th align="center">示例exams</th>
 *         <th align="center">说明docs</th>
 *     </tr>
 *     <tr>
 *         <td nowrap="nowrap">{@link R.styleable#ItemTextInputLayout_itilArrowRightVisiable itilArrowRightVisiable}</td>
 *         <td nowrap="nowrap">visible/invisible/gone</td>
 *         <td>1.右侧箭头显示类型, 默认: 能输入时隐藏, 不能输入时显示</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilDigits itilDigits}</td>
 *         <td>0123456789xX</td>
 *         <td>2.输入限制, 只能输入哪些数字/字母</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilGravity itilGravity}</td>
 *         <td>start|centerVertical</td>
 *         <td>3.右侧输入框文字gravity</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilHint itilHint}</td>
 *         <td>请输入身份证</td>
 *         <td>4.输入框hint</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilImeOptions itilImeOptions}</td>
 *         <td>actionNext(下一步)</td>
 *         <td>5.键盘右下角显示内容</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilInputEnable itilInputEnable}</td>
 *         <td>true</td>
 *         <td>6.是否能输入, 默认true(false的时候,可以当做TextView展示)</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilInputType itilInputType}</td>
 *         <td>text</td>
 *         <td>7.输入类型</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilItemName itilItemName}</td>
 *         <td>请输入身份证：</td>
 *         <td>8.左侧提示文字</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilMarginTop itilMarginTop}</td>
 *         <td>1dp</td>
 *         <td>9.marginTop, 默认1dp</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilMaxLength itilMaxLength}</td>
 *         <td>18</td>
 *         <td>10.最大输入长度</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilRedStarVisiable itilRedStarVisiable}</td>
 *         <td>visible/invisible/gone</td>
 *         <td>11.左侧红点显示类型, 默认visible</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilText itilText}</td>
 *         <td>张三</td>
 *         <td>12.右边EditText的文字</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilPaddingRightText itilPaddingRightText}</td>
 *         <td>0dp</td>
 *         <td>13.右边EditText的PaddingRight(默认: 右侧箭头可见时=0, 不可见时=10dp)</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilArrowRightSrc itilArrowRightSrc}</td>
 *         <td>R.drawable.xxx|color</td>
 *         <td>14.右侧箭头位置图片, 默认箭头</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ItemTextInputLayout_itilCustomLayout itilCustomLayout}</td>
 *         <td>R.layout.xxx</td>
 *         <td>15.自定义布局, 注意必须有默认控件的类型和id, 可参考: <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/tempgen/src/main/res/layout/item_text_input_layout.xml">item_text_input_layout.xml</a></td>
 *     </tr>
 * </table>
 *
 * TODO: 2021/6/1 使用layout的方式, 在页面测试ViewPager+3个Fragment+多个ItemTextInputLayout的过程中, 发现会有et里面数据填充混乱的问题, 感觉可能是编译版本过高, 或者id不再是final等原因, 具体待探索
 */
public class ItemTextInputLayout extends LinearLayout implements TextUtils2.GetTextAble {

    protected          TextView        tvRedStar;
    protected          TextView        tvItem;
    protected          EditText        et1;
    protected          ImageView       ivArrowRight;
    protected          LinearLayout    llContentForItil;
    protected          Space   spaceMarginTop;
    protected static final int         NOTHING = -1;
    //px = dp * density;
    protected float    density;
    protected          OnClickListener clickListener;
    //EditText's hint's color
    protected          ColorStateList  hintTextColors;
    protected @ColorInt int            defaultHintColor;

    public ItemTextInputLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ItemTextInputLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ItemTextInputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ItemTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        density = getResources().getDisplayMetrics().density;
        if (attrs == null) {
            initView(context, NOTHING);
        } else {
            //根据xml中属性, 给view赋值
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemTextInputLayout);
            //左侧红点是否显示
            int redStarVisiable = typedArray.getInt(R.styleable.ItemTextInputLayout_itilRedStarVisiable, VISIBLE);
            //EditText是否能输入
            boolean inputEnable = typedArray.getBoolean(R.styleable.ItemTextInputLayout_itilInputEnable, true);
            //左侧TextView的Text
            String itilItemName = typedArray.getString(R.styleable.ItemTextInputLayout_itilItemName);
            //输入框的Hint
            String itilHint = typedArray.getString(R.styleable.ItemTextInputLayout_itilHint);
            //输入框的Text
            String itilText = typedArray.getString(R.styleable.ItemTextInputLayout_itilText);
            //输入类型(下一步, 完成...)
            int itilImeOptions = typedArray.getInt(R.styleable.ItemTextInputLayout_itilImeOptions, NOTHING);
            //最大输入长度
            int itilMaxLength = typedArray.getInt(R.styleable.ItemTextInputLayout_itilMaxLength, NOTHING);
            //输入框文字gravity
            int gravity = typedArray.getInt(R.styleable.ItemTextInputLayout_itilGravity, Gravity.START | Gravity.CENTER_VERTICAL);
            //marginTop, 默认1dp
            int marginTop = typedArray.getDimensionPixelSize(R.styleable.ItemTextInputLayout_itilMarginTop, dp2px(1));
            //输入类型(text, number...)
            int itilInputType = typedArray.getInt(R.styleable.ItemTextInputLayout_itilInputType, NOTHING);
            //输入限定(例如数字: digits=0123456789)
            String itilDigits = typedArray.getString(R.styleable.ItemTextInputLayout_itilDigits);
            //右侧箭头显示状态
            int arrowRightVisiable = typedArray.getInt(R.styleable.ItemTextInputLayout_itilArrowRightVisiable, NOTHING);
            //EditText的PaddingRight
            int paddingRightText = typedArray.getDimensionPixelSize(R.styleable.ItemTextInputLayout_itilPaddingRightText, Integer.MIN_VALUE);
            //右侧箭头位置图片
            Drawable arrowSrc = typedArray.getDrawable(R.styleable.ItemTextInputLayout_itilArrowRightSrc);
            //Item自定义View
            int resourceId = typedArray.getResourceId(R.styleable.ItemTextInputLayout_itilCustomLayout, NOTHING);
            typedArray.recycle();

            initView(context, resourceId);

            tvRedStar.setVisibility(redStarVisiable * INVISIBLE);
            if (itilInputType != NOTHING) setInputType(itilInputType);
            if (!inputEnable) setInputEnable(false);
            getTextViewItem().setText(itilItemName);

            if (itilText != null) setText(itilText);
            if(itilImeOptions != NOTHING) getEditText().setImeOptions(itilImeOptions);
            if (itilMaxLength >= 0) setMaxLength(itilMaxLength);
            setGravityInput(gravity);
            setMarginTop(marginTop);

            if (!TextUtils.isEmpty(itilDigits)) setDigits(itilDigits, false);
            if (arrowRightVisiable == NOTHING) {
                if (inputEnable) {//如果能输入
                    ivArrowRight.setVisibility(GONE);//隐藏
                } else ivArrowRight.setVisibility(VISIBLE);//显示
            } else ivArrowRight.setVisibility(arrowRightVisiable * INVISIBLE);//根据属性来设置显示状态

            //如果 hint = null
            if (itilHint == null) {
                if (itilItemName != null) {
                    String trim = itilItemName.trim();
                    if (trim.endsWith(":") || trim.endsWith("：")) {//去掉最后:
                        trim = trim.substring(0, trim.length() - 1);
                    }
                    if (inputEnable) {//能输入
                        setHint("请输入".concat(trim));//"请输入" + item名称
                    } else {//不能输入
                        if (ivArrowRight.getVisibility() == VISIBLE) {//右侧箭头显示
                            setHint("请选择".concat(trim));//"请输入请选择 + item名称
                        } else {
                            //不能输入 & 右侧箭头不显示, 就不setHint()
                        }
                    }
                }
            } else setHint(itilHint);

            boolean gone = ivArrowRight.getVisibility() == GONE;
            if (paddingRightText == Integer.MIN_VALUE) paddingRightText = gone ? dp2px(10) : dp2px(5);
            setPaddingRightText(paddingRightText);
            if (arrowSrc != null) setIvArrowRight(arrowSrc, null, null);
        }
        //默认白色背景
        if (getBackground() == null) {
            llContentForItil.setBackgroundColor(Color.WHITE);
        }
    }

    protected void initView(Context context, @LayoutRes int resource) {
        if (resource == NOTHING) {
            //最小高度
            setMinimumHeight(dp2px(50));
            //垂直
            setOrientation(VERTICAL);
            //Space
            spaceMarginTop = new Space(context);
            spaceMarginTop.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(1)));
            addView(spaceMarginTop);
            //Container
            llContentForItil = new LinearLayout(context);
            llContentForItil.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1F));
            llContentForItil.setGravity(Gravity.CENTER_VERTICAL);
            llContentForItil.setOrientation(HORIZONTAL);
            llContentForItil.setPadding(dp2px(10), 0, 0, 0);
            addView(llContentForItil);
            //RedStart
            tvRedStar = new TextView(context);
            tvRedStar.setText("* ");
            tvRedStar.setTextColor(context.getResources().getColor(R.color.red_D90000));
            tvRedStar.setTextSize(20);
            llContentForItil.addView(tvRedStar);
            //TextView(Item)
            tvItem = new TextView(context);
            tvItem.setMinWidth(dp2px(90));
            tvItem.setTextColor(getResources().getColor(R.color.gray_666));
            tvItem.setTextSize(15);
            llContentForItil.addView(tvItem);
            //EditText
            et1 = new EditText(context);
            et1.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1F));
            et1.setBackground(null);
            et1.setMinHeight(dp2px(40));
            et1.setTextColor(getResources().getColor(R.color.gray_999));
            et1.setTextSize(15);
            llContentForItil.addView(et1);
            //IvArrow
            ivArrowRight = new ImageView(context);
            LayoutParams ivLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp2px(25));
            ivLayoutParams.setMarginEnd(dp2px(10));
            ivArrowRight.setLayoutParams(ivLayoutParams);
            ivArrowRight.setAdjustViewBounds(true);
            ivArrowRight.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivArrowRight.setImageResource(R.drawable.arrow_right_gray_dcdcdc);
            llContentForItil.addView(ivArrowRight);
        } else {
            //从Xml布局中填充View, 并找到子view
            View inflate = inflate(context, resource, this);
            llContentForItil = inflate.findViewById(R.id.ll_content_for_itil);
            spaceMarginTop = inflate.findViewById(R.id.space_margin_top_for_itil);
            tvRedStar = inflate.findViewById(R.id.tv_red_star_for_itil);
            tvItem = inflate.findViewById(R.id.tv_item_name_for_itil);
            et1 = inflate.findViewById(R.id.et_input_for_itil);
            ivArrowRight = inflate.findViewById(R.id.iv_arrow_right_for_itil);
        }
        //如果xml中设置了android:onClick, 则clickListener不为空
        if (clickListener != null) setOnClickListener(clickListener);
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
     * @return 获取输入框
     */
    @Override
    public EditText getEditText() {
        return et1;
    }

    /**
     * @return 获取右侧箭头
     */
    public ImageView getIvArrowRight() {
        return ivArrowRight;
    }

    @Override
    public Editable getText(){
        return getEditText().getText();
    }

    public void setText(CharSequence text) {
        getEditText().setText(text);
    }

    /**
     * @param maxLength 设置输入最大长度
     */
    public void setMaxLength(@IntRange(from = 0) int maxLength) {
        getEditText().setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
    }

    /**
     * 设置输入类型
     */
    public void setInputType(int inputType) {
        getEditText().setInputType(inputType);
    }

    /**
     * 设置输入限制
     * @param digits 示例只能输入数字: <string name="digits_number">0123456789</string>, 传入: R.string.digits_number
     * @param reFilter 是否重新对已经输入的内容按照digits过滤一次
     */
    public void setDigits(@StringRes int digits, boolean reFilter) {
        setDigits(getContext().getResources().getString(digits), reFilter);
    }

    /**
     * 设置输入限制
     * @param digits 示例只能输入数字: "0123456789"
     * @param reFilter 是否重新对已经输入的内容按照digits过滤一次
     */
    public void setDigits(String digits, boolean reFilter) {
        if (digits == null) return;
        String regex = "[" + digits + "]+";//正则, 只能输入这些内容, 例: [0123456789]+, 或 [a-zA-Z0-9]+ 等
        //setFilters&setKeyListener会都起作用, 为避免引起bug, 统一使用setFilters方式
//        getEditText().setKeyListener(DigitsKeyListener.getInstance(digits));//设置输入限制
        setDigitsRegex(regex, reFilter);
    }

    /**
     * 设置输入限制, 通过正则匹配判断方式
     * @param regex strings.xml中的资源, 示例: <string name="regex_text">[a-zA-Z0-9\u4E00-\u9FA5]+</string>
     * @param reFilter 是否重新对已经输入的内容按照digits过滤一次
     */
    public void setDigitsRegex(@StringRes int regex, boolean reFilter) {
        setDigitsRegex(getContext().getResources().getString(regex), reFilter);
    }
    /**
     * 设置输入限制, 通过正则匹配判断方式
     * @param regex 输入限制的正则, 示例只能输入数字: [0-9]+
     * @param reFilter 是否重新对已经输入的内容按照digits过滤一次
     */
    public void setDigitsRegex(String regex, boolean reFilter) {
        if (regex == null) return;
        InputFilter[] filters = getEditText().getFilters();//获取所有filter
        boolean hasRegexFilter = false;
        RegexFilter regexFilter = null;
        for (InputFilter filter : filters) {
            if (filter instanceof RegexFilter) {//如果有就替换RegexFilter中的regex
                regexFilter = (RegexFilter) filter;
                regexFilter.setRegex(regex);
                hasRegexFilter = true;
                break;
            }
        }
        if (!hasRegexFilter) {//如果没有RegexFilter, 就增加一个
            InputFilter[] newFilters = new InputFilter[filters.length + 1];
            System.arraycopy(filters, 0, newFilters, 0, filters.length);
            regexFilter = new RegexFilter(regex);
            newFilters[filters.length] = regexFilter;
            getEditText().setFilters(newFilters);
        }
        if (reFilter) filter(regexFilter);
    }

    //根据正则, 对已经输入的内容进行过滤
    protected void filter(RegexFilter regexFilter) {
        EditText editText = getEditText();
        Editable text = editText.getText();
        CharSequence result = regexFilter.filterCharSequence(text);
        if (!TextUtils.equals(text, result)) {
            setText(result);
            //光标移动到最后
            editText.setSelection(result.length());
        }
    }

    @Override
    public CharSequence getHint(){
        return getEditText().getHint();
    }

    public void setHint(CharSequence hilt){
        getEditText().setHint(hilt);
    }

    /**
     * 设置textHint的颜色
     * @param color getResources().getColor(R.color.xxx)
     */
    public void setHintTextColor(@ColorInt int color) {
        EditText editText = getEditText();
        editText.setHintTextColor(color);
        hintTextColors = editText.getHintTextColors();
        defaultHintColor = hintTextColors.getDefaultColor();
    }

    /**
     * 设置是否可输入(false的时候,可以当做TextView展示)
     * @param enable
     */

    public void setInputEnable(boolean enable) {
        EditText editText = getEditText();
//        setInputType(enable ? inputType, EditorInfo.TYPE_NULL);
//        editText.setEnabled(enable);//这样不能编辑,可用于隐藏输入法,但是EditText的点击事件无反应,不能做点击事件
        //要设置focusable, 否则点击事件要第2次才有反应
        editText.setFocusable(enable);
        editText.setClickable(!enable);
        editText.setLongClickable(enable);//长按显示粘贴
        editText.setFocusableInTouchMode(enable);
//        if (enable) editText.requestFocus();//把光标移动到这一个et1,但是不弹出键盘
        if (hintTextColors == null) {
            hintTextColors = editText.getHintTextColors();
            defaultHintColor = hintTextColors.getDefaultColor();
        }
        if (enable) {
            editText.setHintTextColor(hintTextColors);
        } else {
            //点击时, 按下状态不变色
            editText.setHintTextColor(defaultHintColor);
        }
        //点击时, 按下的瞬间不显示光标
        editText.setCursorVisible(enable);
    }

    /**
     * 设置marginTop, 单位dp
     */
    public void setMarginTopDp(int dp) {
        setMarginTop(dp2px(dp));
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
     * 右边EditText的PaddingRight, 单位px(默认: 右侧箭头可见时=0, 不可见时=10dp)
     */
    public void setPaddingRightText(@Px int px) {
        et1.setPadding(0, 0, px, 0);
    }

    /**
     * @param gravity 设置输入框文字gravity
     */
    public void setGravityInput(int gravity) {
        getEditText().setGravity(gravity);
    }

    public void setIvArrowRight(@DrawableRes int drawableRes, @Nullable Integer widthDp, @Nullable Integer heightDp) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        setIvArrowRight(drawable, widthDp, heightDp);
    }

    /**
     * @param drawable 设置最右侧图片drawable
     * @param widthDp 最右侧图片宽度
     * @param heightDp 最右侧图片高度
     */
    public void setIvArrowRight(Drawable drawable, @Nullable Integer widthDp, @Nullable Integer heightDp) {
        ImageView ivArrowRight = getIvArrowRight();
        ivArrowRight.setImageDrawable(drawable);
        if (widthDp != null || heightDp != null) {
            ViewGroup.LayoutParams layoutParams = ivArrowRight.getLayoutParams();
            if (widthDp != null) layoutParams.width = dp2px(widthDp);
            if (heightDp != null) layoutParams.height = dp2px(heightDp);
            ivArrowRight.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置是否可点击
     */
    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        //因为attr是先走的super(context, attrs), 这时候EditText还没开始初始化
        EditText editText = getEditText();
        if (editText != null) editText.setClickable(clickable);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        super.setOnClickListener(onClickListener);
        this.clickListener = onClickListener;
        /**
         * 因为attr是先走的super(context, attrs), 这时候EditText还没开始初始化
         * 必须要设置,否则点击EditText无效
         */
        EditText editText = getEditText();
        if (editText != null) editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null && getEditText().isClickable()) {
                    clickListener.onClick(ItemTextInputLayout.this);
                }
            }
        });
    }

    protected int dp2px(int dp) {
        return (int) (density * dp  + 0.5F);
    }
}
