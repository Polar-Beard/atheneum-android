package com.satchlapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.satchlapp.R;
import com.satchlapp.list.Constants;
import com.satchlapp.model.Content;
import com.satchlapp.model.Story;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sara on 2/21/2017.
 */
public class ReadableViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Content> contents;

    public  ReadableViewAdapter(List<Content> contents) {
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
                itemView = inflater.inflate(R.layout.list_item_textview, parent, false);
                viewHolder = new TextViewHolder(itemView);
                break;
            case Constants.CONTENT_TYPE_IMAGE_WITH_CAPTION:
                itemView = inflater.inflate(R.layout.list_item_image_with_caption, parent, false);
                viewHolder = new ImageViewWithCaptionViewHolder(itemView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Content content = contents.get(position);

        if(viewHolder instanceof TextViewHolder){
       TextViewHolder textViewHolder = (TextViewHolder) viewHolder;
            textViewHolder.text.setText(
                    Story.parseTextContent(content),
                    TextView.BufferType.SPANNABLE
            );
        }
        else if(viewHolder instanceof ImageViewWithCaptionViewHolder) {
            Picasso.with(context)
                    .load(content.getValue())
                    .fit()
                    .centerCrop()
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

    class TextViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public TextViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.listItemTextView);
            text.setTypeface(
                    Typeface.createFromAsset(context.getAssets(), "fonts/Merriweather-Regular.ttf")
            );
        }
    }

    class ImageViewWithCaptionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ImageViewWithCaptionViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.listItemImageWithCaptionImageView);
            textView = (TextView) itemView.findViewById(R.id.listItemImageWithCaptionTextView);
        }
    }
}
