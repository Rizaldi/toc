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
import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.object.ObjUser;
import com.gts.toc.rest.UserTask;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

public class ChangePasswordActivity extends AppCompatActivity {

    private DatabaseHandler mDataBase = new DatabaseHandler();
    private ObjUser mUserinfo   = new ObjUser();
    private EditText mInputOldEmail;
    private EditText mInputNewPassword;
    private EditText mRetypePassword;
    private TextView linkLogin;
    private Button btnChange;
    private String strOldPassword;
    private String strNewPassword;
    private String strRePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Initialise();
    }

    @Override
    public void onBackPressed() {
        BaktoHome();
    }
    private void BaktoHome(){
        Intent intent  = new Intent(ChangePasswordActivity.this, MainActivity.class);
        ActivityTransitionLauncher.with(ChangePasswordActivity.this).from(findViewById(R.id.btnChange)).launch(intent);
    }
    private void Initialise(){
        mInputOldEmail      = (EditText) findViewById(R.id.input_old_password);
        mInputNewPassword   = (EditText) findViewById(R.id.input_new_password);
        mRetypePassword     = (EditText) findViewById(R.id.retype_password);
        btnChange           = (Button)findViewById(R.id.btnChange);
        linkLogin           = (TextView) findViewById(R.id.linkLogin);
        linkLogin.setVisibility(View.GONE);
        linkLogin.setEnabled(false);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strOldPassword  = mInputOldEmail.getText().toString();
                strNewPassword  = mInputNewPassword.getText().toString();
                strRePassword   = mRetypePassword.getText().toString();
                if (validate())
                    if (validate())
                        if (Utility.isNetworkConnected())
                            ChangeProgress.run();
                        else
                            Dialog.InformationDialog(ChangePasswordActivity.this, getResources().getString(R.string.msg_no_internet));
            }
        });
    }

    public boolean validate() {
        boolean valid       = true;
        if (strOldPassword.isEmpty() || strOldPassword.length() == 0 ) {
            mInputOldEmail.setError(getResources().getString(R.string.msg_oldpassword_empty));
            valid = false;
        } else
            mInputOldEmail.setError(null);

        if (strNewPassword.isEmpty() || strNewPassword.length() == 0 ) {
            mInputNewPassword.setError(getResources().getString(R.string.msg_newpassword_empty));
            valid = false;
        } else
            mInputNewPassword.setError(null);

        if (strRePassword.isEmpty() || strRePassword.length() == 0 ) {
            mRetypePassword.setError(getResources().getString(R.string.msg_repassword_empty));
            valid = false;
        } else
            mRetypePassword.setError(null);
        if ( !strNewPassword.contentEquals(strRePassword) ) {
            mRetypePassword.setError(getResources().getString(R.string.msg_newpassword_invalid));
            valid = false;
        }
        return valid;
    }

    private Runnable ChangeProgress = new Runnable() {
        public void run() {
            UserTask.onChangePassword(ChangePasswordActivity.this,
                    ChangeSuccess,
                    ChangeFailed,
                    strOldPassword,
                    strNewPassword);
        }
    };
    private Runnable ChangeSuccess = new Runnable() {
        public void run() {
            mDataBase.DeleteUser(mUserinfo.UserID);
            Dialog.ActionDialog(ChangePasswordActivity.this, getResources().getString(R.string.msg_change_password_success), GotoLogin);
        }
    };
    private Runnable ChangeFailed = new Runnable() {
        public void run() {
            UserTask.onGetAuth(ChangeProgress, mUserinfo.Email);
        }
    };
    private Runnable GotoLogin = new Runnable() {
        public void run() {
            Intent intent  = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            ActivityTransitionLauncher.with(ChangePasswordActivity.this).from(findViewById(R.id.btnChange)).launch(intent);
        }
    };
}
