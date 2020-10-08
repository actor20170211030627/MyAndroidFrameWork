package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.utils.album.AlbumUtils;
import com.actor.myandroidframework.utils.picture_selector.PictureSelectorUtils;
import com.actor.myandroidframework.widget.ItemRadioGroupLayout;
import com.actor.sample.R;
import com.actor.sample.adapter.SelectImageVideoAdapter;
import com.blankj.utilcode.util.GsonUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * description: 文件选择
 *
 * @author : 李大发
 * date       : 2020/10/7 on 20:44
 */
public class SelectFileActivity extends BaseActivity {

    @BindView(R.id.irgl)
    ItemRadioGroupLayout irgl;
    @BindView(R.id.btn_5)
    Button btn5;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private SelectImageVideoAdapter mAdapter;

    //AlbumUtils 回调
    private Action<ArrayList<AlbumFile>> action = new Action<ArrayList<AlbumFile>>() {
        @Override
        public void onAction(@NonNull ArrayList<AlbumFile> result) {
            mAdapter.addData(result);
            String json = GsonUtils.toJson(result);
            logError(json);
        }
    };

    //PictureSelectorUtils 回调
    private OnResultCallbackListener<LocalMedia> listener = new OnResultCallbackListener<LocalMedia>() {
        @Override
        public void onResult(List<LocalMedia> result) {
//            result.get(0).getPath();//content://media/external/file/116272
//            result.get(0).getRealPath();///storage/emulated/0/news_article/a9f5efc45c8f17fa1f160615ed5ba5fb.png
//            result.get(0).getAndroidQToPath();//null
//            result.get(0).getCompressPath();//null
//            result.get(0).getCutPath();//null
//            result.get(0).getOriginalPath();//null
            mAdapter.addData(result);
            String json = GsonUtils.toJson(result);
            logError(json);
        }
        @Override
        public void onCancel() {
            toast("取消了");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        ButterKnife.bind(this);

        setTitle("主页->文件选择");
        recyclerView.setAdapter(mAdapter = new SelectImageVideoAdapter());
        //选中监听
        irgl.setOnCheckedChangeListener((group, checkedId, position, reChecked) -> {
            mAdapter.setIsPictureType(position == 0);
            btn5.setVisibility(position * View.INVISIBLE);
        });
    }

    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5})
    public void onViewClicked(View view) {
        boolean isPics = irgl.getCheckedPosition() == 0;
        switch (view.getId()) {
            case R.id.btn_1://单选图片(显示Camrea)
                if (isPics) {
                    PictureSelectorUtils.selectImage(this, true, listener);
                } else {
                    AlbumUtils.selectImage(this, true, action);
                }
                break;
            case R.id.btn_2://多选图片(不显示Camera)
                if (isPics) {
                    PictureSelectorUtils.selectImages(this, false, 5, listener);
                } else {
                    AlbumUtils.selectImages(this, false, 5, null, action);
                }
                break;
            case R.id.btn_3://单选视频(不显示Camrea)
                if (isPics) {
                    PictureSelectorUtils.selectVideo(this, false, listener);
                } else {
                    AlbumUtils.selectVideo(this, false, null, action);
                }
                break;
            case R.id.btn_4://多选视频(显示Camrea)
                if (isPics) {
                    PictureSelectorUtils.selectVideos(this, true, 5, listener);
                } else {
                    AlbumUtils.selectVideos(this, null, null, 5, action);
                }
                break;
            case R.id.btn_5://选择音频
                PictureSelectorUtils.selectAudio(this, 5, null, listener);
                break;
            default:
                break;
        }
    }
}