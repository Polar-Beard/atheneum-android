package me.atheneum.model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Sara on 9/20/2016.
 */
public class Story {

    private UUID storyId;
    private String title;
    private String description;
    private UUID authorId;

    public Story(String title, String description){
        this.title  = title;
        this.description = description;
    }

    public UUID getStoryId(){
        return storyId;
    }

    public void setStoryId(UUID storyId){
        this.storyId = storyId;
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

    public UUID getAuthorId(){
        return authorId;
    }

    public void setAuthorId(UUID authorId){
        this.authorId = authorId;
    }

}
