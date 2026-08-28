package com.actor.sample.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.sample.databinding.ActivityLogTestBinding;

/**
 * description: LogUtils 测试
 * company    :
 * @author    : ldf
 * date       : 2026/8/25 on 16:35
 */
public class LogTestActivity extends BaseActivity<ActivityLogTestBinding> {

    private final String[] msgs = {
            "\n1行短日志: formatNumber = %s, 1行短日志1行短日志1行短日志end\n",
            "\n\n1行长日志: formatNumber = %s, 1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志1行长日志end",
            "\n\n\n2行短日志: formatNumber = %s, 2行短日志\n2行短日志end",
            "2行长日志: formatNumber = %s, 2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志end0\n2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志2行长日志end1",
            "多行短日志: formatNumber = %s, 多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志\n多行短日志end",
            "多行长日志: formatNumber = %s, 多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志end0\n" +
                    "多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志end1\n" +
                    "多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志多行长日志end2"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("LogUtils 测试");

        //默认选中 Log.ERROR
        viewBinding.islMsgLevel.setSelectedItemPosition(4);

        IllegalStateException exception = new IllegalStateException("IllegalStateException Test!!!!!!!!!!!!!");

        viewBinding.btnClear.setOnClickListener(v -> viewBinding.itilFormat.setText(""));

        viewBinding.btnPrint.setOnClickListener(v -> {
            int level = viewBinding.islMsgLevel.getSelectedItemPosition() + 2;
            int type = viewBinding.islMsgType.getSelectedItemPosition();
            boolean hasException = viewBinding.irglException.getCheckedPosition() == 1;
            String formatNumber = viewBinding.itilFormat.getText().toString();  //格式化数字
            viewBinding.vstvMsg.setText(msgs[type]);
            switch (level) {
                case Log.VERBOSE:
                    if (TextUtils.isEmpty(formatNumber)) {
                        if (hasException) LogUtils.verbose(msgs[type], exception);
                        else LogUtils.verbose(msgs[type]);
                    } else {
                        if (hasException) LogUtils.verboseFormat(exception, msgs[type], formatNumber);
                        else LogUtils.verboseFormat(msgs[type], formatNumber);
                    }
                break;
                case Log.DEBUG:
                    if (TextUtils.isEmpty(formatNumber)) {
                        if (hasException) LogUtils.debug(msgs[type], exception);
                        else LogUtils.debug(msgs[type]);
                    } else {
                        if (hasException) LogUtils.debugFormat(exception, msgs[type], formatNumber);
                        else LogUtils.debugFormat(msgs[type], formatNumber);
                    }
                    break;
                case Log.INFO:
                    if (TextUtils.isEmpty(formatNumber)) {
                        if (hasException) LogUtils.info(msgs[type], exception);
                        else LogUtils.info(msgs[type]);
                    } else {
                        if (hasException) LogUtils.infoFormat(exception, msgs[type], formatNumber);
                        else LogUtils.infoFormat(msgs[type], formatNumber);
                    }
                    break;
                case Log.WARN:
                    if (TextUtils.isEmpty(formatNumber)) {
                        if (hasException) LogUtils.warn(msgs[type], exception);
                        else LogUtils.warn(msgs[type]);
                    } else {
                        if (hasException) LogUtils.warnFormat(exception, msgs[type], formatNumber);
                        else LogUtils.warnFormat(msgs[type], formatNumber);
                    }
                    break;
                case Log.ERROR:
                    if (TextUtils.isEmpty(formatNumber)) {
                        if (hasException) LogUtils.error(msgs[type], exception);
                        else LogUtils.error(msgs[type]);
                    } else {
                        if (hasException) LogUtils.errorFormat(exception, msgs[type], formatNumber);
                        else LogUtils.errorFormat(msgs[type], formatNumber);
                    }
                    break;
                case Log.ASSERT:
                    if (TextUtils.isEmpty(formatNumber)) {
                        if (hasException) LogUtils.assert_(msgs[type], exception);
                        else LogUtils.assert_(msgs[type]);
                    } else {
                        if (hasException) LogUtils.assertFormat(exception, msgs[type], formatNumber);
                        else LogUtils.assertFormat(msgs[type], formatNumber);
                    }
                    break;
            default:
                break;
            }
        });

        viewBinding.btnPrintlnStackTrance.setOnClickListener(v -> {
            int level = viewBinding.islMsgLevel.getSelectedItemPosition() + 2;
            LogUtils.printlnStackTrance(level);
        });
    }
}