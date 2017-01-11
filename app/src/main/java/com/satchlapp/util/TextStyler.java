package com.satchlapp.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.widget.TextView;

/**
 * Created by Sara on 1/10/2017.
 */
public class TextStyler {

    public static SpannableString applySpacing(CharSequence originalText,float spacing) {
        if (originalText == null) return null;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if (i + 1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if (builder.toString().length() > 1) {
            for (int i = 1; i < builder.toString().length(); i += 2) {
                finalText.setSpan(new ScaleXSpan((spacing + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return finalText;
    }
}
