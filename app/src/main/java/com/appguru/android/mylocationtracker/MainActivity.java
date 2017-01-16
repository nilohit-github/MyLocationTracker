package com.appguru.android.mylocationtracker;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private final String log_tag = "MyLocationTracker";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private TextView mTextView;
    private TextView mTextViewZip;
    private TextView mTextViewHome;
    private static final int REQUEST_LOCATION = 0;
    //private List<String> MyAddress ;
    private String home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.location_detail);
        mTextViewZip = (TextView)findViewById(R.id.location_zip);
        mTextViewHome = (TextView) findViewById(R.id.location_home);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.i("inside create", "create");


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(1000);
        Log.i("inside connected", "connected start");


        Log.i("inside else connected", "connected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            Log.i(log_tag, "Google api Connected inside if condition to check run time ");

        } else {
            Log.i(log_tag, "Google api Connected Get Request" );
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

        Log.i(log_tag, "Google api Connected Get Request" );
    }

    // Request Permissions
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Check Permissions Now
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION);
                    Log.i(log_tag, "Inside onRequestPermissionsResult if condition ");
                } else {
                    Log.i(log_tag, "Inside onRequestPermissionsResult else condition ");
                    //
                    //
                    //
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }
                // We can now safely use the API we requested access to

            } else {
                Log.i(log_tag, "Permission was denied or request was cancelled");
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

        Log.i(log_tag,"connection has  been suspended");
        Log.i("inside suspended","suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.i(log_tag,"connection Failed !!");
        Log.i("inside fail","fail");

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i("inside",location.toString());
        Log.i("inside location change","lchange");
        mTextView.setText("Lat: "+location.getLatitude()+
                "\nLang:"+location.getLongitude()+
                "\nprovider "+location.getProvider()+
                "\nAccuracy:"        +location.hasAccuracy()+
                "\n"+location.getTime()+
                "\n"+location.getSpeed());

       // Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

       // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       // String formattedDate = df.format(c.getTime());

        Date date = new Date();
        System.out.println("Date: " + date);

        DateFormat df = new SimpleDateFormat("hh");
        String hour = df.format(date);
        int curTime = Integer.parseInt(hour);

        getAddress(location.getLatitude(),location.getLongitude(),curTime);


    }

    private void getAddress(double latitude, double longitude ,int curTime) {

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0)
        {

            mTextView.setText("current address : "+addresses.get(0).getLocality());
            mTextViewZip.setText("current zip : "+addresses.get(0).getPostalCode());
            if(curTime>=1 & curTime <=6)
            {
                mTextViewHome.setText("The person resides in the city of  : "+addresses.get(0).getLocality()+"  \n at this zip  "+addresses.get(0).getPostalCode());
                home = (String) mTextViewHome.getText();

            }
            else
                if(home != null && !home.isEmpty())
                    mTextViewHome.setText(home);
                else
                    mTextViewHome.setText("Please wait for 24 hrs to get the hoem location of this individual");

           // mTextViewTime.setText(addresses.get(0).getCountryName());
        }
        else
        {
            Log.i(log_tag,"address not found !!");
            mTextView.setText("address not found !!");
            // do your staff
        }
    }
}
