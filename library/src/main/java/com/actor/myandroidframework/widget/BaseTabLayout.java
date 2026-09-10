package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.LogUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
 *     app:tabInlineLabel="false"           /icon 和 text 是否显示在同一行
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
 *
 * //先设置Adapter
 *  viewPager.setAdapter(new MyPagerAdapter());
 *  tabLayout.{@link #setupWithViewPager(ViewPager, boolean, TabLayoutMediator.TabConfigurationStrategy)}
 * </pre>
 *
 * @author : ldf
 * date       : 2020/7/13 on 15:15
 * @version 1.0
 */
public class BaseTabLayout extends TabLayout {

    protected       int                                        tabItemLayoutRes     = 0; //Resources.ID_NULL;
    protected final SparseArray<Object>                        tabLayoutCustomViews = new SparseArray<>();
    protected       boolean                                    setupWithViewPager2  = false;
    protected       boolean                                    tabLayoutLoggable    = false;
    protected       TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy;

    public BaseTabLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }
    public BaseTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public BaseTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseTabLayout);
        tabItemLayoutRes = a.getResourceId(R.styleable.BaseTabLayout_tabItemLayout, 0);
        a.recycle();
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
     * @param viewPager ViewPager
     * @param autoRefresh 如果给定ViewPager的内容发生更改({@link androidx.viewpager.widget.PagerAdapter#notifyDataSetChanged()})，TabLayout是否应刷新其内容
     * @param tabConfigurationStrategy 设置Tab发生变动时, 对Tab赋值
     */
    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh, TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy) {
        super.setupWithViewPager(viewPager, autoRefresh);
        this.tabConfigurationStrategy = tabConfigurationStrategy;
    }

    /**
     * 给 ViewPager2 设置好 Adapter 后，创建 TabLayoutMediator 并调用 attach()
     * @param viewPager2 ViewPager2
     * @param autoRefresh 是否自动刷新Item
     * @param smoothScroll 是否平滑滑动
     * @param tabConfigurationStrategy 给tab设置值
     */
    public void setupWithViewPager2(@NonNull ViewPager2 viewPager2, boolean autoRefresh, boolean smoothScroll,
                                    TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy) {
        if (tabConfigurationStrategy == null) tabConfigurationStrategy = new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull Tab tab, int position) {
//                tab.setCustomView(xxx)    //if有自定义View, 要先设置
//                tab.text = "Tab ${position + 1}";
            }
        };
        new TabLayoutMediator(this, viewPager2, autoRefresh, smoothScroll, tabConfigurationStrategy).attach();
        setupWithViewPager2 = true;
    }

    /**
     * 设置自定义View
     * @param position 第几个tab
     * @param layoutRes 自定义view
     * @return
     */
    @Nullable
    public void setCustomView(int position, @LayoutRes int layoutRes) {
        Tab tabAt = getTabAt(position);
        if (tabAt == null) return;
        tabAt = tabAt.setCustomView(layoutRes);
        tabLayoutCustomViews.put(position, layoutRes);
        if (tabConfigurationStrategy != null) tabConfigurationStrategy.onConfigureTab(tabAt, position);
    }

    @Nullable
    public void setCustomView(int position, @Nullable View view) {
        Tab tabAt = getTabAt(position);
        if (tabAt == null) return;
        tabAt = tabAt.setCustomView(view);
        tabLayoutCustomViews.put(position, view);
        if (tabConfigurationStrategy != null) tabConfigurationStrategy.onConfigureTab(tabAt, position);
    }

    /**
     * 给所有 TabItem 设置自定义View
     * @param layoutRes item 的 layout
     */
    public void setCustomViews(@LayoutRes int layoutRes) {
        this.tabItemLayoutRes = layoutRes;
        tabLayoutCustomViews.clear();
        for (int i = 0; i < getTabCount(); i++) setCustomView(i, layoutRes);
    }

    /**
     * 获取指定TabItem
     * @param index 第几个
     * @return TabItem or null
     */
    @Nullable
    @Override
    public Tab getTabAt(int index) {
        return super.getTabAt(index);
    }

    /**
     * 创建1个Tab
     */
    @NonNull
    @Override
    public Tab newTab() {
//        if (tabLayoutLoggable) LogUtils.errorFormat("newTab");
//        if (tabLayoutLoggable) LogUtils.printlnStackTrance(Log.ERROR);
        return super.newTab();
    }

    @Override
    public void addTab(@NonNull Tab tab) {
        super.addTab(tab);
    }

    @Override
    public void addTab(@NonNull Tab tab, boolean setSelected) {
        super.addTab(tab, setSelected);
    }

    @Override
    public void addTab(@NonNull Tab tab, int position) {
        super.addTab(tab, position);
    }

    @Override
    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        /**
         * {@link TabLayoutMediator#populateTabsFromPagerAdapter()} newTab();
         * 后直接调用了 tabConfigurationStrategy.onConfigureTab(tab, i);
         * 然后才调用 tabLayout.addTab(tab, false); 所以本方法就不用再手动添加 customView 了
         */
        if (setupWithViewPager2) {
            super.addTab(tab, position, setSelected);
            return;
        }
        Object o = tabLayoutCustomViews.get(position, tabItemLayoutRes);
        if (o instanceof View) {
            tab.setCustomView((View) o);
        } else if (o instanceof Integer && ((Integer) o) != 0) {
            tab.setCustomView((Integer) o);
        }
        if (tabConfigurationStrategy != null) tabConfigurationStrategy.onConfigureTab(tab, position);
        super.addTab(tab, position, setSelected);
    }

    @Override
    public void removeTab(@NonNull Tab tab) {
        super.removeTab(tab);
        if (tabLayoutLoggable) LogUtils.errorFormat("removeTab");
    }

    /**
     * 移除Tab, position后面的会自动往前补齐, position位置也会被重新填写
     * @param position
     */
    @Override
    public void removeTabAt(int position) {
        if (position < 0 || position >= getTabCount()) return;
        super.removeTabAt(position);
        tabLayoutCustomViews.remove(position);
        if (tabLayoutLoggable) LogUtils.errorFormat("removeTabAt");
    }

    @Override
    public void removeAllTabs() {
        super.removeAllTabs();
        //每次ViewPager 的 PagerAdapter notifyDataSetChanged 的时候, 都会调用这个方法, 所以这儿不clear
//        tabLayoutCustomViews.clear();
        if (tabLayoutLoggable) LogUtils.errorFormat("removeAllTabs");
    }

    //选中tab
    @Override
    public void selectTab(@Nullable Tab tab) {
        super.selectTab(tab);
    }
    //选中tab, updateIndicator: 是否动画到选中的tab
    @Override
    public void selectTab(@Nullable Tab tab, boolean updateIndicator) {
        super.selectTab(tab, updateIndicator);
    }

    /**
     * 设置Tab发生变动时, 对Tab赋值
     * @param tabConfigurationStrategy tab配置策略, 使用例:
     * <pre>
     *     tab.text = "xxx"; <br />
     *     tab.icon = xxx; <br />
     *     View customView = tabConfigurationStrategy.getCustomView(); <br />
     *     if (customView != null) customView.xxx()
     * </pre>
     */
    public void setTabConfigurationStrategy(TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy) {
        this.tabConfigurationStrategy = tabConfigurationStrategy;
        if (tabConfigurationStrategy != null) {
            //tab已经从xml中加载了
            for (int i = 0; i < getTabCount(); i++) {
                Tab tabAt = getTabAt(i);
                if (tabAt != null) this.tabConfigurationStrategy.onConfigureTab(tabAt, i);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
