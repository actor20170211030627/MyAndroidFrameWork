package com.actor.picture_selector.adapter_recyclerview;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.picture_selector.R;
import com.actor.picture_selector.utils.PictureSelectorUtils;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 添加音频, 目前只有选择音频功能, 使用示例: {@link AddLocalMediaAble}
 *
 * @author : ldf
 * date       : 2020/9/18 on 20:28
 * @version 1.0
 */
public class AddAudioAdapter<UploadInfo> extends BaseQuickAdapter<LocalMedia, BaseViewHolder> implements AddLocalMediaAble<UploadInfo> {

    private       int              maxFiles;//最多选择多少个
    private       boolean          isShowCamera = true;//选择音频的时候, 是否显示录音按钮
    private       int              selectType;//选择类型: 0 录制音频, 1 选择音频
    @DrawableRes
    private final int              lastItemPic;//最后一个Item显示的图片
    @DrawableRes
    private final int              deletePic;//删除按钮图片
    @DrawableRes
    private final int              normalItemPic;//已选择Item显示的图片
    private final List<LocalMedia>                      localMedias = new ArrayList<>();
    //item点击
    private final AddLocalMediaAble.OnItemClickListener itemClickListener;

    private final OnResultCallbackListener<LocalMedia> onResultCallbackListener = new OnResultCallbackListener<LocalMedia>() {
        @Override
        public void onResult(ArrayList<LocalMedia> result) {
            //result和localMedias不是同一个对象
            localMedias.clear();
            localMedias.addAll(result);
            result.add(EXTRA_LAST_MEDIA);
            setList(result);
        }
        @Override
        public void onCancel() {
        }
    };

    public AddAudioAdapter(int maxFile, int type) {
        this(maxFile, type, null);
    }

    public AddAudioAdapter(int maxFile, int type, @Nullable AddLocalMediaAble.OnItemClickListener listener) {
        this(maxFile, type, LAYOUT_RES_ID, R.drawable.audio_gray_for_file_select, R.drawable.headset_gray_for_file_select,
                DRAWABLE_DELETE_ICON, listener);
    }

    /**
     * @param maxFile 最多选择多少个音频
     * @param type 选择类型: 0 录制音频, 1 选择音频
     * @param layoutResId 自定义Item布局
     * @param lastItemPic 最后一个Item显示的图片
     * @param normalItemPic 已选择音频Item显示的图片
     * @param deletePic 删除按钮图片
     * @param listener item点击监听
     */
    public AddAudioAdapter(int maxFile, int type, @LayoutRes int layoutResId,
                           @DrawableRes int lastItemPic, @DrawableRes int normalItemPic,
                           @DrawableRes int deletePic, @Nullable AddLocalMediaAble.OnItemClickListener listener) {
        super(layoutResId);
        this.maxFiles = maxFile;
        this.selectType = type;
        this.lastItemPic = lastItemPic;
        this.normalItemPic = normalItemPic;
        this.deletePic = deletePic;
        this.itemClickListener = listener;
        initAddLocalMediaAble();
        addChildClickViewIds(R.id.iv_for_file_select, R.id.iv_delete_for_file_select);
        addData(EXTRA_LAST_MEDIA);//添加一个+号

        setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                int id = view.getId();
                if (itemClickListener != null) {
                    boolean b = itemClickListener.onItemClick((ImageView) view, id == R.id.iv_delete_for_file_select, position);
                    //如果用户自己处理了点击事件
                    if (b) return;
                }
                //是否是最后一个pos
                boolean isLastPos = position == getItemCount() - 1;
                if (id == R.id.iv_for_file_select) {//添加
                    if (isLastPos) {
                        //判断是否能选择更多
                        if (getItemCount() > maxFiles) {
                            ToastUtils.showShort("最多选择%d个", maxFiles);
                        } else {
                            Activity topActivity = ActivityUtils.getTopActivity();
                            if (topActivity == null) {
                                return;
                            }
                            switch (selectType) {
                                case 0://录制音频
                                    /**
                                     * 调用系统录音, 需要添加权限:
                                     * <uses-permission android:name="android.permission.RECORD_AUDIO" />
                                     * <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
                                     */
                                    PictureSelectorUtils.create(topActivity, localMedias)
                                            .recordAudio()
                                            .forResult(onResultCallbackListener);
                                    break;
                                case 1://选择音频
                                    PictureSelectorUtils.create(topActivity, localMedias)
                                            .selectAudio()
                                            .setMaxSelect(maxFiles)
                                            .setShowCamera(isShowCamera)
                                            .forResult(onResultCallbackListener);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else {//预览
                        Activity topActivity = ActivityUtils.getTopActivity();
                        if (topActivity != null) {
                            PictureSelectorUtils.create(topActivity, localMedias)
                                    .openPreview()
                                    .setPreviewZoomEffect(getRecyclerViewOrNull())
                                    .preview(position, false);
                        }
                    }
                } else if (id == R.id.iv_delete_for_file_select) {//删除
                    removeAt(position);
                    localMedias.remove(position);
                }
            }
        });
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, LocalMedia item) {
        ImageView ivDelete = helper.getView(R.id.iv_delete_for_file_select);//删除按钮
        Glide.with(ivDelete).load(deletePic).into(ivDelete);
        //是否是最后一个pos
        boolean isLastPos = helper.getAdapterPosition() == getItemCount() - 1;
        ImageView iv = helper.setGone(R.id.iv_delete_for_file_select, isLastPos)
                .getView(R.id.iv_for_file_select);
        if (isLastPos) {
            Glide.with(iv).load(lastItemPic).into(iv);
        } else {
            Glide.with(iv).load(normalItemPic).into(iv);
        }
    }

    /**
     * 选择类型: 0 录制音频, 1 选择音频
     */
    public void setSelectType(int selectType) {
        this.selectType = selectType;
    }

    /**
     * 选择音频的时候, 是否显示录音按钮
     */
    public void setIsShowCamera(boolean isShowCamera) {
        this.isShowCamera = isShowCamera;
    }

    /**
     * @param maxFile 设置最多选择多少个文件
     */
    public void setMaxFiles(int maxFile) {
        this.maxFiles = maxFile;
    }

    /**
     * 获取已选择的有效文件
     */
    @Override
    public List<LocalMedia> getSelectFiles() {
        return localMedias;
    }

    /**
     * @deprecated 用户不需要调用这个方法, 应该调用{@link #getSelectFiles() }
     */
//    @Deprecated
//    @NonNull
//    @Override
//    public List<LocalMedia> getData() {
//        return super.getData();
//    }

    private Map<String, UploadInfo> uploads = new LinkedHashMap<>();
    /**
     * @deprecated 用户不需要调用这个方法
     */
    @Deprecated
    @Override
    public Map<String, UploadInfo> getUploads() {
        return uploads;
    }
}
