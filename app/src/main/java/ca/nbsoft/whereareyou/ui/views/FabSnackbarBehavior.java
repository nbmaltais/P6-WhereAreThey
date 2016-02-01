package ca.nbsoft.whereareyou.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by Nicolas on 30/01/2016.
 * This class is ripped and adapted from the design support library
 */
public class FabSnackbarBehavior  extends CoordinatorLayout.Behavior<View> {
    private ValueAnimator mFabTranslationYAnimator;
    private float mFabTranslationY;

    public FabSnackbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,
                                   View child, View dependency) {
        // We're dependent on all SnackbarLayouts (if enabled)
        return  dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child,
                                          View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            updateFabTranslationForSnackbar(parent, child, dependency);
        }
        return false;
    }



    private void updateFabTranslationForSnackbar(CoordinatorLayout parent,
                                                 final View fab, View snackbar) {
        if (fab.getVisibility() != View.VISIBLE) {
            return;
        }

        final float targetTransY = getFabTranslationYForSnackbar(parent, fab);
        if (mFabTranslationY == targetTransY) {
            // We're already at (or currently animating to) the target value, return...
            return;
        }

        final float currentTransY = ViewCompat.getTranslationY(fab);

        // Make sure that any current animation is cancelled
        if (mFabTranslationYAnimator != null && mFabTranslationYAnimator.isRunning()) {
            mFabTranslationYAnimator.cancel();
        }

        if (Math.abs(currentTransY - targetTransY) > (fab.getHeight() * 0.667f)) {
            // If the FAB will be travelling by more than 2/3 of it's height, let's animate
            // it instead
            if (mFabTranslationYAnimator == null) {
                mFabTranslationYAnimator = new ValueAnimator();
                mFabTranslationYAnimator.setInterpolator(new FastOutSlowInInterpolator());
                mFabTranslationYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        fab.setTranslationY( (float)animation.getAnimatedValue() );
                    }
                });


                /*mFabTranslationYAnimator.setUpdateListener(
                        new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                ViewCompat.setTranslationY(fab,
                                        animator.getAnimatedFloatValue());
                            }
                        });*/
            }
            mFabTranslationYAnimator.setFloatValues(currentTransY, targetTransY);
            mFabTranslationYAnimator.start();
        } else {
            // Now update the translation Y
            ViewCompat.setTranslationY(fab, targetTransY);
        }

        mFabTranslationY = targetTransY;
    }

    private float getFabTranslationYForSnackbar(CoordinatorLayout parent,
                                                View fab) {
        float minOffset = 0;
        final List<View> dependencies = parent.getDependencies(fab);
        for (int i = 0, z = dependencies.size(); i < z; i++) {
            final View view = dependencies.get(i);
            if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset,
                        ViewCompat.getTranslationY(view) - view.getHeight());
            }
        }

        return minOffset;
    }
}
