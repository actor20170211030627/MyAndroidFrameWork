package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.myandroidframework.widget.BaseRadioGroup;
import com.actor.myandroidframework.widget.BaseSpinner;
import com.actor.myandroidframework.widget.ItemRadioGroupLayout;
import com.actor.myandroidframework.widget.ItemSpinnerLayout;
import com.actor.myandroidframework.widget.ItemTextInputLayout;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityCustomViewBinding;

/**
 * Description: 主页->自定义View
 * Author     : ldf
 * Date       : 2019-8-27 on 17:37
 */
public class CustomViewActivity extends BaseActivity<ActivityCustomViewBinding> {

    private BaseSpinner<String>  baseSpinner;
    private ItemRadioGroupLayout<String> itemRadioGroup;
    private ItemSpinnerLayout<String> itemSpinner;
    private ItemTextInputLayout  itil1;
    private ItemTextInputLayout  itilCanNotInput;
    private Button               btn2;

    private String[] btns         = {"只能输入数字", "只能输入字母,数字,中文", "只能输入小写字母"};
    private String[] regexs_input = {"[0-9]+", "[a-zA-Z0-9\u4E00-\u9FA5]+", "[a-z]+"};
    private int      pos          = 0;
    private boolean  inPutEnable  = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->自定义View");
        baseSpinner = viewBinding.baseSpinner;
        itemRadioGroup = viewBinding.itemRadioGroup;
        itemSpinner = viewBinding.itemSpinner;
        itil1 = viewBinding.itil1;
        itilCanNotInput = viewBinding.itilCanNotInput;
        btn2 = viewBinding.btn2;

        itemRadioGroup.setOnCheckedChangeListener(new BaseRadioGroup.OnCheckedChangeListener2() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId, int position, boolean reChecked) {
                String format = getStringFormat("checkedId=%d, pos=%d, reChecked=%b", checkedId, position, reChecked);
                LogUtils.error(format);
                ToasterUtils.info(format);
            }
        });
        baseSpinner.setOnItemSelectedListener(new BaseSpinner.OnItemSelectedListener2() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.error("选中了: " + position);
                ToasterUtils.info("选中了: " + position);
            }

            @Override
            public void onItemReSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.error("重复选中了: " + position);
                ToasterUtils.info("重复选中了: " + position);
            }
        });
    }

//    @OnClick({R.id.btn_check, R.id.btn2, R.id.itil_can_not_input, R.id.btn_input_enable, R.id.itil_can_not_input2})
    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_check:
                int checkedPosition = itemRadioGroup.getCheckedPosition();
                itemRadioGroup.setCheckedPosition(2);
                LogUtils.errorFormat("ItemRadioGroupLayout: checkedPosition=%s", checkedPosition);
                itemRadioGroup.setDatas(new String[]{"11111", "2", "33"});
                itemRadioGroup.addRadioButton("45");

                String selectedItem = baseSpinner.getSelectedItem();
                String itemAtPosition = baseSpinner.getItemAtPosition(1);
                LogUtils.errorFormat("BaseSpinner: selectedItem=%s, itemAtPosition=%s", selectedItem, itemAtPosition);

                String selectedItem1 = itemSpinner.getSelectedItem();
                String itemAtPosition1 = itemSpinner.getItemAtPosition(1);
                LogUtils.errorFormat("ItemSpinnerLayout: selectedItem1=%s, itemAtPosition1=%s", selectedItem1, itemAtPosition1);
                break;
            case R.id.btn2:
                if (++pos == btns.length) pos = 0;
//                itil1.setDigits("123456", true);
                itil1.setDigitsRegex(regexs_input[pos], true);
                btn2.setText(btns[pos]);
                break;
            case R.id.itil_can_not_input:
                ToasterUtils.info("clicked!");
                break;
            case R.id.btn_input_enable://测试切换 能/不能输入
                inPutEnable = !inPutEnable;
                itilCanNotInput.setInputEnable(inPutEnable);
                break;
            case R.id.itil_can_not_input2://不能输入, 测试点击事件
                ToasterUtils.info("被点击了!!!");
                break;
            default:
                break;
        }
    }
}
