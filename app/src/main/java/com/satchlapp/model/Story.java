package com.satchlapp.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Sara on 9/20/2016.
 */
public class Story implements Serializable{

    private UUID storyId;
    private String title;
    private String description;
    private String body;
    private UUID authorId;

    public Story(String title, String description){
        this.title  = title;
        this.description = description;
    }

    public Story(){
    };

    public UUID getStoryId(){
        return storyId;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getBody(){
        return body;
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

    public void setBody(String body){
        this.body = body;
    }

    public void setAuthorId(UUID authorId){
        this.authorId = authorId;
    }
}
