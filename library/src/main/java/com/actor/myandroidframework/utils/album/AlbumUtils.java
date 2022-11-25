package com.actor.myandroidframework.utils.album;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumFolder;
import com.yanzhenjie.album.Filter;
import com.yanzhenjie.album.ItemAction;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * Description: Album, 图片选择工具
 *
 * 1.如果使用本工具, 需要添加依赖:
 *   //https://github.com/yanzhenjie/Album 图片选择
 *   implementation 'com.yanzhenjie:album:2.1.3'
 *
 * 2.调用 {@link #init(Application)} 或 {@link #init(AlbumConfig)} 方法初始化
 *
 * 有以下功能:
 * <ol>
 *     <li>{@link #selectVideo$Image(Context, boolean, int, long, ArrayList, Action)}选择视频&照片</li>
 *     <li>{@link #selectImage(Context, boolean, Action)}选择单张照片</li>
 *     <li>{@link #selectImages(Context, boolean, int, ArrayList, Action)}选择照片</li>
 *     <li>{@link #selectVideo(Context, boolean, Long, Action)}选择单个视频</li>
 *     <li>{@link #selectVideos(Context, ArrayList, Long, int, Action)}选择视频</li>
 *     <li>{@link #takePhoto(Context, Action)}拍照</li>
 *     <li>{@link #recordVideo(Context, long, Action)}录制视频</li>
 *     <li>{@link #galleryAlbum(Context, ArrayList, int, boolean, Action)}通过AlbumFile path预览图片</li>
 *     <li>{@link #gallery(Context, ArrayList, int, boolean, Action)}通过String path预览图片</li>
 * </ol>
 * Author     : ldf
 * Date       : 2019/4/2 on 15:03
 * LastUpdate : 2019/04/18
 * AlbumVersion: album:2.1.3
 * Version    : 1.2.2 增加预览一张图片的方法 {@link #gallery(Context, String, boolean, Action)}
 *
 * @deprecated 建议使用 {@link com.actor.myandroidframework.utils.picture_selector.PictureSelectorUtils}
 */
@Deprecated
public class AlbumUtils {

    //拍照/录像 存放地址
    private static Widget widget;//自定义UI
    private static String title;//标题

    /**
     * 初始化, 配置画廊(图片/视频选择)
     */
    public static void init(Application application) {
        init(AlbumConfig.newBuilder(application)
                .setAlbumLoader(new GlideAlbumLoader()) // 设置Album加载器。
                .setLocale(Locale.getDefault())         //Locale.CHINA 比如强制设置在任何语言下都用中文显示。
                .build());
    }

    /**
     * 初始化, 配置画廊(图片/视频选择), 如果需要自定义配置, 请调用此方法配置
     */
    public static void init(AlbumConfig albumConfig) {
        Album.initialize(albumConfig);
    }

    /**
     * 自定义UI, title, color of StatusBar, color of NavigationBar and so on.
     * @return 不能返回null
     */
    private static Widget getWidget(Context context) {
        if (true) return Widget.getDefaultWidget(context);
        if (widget == null) {
            //Widget.newDarkBuilder(this)// StatusBar is a dark background when building:
            //Widget.newLightBuilder(this)// StatusBar is a light background when building:
            //Widget.getDefaultWidget(context)// 默认
            widget = Widget.newLightBuilder(context)
                    .title(title)
                    .statusBarColor(Color.WHITE) // StatusBar color.
                    .toolBarColor(Color.WHITE) // Toolbar color.
                    .navigationBarColor(Color.WHITE) // Virtual NavigationBar color of Android5.0+.
                    .mediaItemCheckSelector(Color.BLUE, Color.GREEN) // Image or video selection box.
                    .bucketItemCheckSelector(Color.RED, Color.YELLOW) // Select the folder selection box.
                    .buttonStyle( // Used to configure the style of button when the image/video is not found.
                            Widget.ButtonStyle.newLightBuilder(context) // With Widget's Builder model.
                                    .setButtonSelector(Color.WHITE, Color.WHITE) // Button selector.
                                    .build()
                    )
                    .build();
        }
        return widget;
    }

    /**
     * 选择视频 & 图片
     * @param context
     * @param showCamera
     * @param selectCount
     * @param durationMs 视频时长, 单位毫秒ms
     * @param checked
     * @param listener
     */
    public static void selectVideo$Image(Context context, boolean showCamera, int selectCount,
                                         long durationMs, ArrayList<AlbumFile> checked,
                                         Action<ArrayList<AlbumFile>> listener) {
        Album.album(context) // Image and video mix options.
                .multipleChoice() // Multi-Mode, Single-Mode: singleChoice().
//                .columnCount(2) //多少列,默认2 The number of columns in the page list.
                .selectCount(selectCount)  // Choose up to a few images.
                .camera(showCamera) // Whether the camera appears in the Item.
                .cameraVideoQuality(1) //拍摄视频时视频质量 Video quality, [0, 1].
                .cameraVideoLimitDuration(durationMs) //拍摄视频时长限制 The longest duration of the video is in milliseconds.
//                .cameraVideoLimitBytes(Long.MAX_VALUE) //视频大小限制 Maximum size of the video, in bytes.
                .checkedList(checked) // To reverse the list.
//                .filterSize() // Filter the file size.
//                .filterMimeType() // Filter file format.
                .filterDuration(new Filter<Long>() {//过滤视频时长 Filter video duration.
                    @Override
                    public boolean filter(Long attributes) {
                        return attributes > durationMs;
                    }
                })
                .afterFilterVisibility(false) // Show the filtered files, but they are not available.
                .widget(getWidget(context))
                .onResult(listener)
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        //点击返回键,什么都没做 The user canceled the operation.
                    }
                })
                .start();
    }

    /**
     * 选择单张图片, 右上角没有选中按钮
     * @param context
     * @param showCamera
     * @param listener
     */
    public static void selectImage(Context context, boolean showCamera, Action<ArrayList<AlbumFile>> listener) {
        Album.image(context)
                .singleChoice()
                .camera(showCamera)
//                .columnCount(2)
//                .filterSize() //过滤图片大小 Filter the file size.
//                .filterMimeType() //过滤图片类型 Filter file format.
                .afterFilterVisibility(false) //显示过滤后的文件，但它们不可用。 Show the filtered files, but they are not available.
                .widget(getWidget(context))
                .onResult(listener)
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {//点击返回键,什么都没做 User canceled.
                        System.out.println("onCancel:" + result);//onCancel:User canceled.
                    }
                })
                .start();
    }

    /**
     * 选择多张图片
     * @param context
     * @param showCamera 是否显示camera
     * @param selectCount 一共能选择几张图片
     * @param checked 已选中图片列表
     * @param listener 以选中回调监听, 返回所有已经选中的图片
     */
    public static void selectImages(Context context, boolean showCamera, int selectCount,
                                    ArrayList<AlbumFile> checked, Action<ArrayList<AlbumFile>> listener) {
        Album.image(context) // Image selection.
                .multipleChoice()
                .camera(showCamera)
//                .columnCount(2)
                .selectCount(selectCount)
                .checkedList(checked)
//                .filterSize() //过滤图片大小 Filter the file size.
//                .filterMimeType() //过滤图片类型 Filter file format.
                .afterFilterVisibility(false) //显示过滤后的文件，但它们不可用。 Show the filtered files, but they are not available.
                .widget(getWidget(context))
                .onResult(listener)
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {//点击返回键,什么都没做 User canceled.
                        System.out.println("onCancel:" + result);//onCancel:User canceled.
                    }
                })
                .start();
    }

    /**
     * 选择单个视频
     * @param context
     * @param showCamera
     * @param durationMs 视频时长限制,单位毫秒ms
     * @param listener
     */
    public static void selectVideo(Context context, boolean showCamera, Long durationMs, Action<ArrayList<AlbumFile>> listener) {
        Album.video(context)
                .singleChoice()
                .camera(showCamera)
//                .columnCount(2)
//                .filterSize()
//                .filterMimeType()
                .filterDuration(new Filter<Long>() {
                    @Override
                    public boolean filter(Long attributes) {
                        return durationMs != null && attributes > durationMs;
                    }
                })
                .afterFilterVisibility(false)
                .widget(getWidget(context))
                .onResult(listener)
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                    }
                }).start();
    }

    /**
     * 选择视频
     * @param context
     * @param checked 已选中视频列表
     * @param durationMs 视频时长限制,单位毫秒ms
     */
    public static void selectVideos(Context context, ArrayList<AlbumFile> checked, Long durationMs,
                                    int selectCount, Action<ArrayList<AlbumFile>> listener) {
        Album.video(context) // Video selection.
                .multipleChoice()
                .camera(true)
//                .columnCount(2)
                .selectCount(selectCount)
                .checkedList(checked)
//                .filterSize()//过滤视频大小
//                .filterMimeType()//过滤视频类型
                .filterDuration(new Filter<Long>() {//过滤视频时长
                    @Override
                    public boolean filter(Long attributes) {
                        return durationMs != null && attributes > durationMs;
                    }
                })
                .afterFilterVisibility(false)//显示过滤后的文件，但它们不可用。 Show the filtered files, but they are not available.
                .widget(getWidget(context))
                .onResult(listener)
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                    }
                })
                .start();
    }

    /**
     * 拍照 {@link com.yanzhenjie.album.app.album.AlbumActivity#clickCamera(View)}
     * //int rotateDegree = ImageUtils.getRotateDegree(result);//??
     * int[] size = ImageUtils.getSize(result);//可根据宽高判断照片横竖
     * @param context
     */
    public static void takePhoto(Context context, Action<String> listener) {
        Album.camera(context) // Camera function.
                .image() // Take Picture.
                // TODO: 2019/4/18 bug:自定义路径拍照后点"确定"没反应
//                .filePath(path) //文件路径,不是必须 File save path, not required.
                .onResult(listener)//AlbumFile albumFile = image2AlbumFile(result, true);
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        LogUtils.error("拍照取消!" + result);
                    }
                })
                .start();
    }

    /**
     * 录制视频
     * @param context
     * @param durationMs 视频时长限制,单位毫秒ms
     */
    public static void recordVideo(Context context, long durationMs, Action<String> listener) {
        Album.camera(context)
                .video() // Record Video.
                .filePath(PathUtils.getInternalAppFilesPath())
                .quality(1) // Video quality, [0, 1].
                .limitDuration(durationMs) //视频时长,单位秒 The longest duration of the video is in milliseconds.
//                .limitBytes(Long.MAX_VALUE) // Maximum size of the video, in bytes.
                .onResult(listener)
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                    }
                })
                .start();
    }

    /**
     * 通过AlbumFile path预览图片
     * @param context
     * @param checked 图片列表
     * @param currentPosition 第几张开始预览
     * @param checkable If checkable(false), listener not required,
     *                 the CheckBox and the FinishButton will be not appear.
     */
    public static void galleryAlbum(Context context, ArrayList<AlbumFile> checked, int currentPosition,
                                    boolean checkable, Action<ArrayList<AlbumFile>> listener) {
        if (checked != null && checked.size() > 0) {
//            ArrayList<String> paths = new ArrayList<>();
//            for (int i = 0; i < checked.size(); i++) {
//                paths.add(checked.get(i).getThumbPath());
//            }

            // Preview AlbumFile:
            Album.galleryAlbum(context)
                    .checkedList(checked)// List of image to view: ArrayList<AlbumFile>.
                    .currentPosition(currentPosition)// 第几张开始预览
                    .checkable(checkable)// Whether there is a selection function.
                    .widget(getWidget(context))
                    .onResult(listener)
                    .onCancel(new Action<String>() {
                        @Override
                        public void onAction(@NonNull String result) {

                        }
                    })
                    .itemClick(new ItemAction<AlbumFile>() {//item点击事件
                        @Override
                        public void onAction(Context context, AlbumFile item) {

                        }
                    })
                    .itemLongClick(new ItemAction<AlbumFile>() {//item长按事件
                        @Override
                        public void onAction(Context context, AlbumFile item) {

                        }
                    })
                    .start();
        }
    }

    /**
     * 预览一张图片
     */
    private static final ArrayList<String> checkedList = new ArrayList<>();
    public static void gallery(Context context, String checked, boolean checkable, Action<ArrayList<String>> listener) {
        if (checked != null) {
            checkedList.clear();
            checkedList.add(checked);
            gallery(context, checkedList, 0, checkable, listener);
        }
    }

    /**
     * 通过String path预览图片
     * @param context
     * @param checked 图片列表
     * @param currentPosition 第几张开始预览
     * @param checkable 是否有选中能力 If checkable(false), listener not required,
     *                 the CheckBox and the FinishButton will be not appear.
     */
    public static void gallery(Context context, ArrayList<String> checked, int currentPosition,
                               boolean checkable, Action<ArrayList<String>> listener) {
        Album.gallery(context)
                .checkedList(checked) // List of image to view: ArrayList<String>.
                .currentPosition(currentPosition)// 第几张开始预览
                .checkable(checkable) // Whether there is a selection function.
                .widget(getWidget(context))
                .onResult(listener)// If checkable(false), action not required.
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                    }
                })
                .itemClick(new ItemAction<String>() {//item点击事件
                    @Override
                    public void onAction(Context context, String item) {

                    }
                })
                .itemLongClick(new ItemAction<String>() {//item长按事件
                    @Override
                    public void onAction(Context context, String item) {

                    }
                })
                .start();
    }

    /**
     * TODO  路径转 AlbumFile, 添加后再进入相册会失效
     * @see com.yanzhenjie.album.app.album.data.MediaReader#scanImageFile(Map, AlbumFolder) 转换
     * @param path
     * @return
     */
    public static AlbumFile image2AlbumFile(String path, boolean checked) {
        if (path == null || path.isEmpty()) return null;
        File file = new File(path);
        if (!file.exists() || !file.isFile()) return null;
        AlbumFile albumFile = new AlbumFile();
        albumFile.setAddDate(file.lastModified());
        albumFile.setBucketName(file.getParent());
        albumFile.setChecked(checked);
//        albumFile.setDisable(disable);
//        albumFile.setLatitude(0);
//        albumFile.setLongitude(0);
        albumFile.setMediaType(AlbumFile.TYPE_IMAGE);
        albumFile.setMimeType("image/jpeg");
        albumFile.setPath(file.getAbsolutePath());
        albumFile.setSize(file.length());
//        albumFile.setThumbPath();//略缩图
        return albumFile;
    }
}
