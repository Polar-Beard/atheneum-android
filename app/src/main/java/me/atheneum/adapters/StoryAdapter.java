package me.atheneum.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.atheneum.R;
import me.atheneum.bind.StoryViewHolder;
import me.atheneum.model.Story;

/**
 * Created by Sara on 9/20/2016.
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryViewHolder> {
    private List<Story> stories;

    public StoryAdapter(List<Story> stories) {
        this.stories = stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View storyView = inflater.inflate(R.layout.list_item_story, parent, false);
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
