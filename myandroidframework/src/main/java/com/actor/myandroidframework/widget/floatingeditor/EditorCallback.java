package com.actor.myandroidframework.widget.floatingeditor;

import android.view.View;
import android.widget.EditText;

/**
 * 创建日期：2017/9/13.
 *
 * @author kevin
 */

public interface EditorCallback {
    default void onCancel(){}//取消, 可不重写
    void onSubmit(String content);
    default void onAttached(View rootView, View cancel, View submit, EditText editText) {}
}
