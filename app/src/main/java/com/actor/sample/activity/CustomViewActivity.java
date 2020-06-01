package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;

import com.actor.myandroidframework.widget.BaseRatingBar;
import com.actor.myandroidframework.widget.BaseSpinner;
import com.actor.myandroidframework.widget.ItemRadioGroupLayout;
import com.actor.myandroidframework.widget.ItemSpinnerLayout;
import com.actor.myandroidframework.widget.ItemTextInputLayout;
import com.actor.sample.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description: 主页->自定义View
 * Author     : 李大发
 * Date       : 2019-8-27 on 17:37
 */
public class CustomViewActivity extends BaseActivity {

    @BindView(R.id.base_rating_bar)
    BaseRatingBar        baseRatingBar;
    @BindView(R.id.base_spinner)
    BaseSpinner          baseSpinner;
    @BindView(R.id.item_radio_group)
    ItemRadioGroupLayout itemRadioGroupLayout;
    @BindView(R.id.item_spinner)
    ItemSpinnerLayout    itemSpinner;
    @BindView(R.id.itil1)
    ItemTextInputLayout  itil1;
    @BindView(R.id.btn2)
    Button               btn2;

    private String[] btns = {"只能输入数字", "只能输入字母,数字,中文", "只能输入小写字母"};
    private String[] digits = {"[^0-9]", "[^a-zA-Z0-9\u4E00-\u9FA5]", "[^a-z]"};
    private int  pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);
        ButterKnife.bind(this);

        setTitle("主页->自定义View");
        baseRatingBar.setOnStarChangeListener(new BaseRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(BaseRatingBar baseRatingBar, float rating, boolean fromUser) {
                String format = getStringFormat("rating=%.2f, fromUser=%b", rating, fromUser);
                logError(format);
                toast(format);//String.valueOf(rating)
            }
        });
        itemRadioGroupLayout.setOnCheckedChangeListener(new ItemRadioGroupLayout.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId, int position, boolean reChecked) {
                String format = getStringFormat("checkedId=%d, pos=%d, reChecked=%b", checkedId, position, reChecked);
                logError(format);
                toast(format);
            }
        });
        baseSpinner.setOnItemSelectedListener(new BaseSpinner.OnItemSelectedListener2() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                logError("选中了: " + position);
                toast("选中了: " + position);
            }

            @Override
            public void onItemReSelected(AdapterView<?> parent, View view, int position, long id) {
                logError("重复选中了: " + position);
                toast("重复选中了: " + position);
            }
        });
    }

    @OnClick({R.id.btn_check, R.id.btn2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_check:
                int position = itemRadioGroupLayout.getCheckedPosition();
                itemRadioGroupLayout.setCheckedPosition(2);
                break;
            case R.id.btn2:
                if (++ pos == btns.length) pos = 0;
//                itil1.setDigits("123456", true);
                itil1.setDigitsRegex(digits[pos], true);
                btn2.setText(btns[pos]);
                break;
        }
    }
}
