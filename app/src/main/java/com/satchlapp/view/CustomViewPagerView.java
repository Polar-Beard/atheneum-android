package com.satchlapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Sara on 1/12/2017.
 */
public class CustomViewPagerView extends RelativeLayout {

    private boolean deleted = false;

    public CustomViewPagerView(Context context){
        super(context);
    }

    public CustomViewPagerView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public CustomViewPagerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void delete(){
        deleted = true;
    }

    public void restore(){
        deleted = false;
    }

    public boolean isDeleted(){
        return deleted;
    }
}
