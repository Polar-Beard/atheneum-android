package com.satchlapp.util;

import java.security.SecureRandom;

/**
 * Created by Sara on 2/24/2017.
 */
public class Tools {
    static final String AB = "0123456789abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}
