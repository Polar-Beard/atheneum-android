package com.satchlapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Sara on 1/30/2017.
 */
public class EditTextCursorWatcher extends EditText {

    private OnSelectionChangedListener onSelectionChangedListener;

    public EditTextCursorWatcher(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditTextCursorWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextCursorWatcher(Context context) {
        super(context);
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener){
        this.onSelectionChangedListener = listener;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if(onSelectionChangedListener != null) {
            onSelectionChangedListener.onSelectionChanged(selStart, selEnd);
        }
    }
}
