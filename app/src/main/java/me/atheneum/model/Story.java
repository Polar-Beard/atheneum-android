package me.atheneum.model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Sara on 9/20/2016.
 */
public class Story {

    private UUID storyId;
    private long date;
    private String title;
    private String description;
    private String author;
    private int viewCount;

    public Story(String title, String description, String author){
        this.title  = title;
        this.description = description;
        this.author = author;
        this.viewCount = 0;
    }

    public UUID getStoryId(){
        return storyId;
    }

    public void setStoryId(UUID storyId){
        this.storyId = storyId;
    }

    public long getDate(){
        return date;
    }

    public void setDate(long date){
        this.date = date;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public int getViewCount(){
        return viewCount;
    }

    public void setViewCount(int viewCount){
        this.viewCount = viewCount;
    }
}
