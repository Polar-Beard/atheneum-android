package com.satchlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.satchlapp.R;
import com.satchlapp.model.Story;

public class ReadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Story story = (Story) intent.getSerializableExtra("story");
        CharSequence bodyText = Html.fromHtml(story.getBody());
        TextView bodyView = (TextView) findViewById(R.id.story_body);
        bodyView.setText(bodyText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Snackbar.make(view, "Don't say nigger", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
