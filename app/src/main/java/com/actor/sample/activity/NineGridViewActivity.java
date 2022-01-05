package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.widget.NineGridView.GetIsVideoAble;
import com.actor.myandroidframework.widget.NineGridView.NineGridView;
import com.actor.myandroidframework.widget.NineGridView.OnItemClickListener1;
import com.actor.sample.databinding.ActivityNineGridViewBinding;
import com.actor.sample.utils.Global;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 主页->九宫格
 * Author     : ldf
 * Date       : 2019-9-6 on 11:38
 */
public class NineGridViewActivity extends BaseActivity<ActivityNineGridViewBinding> {

    private NineGridView<PicOrVideo> nineGridView;

    private final List<PicOrVideo> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->九宫格");
        nineGridView = viewBinding.nineGridView;

        for (int i = 0; i < 9; i++) {
            items.add(new PicOrVideo(Global.girl, i % 4 == 0));
        }
        nineGridView.setData(items);
        nineGridView.setOnItemClickListener(new OnItemClickListener1<PicOrVideo>() {
            @Override
            public void onItemClick(NineGridView<PicOrVideo> nineGridView, PicOrVideo item, @Nullable BaseQuickAdapter<PicOrVideo, BaseViewHolder> adapter, @NonNull View view, int position) {
                toastFormat("position=%d, isVideo=%b", position, item.isVideo());
            }
        });
    }

    //图片或视频, implements GetIsVideoAble
    private static class PicOrVideo implements GetIsVideoAble {

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
