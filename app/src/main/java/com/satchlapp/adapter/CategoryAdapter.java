package com.satchlapp.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.satchlapp.R;
import com.satchlapp.model.Category;

import java.util.List;

/**
 * Created by Sara on 3/9/2017.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;

    public CategoryAdapter(List<Category> categories){
        this.categories = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.list_item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder viewHolder, int position){
        Category category = categories.get(position);
        viewHolder.textViewCategoryTitle.setText(category.getTitle());
        StoryCardViewAdapter adapter = new StoryCardViewAdapter(category);
        viewHolder.storyRecyclerView.setAdapter(adapter);
        viewHolder.storyRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public int getItemCount(){
       return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewCategoryTitle;
        public RecyclerView storyRecyclerView;

        public CategoryViewHolder(View itemView){
            super(itemView);
            textViewCategoryTitle = (TextView) itemView.findViewById(R.id.listItemCategoryTextViewCategoryTitle);
            storyRecyclerView = (RecyclerView) itemView.findViewById(R.id.listItemCategoryRecyclerView);
        }
    }
}
