package com.actor.sample.activity;import android.os.Bundle;import android.view.View;import android.widget.TextView;import androidx.recyclerview.widget.RecyclerView;import com.actor.others.widget.QuickSearchBar;import com.actor.sample.adapter.QuickSearchBarAdapter;import com.actor.sample.bean.Item;import com.actor.sample.databinding.ActivityQuickSearchBarBinding;import com.github.promeg.pinyinhelper.Pinyin;import com.github.promeg.pinyinhelper.PinyinMapDict;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import java.util.Map;/** * Description: 主页->快速查找条 * Author     : ldf * Date       : 2019/10/27 on 19:30 */public class QuickSearchBarActivity extends BaseActivity<ActivityQuickSearchBarBinding> {    private RecyclerView   recyclerView;    private TextView       rtvTips;    private QuickSearchBar<Item> quicksearchbar;    private       QuickSearchBarAdapter myAdapter;    private       boolean               isPressed;    private final List<Item>            items = new ArrayList<>();    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setTitle("主页->快速查找条QuickSearchBar");        recyclerView = viewBinding.recyclerView;        rtvTips = viewBinding.rtvTips;        quicksearchbar = viewBinding.quicksearchbar;        Pinyin.init(Pinyin.newConfig()                .with(new PinyinMapDict() {                    @Override                    public Map<String, String[]> mapping() {                        HashMap<String, String[]> map = new HashMap<>();                        map.put("重庆", new String[]{"CHONG", "QING"});//                        map.put("解",  new String[]{"XIE"});                        return map;                    }                }));        myAdapter = new QuickSearchBarAdapter(items);        recyclerView.setAdapter(myAdapter);        //setListener        quicksearchbar.setOnLetterChangedListener(recyclerView, new QuickSearchBar.OnLetterChangedListener() {            @Override            public void onLetterChanged(String letter) {                isPressed = true;                rtvTips.setVisibility(View.VISIBLE);                rtvTips.setText(letter);            }            @Override            public void onActionUp() {                rtvTips.postDelayed(new Runnable() {                    @Override                    public void run() {                        //如果2秒之内又按下去了, 就不隐藏了                        if (rtvTips != null && isPressed) {                            isPressed = false;                            rtvTips.setVisibility(View.INVISIBLE);                        }                    }                }, 2000L);            }        });        items.clear();        items.add(new Item("点点滴滴"));    //d        items.add(new Item("正则正则"));    //z        items.add(new Item("滚滚滚"));      //g        items.add(new Item("鹅鹅鹅鹅鹅"));   //e        items.add(new Item("-/----/---"));  //#        items.add(new Item("啊啊啊啊啊啊啊"));//a        items.add(new Item("摇一摇"));        //y        items.add(new Item("重庆朝天门"));    //c        items.add(new Item("宝宝贝贝吧"));    //b        items.add(new Item("小星星"));        //x        items.add(new Item("冲冲冲"));        //c        items.add(new Item("反反复复"));      //f        items.add(new Item("烦烦烦"));       //f        items.add(new Item("错错错"));        //c        items.add(new Item("我我我我"));      //w        items.add(new Item("重庆火锅"));      //c        items.add(new Item("v8会员"));        //v        items.add(new Item("呵呵呵"));        //h        items.add(new Item("通天塔"));        //t        items.add(new Item("贾俊杰回家吃饭")); //j        items.add(new Item("么么么么么"));     //m        items.add(new Item("啦啦啦"));         //L        quicksearchbar.sortData(items);        myAdapter.notifyDataSetChanged();    }}