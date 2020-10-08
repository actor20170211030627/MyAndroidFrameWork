package com.actor.sample.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.widget.QuickSearchBar;
import com.actor.sample.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.pinyinhelper.PinyinMapDict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description: 主页->快速查找条
 * Author     : 李大发
 * Date       : 2019/10/27 on 19:30
 */
public class QuickSearchBarActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.rtv_tips)
    TextView     rtvTips;
    @BindView(R.id.quicksearchbar)
    QuickSearchBar quicksearchbar;

    private MyAdapter            myAdapter;
    private List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_search_bar);
        ButterKnife.bind(this);

        setTitle("主页->快速查找条QuickSearchBar");
        Pinyin.init(Pinyin.newConfig()
                .with(new PinyinMapDict() {
                    @Override
                    public Map<String, String[]> mapping() {
                        HashMap<String, String[]> map = new HashMap<>();
                        map.put("重庆",  new String[]{"CHONG", "QING"});
//                        map.put("解",  new String[]{"XIE"});
                        return map;
                    }
                }));

        myAdapter = new MyAdapter(R.layout.item_select_dealer, items);
        recyclerView.setAdapter(myAdapter);
        //setListener
        quicksearchbar.setOnLetterChangedListener(recyclerView, new QuickSearchBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                rtvTips.setVisibility(View.VISIBLE);
                rtvTips.setText(letter);
            }

            @Override
            public void onActionUp() {
                rtvTips.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rtvTips.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });
        items.clear();
        items.add(new Item("点点滴滴"));    //d
        items.add(new Item("正则正则"));    //z
        items.add(new Item("滚滚滚"));      //g
        items.add(new Item("鹅鹅鹅鹅鹅"));   //e
        items.add(new Item("-/----/---"));  //#
        items.add(new Item("啊啊啊啊啊啊啊"));//a
        items.add(new Item("摇一摇"));        //y
        items.add(new Item("重庆朝天门"));    //c
        items.add(new Item("宝宝贝贝吧"));    //b
        items.add(new Item("小星星"));        //x
        items.add(new Item("冲冲冲"));        //c
        items.add(new Item("反反复复"));      //f
        items.add(new Item("烦烦烦"));       //f
        items.add(new Item("错错错"));        //c
        items.add(new Item("我我我我"));      //w
        items.add(new Item("重庆火锅"));      //c
        items.add(new Item("v8会员"));        //v
        items.add(new Item("呵呵呵"));        //h
        items.add(new Item("通天塔"));        //t
        items.add(new Item("贾俊杰回家吃饭")); //j
        items.add(new Item("么么么么么"));     //m
        items.add(new Item("啦啦啦"));         //L
        quicksearchbar.sortData(items);
        myAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends BaseQuickAdapter<Item, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<Item> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Item item) {
            int position = helper.getAdapterPosition();
            helper.setText(R.id.tv_letter, item.letter)
                    .setText(R.id.tv_contact, item.itemName)
                    .setGone(R.id.tv_letter, position == 0 ||
                            !TextUtils.equals(item.letter, items.get(position - 1).letter));
        }
    }

    private class Item implements QuickSearchBar.PinYinSortAble {

        private String itemName;
        private String letter;

        public Item(String itemName) {
            this.itemName = itemName;
        }

        @Override
        public void setLetter(String letter) {
            this.letter = letter;
        }

        @Override
        public String getLetter() {
            return letter;
        }

        @Override
        public String getSortString() {
            return itemName;
        }
    }
}
