package com.actor.chat_layout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.actor.chat_layout.bean.Emoji;
import com.actor.chat_layout.emoji.FaceManager;
import com.actor.chat_layout.fragment.ChatLayoutEmojiFragment;
import com.actor.myandroidframework.adapter_viewpager.BaseFragmentStatePagerAdapter;
import com.actor.myandroidframework.utils.MMKVUtils;
import com.actor.myandroidframework.utils.audio.MediaPlayerUtils;
import com.actor.myandroidframework.utils.audio.MediaRecorderCallback;
import com.actor.myandroidframework.utils.audio.MediaRecorderUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.google.android.material.tabs.TabLayout;

/**
 * description: 聊天控件, 低仿微信聊天界面按钮点击事件, 封装几个按钮及事件, 包含: <br/>
 * author     : ldf <br/>
 * date       : 2018/8/2 on 16:16 <br/>
 *
 * <br/>
 * <ul>
 *     <li>1.语音按钮</li>
 *     <li>2.EditText</li>
 *     <li>3.Emoji按钮</li>
 *     <li>4.发送按钮</li>
 *     <li>5.⊕按钮</li>
 * </ul>
 *
 * <br/>
 * 使用步骤:
 * <ul>
 *     <li>1.如果需要使用emoji表情, 请添加一个emoji库, 或自己添加一些emoji表情. <br/>
 *         &emsp; 示例可以添加如下emoji库 <br/>
 *         &emsp; //https://gitee.com/actor20170211030627/MyAndroidFrameWork <br/>
 *         &emsp; implementation 'com.gitee.actor20170211030627.MyAndroidFrameWork:emojis:gitee's latest version' <br/>
 *     </li>
 *     <li>2.如果需要使用emoji表情, 需要在Application中初始化: ChatLayoutKit.init();</li>
 *     <li>3.如果需要使用语音功能, 请使用: {@link MediaPlayerUtils}</li>
 *     <li>4.示例使用见: <br/>
 *         &emsp; <a href = "https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/res/layout/activity_chat.xml" target="_blank">activity_chat.xml</a> <br/>
 *         &emsp; <a href = "https://gitee.com/actor20170211030627/MyAndroidFrameWork/blob/master/app/src/main/java/com/actor/sample/activity/ChatActivity.java" target="_blank">ChatActivity.java</a>
 *     </li>
 * </ul>
 *
 *
 * <br/>
 * 全部属性都是cl开头:
 * <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *     <tr>
 *         <th align="center">属性attrs</th>
 *         <th align="center">示例exams</th>
 *         <th align="center">说明docs</th>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ChatLayout_clIvVoiceVisiable clIvVoiceVisiable}</td>
 *         <td>visible/invisible/gone</td>
 *         <td>1.左边语音按钮是否显示</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ChatLayout_clIvEmojiVisiable clIvEmojiVisiable}</td>
 *         <td>visible/invisible/gone</td>
 *         <td>2.右边表情按钮是否显示</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ChatLayout_clIvPlusVisiable clIvPlusVisiable}</td>
 *         <td>visible/invisible/gone</td>
 *         <td>3.右边⊕按钮是否显示</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ChatLayout_clBtnSendBackground clBtnSendBackground}</td>
 *         <td>color/drawable</td>
 *         <td>4."发送"按钮的背景(默认有一个蓝色selector)</td>
 *     </tr>
 *     <tr>
 *         <td>{@link R.styleable#ChatLayout_clTooShortRecordingTimeMs clTooShortRecordingTimeMs}</td>
 *         <td>500</td>
 *         <td>5.多少时间内的录音算太短, 默认500ms</td>
 *     </tr>
 * </table>
 *
 *
 * <br/>
 * 注意★★★: 应该重写onBackPressed方法, 示例:
 * <pre> {@code
 * @Override
 * public void onBackPressed() {
 *     if (chatLayout.isBottomViewGone()) {
 *         super.onBackPressed();//自己页面的逻辑
 *     }
 * }
 * } </pre>
 *
 * @version 1.0
 */
public class ChatLayout extends LinearLayout {

