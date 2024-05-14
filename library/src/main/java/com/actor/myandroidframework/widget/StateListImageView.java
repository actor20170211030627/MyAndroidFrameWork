package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.actor.myandroidframework.R;

/**
 * description: 可以直接在布局文件中, 设置各种状态的ImageView <br />
 * see {@link com.google.android.material.imageview.ShapeableImageView} 可以设置各种形状
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *    <tr>
 *        <th>№</th>
 *        <td align="center">属性attrs</td>
 *        <td align="center">示例exams</td>
 *        <td align="center">说明docs</td>
 *    </tr>
 *    <tr>
 *        <td>1</td>
 *        <td>{@link R.styleable#StateListImageView_slivSrcDisabled slivSrcDisabled}</td>
 *        <td>@drawable/xxx</td>
 *        <td>{@link #setEnabled(boolean) setEnabled(false)} 的时候显示的图片</td>
 *    </tr>
 *    <tr>
 *        <td>2</td>
 *        <td>{@link R.styleable#StateListImageView_slivSrcFocused slivSrcFocused}</td>
 *        <td>@drawable/xxx</td>
 *        <td>{@link #requestFocus()} 获取焦点的时候显示的图片</td>
 *    </tr>
 *    <tr>
 *        <td>3</td>
 *        <td>{@link R.styleable#StateListImageView_slivSrcPressed slivSrcPressed}</td>
 *        <td>@drawable/xxx</td>
 *        <td>{@link #setPressed(boolean) setPressed(true)} 的时候显示的图片</td>
 *    </tr>
 *    <tr>
 *        <td>4</td>
 *        <td>{@link R.styleable#StateListImageView_slivSrcSelected slivSrcSelected}</td>
 *        <td>@drawable/xxx</td>
 *        <td>{@link #setSelected(boolean) setSelected(true)} 的时候显示的图片</td>
 *    </tr>
 *    <tr>
 *        <td>5</td>
 *        <td>{@link android.R.styleable#ImageView_src android:src}</td>
 *        <td>@drawable/xxx</td>
 *        <td>默认显示的图片</td>
 *    </tr>
 * </table>
 *
 * @author : ldf
 * @date       : 2024/3/20 on 17
 */
public class StateListImageView extends AppCompatImageView {

    /**
     * @see com.hjq.shape.other.ExtendStateListDrawable
     */
    protected StateListDrawable stateListDrawable = new StateListDrawable();

    protected static final int[] STATE_DEFAULT = new int[]{};
    protected static final int[] STATE_PRESSED = new int[]{android.R.attr.state_pressed};
    protected static final int[] STATE_CHECKED = new int[]{android.R.attr.state_checked};
    protected static final int[] STATE_DISABLED = new int[]{-android.R.attr.state_enabled};
    protected static final int[] STATE_FOCUSED = new int[]{android.R.attr.state_focused};
    protected static final int[] STATE_SELECTED = new int[]{android.R.attr.state_selected};

    public StateListImageView(@NonNull Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public StateListImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public StateListImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    protected void initAttributeSet(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateListImageView);
            //ImageView没有 setChecked()方法
//            Drawable checkedDrawable = a.getDrawable(R.styleable.StateListImageView_slivSrcChecked);
            Drawable disabledDrawable = a.getDrawable(R.styleable.StateListImageView_slivSrcDisabled);
            Drawable focusedDrawable = a.getDrawable(R.styleable.StateListImageView_slivSrcFocused);
            Drawable pressedDrawable = a.getDrawable(R.styleable.StateListImageView_slivSrcPressed);
            Drawable selectedDrawable = a.getDrawable(R.styleable.StateListImageView_slivSrcSelected);
            a.recycle();

            //ImageView没有 setChecked()方法
//            stateListDrawable.addState(STATE_CHECKED, checkedDrawable);
            stateListDrawable.addState(STATE_DISABLED, disabledDrawable);
            //if有焦点图, 设置能获取焦点
            if (focusedDrawable != null) {
                setFocusable(true);
                setFocusableInTouchMode(true);
            }
            stateListDrawable.addState(STATE_FOCUSED, focusedDrawable);
            stateListDrawable.addState(STATE_PRESSED, pressedDrawable);
            stateListDrawable.addState(STATE_SELECTED, selectedDrawable);
        }
        stateListDrawable.addState(STATE_DEFAULT, getDrawable());
        setImageDrawable(stateListDrawable);
    }

    /**
     * 设置Enable, 如果enable=false, 那么其余几个状态属性设置无效
     * @param enabled True if this view is enabled, false otherwise.
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    /**
     * 是否有焦点, 如果focus=true, 那么除了setEnable()有效, 设置另外的pressed, selected这些属性无效
     */
    @Override
    public boolean hasFocus() {
        return super.hasFocus();
    }

    /**
     * 请求获取焦点
     */
//    @Override
//    public final boolean requestFocus() {
//        return super.requestFocus();
//    }

    /**
     * 清除焦点
     */
    @Override
    public void clearFocus() {
        super.clearFocus();
    }

    /**
     * 设置是否按下状态 (需要代码设置按下状态, 手动按下没效果!)
     * @param pressed Pass true to set the View's internal state to "pressed", or false to reverts
     *        the View's internal state from a previously set "pressed" state.
     */
    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
    }

    /**
     * 设置选中状态
     * @param selected true if the view must be selected, false otherwise
     */
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }


    public StateListDrawable getStateListDrawable() {
        return stateListDrawable;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //在RecyclerView加载过程中, 会频繁调用
//        stateListDrawable.clearColorFilter();
//        stateListDrawable = null;
    }
}
