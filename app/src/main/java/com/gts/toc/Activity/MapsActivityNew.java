package com.gts.toc.Activity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gts.toc.R;
import com.gts.toc.object.ObjPosition;
import com.gts.toc.object.ObjUser;
import com.gts.toc.response.PositionResponse;
import com.gts.toc.rest.ApiClient;
import com.gts.toc.rest.ApiService;
import com.gts.toc.rest.DataParser;
import com.gts.toc.rest.UserTask;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivityNew extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static String GPS_KEY   = "gps";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mProvider;
    private Toolbar mToolbar;
    private String mState;
    private TextView mViewMessage;
    private ProgressBar mProgressBar;
    private RelativeLayout mLayoutProgress;
    private RelativeLayout mLayoutDetail;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private double CustLatitude;
    private double CustLongitude;
    private Location mLocation;
    private String Technician;
    private String ClientLocation;
    private double TechLatitude;
    private double TechLongitude;
    private boolean isFirst = true;
    int delay       = 0;                // delay for 0 sec.
    int period      = 15000;            // repeat every 10 sec.
    Timer timer     = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        Intent mIntent  = getIntent();
        mState          = mIntent.getStringExtra(GeneralConstant.FRAGMENT_STATE);
        Technician	    = mIntent.getStringExtra(GeneralConstant.PARAM_TECHID);
        ClientLocation  = mIntent.getStringExtra(GeneralConstant.PARAM_LOCATION);
        mInitMaps();
        Initialise();

        //RUN EVERY 10 SECOND
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (Utility.isNetworkConnected())
                    GetDetailProgress.run();
            }
        }, delay, period);
    }

    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(MapsActivityNew.this, MainActivity.class);
        intent.putExtra(GeneralConstant.FRAGMENT_STATE, mState);
        ActivityTransitionLauncher.with(MapsActivityNew.this).from(findViewById(R.id.layout_job_detail)).launch(intent);
    }

    private void Initialise(){
        mToolbar    = (Toolbar) findViewById(R.id.toolbarDetail);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLayoutDetail   = (RelativeLayout) findViewById(R.id.layout_job_detail);
        mViewMessage    = (TextView) findViewById(R.id.view_message);
        mProgressBar    = (ProgressBar) findViewById(R.id.progressBar);
        mLayoutProgress = (RelativeLayout) findViewById(R.id.LayoutProgressBar);
        mViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetDetailProgress.run();
            }
        });

        if (!ClientLocation.contentEquals("")){
            String delims       = "[,]";
            String[] location   = ClientLocation.split(delims);
            CustLatitude        = Double.parseDouble(location[0]);
            CustLongitude       = Double.parseDouble(location[1]);
        }else{
            CustLatitude        = 0;
            CustLongitude       = 0;
        }
        LatLng ClientLang       = new LatLng(CustLatitude, CustLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ClientLang,12));
