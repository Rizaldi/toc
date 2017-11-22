package com.gts.toc.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gts.toc.R;
import com.gts.toc.rest.UserTask;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

public class RegisterActivity extends AppCompatActivity {

    private EditText mInputUsername;
    private EditText mInputEmail;
    private EditText mInputPhone;
    private EditText mInputPassword;
    private Button btnRegister;
    private TextView mLinkLogin;
    private String strUsername;
    private String strEmail;
    private String strPhone;
    private String strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Initialise();
    }

    @Override
    public void onBackPressed() {
        onLoginClick.run();
    }

    private void Initialise(){
        mInputUsername  = (EditText) findViewById(R.id.input_username);
        mInputEmail     = (EditText) findViewById(R.id.input_email);
        mInputPhone     = (EditText) findViewById(R.id.input_phone);
        mInputPassword  = (EditText) findViewById(R.id.input_password);
        btnRegister     = (Button)findViewById(R.id.btnRegister);
        mLinkLogin      = (TextView) findViewById(R.id.linkLogin);
        mLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick.run();
            }
        });
//        mInputUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    strUsername     = mInputUsername.getText().toString();
//                    if (strUsername.length() > 0){
//                        if (Utility.isNetworkConnected())
//                            UserTask.onUserChecking(
//                                    Available(GeneralConstant.CHECKING_USER),
//                                    NotAvailable(GeneralConstant.CHECKING_USER),
//                                    GeneralConstant.CHECKING_USER, strUsername);
//                    }
//                }
//            }
//        });
        mInputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strEmail     = mInputEmail.getText().toString();
                    if (strEmail.length() > 0){
                        if (Utility.EmailValidation(strEmail)) {
                            if (Utility.isNetworkConnected())
                                UserTask.onUserChecking(
                                        Available(GeneralConstant.CHECKING_EMAIL),
                                        NotAvailable(GeneralConstant.CHECKING_EMAIL),
                                        GeneralConstant.CHECKING_EMAIL, strEmail);
                        }else
                            mInputEmail.setError(getResources().getString(R.string.msg_email_invalid));
                    }
                }
            }
        });
        mInputPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strPhone     = mInputPhone.getText().toString();
                    if ( (strPhone.length() > 0) && (strPhone.length() < 10))
                        mInputPhone.setError(getResources().getString(R.string.msg_phone_invalid));
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strUsername     = mInputUsername.getText().toString();
                strEmail        = mInputEmail.getText().toString();
                strPhone        = mInputPhone.getText().toString();
                strPassword     = mInputPassword.getText().toString();

                if (validate())
                    if (Utility.isNetworkConnected()) {
                        UserTask.onRegistration(RegisterActivity.this,
                                RegistrationSuccess,
                                RegistrationFailed,
                                strUsername,
                                strEmail,
                                strPhone,
                                strPassword);
                    }else
                        Dialog.InformationDialog(RegisterActivity.this, getResources().getString(R.string.msg_no_internet));
            }
        });
    }

    private Runnable Available(final String State){
        Runnable aRunnable = new Runnable(){
            public void run(){
                switch (State) {
                    case GeneralConstant.CHECKING_USER:
                        mInputUsername.setError(null);
                        break;
                    case GeneralConstant.CHECKING_EMAIL:
                        mInputEmail.setError(null);
                        break;
                }
            }
        };
        return aRunnable;
    }
    private Runnable NotAvailable(final String State){
        Runnable aRunnable = new Runnable(){
            public void run(){
                switch (State) {
                    case GeneralConstant.CHECKING_USER:
                        mInputUsername.setError(getResources().getString(R.string.msg_user_notavailable));
                        break;
                    case GeneralConstant.CHECKING_EMAIL:
                        mInputEmail.setError(getResources().getString(R.string.msg_email_notavailable));
                        break;
                }
            }
        };
        return aRunnable;
    }

    public boolean validate() {
        boolean valid       = true;
        if (strUsername.isEmpty() || strUsername.length() == 0 ) {
            mInputUsername.setError(getResources().getString(R.string.msg_username_empty));
            valid = false;
        } else
            mInputUsername.setError(null);

        if (strEmail.isEmpty() || strEmail.length() == 0 ) {
            mInputEmail.setError(getResources().getString(R.string.msg_email_empty));
            valid = false;
        }else {
            if (Utility.EmailValidation(strEmail))
                mInputEmail.setError(null);
            else{
                mInputEmail.setError(getResources().getString(R.string.msg_email_invalid));
                mInputEmail.requestFocus();
                valid = false;
            }
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

        if (strPassword.isEmpty() || strPassword.length() == 0 ) {
            mInputPassword.setError(getResources().getString(R.string.msg_password_empty));
            valid = false;
        } else
            mInputPassword.setError(null);
        return valid;
    }

    private Runnable RegistrationSuccess = new Runnable() {
        public void run() {
            Dialog.ActionDialog(RegisterActivity.this, getResources().getString(R.string.msg_registrasi_success), onLoginClick);

//            onLoginClick.run();
//            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.msg_registrasi_success), Toast.LENGTH_LONG).show();
        }
    };
    private Runnable RegistrationFailed = new Runnable() {
        public void run() {
            Dialog.InformationDialog(RegisterActivity.this, getResources().getString(R.string.msg_registrasi_failed));
        }
    };
    private Runnable onLoginClick = new Runnable() {
        public void run() {
            Intent intent  = new Intent(RegisterActivity.this, LoginActivity.class);
            ActivityTransitionLauncher.with(RegisterActivity.this).from(findViewById(R.id.btnRegister)).launch(intent);
        }
    };
}
