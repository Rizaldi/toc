package com.gts.toc.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gts.toc.Activity.CreateOrderActivity;
import com.gts.toc.Activity.MainActivity;
import com.gts.toc.Activity.MapsActivityNew;
import com.gts.toc.Activity.OrderDetailActivity;
import com.gts.toc.Activity.PaymentActivity;
import com.gts.toc.GlobalApplication;
import com.gts.toc.R;
import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.model.MstParams;
import com.gts.toc.object.ObjOrder;
import com.gts.toc.object.ObjUser;
import com.gts.toc.response.OrderResponse;
import com.gts.toc.rest.ApiClient;
import com.gts.toc.rest.ApiService;
import com.gts.toc.rest.OrderTask;
import com.gts.toc.rest.UserTask;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by warsono on 11/12/16.
 */

public class OrderFragment extends Fragment {

    private DatabaseHandler mDataBase = new DatabaseHandler();
    private Context mContext;
    private ViewGroup mRootView;
    private TextView mViewMessage;
    private FloatingActionButton mBtnOrder;
    private RecyclerView mRecyclerView;
    private List<ObjOrder> mOrderList = new ArrayList<ObjOrder>();
    private OrderAdapter mAdapter;
    private ProgressBar mProgressBar;
    private RelativeLayout mLayoutProgress;
    private int ServiceCost = 20000;
    private SwipeRefreshLayout RefreshLayout;
    private boolean isSwipe = false;
//    private static View mRootView;

    public OrderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (mRootView != null) {
//            ViewGroup parent = (ViewGroup) mRootView.getParent();
//            if (parent != null)
//                parent.removeView(mRootView);
//        }
//        mRootView           = inflater.inflate(R.layout.fragment_order, container, false);

        mRootView   = (ViewGroup) inflater.inflate(R.layout.fragment_order, null);
        mContext    = getActivity();
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.nav_order));
        Initialise();
        initSwipeRefresh();
        ServiceCost = getCancelFee();

        if (Utility.isNetworkConnected())
            GetOrderProgress.run();
        else
            onShowErrorMessage(getResources().getString(R.string.msg_internet_problem));

        return mRootView;
    }
    private void Initialise(){
        RefreshLayout   = (SwipeRefreshLayout) mRootView.findViewById(R.id.order_refresh);
        mViewMessage    = (TextView) mRootView.findViewById(R.id.view_message);
//        mBtnOrder       = (FloatingActionButton) mRootView.findViewById(R.id.addorder_button);
        mProgressBar    = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        mLayoutProgress = (RelativeLayout) mRootView.findViewById(R.id.LayoutProgressBar);
        mRecyclerView   = (RecyclerView) mRootView.findViewById(R.id.order_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(GlobalApplication.getContext()));
        mViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetOrderProgress.run();
            }
        });
//        mBtnOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                goToCreateOrder();
//            }
//        });
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
    private void goToCreateOrder(){
//        Intent intentCreate  = new Intent(mContext, CreateOrderActivityNew.class);
        Intent intentCreate  = new Intent(mContext, CreateOrderActivity.class);
        ActivityTransitionLauncher.with(getActivity()).from(mRecyclerView).launch(intentCreate);
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
        retrofit2.Call<OrderResponse> mRequest  = apiService.getOrder(mUserinfo.UserAuth, mUserinfo.UserID, GeneralConstant.PARAM_ORDER);
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
                        mAdapter    = new OrderAdapter(mContext, mOrderList);
                        mRecyclerView.setAdapter(mAdapter);
                        break;
                    case 202:
                        onHideLoading();
                        onHideErrorMessage();
                        UserTask.onGetAuth(GetOrderProgress, mUserinfo.Email);
                        break;
                    case 203:
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_HOME);
                        ActivityTransitionLauncher.with(getActivity()).from(mRecyclerView).launch(intent);
                        MainActivity.navigationView.setCheckedItem(R.id.nav_home);

