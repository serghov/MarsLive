package am.serghov.marsmaps;

import android.util.Log;

/**
 * Created by serghov
 */
public class Elevation {

    private static int[] elevations = {-9000,   -8000,  -7000,      -6000,      -5000,      -4000,      -3000,      -2000,      -1000,      0, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 11000, 12000, 13000, 14000, 15000, 16000, 17000, 18000, 19000, 20000, 21000};
    private static int[] colors =     {6825575, 8464024, 8529607,    4990675,    2506463,   2524907,    2549418,    4383014,    9760550,    15990310, 16694335, 15766339, 14839621, 13914440, 12479323, 11040617, 11633268, 12225920, 12753293, 13346459, 13874090, 14533049, 15061193, 15654874, 16183020, 16314863, 16381171, 16579065, 16777215, 14351871, 11926526};

    private static int[] red =   {104, 129, 130, 76,  38,  38,  38,  66,  148, 243, 254, 240, 226, 212, 190, 168, 177, 186, 194, 203, 211, 221, 229, 238, 246, 248, 249, 252, 255, 218, 181};
    private static int[] green = {38,  38,  38,  38,  62,  134, 230, 225, 239, 254, 188, 147, 111, 81,  107, 119, 130, 141, 153, 166, 179, 193, 208, 223, 238, 241, 244, 249, 255, 253, 251};
    private static int[] blue =  {103, 152, 199, 211, 223, 235, 170, 38,  38,  38,  63,  67,  69,  72,  91,  105, 116, 128, 141, 155, 170, 185, 201, 218, 236, 239, 243, 249, 255, 255, 254};


    public static double Color2Elevation(int red, int green, int blue) //fix the interploation smh
    {
        int color = red*16*16*16*16 + green * 16*16 + blue;
        Log.e("color",""+color);
        int i;
        int range=0;
        int a,b;

        /*for (i=0;i<colors.length-i;i++) {
            if (Math.abs(colors[i] - color) + Math.abs(colors[i + 1] - color) == Math.abs(colors[i] - colors[i + 1])) {
                break;
            }
        }*/
        int minDistI = 0;
        for (i=0;i<elevations.length;i++) {
            if (Math.abs(red - Elevation.red[i]) + Math.abs(green - Elevation.green[i]) + Math.abs(blue - Elevation.blue[i])
                    < Math.abs(red - Elevation.red[minDistI]) + Math.abs(green - Elevation.green[minDistI]) + Math.abs(blue - Elevation.blue[minDistI])) {
                minDistI = i;
            }
        }
        //a=i;
        //b=i+1;
        Log.e("COLOR", "" + red + " " + green + " " + blue);
        Log.e("COLOR", "" + Elevation.red[minDistI] + " " + Elevation.green[minDistI] + " " + Elevation.blue[minDistI]);

        //return elevations[a] + (elevations[b]-elevations[a]) * Math.abs(colors[a]-color)/Math.abs(colors[a]-colors[b]);
        //return elevations[a];
        //Log.e("ELEVATIONS", "" + elevations.length);
        //Log.e("COLOR", "" + red + " " + green + " " + blue);
        //Log.e("COLOR", "" + Elevation.red[minDistI] + " " + Elevation.green[minDistI] + " " + Elevation.blue[minDistI]);
        return elevations[minDistI];
    }

}
