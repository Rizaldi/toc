package com.gts.toc.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gts.toc.Adapter.OrderDetailAdapter;
import com.gts.toc.GlobalApplication;
import com.gts.toc.R;
import com.gts.toc.object.ObjOrder;
import com.gts.toc.object.ObjOrderDetail;
import com.gts.toc.object.ObjUser;
import com.gts.toc.response.OrderDetailResponse;
import com.gts.toc.rest.ApiClient;
import com.gts.toc.rest.ApiService;
import com.gts.toc.rest.OrderTask;
import com.gts.toc.rest.UserTask;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private ImageView mTechImage;
    private TextView mViewTechName, mViewOrder, mViewDate, mViewDesc;
    private RecyclerView mRecyclerView;
    private List<ObjOrderDetail> mOrderLogList = new ArrayList<ObjOrderDetail>();
    private OrderDetailAdapter mAdapter;
    private Toolbar mToolbar;
    private String mState, orderID;
    private TextView mViewMessage;
    private ProgressBar mProgressBar;
    private RelativeLayout mLayoutProgress;
    private LinearLayout mLayoutDetail;
    private TextView mBtnAction;
    private ObjOrder mOrder;
    private SwipeRefreshLayout RefreshLayout;
    private boolean isSwipe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        Intent mIntent  = getIntent();
        mState          = mIntent.getStringExtra(GeneralConstant.FRAGMENT_STATE);
        orderID         = mIntent.getStringExtra(GeneralConstant.PARAM_ORDERID);
        Initialise();
        initSwipeRefresh();
    }

    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(OrderDetailActivity.this, MainActivity.class);
        intent.putExtra(GeneralConstant.FRAGMENT_STATE, mState);
        ActivityTransitionLauncher.with(OrderDetailActivity.this).from(findViewById(R.id.orderdetail_recycler)).launch(intent);
    }

    private void Initialise(){
        mToolbar    = (Toolbar) findViewById(R.id.toolbarDetail);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBtnAction      = (TextView) findViewById(R.id.buttonAction);
        mBtnAction.setVisibility(View.GONE);
        mBtnAction.setEnabled(false);
        mTechImage      = (ImageView) findViewById(R.id.tech_image);
        mViewTechName   = (TextView) findViewById(R.id.viewTechName);
        mLayoutDetail   = (LinearLayout) findViewById(R.id.layout_order_detail);
        mViewMessage    = (TextView) findViewById(R.id.view_message);
        mProgressBar    = (ProgressBar) findViewById(R.id.progressBar);
        mLayoutProgress = (RelativeLayout) findViewById(R.id.LayoutProgressBar);
        mViewOrder      = (TextView) findViewById(R.id.viewOrder);
        mViewDate       = (TextView) findViewById(R.id.viewDate);
        mViewDesc       = (TextView) findViewById(R.id.viewServiceDescription);
        mRecyclerView   = (RecyclerView) findViewById(R.id.orderdetail_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(GlobalApplication.getContext()));
        mViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetDetailProgress.run();
            }
        });
        switch (mState) {
            case GeneralConstant.FRAGMENT_ORDER:
                mBtnAction.setText(getResources().getString(R.string.btn_cancel));
                break;
            case GeneralConstant.FRAGMENT_HISTORY:
                mBtnAction.setText(getResources().getString(R.string.btn_claim));
                break;
        }
        mBtnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mState.contentEquals(GeneralConstant.FRAGMENT_ORDER)){
                    Dialog.ConfirmationDialog(OrderDetailActivity.this, getResources().getString(R.string.label_batal),
                            getResources().getString(R.string.btn_yes), mCancelOrderProgress(mOrder.getOrderId()),
                            getResources().getString(R.string.btn_no));
                }else{
                    onShowClaim(mOrder.getOrderId());
                }
            }
        });

        if (Utility.isNetworkConnected())
            GetDetailProgress.run();
        else
            onShowErrorMessage(getResources().getString(R.string.msg_internet_problem));
    }
    private void initSwipeRefresh(){
        RefreshLayout   = (SwipeRefreshLayout) findViewById(R.id.log_refresh);
        RefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.YELLOW);
        RefreshLayout.setDistanceToTriggerSync(70);
        RefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        RefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utility.isNetworkConnected()){
                    isSwipe = true;
                    GetDetailProgress.run();
                }
            }
        });
    }
    private Runnable GetDetailProgress = new Runnable() {
        public void run() {
            onGetOrderDetail();
        }
    };
    private void onGetOrderDetail() {
        onShowLoading();
        onHideErrorMessage();
        final ObjUser mUserinfo = new ObjUser();
        ApiService apiService   = ApiClient.getClient().create(ApiService.class);
        retrofit2.Call<OrderDetailResponse> mRequest  = apiService.getOrderDetail(mUserinfo.UserAuth, mUserinfo.UserID, orderID);
        mRequest.enqueue(new retrofit2.Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(retrofit2.Call<OrderDetailResponse> call, retrofit2.Response<OrderDetailResponse> mResponse) {
                OrderDetailResponse Result  = mResponse.body();
                int ResultCode              = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        onHideLoading();
                        onHideErrorMessage();
                        mLayoutDetail.setVisibility(View.VISIBLE);
                        mOrder = Result.getResultData();
                        ShowOrderDetail(mOrder);
                        break;
                    case 202:
                        onHideLoading();
                        onHideErrorMessage();
                        mLayoutDetail.setVisibility(View.GONE);
                        UserTask.onGetAuth(GetDetailProgress, mUserinfo.Email);
                        break;
                    case 203:
                        onHideLoading();
                        mLayoutDetail.setVisibility(View.GONE);
                        onShowErrorMessage(getResources().getString(R.string.msg_request_failed));
                        break;
                    case 204:
                        onHideLoading();
                        mLayoutDetail.setVisibility(View.GONE);
                        onShowErrorMessage(getResources().getString(R.string.sync_failed));
                        break;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<OrderDetailResponse> call, Throwable t) {
                onHideLoading();
                mLayoutDetail.setVisibility(View.GONE);
                onShowErrorMessage(getResources().getString(R.string.sync_failed));
            }
        });
    }
    private void onHideErrorMessage() {
        mViewMessage.setEnabled(false);
        mViewMessage.setVisibility(View.GONE);
    }
    private void onShowErrorMessage(String Message) {
        mViewMessage.setEnabled(true);
        mViewMessage.setVisibility(View.VISIBLE);
        mViewMessage.setText(Message);
    }
    private void onShowLoading(){
        if (isSwipe){
            RefreshLayout.setRefreshing(true);
        }else{
            mLayoutProgress.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }
    private void onHideLoading(){
        if (isSwipe){
            isSwipe = false;
            RefreshLayout.setRefreshing(false);
        }else{
            mLayoutProgress.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void ShowOrderDetail(final ObjOrder mOrder){
        switch (mOrder.getState()) {
            case "-100":
                mBtnAction.setVisibility(View.GONE);
                mBtnAction.setEnabled(false);
                break;
            case "1":
                mBtnAction.setVisibility(View.VISIBLE);
                mBtnAction.setEnabled(true);
                break;
            case "100":
                if (mOrder.getReference().contentEquals("")){
                    Calendar c              = Calendar.getInstance();
                    SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime      = sdf.format(c.getTime());
                    String expiredTime      = expiredWaranty(mOrder.getFinishTime(), Integer.parseInt(mOrder.getWaranty()));
                    if (CheckDates(currentTime, expiredTime)){
                        mBtnAction.setVisibility(View.VISIBLE);
                        mBtnAction.setEnabled(true);
                    }else{
                        mBtnAction.setVisibility(View.GONE);
                        mBtnAction.setEnabled(false);
                    }
                }
                else{
                    mBtnAction.setVisibility(View.GONE);
                    mBtnAction.setEnabled(false);
                }
                break;
            default:
                mBtnAction.setVisibility(View.VISIBLE);
                mBtnAction.setEnabled(true);
                break;
        }

        //SET TECHNICIAN NAME
        if (mOrder.getTechName().contentEquals("") && !mOrder.getState().contentEquals("-100"))
            mViewTechName.setText(getResources().getString(R.string.waiting_technician));
        else
            mViewTechName.setText(mOrder.getTechName());

        //SET TECHNICIAN IMAGE
        if (!mOrder.getTechImage().contentEquals("")){
            Picasso.with(OrderDetailActivity.this)
                    .load(GeneralConstant.DOMAIN_SERVER+"/"+mOrder.getTechImage())
                    .error(R.drawable.ic_profile)
                    .into(mTechImage);
        }

        mViewOrder.setText(mOrder.getOrderId());
        mViewDate.setText(Utility.localizeTime(mOrder.getOrderDate()));
        mViewDesc.setText(mOrder.getDescription());
        mOrderLogList   = mOrder.getOrderLog();
        mAdapter        = new OrderDetailAdapter(OrderDetailActivity.this, mOrderLogList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private Runnable mCancelOrderProgress(final String orderId){
        Runnable aRunnable = new Runnable(){
            public void run(){
                if (Utility.isNetworkConnected()) {
                    OrderTask.onCancelOrder(OrderDetailActivity.this, mCancelOrderSuccess, orderId);
                } else
                    Dialog.InformationDialog(OrderDetailActivity.this, getResources().getString(R.string.msg_no_internet));
            }
        };
        return aRunnable;
    }
    private Runnable mCancelOrderSuccess = new Runnable() {
        public void run() {
            Dialog.InformationDialog(OrderDetailActivity.this, getResources().getString(R.string.msg_cancel_success));
            onBackPressed();
        }
    };

    private void onShowClaim(final String OrderId){
        LayoutInflater inflater = LayoutInflater.from(OrderDetailActivity.this);
        final View alertView    = inflater.inflate(R.layout.dialog_report, null, true);
        final EditText mProblem = (EditText) alertView.findViewById(R.id.input_Problem);

        final AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
        builder.setView(alertView);
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.btn_send), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                builder.setCancelable(true);
                AddClaim(OrderId, mProblem.getText().toString()).run();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                builder.setCancelable(true);
            }
        });
        builder.show();
    }
    public boolean CheckDates(String startDate, String endDate) {
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean b = false;
        try {
            if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                b = true;  // If start date is before end date.
            } else if (dfDate.parse(startDate).equals(dfDate.parse(endDate))) {
                b = true;  // If two dates are equal.
            } else {
                b = false; // If start date is after the end date.
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return b;
    }
    public String expiredWaranty(String finishDate, int orderWaranty) {
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date               = new Date();
        try {
            date            = dfDate.parse(finishDate);
            Calendar cal    = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_YEAR, orderWaranty);

            return dfDate.format(cal.getTime());
        } catch (ParseException e) {
            return "";
        }
    }

    private Runnable AddClaim(final String orderId, final String strDescription){
        Runnable aRunnable = new Runnable(){
            public void run(){
                if (Utility.isNetworkConnected()) {
                    OrderTask.onAddClaim(OrderDetailActivity.this,
                            CreateSuccess,
                            CreateFailed,
                            orderId,
                            strDescription);
                } else
                    Dialog.InformationDialog(OrderDetailActivity.this, getResources().getString(R.string.msg_no_internet));
            }
        };
        return aRunnable;
    }
    private Runnable CreateSuccess = new Runnable() {
        public void run() {
            Toast.makeText(OrderDetailActivity.this, getResources().getString(R.string.msg_claim_success), Toast.LENGTH_LONG).show();
            Intent intent  = new Intent(OrderDetailActivity.this, MainActivity.class);
            intent.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
            ActivityTransitionLauncher.with(OrderDetailActivity.this).from(findViewById(R.id.orderdetail_recycler)).launch(intent);

//            MainActivity.mView      = GeneralConstant.FRAGMENT_ORDER;
//            MainActivity.navigationView.setCheckedItem(R.id.nav_order);
//            MainActivity.mFragment  = new OrderFragment();
//            MainActivity.mFragmentManager.beginTransaction().replace(R.id.FragmentContent, new OrderFragment()).commit();
        }
    };
    private Runnable  CreateFailed = new Runnable() {
        public void run() {
            Toast.makeText(OrderDetailActivity.this, getResources().getString(R.string.msg_claim_failed), Toast.LENGTH_LONG).show();
        }
    };
}
