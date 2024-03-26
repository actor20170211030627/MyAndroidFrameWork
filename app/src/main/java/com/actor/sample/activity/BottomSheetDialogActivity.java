package com.actor.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.actor.myandroidframework.action.AnimAction;
import com.actor.myandroidframework.dialog.BaseBottomSheetDialog;
import com.actor.myandroidframework.dialog.BaseDialog;
import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityBottomSheetDialogBinding;
import com.actor.sample.dialog.BottomFloatEditorDialog;
import com.actor.sample.dialog.MyBottomSheetDialogFragment;
import com.actor.sample.dialog.TestDialog;
import com.blankj.utilcode.util.ConvertUtils;

/**
 * Description: 主页->BottomSheetDialog
 * Author     : ldf
 * Date       : 2019-9-6 on 14:24
 */
public class BottomSheetDialogActivity extends BaseActivity<ActivityBottomSheetDialogBinding> {

    private TestDialog                  alertDialog;
    private BaseDialog bottomDialog;
    private BaseBottomSheetDialog       baseBottomSheetDialog;
    private MyBottomSheetDialogFragment bottomSheetDialogFragment;
    private BottomFloatEditorDialog     bottomFloatEditorDialog;
    private BaseDialog leftDialog;
    private BaseDialog rightDialog;
    private BaseDialog topDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->BottomSheetDialog");

        /**
         * 普通Dialog
         */
        alertDialog = new TestDialog(this);


        /**
         * 从底部弹出的Dialog
         */
        bottomDialog = new BaseDialog(this) {
            @Override
            protected int getLayoutResId() {
                return R.layout.fragment_base_bottom_sheet_dialog;
            }
        }.setGravityAndAnimation(Gravity.BOTTOM, AnimAction.ANIM_BOTTOM_SLIDE)
        //设置背景昏暗度
        .setDimAmount(0.3f)
        //设置点击穿透
        .isClickThrough(true)
        .setCancelAble(false);
        //
        bottomDialog.findViewById(R.id.btn_dismiss).setOnClickListener(v -> bottomDialog.dismiss());
        bottomDialog.findViewById(R.id.btn_ok).setOnClickListener(v -> ToasterUtils.success("yes~"));
        bottomDialog.findViewById(R.id.tv_tips).setVisibility(View.INVISIBLE);
        TextView tvContent1 = bottomDialog.findViewById(R.id.tv_content);
        tvContent1.setText("this is BottomDialog, Click me(点击我试一下)");
        tvContent1.setOnClickListener(v -> ToasterUtils.info("you clicked me~"));


        /**
         * 从底部弹出的Dialog, 可上下滑动
         * 可以写个Dialog extends BaseBottomSheetDialog, 把所有这个Dialog应该有的功能写到你的Dialog中,
         * 一处编写, 到处使用...
         */
        baseBottomSheetDialog = new BaseBottomSheetDialog(this) {
            @Override
            protected int getLayoutResId() {
                return R.layout.fragment_base_bottom_sheet_dialog;
            }
        };
        baseBottomSheetDialog.findViewById(R.id.btn_dismiss).setOnClickListener(v -> baseBottomSheetDialog.dismiss());
        baseBottomSheetDialog.findViewById(R.id.btn_ok).setOnClickListener(v -> ToasterUtils.success("ok~~"));
        baseBottomSheetDialog.setPeekHeight(ConvertUtils.dp2px(100));//首次弹出高度, 可不设置
        baseBottomSheetDialog.setDimAmount(0.3F);//设置背景昏暗度
        TextView tvContent2 = baseBottomSheetDialog.findViewById(R.id.tv_content);
        tvContent2.setText("this is BaseBottomSheetDialog, Click me(点击我试一下)");
        tvContent2.setOnClickListener(v -> ToasterUtils.info("you clicked me~~~~~~"));


