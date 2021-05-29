package com.actor.myandroidframework.adapter_recyclerview;

import android.widget.ImageView;

import com.actor.myandroidframework.R;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 添加文件公共方法抽取
 * @see AddPicAdapter
 * @see AddVideoAdapter
 * @see AddAudioAdapter
 *
 * 选择图片示例使用:
 * //UploadFileInfo: 这是自己项目上传文件后返回的json解析的实体类.
 * private AddPicAdapter<UploadFileInfo> picAdapter = new AddPicAdapter<>(9, AddPicAdapter.TYPE_SELECT_PHOTO);
 * //是否已经选择了图片
 * boolean picSelected = picAdapter.hasFileSelected();
 * //如果选择了图片
 * if(picSelected) {
 *     uploadPics();
 * }
 *
 * //上传图片
 * private void uploadPics() {
 *     //获取已选中的文件
 *     List<LocalMedia> selectFiles = picAdapter.getSelectFiles();
 *     for (LocalMedia selectFile : selectFiles) {
 *         //如果这个文件还未上传到服务器
 *         if (!picAdapter.hasUpload(selectFile.getRealPath())) {
 *             //上传文件, 并设置上传结果, data是上传文件后返回的json解析的实体类 例:
 *             picAdapter.setUpload(selectFile.getRealPath(), (UploadFileInfo) data);
 *         }
 *     }
 * }
 * //获取已上传文件Map<文件路径, UploadInfo>, 注意: 如果上传了1次后又选择了新的文件, 那么上传路径有可能=null
 * Map<String, UploadFileInfo> alreadyUploads = picAdapter.getAlreadyUploadFiles();
 *
 * @author : ldf
 * date       : 2021/2/8 on 11
 * @version 1.0
 */
public interface AddLocalMediaAble<UploadInfo> {

    //Item布局
    public static final int LAYOUT_RES_ID        = R.layout.item_for_file_select;
    //删除图片
    public static final int DRAWABLE_DELETE_ICON = R.drawable.close_gray_for_file_select;

    /**
     * 最后一个文件占位, path例: content://media/external/file/119729
     */
    public static final LocalMedia EXTRA_LAST_MEDIA = new LocalMedia("path", 0, 0, "image/jpeg");

    /**
     * 已经选择的文件
     */
    public abstract List<LocalMedia> getSelectFiles();

    /**
     * Map<文件本地path, T>, 当前页面(new 本对象的页面)所有已上传文件(不管目前是否已在当前Adapter 的 UI 中删除)
     * @return 返回一个局部变量Map
     * @deprecated 用户不需要调用这个方法
     */
    @Deprecated
    public abstract Map<String, UploadInfo> getUploads();


    public default void initAddLocalMediaAble() {
        getSelectFiles().clear();
        getUploads().clear();
    }
    /**
     * 是否有图片选择
     */
    default boolean hasFileSelected() {
        return !getSelectFiles().isEmpty();
    }

    /**
     * 文件是否已上传服务器
     * @param path 文件路径
     * @return 返回是否已上传
     */
    default boolean hasUpload(String path) {
        return path != null && getUploads().get(path) != null;
    }

    /**
     * 设置文件上传路径
     * @param path 文件
     * @param uploadPath 上传路径
     */
    default void setUpload(String path, UploadInfo uploadPath) {
        getUploads().put(path, uploadPath);
    }

    /**
     * @return 获取已上传结果Map, 如果上传文件后又选择了图片, 那么已上传路径可能为空.
     *          所以调用这个方法前, 应该先调用上方3个方法
     */
    default Map<String, UploadInfo> getAlreadyUploadFiles() {
        Map<String, UploadInfo> alreadyUploads = new LinkedHashMap<>();
        for (LocalMedia localMedia : getSelectFiles()) {
            String realPath = localMedia.getRealPath();
            alreadyUploads.put(realPath, getUploads().get(realPath));
        }
        return alreadyUploads;
    }

    public interface OnItemClickListener {
        /**
         * "item图片"和"删除图片"的点击事件
         * @param imageView "item图片" 或 "删除图片"
         * @param isDelete 是否是删除图片的点击
         * @param position 第几个pos
         * @return 如果自己处理点击事件, return true. 否则return false
         */
        boolean onItemClick(ImageView imageView, boolean isDelete, int position);
    }
}
