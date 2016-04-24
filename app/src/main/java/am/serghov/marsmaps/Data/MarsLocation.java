package am.serghov.marsmaps.Data;

import am.serghov.marsmaps.Coordinate.latlon;

/**
 * Created by serghov
 */
public class MarsLocation {
    latlon location;
    String name;
    String descirption;
    String type;
    int diameter;

    boolean isRegion;

    public MarsLocation()
    {
    }

    public MarsLocation(latlon location, String name)
    {
        this.location = location;
        this.name = name;
    }

    public MarsLocation(latlon location, String name, String descirption, String type, int diameter, int isRegion)
    {
        this.location = location;
        this.name = name;
        this.descirption = descirption;
        this.type = type;
        this.diameter = diameter;
        if (isRegion==-1)
            this.isRegion = false;
        else
            this.isRegion = true;
    }

    public String toString()
    {
        return this.name + " at " + this.location.toString();
    }

    public String getName()
    {
        return this.name;
    }

    public latlon getLocation()
    {
        return this.location;
    }

    public boolean shouldShow()
    {
        return isRegion;
    }
}
