package com.satchlapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.satchlapp.model.Story;
import com.satchlapp.view.EditTextCursorWatcher;
import com.squareup.picasso.Picasso;

import com.satchlapp.R;
import com.satchlapp.lists.Constants;
import com.satchlapp.model.Content;

import java.util.List;

/**
 * Created by Sara on 2/14/2017.
 */
public class WysiwygEditorAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Content> contents;

    public WysiwygEditorAdapter(List<Content> contents) {
        this.contents = contents;
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
        Content content = contents.get(position);

        if(viewHolder instanceof EditTextViewHolder){
            ((EditTextViewHolder) viewHolder).editText.setText(
                    Story.parseTextContent(content),
                    TextView.BufferType.EDITABLE
            );
            ((EditTextViewHolder) viewHolder).editText.setSelection(
                    content.getValue().length()
            );

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