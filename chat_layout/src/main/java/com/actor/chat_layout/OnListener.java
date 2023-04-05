package com.actor.chat_layout;

import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * Description: 事件监听 <br />
 * Author     : ldf <br />
 * Date       : 2016/10/4 on 11:40
 */
public interface OnListener {

    /**
     * 左侧语音按钮点击事件,一般不用重写此方法监听
     */
    default void onIvVoiceClick(ImageView ivVoice){}

    /**
     * 没有录音等权限
     */
    default void onNoPermission(String permission) {}

    /**
     * 语音录制完成
     * @param audioPath 语音路径, 已判空
     * @param durationMs 语音时长, 单位ms
     */
    default void onVoiceRecordSuccess(@NonNull String audioPath, long durationMs){}

    /**
     * 录音时间太短
     * @param durationMs 录音时间, 单位ms
     * @return 如果需要自己处理, 返回true. 默认处理: 间隔250ms消失弹框
     */
    default boolean tooShortRecording(long durationMs) {
        return false;
    }

    /**
     * 录音失败
     */
    default void onVoiceRecordError(Exception e) {
    }

    /**
     * 左侧键盘按钮点击事件,一般不用重写此方法监听
     */
    default void onIvKeyBoardClick(ImageView ivKeyBoard) {}

    /**
     * 发送按钮监听,必须重写此方法
     */
    void onBtnSendClick(EditText etMsg);

    /**
     * 上面部分ListView or RecyclerView的触摸事件,一般不用重写此方法监听
     */
    default void onRecyclerViewTouchListener(View recyclerView, MotionEvent event){}

    /**
     * 按住说话的TextView的触摸事件,如果有语音功能,需要重写此方法监听
     */
    default void onTvPressSpeakTouch(TextView tvPressSpeak, MotionEvent event) {}

    /**
     * 右边⊕或ⓧ号点击事件
     */
    default void onIvPlusClick(ImageView ivPlus) {}

    /**
     * 表情的ImageView按钮
     */
    default void onIvEmojiClick(ImageView ivEmoji) {}

    /**
     * EditText的触摸事件,一般不用重写此方法监听
     */
    default void onEditTextToucn(EditText etMsg, MotionEvent event) {}
}
