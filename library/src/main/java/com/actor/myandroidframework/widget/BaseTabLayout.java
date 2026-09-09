package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.actor.myandroidframework.R;
import com.google.android.material.tabs.TabLayout;

/**
 * description: 让 TabLayout 更方便设置自定义View, 示例流程:
 * <pre>
 * &lt;com.actor.myandroidframework.widget.BaseTabLayout
 *     android:layout_width="match_parent"
 *     android:layout_height="40dp"
 *     android:background="color|drawable|selector" //如果设置了tabBackground,这个属性无效. if背景是图片, 图片有点高的话, 这个属性会影响TabItem布局到顶部.
 *     app:tabBackground="color|drawable|selector"  //改变TabItem的背景, selector可做viewpagerIndicator效果. 如果是颜色,不能直接写RGB，需要@color/xxx?
 *     app:tabIndicatorHeight="1dp"                 //指示器高度,高度为0，相当于没有下标
 *     app:tabIndicatorFullWidth="false"            //宽度是否和 TabItem 该有宽度一致
 *     app:tabIndicator="color|drawable"            //自定义Indicator
 *     app:tabIndicatorColor="color"                //下方指示器颜色
 *     app:tabIndicatorGravity="bottom"             //指示器位置, 默认在内容下方(top,center,stretch[填充])
 *     app:tabGravity="fill|center|start"   //TabItem 在'整个 TabLayout' 中: center居中(wrap_content)，如果是fill(TabItem的宽度)，则是充满
 *     app:tabMode="auto|fixed|scrollable"  //fixed:平分控件的宽度.
 * 	   app:tabTextColor="color"             //Tab文字颜色
 *     app:tabSelectedTextColor="color"     //Tab选中文字颜色
 * 	   app:tabTextAppearance="@android:style/TextAppearance.Holo.Large"//设置文字的外貌,大小
 * 	   app:tabContentStart="20dp"           //TabLayout开始位置的偏移量
 *
 * 	   app:tabPadding="xxdp"                //TabLayout 的Padding(app:tabMode="fixed"时才生效)
 * 	   app:tabPaddingStart="0dp"            //TabItem的PaddingStart
 * 	   app:tabPaddingEnd="0dp"              //TabItem的PaddingEnd
 * 	   app:tabPaddingTop="xxdp"
 * 	   app:tabPaddingBottom="-10dp"
 *
 * 	   app:tabMaxWidth="xxdp"           //设置最大的tab宽度
 * 	   app:tabMinWidth="xxdp"           //设置最小的tab宽度
 * 	   app:tabRippleColor="@null"       //点击效果, null取消
 *
 * 	   //{@link null 自定义View}
 * 	   app:tabItemLayout="@R.layout.xxx" //TabItem 的 自定义View(TabItem 的 layout 属性)
 *
 *     app:paddingEnd="xxdp"            //设置整个TabLayout的Padding
 * 	   app:paddingStart="xxdp"          //设置整个TabLayout的Padding
 *     tools:background="@color/gray_E3E3E3">   //假定1个背景, 用于预览
 *
 * //示例Tab填充数据的对象, 仅供参考
 * public class Item {
 *     public @DrawableRes int resId;
 *     public String name;
 *     public Item(@DrawableRes int resId, String name) {
 *         this.resId = resId;
 *         this.name = name;
 *     }
 * }
 *
 * //页面中自定义Tab数据示例
 *  private List<Item> items = new ArrayList<>();
 *  items.add(new Item(R.drawable.selector_tab_item_icon1, "首页"));
 *  items.add(new Item(R.drawable.selector_tab_item_icon2, "联系人"));
 *  items.add(new Item(R.drawable.selector_tab_item_icon3, "个人中心"));
 *  //设置当前页面两侧多少页不会被回收, 取值范围: [1, items.Size() - 1]
 *  viewPager.setOffscreenPageLimit(1);
 *  viewPager.setAdapter(new MyPagerAdapter());
 *  tabLayout.setupWithViewPager(viewPager);
 *
 *  //if要自定义view, 在 {@link ViewPager#setAdapter(PagerAdapter)} & {@link #setupWithViewPager(ViewPager)} 后
 *  tabLayout.{@link #setCustomView(int, InitItemListener)};
 * </pre>
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

    /**
     * @param autoRefresh 如果给定ViewPager的内容发生更改({@link androidx.viewpager.widget.PagerAdapter#notifyDataSetChanged()})，此布局是否应刷新其内容 <br />
     *                    {@link null 注意:} if {@link com.google.android.material.tabs.TabLayout.Tab}是自定义View, 当 {@link androidx.viewpager.widget.PagerAdapter#notifyDataSetChanged()} 刷新后, Tab的自定义{@link com.google.android.material.tabs.TabLayout.Tab#customView}会被移除并重新填充成{@link com.google.android.material.tabs.TabLayout.TabView}, 见: {@link #populateFromPagerAdapter()} <br />
     *                    所以: if 你的 {@link androidx.viewpager.widget.PagerAdapter} 会调用 {@link androidx.viewpager.widget.PagerAdapter#notifyDataSetChanged()} 改变页面布局, TabLayout的Item不变的话, 可以将这个参数传false <br />
     *                    否则: 需要重新调用 {@link #setCustomView(int, InitItemListener)} 再次初始化{@link com.google.android.material.tabs.TabLayout.Tab#customView}
     */
    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh) {
        super.setupWithViewPager(viewPager, autoRefresh);
    }

    /**
     * 设置自定义View
     * @param position 第几个tab
     * @param layoutRes 自定义view
     * @return
     */
    @Nullable
    public Tab setCustomView(int position, @LayoutRes int layoutRes) {
        Tab tabAt = getTabAt(position);
        if (tabAt != null) return tabAt.setCustomView(layoutRes);
        return null;
    }

    @Nullable
    public Tab setCustomView(int position, @Nullable View view) {
        Tab tabAt = getTabAt(position);
        if (tabAt != null) return tabAt.setCustomView(view);
        return null;
    }

    /**
     * 给所有 TabItem 设置自定义View
     * @param layoutRes item 的 layout
     * @param listener 初始化 TabLayout 的 item监听
     */
    public void setCustomView(@LayoutRes int layoutRes, @Nullable InitItemListener listener) {
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
         * @param tab 这个TabItem填充的View, 示例初始化:
         *    <pre>
         *        View customView = tab.getCustomView(); <br />
         *        ImageView iv = customView.findViewById(R.id.iv); <br />
         *        TextView tv = customView.findViewById(R.id.tv); <br />
         *        Item item = items.get(position); <br />
         *        tv.setText(item.name); <br />
         *        iv.setImageResource(item.resId);
         *    </pre>
         */
        void initItem(int position, View itemView);
    }
}
