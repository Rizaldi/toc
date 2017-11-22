package com.gts.toc.Activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class CreateOrderActivityOK extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private DatabaseHandler mDataBase = new DatabaseHandler();
    private ObjUser mUserinfo   = new ObjUser();
    private Toolbar mToolbar;
    private RadioGroup radioGroupType;
    private RadioButton radioStandard, radioSpecific;
    private List<ObjSpinnerItem> mObjSpinnerCategory    = new ArrayList<ObjSpinnerItem>();
    private List<ObjSpinnerItem> mObjSpinnerType        = new ArrayList<ObjSpinnerItem>();
    private NiceSpinner mSpinnerCategory, mSpinnerType;
    private RelativeLayout mLayoutType, mLayoutDescription;
    private TextView mDescriptionView;
    private TextView mPriceView;
    private EditText mInputAddress;
    private FloatingActionButton mOrderButton;
//    private String strTitle;
    private String strDescription;
    private String strCost;
    private String strAddress;
    private String strPoint;
    private String strType = "0";
//    private EditText mInputTitle;
    private EditText mInputDescription;
    private String orderList;
    private String fragmentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createorderok);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        Intent mIntent  = getIntent();
        strPoint        = mIntent.getStringExtra(GeneralConstant.PARAM_LOCATION);
        strAddress      = mIntent.getStringExtra(GeneralConstant.PARAM_ADDRESS);
        fragmentState   = mIntent.getStringExtra(GeneralConstant.FRAGMENT_STATE);
        Initialise();
        mDescriptionView.setText(getResources().getString(R.string.label_cost));
        mPriceView.setText("RP. "+Utility.doubleToStringNoDecimal(0));
    }
    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(CreateOrderActivityOK.this, MainActivity.class);
        intent.putExtra(GeneralConstant.FRAGMENT_STATE, fragmentState);
        ActivityTransitionLauncher.with(CreateOrderActivityOK.this).from(findViewById(R.id.layoutOrder)).launch(intent);
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
        mLayoutDescription  = (RelativeLayout) findViewById(R.id.layout_descripsi);
        radioGroupType      = (RadioGroup)findViewById(R.id.radioGroupType);
        radioStandard       = (RadioButton)findViewById(R.id.radioStandard);
        radioSpecific       = (RadioButton)findViewById(R.id.radioSpesific);
        mSpinnerCategory    = (NiceSpinner) findViewById(R.id.category_spinner);
        mSpinnerType        = (NiceSpinner) findViewById(R.id.type_spinner);
        mDescriptionView    = (TextView) findViewById(R.id.viewServiceSelect);
        mOrderButton        = (FloatingActionButton) findViewById(R.id.btnOrder);
//        mInputTitle         = (EditText) findViewById(R.id.input_servicetitle);
        mInputDescription   = (EditText) findViewById(R.id.input_Description);
        mInputAddress       = (EditText) findViewById(R.id.input_address);
        mPriceView          = (TextView) findViewById(R.id.viewEstimasiPrice);
        mInputAddress.setText(strAddress);

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
        mSpinnerCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MstType> TypeList  = mDataBase.GetType(mObjSpinnerType.get(mSpinnerType.getSelectedIndex()).getItemid());
                MstType mType           = TypeList.get(0);
                if (strType.contentEquals("0")){
                    mDescriptionView.setText(mType.getDescription());
                }
                mPriceView.setText("RP. "+Utility.doubleToStringNoDecimal(Double.parseDouble(mType.getPrice())));
            }
        });
        mSpinnerType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MstType> TypeList  = mDataBase.GetType(mObjSpinnerType.get(mSpinnerType.getSelectedIndex()).getItemid());
                MstType mType           = TypeList.get(0);
                if (strType.contentEquals("0")){
                    mDescriptionView.setText(mType.getDescription());
                }
                mPriceView.setText("RP. "+Utility.doubleToStringNoDecimal(Double.parseDouble(mType.getPrice())));
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                strTitle        = mInputTitle.getText().toString();
                if (radioGroupType.getCheckedRadioButtonId() == R.id.radioStandard){
                    List<MstType> TypeList  = mDataBase.GetType(mObjSpinnerType.get(mSpinnerType.getSelectedIndex()).getItemid());
                    MstType mType       = TypeList.get(0);
                    strDescription      = mType.getDescription();
                    strCost             = mType.getPrice();
                }else {
                    strDescription      = mInputDescription.getText().toString();
                    strCost             = "";
                }
                if (validate()) {
                    if (Utility.isNetworkConnected())
                        CreateProgress.run();
                    else
                        Dialog.InformationDialog(CreateOrderActivityOK.this, getResources().getString(R.string.msg_no_internet));
                }
            }
        });
    }

    private void changeLayout(int typeSelect){
        switch(typeSelect) {
            case R.id.radioStandard:
                showStandard();
                strType = "0";
                break;
            case R.id.radioSpesific:
                showSpecific();
                strType = "1";
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
        mPriceView.setVisibility(View.VISIBLE);
        mDescriptionView.setText(getResources().getString(R.string.label_cost));
        mPriceView.setText("RP. "+Utility.doubleToStringNoDecimal(0));
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
        if (mLayoutDescription.getVisibility()==View.GONE) {
            mLayoutDescription.setVisibility(View.VISIBLE);
            final int widthSpec     = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            final int heightSpec    = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mLayoutDescription.measure(widthSpec, heightSpec);
            ValueAnimator mAnimator = slideAnimator(0, mLayoutDescription.getMeasuredHeight(), mLayoutDescription);
            mAnimator.start();
        }
        mDescriptionView.setText(getResources().getString(R.string.label_estimasi_spesific));
        mPriceView.setVisibility(View.GONE);
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
        if (strType.contentEquals("0")){
            mDescriptionView.setText(mType.getDescription());
        }
        mPriceView.setText("RP. "+Utility.doubleToStringNoDecimal(Double.parseDouble(mType.getPrice())));
    }

    public boolean validate() {
        boolean valid       = true;
//        if (strTitle.isEmpty() || strTitle.length() == 0 ) {
//            mInputTitle.setError(getResources().getString(R.string.msg_title_empty));
//            valid = false;
//        }else
//            mInputTitle.setError(null);
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
            OrderTask.onCreateOrder(CreateOrderActivityOK.this,
                    null,
                    CreateFailed,
                    "",
                    strDescription,
                    strCost,
                    strAddress,
                    strPoint,
                    strType);
        }
    };
//    private Runnable CreateSuccess = new Runnable() {
//        public void run() {
//            if (Utility.isNightTime()){
//                Dialog.ActionDialog(CreateOrderActivityOK.this, getResources().getString(R.string.msg_order_night), onGotoOrderList);
//            }else{
//                Dialog.ActionDialog(CreateOrderActivityOK.this, getResources().getString(R.string.msg_order_success), onGotoOrderList);
//            }
//        }
//    };
    private Runnable  CreateFailed = new Runnable() {
        public void run() {
            UserTask.onGetAuth(CreateProgress, mUserinfo.Email);
        }
    };
    private Runnable onGotoOrderList = new Runnable() {
        public void run() {
            Intent intent  = new Intent(CreateOrderActivityOK.this, MainActivity.class);
            intent.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
            ActivityTransitionLauncher.with(CreateOrderActivityOK.this).from(findViewById(R.id.layoutOrder)).launch(intent);
        }
    };

}
