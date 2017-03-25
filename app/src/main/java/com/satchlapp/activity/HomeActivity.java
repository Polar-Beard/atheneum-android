package com.satchlapp.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.satchlapp.R;
import com.satchlapp.adapter.CategoryAdapter;
import com.satchlapp.listener.OnNavigationItemSelectedListener;
import com.satchlapp.model.Category;
import com.satchlapp.model.Story;
import com.satchlapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategories;

    private String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce eu felis ex. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur tristique nibh condimentum, sodales nisl in, eleifend lorem. Phasellus suscipit elementum vestibulum. Donec interdum eget dolor sit amet gravida. Fusce non eros nec mi cursus hendrerit non eget felis. Vivamus ultricies, est id lobortis efficitur, arcu odio tristique ante, sit amet interdum nisi turpis vel mauris. Fusce eu metus eget sapien porta lacinia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBarHomeToolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activityHomeDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activityHomeNavigationView);
        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener(this, drawer));

        //Testing Data
        User author = new User();
        author.setFirstName("Thomas");
        author.setLastName("Reddingfield");
        author.setProfilePictureUrl("http://res.cloudinary.com/dar5b86jq/image/upload/v1488260503/86vjr5evkqmlq3jjx7xk.jpg");

        List<Story> storyList = new ArrayList<>();
        for(int i = 1; i < 6; i++){
            Story story = new Story();
            story.setTitle("This Is A Story With Kinda A Long Title: " + i);
            story.setDescription(loremIpsum);
            story.setCoverImageUrl("https://source.unsplash.com/random");
            story.setAuthor(author);
            storyList.add(story);
        }

        List<Category> categoryList = new ArrayList<>();
        boolean lastCategoryIsDetailed = false;
        for(int i = 1; i < 11; i++){
            Category category = new Category();
            category.setTitle("Category:" + i);
            category.setStoryList(storyList);
            if(!lastCategoryIsDetailed){
                category.setDisplayTypeDetailed();
                lastCategoryIsDetailed = true;
            } else{
                category.setDisplayTypeSimple();
                lastCategoryIsDetailed = false;
            }
            categoryList.add(category);
        }

        recyclerViewCategories = (RecyclerView) findViewById(R.id.contentHomeRecyclerView);
        CategoryAdapter adapter = new CategoryAdapter(categoryList);
        recyclerViewCategories.setAdapter(adapter);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initAnimations(){
        Animator cardFlipLeftIn = AnimatorInflater.loadAnimator(this, R.animator.card_flip_left_in);
        Animator cardFlipLeftOut = AnimatorInflater.loadAnimator(this, R.animator.card_flip_left_out);
        Animator cardFlipRightIn = AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_in);
        Animator cardFlipRightOut = AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_out);

        //animatorSet = new AnimatorSet();
        //animatorSet.playSequentially(cardFlipLeftIn,cardFlipLeftOut,cardFlipRightIn,cardFlipRightOut);
    }
}
