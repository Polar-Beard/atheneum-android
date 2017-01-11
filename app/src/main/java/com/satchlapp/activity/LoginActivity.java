package com.satchlapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.satchlapp.R;
import com.satchlapp.util.TextStyler;
import com.satchlapp.view.CustomViewPager;

/**
 * Created by Sara on 10/10/2016.
 */
public class LoginActivity extends AppCompatActivity {

    public enum SecondPage{
        Default,Register, Login
    }

    private static final String URL_LOGIN = "http://104.236.163.131:9000/api/user/login";
    private static final String PREFS_NAME = "CredentialPrefsFile";

    private TextView viewLoginMainTextViewTitle;
    private TextView viewLoginMainTextViewTagline;
    private Button registerButton;
    private TextView viewLoginMainTextViewSignIn;
    private CustomViewPager viewPager;
    private LoginPagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewPager = (CustomViewPager) findViewById(R.id.activityLoginViewPager);
        viewPager.setPagingEnabled(false);
        pagerAdapter = new LoginPagerAdapter();
        viewPager.setAdapter(pagerAdapter);


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

    private void saveCredentials(String emailAddress, String password) {
        SharedPreferences credentials = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = credentials.edit();
        editor.putString("emailAddress", emailAddress);
        editor.putString("password", password);
        editor.commit();
    }

    private class LoginPagerAdapter extends PagerAdapter {
        private static final int NUM_PAGES = 2;

        private Typeface fontBold;
        private Typeface fontLight;

        private SecondPage secondPage = SecondPage.Default;

        public LoginPagerAdapter() {
            super();
            fontBold = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Bold.ttf");
            fontLight = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Light.ttf");
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = (LayoutInflater) collection.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = null;
            if (position == 0) {
                view = inflater.inflate(R.layout.view_login_main, null);

                viewLoginMainTextViewTitle = (TextView) view.findViewById(R.id.viewLoginMainTextViewTitle);
                SpannableString title = TextStyler.applySpacing("Satchl", 1.5f);
                viewLoginMainTextViewTitle.setText(title, TextView.BufferType.SPANNABLE);
                viewLoginMainTextViewTitle.setTypeface(fontBold);

                viewLoginMainTextViewTagline = (TextView) view.findViewById(R.id.viewLoginMainTextViewTagline);
                viewLoginMainTextViewTagline.setTypeface(fontLight);

                registerButton = (Button) view.findViewById(R.id.viewLoginMainButtonRegister);
                registerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        secondPage = SecondPage.Register;
                        viewPager.setCurrentItem(1, true);
                    }
                });

                viewLoginMainTextViewSignIn = (TextView) view.findViewById(R.id.viewLoginMainTextViewSignIn);
                viewLoginMainTextViewSignIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        secondPage = SecondPage.Login;
                        viewPager.setCurrentItem(1, true);
                    }
                });
            } else{
                switch (secondPage){
                    case Register:
                        view = inflater.inflate(R.layout.view_register,null);
                        break;
                    case Login:
                       view = inflater.inflate(R.layout.view_login,null);
                        break;
                    case Default:
                        view = inflater.inflate(R.layout.view_register, null);
                }
            }
            collection.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
            arg0.removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        }
    }
}
