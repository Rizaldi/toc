package com.gts.toc.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gts.toc.Activity.CreateOrderActivityOK;
import com.gts.toc.Activity.MainActivity;
import com.gts.toc.R;
import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.model.MstParams;
import com.gts.toc.object.ObjBanner;
import com.gts.toc.object.ObjUser;
import com.gts.toc.response.BannerResponse;
import com.gts.toc.rest.ApiClient;
import com.gts.toc.rest.ApiService;
import com.gts.toc.rest.DataParser;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.squareup.picasso.Picasso;

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
import java.util.Locale;

/**
 * Created by warsono on 11/12/16.
 */

public class DefaultFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static String GPS_KEY       = "gps";
    boolean mChangeLocation             = false;
    private DatabaseHandler mDataBase   = new DatabaseHandler();
    private List<ObjBanner> mBannerList = new ArrayList<ObjBanner>();
    private Context mContext;
//    private ViewGroup mRootView;
    private int mBannerWidth;
    private int mBannerHeight;
    private ViewFlipper BannerFlipper;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private String mProvider;
    private GoogleApiClient mGoogleApiClient;
    private EditText mInputAddress;
    private TextView mInputDistance;
    private LocationRequest mLocationRequest;
    private FloatingActionButton mPickUpButton;
    private int MaxDistance;
    private static View mRootView;

    public DefaultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.nav_home));
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }
        try {
            mRootView = inflater.inflate(R.layout.fragment_default, container, false);
        } catch (InflateException e) {}

