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
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

public class ResetActivity extends AppCompatActivity {

    private EditText mInputEmail;
    private String strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Initialise();
    }

    @Override
    public void onBackPressed() {
        onBacktoLogin.run();
    }

    private void Initialise(){
        mInputEmail             = (EditText) findViewById(R.id.input_email);
        Button btnReset         = (Button)findViewById(R.id.btnReset);
        TextView mLinkLogin     = (TextView) findViewById(R.id.linkLogin);

        mLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBacktoLogin.run();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail     = mInputEmail.getText().toString();

                if (validate())
                    if (Utility.isNetworkConnected())
                        UserTask.onResetPassword(ResetActivity.this, resetSuccess, strEmail);
                    else
                        Dialog.InformationDialog(ResetActivity.this, getResources().getString(R.string.msg_no_internet));
            }
        });
    }

    public boolean validate() {
        boolean valid       = true;
        if (strEmail.isEmpty() || strEmail.length() == 0 ) {
            mInputEmail.setError(getResources().getString(R.string.msg_email_empty));
            valid = false;
        } else {
            if (Utility.EmailValidation(strEmail))
                mInputEmail.setError(null);
            else{
                mInputEmail.setError(getResources().getString(R.string.msg_email_invalid));
                mInputEmail.requestFocus();
                valid = false;
            }
        }
        return valid;
    }

    private Runnable resetSuccess = new Runnable() {
        public void run() {
            Dialog.ActionDialog(ResetActivity.this, getResources().getString(R.string.msg_reset_success), onBacktoLogin);
        }
    };

    private Runnable onBacktoLogin = new Runnable() {
        public void run() {
            Intent intent  = new Intent(ResetActivity.this, LoginActivity.class);
            ActivityTransitionLauncher.with(ResetActivity.this).from(findViewById(R.id.btnReset)).launch(intent);
        }
    };
}
