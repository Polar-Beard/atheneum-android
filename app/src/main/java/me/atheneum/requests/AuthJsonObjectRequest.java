package me.atheneum.requests;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sara on 10/20/2016.
 */
public class AuthJsonObjectRequest extends JsonObjectRequest{

    private String emailAddress;
    private String password;

    public AuthJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener){
        super(url,jsonRequest,listener,errorListener);
    }

    @Override
    public Map<String,String> getHeaders()
            throws AuthFailureError {
        HashMap<String,String> params = new HashMap<>();
        String creds = String.format("%s:%s",emailAddress,password);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        params.put("Authorization", auth);
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            Log.v("Volley", "JSONException " + response.statusCode);
            if (response.statusCode == 200)// Added for 200 response
                return Response.success(new JSONObject(),HttpHeaderParser.parseCacheHeaders(response));
            return Response.error(new ParseError(je));
        }
    }

    public void setEmailAddress(String emailAddress){
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress(){
        return emailAddress;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }
}
