package am.serghov.marsmaps.Models;

import java.util.ArrayList;

/**
 * Created by serghov on 4/22/2016.
 */
public class UserReg {

    private String name;
    private String token;
    private int xp;

    private Calony colony;


    public Calony getColony() {
        return colony;
    }

    public void setColony(Calony colony) {
        this.colony = colony;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
