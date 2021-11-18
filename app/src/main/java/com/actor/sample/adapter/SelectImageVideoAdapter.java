package com.actor.sample.adapter;import android.app.Activity;import android.widget.ImageView;import androidx.annotation.NonNull;import com.actor.myandroidframework.utils.album.AlbumUtils;import com.actor.myandroidframework.utils.picture_selector.PictureSelectorUtils;import com.actor.sample.R;import com.blankj.utilcode.util.ToastUtils;import com.bumptech.glide.Glide;import com.chad.library.adapter.base.BaseQuickAdapter;import com.chad.library.adapter.base.viewholder.BaseViewHolder;import com.luck.picture.lib.entity.LocalMedia;import com.yanzhenjie.album.AlbumFile;import java.util.ArrayList;import java.util.List;/** * description: Album的方式, 选择图片/视频 * * @author : ldf * date       : 2020/10/7 on 21 * @version 1.0 */public class SelectImageVideoAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {    private ArrayList<AlbumFile> albums = new ArrayList<>();    private List<LocalMedia>     pics = new ArrayList<>();    private boolean isPictureType = false;    public SelectImageVideoAdapter() {        super(R.layout.item_image_video_select);        setOnItemChildClickListener((adapter, view, position) -> {            switch (view.getId()) {                case R.id.iv:                    preview();                    break;                case R.id.iv_delete:                    remove(position);                    break;                default:                    break;            }        });        addChildClickViewIds(R.id.iv, R.id.iv_delete);    }    @Override    protected void convert(@NonNull BaseViewHolder helper, Object item) {        ImageView iv = helper.getView(R.id.iv);        if (item instanceof AlbumFile) {            Glide.with(iv).load(((AlbumFile) item).getPath()).into(iv);        } else if (item instanceof LocalMedia) {            Glide.with(iv).load(((LocalMedia) item).getPath()).into(iv);        }    }    public void setIsPictureType(boolean isPictureType) {        this.isPictureType = isPictureType;    }    //预览    private void preview() {        if (isPictureType) {            ToastUtils.showShort("预览 PictureSelectorUtils 选择的图片");            pics.clear();            for (Object datum : getData()) {                if (datum instanceof LocalMedia) {                    pics.add((LocalMedia) datum);                }            }            PictureSelectorUtils.previewImageVideos((Activity) getContext(), true, 0, pics);        } else {            ToastUtils.showShort("预览 AlbumUtils 选择的图片");            albums.clear();            for (Object datum : getData()) {                if (datum instanceof AlbumFile) {                    albums.add((AlbumFile) datum);                }            }            AlbumUtils.galleryAlbum(getContext(), albums, 0, false, null);        }    }}