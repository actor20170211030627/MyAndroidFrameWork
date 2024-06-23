package com.actor.other_utils.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * description: <a href="https://www.jianshu.com/p/0e7ea6687375">Android手势缩放view - 简书.mhtml</a>,
 *     把需要缩放的view放在ZoomView里面. <br />
 *     缩放流畅, 能平移, 子View能点击. <br />
 *     {@link null 注意:}
 *     <ul>
 *         <li>
 *             外层如果有
 *             {@link androidx.swiperefreshlayout.widget.SwipeRefreshLayout SwipeRefreshLayout}
 *             或者
 *             {@link androidx.core.widget.NestedScrollView}, 会有卡顿, 缩放困难的现象
 *         </li>
 *     </ul>
 *
 * @author : ldf
 * @date   : 2024/6/21 on 18
 * @version 1.0
 */
public class ZoomView extends FrameLayout {

    protected static final String TAG = "ZoomView";

    public ZoomView(@NonNull Context context) {
        super(context);
    }

    public ZoomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ZoomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Zooming view listener interface.
     */
    public interface ZoomViewListener {

        void onZoomStarted(float zoom, float zoomx, float zoomy);

        void onZooming(float zoom, float zoomx, float zoomy);

        void onZoomEnded(float zoom, float zoomx, float zoomy);
    }

    // zooming
    protected float zoom = 1.0f;
    protected float maxZoom = 2.0f;
    protected float smoothZoom = 1.0f;
    protected float zoomX, zoomY;
    protected float smoothZoomX, smoothZoomY;
    protected boolean scrolling; // NOPMD by karooolek on 29.06.11 11:45

    // minimap variables
    protected boolean showMinimap = false;
    protected int miniMapColor = Color.WHITE;
    protected int miniMapHeight = -1;
    protected String miniMapCaption;
    protected float miniMapCaptionSize = 10.0f;
    protected int miniMapCaptionColor = Color.WHITE;

    // touching variables
    protected long lastTapTime;
    protected float touchStartX, touchStartY;
    protected float touchLastX, touchLastY;
    protected float startd;
    protected boolean pinching;
    protected float lastd;
    protected float lastdx1, lastdy1;
    protected float lastdx2, lastdy2;

    // drawing
    protected final Matrix m = new Matrix();
    protected final Paint p = new Paint();

    // listener
    protected ZoomViewListener listener;

    protected Bitmap ch;

