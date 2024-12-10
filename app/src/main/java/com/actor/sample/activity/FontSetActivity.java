package com.actor.sample.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.FontUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.sample.R;
import com.actor.sample.databinding.ActivityFontSetBinding;

import java.util.Map;

/**
 * description: Font字体设置
 * company    :
 * @author    : ldf
 * date       : 2024/11/27 on 9:51
 */
public class FontSetActivity extends BaseActivity<ActivityFontSetBinding> {

    //                                  VF: Variable Fonts, 可变字体
    private final String fontAlimamaFangYuanTiVF = "fonts/AlimamaFangYuanTiVF-Thin.ttf";
    private final String fontAwesome = "fonts/FontAwesome.ttf";
    private final String fontSofia = "fonts/Sofia.otf";

    private final int fontAlimamaRes = R.font.a_limama_fang_yuan_ti_vf_thin;
    private final int fontAwesomeRes = R.font.font_awesome;
    private final int fontSofiaRes = R.font.sofia;

    private String assetPath = fontAlimamaFangYuanTiVF;
    private int fontResId = fontAlimamaRes;

    private boolean isUseAssetsFolder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Font字体设置");

        viewBinding.switchFontFrom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isUseAssetsFolder = !isChecked;
            LogUtils.errorFormat("isChecked=%b", isChecked);
        });

        Map<String, Typeface> allFonts = FontUtils.getAllFonts();
        if (allFonts != null) {
            for (Map.Entry<String, Typeface> entry : allFonts.entrySet()) {
                LogUtils.errorFormat("Font Name: %s, typeFace=%s", entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void onViewClicked(@NonNull View view) {
        super.onViewClicked(view);
        long start = System.currentTimeMillis();
        switch (view.getId()) {
            case R.id.btn_font_alimama: //AlimamaFangYuanTiVF字体
                assetPath = fontAlimamaFangYuanTiVF;
                fontResId = fontAlimamaRes;
                break;
            case R.id.btn_font_awesome: //FontAwesome字体
                assetPath = fontAwesome;
                fontResId = fontAwesomeRes;
                break;
            case R.id.btn_font_sofia:   //Sofia字体
                assetPath = fontSofia;
                fontResId = fontSofiaRes;
                break;


            case R.id.btn_set_typeface: //tv.setTypeface
                if (isUseAssetsFolder) {
                    Typeface typeface = FontUtils.createFromAsset(assetPath);
                    viewBinding.tv.setTypeface(typeface);
                } else {
                    Typeface typeface = FontUtils.getFontFromResource(fontResId);
                    viewBinding.tv.setTypeface(typeface);
                }
                break;
            case R.id.btn_bold:         //bold
                if (isUseAssetsFolder) {
                    Typeface typefaceB = FontUtils.createFromAsset(assetPath);
                    viewBinding.tv.setTypeface(typefaceB, Typeface.BOLD);
                } else {
                    Typeface typefaceB = FontUtils.getFontFromResource(fontResId);
                    viewBinding.tv.setTypeface(typefaceB, Typeface.BOLD);
                }
                ToasterUtils.warning("3种字体采用这种加粗方式, 都有问题");
                break;
            case R.id.btn_italic:       //斜体
                if (isUseAssetsFolder) {
                    Typeface typefaceI = FontUtils.createFromAsset(assetPath);
                    viewBinding.tv.setTypeface(typefaceI, Typeface.ITALIC);
                } else {
                    Typeface typefaceI = FontUtils.getFontFromResource(fontResId);
                    viewBinding.tv.setTypeface(typefaceI, Typeface.ITALIC);
                }
                break;
            case R.id.btn_bold_italic:  //bold&斜体
                if (isUseAssetsFolder) {
                    Typeface typefaceBI = FontUtils.createFromAsset(assetPath);
                    viewBinding.tv.setTypeface(typefaceBI, Typeface.BOLD_ITALIC);
                } else {
                    Typeface typefaceBI = FontUtils.getFontFromResource(fontResId);
                    viewBinding.tv.setTypeface(typefaceBI, Typeface.BOLD_ITALIC);
                }
                break;


            case R.id.btn_bold_true:
                if (isUseAssetsFolder) {
                    Typeface typefaceW = FontUtils.createFromAsset(assetPath, true, false);
                    viewBinding.tv.setTypeface(typefaceW);
                } else {
                    Typeface typefaceW = FontUtils.getFontFromResource(fontResId, true, false);
                    viewBinding.tv.setTypeface(typefaceW);
                }
                break;
            case R.id.btn_italic_true:
                if (isUseAssetsFolder) {
                    Typeface typefaceI2 = FontUtils.createFromAsset(assetPath, false, true);
                    viewBinding.tv.setTypeface(typefaceI2);
                } else {
                    Typeface typefaceI2 = FontUtils.getFontFromResource(fontResId, false, true);
                    viewBinding.tv.setTypeface(typefaceI2);
                }
                break;
            case R.id.btn_bold_italic_true:
                if (isUseAssetsFolder) {
                    Typeface typefaceWI = FontUtils.createFromAsset(assetPath, true, true);
                    viewBinding.tv.setTypeface(typefaceWI);
                } else {
                    Typeface typefaceWI = FontUtils.getFontFromResource(fontResId, true, true);
                    viewBinding.tv.setTypeface(typefaceWI);
                }
                break;


            case R.id.btn_weight:       //weight=1000
                if (isUseAssetsFolder) {
                    Typeface typefaceW = FontUtils.createFromAsset(assetPath, 1000, false);
                    viewBinding.tv.setTypeface(typefaceW);
                } else {
                    Typeface typefaceW = FontUtils.getFontFromResource(fontResId, 1000, false);
                    viewBinding.tv.setTypeface(typefaceW);
                }
                break;
            case R.id.btn_italic2:      //斜体: italic=1
                if (isUseAssetsFolder) {
                    Typeface typefaceI2 = FontUtils.createFromAsset(assetPath, 400, true);
                    viewBinding.tv.setTypeface(typefaceI2);
                } else {
                    Typeface typefaceI2 = FontUtils.getFontFromResource(fontResId, 400, true);
                    viewBinding.tv.setTypeface(typefaceI2);
                }
                break;
            case R.id.btn_weight_italic://weight=1000&italic=1(斜体)
                if (isUseAssetsFolder) {
                    Typeface typefaceWI = FontUtils.createFromAsset(assetPath, 1000, true);
                    viewBinding.tv.setTypeface(typefaceWI);
                } else {
                    Typeface typefaceWI = FontUtils.getFontFromResource(fontResId, 1000, true);
                    viewBinding.tv.setTypeface(typefaceWI);
                }
                break;


            case R.id.btn_set_default_font: //setDefaultFont
                if (isUseAssetsFolder) {
                    Typeface typefaceSDF = FontUtils.createFromAsset(assetPath, 1000, false);
                    FontUtils.setDefaultFont(typefaceSDF);
                } else {
                    Typeface typefaceSDF = FontUtils.getFontFromResource(fontResId, 1000, false);
                    FontUtils.setDefaultFont(typefaceSDF);
                }
                viewBinding.tv.setText(viewBinding.tv.getText().toString() + "-");
                break;
            default:
                break;
        }

        long end = System.currentTimeMillis();
        LogUtils.errorFormat("花费时间: %dms", end - start);
    }
}