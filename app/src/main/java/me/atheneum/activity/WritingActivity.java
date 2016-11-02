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
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import me.atheneum.R;

public class WritingActivity extends AppCompatActivity {

    private boolean actionFormatSizeIsActive = false;

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

        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_format_size:
                        EditText bodyTextInput = (EditText) findViewById(R.id.editText);
                        int cursorPos = bodyTextInput.getSelectionStart();
                        int[] lineBoundaries = getCurrentLineBoundaries(bodyTextInput);
                        SpannableStringBuilder bodyText  = new SpannableStringBuilder(bodyTextInput.getText());
                        SpannableStringBuilder currentLine = new SpannableStringBuilder(bodyText.subSequence(lineBoundaries[0],lineBoundaries[1]));
                        RelativeSizeSpan[] formatObj = currentLine.getSpans(0,currentLine.length(),RelativeSizeSpan.class);
                        /*This checks to see if the array is empty. If it is empty, then there is no
                            RelativeSizeSpan on the current selection, meaning it needs to be
                            added. If it is not empty, then the RelativeSizeSpan already exists on
                            the selected text, and needs to be removed.
                         */
                        if(formatObj.length == 0){
                            bodyText.replace(lineBoundaries[0], lineBoundaries[1], activateResizeFormat(currentLine));
                            bodyTextInput.setText(bodyText);
                            item.setIcon(R.drawable.ic_format_size_active);
                        } else{
                            currentLine.removeSpan(formatObj[0]);
                            SpannableStringBuilder updatedString = new SpannableStringBuilder();
                            /* This gets a bit hacky, but I couldn't figure out a better solution.
                                Instead of using replace(), the content from the EditText is
                                reconstructed around the current line, from which the
                                RelativeSizeSpan has been removed. The reason being that replace()
                                was preserving the old span, and I couldn't figure out how to
                                remove it.

                             */
                            if(lineBoundaries[0]==0){
                                updatedString.append(currentLine);
                            }else{
                                CharSequence textBeforeCurrentLine = bodyText.subSequence(0,lineBoundaries[0]);
                                updatedString.append(textBeforeCurrentLine);
                                updatedString.append(currentLine);
                            }
                            if(lineBoundaries[1]!= bodyText.length()){
                                CharSequence textAfterCurrentLine = bodyText.subSequence(lineBoundaries[1],bodyText.length());
                                updatedString.append(textAfterCurrentLine);
                            }
                            bodyTextInput.setText(updatedString);
                            item.setIcon(R.drawable.ic_format_size);
                        }
                        bodyTextInput.setSelection(cursorPos);
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

    private int[] getCurrentLineBoundaries(EditText bodyTextInput){
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

    private SpannableStringBuilder activateResizeFormat(SpannableStringBuilder text){
        text.setSpan(new RelativeSizeSpan(1.5f), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    private SpannableString removeResizeFormat(SpannableString text, RelativeSizeSpan formatObj){
        text.removeSpan(formatObj);
        return text;
    }

}
