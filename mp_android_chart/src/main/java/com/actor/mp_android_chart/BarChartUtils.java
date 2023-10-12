package com.actor.mp_android_chart;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.List;

/**
 * description: 柱状图图表工具类 <a href="https://github.com/PhilJay/MPAndroidChart" target="_blank">Github地址</a> <br />
 * Author     : ldf <br />
 * date       : 2021/1/11 on 18 <br />
 *
 * <br />
 * <pre>
 * 1.使用前添加依赖
 * //https://github.com/PhilJay/MPAndroidChart
 * implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
 * </pre>
 *
 * @version 1.0
 */
public class BarChartUtils {

//    protected static final RectF onValueSelectedRectF = new RectF();

    /**
     * 通用初始化
     * @param barChart 柱状图图表
     */
    public static void init(BarChart barChart) {
        //最大显示图表条数(如果超过count,则在图表顶部不显示值, 100), 不用额外设置
        barChart.setMaxVisibleValueCount(100);
        //图表的文本描述, 默认true
        Description description = barChart.getDescription();
        //图表右下角"描述"是否显示(true)
        description.setEnabled(false);
        description.setText("柱状图描述: Test");

        //柱子背景颜色(阴影,false)
        barChart.setDrawBarShadow(false);
        //图表是否显示表边框(false)
        barChart.setDrawBorders(false);
        //在柱状图顶部上/下方显示值(true), 具体是否显示还需要看具体数据集BarDataSet
        barChart.setDrawValueAboveBar(true);
        //触摸, 默认true
        barChart.setTouchEnabled(true);
        //拖拽, 默认true
        barChart.setDragEnabled(false);
        //缩放, 默认true
        barChart.setScaleEnabled(false);
        //X轴缩放, 默认true
        barChart.setScaleXEnabled(false);
        //Y轴缩放, 默认true
        barChart.setScaleYEnabled(false);
        //设置捏(双指)缩放(false)
        barChart.setPinchZoom(false);
        //图图的背景,灰色(false)
        barChart.setDrawGridBackground(false);
        //禁止右侧Y轴(true)
        barChart.getAxisRight().setEnabled(false);
        //图表选中监听
//        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry e, Highlight h) {
//                LogUtils.errorFormat("Entry selected=%s, Highlight=%s", e, h);
//                if (e == null) {
//                    return;
//                }
//                RectF bounds = onValueSelectedRectF;
//                barChart.getBarBounds((BarEntry) e, bounds);
//                MPPointF position = barChart.getPosition(e, YAxis.AxisDependency.LEFT);
//
//                LogUtils.errorFormat("bounds=%s, position=%s", bounds, position);
//                LogUtils.errorFormat("x-index, low=%f, high=%f", barChart.getLowestVisibleX(), barChart.getHighestVisibleX());
//
//                MPPointF.recycleInstance(position);
//            }
//
//            @Override
//            public void onNothingSelected() {
//                LogUtils.error("onNothingSelected");
//            }
//        });
    }

    /**
     * 通用初始化X轴
     * @param xMaximum X轴最右侧能显示的最大值, 可传入数据list.size() - 1
     * @param xAxisFormatter X轴显示内容格式化(自定义显示内容), 可重写 getFormattedValue()方法, 也可传null
     */
    public static XAxis initXAxis(BarChart barChart, float xMaximum, @Nullable ValueFormatter xAxisFormatter) {
        // 获取 X 轴
        XAxis xAxis = barChart.getXAxis();
        //X轴显示在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置字体
        //xAxis.setTypeface(tfLight);
        //是否显示X 轴网格线(竖线|, true)
        xAxis.setDrawGridLines(true);
        //X轴颗粒度, 如果两个相邻轴的值被舍入为相同的值, 轴值可能重复. 如果使用粒度，可以通过减少可见的轴值来避免这种情况(1F)
        xAxis.setGranularity(1F);
        /*
         * 参1: count: X轴一共显示多少个Label([2,25],默认6).
         * 参2: force, 是否强制显示这么多个
         *      force=true: 例x轴0-100: 0,11,22,33,44,56,67,78,89,100
         *      force=false: 例x轴0-100: 0,10,20,30,40,50,60,70,80,90,100
         */
        xAxis.setLabelCount(6, false);
        //X 轴的垂直网格线(线长,间隔长,偏移量(10°))
        xAxis.enableGridDashedLine(10F, 10F, 0F);
        //设置网格线颜色(Color.GRAY)
        xAxis.setGridColor(Color.GRAY);
        //X轴标签居中, 对分组条形图特别有用(false)
        xAxis.setCenterAxisLabels(true);
        //x轴显示内容格式化
        xAxis.setValueFormatter(xAxisFormatter);
        //X轴最小值, 第一条竖线的值
        xAxis.setAxisMinimum(0F);
        //X轴最大值, 最后一条竖线的值(如果X轴 setCenterAxisLabels(), 最后一个值被右移了, 导致看不见)
        xAxis.setAxisMaximum(xMaximum);
        //设置X轴偏移量(5px), 单位dp, 暂未找到有啥用???
        //xAxis.setXOffset(2F);
        return xAxis;
    }

