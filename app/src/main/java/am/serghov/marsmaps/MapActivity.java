package am.serghov.marsmaps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import am.serghov.marsmaps.BottomSheet.CustomBottomSheet;
import am.serghov.marsmaps.Coordinate.latlon;
import am.serghov.marsmaps.Data.Locations;
import am.serghov.marsmaps.Data.MarsLocation;
import am.serghov.marsmaps.Map.Map;
import am.serghov.marsmaps.Map.MarsMapListener;
import am.serghov.marsmaps.Models.Calony;
import am.serghov.marsmaps.Models.ColonysArray;
import am.serghov.marsmaps.Models.UserReg;
import am.serghov.marsmaps.Rajwali.GLRenderer;
import am.serghov.marsmaps.Utils.Constants;
import am.serghov.marsmaps.searchData.Suggestion;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;
import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    FloatingSearchView searchView;
    private GLRenderer renderer;
    private Map mainMap;
    Target ElevationLoadingTarget;
    BottomSheetLayout.OnSheetStateChangeListener bottomSheetListener;
    private OkHttpClient client = new OkHttpClient();

    public Calony colony;

    //location
    private Location location;
    private double lat;
    private double lng;
    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;

    private Button btnLocationl;
    private Timer timer, timerPosition;

    private TimerTask timerTask;

    private SharedPreferences mMapsharedpreferences;
    private String userName;
    private int imgId;
    private TextView txtUserName;
    private ImageView profileImg;
    final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 10;
    private Context mContext;

    public static MapActivity instance;
    private boolean isConnected = false;
    private RelativeLayout relProgres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mContext = this;
        instance = this;
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        timerPosition = new Timer();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        buildGoogleApiClient();
        googleApiClient.connect();



        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        searchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                drawer.openDrawer(GravityCompat.START);
            }

            @Override
            public void onMenuClosed() {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        FloatingActionButton gpsFab = (FloatingActionButton) findViewById(R.id.gpsFab);
        relProgres = (RelativeLayout)findViewById(R.id.rel_progress);
        FloatingActionButton messageFab = (FloatingActionButton) findViewById(R.id.fab);
        gpsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurennetLocation();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        messageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, Colony.class);
                startActivity(intent);
            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        searchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                drawer.openDrawer(GravityCompat.START);
            }

            @Override
            public void onMenuClosed() {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery

                //pass them on to the search view
                //searchView.showProgress();

                List<SearchSuggestion> suggestionList = new ArrayList<SearchSuggestion>();

                String[] parts;
                if (newQuery.contains(", "))
                    parts = newQuery.toUpperCase().split(", ");
                else if (newQuery.contains(","))
                    parts = newQuery.toUpperCase().split(",");
                else
                    parts = newQuery.toUpperCase().split(" ");

                if (parts.length == 2) { //implement better with regex
                    try {
                        double lat, lon;
                        if (parts[0].contains("N")) {
                            lat = -Double.parseDouble(parts[0].substring(0, parts[0].length() - 1));
                        } else if (parts[0].contains("S")) {
                            lat = Double.parseDouble(parts[0].substring(0, parts[0].length() - 1));
                        } else {
                            lat = Double.parseDouble(parts[0]);
                        }

                        if (parts[1].contains("W")) {
                            lon = -Double.parseDouble(parts[1].substring(0, parts[1].length() - 1));
                        } else if (parts[1].contains("E")) {
                            lon = Double.parseDouble(parts[1].substring(0, parts[1].length() - 1));
                        } else {
                            lon = Double.parseDouble(parts[1]);
                        }
                        latlon searchedLoation = new latlon(lat, lon);

                        suggestionList.add(0, new Suggestion(new MarsLocation(searchedLoation, searchedLoation.toString())) {
                            @Override
                            public String getBody() {
                                return this.mLocation.getLocation().toString();
                            }
                        });

                        Log.e("LATLON", "" + lat + " " + lon);
                    } catch (NumberFormatException e) {

                    }
                }
                int k = 0;

                for (int i = 0; i < Locations.locations.length; i++) {
                    if (k > 6)
                        break;
                    if (Locations.locations[i].getName().toLowerCase().startsWith(newQuery.toLowerCase())) {
                        suggestionList.add(0, new Suggestion(Locations.locations[i]));
                        k++;
                    }
                }

                searchView.swapSuggestions(suggestionList);
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

                //ColorSuggestion colorSuggestion = (ColorSuggestion) searchSuggestion;
                //refreshBackgroundColor(colorSuggestion.getColor().getName(), colorSuggestion.getColor().getHex());

                Suggestion current = (Suggestion) searchSuggestion;

                mainMap.animateTo(current.getLocation().getLocation());

                Log.d("SEARCH", "onSuggestionClicked()");

            }

            @Override
            public void onSearchAction() {

                Log.d("SEARCH", "onSearchAction()");
            }
        });

        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset > 0.05)
                    renderer.hideScale();
                else
                    renderer.showScale();

                //drawer.bringChildToFront(drawerView);
                //drawer.requestLayout();
                renderer.moveToRight(1 - slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

                //since the drawer might have opened as a results of
                //a click on the left menu, we need to make sure
                //to close it right after the drawer opens, so that
                //it is closed when the drawer is  closed.

                searchView.closeMenu(false);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //renderer.show();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        mainMap = (Map) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        final RajawaliSurfaceView surface = new RajawaliSurfaceView(this);
        surface.setFrameRate(60.0);
        surface.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);

        ((RelativeLayout) findViewById(R.id.smallMarsMapContainer)).addView(surface);

        surface.setZOrderOnTop(false);    // necessary
        surface.setTransparent(true);
        // surface.setZOrderOnTop(false);

        //surface.getHolder().setFormat(PixelFormat.TRANSPARENT);
        surface.setAntiAliasingMode(IRajawaliSurface.ANTI_ALIASING_CONFIG.MULTISAMPLING);
        renderer = new GLRenderer(this, mainMap);
        surface.setSurfaceRenderer(renderer);

        //Copyright NASA / JPL / GSFC / Arizona State University

        final CustomBottomSheet bottomSheet = (CustomBottomSheet) findViewById(R.id.bottomsheet);
        bottomSheetListener = new BottomSheetLayout.OnSheetStateChangeListener() {
            @Override
            public void onSheetStateChanged(BottomSheetLayout.State state) {
                Log.e("STATE", "" + state);
                if (state == BottomSheetLayout.State.HIDDEN)
                    renderer.showScale();
            }
        };
        renderer.setBottomSheet(bottomSheet);


        mainMap.AddLongPressListener(new MarsMapListener.LongPressListener() {
            @Override
            public void OnLongPress(MotionEvent event, latlon coord) {
                bottomSheet.showWithSheetView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.sheet_layout, bottomSheet, false));
                bottomSheet.addOnSheetStateChangeListener(bottomSheetListener);

                renderer.hideScale();

                Weather.getSurfaceTemperature(coord, new Weather.WeatherDataListener() {
                    @Override
                    public void onData(double data) {
                        try
                        {
                            ((TextView)(findViewById(R.id.surfaceTemperature))).setText("" + (int)(data - 273) + " C" );
                        }
                        catch (NullPointerException e)
                        {

                        }

                    }
                });

                Weather.getPressure(coord, new Weather.WeatherDataListener() {
                    @Override
                    public void onData(double data) {
                        try {
                            ((TextView) (findViewById(R.id.pressure))).setText("" + (int) (data) + " P");
                        }catch (NullPointerException e)
                        {

                        }

                    }
                });

                Weather.getHorizontalWind(coord, new Weather.WeatherDataListener() {
                    @Override
                    public void onData(double data) {
                        try
                        {
                            ((TextView)(findViewById(R.id.horizontalWind))).setText("" + (int)(data*10)/10.0 + " m/s" );
                        } catch (NullPointerException e)
                        {

                        }

                    }
                });

                ((TextView) findViewById(R.id.textView2)).setText(coord.toString());
                int nearbyPlaces = 0;
                for (int i = 0; i < Locations.locations.length; i++) {
                    if (coord.MarsDistance(Locations.locations[i].getLocation()) < 100000) {
                        nearbyPlaces++;
                    }
                }
                ((TextView) findViewById(R.id.nearby)).setText("" + nearbyPlaces);

                String marsUrl = "http://ms-mars.mars.asu.edu/?SERVICE=WMS&REQUEST=GetMap&VERSION=1.1.1&FORMAT=image/png&SRS=EPSG:4326&LAYERS=MOLA_Color&WIDTH=1&HEIGHT=1&BBOX=";

                ElevationLoadingTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (bitmap == null) {
                            return;
                        }
                        try
                        {
                            ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(bitmap);
                            ((TextView) findViewById(R.id.elevation)).setText("" + Elevation.Color2Elevation(Color.red(bitmap.getPixel(0, 0)), Color.green(bitmap.getPixel(0, 0)), Color.blue(bitmap.getPixel(0, 0)))+" m");
                        }
                        catch (NullPointerException e)
                        {

                        }

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.e("COLOR", "ERROR");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Log.e("COLOR", "PREP");
                    }
                };

                Picasso.with(getApplicationContext())
                        .load(marsUrl + (coord.getLon() - 0.001) + "," + (-coord.getLat() - 0.001) + "," + (coord.getLon() + 0.001) + "," + (-coord.getLat() + 0.001))
                        .into(ElevationLoadingTarget);
            }
        });

        mMapsharedpreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);

        userName = mMapsharedpreferences.getString("userName", "Map");
        imgId = mMapsharedpreferences.getInt("imgId", -1);

        View hView = navigationView.getHeaderView(0);
        txtUserName = (TextView) hView.findViewById(R.id.txt_user_name);
        txtUserName.setText(userName);
        profileImg = (ImageView) hView.findViewById(R.id.imageView);
        if (imgId > 0) profileImg.setImageResource(imgId);

        txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


