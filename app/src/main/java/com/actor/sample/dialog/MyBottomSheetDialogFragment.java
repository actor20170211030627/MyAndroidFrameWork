package com.actor.sample.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.fragment.BaseBottomSheetDialogFragment;
import com.actor.sample.R;
import com.blankj.utilcode.util.ToastUtils;

/**
 * Description: 类的描述
 * Author     : ldf
 * Date       : 2019-8-13 on 13:15
 *
 * @version 1.0
 */
public class MyBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

    private TextView tvContent;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_base_bottom_sheet_dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvContent = view.findViewById(R.id.tv_content);
        tvContent.setText("this is BaseBottomSheetDialogFragment,\n你可以请求网络等,\n Click me!(点击我试一下)");
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("clicked in BaseBottomSheetDialogFragment!");
            }
        });
        view.findViewById(R.id.btn_ok).setOnClickListener(v -> ToastUtils.showShort("ok~"));
        view.findViewById(R.id.btn_dismiss).setOnClickListener(v -> dismiss());
    }

    //request internet请求网络等...
    private void requestInternet() {
//        MyOkHttpUtils.post(.....) {
//              @override
//                onOk(Response result) {
//                    tv.setText(result.xxx);
//                }
//        };
    }
}
