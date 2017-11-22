package com.gts.toc.Activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.gts.toc.R;
import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.model.MstCategory;
import com.gts.toc.model.MstType;
import com.gts.toc.object.ObjSpinnerItem;
import com.gts.toc.object.ObjUser;
import com.gts.toc.rest.OrderTask;
import com.gts.toc.rest.UserTask;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.gts.toc.view.spinner.NiceSpinner;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateOrderActivityNew extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private DatabaseHandler mDataBase = new DatabaseHandler();
    private ObjUser mUserinfo   = new ObjUser();
    private Toolbar mToolbar;
    private RadioGroup radioGroupType;
    private RadioButton radioStandard, radioSpecific;
    private List<ObjSpinnerItem> mObjSpinnerCategory    = new ArrayList<ObjSpinnerItem>();
    private List<ObjSpinnerItem> mObjSpinnerType        = new ArrayList<ObjSpinnerItem>();
    private NiceSpinner mSpinnerCategory, mSpinnerType;
    private RelativeLayout mLayoutType, mLayoutEstimasi, mLayoutDescription;
    private TextView mDescriptionView;
    private TextView mPriceView;
    private EditText mInputAddress;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static String GPS_KEY   = "gps";
    boolean mChangeLocation         = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FloatingActionButton mOrderButton;
    private UiSettings mUiSettings;
    private String mProvider;
    private String strTitle;
    private String strDescription;
    private String strCost;
    private String strAddress;
    private String strPoint;
    private EditText mInputTitle;
    private EditText mInputDescription;
    private TextView mInputCost;
    private String orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createordernew);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        mProvider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        Intent mIntent  = getIntent();
        if (mIntent.getStringExtra(GeneralConstant.PARAM_ORDERLIST) != null)
            orderList       = mIntent.getStringExtra(GeneralConstant.PARAM_ORDERLIST);
        else
            orderList  =  GeneralConstant.ORDER_NOTEMPTY;

        mInitMaps();
        Initialise();
    }
    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(CreateOrderActivityNew.this, MainActivity.class);
        switch (orderList) {
            case GeneralConstant.ORDER_EMPTY:
                intent.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_HOME);
                break;
            default:
                intent.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
                break;
        }
        ActivityTransitionLauncher.with(CreateOrderActivityNew.this).from(findViewById(R.id.layoutOrder)).launch(intent);
    }
    private void Initialise(){
        mToolbar    = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.label_order));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLayoutType         = (RelativeLayout) findViewById(R.id.layoutType);
        mLayoutEstimasi     = (RelativeLayout) findViewById(R.id.layoutEstimasi);
        mLayoutDescription  = (RelativeLayout) findViewById(R.id.layout_descripsi);
        radioGroupType      = (RadioGroup)findViewById(R.id.radioGroupType);
        radioStandard       = (RadioButton)findViewById(R.id.radioStandard);
        radioSpecific       = (RadioButton)findViewById(R.id.radioSpesific);
        mSpinnerCategory    = (NiceSpinner) findViewById(R.id.category_spinner);
        mSpinnerType        = (NiceSpinner) findViewById(R.id.type_spinner);
        mDescriptionView    = (TextView) findViewById(R.id.viewServiceSelect);
        mPriceView          = (TextView) findViewById(R.id.viewEstimasiPrice);
        mOrderButton        = (FloatingActionButton) findViewById(R.id.btnOrder);
        mInputTitle         = (EditText) findViewById(R.id.input_servicetitle);
        mInputDescription   = (EditText) findViewById(R.id.input_Description);
        mInputCost          = (TextView) findViewById(R.id.viewEstimasiPrice);
        mInputAddress       = (EditText) findViewById(R.id.input_address);

        changeLayout(R.id.radioStandard);
        radioGroupType.check(R.id.radioStandard);
        radioStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLayout(R.id.radioStandard);
                radioGroupType.check(R.id.radioStandard);
            }
        });
        radioSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLayout(R.id.radioSpesific);
                radioGroupType.check(R.id.radioSpesific);
            }
        });

        SpinnerCategoryInitialize();
        SpinnerTypeInitialize("");
        mSpinnerCategory.setSelectedIndex(0);
        setSelectedCategory(0);
        mSpinnerCategory.setOnItemSelectedListener(this);
        mSpinnerType.setSelectedIndex(0);
        setSelectedType(0);
        mSpinnerType.setOnItemSelectedListener(this);

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Location location = new Location("Location");
                location.setLatitude(cameraPosition.target.latitude);
                location.setLongitude(cameraPosition.target.longitude);
                handleNewLocation(location);
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng) {
                Location location = new Location("Location");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                handleNewLocation(location);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
            }
        });
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTitle        = mInputTitle.getText().toString();
                if (radioGroupType.getCheckedRadioButtonId() == R.id.radioStandard){
                    List<MstType> TypeList  = mDataBase.GetType(mObjSpinnerType.get(mSpinnerType.getSelectedIndex()).getItemid());
                    MstType mType       = TypeList.get(0);
                    strDescription      = mType.getDescription();
                    strCost             = mType.getPrice();
                }else {
                    strDescription      = mInputDescription.getText().toString();
                    strCost             = "";
                }
                strAddress      = mInputAddress.getText().toString();
                strPoint        = mMap.getCameraPosition().target.latitude + "," + mMap.getCameraPosition().target.longitude;

                if (validate()) {
                    if (Utility.isNetworkConnected())
                        CreateProgress.run();
                    else
                        Dialog.InformationDialog(CreateOrderActivityNew.this, getResources().getString(R.string.msg_no_internet));
                }
            }
        });
    }

    private void changeLayout(int typeSelect){
        switch(typeSelect) {
            case R.id.radioStandard:
                showStandard();
                break;
            case R.id.radioSpesific:
                showSpecific();
                break;
        }
    }
    private void showStandard(){
        if (mLayoutType.getVisibility()==View.GONE) {
            mLayoutType.setVisibility(View.VISIBLE);
            final int widthSpec     = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            final int heightSpec    = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mLayoutType.measure(widthSpec, heightSpec);
            ValueAnimator mAnimator = slideAnimator(0, mLayoutType.getMeasuredHeight(), mLayoutType);
            mAnimator.start();
        }
        if (mLayoutEstimasi.getVisibility()==View.GONE) {
            mLayoutEstimasi.setVisibility(View.VISIBLE);
            final int widthSpec     = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            final int heightSpec    = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mLayoutEstimasi.measure(widthSpec, heightSpec);
            ValueAnimator mAnimator = slideAnimator(0, mLayoutEstimasi.getMeasuredHeight(), mLayoutEstimasi);
            mAnimator.start();
        }
        if (mLayoutDescription.getVisibility()==View.VISIBLE) {
            ValueAnimator mAnimator = slideAnimator(mLayoutDescription.getHeight(), 0, mLayoutDescription);
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationEnd(Animator animator) {
                    mLayoutDescription.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            mAnimator.start();
        }
    }
    private void showSpecific(){
        if (mLayoutType.getVisibility()==View.VISIBLE) {
            ValueAnimator mAnimator = slideAnimator(mLayoutType.getHeight(), 0, mLayoutType);
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationEnd(Animator animator) {
                    mLayoutType.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            mAnimator.start();
        }
        if (mLayoutEstimasi.getVisibility()==View.VISIBLE) {
            ValueAnimator mAnimator = slideAnimator(mLayoutEstimasi.getHeight(), 0, mLayoutEstimasi);
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationEnd(Animator animator) {
                    mLayoutEstimasi.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            mAnimator.start();
        }
        if (mLayoutDescription.getVisibility()==View.GONE) {
            mLayoutDescription.setVisibility(View.VISIBLE);
            final int widthSpec     = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            final int heightSpec    = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mLayoutDescription.measure(widthSpec, heightSpec);
            ValueAnimator mAnimator = slideAnimator(0, mLayoutDescription.getMeasuredHeight(), mLayoutDescription);
            mAnimator.start();
        }
    }
    private ValueAnimator slideAnimator(int start, int end, final RelativeLayout mLayout) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mLayout.getLayoutParams();
                layoutParams.height = value;
                mLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void SpinnerCategoryInitialize(){
        List<MstCategory> ListCategory = mDataBase.GetAllCategory();
        if (mObjSpinnerCategory.size() > 0)
            mObjSpinnerCategory.clear();
        int Count  = ListCategory.size();
        if (Count > 0){
            for (int i=0; i<Count; i++){
                MstCategory ObjCategory     = ListCategory.get(i);
                ObjSpinnerItem NewCategory  = new ObjSpinnerItem();
                NewCategory.setItemid(ObjCategory.getCatID());
                NewCategory.setItemName(ObjCategory.getDescription());
                mObjSpinnerCategory.add(NewCategory);
            }
            mSpinnerCategory.attachDataSource(mObjSpinnerCategory);
        }
    }
    private void SpinnerTypeInitialize(String CatCode){
        List<MstType> ListType  = new ArrayList<MstType>();
        if (mObjSpinnerType.size() > 0)
            mObjSpinnerType.clear();
        if (CatCode.contentEquals(""))
            ListType  = mDataBase.GetAllType();
        else
            ListType  = mDataBase.GetAllTypeBycatCode(CatCode);
        int Count               = ListType.size();
        if (Count > 0){
            for (int i=0; i<Count; i++){
                MstType ObjType         = ListType.get(i);
                ObjSpinnerItem NewType  = new ObjSpinnerItem();
                NewType.setItemid(ObjType.getTypeID());
                NewType.setItemName(ObjType.getDescription());
                mObjSpinnerType.add(NewType);
            }
            mSpinnerType.attachDataSource(mObjSpinnerType);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        View spinner = (View) parent;
        switch (spinner.getId()) {
            case R.id.category_spinner:
                setSelectedCategory(position);
                break;
            case R.id.type_spinner:
                setSelectedType(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void setSelectedCategory(int mPos){
        SpinnerTypeInitialize(mObjSpinnerCategory.get(mPos).getItemid());
        mSpinnerType.setSelectedIndex(0);
        setSelectedType(0);
    }
    private void setSelectedType(int mPos){
        List<MstType> TypeList  = mDataBase.GetType(mObjSpinnerType.get(mPos).getItemid());
        MstType mType           = TypeList.get(0);
        mDescriptionView.setText(mType.getDescription());
        mPriceView.setText("RP. "+Utility.doubleToStringNoDecimal(Double.parseDouble(mType.getPrice())));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChangeLocation = true;
        mInitMaps();
        mProvider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(mProvider.contains(GPS_KEY)) {
            mGetLocation();
        }else
            Dialog.ConfirmationDialog(
                    CreateOrderActivityNew.this,
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
            MapFragment mMapFragment    = (MapFragment) getFragmentManager().findFragmentById(R.id.mapOrder);
            mMap                        = mMapFragment.getMap();
            mUiSettings = mMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(true);
            mUiSettings.setCompassEnabled(true);
            mMap.setMyLocationEnabled(true);
            if (mMap != null)
                setUpMap();

            View mapView    = mMapFragment.getView();
            View myLocation = mapView.findViewWithTag("GoogleMapMyLocationButton");
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80,80); // size of button in dp
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.setMargins(25, 0, 0, 25);
            myLocation.setLayoutParams(params);
        }
    }
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Default Location"));
    }
    private void handleNewLocation(Location location) {
        double currentLatitude      = location.getLatitude();
        double currentLongitude     = location.getLongitude();
        SetAddress(currentLatitude, currentLongitude);
        LatLng latLng               = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions options       = new MarkerOptions().position(latLng).title("My Location");
        mMap.clear();
        mMap.addMarker(options);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            mProvider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
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
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {}
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        if (mChangeLocation) {
            double currentLatitude      = location.getLatitude();
            double currentLongitude     = location.getLongitude();
            SetAddress(currentLatitude, currentLongitude);
            LatLng latLng               = new LatLng(currentLatitude, currentLongitude);
            MarkerOptions options       = new MarkerOptions().position(latLng).title("");
            mMap.clear();
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            mChangeLocation = false;
        }
    }
    private void SetAddress(Double Latitude, Double Longitude){
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
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

    public boolean validate() {
        boolean valid       = true;
        if (strTitle.isEmpty() || strTitle.length() == 0 ) {
            mInputTitle.setError(getResources().getString(R.string.msg_title_empty));
            valid = false;
        }else
            mInputTitle.setError(null);
        if (strDescription.isEmpty() || strDescription.length() == 0 ) {
            mInputDescription.setError(getResources().getString(R.string.msg_description_empty));
            valid = false;
        }else
            mInputDescription.setError(null);
        if (strAddress.isEmpty() || strAddress.length() == 0 ) {
            mInputAddress.setError(getResources().getString(R.string.msg_address_empty));
            valid = false;
        }else
            mInputAddress.setError(null);
        return valid;
    }

    private Runnable CreateProgress = new Runnable() {
        public void run() {
            OrderTask.onCreateOrder(CreateOrderActivityNew.this,
                    CreateSuccess,
                    CreateFailed,
                    strTitle,
                    strDescription,
                    strCost,
                    strAddress,
                    strPoint,
                    "");
        }
    };
    private Runnable CreateSuccess = new Runnable() {
        public void run() {
            Toast.makeText(CreateOrderActivityNew.this, getResources().getString(R.string.msg_order_success), Toast.LENGTH_LONG).show();
            Intent intent  = new Intent(CreateOrderActivityNew.this, MainActivity.class);
            intent.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
            ActivityTransitionLauncher.with(CreateOrderActivityNew.this).from(findViewById(R.id.layoutOrder)).launch(intent);
        }
    };
    private Runnable  CreateFailed = new Runnable() {
        public void run() {
            UserTask.onGetAuth(CreateProgress, mUserinfo.Email);
        }
    };
}
