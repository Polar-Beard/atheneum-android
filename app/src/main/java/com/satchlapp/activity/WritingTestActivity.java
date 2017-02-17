package com.satchlapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.view.ViewTreeObserver;
import android.widget.TextView;


import com.satchlapp.HtmlTagPositioner;
import com.satchlapp.R;
import com.satchlapp.adapters.WysiwygEditorAdapter;
import com.satchlapp.lists.Constants;
import com.satchlapp.model.Content;
import com.satchlapp.model.Story;
import com.satchlapp.view.EditTextCursorWatcher;
import com.satchlapp.view.OnSelectionChangedListener;

import java.util.HashMap;
import java.util.Map;

public class WritingTestActivity extends AppCompatActivity {

    private final static int TITLE_CHAR_LIMIT = 75;
    private final static int DESCRIPTION_CHAR_LIMIT = 150;
    private static final String URL_PUBLISH = "http://104.236.163.131:9000/api/story/publish";
    private static final String PREFS_NAME = "CredentialPrefsFile";


    private static Toolbar toolbarBottom;
    private RecyclerView recyclerView;
    private EditTextCursorWatcher activeEditText;
    private int currentCursorPosition;

    private Story story;
    private int currentContent;
    private HtmlTagPositioner htmlTagPositioner;

    private int activeEditTextPosition;
    private int lastCursorPosition;

