package com.actor.sample.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.actor.myandroidframework.utils.FileUtils;
import com.actor.myandroidframework.utils.jExcel.JExcelApiUtils;
import com.actor.myandroidframework.utils.PermissionRequestUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.actor.sample.R;
import com.actor.sample.bean.ExcelBean;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 导出到Excel
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/11/5 on 10:45
 */
public class Export2ExcelActivity extends BaseActivity {

    @BindView(R.id.tv_result_qq)//显示结果
            TextView tvResult;

    private String[]        row1Titles = {"姓名", "年龄", "男孩", "日期"};//第一行标题
    private List<ExcelBean> items      = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export2_excel);
        ButterKnife.bind(this);

        setTitle("Excel导出");
        items.add(new ExcelBean("张三", 10, true, new Date()));
        items.add(new ExcelBean("小红", 12, false, new Date()));
        items.add(new ExcelBean("李四", 18, true, new Date()));
        items.add(new ExcelBean("王五", 13, false, new Date()));
    }

    @OnClick(R.id.btn)
    public void onViewClicked() {
        exportExcel();
    }

    private void exportExcel() {
        String time = TimeUtils.date2String(new Date(), "yyyyMMddHHmmss").concat(".xls");
        String filePath = FileUtils.getExternalStoragePath(time);

        //高版本手机写文件必须要权限
        PermissionRequestUtils.requestPermissionGroup(this, new PermissionRequestUtils.PermissionCallBack() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccessful(@NonNull List<String> deniedPermissions) {
                showLoadingDialog();
                JExcelApiUtils.initExcel(filePath, "测试表格顶部标题", row1Titles);
                JExcelApiUtils.export2Excel(items, filePath, new JExcelApiUtils.OnExportChangeListener() {
                    @Override
                    public void onExportFinish(String filePath, int id) {
                        dismissLoadingDialog();
                        tvResult.setText("Excel已导出至：" + filePath);
                        JExcelApiUtils.shareFile(activity, filePath);//分享
                    }

                    @Override
                    public void onExportError(String filePath, int id, Exception e) {
                        e.printStackTrace();
                        dismissLoadingDialog();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull List<String> deniedPermissions) {
                toast("没有权限");
            }
        }, Permission.Group.STORAGE);
    }
}
