package com.gts.toc.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gts.toc.Activity.MainActivity;
import com.gts.toc.Adapter.HistoryAdapter;
import com.gts.toc.GlobalApplication;
import com.gts.toc.R;
import com.gts.toc.object.ObjOrder;
import com.gts.toc.object.ObjUser;
import com.gts.toc.response.OrderResponse;
import com.gts.toc.rest.ApiClient;
import com.gts.toc.rest.ApiService;
import com.gts.toc.rest.UserTask;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by warsono on 11/12/16.
 */

public class HistoryFragment extends Fragment {

    private Context mContext;
    private ViewGroup mRootView;
    private TextView mViewMessage;
    private RecyclerView mRecyclerView;
    private List<ObjOrder> mOrderList = new ArrayList<ObjOrder>();
    private HistoryAdapter mAdapter;
    private ProgressBar mProgressBar;
    private RelativeLayout mLayoutProgress;
    private SwipeRefreshLayout RefreshLayout;
    private boolean isSwipe = false;
//    private static View mRootView;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (mRootView != null) {
//            ViewGroup parent = (ViewGroup) mRootView.getParent();
//            if (parent != null)
//                parent.removeView(mRootView);
//        }
//        mRootView           = inflater.inflate(R.layout.fragment_history, container, false);

        mRootView   = (ViewGroup) inflater.inflate(R.layout.fragment_history, null);
        mContext    = getActivity();
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.nav_history));
        Initialise();
        initSwipeRefresh();

        if (Utility.isNetworkConnected())
            GetOrderProgress.run();
        else
            onShowErrorMessage(getResources().getString(R.string.msg_internet_problem));

        return mRootView;
    }
    private void Initialise(){
        RefreshLayout   = (SwipeRefreshLayout) mRootView.findViewById(R.id.history_refresh);
        mViewMessage    = (TextView) mRootView.findViewById(R.id.view_message);
        mProgressBar    = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        mLayoutProgress = (RelativeLayout) mRootView.findViewById(R.id.LayoutProgressBar);
        mRecyclerView   = (RecyclerView) mRootView.findViewById(R.id.history_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(GlobalApplication.getContext()));
        mViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetOrderProgress.run();
            }
        });
    }
    private void initSwipeRefresh(){
        RefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.YELLOW);
        RefreshLayout.setDistanceToTriggerSync(70);
        RefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        RefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSwipe = true;
                GetOrderProgress.run();
            }
        });
    }
    private Runnable GetOrderProgress = new Runnable() {
        public void run() {
            onGetOrder();
        }
    };
    private void onGetOrder() {
        onShowLoading();
        onHideErrorMessage();
        final ObjUser mUserinfo = new ObjUser();
        ApiService apiService   = ApiClient.getClient().create(ApiService.class);
        retrofit2.Call<OrderResponse> mRequest  = apiService.getOrder(mUserinfo.UserAuth, mUserinfo.UserID, GeneralConstant.PARAM_HISTORY);
        mRequest.enqueue(new retrofit2.Callback<OrderResponse>() {
            @Override
            public void onResponse(retrofit2.Call<OrderResponse> call, retrofit2.Response<OrderResponse> mResponse) {
                OrderResponse Result    = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        onHideLoading();
                        onHideErrorMessage();
                        if (mOrderList.size() > 0)
                            mOrderList.clear();
                        mOrderList  = Result.getResultData();
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mViewMessage.setVisibility(View.GONE);
                        mAdapter    = new HistoryAdapter(mContext, mOrderList, GeneralConstant.FRAGMENT_HISTORY);
                        mRecyclerView.setAdapter(mAdapter);
                        break;
                    case 202:
                        onHideLoading();
                        onHideErrorMessage();
                        UserTask.onGetAuth(GetOrderProgress, mUserinfo.Email);
                        break;
                    case 203:
                        onHideLoading();
                        onShowErrorMessage(getResources().getString(R.string.msg_history_empty));
                        mRecyclerView.setVisibility(View.GONE);
                        break;
                    case 204:
                        onHideLoading();
                        onShowErrorMessage(getResources().getString(R.string.sync_failed));
                        break;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<OrderResponse> call, Throwable t) {
                onHideLoading();
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
}