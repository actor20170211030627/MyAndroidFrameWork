package com.actor.sample.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.actor.picture_selector.adapter_recyclerview.AddAudioAdapter;
import com.actor.picture_selector.adapter_recyclerview.AddPicAdapter;
import com.actor.picture_selector.adapter_recyclerview.AddVideoAdapter;
import com.actor.sample.databinding.ActivitySelectFileBinding;
import com.blankj.utilcode.util.SizeUtils;

/**
 * description: 文件选择
 *
 * @author : ldf
 * date       : 2020/10/7 on 20:44
 */
public class SelectFileActivity extends BaseActivity<ActivitySelectFileBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView recyclerView21 = viewBinding.recyclerView21;
        RecyclerView recyclerView22 = viewBinding.recyclerView22;
        RecyclerView recyclerView23 = viewBinding.recyclerView23;

        setTitle("主页->文件选择");
        int dp5 = SizeUtils.dp2px(5);
        BaseItemDecoration decoration = new BaseItemDecoration(dp5, dp5);

        /**
         * LocalMedia 属性:
         * .getPath();//content://media/external/file/116272
         * .getRealPath();///storage/emulated/0/news_article/a9f5efc45c8f17fa1f160615ed5ba5fb.png
         * .getAndroidQToPath();//null
         * .getCompressPath();//null
         * .getCutPath();//null
         * .getOriginalPath();//null
         */
        recyclerView21.addItemDecoration(decoration);
        recyclerView22.addItemDecoration(decoration);
        recyclerView23.addItemDecoration(decoration);
        recyclerView21.setAdapter(new AddPicAdapter<>(9, AddPicAdapter.TYPE_TAKE_SELECT_PHOTO));
        recyclerView22.setAdapter(new AddVideoAdapter<>(9, AddVideoAdapter.TYPE_TAKE_SELECT_VIDEO));
        recyclerView23.setAdapter(new AddAudioAdapter<>(9, AddAudioAdapter.TYPE_RECORD_SELECT_AUDIO));
    }
}