package me.atheneum.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import me.atheneum.R;

public class WritingActivity extends AppCompatActivity {

    private boolean actionFormatSizeIsActive = false;
    private EditText bodyTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarTop);

        Toolbar toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        if(toolbarBottom == null){
            return;
        }
        bodyTextInput = (EditText) findViewById(R.id.editText);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_format_size:
                        int[] lineBoundaries = getCurrentLineBoundaries();
                        //Check that the line is not empty
                        if((lineBoundaries[1] - lineBoundaries[0] > 0)){
                            SpannableString span  = new SpannableString(bodyTextInput.getText());
                            RelativeSizeSpan[] formatObj = span.getSpans(lineBoundaries[0], lineBoundaries[1], RelativeSizeSpan.class);
                            if(formatObj.length == 0){
                                activateResizeFormat(span,lineBoundaries, item);
                            } else {
                                removeResizeFormat(new SpannableString(span.subSequence(lineBoundaries[0],lineBoundaries[1])), formatObj[0], item);
                            }
                        }
                        break;
                    default:
                        break;
                }
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

        if (id == R.id.action_publish) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int[] getCurrentLineBoundaries(){
        int cursorPos = bodyTextInput.getSelectionStart();
        CharSequence enteredText = bodyTextInput.getText();
        //Find nearest line break that precedes the cursor.
        int lineStartPos = 0;
        boolean foundStartingLineBreak = false;
        int i = cursorPos - 1;
        while(!foundStartingLineBreak && i > 0 ){
            if(enteredText.charAt(i) == '\n'){
                lineStartPos = i;
                foundStartingLineBreak = true;
            }
            i--;
        }
        //Find the next nearest line break.
        int lineEndPos = enteredText.length();
        if(cursorPos != enteredText.length()) {
            boolean foundEndingLineBreak = false;
            int j = cursorPos;
            while (!foundEndingLineBreak && j < enteredText.length()) {
                if (enteredText.charAt(j) == '\n') {
                    lineEndPos = j;
                    foundEndingLineBreak = true;
                }
                j++;
            }
        }
        int [] positions = {lineStartPos,lineEndPos};
        return positions;
    }

    private void activateResizeFormat(Spannable span, int[] boundaries, MenuItem item){
        int currentCursorPos = bodyTextInput.getSelectionStart();
        span.setSpan(new RelativeSizeSpan(1.5f),boundaries[0],boundaries[1], Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        bodyTextInput.setText(span);
        bodyTextInput.setSelection(currentCursorPos);
        item.setIcon(R.drawable.ic_format_size_active);
    }

    private void removeResizeFormat(Spannable span, RelativeSizeSpan formatObj, MenuItem item){
        int currentCursorPos = bodyTextInput.getSelectionStart();
        span.removeSpan(formatObj);
        bodyTextInput.setText(span);
        bodyTextInput.setSelection(currentCursorPos);
        item.setIcon(R.drawable.ic_format_size);
    }

}
