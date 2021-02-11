package com.actor.myandroidframework.utils.album;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

/**
 * Description: 用Glide或者Picasso实现AlbumLoader,用于画廊预览图片
 * Author     : 李大发
 * Date       : 2019/3/12 on 9:42
 * @deprecated 建议使用 {@link com.actor.myandroidframework.utils.picture_selector.PictureSelectorUtils}}
 */
@Deprecated
public class GlideAlbumLoader implements AlbumLoader {

    @Override
    public void load(ImageView imageView, AlbumFile albumFile) {
//        int mediaType = albumFile.getMediaType();
//        if (mediaType == AlbumFile.TYPE_IMAGE) {
//            Glide.with(imageView.getContext())
//                    .load(albumFile.getPath())
//                    .into(imageView);
//        } else if (mediaType == AlbumFile.TYPE_VIDEO) {
//            DefaultAlbumLoader.getInstance()
//                    .loadAlbumFile(imageView, albumFile, viewWidth, viewHeight);
//        }
        load(imageView, albumFile.getPath());
    }

    @Override
    public void load(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
//                .error(R.drawable.placeholder)
//                .placeholder(R.drawable.placeholder)
//                .crossFade()
                .into(imageView);
    }
}