package com.gts.toc.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gts.toc.R;
import com.gts.toc.rest.OrderTask;
import com.gts.toc.rest.UserTask;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

public class LoginActivity extends AppCompatActivity {

    private EditText mInputUser;
    private EditText mInputPassword;
    private Button btnLogin;
    private TextView mLinkRegister;
    private TextView mLinkReset;
    private String strUser;
    private String strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Initialise();
    }

    @Override
    public void onBackPressed() {
        Dialog.ConfirmationDialog(LoginActivity.this, getResources().getString(R.string.label_exit),
                getResources().getString(R.string.btn_ok), mCloseApps,
                getResources().getString(R.string.btn_cancel));
    }

    private void Initialise(){
        mInputUser      = (EditText) findViewById(R.id.input_email);
        mInputPassword  = (EditText) findViewById(R.id.input_password);
        btnLogin        = (Button)findViewById(R.id.btnLogin);
        mLinkRegister   = (TextView) findViewById(R.id.linkRegister);
        mLinkReset      = (TextView) findViewById(R.id.linkReset);

        mLinkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                ActivityTransitionLauncher.with(LoginActivity.this).from(findViewById(R.id.linkRegister)).launch(intent);
            }
        });
        mLinkReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetActivity.class);
                ActivityTransitionLauncher.with(LoginActivity.this).from(findViewById(R.id.linkReset)).launch(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strUser     = mInputUser.getText().toString();
                strPassword = mInputPassword.getText().toString();

                if (validate())
                    if (Utility.isNetworkConnected()) {
                        UserTask.onLogin(LoginActivity.this,
                                SignInSuccess,
                                SignInFailed,
                                strUser,
                                strPassword);
                    }else
                        Dialog.InformationDialog(LoginActivity.this, getResources().getString(R.string.msg_no_internet));
            }
        });
    }

    public boolean validate() {
        boolean valid       = true;
        if (strUser.isEmpty() || strUser.length() == 0 ) {
            mInputUser.setError(getResources().getString(R.string.msg_email_empty));
            valid = false;
        } else {
            if (Utility.EmailValidation(strUser))
                mInputUser.setError(null);
            else{
                mInputUser.setError(getResources().getString(R.string.msg_email_invalid));
                mInputUser.requestFocus();
                valid = false;
            }
        }

        if (strPassword.isEmpty() || strPassword.length() == 0 ) {
            mInputPassword.setError(getResources().getString(R.string.msg_password_empty));
            valid = false;
        } else
            mInputPassword.setError(null);
        return valid;
    }

    private Runnable SignInSuccess = new Runnable() {
        public void run() {
            RequestCategoryProgress.run();
            RequestTypeProgress.run();
            RequestMiscProgress.run();
            RequestBankProgress.run();
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.msg_login_success), Toast.LENGTH_LONG).show();
            Intent intent  = new Intent(LoginActivity.this, MainActivity.class);
            ActivityTransitionLauncher.with(LoginActivity.this).from(findViewById(R.id.btnLogin)).launch(intent);
        }
    };
    private Runnable SignInFailed = new Runnable() {
        public void run() {
            Dialog.InformationDialog(LoginActivity.this, getResources().getString(R.string.msg_login_failed));
        }
    };

    public Runnable RequestCategoryProgress = new Runnable() {
        public void run() {
            OrderTask.onGetParameter(null, null, GeneralConstant.PARAM_CATEGORY);
        }
    };
    private Runnable RequestTypeProgress = new Runnable() {
        public void run() {
            OrderTask.onGetParameter(null, null, GeneralConstant.PARAM_TYPE);
        }
    };
    private Runnable RequestMiscProgress = new Runnable() {
        public void run() {
            OrderTask.onGetMiscParams(null, null);
        }
    };
    private Runnable RequestBankProgress = new Runnable() {
        public void run() {
            OrderTask.onGetBank(null, null);
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
