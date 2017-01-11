package com.satchlapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import com.satchlapp.R;
import com.satchlapp.model.User;

/**
 * Created by Sara on 10/20/2016.
 */
public class RegisterActivity extends AppCompatActivity{

    private static final String URL_REGISTER_USER = "http://104.236.163.131:9000/api/user/register";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final RequestQueue queue = Volley.newRequestQueue(this);

        final EditText firstName = (EditText) findViewById(R.id.register_first_name);
        final EditText lastName = (EditText) findViewById(R.id.register_last_name);
        final EditText emailAddress = (EditText) findViewById(R.id.register_email_edit_text);
        final EditText password = (EditText) findViewById(R.id.register_password_edit_text);
        Button createAccountButton = (Button) findViewById(R.id.register_button);
        Button loginButton = (Button) findViewById(R.id.login_button);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                User user = new User();
                user.setFirstName(firstName.getEditableText().toString());
                user.setLastName(lastName.getEditableText().toString());
                user.setEmailAddress(emailAddress.getEditableText().toString());
                user.setPassword(password.getEditableText().toString());
                String postBody = gson.toJson(user);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(postBody);
                } catch(Exception e){
                    return;
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL_REGISTER_USER, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Context context = getApplicationContext();
                                CharSequence text = "You're registered!";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                if(networkResponse==null){
                                    System.out.println("Network Response is null");
                                } else{
                                    System.out.println(networkResponse.statusCode);
                                }
                                Context context = getApplicationContext();
                                CharSequence text = "Failed to create new account";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        }
                ){
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

                };
                queue.add(jsonObjectRequest);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
