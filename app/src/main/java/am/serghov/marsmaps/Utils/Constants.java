package am.serghov.marsmaps.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import am.serghov.marsmaps.MapActivity;

/**
 * Created by serghov.
 */
public class Constants {

    public static String MY_PREFERENCES = "MyPREFERENCES";
    public static String SenderID = "353450091073";

    public static String USER_REG = /*"http://192.168.8.108:3000/users/register?latitude=";//*/"http://singularity.am:3000/users/register?latitude=";
    public static String USER_LNG = "&longitude=";
    public static String TASKS_URL = /*"http://192.168.8.108:3000/tasks";//*/"http://singularity.am:3000/tasks";

    public static String TOKEN = "";

    public static String getToken()
    {
        try {
            SharedPreferences settings = MapActivity.instance.getSharedPreferences("MarsLive", Context.MODE_PRIVATE);
            return settings.getString("token", TOKEN);
        }
        catch (Exception e)
        {
            return Constants.TOKEN;
        }
    }

    public static void setToken(String token)
    {
        SharedPreferences settings = MapActivity.instance.getSharedPreferences("MarsLive",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.commit();
        editor.putString("token",token);
        Constants.TOKEN = token;
    }
}
