package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;

import com.actor.myandroidframework.widget.NineGridView.GetIsVideoAble;
import com.actor.myandroidframework.widget.NineGridView.NineGridView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.actor.sample.R;
import com.actor.sample.utils.Global;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description: 主页->九宫格
 * Author     : 李大发
 * Date       : 2019-9-6 on 11:38
 */
public class NineGridViewActivity extends BaseActivity {

    @BindView(R.id.nine_grid_view)
    NineGridView nineGridView;
    private List<PicOrVideo> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nine_grid_view);
        ButterKnife.bind(this);

        setTitle("主页->九宫格");
        for (int i = 0; i < 9; i++) {
            items.add(new PicOrVideo(Global.girl, i % 4 == 0));
        }
        nineGridView.setData(items);
        nineGridView.setOnItemClickListener(new NineGridView.OnItemClickListener<PicOrVideo>() {
            @Override
            public void onItemClick(NineGridView nineGridView, PicOrVideo item,
                                    BaseQuickAdapter adapter, View view, int position) {
                toast(getStringFormat("position=%d, isVideo=%b", position, item.isVideo()));
            }
        });
    }

    //图片或视频, implements GetIsVideoAble
    private class PicOrVideo implements GetIsVideoAble {

        public int id;
        public String url;//图片/视频 的url
        public boolean isVideo;
        public long videoLength;
        public int width;
        public int height;
        public String createTime;
        public String uploadUser;
        public int uploadUserId;
        //other infos...

        public PicOrVideo(/*int id, */String url, boolean isVideo/*, long videoLength, String createTime, String uploadUser*/) {
            this.id = id;
            this.url = url;
            this.isVideo = isVideo;
            this.videoLength = videoLength;
            this.createTime = createTime;
            this.uploadUser = uploadUser;
        }

        @Override
        public boolean isVideo() {
//            return url.endsWith(".mp4");
            return isVideo;
        }

        @Override
        public String getUrl() {
            return url;
        }
    }
}
