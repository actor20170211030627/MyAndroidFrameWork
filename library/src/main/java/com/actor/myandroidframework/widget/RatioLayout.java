package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.actor.myandroidframework.R;

/**
 * Created by zhengping on 2017/4/6,15:17.
 * 按照比例，动态计算高度的帧布局
 * 自定义属性的使用：
 * 1、给自定义属性起名字
 *      a、自定义属性集合的名称
 *      b、自定义属性的名称
 *              format
 * 2、使用这个自定义属性
 *      a、命名空间
 *      b、自定义属性只能给自定义控件使用
 * 3、在自定义控件中获取自定义属性的值
 *      a、通过attrs，在所有的属性中进行查找
 *      b、通过attrs获取自定义属性集合，然后通过下标索引的方式获取自定义属性的值
 * @version 1.0
 */

public class RatioLayout extends FrameLayout {

    private float ratio;        //比例

    //new对象的时候调用
    public RatioLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    //加载布局的时候
    public RatioLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    //布局文件中有style的时候
    public RatioLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RatioLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
            ratio = typedArray.getFloat(R.styleable.RatioLayout_ratio, 0.0F);
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //整个帧布局的宽度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //排除左/右边距的影响
        int innerWidthSize = widthSize - getPaddingLeft() - getPaddingRight();

        //宽布局的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //比例 != 0;    宽模式 = 确定;    高模式 != 确定;
        if(ratio != 0 && widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {

            //重新计算heightSize
            int heightSize = (int) (innerWidthSize / ratio + 0.5F);//此时的heightSize我们想把它当作啥？当作图片的高度
            heightSize  = heightSize + getPaddingTop() + getPaddingBottom();

            //setMeasuredDimension(widthSize, heightSize);//仅仅只是确定了RatioLayout的大小，但是RatioLayout的孩子没有走measure方法
            //重新生成measureSpec
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
