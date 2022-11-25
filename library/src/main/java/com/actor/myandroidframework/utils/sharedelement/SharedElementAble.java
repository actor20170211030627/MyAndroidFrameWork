package com.actor.myandroidframework.utils.sharedelement;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * description: 共享元素
 *              A界面: (开始跳转前的界面)
 *              B界面: (被跳转的界面)
 *
 * @author : ldf
 * date       : 2021/10/16 on 17
 * @version 1.0
 */
public interface SharedElementAble {

    /**
     * RecyclerView <--> ViewPager, 共享元素跳转
     * 在A界面的回调中, 当B界面返回A界面时, 用于获取A界面currentPosition位置的共享元素
     * 在B界面的回调中, 当B界面返回A页面时, 需要先获取B界面currentPosition位置的共享元素
     *
     * @param oldPosition     跳转前在A界面的position
     * @param currentPosition 跳转后在B界面的position
     * @return 返回新position位置的共享元素View, 注意这个View要设置transitionName
     */
    @NonNull
    View sharedElementPositionChanged(int oldPosition, int currentPosition);
//    {
//        //A界面返回示例:
//        return recyclerView.findViewHolderForAdapterPosition(currentPosition).itemView.findViewById(R.id.image_view);
//        //B界面返回示例:
//        return fragment.getSharedElementView();
//    }
}
