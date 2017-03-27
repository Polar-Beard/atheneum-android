package com.satchlapp.util;

import android.animation.Animator;
import android.view.View;

/**
 * Created by saram on 3/27/2017.
 */

public class CustomAnimation {

    private static final int SHORT_ANIMATION_DURATION = 300;
    private static final int LONG_ANIMATION_DURATION = 500;

    public static void crossFadeViews(View initialView, View finalView) {
        finalView.setAlpha(0f);
        finalView.setVisibility(View.VISIBLE);

        finalView.animate()
                .setDuration(SHORT_ANIMATION_DURATION)
                .alpha(1f)
                .setListener(null);

        //Reassign parameter to use in inner class
        final View initialView1 = initialView;

        initialView1.animate()
                .setDuration(SHORT_ANIMATION_DURATION)
                .alpha(0f)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        initialView1.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }

}
