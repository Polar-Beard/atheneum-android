package com.satchlapp.model;

import java.util.List;

/**
 * Created by Sara on 3/9/2017.
 */
public class Category {

    public static final int DISPLAY_TYPE_DETAILED = 0x00000001;
    public static final int DISPLAY_TYPE_SIMPLE = 0x00000002;

    private String title;
    private List<Story> storyList;
    private int displayType;

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setStoryList(List<Story> storyList){
        this.storyList = storyList;
    }

    public List<Story> getStoryList(){
        return storyList;
    }

    public void setDisplayTypeDetailed(){
        displayType = DISPLAY_TYPE_DETAILED;
    }

    public void setDisplayTypeSimple(){
        displayType = DISPLAY_TYPE_SIMPLE;
    }

    public int getDisplayType(){
        return displayType;
    }
}
