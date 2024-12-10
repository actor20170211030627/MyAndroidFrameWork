package com.actor.myandroidframework.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.List;

/**
 * description: RadioGroup简单封装
 * company    :
 *
 * @author : ldf
 * date       : 2022/6/4 on 16
 * @version 1.0
 *
 * @param <T> 填充Item的数据类型, 见{@link #setDatas(List)}, 示例:<pre> {@code
 * private BaseRadioGroup<User> radioGroup;
 *
 * }</pre>
 */
public class BaseRadioGroup<T> extends RadioGroup {

    protected OnCheckedChangeListener2 checkedChangeListener2;
    protected OnCheckedChangeListener checkedChangeListener;

    public BaseRadioGroup(Context context) {
        super(context);
        init(context, null);
    }

    public BaseRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        /**
         * 如果<RadioButton 在XML中设置了android:checked="true",
         * 但是<RadioButton 没有设置android:id="xxx"的话, 这个button会一直选中, 不能取消选中...
         * 这么多年了, 这个bug也从来不改...
         */
        int childCount = getChildCount();
//        //最后一个选中的孩子的id(系统默认选中最后一个)
        int lastCheckedId = View.NO_ID;
//        //遍历所有child, 将所有已选中的child设为false
        for (int i = 0; i < childCount; i++) {
            RadioButton child = getChildAt(i);
            if (child != null && child.isChecked()) {
                //1,2,3...
                lastCheckedId = child.getId();
                child.setChecked(false);
            }
        }
//        //如果有默认选中的孩子
        if (lastCheckedId != View.NO_ID) {
            clearCheck();
            check(lastCheckedId);
        }
    }

    /**
     * @param datas 设置数据, 填充RadioGroup <br />
     *              &lt;T> 如果数据类型 "T" 不是 CharSequence 或 String 或 @StringRes int resid,
     *              重写数据类型的toString()方法即可, RadioButton 填充的时候会调用toString()的内容 <br />
     * 注意: 每次填充的T数据类型应该一致
     */
    public void setDatas(List<T> datas) {
        if (datas != null && !datas.isEmpty()) {
            //孩子个数
            int childCount = getChildCount();
            int dataSize = datas.size();
            //如果有孩子
            if (childCount > 0) {
                int min = Math.min(childCount, dataSize);
                //将已有孩子重写设置值
                for (int i = 0; i < min; i++) {
                    T data = datas.get(i);
                    setRadioButtonTextAt(i, data);
                }
                if (childCount == dataSize) return;
                //如果还有数据没有填充
                if (childCount < dataSize) {
                    for (int i = min; i < dataSize; i++) {
                        T data = datas.get(i);
                        addRadioButton(data);
                    }
                } else {
                    //数据已经填充完, 有多余的child, 移除多余的child
                    int moreChildCount = childCount - dataSize;
                    for (int i = 0; i < moreChildCount; i++) {
                        removeViewAt(--childCount);
                    }
                }
            } else {
                //如果没孩子
                for (T data : datas) {
                    addRadioButton(data);
                }
            }
        } else {
            removeAllViews();
        }
    }

    /**
     * 添加一个选项
     */
    public void addRadioButton(T data) {
        RadioButton rb = new AppCompatRadioButton(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(rb, getChildCount(), layoutParams);
        setRadioButtonTextAt(getChildCount() - 1, data);
    }

    /**
     * 设置某一个 RadioButton 的值
     * @param position 第几个 RadioButton
     * @param data 值
     */
    public void setRadioButtonTextAt(@IntRange(from = 0) int position, T data) {
        RadioButton child = getChildAt(position);
        if (child == null) return;
        if (data instanceof CharSequence) {
            child.setText((CharSequence) data);
        } else if (data instanceof Integer) {//@StringRes int resid
            child.setText((Integer) data);
        } else {
            child.setText(String.valueOf(data));//toString()
        }
    }

    /**
     * @param position 设置选中的position
     */
    public void setCheckedPosition(@IntRange(from = 0) int position) {
        RadioButton child = getChildAt(position);
        if (child == null) return;
        int checkedPosition = getCheckedPosition();
        child.setChecked(true);
//        radioGroup.check(R.id.rb_for_irgl);//这种方式不行, 会回调多次
        //重复选中
        if (checkedChangeListener2 != null && checkedPosition == position) {
            checkedChangeListener2.onCheckedChanged(this, getCheckedRadioButtonId(), position, true);
        }
    }

    /**
     * @return 获取已选中的position, 如果没有, 返回-1
     */
    public int getCheckedPosition() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioButton child = getChildAt(i);
            if (child != null && child.isChecked()) return i;
        }
        return View.NO_ID;
    }

    @Nullable
    @Override
    public RadioButton getChildAt(int index) {
        return (RadioButton) super.getChildAt(index);
    }

    /**
     * 清空选中
     */
    @Override
    public void clearCheck() {
        super.clearCheck();
    }

    /**
     * @param checkChangeListener2 设置选中改变监听
     */
    public void setOnCheckedChangeListener2(OnCheckedChangeListener2 checkChangeListener2) {
        this.checkedChangeListener2 = checkChangeListener2;
        if (checkedChangeListener == null) {
            checkedChangeListener = new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedChangeListener2 != null) {
                        checkedChangeListener2.onCheckedChanged(group, checkedId, getCheckedPosition(), false);
                    }
                }
            };
        }
        super.setOnCheckedChangeListener(checkedChangeListener);
    }

    /**
     * RadioGroup 的设置监听
     * @param listener 监听
     * @deprecated 使用 {@link #setOnCheckedChangeListener2(OnCheckedChangeListener2)}
     */
    @Deprecated
    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        super.setOnCheckedChangeListener(listener);
    }

    public interface OnCheckedChangeListener2 {
        /**
         * @param group radioGroup
         * @param checkedId 选中的Checkbox的id
         * @param position 第几个position
         * @param reChecked 是否是重复选中
         */
        void onCheckedChanged(RadioGroup group, @IdRes int checkedId, int position, boolean reChecked);
    }
}
