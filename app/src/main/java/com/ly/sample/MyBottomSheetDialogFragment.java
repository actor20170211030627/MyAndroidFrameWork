package com.ly.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.actor.myandroidframework.utils.ToastUtils;
import com.actor.myandroidframework.widget.BaseBottomSheetDialogFragment;

/**
 * Description: 类的描述
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019-8-13 on 13:15
 *
 * @version 1.0
 */
public class MyBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

    private TextView tv;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_base_bottom_sheet_dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv = view.findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show("clicked in BaseBottomSheetDialogFragment!");
            }
        });
        view.findViewById(R.id.btn_ok).setOnClickListener(v -> ToastUtils.show("ok~"));
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
