
package com.actor.myandroidframework.utils.mpchart;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.actor.myandroidframework.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.NumberFormat;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class MarkerViewForMPChart extends MarkerView {

    protected final TextView tvContent;
//    protected DecimalFormat  format = new DecimalFormat("#.##");
    protected NumberFormat   format = NumberFormat.getInstance();

    public MarkerViewForMPChart(Context context) {
        this(context, R.layout.marker_view_for_mpchart);
    }

    public MarkerViewForMPChart(Context context, @LayoutRes int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            //edited
//            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
            tvContent.setText(format.format(ce.getHigh()));
        } else {
            //edited
//            tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
            tvContent.setText(format.format(e.getY()));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
