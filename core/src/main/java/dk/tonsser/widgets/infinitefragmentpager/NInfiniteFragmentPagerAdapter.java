package dk.tonsser.widgets.infinitefragmentpager;
/**
 * @author Adam 2012
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class NInfiniteFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private ArrayList<Fragment> fragments;
    private boolean loop;
    private float pageWidth;

    public NInfiniteFragmentPagerAdapter(Context context, FragmentManager fragmentManager, ArrayList<Fragment> fragments) {
        this(context, fragmentManager, fragments, false, 1.0f);
    }

    public NInfiniteFragmentPagerAdapter(Context context, FragmentManager fragmentManager, ArrayList<Fragment> fragments, boolean loop) {
        this(context, fragmentManager, fragments, loop, 1.0f);
    }

    public NInfiniteFragmentPagerAdapter(Context context, FragmentManager fragmentManager, ArrayList<Fragment> fragments, boolean loop, float pageWidth) {
        super(fragmentManager);
        this.context = context;
        this.fragments = fragments;
        this.loop = loop;
        this.pageWidth = pageWidth;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getPageWidth(int position) {
        return pageWidth;
    }

    @Override
    public Fragment getItem(int i) {
        int page = i % fragments.size();
        return Fragment.instantiate(context, fragments.get(page).getClass().getName());
    }
}
