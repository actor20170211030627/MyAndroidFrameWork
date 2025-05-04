package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Description: Spinner功能增加 <br />
 *
 * <br />
 * 一. {@link AppCompatSpinner AppCompatSpinner} 属性
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th align="center">№</th>
 *         <th align="center">属性attrs</th>
 *         <th align="center">示例exams</th>
 *         <th align="center">说明docs</th>
 *     </tr>
 *     <tr>
 *         <td>1</td>
 *         <td>{@link androidx.appcompat.R.styleable#Spinner_android_entries entries}</td>
 *         <td>@array/languages</td>
 *         <td>绑定数据源, 写在values/arrays.xml</td>
 *     </tr>
 *     <tr>
 *         <td>2</td>
 *         <td>{@link android.R.attr#spinnerMode spinnerMode}</td>
 *         <td>dropdown|dialog</td>
 *         <td>菜单显示方式:下拉菜单or弹出框(默认dropdown, 在android2.3上没有这个属性)</td>
 *     </tr>
 *     <tr>
 *         <td>3</td>
 *         <td>{@link android.R.attr#dropDownVerticalOffset dropDownVerticalOffset}</td>
 *         <td>25.5dp</td>
 *         <td>spinnerMode=”dropdown”时，下拉的项目选择窗口在垂直方向相对于Spinner窗口的偏移量</td>
 *     </tr>
 *     <tr>
 *         <td>4</td>
 *         <td>{@link android.R.attr#prompt prompt}</td>
 *         <td>@string/please_choose_sex</td>
 *         <td>Dialog标题, 在Dialog模式下才有效, 不能直接写汉字在这里</td>
 *     </tr>
 *     <tr>
 *         <td>5</td>
 *         <td>{@link android.R.attr#popupBackground popupBackground}</td>
 *         <td>@drawable/xxx | @color/xxx</td>
 *         <td>在spinner=”dropdown”时，使用这个属性来设置下拉列表的背景</td>
 *     </tr>
 *     <tr>
 *         <td>6</td>
 *         <td>{@link android.R.attr#gravity gravity}</td>
 *         <td>left|top</td>
 *         <td>对齐方式, 默认Gravity.CENTER, 只适用于 TextView 及其子类</td>
 *     </tr>
 *     <tr>
 *         <td>7</td>
 *         <td>{@link android.R.attr#textAlignment textAlignment}</td>
 *         <td>center|textStart|textEnd|viewStart|viewEnd</td>
 *         <td>对齐方式, API 17 以后才启用</td>
 *     </tr>
 *     <tr>
 *         <td>8</td>
 *         <td>{@link android.R.attr#backgroundTint backgroundTint}</td>
 *         <td>@color/xxx</td>
 *         <td>可间接设置箭头颜色, 至少api14(android 4.0)</td>
 *     </tr>
 *     <tr>
 *         <td>9</td>
 *         <td>{@link android.R.attr#dropDownWidth dropDownWidth}</td>
 *         <td>50dp</td>
 *         <td>在spinnerMode=”dropdown”时，设定下拉框的宽度(一般可不用设置)</td>
 *     </tr>
 *     <tr>
 *         <td>10</td>
 *         <td nowrap="nowrap">{@link android.R.attr#dropDownHorizontalOffset dropDownHorizontalOffset}</td>
 *         <td>1.5dp</td>
 *         <td>spinnerMode=”dropdown”时，下拉的项目选择窗口在水平方向相对于Spinner窗口的偏移量(一般可不用设置)</td>
 *     </tr>
 *     <tr>
 *         <td>11</td>
 *         <td>{@link android.R.attr#dropDownSelector dropDownSelector}</td>
 *         <td nowrap="nowrap">@drawable/xxx</td>
 *         <td>下拉列表选中/未选中的selector(一般可不用设置, 且设置了可能没作用, 见: <a href="https://stackoverflow.com/questions/14737811/spinner-does-not-apply-dropdownselector-attribute" target="_blank">Stack Overflow</a>)</td>
 *     </tr>
 *     <tr>
 *         <td>12</td>
 *         <td>{@link android.R.attr#background background}</td>
 *         <td>@color/shape_drop_down_normal</td>
 *         <td>
 *             设置背景颜色, 带箭头, 可参考: <br />
 *             1.<a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/res/drawable/shape_drop_down_normal.xml" target="_blank">shape_drop_down_normal.xml</a>//背景颜色, 背景颜色渐变等, 包括箭头, 边距, 边框 <br />
 *             2.{@code @color/white} //设置纯色背景, 设置后看不见箭头 <br />
 *             3.{@code @null} //去掉背景包括箭头
 *         </td>
 *     </tr>
 * </table>
 *
 * 二. {@link BaseSpinner BaseSpinner} 自定义属性
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th align="center">№</th>
 *         <th align="center">属性attrs</th>
 *         <th align="center">示例exams</th>
 *         <th align="center">说明docs</th>
 *     </tr>
 *     <tr>
 *         <td>1</td>
 *         <td>{@link R.styleable#BaseSpinner_bsEntriesString bsEntriesString}</td>
 *         <td nowrap="nowrap">@string/names</td>
 *         <td>spinner的填充内容, 用','分隔开(如果已经设置了'entries', 不用再设置这个)</td>
 *     </tr>
 *     <tr>
 *         <td>2</td>
 *         <td>{@link R.styleable#BaseSpinner_bsResource bsResource}</td>
 *         <td>@layout/xxx</td>
 *         <td>spinner填充的布局(根部局是一个TextView), 默认: {@link android.R.layout#simple_spinner_item}, 可参考: <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/res/layout/item_textview_textcolor_white.xml" target="_blank">item_textview_textcolor_white.xml</a></td>
 *     </tr>
 *     <tr>
 *         <td>3</td>
 *         <td nowrap="nowrap">{@link R.styleable#BaseSpinner_bsDropDownViewResource bsDropDownViewResource}</td>
 *         <td>@layout/xxx</td>
 *         <td>下拉列表item布局(根部局是一个TextView), 默认: {@link androidx.appcompat.R.layout#support_simple_spinner_dropdown_item}, 可参考: <a href="https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/res/layout/item_textview_textcolor_red.xml" target="_blank">item_textview_textcolor_red.xml</a></td>
 *     </tr>
 * </table>
 *
 * 三. 主要修复的问题
 * <ol>
 *     <li>填充数据后, 会自动回调 onItemSelected()方法的问题.</li>
 * </ol>
 *
 * @author     : ldf
 * @date       : 2019/10/20 on 16:21
 *
 * @param <T> 填充Item的数据类型, 见{@link #setDatas(Collection)}, 示例:<pre> {@code
 * private BaseSpinner<User> spinner;
 *
 * User = spinner.getSelectedItem();//获取当前已选择的User
 * }</pre>
 */
public class BaseSpinner<T> extends AppCompatSpinner {

    protected int prePosition = Integer.MIN_VALUE;
    //spinner布局
    protected int spinnerRes = android.R.layout.simple_spinner_item;
    //下拉item布局
    protected int ddvr = androidx.appcompat.R.layout.support_simple_spinner_dropdown_item;
    /**
     * 为什么有这个默认Listener?
     * 因为{@link #setDatas(Collection)}设置数据后, 会自动回调{@link OnItemSelectedListener2#onItemSelected(AdapterView, View, int, long)}, 无语...
     */
    protected OnItemSelectedListener2 defaultSelectedListener = new OnItemSelectedListener2() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (isJustSetData) {
                isJustSetData = false;
                return;
            }
            if (onItemSelectedListener2 != null) onItemSelectedListener2.onItemSelected(parent, view, position, id);
        }
        @Override
        public void onItemReSelected(AdapterView<?> parent, View view, int position, long id) {
            if (isJustSetData) {
                isJustSetData = false;
                return;
            }
            if (onItemSelectedListener2 != null) onItemSelectedListener2.onItemReSelected(parent, view, position, id);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if (isJustSetData) {
                isJustSetData = false;
                return;
            }
            if (onItemSelectedListener2 != null) onItemSelectedListener2.onNothingSelected(parent);
        }
    };
    protected OnItemSelectedListener2 onItemSelectedListener2;
    protected boolean                 isJustSetData = false;

    public BaseSpinner(Context context) {
        super(context);
        init(context, null);
    }

    public BaseSpinner(Context context, int mode) {
        super(context, mode);
        init(context, null);
    }

    public BaseSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public BaseSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        init(context, attrs);
    }

    public BaseSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseSpinner);
            spinnerRes = a.getResourceId(R.styleable.BaseSpinner_bsResource, spinnerRes);
            ddvr = a.getResourceId(R.styleable.BaseSpinner_bsDropDownViewResource, ddvr);
            //spinner的列表值, 用","隔开
            String items = a.getString(R.styleable.BaseSpinner_bsEntriesString);
            a.recycle();

            /**
             * 如果设置了{@link R.styleable.Spinner_android_entries}属性,
             * @see AppCompatSpinner(Context, AttributeSet, int, int, Resources.Theme)
             * 会设置ArrayAdapter, 并且使用系统固定的layout布局, 会导致自定义属性失效
             */
            SpinnerAdapter adapter = getAdapter();
            if (adapter instanceof ArrayAdapter) {
                List<T> list = new ArrayList<>();
                for (int i = 0; i < adapter.getCount(); i++) {
                    list.add((T) adapter.getItem(i));
                }
                //是Arrays.asList();返回的List, 没有重写clear()方法, 不能调用clear()方法, 否则报错
//                adapter.clear();
                setArrayAdapter();
                setDatas(list);
            } else {
                setArrayAdapter();
                if (items != null && !items.isEmpty()) {
                    String[] split = items.split(",");
                    setDatas((T[]) split);
                }
            }
        }
        super.setOnItemSelectedListener(defaultSelectedListener);
    }

    /**
     * 设置选中项
     * @param position 第几个item, if重复选中, 会回调 {@link OnItemSelectedListener2#onItemReSelected(AdapterView, View, int, long)}
     */
    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        OnItemSelectedListener2 listener = (OnItemSelectedListener2) getOnItemSelectedListener();
        if (listener == null) return;
        boolean sameSelected = position == prePosition;
        prePosition = position;
        if (sameSelected) {
            listener.onItemReSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    /**
     * 设置选中项
     * @param position 第几个item, if重复选中, 会回调 {@link OnItemSelectedListener2#onItemReSelected(AdapterView, View, int, long)}
     * @param animate 是否展示动画
     */
    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        OnItemSelectedListener2 listener = (OnItemSelectedListener2) getOnItemSelectedListener();
        if (listener == null) return;
        boolean sameSelected = position == prePosition;
        prePosition = position;
        if (sameSelected) {
            listener.onItemReSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    /**
     * final: 不要重写这个方法.
     * @deprecated 重复选中时不会回调, 用这个↓
     * @see #setOnItemSelectedListener(OnItemSelectedListener2)
     */
    @Deprecated
    @Override
    public final void setOnItemSelectedListener(@Nullable OnItemSelectedListener listener) {
//        super.setOnItemSelectedListener(listener);
    }

    /**
     * item选中监听(增加重复选中的监听)
     */
//    @Override
    public void setOnItemSelectedListener(@Nullable OnItemSelectedListener2 listener) {
//        super.setOnItemSelectedListener(defaultSelectedListener);
        this.onItemSelectedListener2 = listener;
    }

    public interface OnItemSelectedListener2 extends OnItemSelectedListener {

        //再次选择了同一个item
        default void onItemReSelected(AdapterView<?> parent, View view, int position, long id/*, boolean fromUser*/) {
        }

        //Adapter为空的时候就会调用到这个方法
        @Override
        default void onNothingSelected(AdapterView<?> parent) {
        }
    }

    //触摸事件
    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        super.setOnTouchListener(onTouchListener);

        /**
         * 重写 onTouchListener 的 onTouch 方法:
         * 返回true: 自己处理触摸事件, 不弹出下拉框
         * 返回false:弹出下拉框
         */
//        public boolean onTouch(View v, MotionEvent event) {}
    }

    /**
     * 设置数据, 填充Spinner
     * @param datas 传入CharSequence[] 或 String[]
     */
    public void setDatas(@Nullable T[] datas) {
        if (datas == null || datas.length == 0) {
            setDatas((Collection<T>) null);
            return;
        }
        //Arrays.asList 返回的List是Arrays的内部类, 没有重写add等方法
        List<T> list = new ArrayList<>();
        Collections.addAll(list, datas);
        setDatas(list);
    }

    /**
     * 设置数据, 填充Spinner <br />
     * T: 如果数据类型 "T" 不是CharSequence或String, 重写数据类型'T'的toString()方法即可, 列表item填充的时候会调用toString()的内容 <br />
     * {@link 注意:} 每次填充的T数据类型应该一致
     */
    public void setDatas(@Nullable Collection<T> datas) {
        SpinnerAdapter adapter = getAdapter();
        //如果不是ArrayAdapter, 那就是你自定义了Adapter, 需要你自己处理.
        if (adapter instanceof ArrayAdapter) {
            ArrayAdapter<T> adapterI = (ArrayAdapter<T>) adapter;
            adapterI.clear();
            prePosition = Integer.MIN_VALUE;
            if (datas != null && !datas.isEmpty()) {
                isJustSetData = true;
                adapterI.addAll(datas);
                prePosition = 0;
            }
        } else {
            LogUtils.errorFormat("adapter = %s, 不是ArrayAdapter, 设置数据后需要你自己处理!", adapter);
        }
    }

    /**
     * @return 获取选中的哪一项
     */
    @Override
    public int getSelectedItemPosition() {
        return super.getSelectedItemPosition();
    }

    /**
     * @return 返回已选中的Item数据, T类型
     */
    @Override
    public T getSelectedItem() {
        return (T) super.getSelectedItem();
    }

    /**
     * @return 返回指定的Item数据, T类型
     */
    @Override
    public T getItemAtPosition(int position) {
        if (getCount() > position) {
            return (T) super.getItemAtPosition(position);
        }
        return null;
    }

    /**
     * 设置默认的Adapter成ArrayAdapter
     */
    public void setArrayAdapter() {
        //不能使用getAdapter()这个方法, 因为如果设置了android:entries, 返回的ArrayList是Arrays的内部类, 没重写add等方法
//        SpinnerAdapter adapter = getAdapter();
        ArrayAdapter<T> arrayAdapter = new ArrayAdapter<>(getContext(), spinnerRes);
        arrayAdapter.setDropDownViewResource(ddvr);
        setAdapter(arrayAdapter);
    }

    /**
     * 设置Adapter, 可自定义
     * @param adapter
     */
    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);
    }
}
