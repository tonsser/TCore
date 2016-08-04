package com.tonsser.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.Nullable;

/**
 * API-safe wrapper around
 * Created by mark on 04/08/16.
 */
public class TResources {
    /**
     * Resolves a color from the provided context's resources.
     * @param context    The current context.
     * @param resId      The identifier of the resource.
     * @return A color-encoded int.
     */
    public static int getColor(Context context, int resId) {
        int color = 0;

        // if context is null, we will just send back 'black'
        if (context == null) {
            return color;
        }

        if (Build.VERSION.SDK_INT >= 23) {
            color = context.getResources().getColor(resId, context.getTheme());
        }
        else {
            color = context.getResources().getColor(resId);
        }

        return color;
    }

    /**
     * Resolves a `ColorStateList` from the given context's resources.
     * @param context   The current context.
     * @param resId     The resource identifier.
     * @return A {@code ColorStateList} object or null if the context was null
     * or the resource could not be found.
     */
    @Nullable
    public static ColorStateList getColorStateList(Context context, int resId) {
        ColorStateList csl;

        if (context == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT >= 23) {
            csl = context.getResources().getColorStateList(resId, context.getTheme());
        }
        else {
            csl = context.getResources().getColorStateList(resId);
        }

        return csl;
    }
}
