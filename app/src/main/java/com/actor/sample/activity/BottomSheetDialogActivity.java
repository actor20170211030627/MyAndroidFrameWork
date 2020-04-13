package com.actor.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actor.myandroidframework.dialog.BaseBottomDialog;
import com.actor.myandroidframework.dialog.BaseBottomSheetDialog;
import com.actor.sample.R;
import com.actor.sample.dialog.BottomFloatEditorDialog;
import com.actor.sample.dialog.MyBottomSheetDialogFragment;
import com.actor.sample.dialog.TestDialog;
import com.blankj.utilcode.util.ConvertUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 主页->BottomSheetDialog
 * Author     : 李大发
 * Date       : 2019-9-6 on 14:24
 */
public class BottomSheetDialogActivity extends BaseActivity {

    @BindView(R.id.tv_content)
    TextView tvContent;

    private TestDialog                  alertDialog;
    private BaseBottomDialog            baseBottomDialog;
    private BaseBottomSheetDialog       baseBottomSheetDialog;
    private MyBottomSheetDialogFragment bottomSheetDialogFragment;
    private BottomFloatEditorDialog     bottomFloatEditorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_dialog);
        ButterKnife.bind(this);

        setTitle("主页->BottomSheetDialog");

        //几种Dialog调用的方法风格不一致, 因为没时间, 所以...


        alertDialog = new TestDialog(this);

        //你也可以写个Dialog extends BaseBottomDialog, 就是把所有这个Dialog应该有的功能写到你的Dialog中,
        //一处编写, 到处使用...
        baseBottomDialog = new BaseBottomDialog(this) {
            @Override
            protected int getLayoutResId() {
                return R.layout.fragment_base_bottom_sheet_dialog;
            }
        };
        baseBottomDialog.findViewById(R.id.btn_dismiss).setOnClickListener(v -> baseBottomDialog.dismiss());
        baseBottomDialog.findViewById(R.id.btn_ok).setOnClickListener(v -> toast("yes~"));
        baseBottomDialog.findViewById(R.id.tv_tips).setVisibility(View.INVISIBLE);
        baseBottomDialog.setDimAmount(0.3F);//设置背景昏暗度
        TextView tvContent1 = baseBottomDialog.findViewById(R.id.tv_content);
        tvContent1.setText("this is BaseBottomDialog, Click me(点击我试一下)");
        tvContent1.setOnClickListener(v -> toast("you clicked me~"));



        //你也可以写个Dialog extends BaseBottomSheetDialog, 就是把所有这个Dialog应该有的功能写到你的Dialog中,
        //一处编写, 到处使用...
        baseBottomSheetDialog = new BaseBottomSheetDialog(this);
        baseBottomSheetDialog.setContentView(R.layout.fragment_base_bottom_sheet_dialog);
        baseBottomSheetDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_dismiss:
                        baseBottomSheetDialog.dismiss();
                        break;
                    case R.id.btn_ok:
                        toast("ok~~");
                        break;
                    case R.id.tv_content:
                        toast("you clicked me~~~~~~");
                        break;
                }
            }
        }).setPeekHeight(ConvertUtils.dp2px(100))//首次弹出高度, 可不设置
//                .setMaxHeight(ConvertUtils.dp2px(300))//最大弹出高度, 可不设置
                .addOnclickListener(R.id.btn_dismiss)
                .setVisible(R.id.tv_tips)
                .addOnclickListener(R.id.btn_ok)
                .addOnclickListener(R.id.tv_content)
                .setDimAmount(0.3F);//设置背景昏暗度
        TextView tvContent2 = baseBottomSheetDialog.findViewById(R.id.tv_content);
        tvContent2.setText("this is BaseBottomSheetDialog, Click me(点击我试一下)");



        //你也可以写个DialogFragment extends BaseBottomSheetDialogFragment,
        // 就是把所有这个DialogFragment应该有的功能写到你的DialogFragment中,
        //比如这样↓
        //实际应用中: 比如直播应用中, 送礼物界面, 你可以把 获取礼物列表&送礼物 等功能集成到这个DialogFragment中
        bottomSheetDialogFragment = new MyBottomSheetDialogFragment();
        bottomSheetDialogFragment.setPeekHeight(ConvertUtils.dp2px(100));//首次弹出高度, 可不设置
//        bottomSheetDialogFragment.setMaxHeight(ConvertUtils.dp2px(300));//最大弹出高度, 可不设置
        bottomSheetDialogFragment.setDimAmount(0.3F);//设置背景昏暗度



        //输入框和布局悬浮在 "输入法" 之上的Dialog, 可用于输入评论.
        bottomFloatEditorDialog = new BottomFloatEditorDialog(this, new BottomFloatEditorDialog.OnResultListener() {
            @Override
            public void onResult(CharSequence content) {
                tvContent.setText(content);
            }
        });
    }

    @OnClick({R.id.btn_test_dialog, R.id.btn_bottom_dialog, R.id.btn_bottom_sheet_dialog,
            R.id.btn_bottom_sheet_dialog_fragment, R.id.btn_float_edit, R.id.btn_bottom_activity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test_dialog://普通Dialog
                alertDialog.show();
                break;
            case R.id.btn_bottom_dialog://从底部弹出的Dialog
                baseBottomDialog.show();
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
            case R.id.btn_bottom_activity://从底部弹出的Activity
                startActivity(new Intent(this, MyBaseBottomActivity.class), false, view);
                break;
        }
    }
}