//        if (Utility.isNetworkConnected())
//            GetDetailProgress.run();
//        else
//            onShowErrorMessage(getResources().getString(R.string.msg_internet_problem));
    }
    private Runnable GetDetailProgress = new Runnable() {
        public void run() {
            onGetJobDetail();
        }
    };
    private void onGetJobDetail() {
        if (isFirst){
            isFirst = false;
            onShowLoading();
        }
        onHideErrorMessage();
        final ObjUser mUserinfo = new ObjUser();
        ApiService apiService   = ApiClient.getClient().create(ApiService.class);
        retrofit2.Call<PositionResponse> mRequest  = apiService.getTechnician(mUserinfo.UserAuth, mUserinfo.UserID, Technician);
        mRequest.enqueue(new retrofit2.Callback<PositionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<PositionResponse> call, retrofit2.Response<PositionResponse> mResponse) {
                PositionResponse Result  = mResponse.body();
                int ResultCode          = Result.getResultState();
                    switch (ResultCode) {
                    case 200:
                        onHideLoading();
                        onHideErrorMessage();
                        mLayoutDetail.setVisibility(View.VISIBLE);
                        ObjPosition mPosition = Result.getResultData();
                        ShowJobDetail(mPosition.getPosition());
                        break;
                    case 202:
                        onHideLoading();
                        onHideErrorMessage();
                        mLayoutDetail.setVisibility(View.GONE);
                        UserTask.onGetAuth(GetDetailProgress, mUserinfo.Email);
                        break;
                    case 203:
                        onHideLoading();
                        mLayoutDetail.setVisibility(View.GONE);
                        onShowErrorMessage(getResources().getString(R.string.msg_request_failed));
                        break;
                    case 204:
                        onHideLoading();
                        mLayoutDetail.setVisibility(View.GONE);
                        onShowErrorMessage(getResources().getString(R.string.sync_failed));
                        break;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<PositionResponse> call, Throwable t) {
                onHideLoading();
                mLayoutDetail.setVisibility(View.GONE);
                onShowErrorMessage(getResources().getString(R.string.sync_failed));
            }
        });
    }
    private void onHideErrorMessage() {
        mViewMessage.setEnabled(false);
        mViewMessage.setVisibility(View.GONE);
    }
    private void onShowErrorMessage(String Message) {
        mViewMessage.setEnabled(true);
        mViewMessage.setVisibility(View.VISIBLE);
        mViewMessage.setText(Message);
    }
    private void onShowLoading(){
        mLayoutProgress.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }
    private void onHideLoading(){
        mLayoutProgress.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void ShowJobDetail(String TechLocation){
        if (!TechLocation.contentEquals("")){
            String delims       = "[,]";
            String[] location   = TechLocation.split(delims);
            TechLatitude        = Double.parseDouble(location[0]);
            TechLongitude       = Double.parseDouble(location[1]);
        }else{
            TechLatitude        = 0;
            TechLongitude       = 0;
        }
        LatLng ClientLang       = new LatLng(CustLatitude, CustLongitude);
        LatLng teknisiLang      = new LatLng(TechLatitude,TechLongitude);
        getRoadMap(ClientLang, teknisiLang);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mInitMaps();
        mProvider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(mProvider.contains(GPS_KEY)) {
            mGetLocation();
        }else
            Dialog.ConfirmationDialog(
                    MapsActivityNew.this,
                    getResources().getString(R.string.msg_location_off),
                    getResources().getString(R.string.btn_ok),
                    GPSSetting,
                    getResources().getString(R.string.btn_no));
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mProvider.contains(GPS_KEY)){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }
    private void handleNewLocation(Location location) {
        double Latitude     = location.getLatitude();
        double Longitude    = location.getLongitude();
        LatLng ClientLang   = new LatLng(Latitude, Longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ClientLang,12));
//        ShowJobDetail(Latitude + "," + Longitude);
    }
    private void mGetLocation(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(100 * 1000)             // 10 seconds, in milliseconds
                .setFastestInterval(10 * 1000);      // 1 second, in milliseconds
        mGoogleApiClient.connect();
    }
    public Runnable GPSSetting = new Runnable() {
        public void run() {
            Intent intent   = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    };
    private void mInitMaps() {
        if (mMap == null) {
            MapFragment mMapFragment    = (MapFragment) getFragmentManager().findFragmentById(R.id.mapClient);
            mMap                        = mMapFragment.getMap();
            mUiSettings = mMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(true);
            mUiSettings.setCompassEnabled(true);
            mMap.setMyLocationEnabled(true);
            if (mMap != null)
                setUpMap();

            View mapView    = mMapFragment.getView();
            View myLocation = mapView.findViewWithTag("GoogleMapMyLocationButton");
            myLocation.setPadding(15,15,15,15);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // size of button in dp
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.setMargins(15, 0, 0, 15);
            myLocation.setLayoutParams(params);
        }
    }
    private void setUpMap() {
//        ShowJobDetail("0,0");
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            mProvider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(mProvider.contains(GPS_KEY)) {
                mGoogleApiClient.connect();
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } else {
            handleNewLocation(mLocation);
        }
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {}
        }
    }
    @Override
    public void onLocationChanged(Location location) {
//        if (mChangeLocation) {
//            double CustLatitude     = location.getLatitude();
//            double CustLongitude    = location.getLongitude();
//            teknisiLang             = new LatLng(CustLatitude, CustLongitude);
//            getRoadMap();
//        }
    }

    private void getRoadMap(LatLng ClientLang, LatLng teknisiLang){
        mMap.clear();
        MarkerOptions optionsDesc   = new MarkerOptions().position(ClientLang).title("Customer Position");
        optionsDesc.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(optionsDesc);
        MarkerOptions optionsTech       = new MarkerOptions().position(teknisiLang).title("Technician Position");
        optionsTech.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//        optionsTech.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_black_24dp));
        mMap.addMarker(optionsTech);

        String url          = getUrl(ClientLang, teknisiLang);
        FetchUrl FetchUrl   = new FetchUrl();
        FetchUrl.execute(url);
    }
    private String getUrl(LatLng ClientLang, LatLng teknisiLang) {
        String str_origin   = "origin=" + teknisiLang.latitude + "," + teknisiLang.longitude;
        String str_dest     = "destination=" + ClientLang.latitude + "," + ClientLang.longitude;
        String sensor       = "sensor=false";
        String avoid        = "avoid=highways";
//        String mode         = "mode=bicycling";
        String parameters   = str_origin + "&" + str_dest + "&" + sensor + "&" + avoid;
        String output       = "json";
        String url          = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url         = new URL(strUrl);
            urlConnection   = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream         = urlConnection.getInputStream();

            BufferedReader br   = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb     = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());
            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";

            for (int i = 0; i < result.size(); i++) {
                points      = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if(j==0){    // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(getResources().getColor(R.color.colorInfogreen));
                Log.d("onPostExecute","onPostExecute lineoptions decoded");
            }
            getSupportActionBar().setTitle(distance + ", " + duration);
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}