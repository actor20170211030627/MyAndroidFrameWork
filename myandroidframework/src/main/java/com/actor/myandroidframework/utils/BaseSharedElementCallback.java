package com.actor.myandroidframework.utils;

import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.SharedElementCallback;

import java.util.List;
import java.util.Map;

/**
 * Description: A界面跳B界面, B界面返回A界面, 2种方式的共享元素跳转回调
 *              例: A界面: RecyclerView <--> B界面: ViewPager, 返回后更新A界面共享元素position
 * Author     : 李大发
 * Date       : 2020/2/5 on 15:06
 *
 * @version 1.0
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BaseSharedElementCallback extends SharedElementCallback {

    //A界面跳转B界面时, 是否是"元素共享" & "返回A界面时需更新位置" 的这种方式
    public static final String EXTRA_IS_NEED_UPDATE_POSITION = "EXTRA_IS_NEED_UPDATE_POSITION";
    //如果是↑这种跳转方式, 这个参数表示A界面跳转前共享元素在A界面的position
    public static final String EXTRA_START_POSITION          = "EXTRA_START_POSITION";
    //如果是↑这种跳转方式, 这个参数表示B界面返回A界面时, B界面的position(用于A界面更新共享元素位置)
    public static final String EXTRA_CURRENT_POSITION        = "EXTRA_CURRENT_POSITION";


    public boolean isAJump2B;
    public boolean isAReenter$BReturn;
    public int     oldPosition, currentPosition;
    protected OnSharedElementPositionChangedListener listener;

    /**
     * @param isAJump2B 如果A界面跳B界面, 在A界面new BaseSharedElementCallback(true, listener)
     *                  如果B界面返回A界面,在B界面new BaseSharedElementCallback(false, listener)
     * @param listener 共享元素位置改变监听
     */
    public BaseSharedElementCallback(boolean isAJump2B, @NonNull OnSharedElementPositionChangedListener listener) {
        this.isAJump2B = isAJump2B;
        this.listener = listener;
    }

    /**
     * @param isAReenter$BReturn A界面表示是否
     * @param oldPosition
     * @param currentPosition
     */
    public void set(boolean isAReenter$BReturn, int oldPosition, int currentPosition) {
        this.isAReenter$BReturn = isAReenter$BReturn;
        this.oldPosition = oldPosition;
        this.currentPosition = currentPosition;
    }

    //在A界面: 'A->B'界面 和 'B返回A' 界面Reenter时都会触发
    //在B界面: B界面 enter 和 return 时都会触发

    @Override
    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        //A界面跳B界面, 这是A界面的回调
        if (isAJump2B) {
            //从B界面返回A界面, 先走Activity.onActivityReenter();方法
            if (isAReenter$BReturn) {
                if (oldPosition != currentPosition) {
                    View newSharedElement = listener.onSharedElementPositionChanged(oldPosition, currentPosition);
                    if (newSharedElement != null) {
                        names.clear();
                        names.add(newSharedElement.getTransitionName());
                        sharedElements.clear();
                        sharedElements.put(newSharedElement.getTransitionName(), newSharedElement);
                    }
                }
                isAReenter$BReturn = false;
            } else {
                //从A界面进入到B界面
//            View navigationBar = findViewById(android.R.id.navigationBarBackground);
//            View statusBar = findViewById(android.R.id.statusBarBackground);
//            if (navigationBar != null) {
//                names.add(navigationBar.getTransitionName());
//                sharedElements.put(navigationBar.getTransitionName(), navigationBar);
//            }
//            if (statusBar != null) {
//                names.add(statusBar.getTransitionName());
//                sharedElements.put(statusBar.getTransitionName(), statusBar);
//            }
            }
        } else {
            //B界面返回A界面, 这是B界面的回调
            //B界面返回A界面
            if (isAReenter$BReturn) {
                View sharedElement = listener.onSharedElementPositionChanged(oldPosition, currentPosition);
                if (sharedElement == null) {
                    names.clear();
                    sharedElements.clear();
                } else if (oldPosition != currentPosition) {
                    names.clear();
                    names.add(sharedElement.getTransitionName());
                    sharedElements.clear();
                    sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                }
                isAReenter$BReturn = false;
            } else {
                //A界面进入了B界面
            }
        }
    }

    public interface OnSharedElementPositionChangedListener {
        /**
         * @return 在A界面的回调中, 当B界面返回A界面时, 用于获取A界面currentPosition位置的共享元素
         *         在B界面的回调中, 当B界面返回A页面时, 需要先获取B界面currentPosition位置的共享元素
         */
        View onSharedElementPositionChanged(int oldPosition, int currentPosition);
    }
}
