package me.atheneum.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import me.atheneum.R;

public class WritingActivity extends AppCompatActivity {

    private final static float TITLE = 2.0f;
    private final static float SUBTITLE = 1.25f;

    private static boolean titleIsActive;
    private static boolean subtitleIsActive;
    private static boolean boldIsActive;
    private static boolean italicIsActive;
    private static boolean boldItalicIsActive;
    private static boolean formattedListIsActive;
    private static boolean unformattedListIsActive;

    private static Toolbar toolbarBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarTop);

        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        if(toolbarBottom == null){
            return;
        }
        final EditText bodyTextInput = (EditText) findViewById(R.id.editText);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int cursorPos = bodyTextInput.getSelectionStart();
                SpannableStringBuilder bodyText  = new SpannableStringBuilder(bodyTextInput.getText());
                int[] lineBoundaries = getCurrentLineBoundaries(cursorPos,bodyTextInput);
                int[] wordBoundaries = getCurrentWordBoundaries(cursorPos, bodyTextInput);
                SpannableStringBuilder currentLine = new SpannableStringBuilder(bodyText.subSequence(lineBoundaries[0],lineBoundaries[1]));
                SpannableStringBuilder currentWord = new SpannableStringBuilder(bodyText.subSequence(wordBoundaries[0], wordBoundaries[1]));
                switch (item.getItemId()) {
                    case R.id.action_format_size:
                        if(titleIsActive){
                            currentLine = removeTitleSpan(currentLine);
                            currentLine = addSubtitleSpan(currentLine);
                            bodyTextInput.setText(updateTextSpan(bodyText,currentLine,lineBoundaries));
                            setTitleToInactive();
                            setSubtitleToActive();
                        } else if(subtitleIsActive){
                            currentLine = removeTitleSpan(currentLine);
                            bodyTextInput.setText(updateTextSpan(bodyText,currentLine,lineBoundaries));
                            setSubtitleToInactive();
                        } else{
                            bodyText.replace(lineBoundaries[0], lineBoundaries[1], addTitleSpan(currentLine));
                            bodyTextInput.setText(bodyText);
                            setTitleToActive();
                        }
                        bodyTextInput.setSelection(cursorPos);
                        break;
                    case R.id.action_bold:
                        if(boldItalicIsActive){
                            currentWord = removeStyleSpan(currentWord);
                            currentWord = addItalicSpan(currentWord);
                            bodyTextInput.setText(updateTextSpan(bodyText,currentWord,wordBoundaries));
                            setBoldItalicToInactive();
                            setBoldToInactive();
                        } else if(boldIsActive){
                            currentWord = removeStyleSpan(currentWord);
                            bodyTextInput.setText(updateTextSpan(bodyText, currentWord, wordBoundaries));
                            setBoldToInactive();
                        } else if(italicIsActive){
                            currentWord = removeStyleSpan(currentWord);
                            currentWord = addBoldItalicSpan(currentWord);
                            bodyTextInput.setText(updateTextSpan(bodyText,currentWord,wordBoundaries));
                            setBoldToActive();
                            setBoldItalicToActive();
                        } else{
                            bodyText.replace(wordBoundaries[0], wordBoundaries[1], addBoldSpan(currentWord));
                            bodyTextInput.setText(bodyText);
                            setBoldToActive();
                        }
                        bodyTextInput.setSelection(cursorPos);
                        break;
                    case R.id.action_italic:
                        if(boldItalicIsActive){
                            currentWord = removeStyleSpan(currentWord);
                            currentWord = addBoldSpan(currentWord);
                            bodyTextInput.setText(updateTextSpan(bodyText,currentWord,wordBoundaries));
                            setBoldItalicToInactive();
                            setItalicToInactive();
                        } else if(italicIsActive){
                            currentWord = removeStyleSpan(currentWord);
                            bodyTextInput.setText(updateTextSpan(bodyText, currentWord, wordBoundaries));
                            setItalicToInactive();
                        } else if(boldIsActive){
                            currentWord = removeStyleSpan(currentWord);
                            currentWord = addBoldItalicSpan(currentWord);
                            bodyTextInput.setText(updateTextSpan(bodyText,currentWord,wordBoundaries));
                            setItalicToActive();
                            setBoldItalicToActive();
                        } else{
                            bodyText.replace(wordBoundaries[0], wordBoundaries[1], addItalicSpan(currentWord));
                            bodyTextInput.setText(bodyText);
                            setItalicToActive();
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
    private static SpannableString getCurrentWord(int cursorPos, EditText editText){
        int [] wordBoundaries = getCurrentWordBoundaries(cursorPos,editText);
        return new SpannableString(editText.getText().subSequence(wordBoundaries[0], wordBoundaries[1]));
    }

    private static int[] getCurrentWordBoundaries(int cursorPos, EditText editText) {
        return getStringBoundsByCharLimit(cursorPos,editText,' ');
    }
    private static SpannableString getCurrentLine(int cursorPos, EditText editText){
        int[] lineBoundaries = getCurrentLineBoundaries(cursorPos, editText);
        return new SpannableString(editText.getText().subSequence(lineBoundaries[0],lineBoundaries[1]));
    }
    private static int[] getCurrentLineBoundaries(int cursorPos, EditText editText) {
        return getStringBoundsByCharLimit(cursorPos,editText,'\n');
    }

    private static int[] getStringBoundsByCharLimit(int cursorPos,EditText editText,char boundary){
        CharSequence enteredText = editText.getText();
        //Find nearest boundary that precedes the cursor.
        int stringStartPos = 0;
        boolean foundStartingBoundary = false;
        int i = cursorPos - 1;
        while(!foundStartingBoundary && i > 0 ){
            if(enteredText.charAt(i) == boundary){
                stringStartPos = i;
                foundStartingBoundary = true;
            }
            i--;
        }
        //Find the next nearest boundary.
        int stringEndPos = enteredText.length();
        if(cursorPos != enteredText.length()) {
            boolean foundEndingBoundary = false;
            int j = cursorPos;
            while (!foundEndingBoundary && j < enteredText.length()) {
                if (enteredText.charAt(j) == boundary) {
                    stringEndPos = j;
                    foundEndingBoundary = true;
                }
                j++;
            }
        }
        return new int[] {stringStartPos,stringEndPos};
    }

    private SpannableStringBuilder addTitleSpan(SpannableStringBuilder text){
        text.setSpan(new RelativeSizeSpan(TITLE), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    private SpannableStringBuilder removeTitleSpan(SpannableStringBuilder text){
        RelativeSizeSpan[] spans = text.getSpans(0,text.length(),RelativeSizeSpan.class);
        text.removeSpan(spans[0]);
        return text;
    }

    private SpannableStringBuilder addSubtitleSpan(SpannableStringBuilder text){
        text.setSpan(new RelativeSizeSpan(SUBTITLE), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    private SpannableStringBuilder addItalicSpan(SpannableStringBuilder text){
        text.setSpan(new StyleSpan(Typeface.ITALIC), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    private SpannableStringBuilder addBoldSpan(SpannableStringBuilder text){
        text.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    private SpannableStringBuilder addBoldItalicSpan(SpannableStringBuilder text){
        text.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    private SpannableStringBuilder removeStyleSpan(SpannableStringBuilder text){
        StyleSpan[] spans = text.getSpans(0,text.length(),StyleSpan.class);
        text.removeSpan(spans[0]);
        return text;
    }

    private SpannableStringBuilder updateTextSpan(SpannableStringBuilder originalText, SpannableStringBuilder newText, int[] boundaries){
        SpannableStringBuilder updatedText = new SpannableStringBuilder();
        /* This gets a bit hacky, but I couldn't figure out a better solution.
                                Instead of using replace(), the content from the EditText is
                                reconstructed around the current line, from which the
                                old span has been removed. The reason being that replace()
                                was preserving the old span, and I couldn't figure out how to
                                remove it.
                             */
        if(boundaries[0]== 0){
            updatedText.append(newText);
        } else{
            CharSequence textBeforeCurrentLine = originalText.subSequence(0,boundaries[0]);
            updatedText.append(textBeforeCurrentLine);
            updatedText.append(newText);
        }
        if(boundaries[1]!= originalText.length()){
            CharSequence textAfterCurrentLine = originalText.subSequence(boundaries[1], originalText.length());
            updatedText.append(textAfterCurrentLine);
        }
        return updatedText;
    }

    private static void setBoldToActive(){
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_bold);
        item.setIcon(R.drawable.ic_bold_active);
        boldIsActive = true;
        if(italicIsActive){
            setBoldItalicToActive();
        }
    }

    private static void setItalicToActive(){
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_italic);
        item.setIcon(R.drawable.ic_italic_active);
        italicIsActive = true;
        if(boldIsActive){
            setBoldItalicToActive();
        }
    }

    private static void setBoldItalicToActive(){
        boldItalicIsActive = true;
    }

    private static void setTitleToActive(){
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_format_size);
        item.setIcon(R.drawable.ic_title_active);
        subtitleIsActive = false;
        titleIsActive = true;
    }

    private static void setSubtitleToActive(){
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_format_size);
        item.setIcon(R.drawable.ic_subtitle_active);
        titleIsActive = false;
        subtitleIsActive = true;
    }

    private static void setUnformattedListToActive(){
        unformattedListIsActive = true;
    }

    private static void setFormattedListToActive(){
        formattedListIsActive = true;
    }

    private static void setBoldToInactive(){
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_bold);
        item.setIcon(R.drawable.ic_bold_inactive);
        boldIsActive = false;
        if(boldItalicIsActive){
            setBoldItalicToInactive();
        }
    }

    private static void setItalicToInactive(){
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_italic);
        item.setIcon(R.drawable.ic_italic_inactive);
        italicIsActive = false;
        if(boldItalicIsActive){
            setBoldItalicToInactive();
        }
    }

    private static void setBoldItalicToInactive(){
        boldItalicIsActive = false;
    }

    private static void setTitleToInactive(){
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_format_size);
        item.setIcon(R.drawable.ic_title_inactive);
        titleIsActive = false;
    }

    private static void setSubtitleToInactive(){
        MenuItem item = toolbarBottom.getMenu().findItem(R.id.action_format_size);
        item.setIcon(R.drawable.ic_title_inactive);
        subtitleIsActive = false;
    }

    private static void setUnformattedListToInactive(){
        unformattedListIsActive = false;
    }

    private static void setFormattedListToInactive(){
        formattedListIsActive = false;
    }

    private static void setAllToInactive(){
        setBoldToInactive();
        setItalicToInactive();
        setBoldItalicToInactive();
        setTitleToInactive();
        setSubtitleToInactive();
        setFormattedListToInactive();
        setUnformattedListToInactive();
    }


    public static class EditTextCursorWatcher extends EditText {

        private int lastCursorPos;

        public EditTextCursorWatcher(Context context, AttributeSet attrs,
                                     int defStyle) {
            super(context, attrs, defStyle);
        }

        public EditTextCursorWatcher(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public EditTextCursorWatcher(Context context) {
            super(context);
        }

        @Override
        protected void onSelectionChanged(int selStart, int selEnd) {
            if(selStart == selEnd) {
                if(selStart != lastCursorPos && selStart != lastCursorPos + 1) {
                    if (getText() != null) {
                        SpannableString currentLine = getCurrentLine(selStart, this);
                        SpannableString currentWord = getCurrentWord(selStart, this);
                        StyleSpan[] wordSpans = currentWord.getSpans(0, currentWord.length(), StyleSpan.class);
                        RelativeSizeSpan[] lineSpans = currentLine.getSpans(0, currentLine.length(), RelativeSizeSpan.class);
                        if(wordSpans.length > 0){
                            for(StyleSpan span: wordSpans){
                                switch (span.getStyle()) {
                                    case Typeface.BOLD:
                                        setBoldToActive();
                                        break;
                                    case Typeface.ITALIC:
                                        setItalicToActive();
                                        break;
                                    case Typeface.BOLD_ITALIC:
                                        setBoldItalicToActive();
                                        break;
                                }
                            }
                        }
                        if (lineSpans.length > 0) {
                            for (RelativeSizeSpan span : lineSpans) {
                                float style = span.getSizeChange();
                                if (style == TITLE) {
                                    setTitleToActive();
                                } else if (style == SUBTITLE) {
                                    setSubtitleToActive();
                                }
                            }
                        }
                        if(wordSpans.length == 0 && lineSpans.length == 0){
                            setAllToInactive();
                        }
                    }
                }
                lastCursorPos = selStart;
            }
        }
    }

}
