package dk.tonsser.controllers.font;
/**
 * @author Casper Rasmussen - 2012
 */
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import java.util.HashMap;

import dk.tonsser.utils.NLog;

public class NFontContainer {
    private static NFontContainer instance;

    private HashMap<String,NFontModel> fontList = new HashMap<String,NFontModel>();

    public static NFontContainer getInstance(){
        if(instance == null)
            instance = new NFontContainer();

        return instance;
    }

    public void setFontList(HashMap<String,NFontModel> fontList){
        if(fontList!=null)
            this.fontList = fontList;
        else
            NLog.w("NFontController setFontList", "fontList input was null, did'nt set list");
    }

    public void clearFontList(){
        fontList.clear();
    }

    public void addToFontList(NFontModel mNFontModel){
        fontList.put(mNFontModel.getType(),mNFontModel);
    }

    public HashMap<String,NFontModel> getFontList(){
        return fontList;
    }
    /**
     * This method will loop
     * @param type
     * @return
     */
    public NFontModel getNFontModel(String type){
        NFontModel tempNFontModel = fontList.get(type);
        if(tempNFontModel!=null)
            return tempNFontModel;
        else{
            NLog.w("NFontController NFontModel","No NFontModel got found with given type: "+type+" returning null");
            return null;
        }
    }

    /**
     * This method will loop
     * @param type
     * @return
     */
    public NFontModel getNFontModel(char type){
        return getNFontModel(String.valueOf(type));
    }

    /**
     * This method will apply the font matching the type in input, on all the TextViews listed
     * If Typeface is null or any TextViews is null, it skip them and log.w
     * @param type
     * @param textViewList
     */
    public void setFont(String type, TextView...textViewList) {
        NFontModel mNFontModel = getNFontModel(type);
        if(mNFontModel!=null){
            int textviewIndex = 0;
            for(TextView item : textViewList){
                textviewIndex++;
                try {
                    item.setTypeface(mNFontModel.getFont());

                    item.setPaintFlags( item.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG );
                    if(mNFontModel.isSizeApplied()) {//Appy size if it was set.
                        item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mNFontModel.getSizeInPixels());
                    }
                } catch (Exception e) {
                    NLog.w("NFontController setFont", "TextView at index " + textviewIndex + " was null, and didn't get the typeface applied. Stacktrace found on next log-line:");
                    e.printStackTrace();
                }
            }
        }
        else
            NLog.w("NFontController setFont", "NFontModel was not found: "+type+", no typeface applied");
    }

    /**
     * This method will apply the font matching the type in input, on all the TextViews listed
     * If Typeface is null or any TextViews is null, it skip them and log.w
     * @param type
     * @param textViewList
     */
    public void setFont(char type, TextView...textViewList) {
        setFont(String.valueOf(type), textViewList);
    }
    /**
     * Use this method if you just need to apply a Typeface on a list of TextView
     * If Typeface is null or any TextViews is null, it skip them and log.w
     * @param font
     * @param textViewList
     */
    public static void setFont(Typeface font, TextView...textViewList){
        if(font==null){
            NLog.w("NFontController setFont", "font was null, returning wihtout applying anything");
        }

        for(TextView item : textViewList){
            if(item!=null){
                item.setTypeface(font);
                item.setPaintFlags( item.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG );
            }
            else
                NLog.w("NFontController setFont", "One of the TextViews was null, and did'nt get the typeface applied");
        }
    }

    /**
     * Use this method to load a typeface from assets folder
     * @param mContext
     * @param path, fx "fonts/Berthold Akzidenz Grotesk BE Light Condensed.ttf"
     * @return Typeface
     */
    public static Typeface getTypeFontFromPath(Context mContext, String path) throws Exception {
        return Typeface.createFromAsset(mContext.getAssets(), path);
    }
}
