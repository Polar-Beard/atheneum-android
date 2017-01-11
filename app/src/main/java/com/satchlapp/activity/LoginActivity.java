package com.satchlapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.satchlapp.R;
import com.satchlapp.util.TextStyler;

/**
 * Created by Sara on 10/10/2016.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String URL_LOGIN = "http://104.236.163.131:9000/api/user/login";
    private static final String PREFS_NAME = "CredentialPrefsFile";

    private TextView textViewTitle;
    private TextView taglineTextView;
    private Button registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface fontBold = Typeface.createFromAsset(getAssets(),"fonts/Merriweather-Bold.ttf");
        Typeface fontLight = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Light.ttf");

        textViewTitle = (TextView) findViewById(R.id.activityLoginTextViewTitle);
        SpannableString title = TextStyler.applySpacing("Satchl", 1.5f);
        textViewTitle.setText(title, TextView.BufferType.SPANNABLE);
        textViewTitle.setTypeface(fontBold);

        taglineTextView = (TextView) findViewById(R.id.activityLoginTextViewTagline);
        taglineTextView.setTypeface(fontLight);

        registerButton = (Button) findViewById(R.id.activityLoginButtonRegister);



        final RequestQueue queue = Volley.newRequestQueue(this);

        //final EditText emailInput = (EditText) findViewById(R.id.login_email_edit_text);
        //final EditText passwordInput = (EditText) findViewById(R.id.login_password_edit_text);
        //Button loginButton = (Button) findViewById(R.id.login_button);
        //Button registerButton = (Button) findViewById(R.id.register_button);

        /*loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailAddress = emailInput.getEditableText().toString();
                final String password = passwordInput.getEditableText().toString();

                AuthStringRequest stringRequest = new AuthStringRequest(URL_LOGIN,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response){
                                saveCredentials(emailAddress,password);
                                Context context = getApplicationContext();
                                CharSequence text = "Successfully logged in!";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context,text,duration);
                                toast.show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                Context context = getApplicationContext();
                                CharSequence text = "Failed to log in";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context,text,duration);
                                toast.show();
                            }
                        }
                );
                stringRequest.setEmailAddress(emailAddress);
                stringRequest.setPassword(password);
                queue.add(stringRequest);
            }
        });*/

        /*registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });*/

    }

    private void saveCredentials(String emailAddress, String password){
        SharedPreferences credentials = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = credentials.edit();
        editor.putString("emailAddress", emailAddress);
        editor.putString("password", password);
        editor.commit();
    }
}
