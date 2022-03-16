package com.actor.myandroidframework.utils.mpchart;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

/**
 * description: 折线图图表工具类 <a href="https://github.com/PhilJay/MPAndroidChart" target="_blank">Github地址</a> <br />
 * Author     : ldf <br />
 * date       : 2021/1/12 on 11<br />
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
public class LineChartUtils {

    /**
     * 通用初始化
     * @param lineChart 折线图图表
     */
    public static void init(LineChart lineChart) {
        //最大显示图表条数(如果超过count,则在图表顶部不显示值, 100), 不用额外设置
        lineChart.setMaxVisibleValueCount(100);
        //图表的文本描述, 默认true
        Description description = lineChart.getDescription();
        //图表右下角"描述"是否显示(true)
        description.setEnabled(false);
        description.setText("折线图描述: Test");

        //图表是否显示表边框(false)
        lineChart.setDrawBorders(false);
        //触摸, 默认true
        lineChart.setTouchEnabled(true);
        //拖拽, 默认true
        lineChart.setDragEnabled(false);
        //缩放, 默认true
        lineChart.setScaleEnabled(false);
        //X轴缩放, 默认true
        lineChart.setScaleXEnabled(false);
        //Y轴缩放, 默认true
        lineChart.setScaleYEnabled(false);
        //设置捏(双指)缩放(false)
        lineChart.setPinchZoom(false);
        //显示的时候是按照多大的比率缩放显示,1f表示不放大缩小
        //着重说明一下下面代码的用途
        /**
         * 先将缩放比设置成0后，再去设置你想要的缩放比。
         * 若不这样做的话，在当前页面重新加载数据时，你所设置的缩放比会失效，并且出现你意向不到的显示问题。
         * 如果你的图表只在页面加载一次的话不需要这么做。
         */
//        lineChart.zoom(0f, 1f, 0, 0);

        //图图的背景,灰色(false)
        lineChart.setDrawGridBackground(false);
        //是否在y轴上自动缩放已启用。这是特别有趣的图表显示财务数据。???
        lineChart.setAutoScaleMinMaxEnabled(false);
        //禁止右侧Y轴(true)
        lineChart.getAxisRight().setEnabled(false);
        //图表选中监听
//        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry e, Highlight h) {
//                LogUtils.formatError("Entry selected=%s, Highlight=%s", e, h);
//                LogUtils.formatError("LOW HIGH: ", "low=%f, high=%f",
//                        lineChart.getLowestVisibleX(), lineChart.getHighestVisibleX());
//
//                LogUtils.formatError("MIN MAX: ", "xMin=%f, xMax=%f, yMin=%f, yMax=%f",
//                        lineChart.getXChartMin(), lineChart.getXChartMax(), lineChart.getYChartMin(),
//                        lineChart.getYChartMax());
//            }
//            @Override
//            public void onNothingSelected() {
//                LogUtils.error("onNothingSelected");
//            }
//        });

        //画图表的时候, 是否从左往右依次画
//        lineChart.animateX(2000);
//        lineChart.animateY(2000, Easing.EaseInCubic);
        //xy轴同时动画
//        lineChart.animateXY(2000, 2000);
    }

    /**
     * 通用初始化X轴
     * @param xMaximum X轴最右侧能显示的最大值, 可传入数据list.size() - 1
     * @param xAxisFormatter X轴显示内容格式化(自定义显示内容), 可重写 getFormattedValue()方法, 也可传null
     */
    public static XAxis initXAxis(LineChart lineChart, float xMaximum, @Nullable ValueFormatter xAxisFormatter) {
        // 获取 X 轴
        XAxis xAxis = lineChart.getXAxis();
        //X轴显示在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置字体
        //xAxis.setTypeface(tfLight);
        //是否显示X 轴网格线(竖线|, true)
        xAxis.setDrawGridLines(true);
        //绘制标签  指x轴上的对应数值
        xAxis.setDrawLabels(true);
        //X轴颗粒度, 如果两个相邻轴的值被舍入为相同的值, 轴值可能重复. 如果使用粒度，可以通过减少可见的轴值来避免这种情况(1F)
        xAxis.setGranularity(1F);
        //★★★ X轴标签居中, 对分组条形图特别有用(false).    true: X轴 Label 是否从原点显示
        xAxis.setCenterAxisLabels(false);
        /*
         * X轴显示多少条, 不用额外设置!
         * 参1: count: X轴一共显示多少个Label([2,25],默认6).
         * 参2: force, 是否强制显示这么多个
         *      force=true: 例x轴0-100: 0,11,22,33,44,56,67,78,89,100
         *      force=false: 例x轴0-100: 0,10,20,30,40,50,60,70,80,90,100
         */
        xAxis.setLabelCount(6, false);
        //定制X轴起点和终点Label不能超出屏幕。
        xAxis.setAvoidFirstLastClipping(false);
        //X 轴的垂直网格线(线长,间隔长,偏移量(10°))
        xAxis.enableGridDashedLine(10F, 10F, 0F);
        //设置网格线颜色(Color.GRAY)
        xAxis.setGridColor(Color.GRAY);

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
    public static YAxis initYAxis(LineChart lineChart, boolean isLeftAxis, float yMaximum, @Nullable ValueFormatter yAxisFormatter) {
        YAxis yAxis = isLeftAxis ? lineChart.getAxisLeft() : lineChart.getAxisRight();
        //启用/禁用Y轴. (显示/隐藏)
        //yAxis.setEnabled(true/false);

        //设置Y轴数值显示位置(OUTSIDE_CHART)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //设置字体
        //yAxis.setTypeface(tfLight);
        //是否显示Y 轴网格线(横线─, true)
        yAxis.setDrawGridLines(true);
        //绘制标签  指y轴上的对应数值
        yAxis.setDrawLabels(true);
        //Y轴颗粒度, 如果两个相邻轴的值被舍入为相同的值, 轴值可能重复. 如果使用粒度，可以通过减少可见的轴值来避免这种情况(1F)
        //yAxis.setGranularity(1F);
        //★★★ Y轴 Label 是否从原点显示
        yAxis.setCenterAxisLabels(false);
        /*
         * Y轴显示多少条, 不用额外设置!
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
//        yAxis.setAxisMinValue(0F);
        yAxis.setAxisMinimum(0F);
        //Y轴最大值, 最后一条横线的值(如果Y轴 setCenterAxisLabels(), 最后一个值被上移了, 导致看不见)???
        yAxis.setAxisMaximum(yMaximum);
        //设置X轴偏移量(5px), 单位dp, 暂未找到有啥用???
        //yAxis.setXOffset(2F);
        //轴空间从最大值到顶部百分比的总轴范围(10%)???
        //yAxis.setSpaceTop(10F);


        /**
         * 创建限制线, 会在图表上横着画1/多条限制线
         */
        //参1:在y轴的数值.   参2: label
        //LimitLine ll1 = new LimitLine(150f, "上面条限制线");
        //ll1.setLineWidth(4f);//线宽
        //虚线(线长,间隔长,偏移量(10°))
        //ll1.enableDashedLine(10f, 10f, 0f);
        //label在线的右上方
        //ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        //字体大小
        //ll1.setTextSize(10f);
        //字体
        //ll1.setTypeface(tfRegular);
        //添加限制线
        //yAxis.addLimitLine(ll1);
        return yAxis;
    }

    /**
     * 通用初始化图例对象('─ 水    ─ 电    ─ 气'...)
     */
    public static Legend initLegend(LineChart lineChart) {
        //获取图例对象
        Legend legend = lineChart.getLegend();
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
     * 注意: 设置的数据集 {@link com.github.mikephil.charting.data.LineDataSet#setHighlightEnabled(boolean)} 必须设置true才展示气泡, 否则不展示
     *       如果不想设置数据被选中后的灰色背景高亮, 可以设置透明颜色 {@link com.github.mikephil.charting.data.LineDataSet#setHighLightColor(int)}
     */
    public static MarkerView setMarkerView(LineChart lineChart, @Nullable MarkerView mv) {
        if (mv == null) mv = new MarkerViewForMPChart(lineChart.getContext());
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
        return mv;
    }

    /**
     * 创建一个数据集 DataSet ，用来添加 Entry。一个图中可以包含多个数据集
     * @param dataSetIndex 第几种数据
     * @param lineEntries 数据集的数据, Entry构造方法可添加 icon: getResources().getDrawable(R.drawable.star)
     * @param label 数据集名称
     * @param line$CircleColor 数据集线 & 线转折点的圆球 颜色
     * @param fillColor 填充颜色
     * @param fillDrawable 填充Drawable, 和 fillColor只写一个, 优先:fillDrawable
     */
    public static LineDataSet setLineDataSet(LineChart lineChart, @IntRange(from = 0) int dataSetIndex,
                                             List<Entry> lineEntries, String label, @ColorInt int line$CircleColor,
                                             @ColorInt int fillColor, @Nullable Drawable fillDrawable) {
        LineDataSet lineDataSet;
        LineData lineData = lineChart.getData();//getBarData一样的
        if (lineData != null && lineData.getDataSetCount() > dataSetIndex) {
            lineDataSet = (LineDataSet) lineData.getDataSetByIndex(dataSetIndex);
            lineDataSet.setValues(lineEntries);
            lineDataSet.setLabel(label);
            lineDataSet.setColor(line$CircleColor);
            lineDataSet.notifyDataSetChanged();
//            lineData.notifyDataChanged();
//            lineChart.notifyDataSetChanged();
        } else {
            // 2. 创建一个数据集 DataSet ，用来添加 Entry。一个图中可以包含多个数据集
            lineDataSet = new LineDataSet(lineEntries, label);
            //线的样式(线长,间隔长,偏移量(10°))
            lineDataSet.enableDashedLine(10f, 5f, 0f);
            //是否在折线转折点显示值(true)
            lineDataSet.setDrawValues(true);
            //线的厚度, 单位dp
            lineDataSet.setLineWidth(1f);
            //折线颜色
            lineDataSet.setColor(line$CircleColor/*, alpha*/);
            //线转折点的圆球的颜色
            lineDataSet.setCircleColor(line$CircleColor);
            //转折点处是否画圆点(默认true)
            lineDataSet.setDrawCircles(true);
            //点的半径大小, 默认8px, 单位dp(最小1设置才生效)
            lineDataSet.setCircleRadius(0f);
            //是否在每个点的中心画一个洞
            lineDataSet.setDrawCircleHole(false);
            /**
             * 设置线的样式
             * LINEAR: 直线
             * STEPPED: 台阶(有点像俄罗斯方块)
             * CUBIC_BEZIER: 三次贝塞尔曲线
             * HORIZONTAL_BEZIER: 水平的贝塞尔曲线(没有 CUBIC_BEZIER 平滑)
             */
            lineDataSet.setMode(LineDataSet.Mode.LINEAR);

            /**
             * 选中以后是否高亮(添加一个灰色背景层,true). 注意: 如果要显示 {@link MarkerView}, 这儿必须设置为true
             */
            lineDataSet.setHighlightEnabled(true);
            //设置选中后高亮线的颜色
            //lineDataSet.setHighLightColor();
            //选中后高亮, 画选择十字线
            lineDataSet.setDrawHighlightIndicators(true);
            //选中后高亮, 是否能话横着那条水平线
            lineDataSet.setDrawHorizontalHighlightIndicator(true);
            //选中后高亮, 是否能话竖着那条线
            lineDataSet.setDrawVerticalHighlightIndicator(true);
            //设置选中后, 高亮线的样式
            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);


            //自定义图例对象('─ 水    ─ 电    ─ 气'...)
//            lineDataSet.setFormLineWidth(1f);
            //虚线(线长,间隔长,偏移量(10°))
//            lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            //字体大小
//            lineDataSet.setFormSize(15.f);

            //设置值的字体大小, 默认17px, 单位dp
            //lineDataSet.setValueTextSize(9f);

            //设置图标区域是否填充颜色/drawable, 默认false
            lineDataSet.setDrawFilled(true);
            //从什么地方开始绘制填充色(如果return20, 从y=20的地方开始往上绘制填充色)
//            lineDataSet.setFillFormatter(new IFillFormatter() {
//                @Override
//                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
//                    return 20;//lineChart.getAxisLeft().getAxisMinimum();
//                }
//            });
            //设置填充 Drawable(可画shape,渐变)
            if (fillDrawable != null) {
                lineDataSet.setFillDrawable(fillDrawable);
            } else {
                //设置填充颜色
                lineDataSet.setFillColor(fillColor);
            }

            lineDataSet.setDrawIcons(false);
        }
        return lineDataSet;
    }

    /**
     * 将单/多个数据集添加到数据 ChartData 中
     *                  注意: (barWidth + barSpace) * 2 + groupSpace = 1.00 -> interval per "group" 一定要等于1,乘以2是表示每组有两个数据
     * @param dataSets 数据集
     */
    public static LineData groupLines(LineChart lineChart, ILineDataSet... dataSets) {
        //LineData lineData = lineChart.getData();//getLineData一样的
        LineData lineData = new LineData(dataSets);
//        if (lineData == null) {
//            lineData = new LineData(dataSets);
//        } else {
//            //lineData.
//        }
        //设置图表顶部显示的内容(自定义显示内容), 可重写 getFormattedValue()方法, 也可传null
        //lineData.setValueFormatter();

        // 5. 将数据添加到图表中
        lineChart.setData(lineData);

        //需要先设置数据
//        lineChart.groupBars(/*startYear*/0, groupSpace, barSpace);

        return lineData;
    }
}
