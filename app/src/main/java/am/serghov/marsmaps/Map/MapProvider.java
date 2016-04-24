package am.serghov.marsmaps.Map;

/**
 * Created by serghov on 4/14/2016.
 */
public class MapProvider {
    public int zoomLevels;
    public String genericUrl;
    public int downSample;
    public String name;
    public MapBitmapProvider bitmapProvider;

    MapProvider(int zoomLevels, String genericUrl, int downSample, MapBitmapProvider bitmapProvider)
    {
        this.zoomLevels = zoomLevels;
        this.genericUrl = genericUrl;
        this.downSample = downSample;
        this.bitmapProvider = bitmapProvider;
    }

}
