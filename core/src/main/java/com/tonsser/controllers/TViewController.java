package com.tonsser.controllers;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tonsser.utils.TViewPadding;

public class TViewController {

    public static void setViewAndChildsEnable(View view, boolean enabled) {
        view.setEnabled(enabled);

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int idx = 0; idx < group.getChildCount(); idx++) {
                setViewAndChildsEnable(group.getChildAt(idx), enabled);
            }
        }
    }

    public static void setViewsVisisble(int visibility, View... views) {
        for (View item : views)
            if (item != null)
                item.setVisibility(visibility);
    }

    public static void setViewsEnabled(boolean enabled, View... views) {
        for (View item : views)
            if (item != null)
                item.setEnabled(enabled);
    }

    public static void bringToFront(View... views) {
        for (View item : views)
            if (item != null)
                item.bringToFront();
    }

    public static TViewPadding getPaddings(View mView) {
        return new TViewPadding(mView.getPaddingTop(), mView.getPaddingTop(), mView.getPaddingLeft(), mView.getPaddingRight());
    }

    public static void setPaddings(View mView, TViewPadding mTViewPadding) {
        mView.setPadding(mTViewPadding.getLeft(), mTViewPadding.getTop(), mTViewPadding.getRight(), mTViewPadding.getBottom());
    }

    /**
     * Use this to center the text correctly on a button that has a left/right compound drawable.
     *
     * @param button With either left or right compound-drawable, where the text isn't centered because of the drawable.
     */
    public static void forceCenterText(Button... button) {

        for (Button mButton : button) {

            Drawable[] drawables = mButton.getCompoundDrawables();

            // make sure we do not get any nullpointers if the layout XML changes
            if ((drawables != null) && (drawables.length > 0) && (drawables[0] != null || drawables[2] != null)) {
                // fetch drawableLeft
                Drawable drawableLeft = drawables[0];
                Drawable drawableRight = drawables[2];

                // get the width of the image
                int width = 0;

                if (drawableLeft != null) {
                    width = drawableLeft.getIntrinsicWidth();

                    // Set the padding on the button to match the previous padding
                    // but add the image width to the right to center the text
                    mButton.setPadding(mButton.getPaddingLeft(),
                            mButton.getPaddingTop(), mButton.getPaddingRight() + width,
                            mButton.getPaddingBottom());
                } else if (drawableRight != null) {
                    width = drawableRight.getIntrinsicWidth();

                    // Set the padding on the button to match the previous padding
                    // but add the image width to the left to center the text
                    mButton.setPadding(mButton.getPaddingLeft() + width,
                            mButton.getPaddingTop(), mButton.getPaddingRight(),
                            mButton.getPaddingBottom());
                }
            }
        }
    }
}