    public float getZoom() {
        return zoom;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(final float maxZoom) {
        if (maxZoom < 1.0f) {
            return;
        }

        this.maxZoom = maxZoom;
    }

    public void setMiniMapEnabled(final boolean showMiniMap) {
        this.showMinimap = showMiniMap;
    }

    public boolean isMiniMapEnabled() {
        return showMinimap;
    }

    public void setMiniMapHeight(final int miniMapHeight) {
        if (miniMapHeight < 0) {
            return;
        }
        this.miniMapHeight = miniMapHeight;
    }

    public int getMiniMapHeight() {
        return miniMapHeight;
    }

    public void setMiniMapColor(final int color) {
        miniMapColor = color;
    }

    public int getMiniMapColor() {
        return miniMapColor;
    }

    public String getMiniMapCaption() {
        return miniMapCaption;
    }

    public void setMiniMapCaption(final String miniMapCaption) {
        this.miniMapCaption = miniMapCaption;
    }

    public float getMiniMapCaptionSize() {
        return miniMapCaptionSize;
    }

    public void setMiniMapCaptionSize(final float size) {
        miniMapCaptionSize = size;
    }

    public int getMiniMapCaptionColor() {
        return miniMapCaptionColor;
    }

    public void setMiniMapCaptionColor(final int color) {
        miniMapCaptionColor = color;
    }

    public void zoomTo(final float zoom, final float x, final float y) {
        this.zoom = Math.min(zoom, maxZoom);
        zoomX = x;
        zoomY = y;
        smoothZoomTo(this.zoom, x, y);
    }

    public void smoothZoomTo(final float zoom, final float x, final float y) {
        smoothZoom = clamp(1.0f, zoom, maxZoom);
        smoothZoomX = x;
        smoothZoomY = y;
        if (listener != null) {
            listener.onZoomStarted(smoothZoom, x, y);
        }
    }

    public ZoomViewListener getListener() {
        return listener;
    }

    public void setListner(final ZoomViewListener listener) {
        this.listener = listener;
    }

    public float getZoomFocusX() {
        return zoomX * zoom;
    }

    public float getZoomFocusY() {
        return zoomY * zoom;
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
// single touch
        if (ev.getPointerCount() == 1) {
            processSingleTouchEvent(ev);
        }
// // double touch
        if (ev.getPointerCount() == 2) {
            processDoubleTouchEvent(ev);
        }

// redraw
        getRootView().invalidate();
        invalidate();

        return true;
    }

    protected void processSingleTouchEvent(final MotionEvent ev) {

        final float x = ev.getX();
        final float y = ev.getY();

        final float w = miniMapHeight * (float) getWidth() / getHeight();
        final float h = miniMapHeight;
        final boolean touchingMiniMap = x >= 10.0f && x <= 10.0f + w
                && y >= 10.0f && y <= 10.0f + h;

        if (showMinimap && smoothZoom > 1.0f && touchingMiniMap) {
            processSingleTouchOnMinimap(ev);
        } else {
            processSingleTouchOutsideMinimap(ev);
        }
    }

    protected void processSingleTouchOnMinimap(final MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();

        final float w = miniMapHeight * (float) getWidth() / getHeight();
        final float h = miniMapHeight;
        final float zx = (x - 10.0f) / w * getWidth();
        final float zy = (y - 10.0f) / h * getHeight();
        smoothZoomTo(smoothZoom, zx, zy);
    }

    protected void processSingleTouchOutsideMinimap(final MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        float lx = x - touchStartX;
        float ly = y - touchStartY;
        final float l = (float) Math.hypot(lx, ly);
        float dx = x - touchLastX;
        float dy = y - touchLastY;
        touchLastX = x;
        touchLastY = y;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStartX = x;
                touchStartY = y;
                touchLastX = x;
                touchLastY = y;
                dx = 0;
                dy = 0;
                lx = 0;
                ly = 0;
                scrolling = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (scrolling || (smoothZoom > 1.0f && l > 30.0f)) {
                    if (!scrolling) {
                        scrolling = true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        super.dispatchTouchEvent(ev);
                    }
                    smoothZoomX -= dx / zoom;
                    smoothZoomY -= dy / zoom;
                    return;
                }
                break;

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:

// tap
                if (l < 30.0f) {
// check double tap
                    if (System.currentTimeMillis() - lastTapTime < 500) {
                        if (smoothZoom == 1.0f) {
                            smoothZoomTo(maxZoom, x, y);
                        } else {
                            smoothZoomTo(1.0f, getWidth() / 2.0f,
                                    getHeight() / 2.0f);
                        }
                        lastTapTime = 0;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        super.dispatchTouchEvent(ev);
                        return;
                    }

                    lastTapTime = System.currentTimeMillis();

                    performClick();
                }
                break;

            default:
                break;
        }

        ev.setLocation(zoomX + (x - 0.5f * getWidth()) / zoom, zoomY
                + (y - 0.5f * getHeight()) / zoom);

        ev.getX();
        ev.getY();

        super.dispatchTouchEvent(ev);
    }

    protected void processDoubleTouchEvent(final MotionEvent ev) {
        final float x1 = ev.getX(0);
        final float dx1 = x1 - lastdx1;
        lastdx1 = x1;
        final float y1 = ev.getY(0);
        final float dy1 = y1 - lastdy1;
        lastdy1 = y1;
        final float x2 = ev.getX(1);
        final float dx2 = x2 - lastdx2;
        lastdx2 = x2;
        final float y2 = ev.getY(1);
        final float dy2 = y2 - lastdy2;
        lastdy2 = y2;

// pointers distance
        final float d = (float) Math.hypot(x2 - x1, y2 - y1);
        final float dd = d - lastd;
        lastd = d;
        final float ld = Math.abs(d - startd);

        Math.atan2(y2 - y1, x2 - x1);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startd = d;
                pinching = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (pinching || ld > 30.0f) {
                    pinching = true;
                    final float dxk = 0.5f * (dx1 + dx2);
                    final float dyk = 0.5f * (dy1 + dy2);
                    smoothZoomTo(Math.max(1.0f, zoom * d / (d - dd)), zoomX - dxk
                            / zoom, zoomY - dyk / zoom);
                }

                break;

            case MotionEvent.ACTION_UP:
            default:
                pinching = false;
                break;
        }

