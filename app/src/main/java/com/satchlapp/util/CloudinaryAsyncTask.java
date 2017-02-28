package com.satchlapp.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.satchlapp.lists.SecretKeys;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sara on 2/27/2017.
 */
public class CloudinaryAsyncTask extends AsyncTask<Map,Void,Void> {

    private Cloudinary cloudinary;

    public CloudinaryAsyncTask(){

        //Configure Cloudinary
        Map config = new HashMap();
        config.put("cloud_name", SecretKeys.CLOUDINARY_CLOUD_NAME);
        config.put("api_key", SecretKeys.CLOUDINARY_API_KEY);
        config.put("api_secret", SecretKeys.CLOUDINARY_API_SECRET);
        cloudinary = new Cloudinary(config);
    }

    @Override
    protected Void doInBackground(Map... maps){
        Map<String,InputStream> images = maps[0];
        for(String imageId: images.keySet()){
            try {
                Map uploadResult = cloudinary.uploader().upload(images.get(imageId)
                        ,ObjectUtils.asMap("public_id", imageId));
                System.out.println(uploadResult.toString());
            } catch(IOException e){
                Log.e("CloudinaryAsyncTask", e.toString());
            }
        }
        return null;
    }



}
