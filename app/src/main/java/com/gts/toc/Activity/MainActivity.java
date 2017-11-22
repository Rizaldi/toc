package com.gts.toc.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gts.toc.Fragment.DefaultFragment;
import com.gts.toc.Fragment.HistoryFragment;
import com.gts.toc.Fragment.OrderFragment;
import com.gts.toc.Fragment.TermFragment;
import com.gts.toc.R;
import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.object.ObjUser;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHandler mDataBase = new DatabaseHandler();
    private ObjUser mUserinfo = new ObjUser();
    public static Fragment mFragment;
    private Toolbar toolbar;
    public static FragmentManager mFragmentManager;
    private DrawerLayout mDrawer;
    public static NavigationView navigationView;
    public static String mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        InitToolbar();
        initNavigationDrawer();
        mFragmentManager = getSupportFragmentManager();
        Intent mIntent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putString(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_HOME);
        if (mIntent.getStringExtra(GeneralConstant.FRAGMENT_STATE) != null) {
            String mState = mIntent.getStringExtra(GeneralConstant.FRAGMENT_STATE);
            bundle.putString(GeneralConstant.FRAGMENT_STATE, mState);
        }
        if (savedInstanceState == null) {
            if (mIntent.getStringExtra(GeneralConstant.FRAGMENT_STATE) != null) {
                switch (mIntent.getStringExtra(GeneralConstant.FRAGMENT_STATE)) {
                    case GeneralConstant.FRAGMENT_ORDER:
                        mView = GeneralConstant.FRAGMENT_ORDER;
                        mFragment = new OrderFragment();
                        navigationView.setCheckedItem(R.id.nav_order);
                        break;
                    case GeneralConstant.FRAGMENT_HISTORY:
                        mView = GeneralConstant.FRAGMENT_HISTORY;
                        mFragment = new HistoryFragment();
                        navigationView.setCheckedItem(R.id.nav_history);
                        break;
                    default:
                        mView = GeneralConstant.FRAGMENT_HOME;
                        mFragment = new DefaultFragment();
                        navigationView.setCheckedItem(R.id.nav_home);
                        break;
                }
            } else {
                mView = GeneralConstant.FRAGMENT_HOME;
                mFragment = new DefaultFragment();
                navigationView.setCheckedItem(R.id.nav_home);
            }
        } else
            mFragment = getSupportFragmentManager().findFragmentByTag(GeneralConstant.FRAGMENT_TAG);

        if (!mFragment.isAdded()) {
            mFragment.setArguments(bundle);
        }
        mFragmentManager.beginTransaction().replace(R.id.FragmentContent, mFragment, GeneralConstant.FRAGMENT_TAG).commit();
    }

    void InitToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    void initNavigationDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, 0, 0);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView viewUsername = (TextView) view.findViewById(R.id.viewUserName);
        TextView viewEmail = (TextView) view.findViewById(R.id.viewEmail);
        ImageView profileImage = (ImageView) view.findViewById(R.id.profile_image);
        viewUsername.setText(mUserinfo.UserName);
        viewEmail.setText(mUserinfo.Email);
        if (!mUserinfo.Image.contentEquals("") && mUserinfo.Image != null) {
            Picasso.with(MainActivity.this)
                    .load(GeneralConstant.DOMAIN_SERVER + "/" + mUserinfo.Image)
                    .into(profileImage);
        } else {
            Picasso.with(MainActivity.this)
                    .load(R.drawable.ic_profile)
                    .into(profileImage);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);

        } else {
            if ((mView != null) && (mView.contentEquals(GeneralConstant.FRAGMENT_ORDER)
                    || mView.contentEquals(GeneralConstant.FRAGMENT_HISTORY)
                    || mView.contentEquals(GeneralConstant.FRAGMENT_TERM) )) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_HOME);
                ActivityTransitionLauncher.with(MainActivity.this).from(findViewById(R.id.FragmentContent)).launch(intent);
                navigationView.setCheckedItem(R.id.nav_home);
            } else
                Dialog.ConfirmationDialog(MainActivity.this, getResources().getString(R.string.label_exit),
                        getResources().getString(R.string.btn_ok), mCloseApps,
                        getResources().getString(R.string.btn_cancel));
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                if (!mView.contentEquals(GeneralConstant.FRAGMENT_HOME)){
                    mView = GeneralConstant.FRAGMENT_HOME;
                    mFragment = new DefaultFragment();
                }
                item.setChecked(true);
                break;
            case R.id.nav_profile:
                Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                ActivityTransitionLauncher.with(MainActivity.this).from(findViewById(R.id.FragmentContent)).launch(intentProfile);
                break;
            case R.id.nav_changepassword:
                Intent intentReset = new Intent(MainActivity.this, ChangePasswordActivity.class);
                ActivityTransitionLauncher.with(MainActivity.this).from(findViewById(R.id.FragmentContent)).launch(intentReset);
                break;
            case R.id.nav_logout:
                Dialog.ConfirmationDialog(MainActivity.this, getResources().getString(R.string.label_logout),
                        getResources().getString(R.string.btn_yes), mLogOut,
                        getResources().getString(R.string.btn_cancel));
                break;
            case R.id.nav_order:
                if (!mView.contentEquals(GeneralConstant.FRAGMENT_ORDER)){
                    mView = GeneralConstant.FRAGMENT_ORDER;
                    mFragment = new OrderFragment();
                }
                item.setChecked(true);
                break;
            case R.id.nav_history:
                if (!mView.contentEquals(GeneralConstant.FRAGMENT_HISTORY)){
                    mView = GeneralConstant.FRAGMENT_HISTORY;
                    mFragment = new HistoryFragment();
                }
                item.setChecked(true);
                break;
            case R.id.nav_update:
                if (Utility.isNetworkConnected()) {
                    Intent updateIntent = new Intent(Intent.ACTION_VIEW);
                    updateIntent.setData(Uri.parse(GeneralConstant.API_URLUPDATE));
                    startActivity(updateIntent);
                } else
                    Dialog.InformationDialog(MainActivity.this, getResources().getString(R.string.msg_no_internet));
                break;
            case R.id.nav_term:
                mView = GeneralConstant.FRAGMENT_TERM;
                mFragment = new TermFragment();
                item.setChecked(true);
                break;
            case R.id.nav_about:
                try {
                    PackageInfo mAppInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                    String mVersionName = mAppInfo.versionName;
                    int mVersionCode = mAppInfo.versionCode;
                    Dialog.AboutDialog(MainActivity.this, getResources().getString(R.string.app_desc), mVersionName + "." + mVersionCode + " (Beta)");
                } catch (PackageManager.NameNotFoundException e) {
                }
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        mFragmentManager.beginTransaction().replace(R.id.FragmentContent, mFragment).commit();

        return true;
    }

    private Runnable mLogOut = new Runnable() {
        @Override
        public void run() {
            mDataBase.DeleteUser(mUserinfo.UserID);
            Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
            ActivityTransitionLauncher.with(MainActivity.this).from(findViewById(R.id.FragmentContent)).launch(intentLogout);
        }
    };
    public Runnable mCloseApps = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };
}
