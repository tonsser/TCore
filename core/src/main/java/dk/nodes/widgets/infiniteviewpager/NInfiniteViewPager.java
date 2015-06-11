package dk.nodes.widgets.infiniteviewpager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;

@Deprecated
public class NInfiniteViewPager extends ViewPager {

	private final boolean ENABLED = true;
	private final String CACHE_DIR = Environment.getExternalStorageDirectory() + "/.ninfiniteviewpager/cache/";

	private LinkedList<String> mViewTitles = new LinkedList<String>();
	private LinkedList<FrameLayout> mViews = new LinkedList<FrameLayout>();
	
	private NInfinitePageListener mInfinitePageListener;

	/**
	 * Do this for example:
	 * 
	 * NInfiniteViewPager mInfinitePageView = (NInfiniteViewPager)
	 * v.findViewById(R.id.the_viewpager_view);
	 * 
	 * mInfinitePageView.setListener(new NInfinitePageListener() {
	 * 
	 * @Override public void onPageChanged(int position) { // Do something,
	 *           maybe } });
	 * 
	 *           for (int i = 0; i < 3; i++) { LayoutInflater inflater =
	 *           (LayoutInflater) getBaseContext().getSystemService(Context.
	 *           LAYOUT_INFLATER_SERVICE); View view =
	 *           inflater.inflate(R.layout.some_layout, null); TextView textTv =
	 *           new TextView(getBaseContext()); textTv.setText("Test view " +
	 *           i); mInfinitePageView.addPage(view); }
	 * 
	 * @param context
	 * @param attrs
	 */
	public NInfiniteViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setAdapter(mPageViewAdapter);
		this.setOnPageChangeListener(mOnPageChangeListener);
		if (ENABLED)
			this.setCurrentItem(0, false);
	}

	public void addPage(String title, View view) {
		FrameLayout frameLayout = new FrameLayout(this.getContext());
		frameLayout.addView(view);
		mViews.add(frameLayout);
		mViewTitles.add(title);
	}

	public void addPage(View view) {
		addPage("Untitled", view);
	}

	public String[] getTitles() {
		return mViewTitles.toArray(new String[1]);
	}

	public void setListener(NInfinitePageListener listener) {
		mInfinitePageListener = listener;
	}

	private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

		private int mDirection;
		private int mCurrent = (ENABLED) ? 1 : 0;

		private final int LEFT = 0;
		private final int RIGHT = 2;

		@Override
		public void onPageSelected(int position) {
			mDirection = position;
			if (ENABLED) {
				mCurrent += (mDirection == LEFT) ? -1 : 1;
				mCurrent %= mViews.size();
			}
			else {
				mCurrent = position;
			}
			mInfinitePageListener.onPageChanged(mCurrent);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

		@Override
		public void onPageScrollStateChanged(int state) {
			
			if (!ENABLED) return;
			
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				do {
					switch (mDirection) {
					case LEFT:
						View lastView = mViews.getLast().getChildAt(0);
						mViews.getLast().removeAllViews();
						for (int i = mViews.size() - 1; i > 0; i--) {
							View view = mViews.get(i - 1).getChildAt(0);
							mViews.get(i - 1).removeAllViews();
							mViews.get(i).addView(view);
						}
						mViews.getFirst().addView(lastView);
						break;
					case RIGHT:
						View firstView = mViews.getFirst().getChildAt(0);
						mViews.getFirst().removeAllViews();
						for (int i = 0; i < mViews.size() - 1; i++) {
							View view = mViews.get(i + 1).getChildAt(0);
							mViews.get(i + 1).removeAllViews();
							mViews.get(i).addView(view);
						}
						mViews.getLast().addView(firstView);
					}
				} while (mViews.get(1).getChildAt(0).getTag() != null);
				NInfiniteViewPager.this.setCurrentItem(1, false);
			}

			else if (state == ViewPager.SCROLL_STATE_DRAGGING && mCompensateModeCount > 0) {
				for (int i = 0; i < 2; i++) {
					Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
					mViews.get(i).getChildAt(0).layout(0, 0, display.getWidth(), display.getHeight());
					mViews.get(i).getChildAt(0).setDrawingCacheEnabled(true);
					mViews.get(i).getChildAt(0).buildDrawingCache();
					Bitmap b = mViews.get(i).getChildAt(0).getDrawingCache();
					String path = CACHE_DIR + i + ".jpg";
					try {
						b.compress(CompressFormat.JPEG, 95, new FileOutputStream(path));
					}
					catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					mCompensateImageViews[i].setImageDrawable(Drawable.createFromPath(path));
				}
			}

		}
	};

	private int mCompensateModeCount = 0;

	private ImageView[] mCompensateImageViews;

	private PagerAdapter mPageViewAdapter = new PagerAdapter() {

		@Override
		public int getCount() {

			if (!ENABLED) return mViews.size();
			else {
				if (mViews.size() == 1 || mViews.size() == 2) {
					mCompensateModeCount = mViews.size();
					mCompensateImageViews = new ImageView[2];
					for (int i = 0; i < 2; i++) {

						Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
						mViews.get(i).getChildAt(0).layout(0, 0, display.getWidth(), display.getHeight());
						mViews.get(i).getChildAt(0).setDrawingCacheEnabled(true);
						mViews.get(i).getChildAt(0).buildDrawingCache();
						Bitmap b = mViews.get(i).getChildAt(0).getDrawingCache();
						String path = CACHE_DIR + i + ".jpg";
						new File(CACHE_DIR).mkdirs();
						try {
							b.compress(CompressFormat.JPEG, 95, new FileOutputStream(path));
						}
						catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						ImageView imageView = new ImageView(getContext());
						imageView.setScaleType(ImageView.ScaleType.FIT_START);
						imageView.setLayoutParams(new ViewGroup.LayoutParams(display.getWidth(), display.getHeight()));
						imageView.setImageDrawable(Drawable.createFromPath(path));
						imageView.setTag(i);
						mCompensateImageViews[i] = imageView;
						addPage(imageView);
					}
				}
				return 3;
			}
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			if (mViews.size() == 0) 
				return null;
			((ViewPager) collection).addView(mViews.get(position));
			return mViews.get(position);
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((FrameLayout) view);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((FrameLayout) object);
		}

		@Override
		public void finishUpdate(View arg0) {}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {}

	};
	
	public interface NInfinitePageListener {
		public void onPageChanged(int position);
	}

}