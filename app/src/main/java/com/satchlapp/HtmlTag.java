package com.satchlapp;

import android.text.Html;

/**
 * Created by Sara on 1/30/2017.
 */
public class HtmlTag {
    public enum Type{
        Title, Subtitle, Bold, Italic
    }

    private Type type;
    private String tag;
    private int startPosition;
    private int length;
    private int endPosition;

    public HtmlTag(Type type){
        this.setType(type);
    }

    public void setType(Type type){
        this.type = type;
        switch (this.type) {
            case Title:
                tag = "h1";
                break;
            case Subtitle:
                tag = "h2";
                break;
            case Bold:
                tag = "b";
                break;
            case Italic:
                tag = "i";
                break;
        }
    }

    public Type getType(){
        return type;
    }

    public String getTag(){
        return tag;
    }

    public void setStartPosition(int startingPosition){
        this.startPosition = startingPosition;
    }

    public int getStartPosition(){
        return startPosition;
    }

    public void setLength(int length){
        this.length = length;
        endPosition = startPosition + length - 1;
    }
    public int getLength(){
        return length;
    }

    public int getEndPosition(){
        return endPosition;
    }
}