    /**
     * 通用初始化Y轴
     * @param isLeftAxis 是左侧Y轴还是右侧Y轴
     * @param yMaximum Y轴最顶部能显示的最大值
     * @param yAxisFormatter Y轴显示内容格式化(自定义显示内容), 可重写 getFormattedValue()方法, 也可传null
     */
    public static YAxis initYAxis(BarChart barChart, boolean isLeftAxis, float yMaximum, @Nullable ValueFormatter yAxisFormatter) {
        YAxis yAxis = isLeftAxis ? barChart.getAxisLeft() : barChart.getAxisRight();
        //启用/禁用Y轴. (显示/隐藏)
        //yAxis.setEnabled(true/false);

        //设置Y轴数值显示位置(OUTSIDE_CHART)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //设置字体
        //yAxis.setTypeface(tfLight);
        //是否显示Y 轴网格线(横线─, true)
        yAxis.setDrawGridLines(true);
        //Y轴颗粒度, 如果两个相邻轴的值被舍入为相同的值, 轴值可能重复. 如果使用粒度，可以通过减少可见的轴值来避免这种情况(1F)
        //yAxis.setGranularity(1F);
        /*
         * 参1: count: Y轴一共显示多少个Label([2,25],默认6).
         * 参2: force, 是否强制显示这么多个
         *      force=true: 例y轴0-100: 0,11,22,33,44,56,67,78,89,100
         *      force=false: 例y轴0-100: 0,10,20,30,40,50,60,70,80,90,100
         */
        yAxis.setLabelCount(6, false);
        //Y 轴的水平网格线
        yAxis.enableGridDashedLine(10F, 10F, 0F);
        //设置网格线颜色(Color.GRAY)
        yAxis.setGridColor(Color.GRAY);
        //Y轴标签居中, 对分组条形图特别有用(false)
        //yAxis.setCenterAxisLabels(false);
        //Y轴显示内容格式化
        yAxis.setValueFormatter(yAxisFormatter);
        //X轴最小值, 第一条横线的值
        yAxis.setAxisMinimum(0F);
        //Y轴最大值, 最后一条横线的值(如果Y轴 setCenterAxisLabels(), 最后一个值被上移了, 导致看不见)???
        yAxis.setAxisMaximum(yMaximum);
        //设置X轴偏移量(5px), 单位dp, 暂未找到有啥用???
        //yAxis.setXOffset(2F);
        //轴空间从最大值到顶部百分比的总轴范围(10%)???
        //yAxis.setSpaceTop(10F);
        return yAxis;
    }

    /**
     * 通用初始化图例对象('─ 水    ─ 电    ─ 气'...)
     */
    public static Legend initLegend(BarChart barChart) {
        //获取图例对象
        Legend legend = barChart.getLegend();
        //是否绘制图例对象
        legend.setEnabled(true);
        //设置字体
//        legend.setTypeface(tfLight);
        //设置X轴偏移(5px), 单位dp
        //legend.setXOffset(10f);
        //设置Y轴偏移(5px), 单位dp
        //legend.setYOffset(0f);
        //显示在顶部
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //显示水平居中
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //水平排列
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //在图表内/外绘制
        legend.setDrawInside(false);
        //自动换行, 默认false
        legend.setWordWrapEnabled(false);


        //图例标签形状(NONE:不绘制, EMPTY:不绘制但保留空间, DEFAULT:圆形, SQUARE:正方形, CIRCLE:圆形, LINE:水平线)
        legend.setForm(Legend.LegendForm.LINE);
        //设置线宽(高度)
        legend.setFormLineWidth(3F);
        //虚线(线长,间隔长,偏移量(10°))
//        legend.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        //图例标签宽/高(8)
        legend.setFormSize(20F);


        //设置标签文字大小([6,24],默认10dp), 单位dp
        legend.setTextSize(12);
        //设置标签X轴间距(6), 单位px
        legend.setXEntrySpace(30);
        //设置标签Y轴间距(0), 垂直排序才有效, 单位px
        //legend.setYEntrySpace(ConvertUtils.dp2px(23));
        //标签和文字间距
        legend.setFormToTextSpace(2.5F);
        //自定义
        //legend.setCustom();
        return legend;
    }

