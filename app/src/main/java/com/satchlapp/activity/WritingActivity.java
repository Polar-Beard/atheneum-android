package com.satchlapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.cloudinary.Cloudinary;
import com.google.gson.Gson;
import com.satchlapp.R;
import com.satchlapp.adapter.WysiwygEditorAdapter;
import com.satchlapp.list.Constants;
import com.satchlapp.list.ListLinks;
import com.satchlapp.list.SecretKeys;
import com.satchlapp.model.Content;
import com.satchlapp.model.Story;
import com.satchlapp.request.AuthJsonObjectRequest;
import com.satchlapp.util.CloudinaryAsyncTask;
import com.satchlapp.util.TextContentFormatter;
import com.satchlapp.util.Tools;
import com.satchlapp.view.EditTextCursorWatcher;

import org.json.JSONObject;

import java.io.IOException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class WritingActivity extends AppCompatActivity {

    private final static int PICK_IMAGE_REQUEST = 100;

    private static Toolbar toolbarBottom;
    private RecyclerView recyclerView;
    private EditTextCursorWatcher activeEditText;
    private WysiwygEditorAdapter adapter;
    private LinearLayoutManager layoutManager;

    private Story story;
    private Content currentContent;

    private TextContentFormatter formatter;
    private Map<Integer, MenuItem> menuItemMap;

    private int currentCursorPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarTop);

        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);

        formatter = new TextContentFormatter();

        story = new Story();
        Content content = new Content();
        content.setType(Constants.CONTENT_TYPE_TEXT);
        story.addContent(content);
        currentContent = content;

        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_add_image){
                    Intent intent = new Intent();
                    // Show only images, no videos or anything else
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    // Always show the chooser (if there are multiple options available)
                    startActivityForResult(Intent.createChooser(intent, "Image"), PICK_IMAGE_REQUEST);
                    return true;
                }

                View focusedChild = layoutManager.getFocusedChild();
                if(focusedChild instanceof EditTextCursorWatcher){
                    activeEditText = (EditTextCursorWatcher) focusedChild;
                    currentContent = story.getContent(layoutManager.getPosition(focusedChild));
                    currentCursorPosition = activeEditText.getSelectionStart();
                    updateContent(currentContent,activeEditText);
                    formatter.findActiveFormattingPositions(currentContent, currentCursorPosition);

                    switch (item.getItemId()) {
                        case R.id.action_format_size:
                            if (formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_TITLE)) {
                                setSubtitleToActive();
                            } else if (formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_SUBTITLE)) {
                                setSubtitleToInactive();
                            } else {
                                setTitleToActive();
                            }
                            break;
                        case R.id.action_italic:
                            if (formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC)) {
                                setBoldItalicToInactive();
                                setBoldToActive();
                            } else if (!formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_ITALIC)) {
                                if (formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_BOLD)) {
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
                            if (formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC)) {
                                setBoldItalicToInactive();
                                setItalicToActive();
                            } else if (!formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_BOLD)) {
                                if (formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_ITALIC)) {
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

                    setEditTextBody(currentCursorPosition);
                    return true;
                }

                return false;

            }
        });

        toolbarBottom.inflateMenu(R.menu.activity_writing_toolbar_bottom);
        menuItemMap = new HashMap<>();
        menuItemMap.put(R.id.action_italic,
                toolbarBottom.getMenu().findItem(R.id.action_italic));
        menuItemMap.put(R.id.action_bold,
                toolbarBottom.getMenu().findItem(R.id.action_bold));
        menuItemMap.put(R.id.action_format_size,
                toolbarBottom.getMenu().findItem(R.id.action_format_size));
        menuItemMap.put(R.id.action_add_image,
                toolbarBottom.getMenu().findItem(R.id.action_add_image));

        recyclerView = (RecyclerView) findViewById(R.id.activityWritingRecyclerView);
        adapter = new WysiwygEditorAdapter(story.getContents(), menuItemMap);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            //Add image to RecyclerView
            Uri uri = data.getData();
            Content content = new Content();
            content.setType(Constants.CONTENT_TYPE_IMAGE_WITH_CAPTION);
            content.setValue(uri.toString());
            story.addContent(content);

            //Add new EditText beneath image
            content = new Content();
            content.setType(Constants.CONTENT_TYPE_TEXT);
            content.setValue("");
            story.addContent(content);
            currentContent = content;

            adapter.notifyItemInserted(story.getContents().size() - 1);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_writing_toolbar_top, menu);
        return true;
    }

    @Override
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

        if (id == R.id.action_publish){

            Content firstTextContent = null;

            //Updating all the contents with their associated Edit Texts. Make sure everything
            //is current!
            for(int i = 0; i < adapter.getItemCount(); i++){
                if(adapter.getItemViewType(i) == Constants.CONTENT_TYPE_TEXT){
                    EditText view = (EditText) layoutManager.findViewByPosition(i);
                    Content content = story.getContent(i);
                    updateContent(content, view);

                    //Get the first text content while I'm processing the edit texts. Save a loop!
                    if(firstTextContent == null){
                        firstTextContent = content;
                    }
                }
            }

            String title = null;

            if(firstTextContent != null){
                //Get first line to use as title
                int[] titleBounds = formatter.getLinePositions(0,firstTextContent);
                title = firstTextContent.getValue().substring(0, titleBounds[1]);
                if(title.length() >= Constants.TITLE_CHAR_LIMIT){
                    title = title.substring(0,Constants.TITLE_CHAR_LIMIT);
                }
                //Get second line to use as description
                if(titleBounds[1] < firstTextContent.getValue().length()) {
                    int[] descriptionBounds = formatter.getLinePositions(titleBounds[1] + 1, firstTextContent);
                    String description = firstTextContent.getValue().substring(titleBounds[1] + 1, descriptionBounds[1]);
                    if (description.length() >= Constants.DESCRIPTION_CHAR_LIMIT) {
                        description = description.substring(0, Constants.DESCRIPTION_CHAR_LIMIT);
                    }
                    story.setDescription(description);
                }
            }

            if(title == null || title.equals("")){
                title = "Untitled";
            }
            story.setTitle(title);

            Map config = new HashMap();
            config.put("cloud_name", SecretKeys.CLOUDINARY_CLOUD_NAME);
            config.put("api_key", SecretKeys.CLOUDINARY_API_KEY);
            config.put("api_secret", SecretKeys.CLOUDINARY_API_SECRET);
            Cloudinary cloudinary = new Cloudinary(config);

            Map<String,InputStream> imagesToUpload = new HashMap<>();
            for(Content content: story.getContents()){
                if(content.getType() == Constants.CONTENT_TYPE_IMAGE_WITH_CAPTION){
                    String imageId = Tools.randomString(20);
                    try{
                        imagesToUpload.put(imageId,getAssetStream(Uri.parse(content.getValue())));
                        content.setValue(cloudinary.url().generate(imageId));
                    }catch (IOException e){
                        Log.e("WritingActivity",e.toString());
                    }
                }
            }

            new CloudinaryAsyncTask().execute(imagesToUpload);

            Gson gson = new Gson();
            String postBody = gson.toJson(story);
            System.out.println(postBody);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(postBody);
            } catch (Exception e){
                return true;
            }
            AuthJsonObjectRequest jsonObjectRequest = new AuthJsonObjectRequest(ListLinks.API_STORY_PUBLISH,jsonObject,
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
            SharedPreferences credentials = getSharedPreferences(Constants.PREFS_NAME, 0);
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
    }

    private InputStream getAssetStream(Uri uri) throws IOException {
        return getContentResolver().openInputStream(uri);
    }

    private void setBoldToActive(){
        int[] positions = formatter.getWordPositions(currentCursorPosition, currentContent);
        formatter.setFormatting(Constants.QUALIFIER_TYPE_TEXT_BOLD, positions, currentContent);
        setIconOnToolbarBottom(R.id.action_bold, R.drawable.ic_bold_active);
    }

    private void setItalicToActive(){
        int[] positions = formatter.getWordPositions(currentCursorPosition, currentContent);
        formatter.setFormatting(Constants.QUALIFIER_TYPE_TEXT_ITALIC, positions, currentContent);
        setIconOnToolbarBottom(R.id.action_italic, R.drawable.ic_italic_active);
    }

    private void setBoldItalicToActive(){
        int[] positions = formatter.getWordPositions(currentCursorPosition, currentContent);
        formatter.setFormatting(Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC, positions, currentContent);
        setIconOnToolbarBottom(R.id.action_italic, R.drawable.ic_italic_active);
        setIconOnToolbarBottom(R.id.action_bold, R.drawable.ic_bold_active);
    }

    private void setTitleToActive(){
        int[] positions = formatter.getLinePositions(currentCursorPosition, currentContent);
        formatter.setFormatting(Constants.QUALIFIER_TYPE_TEXT_TITLE, positions, currentContent);
        setIconOnToolbarBottom(R.id.action_format_size, R.drawable.ic_title_active);
    }

    private void setSubtitleToActive(){
        this.setTitleToInactive();

        int[] positions = formatter.getLinePositions(currentCursorPosition, currentContent);
        formatter.setFormatting(Constants.QUALIFIER_TYPE_TEXT_SUBTITLE, positions, currentContent);
        setIconOnToolbarBottom(R.id.action_format_size, R.drawable.ic_subtitle_active);
    }

    private void setBoldToInactive(){
        int pos = formatter.getActiveFormatIndex(Constants.QUALIFIER_TYPE_TEXT_BOLD);
        formatter.removeActiveFormat(Constants.QUALIFIER_TYPE_TEXT_BOLD);
        currentContent.removeQualifier(pos);

        setIconOnToolbarBottom(R.id.action_bold, R.drawable.ic_bold_inactive);
    }

    private void setItalicToInactive(){
        int pos = formatter.getActiveFormatIndex(Constants.QUALIFIER_TYPE_TEXT_ITALIC);
        formatter.removeActiveFormat(Constants.QUALIFIER_TYPE_TEXT_ITALIC);
        currentContent.removeQualifier(pos);

        setIconOnToolbarBottom(R.id.action_italic, R.drawable.ic_italic_inactive);
    }

    private void setBoldItalicToInactive(){
        int pos = formatter.getActiveFormatIndex(Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC);
        formatter.removeActiveFormat(Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC);
        currentContent.removeQualifier(pos);

        setIconOnToolbarBottom(R.id.action_italic, R.drawable.ic_italic_inactive);
        setIconOnToolbarBottom(R.id.action_bold, R.drawable.ic_bold_inactive);
    }

    private void setTitleToInactive(){
        int pos = formatter.getActiveFormatIndex(Constants.QUALIFIER_TYPE_TEXT_TITLE);
        formatter.removeActiveFormat(Constants.QUALIFIER_TYPE_TEXT_TITLE);
        currentContent.removeQualifier(pos);

        setIconOnToolbarBottom(R.id.action_format_size, R.drawable.ic_title_inactive);
    }

    private void setSubtitleToInactive(){
        int pos = formatter.getActiveFormatIndex(Constants.QUALIFIER_TYPE_TEXT_SUBTITLE);
        formatter.removeActiveFormat(Constants.QUALIFIER_TYPE_TEXT_SUBTITLE);
        currentContent.removeQualifier(pos);

        setIconOnToolbarBottom(R.id.action_format_size, R.drawable.ic_title_inactive);
    }

    private void setEditTextBody(int cursorPosition){
        activeEditText.setText(Story.parseTextContent(currentContent),
                TextView.BufferType.EDITABLE);
        activeEditText.setSelection(cursorPosition);
    }

    private void updateContent(Content content, EditText editText){
        content.setValue(editText.getText().toString());
    }

    /*private void updateFormatting(int cursorPosition){
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
    }

    private boolean isFormattingActive(){
        return activeFormatPositions.size() > 0;
    }*/

    private void setIconOnToolbarBottom(int item, int icon){
        menuItemMap.get(item).setIcon(icon);
    }
}
