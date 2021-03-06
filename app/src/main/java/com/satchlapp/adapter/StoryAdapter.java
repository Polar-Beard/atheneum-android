package com.satchlapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.satchlapp.R;
import com.satchlapp.bind.StoryViewHolder;
import com.satchlapp.model.Story;

/**
 * Created by Sara on 9/20/2016.
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryViewHolder> {

    private List<Story> stories;
    private static View.OnClickListener onClickListener;

    public StoryAdapter(List<Story> stories, View.OnClickListener onClickListener) {
        this.stories = stories;
        this.onClickListener = onClickListener;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View storyView = inflater.inflate(R.layout.list_item_story, parent, false);
        storyView.setOnClickListener(onClickListener);
        return new StoryViewHolder(storyView);
    }

    @Override
    public void onBindViewHolder(StoryViewHolder holder, int position){
        Story story = stories.get(position);
        View view = holder.itemView;
        TextView title = (TextView) view.findViewById(R.id.list_item_story_title);
        TextView description = (TextView) view.findViewById(R.id.list_item_story_description);
        title.setText(story.getTitle());
        description.setText(story.getDescription());
    }

    @Override
    public int getItemCount(){
        return stories.size();
    }
}
