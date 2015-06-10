package dk.nodes.widgets.listviews;
import android.view.View;
import android.widget.ListView;

import java.util.Dictionary;
import java.util.Hashtable;

import dk.nodes.utils.NLog;

/**
 * @author Casper Rasmussen - 2013
 */
public class NListViewController {

	/**
	 * Use this method to check if the last visible child is shown, call this on scroll-events
	 * Before requesting more entries, make sure to check that limit of the first request was max.
	 * Also make sure to only start the request for more entries once and wait for callback before enabling option to request again.
	 *
	 * Instead of using ScrollListener, use the setOnLoadNextPageListener in NBaseAdapter, seems more stabil. For unknown reasons
	 * The scrollListener is not working in some views
	 *
	 * @param mListView
	 * @return
	 */
	@Deprecated
	public static boolean isListScrolledToBottom(ListView mListView){
		if(mListView == null || mListView.getAdapter() == null){
			NLog.w("NListViewFooterProgressInFillController isListScrolledToBottom","ListView or adapter is null, returning false");
			return false;
		}

		if ((mListView.getFirstVisiblePosition() + mListView.getChildCount()) >= mListView.getAdapter().getCount()){
			return true;
		}
		else
			return false;
	}


	public static int getScrollValue(ListView lv) {
		Dictionary<Integer, Integer> listViewItemHeights = new Hashtable<Integer, Integer>();
		if(lv == null)
			return 0;

	    View c = lv.getChildAt(0); //this is the first visible row

	    if(c == null)
	    	return 0;

	    int scrollY = -c.getTop();
	    listViewItemHeights.put(lv.getFirstVisiblePosition(), c.getHeight());
	    for (int i = 0; i < lv.getFirstVisiblePosition(); ++i) {
	        if (listViewItemHeights.get(i) != null) // (this is a sanity check)
	            scrollY += listViewItemHeights.get(i); //add all heights of the views that are gone
	    }
	    return scrollY;
	}
}