        ev.setAction(MotionEvent.ACTION_CANCEL);
        super.dispatchTouchEvent(ev);
    }

    protected float clamp(final float min, final float value, final float max) {
        return Math.max(min, Math.min(value, max));
    }

    protected float lerp(final float a, final float b, final float k) {
        return a + (b - a) * k;
    }

    protected float bias(final float a, final float b, final float k) {
        return Math.abs(b - a) >= k ? a + k * Math.signum(b - a) : b;
    }

    @Override
    protected void dispatchDraw(final Canvas canvas) {

// do zoom
        zoom = lerp(bias(zoom, smoothZoom, 0.05f), smoothZoom, 0.2f);
        smoothZoomX = clamp(0.5f * getWidth() / smoothZoom, smoothZoomX,
                getWidth() - 0.5f * getWidth() / smoothZoom);
        smoothZoomY = clamp(0.5f * getHeight() / smoothZoom, smoothZoomY,
                getHeight() - 0.5f * getHeight() / smoothZoom);

        zoomX = lerp(bias(zoomX, smoothZoomX, 0.1f), smoothZoomX, 0.35f);
        zoomY = lerp(bias(zoomY, smoothZoomY, 0.1f), smoothZoomY, 0.35f);
        if (zoom != smoothZoom && listener != null) {
            listener.onZooming(zoom, zoomX, zoomY);
        }

        final boolean animating = Math.abs(zoom - smoothZoom) > 0.0000001f
                || Math.abs(zoomX - smoothZoomX) > 0.0000001f
                || Math.abs(zoomY - smoothZoomY) > 0.0000001f;

// nothing to draw
        if (getChildCount() == 0) {
            return;
        }

// prepare matrix
        m.setTranslate(0.5f * getWidth(), 0.5f * getHeight());
        m.preScale(zoom, zoom);
        m.preTranslate(
                -clamp(0.5f * getWidth() / zoom, zoomX, getWidth() - 0.5f
                        * getWidth() / zoom),
                -clamp(0.5f * getHeight() / zoom, zoomY, getHeight() - 0.5f
                        * getHeight() / zoom));

// get view
        final View v = getChildAt(0);
        m.preTranslate(v.getLeft(), v.getTop());

// get drawing cache if available
        if (animating && ch == null && isAnimationCacheEnabled()) {
            v.setDrawingCacheEnabled(true);
            ch = v.getDrawingCache();
        }

// draw using cache while animating
        if (animating && isAnimationCacheEnabled() && ch != null) {
            p.setColor(0xffffffff);
            canvas.drawBitmap(ch, m, p);
        } else { // zoomed or cache unavailable
            ch = null;
            canvas.save();
            canvas.concat(m);
            v.draw(canvas);
            canvas.restore();
        }

// draw minimap
        if (showMinimap) {
            if (miniMapHeight < 0) {
                miniMapHeight = getHeight() / 4;
            }

            canvas.translate(10.0f, 10.0f);

            p.setColor(0x80000000 | 0x00ffffff & miniMapColor);
            final float w = miniMapHeight * (float) getWidth() / getHeight();
            final float h = miniMapHeight;
            canvas.drawRect(0.0f, 0.0f, w, h, p);

            if (miniMapCaption != null && miniMapCaption.length() > 0) {
                p.setTextSize(miniMapCaptionSize);
                p.setColor(miniMapCaptionColor);
                p.setAntiAlias(true);
                canvas.drawText(miniMapCaption, 10.0f,
                        10.0f + miniMapCaptionSize, p);
                p.setAntiAlias(false);
            }

            p.setColor(0x80000000 | 0x00ffffff & miniMapColor);
            final float dx = w * zoomX / getWidth();
            final float dy = h * zoomY / getHeight();
            canvas.drawRect(dx - 0.5f * w / zoom, dy - 0.5f * h / zoom, dx
                    + 0.5f * w / zoom, dy + 0.5f * h / zoom, p);

            canvas.translate(-10.0f, -10.0f);
        }

// redraw
        getRootView().invalidate();
        invalidate();
    }
}