    /**
     * 自定义 MarkerView，当数据被选择时会展示(在顶部展示气泡)
     * 注意: 设置的数据集 {@link com.github.mikephil.charting.data.BarDataSet#setHighlightEnabled(boolean)} 必须设置true才展示气泡, 否则不展示
     *       如果不想设置数据被选中后的灰色背景高亮, 可以设置透明度=0{@link com.github.mikephil.charting.data.BarDataSet#setHighLightAlpha(int)},
     *       或者设置透明颜色 {@link com.github.mikephil.charting.data.BarDataSet#setHighLightColor(int)}
     */
    public static MarkerView setMarkerView(BarChart barChart, @Nullable MarkerView mv) {
        if (mv == null) mv = new MarkerViewForMPChart(barChart.getContext());
        mv.setChartView(barChart);
        barChart.setMarker(mv);
        return mv;
    }

    /**
     * 创建一个数据集 DataSet ，用来添加 Entry。一个图中可以包含多个数据集
     * @param dataSetIndex 第几种数据
     * @param barEntries 数据集的数据, BarEntry构造方法可添加 icon: getResources().getDrawable(R.drawable.star)
     * @param label 数据集名称
     * @param color 数据集颜色
     */
    public static BarDataSet setBarDataSet(BarChart barChart, @IntRange(from = 0) int dataSetIndex, List<BarEntry> barEntries, String label, @ColorInt int color) {
        BarDataSet barDataSet;
        BarData barData = barChart.getData();//getBarData一样的
        if (barData != null && barData.getDataSetCount() > dataSetIndex) {
            barDataSet = (BarDataSet) barData.getDataSetByIndex(dataSetIndex);
            barDataSet.setValues(barEntries);
            barDataSet.setLabel(label);
            barDataSet.setColor(color);
            barDataSet.notifyDataSetChanged();
        } else {
            // 2. 创建一个数据集 DataSet ，用来添加 Entry。一个图中可以包含多个数据集
            barDataSet = new BarDataSet(barEntries, label);
            //是否在柱子顶部显示值(true)
            barDataSet.setDrawValues(true);
            //设置边框宽度(0F)
            barDataSet.setBarBorderWidth(0F);
            /**
             * 选中以后是否高亮(添加一个灰色背景层,true). 注意: 如果要显示 {@link MarkerView}, 这儿必须设置为true
             */
            barDataSet.setHighlightEnabled(true);
            //设置高亮透明度(120)
            //barDataSet.setHighLightAlpha(120);
            //设置高亮颜色
            //barDataSet.setHighLightColor();
            //柱子颜色
            barDataSet.setColor(color/*, alpha*/);

            //自定义图例对象('─ 水    ─ 电    ─ 气'...)
//            barDataSet.setFormLineWidth(1f);
            //虚线(线长,间隔长,偏移量(10°))
//            barDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            //字体大小
//            barDataSet.setFormSize(15.f);


            barDataSet.setDrawIcons(false);
        }
        return barDataSet;
    }

    /**
     * 将单/多个数据集添加到数据 ChartData 中
     * @param fromX 从X轴的哪儿开始画柱状图, 这个值对应 BarEntry 的第1参数
     * @param barWidth 柱子宽度占比 = 柱子宽度/每一列宽度, 例: 0.25F
     * @param barSpace: 每一条数据之间间隔百分比 = 间隔/每一列宽度, 例: 0.125F
     * @param groupSpace: 每一组数据间隔百分比 = 间隔/每一列宽度, 例: 0.25F
     *                  注意: (barWidth + barSpace) * 2 + groupSpace = 1.00 -> interval per "group" 一定要等于1,乘以2是表示每组有两个数据
     * @param dataSets 数据集
     */
    public static BarData groupBars(BarChart barChart, float fromX, float barWidth, float barSpace, float groupSpace, IBarDataSet... dataSets) {
//        BarData barData = barChart.getData();//getBarData一样的
        BarData barData = new BarData(dataSets);
//        if (barData == null) {
//            barData = new BarData(dataSets);
//        } else {
//            //barData.
//        }
        barData.setBarWidth(barWidth);
        if (dataSets.length > 1) {
            barData.groupBars(fromX, groupSpace, barSpace);
        }
        //设置柱状图顶部显示的内容(自定义显示内容), 可重写 getFormattedValue()方法, 也可传null
        //barData.setValueFormatter();

        // 5. 将数据添加到图表中
        barChart.setData(barData);

        //需要先设置数据
//        barChart.groupBars(/*startYear*/0, groupSpace, barSpace);

        return barData;
    }
}
