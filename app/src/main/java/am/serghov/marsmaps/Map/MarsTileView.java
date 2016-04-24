package am.serghov.marsmaps.Map;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.qozix.tileview.TileView;
import am.serghov.marsmaps.Coordinate.latlon;

import org.rajawali3d.math.vector.Vector2;


public class MarsTileView extends TileView {
    private MarsMapListener.LongPressListener longPressListener;

    public MarsTileView(Context context) {
        super(context);
    }

    @Override
    public void onLongPress( MotionEvent event )
    {
        //Log.e("LONGPRESS", "" + event.getRawX() + " "+event.getRawY());
        latlon coord = getCoordinates(new Vector2(event.getRawX()/this.getWidth(), event.getRawY()/this.getHeight()));
        //Log.e("LONGPRESS", "" + coord.getLon() + " " + coord.getLat());

        if (longPressListener != null)
            longPressListener.OnLongPress(event, coord);
    }

    public latlon getCoordinates(Vector2 pos)
    {
        double x = (this.getScrollX() + this.getWidth() * pos.getX()) / this.getScale() / this.getBaseWidth();
        double y = (this.getScrollY() + (float)this.getHeight() * pos.getY()) / this.getScale() / this.getBaseHeight();

        return latlon.toLatLon(x,y);
    }

    public latlon getCenterCoorinates()
    {
        return this.getCoordinates(new Vector2(0.5,0.5));
    }


    public void AddOnLongPressListener(MarsMapListener.LongPressListener longPressListener)
    {
        this.longPressListener = longPressListener;
    }
}
