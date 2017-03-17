package com.satchlapp.util;

/**
 * Created by Sara on 3/8/2017.
 */
public class MailgunVerifyEmailResponse {

    private String address = "";
    private String did_you_mean = "";
    private boolean is_valid = false;

    public boolean isEmailValid(){
        return is_valid;
    }

}