        /**
         * 从底部弹出的DialogFragment, 可上下滑动
         * 可以写个DialogFragment extends BaseBottomSheetDialogFragment,
         * 把所有这个DialogFragment应该有的功能写到你的DialogFragment中, 比如这样↓
         */
        bottomSheetDialogFragment = new MyBottomSheetDialogFragment();
        bottomSheetDialogFragment.setPeekHeight(ConvertUtils.dp2px(100));//首次弹出高度, 可不设置
//        bottomSheetDialogFragment.setMaxHeight(ConvertUtils.dp2px(300));//最大弹出高度, 可不设置
        bottomSheetDialogFragment.setDimAmount(0.3F);//设置背景昏暗度


        /**
         * 输入框和布局悬浮在 "输入法" 之上的Dialog, 可用于输入评论.
         */
        bottomFloatEditorDialog = new BottomFloatEditorDialog(this, new BottomFloatEditorDialog.OnResultListener() {
            @Override
            public void onResult(CharSequence content) {
                viewBinding.tvContent.setText(content);
            }
        });

        /**
         * 左侧弹出的Dialog
         */
        leftDialog = new BaseDialog(this) {
            @Override
            protected int getLayoutResId() {
                return R.layout.dialog_base_left;
            }
        }.setGravityAndAnimation(Gravity.START, AnimAction.ANIM_LEFT_SLIDE)
                .setWidth(ConfigUtils.APP_SCREEN_WIDTH / 3 * 2)
                .setHeightFullScreen(true, true);
        leftDialog.findViewById(R.id.btn_dismiss).setOnClickListener(v -> leftDialog.dismiss());


        /**
         * 右侧弹出的Dialog
         */
        rightDialog = new BaseDialog(this) {
            @Override
            protected int getLayoutResId() {
                return R.layout.dialog_base_left;
            }
        }.setGravityAndAnimation(Gravity.END, AnimAction.ANIM_RIGHT_SLIDE)
                .setWidth(ConfigUtils.APP_SCREEN_WIDTH / 3 * 2)
                .setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        rightDialog.findViewById(R.id.btn_dismiss).setOnClickListener(v -> rightDialog.dismiss());


        /**
         * 右侧弹出的Dialog
         */
        topDialog = new BaseDialog(this) {
            @Override
            protected int getLayoutResId() {
                return R.layout.fragment_base_bottom_sheet_dialog;
            }
        }.setGravityAndAnimation(Gravity.TOP, AnimAction.ANIM_TOP_SLIDE);
        topDialog.findViewById(R.id.tv_tips).setVisibility(View.INVISIBLE);
        topDialog.findViewById(R.id.btn_dismiss).setOnClickListener(v -> rightDialog.dismiss());
        topDialog.findViewById(R.id.btn_ok).setOnClickListener(v -> ToasterUtils.success("ok~~"));
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test_dialog://普通Dialog
                alertDialog.show();
                break;
            case R.id.btn_bottom_sheet_dialog://从底部弹出, 可上下滑动的Dialog
                baseBottomSheetDialog.show();
                break;
            case R.id.btn_bottom_sheet_dialog_fragment://从底部弹出, 可上下滑动的DialogFragment
                bottomSheetDialogFragment.show(getSupportFragmentManager());
                break;
            case R.id.btn_float_edit://悬浮输入Dialog, 可用于评论等.
                bottomFloatEditorDialog.show();
                break;
            case R.id.btn_left_dialog://左侧弹出
                leftDialog.show();
                break;
            case R.id.btn_right_dialog://右侧弹出
                rightDialog.show();
                break;
            case R.id.btn_top_dialog://
                topDialog.show();
                break;
            case R.id.btn_bottom_dialog1://从底部弹出的Dialog
                bottomDialog.show();
                break;
            case R.id.btn_bottom_activity://从底部弹出的Activity
                //不要弄元素共享动画, 否则动画有问题
                startActivity(new Intent(this, MyBaseBottomActivity.class)/*, false, null, null, view*/);
                //需要重写进入动画, 从底部弹出
                overridePendingTransition(R.anim.bottom_slide_in, 0);
                break;
            default:
                break;
        }
    }
}
