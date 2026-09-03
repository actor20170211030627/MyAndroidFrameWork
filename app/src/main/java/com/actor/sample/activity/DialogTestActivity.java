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
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityDialogTestBinding;
import com.actor.sample.dialog.BottomFloatEditorDialog;
import com.actor.sample.dialog.MyBottomSheetDialogFragment;
import com.actor.sample.dialog.TestDialog;
import com.actor.sample.fragment.MyDialogFragment;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SizeUtils;

/**
 * Description: 主页->BottomSheetDialog
 * Author     : ldf
 * Date       : 2019-9-6 on 14:24
 */
public class DialogTestActivity extends BaseActivity<ActivityDialogTestBinding> {

    private final int[]    widths     = {WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, SizeUtils.dp2px(250)};
    private final int[]    heights    = {WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, SizeUtils.dp2px(300)};
    private final int[]    gravities  = {Gravity.CENTER, Gravity.START, Gravity.TOP, Gravity.END, Gravity.BOTTOM};
    private final int[]    animations = {AnimAction.ANIM_DEFAULT, AnimAction.ANIM_LEFT_SLIDE, AnimAction.ANIM_TOP_SLIDE, AnimAction.ANIM_RIGHT_SLIDE, AnimAction.ANIM_BOTTOM_SLIDE};
    private final String[] messages   = {"Test", "所发生的反馈", null};
    private       int      messagePos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->BottomSheetDialog");
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test_dialog://普通Dialog
                new TestDialog(this).show();
                break;
            case R.id.btn_bottom_sheet_dialog://从底部弹出, 可上下滑动的Dialog
                BaseBottomSheetDialog baseBottomSheetDialog = new BaseBottomSheetDialog(this) {
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
                baseBottomSheetDialog.show();
                break;


            case R.id.btn_dialog_fragment://Dialog样式的Fragment
                new MyDialogFragment()
                        .setCancelAble(true)
                        .show(getSupportFragmentManager());
                break;
            case R.id.btn_bottom_sheet_dialog_fragment://从底部弹出, 可上下滑动的DialogFragment
                MyBottomSheetDialogFragment bottomSheetDialogFragment = new MyBottomSheetDialogFragment();
                bottomSheetDialogFragment.setPeekHeight(ConvertUtils.dp2px(100));//首次弹出高度, 可不设置
//                bottomSheetDialogFragment.setMaxHeight(ConvertUtils.dp2px(300));//最大弹出高度, 可不设置
                bottomSheetDialogFragment.setDimAmount(0.3F);//设置背景昏暗度
                bottomSheetDialogFragment.show(getSupportFragmentManager());
                break;


            case R.id.btn_float_edit://悬浮输入Dialog, 可用于评论等.
                new BottomFloatEditorDialog(this, content -> {
                    viewBinding.tvContent.setText(content);
                }).show();
                break;
            case R.id.btn_show_loading_dialog:
                messagePos ++;
                if (messagePos >= messages.length) messagePos = 0;
                getNetWorkLoadingDialog()
                        .setMessage(messages[messagePos])
                        .setCancelAble(false)
                        .setClickThrough(true)
                        .show();
                break;
            case R.id.btn_dismiss_loading_dialog:
                getNetWorkLoadingDialog().dismiss();
                break;
            case R.id.btn_bottom_activity://从底部弹出的Activity
                //不要弄元素共享动画, 否则动画有问题
                startActivity(new Intent(this, MyBaseBottomActivity.class)/*, view*/);
                //需要重写进入动画, 从底部弹出
                overridePendingTransition(com.actor.myandroidframework.R.anim.bottom_slide_in, 0);
                break;


            case R.id.btn_show_dialog_test:     //各种属性测试
                int animPos = viewBinding.irglOrientation.getCheckedPosition();
                BaseDialog baseDialog = new BaseDialog(this) {
                    @Override
                    protected int getLayoutResId() {
                        return R.layout.dialog_test;
                    }
                }.setWidth(widths[viewBinding.islWidth.getSelectedItemPosition()])
                 .setHeight(heights[viewBinding.islHeight.getSelectedItemPosition()])
                 .setCancelAble(viewBinding.islCancelableOnBackPressed.isChecked(), viewBinding.islCancelableOnTouchOutside.isChecked())
                 .setDimEnable(viewBinding.islDimEnable.isChecked())
                 .setClickThrough(viewBinding.islClickThrough.isChecked())
                 .setDrawIntoStatusBar(viewBinding.islIsDrawIntoStatusBar.isChecked(), viewBinding.islStatusBarHide.isChecked())
                 .setDrawIntoNavigationBar(viewBinding.islIsDrawIntoNavigationBar.isChecked(), viewBinding.islNavigationBarHide.isChecked())
                 .setGravityAndAnimation(gravities[animPos], animations[animPos]);
                baseDialog.findViewById(R.id.btn_dismiss).setOnClickListener(v -> baseDialog.dismiss());
                baseDialog.findViewById(R.id.btn_ok).setOnClickListener(v -> ToasterUtils.success("Ok~"));
                baseDialog.show();
                break;
            default:
                break;
        }
    }
}
