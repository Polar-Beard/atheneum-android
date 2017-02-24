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

    public void addContent(Content content){
        contents.add(content);
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

        if(content.getValue() == null){
            content.setValue("");
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

            int pos0 = Integer.parseInt(q.getSpecifications().get(0).getValue());
            int pos1 = Integer.parseInt(q.getSpecifications().get(1).getValue());

            int start;
            int end;

            if(pos0 <= pos1){
                start = pos0;
                end = pos1;
            } else{
                start = pos1;
                end = pos0;
            }

            switch(q.getType()){
                case Constants.QUALIFIER_TYPE_TEXT_BOLD:
                    parsedContent.setSpan(
                            new StyleSpan(Typeface.BOLD),
                            start,
                            end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                    break;
                case Constants.QUALIFIER_TYPE_TEXT_ITALIC:
                    parsedContent.setSpan(
                            new StyleSpan(Typeface.ITALIC),
                            start,
                            end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    break;
                case Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC:
                    parsedContent.setSpan(
                            new StyleSpan(Typeface.BOLD_ITALIC),
                            start,
                            end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    break;
                case Constants.QUALIFIER_TYPE_TEXT_TITLE:
                    parsedContent.setSpan(
                            new RelativeSizeSpan(Constants.TITLE_FLOAT_PROPORTION),
                            start,
                            end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    break;
                case Constants.QUALIFIER_TYPE_TEXT_SUBTITLE:
                    parsedContent.setSpan(
                            new RelativeSizeSpan(Constants.SUBTITLE_FLOAT_PROPORTION),
                            start,
                            end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    break;
            }
        }

        return parsedContent;
    }


}
