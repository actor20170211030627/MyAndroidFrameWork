package com.actor.sample.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.sample.adapter.QuickSearchBarAdapter;
import com.actor.sample.bean.Item;
import com.actor.sample.databinding.Fragment2ViewPagerHeightAutoCaculateBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * description: ViewPagerHeightAutoCaculate Fragment2
 * company    :
 * @author    : ldf
 * date       : 2022/3/5 on 14:00
 */
public class ViewPagerHeightAutoCaculateFragment2 extends BaseFragment<Fragment2ViewPagerHeightAutoCaculateBinding> {

    private RecyclerView rvComment;

    private       QuickSearchBarAdapter myCommentListAdapter;
    private final List<Item>            items = new ArrayList<>(9);

    public ViewPagerHeightAutoCaculateFragment2() {
        // Required empty public constructor
    }

    public static ViewPagerHeightAutoCaculateFragment2 newInstance() {
        return new ViewPagerHeightAutoCaculateFragment2();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvComment = viewBinding.rvComment;

        items.clear();
        items.add(new Item("是懂法守法"));
        items.add(new Item("斯蒂芬斯蒂芬"));
        items.add(new Item("撒地方数到法师的"));
        items.add(new Item("sfsdf"));
        items.add(new Item("的沙发斯蒂芬"));
        items.add(new Item("水电费人更"));
        items.add(new Item("dsdfg"));
        items.add(new Item("发过火的具体局"));
        items.add(new Item("u,y;utl'yjlty]fhlplh[prLPL普通"));
        rvComment.setAdapter(myCommentListAdapter = new QuickSearchBarAdapter(items));
    }
}