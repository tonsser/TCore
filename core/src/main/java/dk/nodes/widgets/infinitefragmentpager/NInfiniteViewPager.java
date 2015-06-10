package dk.nodes.widgets.infinitefragmentpager;
/**
 * @author Adam 2012
 */
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class NInfiniteViewPager extends ViewPager {

    public NInfiniteViewPager(Context context) {
        super(context);
        setOffscreenPageLimit(9); // TODO
    }

    public NInfiniteViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOffscreenPageLimit(9); // TODO
    }
}