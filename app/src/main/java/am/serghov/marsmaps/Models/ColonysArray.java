package am.serghov.marsmaps.Models;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

import am.serghov.marsmaps.Utils.Constants;

/**
 * Created by serghov on 4/23/2016.
 */
public class ColonysArray {

    private ArrayAdapter<Colonys>colonies;
    private String name;
    private int xp;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public ArrayAdapter<Colonys> getColonies() {
        return colonies;
    }

    public void setColonies(ArrayAdapter<Colonys> colonies) {
        this.colonies = colonies;
    }
}
