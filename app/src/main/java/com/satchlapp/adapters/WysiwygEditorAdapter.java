package com.satchlapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.satchlapp.model.Story;
import com.satchlapp.util.TextContentFormatter;
import com.satchlapp.view.EditTextCursorWatcher;
import com.satchlapp.view.OnSelectionChangedListener;
import com.squareup.picasso.Picasso;

import com.satchlapp.R;
import com.satchlapp.lists.Constants;
import com.satchlapp.model.Content;

import java.util.List;
import java.util.Map;

/**
 * Created by Sara on 2/14/2017.
 */
public class WysiwygEditorAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Content> contents;
    final private Map<Integer,MenuItem> menuItemMap;
    private TextContentFormatter formatter;

    public WysiwygEditorAdapter(List<Content> contents, Map<Integer,MenuItem> menuItemMap) {
        this.contents = contents;
        this.menuItemMap = menuItemMap;
        formatter = new TextContentFormatter();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case Constants.CONTENT_TYPE_TEXT:
                itemView = inflater.inflate(R.layout.list_item_wysiwyg_text, parent, false);
                viewHolder = new EditTextViewHolder(itemView);
                break;
            case Constants.CONTENT_TYPE_IMAGE_WITH_CAPTION:
                itemView = inflater.inflate(R.layout.list_item_wysiwyg_image, parent, false);
                viewHolder = new ImageViewWithCaptionViewHolder(itemView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Content content = contents.get(position);

        if(viewHolder instanceof EditTextViewHolder){
            EditTextViewHolder editTextViewHolder = (EditTextViewHolder) viewHolder;
            editTextViewHolder.editText.setText(
                    Story.parseTextContent(content),
                    TextView.BufferType.EDITABLE
            );
            editTextViewHolder.editText.setSelection(
                    content.getValue().length()
            );
            editTextViewHolder.editText.setOnSelectionChangedListener(
                    new OnSelectionChangedListener() {
                        int lastCursorPosition = 0;

                        @Override
                        public void onSelectionChanged(int selStart, int selEnd) {
                            if(selStart != lastCursorPosition
                                    && selStart != 0){
                                lastCursorPosition = selStart;
                                formatter.findActiveFormattingPositions(content, selStart);
                                menuItemMap.get(R.id.action_bold).setIcon(R.drawable.ic_bold_inactive);
                                menuItemMap.get(R.id.action_format_size).setIcon(R.drawable.ic_title_inactive);
                                menuItemMap.get(R.id.action_italic).setIcon(R.drawable.ic_italic_inactive);
                                if(formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_BOLD)){
                                    menuItemMap.get(R.id.action_bold).setIcon(R.drawable.ic_bold_active);
                                }
                                if(formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_ITALIC)){
                                    menuItemMap.get(R.id.action_italic).setIcon(R.drawable.ic_italic_active);
                                }
                                if(formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_BOLD_ITALIC)){
                                    menuItemMap.get(R.id.action_bold).setIcon(R.drawable.ic_bold_active);
                                    menuItemMap.get(R.id.action_italic).setIcon(R.drawable.ic_italic_active);
                                }
                                if(formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_TITLE)){
                                    menuItemMap.get(R.id.action_format_size).setIcon(R.drawable.ic_title_active);
                                }
                                if(formatter.isFormatActive(Constants.QUALIFIER_TYPE_TEXT_SUBTITLE)){
                                    menuItemMap.get(R.id.action_format_size).setIcon(R.drawable.ic_subtitle_active);
                                }
                            }
                        }
                    });

        }
        else if(viewHolder instanceof ImageViewWithCaptionViewHolder) {
            Picasso.with(context)
                    .load(content.getValue())
                    .into(((ImageViewWithCaptionViewHolder) viewHolder).imageView);
        }

    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public int getItemViewType(int position) {
        return contents.get(position).getType();
    }

    class EditTextViewHolder extends RecyclerView.ViewHolder {
        EditTextCursorWatcher editText;

        public EditTextViewHolder(View itemView) {
            super(itemView);
            editText = (EditTextCursorWatcher) itemView.findViewById(R.id.listItemWysiwygTextEditText);
            editText.setTypeface(
                    Typeface.createFromAsset(context.getAssets(),"fonts/Merriweather-Light.ttf")
            );
        }
    }

    class ImageViewWithCaptionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        EditText editText;

        public ImageViewWithCaptionViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.listItemWysiwygImageImageView);
            editText = (EditText) itemView.findViewById(R.id.listItemWysiwygImageEditText);
        }
    }
}