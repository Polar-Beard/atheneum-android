package com.satchlapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.satchlapp.R;
import com.satchlapp.model.User;
import com.satchlapp.requests.AuthStringRequest;
import com.satchlapp.util.TextStyler;
import com.satchlapp.view.CustomViewPager;
import com.satchlapp.view.CustomViewPagerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Sara on 10/10/2016.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String URL_LOGIN = "http://104.236.163.131:9000/api/user/login";
    private static final String URL_REGISTER_USER = "http://104.236.163.131:9000/api/user/register";
    private static final String PREFS_NAME = "CredentialPrefsFile";

    private Typeface fontBold;
    private Typeface fontLight;

    private TextView viewLoginMainTextViewTitle;
    private TextView viewLoginMainTextViewTagline;
    private Button viewLoginMainRegisterButton;
    private TextView viewLoginMainTextViewSignIn;
    private EditText viewRegisterEditTextEmail;
    private EditText viewRegisterEditTextPassword;
    private Button viewRegisterButton;
    private EditText viewLoginEditTextEmail;
    private EditText viewLoginEditTextPassword;
    private Button viewLoginButton;
    private CustomViewPager viewPager;
    private LoginPagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fontBold = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Bold.ttf");
        fontLight = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Light.ttf");

        viewPager = (CustomViewPager) findViewById(R.id.activityLoginViewPager);
        viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int state){

                if(state == ViewPager.SCROLL_STATE_IDLE){
                    if(pagerAdapter.getView(1).isDeleted()){
                        pagerAdapter.removeView(viewPager, 1);
                    }
                }

            }
        });
        pagerAdapter = new LoginPagerAdapter();
        viewPager.setAdapter(pagerAdapter);

        //Create views for viewPager.
        LayoutInflater inflater = getLayoutInflater();

        CustomViewPagerView viewMain = (CustomViewPagerView) inflater.inflate(R.layout.view_login_main, null);
        final CustomViewPagerView viewRegister = (CustomViewPagerView) inflater.inflate(R.layout.view_register, null);
        final CustomViewPagerView viewLogin = (CustomViewPagerView) inflater.inflate(R.layout.view_login, null);
        viewLoginMainTextViewTitle = (TextView) viewMain.findViewById(R.id.viewLoginMainTextViewTitle);
        SpannableString title = TextStyler.applySpacing("Satchl", 1.5f);
        viewLoginMainTextViewTitle.setText(title, TextView.BufferType.SPANNABLE);
        viewLoginMainTextViewTitle.setTypeface(fontBold);
        viewLoginMainTextViewTagline = (TextView) viewMain.findViewById(R.id.viewLoginMainTextViewTagline);
        viewLoginMainTextViewTagline.setTypeface(fontLight);
        viewLoginMainRegisterButton = (Button) viewMain.findViewById(R.id.viewLoginMainButtonRegister);
        viewLoginMainRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerAdapter.addView(viewRegister);
                viewPager.setCurrentItem(1, true);
            }
        });
        viewLoginMainTextViewSignIn = (TextView) viewMain.findViewById(R.id.viewLoginMainTextViewSignIn);
        viewLoginMainTextViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerAdapter.addView(viewLogin);
                viewPager.setCurrentItem(2, true);
            }
        });

        pagerAdapter.addView(viewMain);

        final RequestQueue queue = Volley.newRequestQueue(this);

        viewRegisterEditTextEmail = (EditText) viewRegister.findViewById(R.id.viewRegisterEditTextEmail);
        viewRegisterEditTextPassword = (EditText) viewRegister.findViewById(R.id.viewRegisterEditTextPassword);
        viewRegisterButton = (Button) viewRegister.findViewById(R.id.viewRegisterButton);

        viewLoginEditTextEmail = (EditText) viewLogin.findViewById(R.id.viewLoginEditTextEmail);
        viewLoginEditTextPassword = (EditText) viewLogin.findViewById(R.id.viewLoginEditTextPassword);
        viewLoginButton = (Button) viewLogin.findViewById(R.id.viewLoginButton);

        viewLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailAddress = viewLoginEditTextEmail.getEditableText().toString();
                final String password = viewLoginEditTextPassword.getEditableText().toString();

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
        });

        viewRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                User user = new User();
                user.setEmailAddress(viewRegisterEditTextEmail.getEditableText().toString());
                user.setPassword(viewRegisterEditTextPassword.getEditableText().toString());
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

    }

    private void saveCredentials(String emailAddress, String password) {
        SharedPreferences credentials = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = credentials.edit();
        editor.putString("emailAddress", emailAddress);
        editor.putString("password", password);
        editor.commit();
    }

    private class LoginPagerAdapter extends PagerAdapter {
        private ArrayList<CustomViewPagerView> views = new ArrayList<>();

        @Override
        public int getItemPosition(Object object) {
            int index = views.indexOf(object);
            if (index == -1)
                return POSITION_NONE;
            else
                return index;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            CustomViewPagerView v = views.get(position);
                if(v.isDeleted()){
                    v.restore();
                }
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public int addView(CustomViewPagerView v) {
            return addView(v, views.size());
        }

        public int addView(CustomViewPagerView v, int position) {
            views.add(position, v);
            this.notifyDataSetChanged();
            return position;
        }

        public int removeView(ViewPager pager, View v) {
            return removeView(pager, views.indexOf(v));
        }

        public int removeView(ViewPager pager, int position) {
            // ViewPager doesn't have a delete method; the closest is to set the adapter
            // again.  When doing so, it deletes all its views.  Then we can delete the view
            // from from the adapter and finally set the adapter to the pager again.  Note
            // that we set the adapter to null before removing the view from "views" - that's
            // because while ViewPager deletes all its views, it will call destroyItem which
            // will in turn cause a null pointer ref.
            pager.setAdapter(null);
            views.remove(position);
            this.notifyDataSetChanged();
            pager.setAdapter(this);

            return position;
        }

        public CustomViewPagerView getView(int position) {
            return views.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(0, true);
            pagerAdapter.getView(1).delete();
        }
    }
}
