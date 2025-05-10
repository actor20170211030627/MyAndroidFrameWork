package com.actor.sample.adapter;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.actor.myandroidframework.utils.FileUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.glide.GlideUtils;
import com.actor.picture_selector.utils.PictureSelectorUtils;
import com.actor.sample.R;
import com.actor.sample.utils.Global;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnDownloadListener;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.util.ArrayList;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2021/11/16 on 12
 * @version 1.0
 */
public class GlideExampleAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private static final String[] TYPES = {"url", "assets", "Resources", "File", "Uri", "byte[]",
            "raw", "raw", "ContentProvider(点击选择本都图片)"};
    private              String   contentProvider;
    private AppCompatActivity     activity;

    public GlideExampleAdapter(AppCompatActivity activity) {
        super(R.layout.item_glide_example);
        this.activity = activity;
        for (String type : TYPES) {
            addData(type);
        }
        setOnItemClickListener((adapter, view, position) -> {
            if (position == TYPES.length - 1) {
                PictureSelectorUtils.create(this.activity, null)
                        .selectImage(false)
                        .setSingleSelect(true)
                        .setShowCamera(false)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                //content://media/external/file/122414
                                contentProvider = result.get(0).getPath();
                                String path = TextUtils2.getStringFormat("选择图片, path = %s", contentProvider);
                                LogUtils.error(path);
                                ToastUtils.showShort(path);
                                //刷新最后一个itme
                                notifyItemChanged(position);
                            }

                            @Override
                            public void onCancel() {
                        ToastUtils.showShort("取消选择");
                    }
                        });
            }
        });
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        int position = helper.getAdapterPosition();
        ImageView iv = helper.setText(R.id.tv_name, position + "." + item).getView(R.id.iv);
        switch (position) {
            case 0://url网络
                Glide.with(iv).load(Global.BAIDU_LOGO).into(iv);
                break;
            case 1://assets
                GlideUtils.loadAsset(iv, "logo.png");
                break;
            case 2://Resources
                Glide.with(iv).load(R.mipmap.ic_launcher).into(iv);
                break;
            case 3://File
                Context context = getContext();
                if (context instanceof LifecycleOwner) {
                    EasyHttp.download((LifecycleOwner) context)
                            .file(PathUtils.getInternalAppFilesPath().concat(FileUtils.getFileNameFromUrl(Global.BAIDU_LOGO)))
//                            .api(Global.BAIDU_LOGO)
                            .url(Global.BAIDU_LOGO)
                            .listener(new OnDownloadListener() {
                                @Override
                                public void onDownloadProgressChange(File file, int progress) {
                                }
                                @Override
                                public void onDownloadSuccess(File file) {
                                    Glide.with(iv).load(file).into(iv);
                                }
                                @Override
                                public void onDownloadFail(File file, Throwable throwable) {
                                }
                            }).start();
                }
                break;
            case 4://Uri
                GlideUtils.loadUri(iv, null, Uri.parse(Global.BAIDU_LOGO));
                break;
            case 5://byte[]字节数组
                Context context2 = getContext();
                if (context2 instanceof LifecycleOwner) {
                    EasyHttp.download((LifecycleOwner) context2)
                            .file(PathUtils.getInternalAppFilesPath().concat(FileUtils.getFileNameFromUrl(Global.BAIDU_LOGO)))
//                            .api(Global.BAIDU_LOGO)
                            .url(Global.BAIDU_LOGO)
                            .listener(new OnDownloadListener() {
                                @Override
                                public void onDownloadProgressChange(File file, int progress) {
                                }
                                @Override
                                public void onDownloadSuccess(File file) {
                                    byte[] bytes = FileIOUtils.readFile2BytesByStream(file);
                                    Glide.with(iv).load(bytes).into(iv);
                                }
                                @Override
                                public void onDownloadFail(File file, Throwable throwable) {
                                }
                            }).start();
                }
                break;
            case 6://raw
                Glide.with(iv).load(TextUtils2.getStringFormat("android.resource://%s/raw/%s", getContext().getPackageName(), "logo")).into(iv);
                break;
            case 7://raw
                GlideUtils.loadRaw(iv, R.raw.logo);
                break;
            case 8://ContentProvider
                //DownloadManagerUtils 查询下载: content://downloads/my_downloads/xxx
                Glide.with(iv).load(contentProvider).into(iv);
                break;
            default:
                break;
        }
    }
}
