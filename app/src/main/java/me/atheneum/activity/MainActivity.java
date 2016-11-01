package me.atheneum.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import me.atheneum.R;
import me.atheneum.adapters.StoryAdapter;
import me.atheneum.model.Story;
import me.atheneum.requests.AuthJsonObjectRequest;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String URL_PUBLISH = "http://104.236.163.131:9000/api/story/publish";
    private static final String PREFS_NAME = "CredentialPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RequestQueue queue = Volley.newRequestQueue(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.create_story_dialog);
                final EditText titleInput = (EditText) dialog.findViewById(R.id.title_input);
                final EditText descriptionInput = (EditText) dialog.findViewById(R.id.description_input);
                Button storySubmitButton = (Button) dialog.findViewById(R.id.story_submit_button);

                storySubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = titleInput.getEditableText().toString();
                        String description = descriptionInput.getEditableText().toString();
                        Story story = new Story(title,description);
                        Gson gson = new Gson();
                        String postBody = gson.toJson(story);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(postBody);
                        } catch (Exception e){
                            return;
                        }

                        // Request a string response from the provided URL.
                        AuthJsonObjectRequest jsonObjectRequest = new AuthJsonObjectRequest(URL_PUBLISH,jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        System.out.println("It worked!");
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                         System.out.println("Posting didn't work!");
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
                        SharedPreferences credentials = getSharedPreferences(PREFS_NAME, 0);
                        String emailAddress = credentials.getString("emailAddress", null);
                        String password = credentials.getString("password", null);
                        if(emailAddress == null|| password == null){
                            System.out.println("Credentials do not exist");
                            return;
                        } else{
                            jsonObjectRequest.setEmailAddress(emailAddress);
                            jsonObjectRequest.setPassword(password);
                            queue.add(jsonObjectRequest);
                        }
                    }
                });
                dialog.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Networking stuff
        // Instantiate the RequestQueue.

        String url = "http://104.236.163.131:9000/api/story/n/100";
        final StoryAdapter storyAdapter = new StoryAdapter(new ArrayList<Story>());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("It worked!");
                        Gson gson = new Gson();
                        List<Story> stories = gson.fromJson(response, new TypeToken<List<Story>>(){}.getType());
                        storyAdapter.setStories(stories);
                        storyAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Getting didn't work!");
                    }
                });
        queue.add(stringRequest);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_story);
        recyclerView.setAdapter(storyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new_story) {
            Intent intent = new Intent(getApplicationContext(), WritingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
