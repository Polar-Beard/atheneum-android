package com.satchlapp.activity;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.satchlapp.R;

public class StoryPreviewActivity extends AppCompatActivity {

    private static final int ANIM_DURATION = 500;
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();

    private FrameLayout topLevelLayout;
    private FrameLayout colorBar;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private FrameLayout frameLayoutButtonContainer;
    private Button continueButton;

    private ColorDrawable backgroundDrawable;

    private int titleLeftDelta;
    private int titleTopDelta;
    private float titleWidthScale;
    private float titleHeightScale;

    private int descriptionLeftDelta;
    private int descriptionTopDelta;
    private float descriptionWidthScale;
    private float descriptionHeightScale;

    private boolean backgroundAnimationIsRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_preview);

        topLevelLayout = (FrameLayout) findViewById(R.id.activityStoryPreviewTopLevelLayout);
        colorBar = (FrameLayout) findViewById(R.id.activityStoryPreviewFrameLayoutColorBar);
        textViewTitle = (TextView) findViewById(R.id.activityStoryPreviewTextViewTitle);
        textViewDescription = (TextView) findViewById(R.id.activityStoryPreviewTextViewDescription);
        frameLayoutButtonContainer = (FrameLayout) findViewById(R.id.activityStoryPreviewButtonContainer);
        continueButton = (Button) findViewById(R.id.activityStoryContinueButton);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title");
        String description = bundle.getString("description");
        final int originalTitleTop = bundle.getInt("titleTop");
        final int originalTitleLeft = bundle.getInt("titleLeft");
        final int originalDescriptionTop = bundle.getInt("descriptionTop");
        final int originalDescriptionLeft = bundle.getInt("descriptionLeft");
        final int originalTitleWidth = bundle.getInt("titleWidth");
        final int originalTitleHeight = bundle.getInt("titleHeight");
        final int originalDescriptionWidth = bundle.getInt("descriptionWidth");
        final int originalDescriptionHeight = bundle.getInt("descriptionHeight");
        int titleColor = bundle.getInt("titleColor");

        textViewTitle.setText(title);
        textViewDescription.setText(description);

        colorBar.setBackgroundColor(titleColor);

        backgroundDrawable = new ColorDrawable(Color.GRAY);

        topLevelLayout.setBackgroundDrawable(backgroundDrawable);

        Drawable drawable = getResources().getDrawable(R.drawable.border_top);
        drawable.setColorFilter(titleColor, PorterDuff.Mode.OVERLAY);
        frameLayoutButtonContainer.setBackgroundDrawable(drawable);

        if(savedInstanceState == null){
            ViewTreeObserver titleObserver = textViewTitle.getViewTreeObserver();
            titleObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    textViewTitle.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    textViewTitle.getLocationOnScreen(screenLocation);
                    titleLeftDelta = originalTitleLeft - screenLocation[0];
                    titleTopDelta = originalTitleTop - screenLocation[1];
                    titleWidthScale = (float) originalTitleWidth / textViewTitle.getWidth();
                    titleHeightScale = (float) originalTitleHeight / textViewTitle.getHeight();

                    runViewAnimation(textViewTitle, titleWidthScale, titleHeightScale,
                            titleLeftDelta, titleTopDelta);

                    return true;
                }
            });

            ViewTreeObserver descriptionObserver = textViewDescription.getViewTreeObserver();
            descriptionObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    textViewDescription.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    textViewDescription.getLocationOnScreen(screenLocation);
                    descriptionLeftDelta = originalDescriptionLeft - screenLocation[0];
                    descriptionTopDelta = originalDescriptionTop - screenLocation[1];
                    descriptionWidthScale = (float) originalDescriptionWidth / textViewDescription.getWidth();
                    descriptionHeightScale = (float) originalDescriptionHeight / textViewDescription.getHeight();

                    runViewAnimation(textViewDescription, descriptionWidthScale, descriptionHeightScale,
                            descriptionLeftDelta, descriptionTopDelta);

                    return true;
                }
            });

            ViewTreeObserver colorBarObserver = colorBar.getViewTreeObserver();
            colorBarObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    colorBar.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    colorBar.getLocationOnScreen(screenLocation);
                    int leftDelta = originalTitleLeft - screenLocation[0];
                    int topDelta = originalTitleTop - screenLocation[1];
                    float widthScale = (float) originalTitleWidth / colorBar.getWidth();
                    float heightScale = (float) originalTitleHeight / colorBar.getHeight();

                    runViewAnimation(colorBar, widthScale, heightScale,
                            leftDelta, topDelta);

                    return true;
                }
            });
        }
    }

    private void runViewAnimation(View textView, float scaleX, float scaleY,
                                  int translationX, int translationY){
        textView.setPivotX(0);
        textView.setPivotY(0);
        textView.setScaleX(scaleX);
        textView.setScaleY(scaleY);
        textView.setTranslationX(translationX);
        textView.setTranslationY(translationY);

        textView.animate().setDuration((long) ANIM_DURATION)
                .scaleX(1).scaleY(1)
                .translationX(0).translationY(0)
                .setInterpolator(sDecelerator);

        if(!backgroundAnimationIsRunning) {
            runBackgroundAnimation();
        }
    }

    private void runBackgroundAnimation(){
        backgroundAnimationIsRunning = true;

        ObjectAnimator backgroundAnimator = ObjectAnimator.ofInt(backgroundDrawable,"alpha",0, 166);
        backgroundAnimator.setDuration((long) ANIM_DURATION);
        backgroundAnimator.start();
    }
}
