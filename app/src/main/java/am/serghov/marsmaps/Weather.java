package am.serghov.marsmaps;

import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Struct;
import java.util.Calendar;
import java.util.Date;

import am.serghov.marsmaps.Coordinate.latlon;
import am.serghov.marsmaps.Date.MarsDate;

/**
 * Created by serghov on 4/22/2016.
 */
public class Weather {
    //http://www-mars.lmd.jussieu.fr/mcd_python/cgi-bin/mcdcgi.py?datekeyhtml=1&ls=141.3&localtime=0.&year=2016&month=4&day=22&hours=19&minutes=21&seconds=12&isfixedlt=off&julian=2457501.306388889&martianyear=33&martianmonth=5&sol=301&latitude=12&longitude=23&altitude=10.&zkey=3&dust=1&hrkey=1&zonmean=off&dpi=80&var1=t&var2=none&var3=none&var4=none&islog=off&colorm=jet&minval=&maxval=&proj=cyl&plat=&plon=&trans=&iswind=off

    //http://www-mars.lmd.jussieu.fr/mcd_python/cgi-bin/mcdcgi.py?ls=141.3&localtime=0.&datekeyhtml=0&isfixedlt=off&julian=2457501.306388889&latitude=12&longitude=23&altitude=10.&zkey=3&dust=1&hrkey=1&var1=t&var2=none&var3=none&var4=none

    private static String baseUrl = "http://www-mars.lmd.jussieu.fr/mcd_python/cgi-bin/mcdcgi.py?";

    Weather()
    {
        //new RequestTask().execute(baseUrl + genTemperatureUrl(new latlon(0,0)));
        //new RequestTask().execute(baseUrl + getPressureUrl(new latlon(0,0)));
        //new RequestTask().execute(baseUrl + getDensityUrl(new latlon(0,0)));
    }

    public static void getTemperature(latlon pos, WeatherDataListener weatherDataListener)
    {
        Log.e("BASEURL",baseUrl + genTemperatureUrl(new latlon(0,0)));
        new RequestTask(weatherDataListener).execute(baseUrl + genTemperatureUrl(pos));
    }

    private static String genTemperatureUrl(latlon pos)
    {
        String res = getBaseAddonUrl(pos);
        res += "var1=t&var2=none&var3=none&var4=none";

        return res;
    }

    public static void getSurfaceTemperature(latlon pos, WeatherDataListener weatherDataListener)
    {
        new RequestTask(weatherDataListener).execute(baseUrl + genSurfaceTemperatureUrl(pos));
    }

    private static String genSurfaceTemperatureUrl(latlon pos)
    {
        String res = getBaseAddonUrl(pos);
        res += "var1=tsurf&var2=none&var3=none&var4=none";

        return res;
    }

    public static void getPressure(latlon pos, WeatherDataListener weatherDataListener)
    {
        new RequestTask(weatherDataListener).execute(baseUrl + getPressureUrl(pos));
    }

    private static String getPressureUrl(latlon pos)
    {
        String res = getBaseAddonUrl(pos);
        res += "var1=p&var2=none&var3=none&var4=none"; //difference in one number;

        return res;
    }

    public static void getSurfaceCO2IceLayer(latlon pos, WeatherDataListener weatherDataListener)
    {
        new RequestTask(weatherDataListener).execute(baseUrl + getSurfaceCO2IceLayerUrl(pos));
    }

    private static String getSurfaceCO2IceLayerUrl(latlon pos)
    {
        String res = getBaseAddonUrl(pos);
        res += "var1=co2ice&var2=none&var3=none&var4=none"; //difference in one number;

        return res;
    }

    public static void getDensity(latlon pos, WeatherDataListener weatherDataListener)
    {
        new RequestTask(weatherDataListener).execute(baseUrl + getDensityUrl(pos));
    }

    private static String getDensityUrl(latlon pos)
    {
        String res = getBaseAddonUrl(pos);
        res += "var1=rho&var2=none&var3=none&var4=none"; //difference in several number;

        return res;
    }

    public static void getHorizontalWind(latlon pos, WeatherDataListener weatherDataListener)
    {
        new RequestTask(weatherDataListener).execute(baseUrl + getHorizontalWindUrl(pos));
    }

    private static String getHorizontalWindUrl(latlon pos)
    {
        String res = getBaseAddonUrl(pos);
        res += "var1=wind&var2=none&var3=none&var4=none"; //difference in several number;

        return res;
    }

    public static void getDustEffectiveRadius(latlon pos, WeatherDataListener weatherDataListener)
    {
        new RequestTask(weatherDataListener).execute(baseUrl + getDustEffectiveRadiusUrl(pos));
    }

    private static String getDustEffectiveRadiusUrl(latlon pos)
    {
        String res = getBaseAddonUrl(pos);
        res += "var1=rdust&var2=none&var3=none&var4=none"; //difference in several number;

        return res;
    }

    private static String getBaseAddonUrl(latlon pos)
    {
        String res = "";
        Calendar cal = Calendar.getInstance();
        MarsDate curDate = new MarsDate(cal.getTime());


        res += "ls=" + curDate.getSolarLongitude() + "&localtime=0.&datekeyhtml=0&isfixedlt=off&";
        res += "julian=" + curDate.getJulian();
        res += "&latitude=" + pos.getLat() + "&longitude=" + pos.getLon();
        res += "&altitude=10.&zkey=3&dust=1&hrkey=1&";

        return res;
    }

    static class RequestTask extends AsyncTask<String, String, String> {

        WeatherDataListener tt ;
        public RequestTask(WeatherDataListener tt){
            this.tt = tt;
        }
        @Override
        // username, password, message, mobile
        protected String doInBackground(String... url) {
            // constants
            int timeoutSocket = 5000;
            int timeoutConnection = 5000;

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);

            HttpGet httpget = new HttpGet(url[0]);

            try {
                HttpResponse getResponse = client.execute(httpget);
                final int statusCode = getResponse.getStatusLine().getStatusCode();

                if(statusCode != HttpStatus.SC_OK) {
                    Log.w("MyApp", "Download Error: " + statusCode + "| for URL: " + url);
                    return null;
                }

                String line = "";
                StringBuilder total = new StringBuilder();

                HttpEntity getResponseEntity = getResponse.getEntity();

                BufferedReader reader = new BufferedReader(new InputStreamReader(getResponseEntity.getContent()));

                while((line = reader.readLine()) != null) {
                    total.append(line);
                }

                line = total.toString();
                return line;
            } catch (Exception e) {
                Log.w("MyApp", "Download Exception : " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // do something with result
            if (result == null)
                return;
            double res=0;
            if (result.indexOf("..... ")!=-1)
            {
                res = Double.parseDouble(result.substring(result.indexOf("..... ") + 6, result.indexOf("</li>")));
            }
            this.tt.onData(res);
            Log.w("WEATHER DATA", ""+res);
        }
    }

    public interface WeatherDataListener {
        void onData(double data);
    }
}
