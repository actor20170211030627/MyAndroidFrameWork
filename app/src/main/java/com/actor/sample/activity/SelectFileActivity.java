package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.actor.myandroidframework.utils.LogUtils;
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

    private final AddPicAdapter<Object> mPictureAdapter = new AddPicAdapter<>(9, 1);
    private final AddVideoAdapter<Object> mVideoAdapter = new AddVideoAdapter<>(9, 1);
    private final AddAudioAdapter<Object> mAudioAdapter = new AddAudioAdapter<>(9, 1);

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
        recyclerView21.setAdapter(mPictureAdapter);
        recyclerView22.setAdapter(mVideoAdapter);
        recyclerView23.setAdapter(mAudioAdapter);

        //选择类型
        viewBinding.islSelectType.setSelectedItemPosition(1);
        //选图片/视频/音频是否显示拍照按钮
        viewBinding.islIsShowCamera.setSelectedItemPosition(1);
        //选图片/视频时是否显示'原图'
        viewBinding.islIsShowOriginal.setSelectedItemPosition(1);

        viewBinding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNoEmpty(viewBinding.itilMaxFiles)) {
                    String input = viewBinding.itilMaxFiles.getText().toString().trim();
                    int maxFiles = Integer.parseInt(input);
                    int selectType = viewBinding.islSelectType.getSelectedItemPosition();
                    int showCamera = viewBinding.islIsShowCamera.getSelectedItemPosition();
                    int showOriginal = viewBinding.islIsShowOriginal.getSelectedItemPosition();
                    LogUtils.errorFormat("maxFiles=%d, selectType=%d, showCamera=%b, showOriginal=%b",
                            maxFiles, selectType, showCamera, showOriginal
                            );

                    mPictureAdapter.setMaxFiles(maxFiles);
                    mPictureAdapter.setSelectType(selectType);
                    mPictureAdapter.setIsShowCamera(showCamera == 1);
                    mPictureAdapter.setIsShowOriginal(showOriginal == 1);

                    mVideoAdapter.setMaxFiles(maxFiles);
                    mVideoAdapter.setSelectType(selectType);
                    mVideoAdapter.setIsShowCamera(showCamera == 1);
                    mVideoAdapter.setIsShowOriginal(showOriginal == 1);

                    mAudioAdapter.setMaxFiles(maxFiles);
                    mAudioAdapter.setSelectType(selectType);
                    mAudioAdapter.setIsShowCamera(showCamera == 1);
//                    mAudioAdapter.setIsShowOriginal(showOriginal == 1);
                }
            }
        });

    }
}