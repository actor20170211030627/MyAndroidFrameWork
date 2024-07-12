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
import com.actor.others.widget.ItemRadioGroupLayout;
import com.actor.others.widget.ItemSpinnerLayout;
import com.actor.others.widget.ItemTextInputLayout;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityCustomViewBinding;
import com.actor.sample.utils.Global;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;

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

    private final String[] btns         = {"只能输入数字", "只能输入字母,数字,中文", "只能输入小写字母"};
    private final String[] regexs_input = {"[0-9]+", "[a-zA-Z0-9\u4E00-\u9FA5]+", "[a-z]+"};

    private final int dp10 = SizeUtils.dp2px(10f);
    private final int[] cardViewRadius = {0, dp10, 0, dp10};
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

        //加载CardView里的图片
        Glide.with(this).load(Global.girl).into(viewBinding.ivInCardView);
    }

//    @OnClick({R.id.btn_check, R.id.btn2, R.id.itil_can_not_input, R.id.btn_input_enable, R.id.itil_can_not_input2})
    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_enable:   //enable的图片
                viewBinding.ivSliv.setEnabled(!viewBinding.ivSliv.isEnabled());
                viewBinding.btnEnable.setText("enable=" + viewBinding.ivSliv.isEnabled());
                break;
            case R.id.btn_focus:
                boolean focusable = viewBinding.ivSliv.isFocusable();
                boolean hasFocus = viewBinding.ivSliv.hasFocus();
                LogUtils.errorFormat("focusable=%b, hasFocus=%b", focusable, hasFocus);
                if (hasFocus) {
                    viewBinding.ivSliv.clearFocus();
                } else {
                    boolean requestFocus = viewBinding.ivSliv.requestFocus();
                    LogUtils.errorFormat("requestFocus=%b", requestFocus);
                }
                viewBinding.btnFocus.setText("focus=" + viewBinding.ivSliv.hasFocus());
                break;
            case R.id.btn_pressed:  //按下时的图片
                viewBinding.ivSliv.setPressed(!viewBinding.ivSliv.isPressed());
                viewBinding.btnPressed.setText("pressed=" + viewBinding.ivSliv.isPressed());
                break;
            case R.id.btn_selected:
                viewBinding.ivSliv.setSelected(!viewBinding.ivSliv.isSelected());
                viewBinding.btnSelected.setText("selected=" + viewBinding.ivSliv.isSelected());
                break;
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

            case R.id.btn_left_top:     //↖
                cardViewRadius[0] = dp10 - cardViewRadius[0];
                viewBinding.roundCardView.setRadius(cardViewRadius[0], cardViewRadius[1], cardViewRadius[2], cardViewRadius[3]);
                break;
            case R.id.btn_right_top:     //↗
                cardViewRadius[1] = dp10 - cardViewRadius[1];
                viewBinding.roundCardView.setRadius(cardViewRadius[0], cardViewRadius[1], cardViewRadius[2], cardViewRadius[3]);
                break;
            case R.id.btn_right_bottom:     //↘
                cardViewRadius[2] = dp10 - cardViewRadius[2];
                viewBinding.roundCardView.setRadius(cardViewRadius[0], cardViewRadius[1], cardViewRadius[2], cardViewRadius[3]);
                break;
            case R.id.btn_left_bottom:     //↙
                cardViewRadius[3] = dp10 - cardViewRadius[3];
                viewBinding.roundCardView.setRadius(cardViewRadius[0], cardViewRadius[1], cardViewRadius[2], cardViewRadius[3]);
                break;

            case R.id.btn2:             //只能输入数字
                if (++pos == btns.length) pos = 0;
//                itil1.setDigits("123456", true);
                itil1.setDigitsRegex(regexs_input[pos], true);
                btn2.setText(btns[pos]);
                break;
            case R.id.itil_can_not_input:   //测试不能输入
                ToasterUtils.info("clicked!");
                break;
            case R.id.btn_input_enable: //测试切换 能/不能输入
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