//        if (WsConnection.getInstance().wsClient != null && !(WsConnection.getInstance().wsClient.isConnected())) {
//            WsConnection.getInstance().initializeClient("ws://192.168.8.100:3030");
//        }

        Weather.getSurfaceTemperature(new latlon(0, 0), new Weather.WeatherDataListener() {
            @Override
            public void onData(double data) {
                Log.e("DATALISTENER", ""+data);
            }
        });

        Weather.getDensity(new latlon(0, 0), new Weather.WeatherDataListener() {
            @Override
            public void onData(double data) {
                Log.e("DATALISTENER DENSITY", ""+data);
            }
        });

    }


    private UserReg getSendCords(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        UserReg tmp = null;

        try
        {
            tmp = new Gson().fromJson(response.body().string(),UserReg.class);
        }
        catch (Exception  e){}

        return tmp;
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, UserReg> {

        private String resp;

        @Override
        protected UserReg doInBackground(String... params) {

            UserReg userReg = null;
            try {
                userReg = getSendCords(Constants.USER_REG + params[0] + Constants.USER_LNG + params[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return userReg;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(UserReg result) {

            // execution of result of Long time consuming operation
            relProgres.setVisibility(View.GONE);
            if (result!=null){
                colony = result.getColony();
                Constants.setToken(result.getToken());
                WsConnection.getInstance();
                //WsConnection.getInstance().initializeClient("ws://192.168.8.108:3000/chat?token="+Constants.TOKEN);
                WsConnection.getInstance().initializeClient("ws://singularity.am:3000/chat?token="+Constants.getToken());
            }
            //new AsyncColonysArray().execute("http://marslive.herokuapp.com/colonies");

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            relProgres.setVisibility(View.VISIBLE);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {

        }
    }


    private ColonysArray getConl(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return new Gson().fromJson(response.body().string(),ColonysArray.class);
    }

    private class AsyncColonysArray extends AsyncTask<String, String, ColonysArray> {

        private String resp;

        @Override
        protected ColonysArray doInBackground(String... params) {

            ColonysArray colonysArray = null;
            try {
                colonysArray = getConl(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return colonysArray;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ColonysArray result) {
            // execution of result of Long time consuming operation

            if (result!=null){
                //result.getColonies().getItem(0).
            }

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

    private void editSharedPreferences(String userName, int imgID) {
        SharedPreferences.Editor editor = mMapsharedpreferences.edit();
        if (!(userName.equals(""))) editor.putString("userName", userName);
        if (imgID > 0) editor.putInt("imgId", imgID);
        editor.apply();
    }

    private void showAlertDialog() {
        final ImageAdapter imageAdapter = new ImageAdapter(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        GridView gridView = new GridView(this);
        gridView.setAdapter(imageAdapter);
        gridView.setNumColumns(5);

        builder.setView(gridView);
        builder.setTitle("Choose profile picture");
        final AlertDialog ad = builder.show();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // do something here
                int imgId = imageAdapter.imageIDs[position];
                profileImg.setImageResource(imgId);
                editSharedPreferences("", imgId);
                ad.dismiss();
            }
        });
    }

    protected void showInputDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(MapActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edit_name);
        editText.setText(txtUserName.getText().toString());

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        txtUserName.setText(editText.getText().toString());
                        editSharedPreferences(editText.getText().toString(), -1);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_elevation) {
            Map frag = (Map) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            frag.setElevationMap();
        } else if (id == R.id.nav_visible) {
            Map frag = (Map) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            frag.setVisibleMap();
        } else if (id == R.id.nav_infrared) {
            Map frag = (Map) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            frag.setInfraredMap();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        try {
            if (!isConnected && Constants.getToken().equals("")){
                isConnected = true;

                new AsyncTaskRunner().execute(location.getLatitude() + "", location.getLongitude() + "");

            }
            mainMap.animateTo(new latlon(-location.getLatitude(), -location.getLongitude()));
            currentGpsLocation();
        } catch (Exception e)
        {
            Intent i = new Intent(getBaseContext(), NoGPSActivity.class);
            startActivity(i);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        this.location = location;
        getCurennetLocation();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void getCurennetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MapActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);


        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            if (Constants.getToken().equals("")){
                new AsyncTaskRunner().execute(location.getLatitude()+"",location.getLongitude()+"");
            }

            mainMap.animateTo(new latlon(-lat, -lng));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //noinspection MissingPermission
                    location = LocationServices.FusedLocationApi.getLastLocation(
                            googleApiClient);

                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    mainMap.animateTo(new latlon(-lat, -lng));


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void sendCordinets() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if (location != null) {
                    MapActivity.this.sendPositions(
                            location.getLatitude(),
                            location.getLongitude());
                }

            }
        };

        timerPosition.schedule(timerTask, 0, 60 * 60 * 1000);
    }

    public void currentGpsLocation() {

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }

                        location = LocationServices.FusedLocationApi.getLastLocation(
                                googleApiClient);

//                        if (!(location == null)) {
//                            mainMap.animateTo(new latlon(-location.getLatitude(), -location.getLongitude()));
//                        }
                    }

                });
            }
        };

        timer.schedule(timerTask, 0, 5000);
    }

    public void sendPositions(double latitude, double longitude) {
        try {
            JSONObject driverPositionsJSON = new JSONObject();
            driverPositionsJSON.put("latitude", latitude);
            driverPositionsJSON.put("longitude", longitude);
            driverPositionsJSON.put("id", 12);
            JSONObject jsonDriver = new JSONObject();
            jsonDriver.put("action", "change_position");

            jsonDriver.put("coordinates", driverPositionsJSON);

            if (WsConnection.getInstance().wsClient != null && WsConnection.getInstance().wsClient.isConnected()) {

                WsConnection.getInstance().wsClient.send(jsonDriver.toString());
            }

        } catch (JSONException e) {

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();

        try {
            alert.show();
        } catch (Exception e) {
        }

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
