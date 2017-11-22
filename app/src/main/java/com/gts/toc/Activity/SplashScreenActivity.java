package com.gts.toc.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.gts.toc.R;
import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.model.MstUser;
import com.gts.toc.rest.OrderTask;
import com.gts.toc.rest.UserTask;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.tutorial.TutorialItem;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends Activity {

    private static final int REQUEST_CODE   = 1234;
    private DatabaseHandler mDataBase       = new DatabaseHandler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_splashscreen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(1000);
                } catch (Exception e) {}
                finally {
                    List<MstUser> dataUser  = mDataBase.GetUser();
                    if (dataUser.size()>0){
                        if (Utility.isNetworkConnected()) {
                            UserTask.onGetAuth(GotoDashboard, dataUser.get(0).getEmail());
                            RequestCategoryProgress.run();
                            RequestTypeProgress.run();
                            RequestMiscProgress.run();
                            RequestBankProgress.run();
                        }else
                            GotoDashboard.run();
                    }else{
                        loadTutorial();
                    }
                }
            }
        };
        welcomeThread.start();
    }

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
    private Runnable GotoDashboard = new Runnable() {
        public void run() {
            Intent intent   = new Intent(SplashScreenActivity.this, MainActivity.class);
            ActivityTransitionLauncher.with(SplashScreenActivity.this).from(findViewById(R.id.splashScreen)).launch(intent);
        }
    };
    private Runnable RequestBankProgress = new Runnable() {
        public void run() {
            OrderTask.onGetBank(null, null);
        }
    };

    public void loadTutorial() {
        Intent mainAct = new Intent(this, MaterialTutorialActivity.class);
        mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems(this));
        startActivityForResult(mainAct, REQUEST_CODE);
    }

    private ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem tutorialItem1 = new TutorialItem(R.string.slide_1, R.string.slide_1_subtitle,
                R.color.slide_1, R.drawable.tut_page_1_background);

        TutorialItem tutorialItem2 = new TutorialItem(R.string.slide_2, R.string.slide_2_subtitle,
                R.color.slide_2,  R.drawable.tut_page_2_background);

        TutorialItem tutorialItem3 = new TutorialItem(R.string.slide_3, R.string.slide_3_subtitle,
                R.color.slide_3, R.drawable.tut_page_3_background);

        TutorialItem tutorialItem4 = new TutorialItem(R.string.slide_4, R.string.slide_4_subtitle,
                R.color.slide_4, R.drawable.tut_page_4_background);

        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        tutorialItems.add(tutorialItem4);

        return tutorialItems;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            Intent intent   = new Intent(SplashScreenActivity.this, LoginActivity.class);
            ActivityTransitionLauncher.with(SplashScreenActivity.this).from(findViewById(R.id.splashScreen)).launch(intent);
        }
    }
}