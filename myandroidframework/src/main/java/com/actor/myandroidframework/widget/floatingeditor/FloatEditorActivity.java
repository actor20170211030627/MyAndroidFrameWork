package com.actor.myandroidframework.widget.floatingeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 浮动编辑Activity
 * https://github.com/kevin-mob/floatingeditor
 * https://www.jianshu.com/p/18279708fa46
 *
 * 使用:
 * 1.FloatEditorActivity.openDefaultEditor();
 *
 * 2.自定义view
 * FloatEditorActivity.openEditor(context, editorCallback,
 *         new EditorHolder(R.layout.fast_reply_floating_layout,//Custom layout
 *                                 R.id.tv_cancel,//cancel view id
 *                                 R.id.tv_submit,//submit view id
 *                                 R.id.et_content));//edittext id
 *
 * 创建日期：2017/9/13.
 *
 * @author kevin
 * @version 1.0
 */

public class FloatEditorActivity extends Activity implements View.OnClickListener {
    public static final String         KEY_EDITOR_HOLDER = "editor_holder";
    public static final String         KEY_EDITOR_CHECKER = "editor_checker";
    private             View           cancel;
    private             View           submit;
    private             EditText       etContent;
    private static      EditorCallback mEditorCallback;
    private             EditorHolder   holder;
    private             InputCheckRule checkRule;
    private             boolean        isClicked;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        holder = (EditorHolder) getIntent().getSerializableExtra(KEY_EDITOR_HOLDER);
        checkRule = (InputCheckRule) getIntent().getSerializableExtra(KEY_EDITOR_CHECKER);
        if(holder == null){
            throw new RuntimeException("EditorHolder params not found!");
        }
        setContentView(holder.layoutResId);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);

        initView();
        mEditorCallback.onAttached(getWindow().getDecorView(), cancel, submit, etContent);

        setEvent();
    }

    private void initView() {
        cancel = findViewById(holder.cancelViewId);
        submit = findViewById(holder.submitViewId);
        etContent = findViewById(holder.editTextId);
        int imeO = etContent.getImeOptions();//现在的IMEOptions
        //发送, 搜索, Go, 完成
        if (imeO == EditorInfo.IME_ACTION_GO || imeO == EditorInfo.IME_ACTION_SEARCH ||
                imeO == EditorInfo.IME_ACTION_SEND || imeO == EditorInfo.IME_ACTION_DONE) {
            etContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    //ACTION_UP和ACTION_DOWN时都会触发这个方法
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                        isClicked = true;
                        mEditorCallback.onSubmit(etContent.getText().toString().trim());
                        finish();
                    }
                    return false;
                }
            });
        }
    }

    private void setEvent() {
        if(cancel != null) cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    public static void openEditor(Context context, EditorCallback editorCallback, EditorHolder holder){
        openEditor(context, editorCallback, holder, null);
    }

    public static void openEditor(Context context, EditorCallback editorCallback, EditorHolder holder, InputCheckRule checkRule){
        Intent intent = new Intent(context, FloatEditorActivity.class);
//        if (!(context instanceof Activity)) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
        intent.putExtra(KEY_EDITOR_HOLDER, holder);
        intent.putExtra(KEY_EDITOR_CHECKER, checkRule);
        mEditorCallback = editorCallback;
        context.startActivity(intent);
    }

    public static void openDefaultEditor(Context context, EditorCallback editorCallback, InputCheckRule checkRule){
        openEditor(context, editorCallback, DefaultEditorHolder.createDefaultHolder(), checkRule);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == holder.cancelViewId){
            mEditorCallback.onCancel();
        }else if(id == holder.submitViewId){
            if (checkRule != null && !(checkRule.minLength == 0 && checkRule.maxLength == 0)) {
                if (!illegal()) {
                    isClicked = true;
                    mEditorCallback.onSubmit(etContent.getText().toString().trim());
                    finish();
                }

                return;
            }
            mEditorCallback.onSubmit(etContent.getText().toString().trim());
        }
        isClicked = true;
        finish();
    }

    private boolean illegal() {
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(content) || content.length() < checkRule.minLength) {
            ToastUtils.show(getString(R.string.min_input_limit_warn, checkRule.minLength));
            return true;
        }

        if (content.length() > checkRule.maxLength) {
            ToastUtils.show(getString(R.string.max_input_limit_warn, checkRule.maxLength));
            return true;
        }

        if (!TextUtils.isEmpty(checkRule.regxRule)) {
            Pattern pattern = Pattern.compile(checkRule.regxRule);
            Matcher matcher = pattern.matcher(content);
            if (!matcher.matches()) {
                ToastUtils.show(getString(checkRule.regxWarn));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isClicked){
            mEditorCallback.onCancel();
        }
//        mEditorCallback = null;//输入快了会空指针
    }
}