    protected ImageView    ivVoice;
    protected ImageView    ivKeyboard;
    protected EditText     etMsg;
    //按住说话按钮
    protected TextView     tvPressSpeak;
    //表情
    protected ImageView    ivEmoji;
    protected FrameLayout  flParent;
    protected Button       btnSend;
    //右边⊕或ⓧ号
    protected ImageView ivSendPlus;
    //下方的ViewPager
    protected ViewPager viewPager;
    protected TabLayout tabLayout;
    //上面列表RecyclerView
    @Nullable
    protected RecyclerView      recyclerView;
    //按住说话
    @Nullable
    protected VoiceRecorderView voiceRecorderView;

    //虚拟键盘(输入法)
    protected InputMethodManager imm;
    protected int ivVoiceVisiable;
    protected int ivEmojiVisiable;
    protected           int        ivPlusVisiable;
    protected           OnListener onListener;
    //键盘高度, 经我的手机测试: 手写:478 语音:477 26键:831
    public static final String     KEYBOARD_HEIGHT = "KEYBOARD_HEIGHT_FOR_CHAT_LAYOUT_ACTOR_APPLICATION";
    //延迟时间
    protected static final int DELAY_TIME = 250;
    //多少时间内的录音算太短, 默认500ms
    protected int tooShortRecording;

    //语音录制是否已取消(上滑一定距离, 并且松开)
    protected boolean                    audioRecordIsCancel;
    //是否按下了语音录制按钮
    protected boolean                    ispressedDown;
    //按下时的y坐标
    protected float                      startRecordY;
    protected AlertDialog                mPermissionDialog;
    @Nullable
    protected ViewPagerAdapter viewPagerAdapter;
    @Nullable
    protected Fragment[]       moreFragments;

