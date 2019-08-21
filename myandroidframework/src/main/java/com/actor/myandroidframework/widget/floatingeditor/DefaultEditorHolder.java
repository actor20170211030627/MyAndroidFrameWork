package com.actor.myandroidframework.widget.floatingeditor;

import com.actor.myandroidframework.R;

/**
 * Created by like on 2017/9/18.
 */

public class DefaultEditorHolder {

    public static final int DEFAULT_LAYOUT = R.layout.activity_float_editor;
    public static final int DEFAULT_TITLE = R.id.tv_title_for_float_edit_activity;
    public static final int DEFAULT_ID_CANCEL = R.id.tv_cancel_for_float_edit_activity;
    public static final int DEFAULT_ID_SEND = R.id.tv_send_for_float_edit_activity;
    public static final int DEFAULT_ID_WRITE = R.id.et_content_for_float_edit_activity;

    public static EditorHolder createDefaultHolder() {
        return new EditorHolder(DEFAULT_LAYOUT, DEFAULT_ID_CANCEL, DEFAULT_ID_SEND, DEFAULT_ID_WRITE);
    }


}
