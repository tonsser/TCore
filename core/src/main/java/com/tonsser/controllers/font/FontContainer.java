package com.tonsser.controllers.font;
/**
 * @author Casper Rasmussen - 2012
 */

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import com.tonsser.utils.TLog;

import java.util.HashMap;

public class FontContainer {
    private static FontContainer instance;

    private HashMap<String, FontModel> fontList = new HashMap<String, FontModel>();

    public static FontContainer getInstance() {
        if (instance == null)
            instance = new FontContainer();

        return instance;
    }

    public void setFontList(HashMap<String, FontModel> fontList) {
        if (fontList != null)
            this.fontList = fontList;
        else
            TLog.w("NFontController setFontList", "fontList input was null, did'nt set list");
    }

    public void clearFontList() {
        fontList.clear();
    }

    public void addToFontList(FontModel mFontModel) {
        fontList.put(mFontModel.getType(), mFontModel);
    }

    public HashMap<String, FontModel> getFontList() {
        return fontList;
    }

    /**
     * This method will loop
     *
     * @param type
     * @return
     */
    public FontModel getNFontModel(String type) {
        FontModel tempFontModel = fontList.get(type);
        if (tempFontModel != null)
            return tempFontModel;
        else {
            TLog.w("NFontController FontModel", "No FontModel got found with given type: " + type + " returning null");
            return null;
        }
    }

    /**
     * This method will loop
     *
     * @param type
     * @return
     */
    public FontModel getNFontModel(char type) {
        return getNFontModel(String.valueOf(type));
    }

    /**
     * This method will apply the font matching the type in input, on all the TextViews listed
     * If Typeface is null or any TextViews is null, it skip them and log.w
     *
     * @param type
     * @param textViewList
     */
    public void setFont(String type, TextView... textViewList) {
        FontModel mFontModel = getNFontModel(type);
        if (mFontModel != null) {
            int textviewIndex = 0;
            for (TextView item : textViewList) {
                textviewIndex++;
                try {
                    item.setTypeface(mFontModel.getFont());

                    item.setPaintFlags(item.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
                    if (mFontModel.isSizeApplied()) {//Appy size if it was set.
                        item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mFontModel.getSizeInPixels());
                    }
                } catch (Exception e) {
                    TLog.w("NFontController setFont", "TextView at index " + textviewIndex + " was null, and didn't get the typeface applied. Stacktrace found on next log-line:");
                    e.printStackTrace();
                }
            }
        } else
            TLog.w("NFontController setFont", "FontModel was not found: " + type + ", no typeface applied");
    }

    /**
     * This method will apply the font matching the type in input, on all the TextViews listed
     * If Typeface is null or any TextViews is null, it skip them and log.w
     *
     * @param type
     * @param textViewList
     */
    public void setFont(char type, TextView... textViewList) {
        setFont(String.valueOf(type), textViewList);
    }

    /**
     * Use this method if you just need to apply a Typeface on a list of TextView
     * If Typeface is null or any TextViews is null, it skip them and log.w
     *
     * @param font
     * @param textViewList
     */
    public static void setFont(Typeface font, TextView... textViewList) {
        if (font == null) {
            TLog.w("NFontController setFont", "font was null, returning wihtout applying anything");
        }

        for (TextView item : textViewList) {
            if (item != null) {
                item.setTypeface(font);
                item.setPaintFlags(item.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            } else
                TLog.w("NFontController setFont", "One of the TextViews was null, and did'nt get the typeface applied");
        }
    }

    /**
     * Use this method to load a typeface from assets folder
     *
     * @param mContext
     * @param path,    fx "fonts/Berthold Akzidenz Grotesk BE Light Condensed.ttf"
     * @return Typeface
     */
    public static Typeface getTypeFontFromPath(Context mContext, String path) throws Exception {
        return Typeface.createFromAsset(mContext.getAssets(), path);
    }
}
