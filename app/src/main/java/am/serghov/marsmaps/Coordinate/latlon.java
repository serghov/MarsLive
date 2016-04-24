package am.serghov.marsmaps.Coordinate;

import android.util.Log;

import org.rajawali3d.math.vector.Vector2;
import org.rajawali3d.math.vector.Vector3;

/**
 * Created by serghov
 */
public class latlon {
    public double lat, lon;
    public latlon(){}

    public latlon(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
    }
    public double getLat()
    {
        return this.lat;
    }
    public double getLon()
    {
        return this.lon;
    }
    public latlon setLon(double lon)
    {
        this.lon = lon;
        return this;
    }

    public latlon setLat(double lat)
    {
        this.lat = lat;
        return this;
    }

    public static latlon toLatLon(double x, double y)
    {
        y-=0.5;
        double lat = Math.toDegrees(Math.PI/2.0 - (Math.atan(Math.exp(-y * 2 * Math.PI)) * 2));
        double lon = Math.toDegrees((2.0 * x * Math.PI) + Math.PI/2.0);

        lon -=270;

        return new latlon(lat,lon);
    }

    public Vector3 toVector()
    {
        Vector3 res = new Vector3(0,0,1);
        res.rotateY(Math.toRadians(this.lon));
        res.rotateX(Math.toRadians(this.lat));
        return res;
    }

    public String toString()
    {
        String res = "";
        double lat = (int)(this.lat * 10)/10.0;
        double lon = (int)(this.lon * 10)/10.0;

        if (lat>=0)
            res +=lat + "S, ";
        else
            res +=-lat + "N, ";

        if (lon>=0)
            res +=lon + "E";
        else
            res +=-lon + "W";
        return res;
    }

    public Vector2 toVector2()
    {
        Vector2 res = new Vector2();
        res.setX((Math.toRadians(this.lon + 270) - Math.PI/2.0)/2.0/Math.PI);
        res.setY(1.0 - Math.log(  Math.tan(  0.25*(Math.PI - 2.0 * Math.toRadians(lat))  )  )/Math.PI/2.0 - 0.5);

        return res;
    }

    public double MarsDistance(latlon b) //maybe wrong check this
    {

        double R = 3390000.0;
        double fi1 = Math.toRadians(this.lat);
        double fi2 = Math.toRadians(b.lat);

        double deltaFi = Math.toRadians(b.lat - this.lat);
        double deltaLyambda = Math.toRadians(b.lon - this.lon);

        double a = Math.sin(deltaFi/2) * Math.sin(deltaFi/2) +  Math.cos(fi1) * Math.cos(fi2) * Math.sin(deltaLyambda/2) * Math.sin(deltaLyambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c;
        Log.e("DISTNACE", "" + d);
        return d;
    }
}
