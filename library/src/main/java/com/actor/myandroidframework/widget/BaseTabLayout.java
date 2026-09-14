package com.actor.myandroidframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseIntArray;
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
 * 	   app:btlTabItemLayout="@layout/xxx" //TabItem 的 自定义View(TabItem 的 layout 属性)
 *
 *     app:paddingEnd="xxdp"            //设置整个TabLayout的Padding
 * 	   app:paddingStart="xxdp"          //设置整个TabLayout的Padding
 *     tools:background="@color/gray_E3E3E3">   //假定1个背景, 用于预览
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
    protected final SparseIntArray                             tabLayoutCustomViews = new SparseIntArray();
    protected       boolean                                    setupWithViewPager2  = false;
    protected       boolean                                    tabLayoutLoggable    = false;
    protected       TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy;
    protected       TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy2;

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
        tabItemLayoutRes = a.getResourceId(R.styleable.BaseTabLayout_btlTabItemLayout, 0);
        a.recycle();
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        super.setupWithViewPager(viewPager);
        setupWithViewPager2 = false;
    }
    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh) {
        super.setupWithViewPager(viewPager, autoRefresh);
        setupWithViewPager2 = false;
    }
    /**
     * 设置和ViewPager一起联动, 需要先 viewPager.setAdapter(xxx);后才调用本方法
     * @param viewPager ViewPager
     * @param autoRefresh 如果给定ViewPager的内容发生更改, 如: {@link androidx.viewpager.widget.PagerAdapter#notifyDataSetChanged() PagerAdapter.notifyDataSetChanged()}，TabLayout是否应刷新其内容
     * @param tabConfigurationStrategy 在回调中给tab设置值, if不关心tab的赋值可传null, 例:
     * <pre>
     *     public void onConfigureTab(@NonNull Tab tab, int position) { <br />
     *         //if已经设置了 {@link R.styleable#BaseTabLayout_btlTabItemLayout app:btlTabItemLayout="@layout/xxx"} 自定义View <br />
     *         View customView = tab.getCustomView(); <br />
     *         if (customView != null) customView.xxx(); //对自定义View赋值等 <br />
     *         tab.setText("Tab ${position + 1}"); <br />
     *         tab.icon = xxx; <br />
     *     }
     * </pre>
     */
    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh, @Nullable TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy) {
        super.setupWithViewPager(viewPager, autoRefresh);
        setupWithViewPager2 = false;
        this.tabConfigurationStrategy = tabConfigurationStrategy;
    }

    /**
     * 设置和ViewPager2一起联动, 需要先 viewPager.setAdapter(xxx);后才调用本方法
     * @param viewPager2 ViewPager2
     * @param autoRefresh 如果给定ViewPager的内容发生更改, 如: {@link androidx.viewpager.widget.PagerAdapter#notifyDataSetChanged() PagerAdapter.notifyDataSetChanged()}，TabLayout是否应刷新其内容
     * @param smoothScroll 是否平滑滑动
     * @param tabConfigurationStrategy 在回调中给tab设置值, if不关心tab的赋值可传null, 例:
     * <pre>
     *     public void onConfigureTab(@NonNull Tab tab, int position) { <br />
     *         //if已经设置了 {@link R.styleable#BaseTabLayout_btlTabItemLayout app:btlTabItemLayout="@layout/xxx"} 自定义View <br />
     *         View customView = tab.getCustomView(); <br />
     *         if (customView != null) customView.xxx(); //对自定义View赋值等 <br />
     *         tab.setText("Tab ${position + 1}"); <br />
     *         tab.icon = xxx; <br />
     *     }
     * </pre>
     */
    public TabLayoutMediator setupWithViewPager2(@NonNull ViewPager2 viewPager2, boolean autoRefresh, boolean smoothScroll,
                                    @Nullable TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy) {
        setupWithViewPager2 = true;
        tabConfigurationStrategy2 = tabConfigurationStrategy;
        if (this.tabConfigurationStrategy == null) {
            this.tabConfigurationStrategy = new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull Tab tab, int position) {
                    /**
                     * {@link TabLayoutMediator#populateTabsFromPagerAdapter()} newTab();
                     * 后直接调用了 tabConfigurationStrategy.onConfigureTab(tab, i);
                     * 然后才调用 tabLayout.addTab(tab, false); 所以本方法就不用再手动添加 customView 了
                     * 乺: 我在这儿就将自定义view设置进去
                     */
                    tabSetCustomView(tab, position);
                    if (tabConfigurationStrategy2 != null) tabConfigurationStrategy2.onConfigureTab(tab, position);
                }
            };
        }
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(this, viewPager2, autoRefresh, smoothScroll, this.tabConfigurationStrategy);
        tabLayoutMediator.attach();
        return tabLayoutMediator;
    }

    /**
     * 设置自定义View
     * @param position 第几个tab
     * @param layoutRes 自定义view
     */
    public void setCustomView(int position, @LayoutRes int layoutRes) {
        Tab tabAt = getTabAt(position);
        if (tabAt == null) return;
        tabAt = tabAt.setCustomView(layoutRes);
        tabLayoutCustomViews.put(position, layoutRes);
        if (tabConfigurationStrategy != null) tabConfigurationStrategy.onConfigureTab(tabAt, position);
    }

    /**
     * 设置自定义View, {@link null 注意:} view 并不会缓存入list, if 再次刷新布局, view需要你自己再重新设置
     * @param position 第几个tab
     * @param view 自定义view
     */
    public void setCustomView(int position, @Nullable View view) {
        Tab tabAt = getTabAt(position);
        if (tabAt == null) return;
        tabAt = tabAt.setCustomView(view);
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
         * 然后才调用 tabLayout.addTab(tab, false); 所以ViewPager2就不用再手动添加 customView 了
         */
        if (!setupWithViewPager2) {
            tabSetCustomView(tab, position);
            if (tabConfigurationStrategy != null) tabConfigurationStrategy.onConfigureTab(tab, position);
        }
        super.addTab(tab, position, setSelected);
    }

    /**
     * if有 customView 的话, 给 tab 设置进去
     * @param tab
     * @param position
     */
    protected void tabSetCustomView(@NonNull Tab tab, int position) {
        int layoutRes = tabLayoutCustomViews.get(position, tabItemLayoutRes);
        if (layoutRes != 0) tab.setCustomView(layoutRes);
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
        tabLayoutCustomViews.removeAt(position);
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
        if (this.tabConfigurationStrategy != null) {
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
