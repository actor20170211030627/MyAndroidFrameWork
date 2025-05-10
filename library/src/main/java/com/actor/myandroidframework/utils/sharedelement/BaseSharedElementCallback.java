package com.actor.myandroidframework.utils.sharedelement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.SharedElementCallback;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.GsonUtils;

import java.util.List;
import java.util.Map;

/**
 * Description: A界面跳转B界面 or B界面返回A界面 时, 需要提供的共享元素. <br />
 * 例: A界面: RecyclerView <--> B界面: ViewPager, 返回后更新A界面共享元素position <br /> <br />
 *
 * {@link 注意:}
 * <pre>
 *     if (你在A界面的共享元素views && transitionNames 跳转前后都不变)
 *     && (你在B界面的共享元素views && transitionNames 跳转前后也不变)
 *                  ↓
 *     你可以 {@link null <b>不 new 本类()</b>}
 * </pre>
 *
 * <b>一、A界面跳B界面要走的方法:</b>
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th nowrap="nowrap">页面名</th>
 *         <th align="center">方法</th>
 *         <th>说明</th>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link SharedElementCallback#onMapSharedElements(List, Map) onMapSharedElements(List, Map)}</td>
 *         <td>需要提供共享元素</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link SharedElementCallback#onCaptureSharedElementSnapshot(View, Matrix, RectF) onCaptureSharedElementSnapshot(View, Matrix, RectF)}</td>
 *         <td>捕获共享元素的快照(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link android.app.Activity#onCreate(Bundle) onCreate(Bundle)}</td>
 *         <td>进入B界面且走完了onCreate()</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td nowrap="nowrap">{@link SharedElementCallback#onSharedElementsArrived(List, List, OnSharedElementsReadyListener) onSharedElementsArrived(List, List, OnSharedElementsReadyListener)}</td>
 *         <td>当共享元素已到达目标 Activity 时触发(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link SharedElementCallback#onMapSharedElements(List, Map) onMapSharedElements(List, Map)}</td>
 *         <td>需要提供共享元素</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link SharedElementCallback#onSharedElementsArrived(List, List, OnSharedElementsReadyListener) onSharedElementsArrived(List, List, OnSharedElementsReadyListener)}</td>
 *         <td>当共享元素已到达目标 Activity 时触发(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link SharedElementCallback#onRejectSharedElements(List) onRejectSharedElements(List)}</td>
 *         <td>当共享元素被拒绝时调用(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link SharedElementCallback#onCreateSnapshotView(Context, Parcelable) onCreateSnapshotView(Context, Parcelable)}</td>
 *         <td>创建快照视图(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link SharedElementCallback#onSharedElementStart(List, List, List) onSharedElementStart(List, List, List)}</td>
 *         <td>在共享元素动画开始时调用(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link SharedElementCallback#onSharedElementEnd(List, List, List) onSharedElementEnd(List, List, List)}</td>
 *         <td>在共享元素动画结束时调用(可不管)</td>
 *     </tr>
 * </table>
 * <br />
 *
 * <b>二、B界面返回A界面要走的方法:</b>
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th nowrap="nowrap">页面名</th>
 *         <th align="center">方法</th>
 *         <th>说明</th>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link SharedElementCallback#onMapSharedElements(List, Map) onMapSharedElements(List, Map)}</td>
 *         <td>需要提供共享元素</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link android.app.Activity#onActivityReenter(int, Intent) onActivityReenter(int, Intent)}</td>
 *         <td>返回A页面后, 先回调这个方法. 你可以从这儿获取第2页面返回的数据({@link null if页面B的transitionName发生了改变, 导致A页面元素需要更新, 可从这儿获取改变的信息})</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link SharedElementCallback#onMapSharedElements(List, Map) onMapSharedElements(List, Map)}</td>
 *         <td>需要提供共享元素</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link SharedElementCallback#onCaptureSharedElementSnapshot(View, Matrix, RectF) onCaptureSharedElementSnapshot(View, Matrix, RectF)}</td>
 *         <td>捕获共享元素的快照(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link SharedElementCallback#onCreateSnapshotView(Context, Parcelable) onCreateSnapshotView(Context, Parcelable)}</td>
 *         <td>创建快照视图(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link SharedElementCallback#onSharedElementEnd(List, List, List) onSharedElementEnd(List, List, List)}</td>
 *         <td>在共享元素动画结束时调用(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td>{@link SharedElementCallback#onSharedElementStart(List, List, List) onSharedElementStart(List, List, List)}</td>
 *         <td>在共享元素动画开始时调用(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>B页面</td>
 *         <td nowrap="nowrap">{@link SharedElementCallback#onSharedElementsArrived(List, List, OnSharedElementsReadyListener) onSharedElementsArrived(List, List, OnSharedElementsReadyListener)}</td>
 *         <td>当共享元素已到达目标 Activity 时触发(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link SharedElementCallback#onSharedElementsArrived(List, List, OnSharedElementsReadyListener) onSharedElementsArrived(List, List, OnSharedElementsReadyListener)}</td>
 *         <td>当共享元素已到达目标 Activity 时触发(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link SharedElementCallback#onRejectSharedElements(List) onRejectSharedElements(List)}</td>
 *         <td>当共享元素被拒绝时调用(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link SharedElementCallback#onCreateSnapshotView(Context, Parcelable) onCreateSnapshotView(Context, Parcelable)}</td>
 *         <td>创建快照视图(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link SharedElementCallback#onSharedElementStart(List, List, List) onSharedElementStart(List, List, List)}</td>
 *         <td>在共享元素动画开始时调用(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link SharedElementCallback#onSharedElementEnd(List, List, List) onSharedElementEnd(List, List, List)}</td>
 *         <td>在共享元素动画结束时调用(可不管)</td>
 *     </tr>
 *     <tr>
 *         <td>A页面</td>
 *         <td>{@link android.app.Activity#onActivityResult(int, int, Intent) onActivityResult(int, int, Intent)}</td>
 *         <td>再走A页面的这个方法(可从这儿接受从B返回的其它信息)</td>
 *     </tr>
 * </table>
 *
 * @author     : ldf
 * Date       : 2020/2/5 on 15:06
 *
 * @version 1.1
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class BaseSharedElementCallback extends SharedElementCallback {

    protected String pageTag;

    /**
     * 可用于标记是哪一个页面. if传null, 就不再打印日志
     * @param pageTag 例: 页面A, 页面B, null
     */
    public void setLogPageTag(@Nullable String pageTag) {
        this.pageTag = pageTag;
    }

    /**
     * A界面跳转B界面 or B界面返回A界面 时, 需要提供的共享元素. <br />
     * if你在A&B界面的共享元素不变 & 你在A&B界面这些共享元素的transitionName也不变, 可不用管这个方法. <br />
     * 示例写法:
     * <pre>
     *     names.clean();
     *     sharedElements.clear();
     *     names.add("shared_image_1");         //添加改变后的共享元素的transitionName
     *     sharedElements.put("shared_image_1", iv); //添加transitionName和对应的view
     * </pre>
     * @param names 共享元素transitionName的List
     * @param sharedElements 共享原始views和transitionNames
     */
    @Override
    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        super.onMapSharedElements(names, sharedElements);
        if (!TextUtils.isEmpty(pageTag)) {
            LogUtils.errorFormat("pageTag = %s", pageTag);
            LogUtils.errorFormat("names = %s, sharedElements.size = %d", GsonUtils.toJson(names), sharedElements.size());
        }
    }

    /**
     * 捕获共享元素的快照：在动画开始前，系统会调用此方法生成共享元素的静态快照（通常是一个 Bitmap）,
     * 当默认快照无法满足需求时（如复杂自定义 View），可在此方法中返回自定义快照数据
     * @param sharedElement 需要捕获的共享元素 View
     * @param viewToGlobalMatrix View 到全局坐标的变换矩阵
     * @param screenBounds View 在屏幕上的边界坐标
     */
    @Override
    public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
        if (!TextUtils.isEmpty(pageTag)) {
            LogUtils.errorFormat("pageTag = %s", pageTag);
            LogUtils.errorFormat("sharedElement = %s, viewToGlobalMatrix = %s, screenBounds = %s",
                    sharedElement, viewToGlobalMatrix, screenBounds);
        }
        return super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
    }

    /**
     * 协调动画时序：当共享元素已到达目标 Activity 时触发，用于延迟或控制动画启动时机 <br />
     * 处理异步操作：在动画开始前等待必要的数据加载（如图片下载） <br />
     * 使用场景:
     * <ol>
     *     <li>等待图片加载完成: 在 Glide 加载完成后调用 listener.onSharedElementsReady()</li>
     *     <li>同步复杂布局    : 确保 RecyclerView 已滚动到目标位置</li>
     *     <li>权限检查       : 在获得权限后才启动动画</li>
     * </ol>
     *
     * @param sharedElementNames 共享元素名称列表
     * @param sharedElements 共享元素 View 列表
     * @param listener 动画就绪回调
     */
    @Override
    public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, OnSharedElementsReadyListener listener) {
        super.onSharedElementsArrived(sharedElementNames, sharedElements, listener);
        if (!TextUtils.isEmpty(pageTag)) {
            LogUtils.errorFormat("pageTag = %s", pageTag);
            LogUtils.errorFormat("sharedElementNames = %s, sharedElements.size = %d, listener = %s",
                    GsonUtils.toJson(sharedElementNames), sharedElements.size(), listener);
        }
    }

    /**
     * 当共享元素被拒绝时调用（如目标 View 不可见）（较少使用）, 可在此处理动画取消后的逻辑
     * @param rejectedSharedElements
     */
    @Override
    public void onRejectSharedElements(List<View> rejectedSharedElements) {
        super.onRejectSharedElements(rejectedSharedElements);
        if (!TextUtils.isEmpty(pageTag)) {
            LogUtils.errorFormat("pageTag = %s", pageTag);
            LogUtils.errorFormat("rejectedSharedElements.size = %d", rejectedSharedElements.size());
        }
    }

    /**
     * 创建快照视图：将 onCaptureSharedElementSnapshot 捕获的快照数据转换为实际显示的 View, 可自定义快照的显示形式（如添加遮罩、调整尺寸）
     * @param snapshot onCaptureSharedElementSnapshot 返回的快照数据
     */
    @Override
    public View onCreateSnapshotView(Context context, Parcelable snapshot) {
        if (!TextUtils.isEmpty(pageTag)) {
            LogUtils.errorFormat("pageTag = %s", pageTag);
            LogUtils.errorFormat("context = %s, snapshot = %s", context, snapshot);
        }
        return super.onCreateSnapshotView(context, snapshot);
    }


    /**
     * 在共享元素动画开始时调用, 可以在此方法中记录视图的初始状态或执行动画前的准备工作
     * @param sharedElementNames 共享元素的 transitionName 列表
     * @param sharedElements 共享元素 View 列表
     * @param sharedElementSnapshots 系统生成的视图快照（不可修改）
     */
    @Override
    public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
        super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
        if (!TextUtils.isEmpty(pageTag)) {
            LogUtils.errorFormat("pageTag = %s", pageTag);
            LogUtils.errorFormat("names = %s, sharedElements.size = %d, sharedElementSnapshots.size = %d",
                    GsonUtils.toJson(sharedElementNames), sharedElements.size(), sharedElementSnapshots.size());
        }
    }

    /**
     * 在共享元素动画结束时调用, 通常用于恢复视图状态或执行后续操作
     * @param sharedElementNames 共享元素的 transitionName 列表
     * @param sharedElements 共享元素 View 列表
     * @param sharedElementSnapshots 系统生成的视图快照（不可修改）
     */
    @Override
    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
        super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
        if (!TextUtils.isEmpty(pageTag)) {
            LogUtils.errorFormat("pageTag = %s", pageTag);
            LogUtils.errorFormat("names = %s, sharedElements.size = %d, sharedElementSnapshots.size = %d",
                    GsonUtils.toJson(sharedElementNames), sharedElements.size(), sharedElementSnapshots.size());
        }
    }
}
