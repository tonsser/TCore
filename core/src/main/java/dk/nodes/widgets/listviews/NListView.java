package dk.nodes.widgets.listviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Thomas on 28-11-2014.
 */
@Deprecated
public class NListView extends ListView {
    private int mPosition;
    private boolean scrollEnabled = true;

    public NListView(Context context) {
        super(context);
    }

    public NListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setScrollEnabled(boolean enabled){
        this.scrollEnabled = enabled;
    }

    public boolean isScrollEnabled(){
        return scrollEnabled;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(scrollEnabled){
            return super.dispatchTouchEvent(ev);
        }

        final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;

        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // Record the position the list the touch landed on
            mPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
            return super.dispatchTouchEvent(ev);
        }

        if (actionMasked == MotionEvent.ACTION_MOVE) {
            // Ignore move events
            return true;
        }

        if (actionMasked == MotionEvent.ACTION_UP) {
            // Check if we are still within the same view
            if (pointToPosition((int) ev.getX(), (int) ev.getY()) == mPosition) {
                super.dispatchTouchEvent(ev);
            } else {
                // Clear pressed state, cancel the action
                setPressed(false);
                invalidate();
                return true;
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
