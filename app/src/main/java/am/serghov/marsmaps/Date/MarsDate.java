package am.serghov.marsmaps.Date;


import android.util.Log;

import java.util.Date;

/**
 * Created by serghov on 4/18/2016.
 */
public class MarsDate {

    double martianMonth, sol, martianYear, solarLongitude;

    int year = 2016, month = 4, day = 18;
    int hours = 0, minutes = 0, seconds = 0;

    double julianValue;

    private boolean isDirty = true;

    public MarsDate() {

    }

    public MarsDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public MarsDate(int year, int month, int day, int hours, int minutes, int seconds) {
        this.year = year;
        this.month = month;
        this.day = day;

        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public MarsDate(Date date) {

        this.year = date.getYear() + 1900;
        this.month = date.getMonth();
        this.day = date.getDay();

        this.hours = date.getHours();
        this.minutes = date.getMinutes();
        this.seconds = date.getSeconds();

        Log.e("DATE", ""+this.year + " "+this.month + " " + this.day + " " + this.hours);
    }

    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        isDirty = true;
    }

    private boolean isBissextil(int year) {
        boolean bissextil; // bissextil year ? (0==no, 1==yes) (returned value)

        if (year < 1583) {
            // val=prompt("Sorry, Year must not be less than 1583 \n (in order
            // to stick to the Gregorian calendar)",1583);
        }
        this.year = year;

        if ((((year % 4) == 0) && ((year % 100) != 0)) || ((year % 400) == 0)) {
            bissextil = true;
        } else {
            bissextil = false; // not a bissextil year
        }

        return bissextil;
    }

    private void Convert2Julian() {
        boolean bissextil; // bissextil year ? (0==no, 1==yes)
        int year;
        int month;
        int day;
        int i;
        int hours, minutes, seconds;
        int ref_year = 1968;
        double ref_jdate = 2.4398565e6; // Julian date for 01/01/1968 00:00:00
        double jdate;
        int[] edays = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
        // edays = number of elapsed days during previous monthes of same year
        double nday = 0.0; // number of days

        // start by checking validity of given date
        bissextil = isBissextil(this.year);

        year = this.year;
        month = this.month;
        day = this.day;

        // compute number of days due to years

        if (year > ref_year) {
            for (i = ref_year; i < year; i++) {
                nday = nday + 365.0;
                if ((((i % 4) == 0) && ((i % 100) != 0)) || ((i % 400) == 0)) {
                    nday++;
                }
            }
        } else {
            for (i = year; i < ref_year; i++) {
                nday = nday - 365.0;
                if ((((i % 4) == 0) && ((i % 100) != 0)) || ((i % 400) == 0)) { // bissextil
                    nday--;
                }
            }
        }

        // add number of days due to elapsed monthes
        nday = nday + edays[month - 1];
        // alert(nday)

        // add 1 if year is bissextil and month >=3
        if ((bissextil) && (month >= 3)) {
            nday = nday + 1;
        }
        // add reference year offset and day
        // jdate=ref_jdate+nday+day;
        jdate = nday * 1.0 + day * 1.0 + ref_jdate * 1.0 - 1.0;

        // add time (hours+minutes+seconds)
        hours = this.hours;
        minutes = this.minutes;
        seconds = this.seconds;
        jdate = jdate + hours / 24.0 + minutes / 1440.0 + seconds / 86400.0;

        this.julianValue = jdate;
    }

    private void Convert2Ls() {
        if (!isDirty)
            return;

        // Convert a Julian date to corresponding "sol" and "Ls"
        double jdate;
        double sol;
        double ls;
        double martianyear;
        double martianmonth;

        double jdate_ref = 2.442765667e6; // 19/12/1975 4:00:00, such that Ls=0
        // jdate_ref is also the begining of Martian Year "12"
        int martianyear_ref = 12;
        double earthday = 86400.0;
        double marsday = 88775.245;
        double marsyear = 668.60; // number of sols in a martian year


        this.Convert2Julian();


        jdate = this.julianValue;

        sol = (jdate - jdate_ref) * earthday / marsday;

        martianyear = martianyear_ref;

        while (sol >= marsyear) {
            sol = sol - marsyear;
            martianyear = martianyear + 1;
        }
        while (sol < 0.0) {
            sol = sol + marsyear;
            martianyear = martianyear - 1;
        }

        ls = Sol2Ls(sol);

        martianmonth = 1 + Math.floor(ls / 30.);

        this.martianYear = martianyear;
        this.martianMonth = martianmonth;
        this.solarLongitude = ls;
        this.sol = sol;

        isDirty = false;
    }

    private double Sol2Ls(double sol) {
        // var sol;
        double ls;

        double year_day = 668.6; // number of sols in a martian year
        double peri_day = 485.35; // perihelion date
        double e_ellip = 0.09340; // orbital ecentricity
        double timeperi = 1.90258341759902; // 2*Pi*(1-Ls(perihelion)/360);
        // Ls(perihelion)=250.99
        double rad2deg = 180. / Math.PI;

        int i;
        double zz, zanom, zdx = 10;
        double xref, zx0, zteta;
        // xref: mean anomaly, zx0: eccentric anomaly, zteta: true anomaly

        zz = (sol - peri_day) / year_day;
        zanom = 2. * Math.PI * (zz - Math.round(zz));
        xref = Math.abs(zanom);

        // Solve Kepler equation zx0 - e *sin(zx0) = xref
        // Using Newton iterations
        zx0 = xref + e_ellip * Math.sin(xref);
        do {
            zdx = -(zx0 - e_ellip * Math.sin(zx0) - xref) / (1. - e_ellip * Math.cos(zx0));
            zx0 = zx0 + zdx;
        } while (zdx > 1.e-7);
        if (zanom < 0)
            zx0 = -zx0;

        // Compute true anomaly zteta, now that eccentric anomaly zx0 is known
        zteta = 2. * Math.atan(Math.sqrt((1. + e_ellip) / (1. - e_ellip)) * Math.tan(zx0 / 2.));

        // compute Ls
        ls = zteta - timeperi;
        if (ls < 0)
            ls = ls + 2. * Math.PI;
        if (ls > 2. * Math.PI)
            ls = ls - 2. * Math.PI;
        // convert Ls into degrees
        ls = rad2deg * ls;

        return ls;
    }

    public double getMartianYear()
    {
        this.Convert2Ls();
        return this.martianYear;
    }

    public double getMartianMonth()
    {
        this.Convert2Ls();
        return this.martianMonth;
    }

    public double getMartianSol()
    {
        this.Convert2Ls();
        return this.sol;
    }

    public double getSolarLongitude()
    {
        this.Convert2Ls();
        return this.solarLongitude;
    }

    public double getJulian()
    {
        this.Convert2Ls();
        return this.julianValue;
    }
}
