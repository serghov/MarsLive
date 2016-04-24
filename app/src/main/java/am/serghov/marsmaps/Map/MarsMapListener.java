package am.serghov.marsmaps.Map;

import android.view.MotionEvent;

import am.serghov.marsmaps.Coordinate.latlon;

/**
 * Created by serghov on 4/19/2016.
 */
public class MarsMapListener {
    public interface LongPressListener {
        void OnLongPress(MotionEvent event, latlon coord);
    }
}