    public ChatLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChatLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //                                  if 你需要自定义View, 可将↓ 的布局抄一个, 然后修改布局(id不要改, 否则找不到!)
        View inflate = View.inflate(context, R.layout.layout_for_chat_layout, this);
        //左侧"语音"
        ivVoice = inflate.findViewById(R.id.iv_voice_for_chat_layout);
        //左侧"键盘"
        ivKeyboard = inflate.findViewById(R.id.iv_keyboard_for_chat_layout);
        //中间"输入框"
        etMsg = inflate.findViewById(R.id.et_msg_for_chat_layout);
        //中间"按下说话"
        tvPressSpeak = inflate.findViewById(R.id.tv_press_speak_for_chat_layout);
        //右侧"表情"
        ivEmoji = inflate.findViewById(R.id.iv_emoji_for_chat_layout);
        //"更多⊕"和"发送"按钮
        flParent = inflate.findViewById(R.id.fl_send_plus_for_chat_layout);
        //"发送"按钮
        btnSend = inflate.findViewById(R.id.btn_send_for_chat_layout);
        //"更多⊕"
        ivSendPlus = inflate.findViewById(R.id.iv_sendplus_for_chat_layout);
        //下方ViewPager
        viewPager = inflate.findViewById(R.id.view_pager_for_chat_layout);
        //最下方TabLayout
        tabLayout = inflate.findViewById(R.id.tab_layout_for_chat_layout);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChatLayout);
            ivVoiceVisiable = typedArray.getInt(R.styleable.ChatLayout_clIvVoiceVisiable, VISIBLE) * INVISIBLE;
            ivEmojiVisiable = typedArray.getInt(R.styleable.ChatLayout_clIvEmojiVisiable, VISIBLE) * INVISIBLE;
            ivPlusVisiable = typedArray.getInt(R.styleable.ChatLayout_clIvPlusVisiable, VISIBLE) * INVISIBLE;
            Drawable background = typedArray.getDrawable(R.styleable.ChatLayout_clBtnSendBackground);
            tooShortRecording = typedArray.getInt(R.styleable.ChatLayout_clTooShortRecordingTimeMs, DELAY_TIME * 2);
            typedArray.recycle();
            //设置语音按钮是否显示
            ivVoice.setVisibility(ivVoiceVisiable);
            //表情按钮是否显示
            ivEmoji.setVisibility(ivEmojiVisiable);
            //设置右边⊕号是否显示
            ivSendPlus.setVisibility(ivPlusVisiable);
            //发送按钮
            btnSend.setVisibility(ivPlusVisiable == VISIBLE ? GONE : VISIBLE);
            //背景
            if (background != null) btnSend.setBackground(background);
        }
        //监听布局变化
        KeyboardUtils.registerSoftInputChangedListener((Activity) context, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChanged(int height) {
                if (height > 0) {
                    MMKVUtils.putInt(KEYBOARD_HEIGHT, height);
                    etMsg.requestFocus();
                    setViewPagerHeight(height);
                }
            }
        });
    }

    /**
     * 初始化
     * @param recyclerView 聊天列表, 用来设置触摸事件,响应隐藏键盘
     * @param voiceRecorderView 按住说话View
     */
    @SuppressLint("ClickableViewAccessibility")
    public void init(@Nullable RecyclerView recyclerView, @Nullable VoiceRecorderView voiceRecorderView) {
        this.recyclerView = recyclerView;
        this.voiceRecorderView = voiceRecorderView;
        if (this.recyclerView != null) {
            this.recyclerView.setOnTouchListener(new OnTouchListener() {

                private float startY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            float endY = event.getY();
                            if (Math.abs(endY - startY) < 15) {//点击
                                if (onListener != null) onListener.onRecyclerViewTouchListener(v, event);
                                etMsg.clearFocus();
                                setKeyBoardVisiable(false);
                                viewPager.setVisibility(GONE);
                            }
                            break;
                    }
                    return false;
                }
            });
        }

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        int keyboardHeight = MMKVUtils.getInt(KEYBOARD_HEIGHT, 831);
        setViewPagerHeight(keyboardHeight);
        viewPager.setVisibility(GONE);
        if (voiceRecorderView != null) voiceRecorderView.setVisibility(GONE);
    }

    /**
     * 设置下方显示的emoji & more Fragment
     * @param fragmentManager Fragment管理器, Activity中传入getSupportFragmentManager()
     * @param moreFragments 更多的Fragment, 可以使用默认的{@link com.actor.chat_layout.fragment.ChatLayoutMoreFragment}
     */
    public void setBottomFragment(FragmentManager fragmentManager, Fragment... moreFragments) {
        this.moreFragments = moreFragments;
        //emoji是否显示
        int emojiFragmentSize = ivEmojiVisiable == VISIBLE ? 1 : 0;
        viewPagerAdapter = new ViewPagerAdapter(fragmentManager, moreFragments.length + emojiFragmentSize);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (emojiFragmentSize == 1) {
            //设置 TabLayout 的 TabItem 的 Icon
            TabLayout.Tab tabAt = tabLayout.getTabAt(0);
            if (tabAt != null) {
                if (FaceManager.emojiResShowInTabLayout != null) {
                    tabAt.setIcon(FaceManager.emojiResShowInTabLayout);
                } else if (FaceManager.emojiDrawableShowInTabLayout != null) {
                    tabAt.setIcon(FaceManager.emojiDrawableShowInTabLayout);
                }
            }
        }
    }

    //ViewPager 的 Adapter
    protected class ViewPagerAdapter extends BaseFragmentStatePagerAdapter {
        protected ViewPagerAdapter(FragmentManager fm, int size) {
            super(fm, size);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //emoji是否显示
                    boolean emojiFragmentVisible = ivEmojiVisiable == VISIBLE;
                    //如果不显示
                    if (!emojiFragmentVisible) {
                        return getMoreFragmentItem(position);
                    }
                    //Emoji表情Fragment
                    ChatLayoutEmojiFragment emojiFragment = ChatLayoutEmojiFragment.newInstance();
                    emojiFragment.setOnEmojiClickListener(new ChatLayoutEmojiFragment.OnEmojiClickListener() {
                        @Override
                        public void onEmojiDelete() {
                            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
                            etMsg.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        }

                        @Override
                        public void onEmojiClick(Emoji emoji) {
                            int start = etMsg.getSelectionStart();
                            int end = etMsg.getSelectionEnd();
                            Editable editable = etMsg.getText();
                            if (start != end) editable.delete(start, end);//已选中
                            editable.insert(start, emoji.filter);
                            FaceManager.handlerEmojiText(etMsg, FaceManager.EMOJI_REGEX, editable);
                        }
                    });
                    return emojiFragment;
                default:
                    return getMoreFragmentItem(position);
            }
        }
    }

    protected Fragment getMoreFragmentItem(int position) {
        //emoji是否显示
        int emojiFragmentSize = ivEmojiVisiable == VISIBLE ? 1 : 0;
        //应该在more中取第几个
        int pos = position - emojiFragmentSize;
        if (moreFragments != null && moreFragments.length > pos) return moreFragments[pos];
        return null;
    }

    /**
     * 设置ViewPager高度
     */
    protected void setViewPagerHeight(int keyboardHeight) {
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();//设置高度和键盘高度一致
        if (params.height != keyboardHeight) {
            params.height = keyboardHeight;
            viewPager.setLayoutParams(params);
        }
    }

    /**
     * 填充完成
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //语音按钮
        ivVoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListener != null) onListener.onIvVoiceClick(ivVoice);
                //如果不设置这句,别的应用再切换过来的时候,键盘会跳出来
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                v.setVisibility(GONE);
                ivKeyboard.setVisibility(VISIBLE);
                tvPressSpeak.setVisibility(VISIBLE);
                if (ivPlusVisiable == VISIBLE) {//如果ivPlus能显示
                    btnSend.setVisibility(GONE);
                    ivSendPlus.setVisibility(VISIBLE);
                } else {//否则全隐藏,不然右侧会有个空白
                    flParent.setVisibility(GONE);
                }
                etMsg.clearFocus();
                etMsg.setVisibility(GONE);
                viewPager.setVisibility(GONE);
                setKeyBoardVisiable(false);
            }
        });

        //键盘按钮
        ivKeyboard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListener != null) onListener.onIvKeyBoardClick(ivKeyboard);
                v.setVisibility(GONE);
                ivVoice.setVisibility(VISIBLE);
                tvPressSpeak.setVisibility(GONE);
                etMsg.setVisibility(VISIBLE);
                flParent.setVisibility(VISIBLE);
                //如果ivPlus不显示 或者 EditText里有字,都要显示发送按钮
                if (ivPlusVisiable != VISIBLE || etMsg.getText().toString().length() > 0) btnSend.setVisibility(VISIBLE);
                etMsg.requestFocus();
                // 输入法弹出之后，重新调整
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                setKeyBoardVisiable(true);
            }
        });

        //语音按钮
        tvPressSpeak.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                if (onListener != null) {
                    onListener.onTvPressSpeakTouch(tvPressSpeak, event);
                    //如果语音按钮显示 && 按下录音View不为空
                    if (ivVoiceVisiable == VISIBLE && voiceRecorderView != null) {
                        if (!checkStoragePermisson(Manifest.permission.RECORD_AUDIO)) {
                            onListener.onNoPermission(Manifest.permission.RECORD_AUDIO);
                        } else {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    audioRecordIsCancel = false;
                                    ispressedDown = true;
                                    startRecordY = event.getY();
                                    voiceRecorderView.startRecording();
                                    MediaRecorderUtils.getInstance().startRecordAmr(new MediaRecorderCallback() {

                                        @Override
                                        public void recordComplete(String audioPath, long durationMs) {
                                            if (audioRecordIsCancel) {
                                                voiceRecorderView.stopRecording(View.INVISIBLE);
                                                return;
                                            }
                                            if (durationMs < tooShortRecording) {
                                                voiceRecorderView.tooShortRecording();
                                                //是否自己处理
                                                boolean delSelf = onListener.tooShortRecording(durationMs);
                                                //如果不自己处理
                                                if (!delSelf) {
                                                    voiceRecorderView.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //如果页面还没销毁&DELAY_TIME内没有再按下录音
                                                            if (voiceRecorderView != null && !ispressedDown) {
                                                                voiceRecorderView.setVisibility(View.INVISIBLE);
                                                            }
                                                        }
                                                    }, DELAY_TIME);
                                                }
                                                return;
                                            }
                                            voiceRecorderView.stopRecording(View.INVISIBLE);
                                            //语音路径
                                            String recordAudioPath = MediaRecorderUtils.getInstance().getRecordAudioPath();
                                            if (!TextUtils.isEmpty(recordAudioPath))
                                                onListener.onVoiceRecordSuccess(recordAudioPath, durationMs);
                                        }

                                        @Override
                                        public void recordCancel(String audioPath, long durationMs) {
                                            voiceRecorderView.stopRecording(View.INVISIBLE);
                                        }

                                        @Override
                                        public void recordError(@NonNull Exception e) {//子线程
                                            post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    voiceRecorderView.stopRecording(View.INVISIBLE);
                                                    onListener.onVoiceRecordError(e);
                                                }
                                            });
                                        }
                                    });
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    if (event.getY() - startRecordY < -100) {
                                        audioRecordIsCancel = true;
                                        //松开手指取消发送
                                        voiceRecorderView.release2CancelRecording();
                                    } else {
                                        //如果release2CancelRecording(), 再重新startRecording(), 否则会一直调startRecording().
                                        if (audioRecordIsCancel) {
                                            audioRecordIsCancel = false;
                                            //开始录音
                                            voiceRecorderView.startRecording();
                                        }
                                    }
                                    break;
                                case MotionEvent.ACTION_UP:
                                    ispressedDown = false;
//                                    if (event.getY() - startRecordY < -100) {
//                                        audioRecordIsCancel = true;
//                                    } else {
//                                        audioRecordIsCancel = false;
//                                    }
                                    MediaRecorderUtils.getInstance().stopRecord(audioRecordIsCancel);
                                    break;
                            }
                        }
                    }
                }
                return true;
            }
        });

        //EditText如果没有焦点的时候,onClick点击事件不回调,所以用这个onTouch事件
        etMsg.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                if (onListener != null) onListener.onEditTextToucn(etMsg, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (viewPager.getVisibility() != GONE) {
                        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    }
                    etMsg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerViewScroll2Last(300);
                            if (viewPager != null) {
                                viewPager.setVisibility(View.GONE);
                                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            }
                        }
                        // 延迟一段时间，等待输入法完全弹出
                    }, DELAY_TIME);
                }
                return true;
            }
        });

        //文字改变监听,用于切换"发送按钮"和"右侧⊕",所以ivPlus能显示时才设置监听
        if (ivPlusVisiable == VISIBLE) {
            etMsg.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(s)) {
                        ivSendPlus.setVisibility(View.VISIBLE);
                        btnSend.setVisibility(View.GONE);
                    } else {
                        ivSendPlus.setVisibility(View.GONE);
                        btnSend.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        //Emoji表情
        ivEmoji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListener != null) {
                    onListener.onIvEmojiClick(ivEmoji);
                }
                onEmoji$PlusClicked(true);
            }
        });

        //发送按钮
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListener != null) onListener.onBtnSendClick(etMsg);
            }
        });

        //右边⊕号
        ivSendPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListener != null) {
                    onListener.onIvPlusClick(ivSendPlus);
                }
                onEmoji$PlusClicked(false);
            }
        });

        if(!isInEditMode()){//造成错误的代码段
        }
    }

    //检查权限, 返回是否有权限
    public boolean checkStoragePermisson(String permisson) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ActivityCompat.checkSelfPermission(getContext(), permisson);
            return PackageManager.PERMISSION_GRANTED == permission;
        }
        return true;
    }
    //显示没有权限的对话框, 跳转设置界面
    public void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(getContext())
                    .setMessage("使用该功能，需要开启权限，鉴于您禁用相关权限，请手动设置开启权限")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Uri packageURI = Uri.parse("package:".concat(getContext().getPackageName()));
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            getContext().startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    //当 表情 or "⊕"按钮点击的时候
    protected void onEmoji$PlusClicked(boolean isClickEmoji) {
        if (ivVoiceVisiable == VISIBLE) ivVoice.setVisibility(VISIBLE);
        ivKeyboard.setVisibility(GONE);
        etMsg.setVisibility(VISIBLE);
        tvPressSpeak.setVisibility(GONE);
        flParent.setVisibility(VISIBLE);
        int selectedTabPosition = tabLayout.getSelectedTabPosition();
        //切换到某个Fragment
        if (isClickEmoji) {
            //如果当前显示的不是emoji
            if (selectedTabPosition != 0) {
                TabLayout.Tab tabAt = tabLayout.getTabAt(0);
                if (tabAt != null) tabAt.select();
            }
        } else {
            //点击"⊕"号. 如果有emoji, 并且现在正在显示
            if (ivEmojiVisiable == VISIBLE && selectedTabPosition == 0) {
                TabLayout.Tab tabAt = tabLayout.getTabAt(1);
                if (tabAt != null) tabAt.select();
            }
        }

        //如果ivPlust不显示 or EditText里有内容
        if (ivPlusVisiable != VISIBLE || etMsg.getText().toString().length() > 0) {
            btnSend.setVisibility(VISIBLE);
        }
        if (KeyboardUtils.isSoftInputVisible((Activity) getContext())) {//输入法打开状态下
            // 设置为不会调整大小，以便输入法弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            viewPager.setVisibility(VISIBLE);
            setKeyBoardVisiable(false);
            recyclerViewScroll2Last(0);
        } else {//输入法关闭状态下
            if (viewPager.getVisibility() != VISIBLE) {//bottomView是隐藏状态
                viewPager.setVisibility(VISIBLE);
                recyclerViewScroll2Last(0);
            } else {
                if (isClickEmoji) {//如果点击的是Emoji
                    if (selectedTabPosition == 0) {//已经选中了Emoji: 显示键盘, 隐藏ViewPager
                        // 设置为不会调整大小，以便输入弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
                        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        setKeyBoardVisiable(true);
                        viewPager.postDelayed(new Runnable() {
                            @Override
                            public void run() { //输入法弹出之后，重新调整
                                if (viewPager != null) {
                                    viewPager.setVisibility(View.GONE);
                                    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                }
                            }
                            // 延迟一段时间，等待输入法完全弹出
                        }, DELAY_TIME);
                        etMsg.requestFocus();
                    }
                } else {//如果点击的是⊕
                    if (selectedTabPosition != 0) {//已经选中了⊕: 显示键盘, 隐藏ViewPager
                        // 设置为不会调整大小，以便输入弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
                        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        setKeyBoardVisiable(true);
                        viewPager.postDelayed(new Runnable() {
                            @Override
                            public void run() { //输入法弹出之后，重新调整
                                if (viewPager != null) {
                                    viewPager.setVisibility(View.GONE);
                                    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                }
                            }
                            // 延迟一段时间，等待输入法完全弹出
                        }, DELAY_TIME);
                        etMsg.requestFocus();
                    }
                }
            }
        }
    }

    /**
     * 设置点击事件&其它事件的监听
     */
    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    /**
     * 获取语音ImageView
     */
    public ImageView getIvVoice() {
        return ivVoice;
    }

    /**
     * 获取键盘ImageView
     */
    public ImageView getIvKeyBoard() {
        return ivKeyboard;
    }

    /**
     * 获取输入框EditText
     */
    public EditText getEditText() {
        return etMsg;
    }

    /**
     * 获取按住说话TextView
     */
    public TextView getTvPressSpeak() {
        return tvPressSpeak;
    }

    /**
     * 获取右侧Emoji☺ImageView
     */
    public ImageView getIvEmoji() {
        return ivEmoji;
    }

    /**
     * 获取发送按钮Button
     * @return
     */
    public Button getBtnSend() {
        return btnSend;
    }

    /**
     * 获取右侧⊕ImageView
     */
    public ImageView getIvPlus() {
        return ivSendPlus;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    /**
     * 如果下方控件没有隐藏,就隐藏:if (clChatLayout.isBottomViewGone()) super.onBackPressed();
     * @return 是否已经处理完成
     */
    public boolean isBottomViewGone() {
        if (viewPager.getVisibility() != GONE) {
            viewPager.setVisibility(GONE);
            return false;
        }
        return true;
    }

    //设置键盘是否显示
    protected boolean setKeyBoardVisiable(boolean isVisiable) {
        if (isVisiable) {
            recyclerViewScroll2Last(300);
            return imm.showSoftInput(etMsg, 0);
        } else {
            return imm.hideSoftInputFromWindow(etMsg.getWindowToken(), 0);
        }
    }

    protected void setSoftInputMode(int mode) {
        Context context = getContext();
        if (context instanceof Activity) {
            ((Activity) context).getWindow().setSoftInputMode(mode);
        }
    }

    /**
     * RecyclerView滚动到最后
     * @param delay 延时多少秒后滚动到最后
     */
    protected void recyclerViewScroll2Last(int delay) {
        if (recyclerView == null) return;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null) {
            final int itemCount = adapter.getItemCount();
            if (itemCount > 0) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (recyclerView != null) {
                            recyclerView.scrollToPosition(itemCount - 1);
                        }
                    }
                }, delay);//等输入法弹出后, 再滑动到最后
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        KeyboardUtils.unregisterSoftInputChangedListener(((Activity) getContext()).getWindow());
        MediaRecorderUtils.getInstance().stopRecord(true);
        //请用户自己根据需要调用释放资源
//        MediaPlayerUtils.getInstance().stopPlayer(audioSessionId);
        super.onDetachedFromWindow();
    }
}
