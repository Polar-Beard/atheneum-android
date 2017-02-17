package com.satchlapp.model;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.satchlapp.lists.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sara on 9/20/2016.
 */
public class Story implements Serializable{

    private UUID storyId;
    private String title;
    private String description;
    private UUID authorId;
    private List<Content> contents;

    public Story(){
        this(null,null);
    }

    public Story(String title, String description){
        this.title  = title;
        this.description = description;
        contents = new ArrayList<>();
    }
    public UUID getStoryId(){
        return storyId;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public UUID getAuthorId(){
        return authorId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setStoryId(UUID storyId){
        this.storyId = storyId;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setAuthorId(UUID authorId){
        this.authorId = authorId;
    }

    //Returns the position of the new content in the array.
    public int addNewContent(){
        contents.add(new Content());
        return contents.size() - 1;
    }

    public Content getContent(int index){
        if(0 <= index && index < contents.size()) {
            return contents.get(index);
        }
        return null;
    }

    public void removeContent(int index){
        contents.remove(index);
    }

    public List<Content> getContents(){
        return contents;
    }

    public static SpannableString parseTextContent(Content content){
        //If the content is not text, return nothing
        if(content.getType() != Constants.CONTENT_TYPE_TEXT){
            return null;
        }

        SpannableString parsedContent = new SpannableString(content.getValue());

        //If the content doesn't have any formatting, return the plain string.
        if(content.getQualifiers().size() == 0){
            return parsedContent;
        }

        for(Content.Qualifier q: content.getQualifiers()){
            if(q.getSpecifications().size() == 0){
                break;
            }

            int startPos = Integer.parseInt(q.getSpecifications().get(0).getValue());
            int endPos = Integer.parseInt(q.getSpecifications().get(1).getValue());

            switch(q.getType()){
                case Constants.QUALIFIER_TYPE_TEXT_BOLD:
                    parsedContent.setSpan(
                            new StyleSpan(Typeface.BOLD),
                            startPos,
                            endPos,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                    break;
                case Constants.QUALIFIER_TYPE_TEXT_ITALIC:
                    parsedContent.setSpan(
                            new StyleSpan(Typeface.ITALIC),
                            startPos,
                            endPos,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    break;
                case Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC:
                    parsedContent.setSpan(
                            new StyleSpan(Typeface.BOLD_ITALIC),
                            startPos,
                            endPos,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    break;
                case Constants.QUALIFIER_TYPE_TEXT_TITLE:
                    parsedContent.setSpan(
                            new RelativeSizeSpan(Constants.TITLE_FLOAT_PROPORTION),
                            startPos,
                            endPos,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    break;
                case Constants.QUALIFIER_TYPE_TEXT_SUBTITLE:
                    parsedContent.setSpan(
                            new RelativeSizeSpan(Constants.SUBTITLE_FLOAT_PROPORTION),
                            startPos,
                            endPos,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    break;
            }
        }

        return parsedContent;
    }


}