    private Map<Integer,Integer> activeFormatPositions = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarTop);

        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);

        htmlTagPositioner = new HtmlTagPositioner();

        story = new Story();
        currentContent = story.addNewContent();
        story.getContent(currentContent).setType(Constants.CONTENT_TYPE_TEXT);

        lastCursorPosition = 0;

        recyclerView = (RecyclerView) findViewById(R.id.activityWritingRecyclerView);
        WysiwygEditorAdapter adapter = new WysiwygEditorAdapter(story.getContents());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        ViewTreeObserver viewTreeObserverRecyclerView = recyclerView.getViewTreeObserver();
        if(viewTreeObserverRecyclerView.isAlive()){
            viewTreeObserverRecyclerView.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    //Try & Catch block in place for using the deprecated method.
                    try {
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } catch (NoSuchMethodError e) {
                        recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    View activeView = layoutManager.findViewByPosition(0);

                    activeEditText = (EditTextCursorWatcher) activeView.findViewById(R.id.listItemWysiwygTextEditText);
                    activeEditText.setOnSelectionChangedListener(
                            new OnSelectionChangedListener() {
                                @Override
                                public void onSelectionChanged(int selStart, int selEnd) {
                                    if(selStart != lastCursorPosition
                                            && selStart != 0){
                                        updateContent(selStart);
                                        setEditTextBody(selStart);
                                        lastCursorPosition = selStart;
                                    }
                                }
                            }
                    );
                }
            });
        }

        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                currentCursorPosition = activeEditText.getSelectionStart();

                switch (item.getItemId()) {
                    case R.id.action_format_size:
                        if (isFormatActive(Constants.QUALIFIER_TYPE_TEXT_TITLE)) {
                            setSubtitleToActive();
                        } else if (isFormatActive(Constants.QUALIFIER_TYPE_TEXT_SUBTITLE)) {
                            setSubtitleToInactive();
                        } else {
                            setTitleToActive();
                        }
                        break;
                    case R.id.action_italic:
                        if (isFormatActive(Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC)) {
                            setBoldItalicToInactive();
                            setBoldToActive();
                        } else if (!isFormatActive(Constants.QUALIFIER_TYPE_TEXT_ITALIC)) {
                            if (isFormatActive(Constants.QUALIFIER_TYPE_TEXT_BOLD)) {
                                setBoldItalicToActive();
                                setBoldToInactive();
                            } else {
                                setItalicToActive();
                            }
                        } else {
                            setItalicToInactive();
                        }
                        break;
                    case R.id.action_bold:
                        if (isFormatActive(Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC)) {
                            setBoldItalicToInactive();
                            setItalicToActive();
                        } else if (!isFormatActive(Constants.QUALIFIER_TYPE_TEXT_BOLD)) {
                            if (isFormatActive(Constants.QUALIFIER_TYPE_TEXT_ITALIC)) {
                                setBoldItalicToActive();
                                setItalicToInactive();
                            } else {
                                setBoldToActive();
                            }
                        } else {
                            setBoldToInactive();
                        }
                        break;
                }

                updateContent(currentCursorPosition);
                setEditTextBody(currentCursorPosition);
                return true;
            }
        });

        toolbarBottom.inflateMenu(R.menu.activity_writing_toolbar_bottom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_writing_toolbar_top, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        RequestQueue queue = Volley.newRequestQueue(this);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_publish) {
            Story story = new Story();
            String text = bodyTextInput.getText().toString();
            //Get first line to use as title
            int[] titleBounds = getLineBoundaries(0,bodyTextInput);
            String title = text.substring(0,titleBounds[1]);
            if(title.length() >= TITLE_CHAR_LIMIT){
                title = title.substring(0,TITLE_CHAR_LIMIT);
            }
            //Get second line to use as description
            if(titleBounds[1] < text.length()) {
                int[] descriptionBounds = getLineBoundaries(titleBounds[1] + 1, bodyTextInput);
                String description = text.substring(titleBounds[1] + 1, descriptionBounds[1]);
                if (description.length() >= DESCRIPTION_CHAR_LIMIT) {
                    description = description.substring(0, DESCRIPTION_CHAR_LIMIT);
                }
                story.setDescription(description);
            }
            String body = CustomHtml.toHtml(bodyTextInput.getText());
            story.setTitle(title);
            story.setBody(body);
            Gson gson = new Gson();
            String postBody = gson.toJson(story);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(postBody);
            } catch (Exception e){
                return true;
            }
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
            );
            SharedPreferences credentials = getSharedPreferences(PREFS_NAME, 0);
            String emailAddress = credentials.getString("emailAddress", null);
            String password = credentials.getString("password", null);
            if(emailAddress == null|| password == null){
                System.out.println("Credentials do not exist");
                return true;
            } else{
                jsonObjectRequest.setEmailAddress(emailAddress);
                jsonObjectRequest.setPassword(password);
                queue.add(jsonObjectRequest);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void setBoldToActive(){
        int[] positions = htmlTagPositioner.setPositionsToWord(currentCursorPosition);
        setFormatting(Constants.QUALIFIER_TYPE_TEXT_BOLD, positions);
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_bold);
        item.setIcon(R.drawable.ic_bold_active);
    }

    private void setItalicToActive(){
        int[] positions = htmlTagPositioner.setPositionsToWord(currentCursorPosition);
        setFormatting(Constants.QUALIFIER_TYPE_TEXT_ITALIC, positions);
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_italic);
        item.setIcon(R.drawable.ic_italic_active);
    }

    private void setBoldItalicToActive(){
        int[] positions = htmlTagPositioner.setPositionsToWord(currentCursorPosition);
        setFormatting(Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC, positions);
        toolbarBottom.getMenu().findItem(R.id.action_italic)
                .setIcon(R.drawable.ic_italic_active);
        toolbarBottom.getMenu().findItem(R.id.action_bold)
                .setIcon(R.drawable.ic_bold_active);
    }

    private void setTitleToActive(){
        int[] positions = htmlTagPositioner.setPositionsToLine(currentCursorPosition);
        setFormatting(Constants.QUALIFIER_TYPE_TEXT_TITLE, positions);
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_format_size);
        item.setIcon(R.drawable.ic_title_active);
    }

    private void setSubtitleToActive(){
        this.setTitleToInactive();

        int[] positions = htmlTagPositioner.setPositionsToLine(currentCursorPosition);
        setFormatting(Constants.QUALIFIER_TYPE_TEXT_SUBTITLE, positions);
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_format_size);
        item.setIcon(R.drawable.ic_subtitle_active);
    }

    private void setBoldToInactive(){
        int pos = activeFormatPositions.get(Constants.QUALIFIER_TYPE_TEXT_BOLD);
        story.getContent(currentContent).removeQualifier(pos);

        toolbarBottom.getMenu().findItem(R.id.action_bold)
                .setIcon(R.drawable.ic_bold_inactive);
    }

    private void setItalicToInactive(){
        int pos = activeFormatPositions.get(Constants.QUALIFIER_TYPE_TEXT_ITALIC);
        story.getContent(currentContent).removeQualifier(pos);

        toolbarBottom.getMenu().findItem(R.id.action_italic)
                .setIcon(R.drawable.ic_italic_inactive);
    }

    private void setBoldItalicToInactive(){
        int pos = activeFormatPositions.get(Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC);
        story.getContent(currentContent).removeQualifier(pos);

        toolbarBottom.getMenu().findItem(R.id.action_italic)
                .setIcon(R.drawable.ic_italic_inactive);
        toolbarBottom.getMenu().findItem(R.id.action_bold)
                .setIcon(R.drawable.ic_bold_inactive);
    }

    private void setTitleToInactive(){
        int pos = activeFormatPositions.get(Constants.QUALIFIER_TYPE_TEXT_TITLE);
        story.getContent(currentContent).removeQualifier(pos);

        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_format_size);
        item.setIcon(R.drawable.ic_title_inactive);
    }

    private void setSubtitleToInactive(){
        int pos = activeFormatPositions.get(Constants.QUALIFIER_TYPE_TEXT_SUBTITLE);
        story.getContent(currentContent).removeQualifier(pos);

        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_format_size);
        item.setIcon(R.drawable.ic_title_inactive);
    }

    private void setEditTextBody(int cursorPosition){
        activeEditText.setText(Story.parseTextContent(story.getContent(currentContent)),
                TextView.BufferType.EDITABLE);
        activeEditText.setSelection(cursorPosition);
    }

    private void setFormatting(int formatType, int[] positions){
        Content.Qualifier qualifier = story.getContent(currentContent).addNewQualifier();
        qualifier.setType(formatType);
        qualifier.addNewSpecification()
                .setType("int")
                .setName("starting_position")
                .setValue(String.valueOf(positions[0]));
        qualifier.addNewSpecification()
                .setType("int")
                .setName("ending_position")
                .setValue(String.valueOf(positions[1]));
    }

    private boolean isFormatActive(int formatType){
        for(int i = 0; i < story.getContent(currentContent).getQualifiers().size(); i++){
            Content.Qualifier q = story.getContent(currentContent).getQualifier(i);
            if(q.getType() == formatType){
                if(Integer.valueOf(q.getSpecification(0).getValue())<= currentCursorPosition &&
                        currentCursorPosition <= Integer.valueOf(q.getSpecification(1).getValue())){
                    activeFormatPositions.put(formatType,i);
                    return true;
                }
            }
        }
        return false;
    }

    private void updateContent(int cursorPosition){
        if(isFormattingActive()){
            for(Integer integer: activeFormatPositions.values()){
                story.getContent(currentContent)
                        .getQualifier(integer)
                        .getSpecifications()
                        .get(1)
                        .setValue(cursorPosition + "")
                ;
            }
        }
        story.getContent(currentContent).setValue(activeEditText.getText().toString());
    }

    private boolean isFormattingActive(){
        return activeFormatPositions.size() > 0;
    }

}