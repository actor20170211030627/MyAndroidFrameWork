package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.actor.myandroidframework.widget.ItemTextInputLayout;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityIsEmptyBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 主页->判空
 * Author     : ldf
 * Date       : 2019-9-6 on 17:47
 */
public class IsEmptyActivity extends BaseActivity<ActivityIsEmptyBinding> implements CompoundButton.OnCheckedChangeListener {

    private EditText            etContent;
    private TextInputLayout     textInputLayout;
    /**
     * 注意: {@link ItemTextInputLayout} implements {@link com.actor.myandroidframework.utils.TextUtils2.GetTextAble}
     */
    private ItemTextInputLayout itilPhone;

    private String string;
    private String[] arrays;
    private final List<String> list = new ArrayList<>(1);
    private final Map<Object, Object> map = new HashMap<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->判空");
        etContent = viewBinding.etContent;
        textInputLayout = viewBinding.textInputLayout;
        itilPhone = viewBinding.itilPhone;
        //选中时间
        viewBinding.switchString.setOnCheckedChangeListener(this);
        viewBinding.switchArray.setOnCheckedChangeListener(this);
        viewBinding.switchCollection.setOnCheckedChangeListener(this);
        viewBinding.switchMap.setOnCheckedChangeListener(this);
    }

//    @OnCheckedChanged({R.id.switch_string, R.id.switch_array, R.id.switch_collection, R.id.switch_map})
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        switch (buttonView.getId()) {
            case R.id.switch_string:
                string = isChecked ? null : "\"aa\"";
                buttonView.setText(getStringFormat("string = %s", string));
                break;
            case R.id.switch_array:
                arrays = new String[isChecked ? 0 : 1];
                buttonView.setText(getStringFormat("arrays = new String[%d];", arrays.length));
                break;
            case R.id.switch_collection:
                if (isChecked) {
                    list.clear();
                } else list.add("Not Null");
                buttonView.setText(getStringFormat("list.size() = %d", list.size()));
                break;
            case R.id.switch_map:
                if (isChecked) {
                    map.clear();
                } else map.put("key", "Not Null");
                buttonView.setText(getStringFormat("map.size() = %d", map.size()));
                break;
        }
    }

//    @OnClick(R.id.btn_is_empty)
    public void onViewClicked(View view) {
        /**
         * @param objs 参数的类型为:
         * <ol>
         *      <li>{@link TextView}</li>
         *      <li>{@link TextInputLayout}</li>
         *      <li>{@link com.actor.myandroidframework.utils.TextUtils2.GetTextAble}</li>
         *      <li>{@link CharSequence}</li>
         *      <li>{@link Array}</li>
         *      <li>{@link Collection}</li>
         *      <li>{@link Map}</li>
         * </ol>
         * @return 都不为空, 返回true
         */
        //如果String, Array, List, Map 不需要提示的话, 也可以这样一次性判断
//        if (isNoEmpty(etContent, textInputLayout, itilPhone, string, arrays, list, map)) {
        if (isNoEmpty(etContent, textInputLayout, itilPhone) &&
                isNoEmpty(string, "string = null了啊..") &&
                isNoEmpty(arrays, "arrays里没有元素哟") &&
                isNoEmpty(list, "list里没有数据") &&
                isNoEmpty(map, "map里没有数据")) {
            showToast("恭喜, 都不为空!");
        }
    }
}
