package com.ly.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actor.myandroidframework.widget.BaseBottomDialog;
import com.actor.myandroidframework.widget.BaseBottomSheetDialog;
import com.actor.myandroidframework.widget.floatingeditor.EditorCallback;
import com.actor.myandroidframework.widget.floatingeditor.FloatEditorActivity;
import com.blankj.utilcode.util.ConvertUtils;
import com.ly.sample.R;
import com.ly.sample.dialog.MyBottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 主页->BottomSheetDialog
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019-9-6 on 14:24
 */
public class BottomSheetDialogActivity extends BaseActivity {

    @BindView(R.id.tv_content)
    TextView tvContent;

    private BaseBottomDialog baseBottomDialog;
    private BaseBottomSheetDialog baseBottomSheetDialog;
    private MyBottomSheetDialogFragment bottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_dialog);
        ButterKnife.bind(this);

        //几种Dialog调用的方法风格不一致, 因为没时间, 所以...



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
        TextView tvContent = baseBottomDialog.findViewById(R.id.tv_content);
        tvContent.setText("this is BaseBottomDialog, Click me(点击我试一下)");
        tvContent.setOnClickListener(v -> toast("you clicked me~"));



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
        TextView tvContent1 = baseBottomSheetDialog.findViewById(R.id.tv_content);
        tvContent1.setText("this is BaseBottomSheetDialog, Click me(点击我试一下)");



        //你也可以写个DialogFragment extends BaseBottomSheetDialogFragment,
        // 就是把所有这个DialogFragment应该有的功能写到你的DialogFragment中,
        //比如这样↓
        //实际应用中: 比如直播应用中, 送礼物界面, 你可以把 获取礼物列表&送礼物 等功能集成到这个DialogFragment中
        bottomSheetDialogFragment = new MyBottomSheetDialogFragment();
        bottomSheetDialogFragment.setPeekHeight(ConvertUtils.dp2px(100));//首次弹出高度, 可不设置
//        bottomSheetDialogFragment.setMaxHeight(ConvertUtils.dp2px(300));//最大弹出高度, 可不设置
        bottomSheetDialogFragment.setDimAmount(0.3F);//设置背景昏暗度
    }

    @OnClick({R.id.btn_bottom_dialog, R.id.btn_bottom_sheet_dialog,
            R.id.btn_bottom_sheet_dialog_fragment, R.id.btn_float_edit_activity,
            R.id.btn_bottom_activity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bottom_dialog:
                baseBottomDialog.show();
                break;
            case R.id.btn_bottom_sheet_dialog:
                baseBottomSheetDialog.show();
                break;
            case R.id.btn_bottom_sheet_dialog_fragment:
                bottomSheetDialogFragment.show(getSupportFragmentManager());
                break;
            case R.id.btn_float_edit_activity:
//                FloatEditorActivity.openEditor();//自定义view
                FloatEditorActivity.openDefaultEditor(this, new EditorCallback() {
                    @Override
                    public void onSubmit(String content) {
                        tvContent.setText(content);
                    }
                }, null);
                break;
            case R.id.btn_bottom_activity:
                startActivity(new Intent(this, MyBaseBottomActivity.class), view);
                break;
        }
    }
}
