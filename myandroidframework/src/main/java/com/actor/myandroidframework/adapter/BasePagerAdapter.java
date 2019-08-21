package com.actor.myandroidframework.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Description: 适用于 ViewPager 的 pager 不是 Fragment 的情况, 比如轮播图示例:
 *
 * 1.用法
 * viewPager.setAdapter(new BasePagerAdapter(imageRes));
 *
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/3/27 on 20:03
 */
public class BasePagerAdapter extends PagerAdapter {
	
    private List<Integer> imageRes;

    public BasePagerAdapter(List<Integer> imageRes) {
        this.imageRes = imageRes;
    }

    @Override
    public int getCount() {
        return imageRes.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setImageResource(imageRes.get(position));
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {//移除布局
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
