package am.serghov.marsmaps.Map;

import android.content.Context;
import android.graphics.Bitmap;

import com.qozix.tileview.graphics.BitmapProvider;
import com.qozix.tileview.tiles.Tile;
import am.serghov.marsmaps.Map.MapBitmapProvider;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class BitmapProviderPicassoGoogleMaps extends MapBitmapProvider implements BitmapProvider {
    public Bitmap getBitmap( Tile tile, Context context ) {

        Object data = tile.getData();
        //Log.e("Mars", data.toString());
        //if( data instanceof String ) {

        String unformattedFileName = data.toString();
        //String formattedFileName = String.format( unformattedFileName, tile.getColumn(), tile.getRow() );
        //Log.e("Mars", unformattedFileName);
        //return Picasso.with(context).load( path ).get();
        String mUrl = getTileUrl(tile.getColumn(), tile.getRow(), unformattedFileName);


        try {
            return Picasso.with( context ).load( mUrl ).memoryPolicy( MemoryPolicy.NO_CACHE).get();
        } catch( IOException e ) {
            // probably couldn't find the file
            /*try {
                return Picasso.with(context).load(R.drawable.no_image).get();
            } catch (IOException e1) {

            }*/
        }

        //}
        return null;
    }

    private String getTileUrl (int x, int y, String unformattedName) {

        String[] tmp =unformattedName.split("zoom");
        int zoom = Integer.parseInt(tmp[tmp.length - 1]);

        int bound = (int) Math.pow(2, zoom);

        // Don't repeat across y-axis (vertically).
        if (y < 0 || y >= bound) {
            return null;
        }

        // Repeat across x-axis.
        if (x < 0 || x >= bound) {
            x = (x % bound + bound) % bound;
        }

        String qstr = "t";
        for (int z = 0; z < zoom; z++) {
            bound = bound / 2;
            if (y < bound) {
                if (x < bound) {
                    qstr += "q";
                } else {
                    qstr += "r";
                    x -= bound;
                }
            } else {
                if (x < bound) {
                    qstr += "t";
                    y -= bound;
                } else {
                    qstr += "s";
                    x -= bound;
                    y -= bound;
                }
            }
        }
        return tmp[0] + qstr + ".jpg";
    }

}