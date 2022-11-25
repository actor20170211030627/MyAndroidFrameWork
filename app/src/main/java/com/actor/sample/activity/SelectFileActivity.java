package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.adapter_recyclerview.AddAudioAdapter;
import com.actor.myandroidframework.adapter_recyclerview.AddPicAdapter;
import com.actor.myandroidframework.adapter_recyclerview.AddVideoAdapter;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.album.AlbumUtils;
import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.actor.sample.R;
import com.actor.sample.adapter.SelectImageVideoAdapter;
import com.actor.sample.databinding.ActivitySelectFileBinding;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;

/**
 * description: 文件选择
 *
 * @author : ldf
 * date       : 2020/10/7 on 20:44
 */
public class SelectFileActivity extends BaseActivity<ActivitySelectFileBinding> {

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView21;
    private RecyclerView recyclerView22;
    private RecyclerView recyclerView23;

    private SelectImageVideoAdapter mAdapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView1 = viewBinding.recyclerView1;
        recyclerView21 = viewBinding.recyclerView21;
        recyclerView22 = viewBinding.recyclerView22;
        recyclerView23 = viewBinding.recyclerView23;

        setTitle("主页->文件选择");
        int dp5 = SizeUtils.dp2px(5);
        BaseItemDecoration decoration = new BaseItemDecoration(dp5, dp5);
        recyclerView1.addItemDecoration(decoration);
        recyclerView1.setAdapter(mAdapter1 = new SelectImageVideoAdapter());

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

//    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4})
    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //AlbumUtils:
            case R.id.btn_1://单选图片(显示Camrea)
                AlbumUtils.selectImage(this, true, action);
                break;
            case R.id.btn_2://多选图片(不显示Camera)
                AlbumUtils.selectImages(this, false, 5, null, action);
                break;
            case R.id.btn_3://单选视频(不显示Camrea)
                AlbumUtils.selectVideo(this, false, null, action);
                break;
            case R.id.btn_4://多选视频(显示Camrea)
                AlbumUtils.selectVideos(this, null, null, 5, action);
                break;
            default:
                break;
        }
    }

    //AlbumUtils 回调
    private final Action<ArrayList<AlbumFile>> action = new Action<ArrayList<AlbumFile>>() {
        @Override
        public void onAction(@NonNull ArrayList<AlbumFile> result) {
            mAdapter1.addData(result);
            String json = GsonUtils.toJson(result);
            LogUtils.error(json);
        }
    };
}