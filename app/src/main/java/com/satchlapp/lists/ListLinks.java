package com.satchlapp.lists;

/**
 * Created by Sara on 9/23/2016.
 */
public class ListLinks {
    private static final String BASE_URL = "http://104.236.163.131:9000/api/";
    public static final String API_GET_STORIES = BASE_URL + "story/n/"; //Needs int num of stories
    public static final String API_USER_LOGIN = BASE_URL + "user/login";
    public static final String API_USER_REGISTER = BASE_URL + "user/register";
    public static final String API_STORY_PUBLISH = BASE_URL + "story/publish";
}
