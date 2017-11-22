package com.gts.toc.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gts.toc.R;
import com.gts.toc.object.ObjUser;
import com.gts.toc.rest.UserTask;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

public class ProfileActivity extends AppCompatActivity {

    private ObjUser mUserinfo   = new ObjUser();
    private Toolbar mToolbar;
    private Button btnUpdate;
    private TextView mViewemail;
    private EditText mInputUserName;
    private EditText mInputPhone;
    private EditText mInputAddress;
    private String strName;
    private String strPhone;
    private String strAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Initialise();
    }

    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(ProfileActivity.this, MainActivity.class);
        ActivityTransitionLauncher.with(ProfileActivity.this).from(findViewById(R.id.profile_layout)).launch(intent);
    }

    private void Initialise(){
        mToolbar    = (Toolbar) findViewById(R.id.toolbarProfile);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.nav_profile));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mViewemail      = (TextView) findViewById(R.id.viewUserEmail);
        mInputUserName  = (EditText) findViewById(R.id.viewUserName);
        mInputPhone     = (EditText) findViewById(R.id.viewPhone);
        mInputAddress   = (EditText) findViewById(R.id.viewAddress);
        mInputUserName.setText(mUserinfo.UserName);
        mViewemail.setText(mUserinfo.Email);
        mInputPhone.setText(mUserinfo.Phone);
        mInputAddress.setText(mUserinfo.Address);

        btnUpdate    = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName     = mInputUserName.getText().toString();
                strPhone    = mInputPhone.getText().toString();
                strAddress  = mInputAddress.getText().toString();
                if (validate())
                    if (Utility.isNetworkConnected())
                        UpdateProgress.run();
                    else
                        Dialog.InformationDialog(ProfileActivity.this, getResources().getString(R.string.msg_no_internet));
            }
        });
    }

    public boolean validate() {
        boolean valid       = true;
        if (strName.isEmpty() || strName.length() == 0 ) {
            mInputUserName.setError(getResources().getString(R.string.msg_username_empty));
            valid = false;
        }else{
            mInputPhone.setError(null);
        }

        if (strPhone.isEmpty() || strPhone.length() == 0 ) {
            mInputPhone.setError(getResources().getString(R.string.msg_phone_empty));
            valid = false;
        }else {
            if (strPhone.length() < 10){
                mInputPhone.setError(getResources().getString(R.string.msg_phone_invalid));
                valid = false;
            }else{
                mInputPhone.setError(null);
            }
        }
        return valid;
    }

    private Runnable UpdateProgress = new Runnable() {
        public void run() {
            UserTask.onUpdateProfile(ProfileActivity.this,
                    UpdateSuccess,
                    UpdateFailed,
                    strName,
                    strPhone,
                    strAddress);
        }
    };
    private Runnable UpdateSuccess = new Runnable() {
        public void run() {
            mUserinfo   = new ObjUser();
            mInputUserName.setText(mUserinfo.UserName);
            mViewemail.setText(mUserinfo.Email);
            mInputPhone.setText(mUserinfo.Phone);
            mInputAddress.setText(mUserinfo.Address);
            Dialog.InformationDialog(ProfileActivity.this, getResources().getString(R.string.msg_update_success));

//            Toast.makeText(ProfileActivity.this, getResources().getString(R.string.msg_update_success), Toast.LENGTH_LONG).show();
//            Intent intent  = new Intent(ProfileActivity.this, ProfileActivity.class);
//            ActivityTransitionLauncher.with(ProfileActivity.this).from(findViewById(R.id.btnUpdate)).launch(intent);
        }
    };
    private Runnable  UpdateFailed = new Runnable() {
        public void run() {
            UserTask.onGetAuth(UpdateProgress, mUserinfo.Email);
        }
    };
}