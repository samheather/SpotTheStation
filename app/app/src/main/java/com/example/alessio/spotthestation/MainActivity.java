package com.example.alessio.spotthestation;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The station content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity implements LocationListener {

    /** {@link CardScrollView} to use as the station content view. */
    //private CardScrollView mCardScroller;

    /**
     * "Hello World!" {@link View} generated by {@link #buildView()}.
     */
    private View mView;

    private LocationManager locationManager;
    private Location location;
    private long MIN_TIME_BW_UPDATES = 1000;
    private float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    double longitude = 0;
    double latitude = 0;

    private static double ISSLongitude = 0;
    private static double ISSLatitude = 0;
    private static long ISSNextTime = 0;
    private static long ISSNextDuration = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.station);

        TextView leftText = (TextView) findViewById(R.id.textView2);
        leftText.setText("42");

        new RequestIISLocation().execute("http://api.open-notify.org/iss-now.json");

        new RequestIISTime().execute("http://api.open-notify.org/iss-pass.json?lat=50&lon=0.1");

        // Subscribe to the location service - from now on Longitude and Latitude doubles above will
        // be updated when users location changes significantly.
        getLocation();

        /*
        mView = buildView();

        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });*/
        // Handle the TAP event.
/*        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Plays disallowed sound to indicate that TAP actions are not supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.DISALLOWED);
            }
        });
        setContentView(mCardScroller);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        //mCardScroller.deactivate();
        super.onPause();
    }

    private void refreshUI() {
        System.out.println("Refreshing the UI");

        // TODO: Remove updating the current IIS time for every UI update
        new RequestIISTime().execute("http://api.open-notify.org/iss-pass.json?lat=50&lon=0.1");

        TextView timeText = (TextView)findViewById(R.id.timeTillVisible);
        long unixTime = System.currentTimeMillis() / 1000L;
        long timeToShow = ISSNextTime - unixTime;

        if (unixTime - ISSNextTime > ISSNextDuration) {
            // Convert to String
            Date date = new Date(timeToShow * 1000L);
            // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); // the format of your date
            String formattedDate = sdf.format(date);
            System.out.println(formattedDate);

            timeText.setText(formattedDate);
        }
        else {
            timeText.setText("Look Up!");
        }
    }

    /**
     * Builds a Glass styled "Hello World!" view using the {@link Card} class.
     */
    private View buildView() {
        Card card = new Card(this);
        //getMenuInflater().inflate(R.layout.station);
        card.setText(R.string.hello_world);
        card.addImage(R.drawable.up);
        return card.getView();

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

//            (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    System.out.println("Network - Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        System.out.println("GPS - GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location loc) {
        System.out.println("New GPS position updated");

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        System.out.println("User: Lat: " + latitude + ", Longitude: " + longitude);

        System.out.println(updateElevation(longitude, latitude, 165, 15).getDegsHeading());

        // Refresh GUI
        refreshUI();
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public Position updateElevation(double user_long_degs, double user_lat_degs, double iss_long_degs, double iss_lat_degs) {
        // console.log("my lon", user_long_degs, " my lat", user_lat_degs, "isslon", iss_long_degs, "isslat", iss_long_degs);
        Position position = new Position();
        double lat_difference = Math.abs(user_lat_degs - iss_lat_degs);
        double long_difference = Math.abs(user_long_degs - iss_long_degs);

        double lat_distance = lat_difference * 111;
        double long_distance = long_difference * 111;

        double total_distance = Math.sqrt(Math.pow(lat_distance, 2) + Math.pow(long_distance, 2));
        double elevation = total_distance * 0.009;

        position.setUserDistance(Math.round(elevation * 10) / 10);

        //Finding distance to station
        double userDistance = Math.sqrt(Math.pow(total_distance, 2) + Math.pow(370, 2));
        position.setUserDistance((Math.round(userDistance * 10) / 10));

        //Finding heading
        double radiansHeading = Math.atan(long_distance / lat_distance);
        double degsHeading = (radiansHeading / Math.PI) * 180;
        position.setDegsHeading(degsHeading);

        return position;
    }

    public static double getISSLongitude() {
        return MainActivity.ISSLongitude;
    }

    public static void setISSLongitude(double ISSLongitude) {
        MainActivity.ISSLongitude = ISSLongitude;
    }

    public static double getISSLatitude() {
        return MainActivity.ISSLatitude;
    }

    public static void setISSLatitude(double ISSLatitude) {
        MainActivity.ISSLatitude = ISSLatitude;
    }

    public static long getISSNextTime() {
        return MainActivity.ISSNextTime;
    }

    public static void setISSNextTime(long newNextTime) {
        MainActivity.ISSNextTime = newNextTime;
    }

    public static long getISSNextDuration() {
        return MainActivity.ISSNextDuration;
    }

    public static void setISSNextDuration(long newNextDuration) {
        MainActivity.ISSNextDuration = newNextDuration;
    }
}