//                        Intent intentCreate  = new Intent(mContext, CreateOrderActivity.class);
//                        intentCreate.putExtra(GeneralConstant.PARAM_ORDERLIST, GeneralConstant.ORDER_EMPTY);
//                        ActivityTransitionLauncher.with(getActivity()).from(mRecyclerView).launch(intentCreate);

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

    public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
        private Context mContext;
        private List<ObjOrder> mItems;

        public OrderAdapter(Context Context, List<ObjOrder> itemData) {
            super();
            mContext = Context;
            mItems = itemData;
        }

        @Override
        public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_item, viewGroup, false);
            ViewHolder viewHolder = new OrderAdapter.ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            final ObjOrder Order = mItems.get(position);
            viewHolder.orderDate.setText(Utility.localizeTime(Order.getOrderDate()));
//            viewHolder.orderTitle.setText(Order.getTitle());

            viewHolder.orderDesc.setText(Order.getDescription());
            SetState(mContext, Order.getState(), viewHolder.orderState, Order.getType(), Order.getPrice());
//            viewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Dialog.ConfirmationDialog(mContext, mContext.getResources().getString(R.string.label_batal),
//                            mContext.getResources().getString(R.string.btn_yes), mCancelOrderProgress(Order.getOrderId()),
//                            mContext.getResources().getString(R.string.btn_no));
//                }
//            });
            viewHolder.orderLayOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDetailAction(viewHolder.orderLayOut, Order.getState(), mItems.get(position));
                }
            });
            viewHolder.btnClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDetailAction(viewHolder.orderLayOut, Order.getState(), mItems.get(position));
                }
            });
            if (Order.getSelectState().contentEquals("1")){
                viewHolder.btnMap.setVisibility(View.VISIBLE);
                viewHolder.btnMap.setEnabled(true);
            }else{
                viewHolder.btnMap.setVisibility(View.INVISIBLE);
                viewHolder.btnMap.setEnabled(false);
            }
            if (Order.getType().contentEquals("1")){
                viewHolder.order_item.setBackgroundColor(getResources().getColor(R.color.colorClaim));
                viewHolder.orderID.setText(mContext.getResources().getString(R.string.label_claimid) + " : " + Order.getOrderId());
                viewHolder.btnMap.setVisibility(View.INVISIBLE);
                viewHolder.btnMap.setEnabled(false);
            }else{
                viewHolder.orderID.setText(mContext.getResources().getString(R.string.label_orderid) + " : " + Order.getOrderId());
            }
            viewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ( Utility.isNetworkConnected() ) {
                        Intent intent = new Intent(mContext, MapsActivityNew.class);
                        intent.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
                        intent.putExtra(GeneralConstant.PARAM_TECHID, Order.getTechID());
                        intent.putExtra(GeneralConstant.PARAM_LOCATION, Order.getPoint());
                        ActivityTransitionLauncher.with((Activity) mContext).from(viewHolder.orderLayOut).launch(intent);
                    }else {
                        Dialog.InformationDialog(mContext, getResources().getString(R.string.msg_no_internet));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public RelativeLayout orderLayOut;
            public RelativeLayout order_item;
            public TextView orderID;
            public TextView orderDate;
//            public TextView orderTitle;
            public TextView orderDesc;
            public TextView orderState;
//            public ImageView btnCancel;
            public ImageView btnMap;
            public TextView btnClick;

            public ViewHolder(View itemView) {
                super(itemView);
                orderLayOut = (RelativeLayout) itemView.findViewById(R.id.order_layout);
                order_item  = (RelativeLayout) itemView.findViewById(R.id.order_item);
                orderID     = (TextView) itemView.findViewById(R.id.order_id);
                orderDate   = (TextView) itemView.findViewById(R.id.order_date);
//                orderTitle  = (TextView) itemView.findViewById(R.id.order_title);
                orderDesc   = (TextView) itemView.findViewById(R.id.order_desc);
                orderState  = (TextView) itemView.findViewById(R.id.label_info);
//                btnCancel   = (ImageView) itemView.findViewById(R.id.btn_cancel);
                btnMap      = (ImageView) itemView.findViewById(R.id.btn_map);
                btnClick    = (TextView) itemView.findViewById(R.id.label_click);
            }
        }
    }

    private Runnable mCancelOrderProgress(final String orderId){
        Runnable aRunnable = new Runnable(){
            public void run(){
                if (Utility.isNetworkConnected()) {
                    OrderTask.onCancelOrder(mContext, mCancelOrderSuccess, orderId);
                } else
                    Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_no_internet));
            }
        };
        return aRunnable;
    }
    private Runnable mCancelOrderSuccess = new Runnable() {
        public void run() {
            Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_cancel_success));
            GetOrderProgress.run();
        }
    };
    private void SetState(Context mContext, String mState, TextView mView, String Type, String mPrice) {
        String mStateDesc = "";
        if (Type.contentEquals("1")){
            switch (mState) {
//                case "-100":
//                    mStateDesc = mContext.getResources().getString(R.string.info_cancel);
//                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
//                    break;
//                case "0":
//                    mStateDesc = mContext.getResources().getString(R.string.info_claim_receive);
//                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
//                    break;
//                case "5":
//                    mStateDesc = mContext.getResources().getString(R.string.info_agreement);
//                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
//                    break;
                case "10":
                    mStateDesc = mContext.getResources().getString(R.string.info_delivery);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
                    break;
//                case "100":
//                    mStateDesc = mContext.getResources().getString(R.string.info_end);
//                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
//                    break;
                case "8":
                    mStateDesc = mContext.getResources().getString(R.string.claim_completed);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
                    break;
                default:
                    mStateDesc = mContext.getResources().getString(R.string.info_garansi);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
//                    mStateDesc = "";
                    break;
            }
        }else{
            switch (mState) {
                case "-100":
                    mStateDesc = mContext.getResources().getString(R.string.info_cancel);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
                    break;
                case "0":
                    mStateDesc = mContext.getResources().getString(R.string.info_payment_wait);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
                    break;
                case "41":
                    mStateDesc = mContext.getResources().getString(R.string.label_wait_estimation);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
                    break;
                case "5":
                    mStateDesc = mContext.getResources().getString(R.string.info_agreement);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
                    break;
                case "51":
                    mStateDesc = mContext.getResources().getString(R.string.info_onprogress);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
                    break;
                case "6":
                    mStateDesc = mContext.getResources().getString(R.string.info_cost_wait) +" ( Rp. "+Utility.doubleToStringNoDecimal(Integer.parseInt(mPrice))+" )";
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
                    break;
                case "61":
                    mStateDesc = mContext.getResources().getString(R.string.info_cost_agreement);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
//                    mStateDesc = mContext.getResources().getString(R.string.info_onprogress);
//                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
                    break;
//                case "62":
//                    mStateDesc = mContext.getResources().getString(R.string.info_delivery_agree_wait);
//                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
//                    break;
//                case "71":
//                    mStateDesc = mContext.getResources().getString(R.string.info_payment_cancel);
//                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
//                    break;
//                case "72":
//                    mStateDesc = mContext.getResources().getString(R.string.info_notdelivery);
//                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
//                    break;
                case "8":
                    mStateDesc = mContext.getResources().getString(R.string.info_finish);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
                    break;
                case "9":
                    mStateDesc = mContext.getResources().getString(R.string.info_lunas);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
                    break;
                case "10":
                    mStateDesc = mContext.getResources().getString(R.string.info_delivery);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
                    break;
                case "100":
                    mStateDesc = mContext.getResources().getString(R.string.info_end);
                    mView.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));
                    break;
                default:
                    mStateDesc = "";
                    mView.setVisibility(View.GONE);
                    break;
            }
        }
        mView.setText(mStateDesc);
    }

    private int getCancelFee(){
        List<MstParams> ParamsList = mDataBase.GetParams(GeneralConstant.PARAMS_FEE_ID);
        if (ParamsList.size() > 0){
            MstParams mParams = ParamsList.get(0);
            if (mParams.getParamsValue() != null){
                return Integer.parseInt(mParams.getParamsValue());
            }else{
                return 20000;
            }
        }else{
            return 20000;
        }
    }

    public void onDetailAction(View mView, String mState, ObjOrder mOrder) {
        if (mOrder.getType().contentEquals("1")){
            switch (mState) {
//                case "5":
//                    Dialog.ApprovalDialog(mContext, getResources().getString(R.string.pick_up_approval),
//                            getResources().getString(R.string.btn_yes), onPickupApprovProgress(mOrder.getOrderId(), "1"),
//                            getResources().getString(R.string.btn_no), onPickupApprovProgress(mOrder.getOrderId(), "0"));
//                    break;
                case "8":
                    Dialog.ApprovalDialog(mContext, getResources().getString(R.string.claim_finish),
                            getResources().getString(R.string.btn_yes), onFinishOrderProgress(mOrder.getOrderId()),
                            getResources().getString(R.string.btn_no), null);
                    break;
                case "10":
                    Dialog.ApprovalDialog(mContext, getResources().getString(R.string.claim_finish),
                            getResources().getString(R.string.btn_yes), onFinishOrderProgress(mOrder.getOrderId()),
                            getResources().getString(R.string.btn_no), null);
                    break;
                default:
                    Intent intentDetail = new Intent(mContext, OrderDetailActivity.class);
                    intentDetail.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
                    intentDetail.putExtra(GeneralConstant.PARAM_ORDERID, mOrder.getOrderId());
                    ActivityTransitionLauncher.with((Activity) mContext).from(mView).launch(intentDetail);
                    break;
            }
        }else{
            switch (mState) {
                case "0":
                    Intent intentBooking = new Intent(mContext, PaymentActivity.class);
                    intentBooking.putExtra(GeneralConstant.PAYMENT_TYPE, GeneralConstant.PAYMENT_BOOKING);
                    intentBooking.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
                    intentBooking.putExtra(GeneralConstant.PARAM_ORDERID, mOrder.getOrderId());
                    intentBooking.putExtra(GeneralConstant.PARAM_COST, ServiceCost);
                    ActivityTransitionLauncher.with((Activity) mContext).from(mView).launch(intentBooking);
                    break;
                case "5":
                    Dialog.ApprovalDialog(mContext, getResources().getString(R.string.pick_up_approval),
                            getResources().getString(R.string.btn_yes), onPickupApprovProgress(mOrder.getOrderId(), "1"),
                            getResources().getString(R.string.btn_no), onPickupApprovProgress(mOrder.getOrderId(), "0"));
                    break;
                case "6":
                    Dialog.ApprovalDialog(mContext, mContext.getResources().getString(R.string.cost_approval, Utility.doubleToStringNoDecimal(Integer.parseInt(mOrder.getPrice()))),
                            getResources().getString(R.string.btn_yes), onEstimasiApprovProgress(mOrder.getOrderId(), "1", mOrder.getPrice(), mView),
                            getResources().getString(R.string.btn_no), onEstimasiApprovProgress(mOrder.getOrderId(), "0", mOrder.getPrice(), mView));
                    break;
                case "61":
                    Intent intentLunas = new Intent(mContext, PaymentActivity.class);
                    intentLunas.putExtra(GeneralConstant.PAYMENT_TYPE, GeneralConstant.PAYMENT_LUNAS);
                    intentLunas.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
                    intentLunas.putExtra(GeneralConstant.PARAM_ORDERID, mOrder.getOrderId());
                    intentLunas.putExtra(GeneralConstant.PARAM_COST, Integer.parseInt(mOrder.getPrice()));
                    ActivityTransitionLauncher.with((Activity) mContext).from(mView).launch(intentLunas);
                    break;
//                case "62":
//                    Dialog.ApprovalDialog(mContext, getResources().getString(R.string.delivery_approval),
//                            getResources().getString(R.string.btn_delivery), onDeliveryApprovProgress(mOrder.getOrderId(), "1"),
//                            getResources().getString(R.string.btn_ambil), onDeliveryApprovProgress(mOrder.getOrderId(), "0"));
//                    break;
//                case "71":
//                    Intent intentCancelPay = new Intent(mContext, PaymentActivity.class);
//                    intentCancelPay.putExtra(GeneralConstant.PAYMENT_TYPE, GeneralConstant.PAYMENT_CANCEL);
//                    intentCancelPay.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
//                    intentCancelPay.putExtra(GeneralConstant.PARAM_ORDERID, mOrder.getOrderId());
//                    intentCancelPay.putExtra(GeneralConstant.PARAM_COST, ServiceCost);
//                    ActivityTransitionLauncher.with((Activity) mContext).from(mView).launch(intentCancelPay);
//                    break;
//                case "8":
//                    Intent intentLunas = new Intent(mContext, PaymentActivity.class);
//                    intentLunas.putExtra(GeneralConstant.PAYMENT_TYPE, GeneralConstant.PAYMENT_LUNAS);
//                    intentLunas.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
//                    intentLunas.putExtra(GeneralConstant.PARAM_ORDERID, mOrder.getOrderId());
//                    intentLunas.putExtra(GeneralConstant.PARAM_COST, Integer.parseInt(mOrder.getPrice()));
//                    ActivityTransitionLauncher.with((Activity) mContext).from(mView).launch(intentLunas);
//                    break;
                case "10":
                    Dialog.ApprovalDialog(mContext, getResources().getString(R.string.delivery_info),
                            getResources().getString(R.string.btn_yes), onFinishOrderProgress(mOrder.getOrderId()),
                            getResources().getString(R.string.btn_no), null);
                    break;
                default:
                    Intent intentDetail = new Intent(mContext, OrderDetailActivity.class);
                    intentDetail.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
                    intentDetail.putExtra(GeneralConstant.PARAM_ORDERID, mOrder.getOrderId());
                    ActivityTransitionLauncher.with((Activity) mContext).from(mView).launch(intentDetail);
                    break;
            }
        }
    }

    Runnable onPickupApprovProgress(final String OrderID, final String PickupState) {
        Runnable mRun = new Runnable(){
            public void run(){
                if ( Utility.isNetworkConnected() ) {
                    OrderTask.onUpdatePickupState(
                            mContext,
                            GetOrderProgress,
                            OrderID,
                            PickupState);
                }else
                    Dialog.InformationDialog(mContext, getResources().getString(R.string.msg_no_internet));
            }
        };
        return mRun;
    }
    Runnable onEstimasiApprovProgress(final String OrderID, final String EstimasiState, final String Price, final View mView) {
        Runnable mRun = new Runnable(){
            public void run(){
                if ( Utility.isNetworkConnected() ) {
                    if (EstimasiState.contentEquals("1")){
                        OrderTask.onUpdateEstimasiState(
                                mContext,
                                onGotoPayment(OrderID, Price,mView),
                                OrderID,
                                EstimasiState);
                    }else{
                        OrderTask.onUpdateEstimasiState(
                                mContext,
                                GetOrderProgress,
                                OrderID,
                                EstimasiState);
                    }
                }else
                    Dialog.InformationDialog(mContext, getResources().getString(R.string.msg_no_internet));
            }
        };
        return mRun;
    }
    Runnable onDeliveryApprovProgress(final String OrderID, final String DeliveryState) {
        Runnable mRun = new Runnable(){
            public void run(){
                if ( Utility.isNetworkConnected() ) {
                    OrderTask.onUpdateDeliveryState(
                            mContext,
                            GetOrderProgress,
                            OrderID,
                            DeliveryState);
                }else
                    Dialog.InformationDialog(mContext, getResources().getString(R.string.msg_no_internet));
            }
        };
        return mRun;
    }
    Runnable onFinishOrderProgress(final String OrderID) {
        Runnable mRun = new Runnable(){
            public void run(){
                if ( Utility.isNetworkConnected() ) {
                    OrderTask.onFinishOrder(
                            mContext,
                            GetOrderProgress,
                            OrderID);
                }else
                    Dialog.InformationDialog(mContext, getResources().getString(R.string.msg_no_internet));
            }
        };
        return mRun;
    }
    Runnable onGotoPayment(final String OrderID, final String Price, final View mView) {
        Runnable mRun = new Runnable(){
            public void run(){
                Intent intentLunas = new Intent(mContext, PaymentActivity.class);
                intentLunas.putExtra(GeneralConstant.PAYMENT_TYPE, GeneralConstant.PAYMENT_LUNAS);
                intentLunas.putExtra(GeneralConstant.FRAGMENT_STATE, GeneralConstant.FRAGMENT_ORDER);
                intentLunas.putExtra(GeneralConstant.PARAM_ORDERID, OrderID);
                intentLunas.putExtra(GeneralConstant.PARAM_COST, Integer.parseInt(Price));
                ActivityTransitionLauncher.with((Activity) mContext).from(mView).launch(intentLunas);
            }
        };
        return mRun;
    }
}