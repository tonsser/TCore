package dk.tonsser.widgets.spinners;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * Taken from:
 * http://stackoverflow.com/questions/867518/how-to-make-an-android-spinner-with-initial-text-select-one
 * 
 * Decorator Adapter to allow a Spinner to show a 'Nothing Selected...' initially
 * displayed instead of the first choice in the Adapter.
 * 
 * Example of use:
 */

/*
	Spinner spinner = (Spinner) findViewById(R.id.spinner);
	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	spinner.setPrompt("Select your favorite Planet!");
	
	spinner.setAdapter(
			new NothingSelectedSpinnerAdapter(
					adapter,
					R.layout.spinner_row_nothing_selected,
					// R.layout.contact_spinner_nothing_selected_dropdown, // Optional
					this));
 */

public class NNothingSelectedSpinnerAdapter implements SpinnerAdapter, ListAdapter {

	protected static final int EXTRA = 1;
	protected SpinnerAdapter adapter;
	protected Context context;
	protected int nothingSelectedLayout;
	protected int nothingSelectedDropdownLayout;
	protected int nothingSelectedTextId;
	protected LayoutInflater layoutInflater;
	protected String nothingSelectedText;

	/**
	 * Use this constructor to have NO 'Select One...' item, instead use
	 * the standard prompt or nothing at all.
	 * @param spinnerAdapter wrapped Adapter.
	 * @param nothingSelectedLayout layout for nothing selected, perhaps
	 * you want text grayed out like a prompt...
	 * @param nothingSelectedText The text to be displayed when nothing is selected.
	 * @param context
	 */
	public NNothingSelectedSpinnerAdapter(
			SpinnerAdapter spinnerAdapter,
			int nothingSelectedLayout, int nothingSelectedTextId, String nothingSelectedText, Context context) {

		this(spinnerAdapter, nothingSelectedLayout, -1, nothingSelectedTextId, nothingSelectedText, context);
	}

	public NNothingSelectedSpinnerAdapter(
			SpinnerAdapter spinnerAdapter,
			int nothingSelectedLayout, Context context) {

		this(spinnerAdapter, nothingSelectedLayout, -1, -1, null, context);
	}
	/**
	 * Use this constructor to Define your 'Select One...' layout as the first
	 * row in the returned choices.
	 * If you do this, you probably don't want a prompt on your spinner or it'll
	 * have two 'Select' rows.
	 * @param spinnerAdapter wrapped Adapter. Should probably return false for isEnabled(0)
	 * @param nothingSelectedLayout layout for nothing selected, perhaps you want
	 * text grayed out like a prompt...
	 * @param nothingSelectedDropdownLayout layout for your 'Select an Item...' in
	 * the dropdown.
	 * @param nothingSelectedText The text to be displayed when nothing is selected.
	 * @param context
	 */
	public NNothingSelectedSpinnerAdapter(SpinnerAdapter spinnerAdapter,
			int nothingSelectedLayout, int nothingSelectedDropdownLayout, int nothingSelectedTextId, String nothingSelectedText, Context context) {
		this.adapter = spinnerAdapter;
		this.context = context;
		this.nothingSelectedLayout = nothingSelectedLayout;
		this.nothingSelectedTextId = nothingSelectedTextId;
		this.nothingSelectedDropdownLayout = nothingSelectedDropdownLayout;
		this.nothingSelectedText = nothingSelectedText;
		layoutInflater = LayoutInflater.from(context);
	}

	/**
	 * Use this constructor to Define your 'Select One...' layout as the first
	 * row in the returned choices.
	 * If you do this, you probably don't want a prompt on your spinner or it'll
	 * have two 'Select' rows.
	 * @param spinnerAdapter wrapped Adapter. Should probably return false for isEnabled(0)
	 * @param nothingSelectedLayout layout for nothing selected, perhaps you want
	 * text grayed out like a prompt...
	 * @param nothingSelectedDropdownLayout layout for your 'Select an Item...' in
	 * the dropdown.
	 * @param context
	 */
	public NNothingSelectedSpinnerAdapter(SpinnerAdapter spinnerAdapter,
			int nothingSelectedLayout, int nothingSelectedDropdownLayout, Context context) {
		this(spinnerAdapter, nothingSelectedLayout, -1, null, context);
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		// This provides the View for the Selected Item in the Spinner, not
		// the dropdown (unless dropdownView is not set).
		if (position == 0) {
			return getNothingSelectedView(parent);
		}
		return adapter.getView(position - EXTRA, null, parent); // Could re-use
		// the convertView if possible.
	}

	/**
	 * View to show in Spinner with Nothing Selected
	 * Override this to do something dynamic... e.g. "37 Options Found"
	 * @param parent
	 * @return
	 */
	protected View getNothingSelectedView(ViewGroup parent) {
		View nothingSelectedView = layoutInflater.inflate(nothingSelectedLayout, parent, false);
		if(nothingSelectedTextId != -1 && nothingSelectedText != null && nothingSelectedView.findViewById(nothingSelectedTextId) != null){
			TextView nothingSelectedTv = (TextView) nothingSelectedView.findViewById(android.R.id.text1);
			nothingSelectedTv.setText(nothingSelectedText);
		}
		return nothingSelectedView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// BUG! Vote to fix!! http://code.google.com/p/android/issues/detail?id=17128 -
		// Spinner does not support multiple view types
		if (position == 0) {
			return nothingSelectedDropdownLayout == -1 ?
					new View(context) :
						getNothingSelectedDropdownView(parent);
		}

		// Could re-use the convertView if possible, use setTag...
		return adapter.getDropDownView(position - EXTRA, null, parent);
	}

	/**
	 * Override this to do something dynamic... For example, "Pick your favorite
	 * of these 37".
	 * @param parent
	 * @return
	 */
	protected View getNothingSelectedDropdownView(ViewGroup parent) {
		return layoutInflater.inflate(nothingSelectedDropdownLayout, parent, false);
	}

	@Override
	public int getCount() {
		int count = adapter.getCount();
		return count == 0 ? 0 : count + EXTRA;
	}

	@Override
	public Object getItem(int position) {
		return position == 0 ? null : adapter.getItem(position - EXTRA);
	}

	@Override
	public int getItemViewType(int position) {
		// Doesn't work!! Vote to Fix! http://code.google.com/p/android/issues/detail?id=17128 -
		// Spinner does not support multiple view types
		// This method determines what is the convertView, this should
		// return 1 for pos 0 or return 0 otherwise.
		return position == 0 ?
				getViewTypeCount() - EXTRA :
					adapter.getItemViewType(position - EXTRA);
	}

	@Override
	public int getViewTypeCount() {
		return adapter.getViewTypeCount() + EXTRA;
	}

	@Override
	public long getItemId(int position) {
		return adapter.getItemId(position - EXTRA);
	}

	@Override
	public boolean hasStableIds() {
		return adapter.hasStableIds();
	}

	@Override
	public boolean isEmpty() {
		return adapter.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		adapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		adapter.unregisterDataSetObserver(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return position == 0 ? false : true; // Don't allow the 'nothing selected'
		// item to be picked.
	}

}