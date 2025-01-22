package com.actor.picture_selector.adapter_recyclerview;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 添加图片, 使用示例: {@link AddLocalMediaAble}
 *
 * @author : ldf
 * date       : 2020/9/18 on 20:28
 * @version 1.0
 */
public class AddPicAdapter<UploadInfo> extends BaseQuickAdapter<LocalMedia, BaseViewHolder> implements AddLocalMediaAble<UploadInfo> {

    private       int              maxFiles;//最多选择多少个
    private       boolean          isShowCamera = true;//是否显示相机
    private       boolean          isShowOriginal = true;//是否开启原图☑选项
    private       int              selectType;//选择类型: 0 拍照, 1 选择图片
    @DrawableRes
    private final int              lastItemPic;//最后一个Item显示的图片
    @DrawableRes
    private final int              deletePic;//删除按钮图片
    private final List<LocalMedia> localMedias = new ArrayList<>();
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

    public AddPicAdapter(int maxFile, int type) {
        this(maxFile, type, null);
    }

    public AddPicAdapter(int maxFile, int type, @Nullable AddLocalMediaAble.OnItemClickListener listener) {
        this(maxFile, type, LAYOUT_RES_ID, R.drawable.camera_gray_for_file_select, DRAWABLE_DELETE_ICON, listener);
    }

    /**
     * @param maxFile 最多选择多少张图片
     * @param type 选择类型: 0 拍照, 1 选择图片
     * @param layoutResId 自定义Item布局
     * @param lastItemPic 最后一个Item显示的图片
     * @param deletePic 删除按钮图片
     * @param listener item点击监听
     */
    public AddPicAdapter(int maxFile, int type, @LayoutRes int layoutResId,
                         @DrawableRes int lastItemPic, @DrawableRes int deletePic,
                         @Nullable AddLocalMediaAble.OnItemClickListener listener) {
        super(layoutResId);
        this.maxFiles = maxFile;
        this.selectType = type;
        this.lastItemPic = lastItemPic;
        this.deletePic = deletePic;
        this.itemClickListener = listener;
        initAddLocalMediaAble();
        addChildClickViewIds(R.id.iv_for_file_select, R.id.iv_delete_for_file_select);
        addData(EXTRA_LAST_MEDIA);

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
                            ToastUtils.showShort("最多选择%d张", maxFiles);
                        } else {
                            Activity topActivity = ActivityUtils.getTopActivity();
                            if (topActivity == null) {
                                return;
                            }
                            switch (selectType) {
                                case 0://拍照
                                    PictureSelectorUtils.create(topActivity, localMedias)
                                            .takePhoto(true)
                                            .setCrop(false)
                                            .build()
                                            .forResult(onResultCallbackListener);
                                    break;
                                case 1://选择图片
                                    PictureSelectorUtils.create(topActivity, localMedias)
                                            .selectImage(true)
                                            .setMaxSelect(maxFiles)
                                            .setShowCamera(isShowCamera)
                                            .setIsShowOriginal(isShowOriginal)
                                            .build()
                                            .forResult(onResultCallbackListener);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else {//预览
                        Activity topActivity = ActivityUtils.getTopActivity();
                        if (topActivity != null && !topActivity.isDestroyed()) {
                            PictureSelectorUtils.create(topActivity, localMedias).openPreview().preview(position, false);
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
            Glide.with(iv).load(item.getPath()).into(iv);
        }
    }

    /**
     * 选择类型: 0 拍照, 1 选择图片
     */
    public void setSelectType(int selectType) {
        this.selectType = selectType;
    }

    /**
     * 选择图片的时候, 是否显示相机
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
     * 是否开启原图☑选项
     */
    public void setIsShowOriginal(boolean isShowOriginal) {
        this.isShowOriginal = isShowOriginal;
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
