package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.actor.myandroidframework.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Description: Spinner功能增加
 * Author     : ldf
 * Date       : 2019/10/20 on 16:21
 *
 * 属性:
 * 1.绑定数据源, 写在values/arrays.xml
 *   android:entries="@array/languages"
 *
 * 2.菜单显示方式:下拉菜单or弹出框(默认弹出框, 在android2.3上没有这个属性)
 *   android:spinnerMode="dropdown|dialog"
 *
 * 3.spinnerMode=”dropdown”时，下拉的项目选择窗口在垂直方向相对于Spinner窗口的偏移量
 *   android:dropDownVerticalOffset="25.5dp"
 *
 * 4.Dialog标题, 在Dialog模式下才有效, 不能直接写汉字在这里
 *   android:prompt="@string/please_choose_sex"
 *
 * 5.在spinner=”dropdown”时，使用这个属性来设置下拉列表的背景
 *   android:popupBackground="@drawable/xxx | @color/xxx"
 *
 * 6.对齐方式, 默认Gravity.CENTER, 只适用于 TextView 及其子类
 *   android:gravity="left|top"
 *
 * 7.对齐方式, API 17 以后才启用
 *   android:textAlignment="center|textStart|textEnd|viewStart|viewEnd"
 *
 * 8.可间接设置箭头颜色, 至少api14(android 4.0)
 *   android:backgroundTint="@color/xxx"
 *
 * 9.在spinnerMode=”dropdown”时，设定下拉框的宽度(一般可不用设置)
 *   //android:dropDownWidth="50dp"
 *
 * 10.spinnerMode=”dropdown”时，下拉的项目选择窗口在水平方向相对于Spinner窗口的偏移量(一般可不用设置)
 *   //android:dropDownHorizontalOffset="1.5dp"
 *
 * 11.选中/未选中的selector(一般可不用设置)
 *   //android:dropDownSelector="@drawable/xxx"
 *
 * 12.背景颜色(设置了之后看不见箭头)
 *   //android:background="@color/white"
 *   android:background="@null" //去掉箭头(可用于自定义箭头的情况, 或不满意文字和箭头间隔过大的问题.)
 *
 *
 * 自定义属性, bs开头:
 * 1.spinner的填充内容, 用','分隔开(如果已经设置了'entries', 不用再设置这个)
 * @see R.styleable#BaseSpinner_bsEntriesString         //@string/names
 *
 * 2.spinner填充的布局(根部局是一个TextView), 默认: android.R.layout.simple_spinner_item
 * @see R.styleable#BaseSpinner_bsResource              //@layout/xxx
 *
 * 3.下拉列表item布局(根部局是一个TextView), 默认: android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item
 * @see R.styleable#BaseSpinner_bsDropDownViewResource  //@layout/xxx
 *
 *
 * @version 1.0 增加重复选中的监听
 * @version 1.0.1 禁止OnItemSelectedListener默认会自动调用一次的问题: {@link #init(Context, AttributeSet)}
 *
 * @param <T> 填充Item的数据类型, 见{@link #setDatas(Collection)}, 示例:
 *           @BindView(R.id.spinner)
 *           BaseSpinner<User> spinner;
 *
 *           User = spinner.getSelectedItem();//获取当前已选择的User
 */
public class BaseSpinner<T> extends AppCompatSpinner {

    protected int prePosition = -1;
    protected ArrayAdapter<T> arrayAdapter;
    protected int spinnerRes;
    protected int ddvr;

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

    protected void init(Context context, AttributeSet attrs) {
        //https://www.cnblogs.com/jooy/p/9165769.html
        //禁止OnItemSelectedListener默认会自动调用一次
        setSelection(0);//不写这句貌似都可以
        setSelection(0, true);

        //spinner布局
        spinnerRes = android.R.layout.simple_spinner_item;
        //下拉item布局
        ddvr = androidx.appcompat.R.layout.support_simple_spinner_dropdown_item;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseSpinner);
            spinnerRes = a.getResourceId(R.styleable.BaseSpinner_bsResource, spinnerRes);
            ddvr = a.getResourceId(R.styleable.BaseSpinner_bsDropDownViewResource, ddvr);
            //spinner的列表值, 用","隔开
            String items = a.getString(R.styleable.BaseSpinner_bsEntriesString);
            a.recycle();

            if (items != null && !items.isEmpty()) {
                String[] split = items.split(",");
                setDatas(split);
            }
        }
    }

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
        super.setOnItemSelectedListener(listener);
    }

    public interface OnItemSelectedListener2 extends OnItemSelectedListener {

        //再次选择了同一个item
        default void onItemReSelected(AdapterView<?> parent, View view, int position, long id){
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
    public void setDatas(CharSequence[] datas) {
        if (datas != null) {
            //Arrays.asList 返回的List是Arrays的内部类, 没有重写add等方法
            List<CharSequence> list = new ArrayList<>();
            Collections.addAll(list, datas);
            setDatas((Collection<T>) list);
        }
    }

    /**
     * 设置数据, 填充Spinner
     * @param <T> 如果数据类型 "T" 不是CharSequence或String,
     *            重写数据类型的toString()方法即可, 列表item填充的时候会调用toString()的内容
     * 注意: 每次填充的T数据类型应该一致
     */
    public void setDatas(Collection<? extends T> datas) {
        if (datas != null) {
            //如果不是ArrayAdapter, 需要你自己处理.
            getArrayAdapter().clear();
            getArrayAdapter().addAll(datas);
        }
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

    public ArrayAdapter<T> getArrayAdapter() {
        //不能使用getAdapter()这个方法, 因为如果设置了android:entries, 返回的ArrayList是Arrays的内部类, 没重写add等方法
//        SpinnerAdapter adapter = getAdapter();
        if (arrayAdapter == null) {
            arrayAdapter = new ArrayAdapter<>(getContext(), spinnerRes);
            arrayAdapter.setDropDownViewResource(ddvr);
            setAdapter(arrayAdapter);
        }
        return arrayAdapter;
    }
}
