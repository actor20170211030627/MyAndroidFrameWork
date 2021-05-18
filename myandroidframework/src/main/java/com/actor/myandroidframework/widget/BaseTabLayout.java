package com.actor.myandroidframework.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

/**
 * description: 让 TabLayout 更方便设置自定义View, 示例流程:
 *
 * public class Item {
 *     public @DrawableRes int resId;
 *     public String name;
 *     public Item(@DrawableRes int resId, String name) {
 *         this.resId = resId;
 *         this.name = name;
 *     }
 * }
 *
 *  private List<Item> items = new ArrayList<>();
 *
 *  items.add(new Item(R.drawable.selector_tab_item_icon1, "首页"));
 *  items.add(new Item(R.drawable.selector_tab_item_icon2, "联系人"));
 *  items.add(new Item(R.drawable.selector_tab_item_icon3, "个人中心"));
 *
 *  viewPager.setOffscreenPageLimit(items.size());
 *  viewPager.setAdapter(new MyPagerAdapter());
 *  tabLayout.setupWithViewPager(viewPager);
 *  tabLayout.{@link #setCustomView(Activity, int, InitItemListener)};//设置成自定义View
 *
 * @author : ldf
 * date       : 2020/7/13 on 15:15
 * @version 1.0
 */
public class BaseTabLayout extends TabLayout {

    public BaseTabLayout(Context context) {
        super(context);
    }

    public BaseTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        super.setupWithViewPager(viewPager);
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh) {
        super.setupWithViewPager(viewPager, autoRefresh);
    }

    /**
     * 给 TabItem 设置自定义View
     * @param layoutRes item 的 layout
     * @param listener 初始化 TabLayout 的 item监听
     */
    public void setCustomView(Activity activity, @LayoutRes int layoutRes, InitItemListener listener) {
        int tabCount = getTabCount();
        for (int i = 0; i < tabCount; i++) {
            View view = activity.getLayoutInflater().inflate(layoutRes, this, false);//itemView
            if (listener != null) {
                listener.initItem(i, view);
            }
            getTabAt(i).setCustomView(view);//设置自定义View
        }
    }

    /**
     * 初始化 TabLayout 的 item监听
     */
    public interface InitItemListener {

        /**
         * @param position 第几个TabItem
         * @param itemView 这个TabItem的View, 示例初始化:
         *                 ImageView iv = itemView.findViewById(R.id.iv);
         *                 TextView tv = itemView.findViewById(R.id.tv);
         *                 Item item = items.get(position);
         *                 tv.setText(item.name);
         *                 iv.setImageResource(item.resId);
         */
        void initItem(int position, View itemView);
    }
}
