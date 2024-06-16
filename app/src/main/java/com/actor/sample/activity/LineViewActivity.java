package com.actor.sample.activity;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;

import com.actor.myandroidframework.widget.LineView;
import com.actor.sample.databinding.ActivityLineViewBinding;
import com.blankj.utilcode.util.SizeUtils;

/**
 * description: 线条LineView
 * company    :
 * @author    : ldf
 * date       : 2024/6/14 on 16:27
 */
public class LineViewActivity extends BaseActivity<ActivityLineViewBinding> {

    private final int dp1 = SizeUtils.dp2px(1f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("线条LineView");
        //线宽
        viewBinding.btnLineWidth.setOnClickListener(v -> {
            viewBinding.lvCustom.setLineWidth(viewBinding.sbLineWidth.getProgress() * dp1);
        });
        //虚线
        viewBinding.btnDashLine.setOnClickListener(v -> {
            float lineDashWidth = viewBinding.sbDashWidth.getProgress() * dp1;
            float lineDashGap = viewBinding.sbDashGap.getProgress() * dp1;
            viewBinding.lvCustom.setDashLine(new DashPathEffect(new float[]{lineDashWidth, lineDashGap}, 0f));
        });

        //方向
        viewBinding.btnOrientationHorizontal.setOnClickListener(v -> {
            viewBinding.lvCustom.setOrientation(LineView.HORIZONTAL);
        });
        viewBinding.btnOrientationVertical.setOnClickListener(v -> {
            viewBinding.lvCustom.setOrientation(LineView.VERTICAL);
        });
        viewBinding.btnOrientationTopLeft2BottomRight.setOnClickListener(v -> {
            viewBinding.lvCustom.setOrientation(LineView.TOPLEFT_2_BOTTOMRIGHT);
        });
        viewBinding.btnOrientationBottomLeft2TopRight.setOnClickListener(v -> {
            viewBinding.lvCustom.setOrientation(LineView.BOTTOMLEFT_2_TOPRIGHT);
        });

        //纯色
        viewBinding.btnColorSolid.setOnClickListener(v -> {
            viewBinding.lvCustom.setLineGradientColors(Color.RED, null, Color.RED);
        });
        //渐变
        viewBinding.btnColorGradient.setOnClickListener(v -> {
            viewBinding.lvCustom.setLineGradientColors(Color.RED, Color.GREEN, Color.BLUE);
        });

        //旋转角度
        viewBinding.btnAngle.setOnClickListener(v -> {
            viewBinding.lvCustom.setLineAngle(viewBinding.sbAngle.getProgress());
        });
    }
}