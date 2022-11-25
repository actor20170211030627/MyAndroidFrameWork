package com.actor.myandroidframework.utils.sharedelement;

/**
 * description: 共享元素A界面(开始跳转前的界面)
 *
 * @author : ldf
 * date       : 2021/10/16 on 17
 * @version 1.0
 */
public interface SharedElementA extends SharedElementAble {

    /***
     * B界面返回A界面, 且position发生了改变. A界面重写此方法, 更新共享元素位置
     * @param oldPosition
     * @param currentPosition
     */
    void onSharedElementBacked(int oldPosition, int currentPosition);
//    {
//        recyclerView.scrollToPosition(currentPosition);
//        postponeEnterTransition();//延时动画
//        recyclerView.post(this::startPostponedEnterTransition);//开始延时共享动画
//    }
}
