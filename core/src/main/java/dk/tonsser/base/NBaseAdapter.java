package dk.tonsser.base;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class NBaseAdapter extends BaseAdapter {

	protected LayoutInflater inflater;
	protected OnLoadNextPage mOnLoadNextPage;
	protected Context mContext;

	public NBaseAdapter(Context mContext){
		inflater = LayoutInflater.from(mContext);
		this.mContext = mContext;
	}

	protected Context getContext(){
		return mContext;
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (observer != null) {
			super.unregisterDataSetObserver(observer);
		}
	}

	/**
	 * Will return true if position is last item, else return false
	 * @param position
	 * @return
	 */
	public boolean isLastItem(int position){
		return (getCount()== position+1) ? true:false;
	}

	/**
	 * Set a listener to know when last item of the list have been inflated in the list
	 * **REMEMBER to add onGetView in getView(), else the 
	 * @param OnLoadNextPage
	 */
	public void setOnLoadNextPageListener(OnLoadNextPage OnLoadNextPage){
		mOnLoadNextPage = OnLoadNextPage;
	}

	/**
	 * This will trigger the onLoadNextPage if
	 * @param position
	 */
	protected void onGetView(int position){
		if(isLastItem(position) && mOnLoadNextPage != null)
			mOnLoadNextPage.onLoadNextPage();
	}

	public interface OnLoadNextPage{
		void onLoadNextPage();
	}
}
