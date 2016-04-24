package am.serghov.marsmaps.Map;

import android.content.Context;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qozix.tileview.graphics.BitmapProvider;
import com.qozix.tileview.paths.CompositePathView;
import com.qozix.tileview.widgets.ZoomPanLayout;
import am.serghov.marsmaps.Coordinate.latlon;
import am.serghov.marsmaps.Data.Locations;
import am.serghov.marsmaps.R;
import com.squareup.picasso.Picasso;

import org.rajawali3d.math.vector.Vector2;

import java.util.ArrayList;

/**
 * Created by serghov
 */
public class Map extends Fragment{

    public static String mapType = "elevation";
    private LinearLayout mainContainer;
    public MarsTileView tileView;

    BitmapProviderPicassoGoogleMaps googleBitmapProvider = new BitmapProviderPicassoGoogleMaps();

    public float cHeight = 65536, cWidth = 65536;

    MapProvider googleVisibleMap = new MapProvider(10,"https://mw1.google.com/mw-planetary/mars/visible/zoom", R.drawable.mars_visible, googleBitmapProvider);
    MapProvider googleElevationMap = new MapProvider(9,"https://mw1.google.com/mw-planetary/mars/elevation/zoom", R.drawable.mars_elevation, googleBitmapProvider);
    MapProvider googleInfraredMap = new MapProvider(11,"https://mw1.google.com/mw-planetary/mars/infrared/zoom", R.drawable.mars_infrared, googleBitmapProvider);

    private ZoomPanLayout.ZoomPanListener zoomPanListener;
    private MarsMapListener.LongPressListener longPressListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mapType == "elevation"){
            tileView = getTileView(googleElevationMap);
        } else if (mapType == "visible"){
            tileView = getTileView(googleVisibleMap);
        } else if (mapType == "infrared"){
            tileView = getTileView(googleInfraredMap);
        }

        mainContainer = new LinearLayout(getActivity());
        mainContainer.addView(tileView);

        return mainContainer;
    }

    public void setZoomPanListener(ZoomPanLayout.ZoomPanListener zoomPanListener)
    {
        this.zoomPanListener = zoomPanListener;
        addZoomPanListener();
    }

    private void addZoomPanListener()
    {
        if (zoomPanListener !=null)
        tileView.addZoomPanListener(zoomPanListener);
    }

    public void setElevationMap()
    {
        mapType = "elevation";
        mainContainer.removeView(tileView);
        tileView = getTileView(googleElevationMap);//getElevationTileView();
        mainContainer.addView(tileView);
    }

    public void setVisibleMap()
    {
        mapType = "visible";
        mainContainer.removeView(tileView);
        tileView = getTileView(googleVisibleMap);//getVisibleTileView();
        mainContainer.addView(tileView);
    }

    public void setInfraredMap()
    {
        mapType = "infrared";
        mainContainer.removeView(tileView);
        tileView = getTileView(googleInfraredMap);
        mainContainer.addView(tileView);
    }

   public latlon getCoordinates(Vector2 pos)
    {
        return tileView.getCoordinates(pos);
        /*double x = (tileView.getScrollX() + tileView.getWidth() * pos.getX()) / tileView.getScale() / cWidth;
        double y = (tileView.getScrollY() + (float)tileView.getHeight() * pos.getY()) / tileView.getScale() / cWidth;

        return latlon.toLatLon(x,y);*/
    }

    public latlon getCenterCoorinates()
    {
        return this.getCoordinates(new Vector2(0.5,0.5));
    }

    private MarsTileView getTileView(MapProvider provider)
    {
        float height = (float) (Math.pow(2.0,provider.zoomLevels-1.0) * 256), width = (float) (Math.pow(2.0,provider.zoomLevels-1.0) * 256);

        float originalCenterX = 0;
        float originalCenterY = 0;
        float originalZoom = 1;

        if (tileView != null) {
            originalCenterX = tileView.getScrollX() / tileView.getScale() / cWidth * width;
            originalCenterY = tileView.getScrollY() / tileView.getScale() / cWidth * width;

            originalZoom = (float) Math.max(tileView.getScale() * (cWidth / width), Math.pow(0.5,provider.zoomLevels-1));
            Log.e("originalCenterX", originalCenterX + "");
            Log.e("originalCenterY", originalCenterY + "");
        }
        tileView = new MarsTileView( getActivity() );

        tileView.setSize((int)width, (int)height);  // the original size of the untiled image
        cHeight = height;
        cWidth = width;

        tileView.setScale(originalZoom);
        tileView.scrollToAndCenter(originalCenterX, originalCenterY);

        ImageView downSample = new ImageView( getActivity() );
        Picasso.with(getActivity()).load(provider.downSample).into(downSample);
        tileView.addView(downSample, 0);

        float curZoom = 1;
        for (int i = provider.zoomLevels-1;i>=0;i--)
        {
            tileView.addDetailLevel(curZoom,provider.genericUrl+i);
            curZoom *=0.5f;
        }

        tileView.setBitmapProvider((BitmapProvider) provider.bitmapProvider);
        tileView.setShouldLoopScale( false );
        addZoomPanListener();
        tileView.AddOnLongPressListener(longPressListener);
        tileView.setAnimationDuration(1500);




        for (int i = 0; i < Locations.locations.length; i++)
        {
            if (!Locations.locations[i].shouldShow())
                continue;

            ImageView markerView = new ImageView(getActivity().getApplicationContext());
            //markerView.setText(Locations.locations[i].getName());
            markerView.setImageDrawable(getResources().getDrawable(R.drawable.pin));




            Vector2 loc = Locations.locations[i].getLocation().toVector2();

            tileView.addMarker(markerView, loc.getX() * cWidth, loc.getY() * cHeight , -0.5f , -1f);
        }

        return tileView;
    }

    public void AddLongPressListener(MarsMapListener.LongPressListener longPressListener)
    {
        this.longPressListener = longPressListener;
        tileView.AddOnLongPressListener(this.longPressListener);
    }

    public void animateTo(latlon pos)
    {
        Log.e("LOC", ""+pos.getLat());
        Vector2 XYpos = pos.toVector2();

        this.tileView.slideToAndCenterWithScale(this.tileView.getBaseWidth() * XYpos.getX(),  this.tileView.getBaseHeight() * XYpos.getY(),1);

    }

}