//        mRootView           = (ViewGroup) inflater.inflate(R.layout.fragment_default, null);
        mContext            = getActivity();
        mProvider           = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        mInputDistance      = (TextView) mRootView.findViewById(R.id.viewEstimasiDistance);
        mInputAddress       = (EditText) mRootView.findViewById(R.id.input_address);
        mInputDistance      = (TextView) mRootView.findViewById(R.id.viewEstimasiDistance);
        mPickUpButton       = (FloatingActionButton) mRootView.findViewById(R.id.btnPickup);
        mPickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAddress   = mInputAddress.getText().toString();
                String strPoint     = mMap.getCameraPosition().target.latitude + "," + mMap.getCameraPosition().target.longitude;
                MaxDistance         = getMaxDistance();
                double mDistance    = getDistance();
                if (mDistance <= MaxDistance) {
                    Intent intentDetail = new Intent(mContext, CreateOrderActivityOK.class);
                    intentDetail.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_HOME);
                    intentDetail.putExtra(GeneralConstant.PARAM_LOCATION, strPoint);
                    intentDetail.putExtra(GeneralConstant.PARAM_ADDRESS, strAddress);
                    ActivityTransitionLauncher.with((Activity) mContext).from(mRootView.findViewById(R.id.mapOrder)).launch(intentDetail);
                } else {
                    Dialog.InformationDialog(mContext, getResources().getString(R.string.over_distance));
                }
            }
        });
        initBanner();
        mInitMaps();
        checkGPS();

        if (Utility.isNetworkConnected())
            GetBannerProgress.run();
        else
            showDefaultFlipper();

        return mRootView;
    }
    private void checkGPS(){
        mProvider = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(mProvider.contains(GPS_KEY)) {
            mGetLocation();
        }else {
            Dialog.ConfirmationDialog(
                    mContext,
                    getResources().getString(R.string.msg_location_off),
                    getResources().getString(R.string.btn_ok),
                    GPSSetting,
                    getResources().getString(R.string.btn_no));
        }
    }
    private void initBanner() {
        mBannerWidth    = Utility.getScreenWidth();
        mBannerHeight   = (int) (0.35 * mBannerWidth);
        RelativeLayout.LayoutParams layoutParams    = new RelativeLayout.LayoutParams(mBannerWidth, mBannerHeight);
        RelativeLayout mBannerLayout                = (RelativeLayout) mRootView.findViewById(R.id.imgBanner);
        BannerFlipper   = new ViewFlipper(mContext);
        mBannerLayout.addView(BannerFlipper);
        mBannerLayout.setLayoutParams(layoutParams);
        BannerFlipper.setLayoutParams(layoutParams);
//        showDefaultFlipper();
    }

    private void mGetLocation(){
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
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
    private void mInitMaps() {
        if (mMap == null) {
            MapFragment mMapFragment    = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapOrder);
            mMap                        = mMapFragment.getMap();
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Location location = new Location("Location");
                    location.setLatitude(cameraPosition.target.latitude);
                    location.setLongitude(cameraPosition.target.longitude);
                    SetAddress(cameraPosition.target.latitude, cameraPosition.target.longitude);

                    LatLng latLng               = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                    MarkerOptions options       = new MarkerOptions().position(latLng).title("Click for Pick Up");
                    mMap.clear();
                    mMap.addMarker(options);
                    String url          = getUrl(new LatLng(GeneralConstant.LOC_LATITUDE, GeneralConstant.LOC_LONGITUDE),latLng);
                    FetchUrl FetchUrl   = new FetchUrl();
                    FetchUrl.execute(url);
                }
            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Location location = new Location("Location");
                    location.setLatitude(marker.getPosition().latitude);
                    location.setLongitude(marker.getPosition().longitude);
                    handleNewLocation(location);
                    SetAddress(marker.getPosition().latitude, marker.getPosition().longitude);
                    return false;
                }
            });
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
    public Runnable GPSSetting = new Runnable() {
        public void run() {
            Intent intent   = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    };
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Default Location"));
    }
    private void handleNewLocation(Location location) {
        double currentLatitude      = location.getLatitude();
        double currentLongitude     = location.getLongitude();
        LatLng latLng               = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions options       = new MarkerOptions().position(latLng).title("My Location");
        mMap.clear();
        mMap.addMarker(options);

        SetAddress(currentLatitude, currentLongitude);
        SetDistance( new LatLng(GeneralConstant.LOC_LATITUDE, GeneralConstant.LOC_LONGITUDE),latLng);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            mProvider = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(mProvider.contains(GPS_KEY)) {
                mGoogleApiClient.connect();
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
        else {
            handleNewLocation(location);
            double currentLatitude      = location.getLatitude();
            double currentLongitude     = location.getLongitude();
            LatLng latLng               = new LatLng(currentLatitude, currentLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        }
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {}
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        if (mChangeLocation) {
            handleNewLocation(location);
            mChangeLocation = false;
        }
    }
    private void SetAddress(Double Latitude, Double Longitude){
        Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(Latitude, Longitude, 1);

            if(addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                }
                mInputAddress.setText(strReturnedAddress.toString());
            }
            else
                mInputAddress.setText("");
        } catch (IOException e) {
            mInputAddress.setText("");
        }
    }
    private int getMaxDistance(){
        List<MstParams> ParamsList = mDataBase.GetParams(GeneralConstant.PARAMS_DISTANCE_ID);
        if (ParamsList.size() > 0){
            MstParams mParams = ParamsList.get(0);
            if (mParams.getParamsValue() != null){
                return Integer.parseInt(mParams.getParamsValue());
            }else{
                return 20;
            }
        }else{
            return 20;
        }
    }
    private double getDistance (){
        String strDistance      = mInputDistance.getText().toString();
        if (strDistance.contains("km")){
            String Distance     = strDistance.replace(" km", "");
            double dbDistance   = Double.parseDouble(Distance);
            return dbDistance;
        }else{
            String Distance     = strDistance.replace(" m", "");
            double dbDistance   = (Double.parseDouble(Distance))/1000;
            return dbDistance;
        }
    }
    private void SetDistance(LatLng teknisiLang, LatLng destLang){
        String url          = getUrl(destLang, teknisiLang);
        FetchUrl FetchUrl   = new FetchUrl();
        FetchUrl.execute(url);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLang, 16));
    }
    final int MODE_BICYCLING = 1;
    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin   = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest     = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor       = "sensor=false";
        String parameters   = str_origin + "&" + str_dest + "&" + sensor + "&" + MODE_BICYCLING;
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
            br.close();
        } catch (Exception e) {} finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (isFirst){
//                mLoadingDialog = ProgressDialog.show(CreateOrderActivity.this, "", getResources().getString(R.string.msg_loading));
//            }
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {}
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
                jObject             = new JSONObject(jsonData[0]);
                DataParser parser   = new DataParser();
                routes              = parser.parse(jObject);
            } catch (Exception e) {}
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            String duration = "";
            String distance = "";

            for (int i = 0; i < result.size(); i++) {
                points      = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if(j==0){
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
            }
            mInputDistance.setText(distance);
        }
    }

    void showDefaultFlipper(){
        ImageView BannerImage = new ImageView(mContext);
        BannerFlipper.addView(BannerImage);
        Picasso.with(mContext)
                .load(R.drawable.banner_0)
                .resize(mBannerWidth, mBannerHeight)
                .into(BannerImage);
        BannerImage = new ImageView(mContext);
        BannerFlipper.addView(BannerImage);
        Picasso.with(mContext)
                .load(R.drawable.banner_1)
                .resize(mBannerWidth, mBannerHeight)
                .into(BannerImage);
        BannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isNetworkConnected()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://gianteknologi.com"));
                    startActivity(intent);
                }else {
                    Dialog.InformationDialog(mContext, getResources().getString(R.string.msg_no_internet));
                }
            }
        });
        BannerFlipper.setFlipInterval(2000);
        BannerFlipper.startFlipping();
    }
    void showServerBanner(){
        int Count       = mBannerList.size();
        for (int i=0; i < Count; i++){
            final ObjBanner mBanner     = mBannerList.get(i);
            ImageView BannerImage       = new ImageView(mContext);
            Picasso.with(mContext)
                    .load(GeneralConstant.DOMAIN_SERVER+"/"+mBanner.getBannerImage())
                    .resize(mBannerWidth, mBannerHeight)
                    .into(BannerImage);

            BannerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mLink  = mBanner.getBannerLink();
                    if(mLink.equals("") || mLink.equals("null") || mLink == null || mLink.contentEquals("null")) {}
                    else {
                        if (Utility.isNetworkConnected()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLink));
                            startActivity(intent);
                        }else {
                            Dialog.InformationDialog(mContext, getResources().getString(R.string.msg_no_internet));
                        }
                    }
                }
            });
            BannerFlipper.addView(BannerImage);
        }
        BannerFlipper.setFlipInterval(2000);
        BannerFlipper.startFlipping();
    }
    private Runnable GetBannerProgress = new Runnable() {
        public void run() {
            onGetBanner();
        }
    };
    private void onGetBanner() {
        final ObjUser mUserinfo = new ObjUser();
        ApiService apiService   = ApiClient.getClient().create(ApiService.class);
        retrofit2.Call<BannerResponse> mRequest  = apiService.getBanner(mUserinfo.UserAuth, mUserinfo.UserID);
        mRequest.enqueue(new retrofit2.Callback<BannerResponse>() {
            @Override
            public void onResponse(retrofit2.Call<BannerResponse> call, retrofit2.Response<BannerResponse> mResponse) {
                BannerResponse Result   = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        if (mBannerList.size() > 0)
                            mBannerList.clear();
                        mBannerList  = Result.getResultData();
                        showServerBanner();
                        break;
                    default:
                        showDefaultFlipper();
                        break;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<BannerResponse> call, Throwable t) {
                showDefaultFlipper();
            }
        });
    }
}