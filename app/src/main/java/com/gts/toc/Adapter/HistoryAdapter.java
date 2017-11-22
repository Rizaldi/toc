package com.gts.toc.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gts.toc.Activity.MainActivity;
import com.gts.toc.Activity.OrderDetailActivity;
import com.gts.toc.Fragment.OrderFragment;
import com.gts.toc.R;
import com.gts.toc.object.ObjOrder;
import com.gts.toc.rest.OrderTask;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by war on 11/13/2016.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context mContext;
    private List<ObjOrder> mItems;
    private String mState;

    public HistoryAdapter(Context Context, List<ObjOrder> itemData, String State) {
        super();
        mContext    = Context;
        mItems      = itemData;
        mState      = State;
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v      = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_item, viewGroup, false);
        ViewHolder viewHolder  = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final ObjOrder Order    = mItems.get(position);
        viewHolder.orderID.setText(mContext.getResources().getString(R.string.label_orderid) +" : "+Order.getOrderId());
        viewHolder.orderDate.setText(Utility.localizeTime(Order.getOrderDate()));
//        viewHolder.orderTitle.setText(Order.getTitle());
        viewHolder.orderDesc.setText(Order.getDescription());
        if (Order.getState().contentEquals("100")){
            viewHolder.orderState.setText(mContext.getResources().getString(R.string.info_order_finish));
            viewHolder.orderState.setTextColor(mContext.getResources().getColor(R.color.colorInfogreen));

//            if (!Order.getReference().contentEquals("")){
//                Calendar c              = Calendar.getInstance();
//                SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String currentTime      = sdf.format(c.getTime());
//                String expiredTime      = expiredWaranty(Order.getFinishTime(), Integer.parseInt(Order.getWaranty()));
//                if (CheckDates(currentTime, expiredTime)){
//                    viewHolder.btnClaim.setVisibility(View.VISIBLE);
//                    viewHolder.btnClaim.setEnabled(true);
//                    viewHolder.btnClaim.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_assignment_late_black_24dp));
//                }else{
//                    viewHolder.btnClaim.setVisibility(View.GONE);
//                    viewHolder.btnClaim.setEnabled(false);
//                }
//            }
//            else{
//                viewHolder.btnClaim.setVisibility(View.GONE);
//                viewHolder.btnClaim.setEnabled(false);
//            }
        }else{
            viewHolder.orderState.setText(mContext.getResources().getString(R.string.info_order_cancel));
            viewHolder.orderState.setTextColor(mContext.getResources().getColor(R.color.colorInfo));
//            viewHolder.btnClaim.setVisibility(View.GONE);
//            viewHolder.btnClaim.setEnabled(false);
        }
        viewHolder.orderLayOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDetail  = new Intent(mContext, OrderDetailActivity.class);
                intentDetail.putExtra(GeneralConstant.FRAGMENT_STATE, mState);
                intentDetail.putExtra(GeneralConstant.PARAM_ORDERID, Order.getOrderId());
                ActivityTransitionLauncher.with((Activity) mContext).from(viewHolder.orderLayOut).launch(intentDetail);
            }
        });
        viewHolder.btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDetail  = new Intent(mContext, OrderDetailActivity.class);
                intentDetail.putExtra(GeneralConstant.FRAGMENT_STATE, mState);
                intentDetail.putExtra(GeneralConstant.PARAM_ORDERID, Order.getOrderId());
                ActivityTransitionLauncher.with((Activity) mContext).from(viewHolder.orderLayOut).launch(intentDetail);
            }
        });
//        viewHolder.btnClaim.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onShowClaim(Order.getOrderId());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout orderLayOut;
        public TextView orderID;
        public TextView orderDate;
//        public TextView orderTitle;
        public TextView orderDesc;
        public TextView orderState;
//        public ImageView btnClaim;
        public ImageView btnMap;
        public TextView btnClick;

        public ViewHolder(View itemView) {
            super(itemView);
            orderLayOut     = (RelativeLayout)itemView.findViewById(R.id.order_layout);
            orderID         = (TextView)itemView.findViewById(R.id.order_id);
            orderDate       = (TextView)itemView.findViewById(R.id.order_date);
//            orderTitle      = (TextView)itemView.findViewById(R.id.order_title);
            orderDesc       = (TextView)itemView.findViewById(R.id.order_desc);
            orderState      = (TextView)itemView.findViewById(R.id.label_info);
//            btnClaim   = (ImageView) itemView.findViewById(R.id.btn_cancel);
            btnMap      = (ImageView) itemView.findViewById(R.id.btn_map);
            btnMap.setVisibility(View.INVISIBLE);
            btnMap.setEnabled(false);
            btnClick    = (TextView) itemView.findViewById(R.id.label_click);
        }
    }

    private void onShowClaim(final String OrderId){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View alertView    = inflater.inflate(R.layout.dialog_report, null, true);
        final EditText mProblem = (EditText) alertView.findViewById(R.id.input_Problem);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertView);
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getResources().getString(R.string.btn_send), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                builder.setCancelable(true);
                AddClaim(OrderId, mProblem.getText().toString()).run();
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
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
                    OrderTask.onAddClaim(mContext,
                            CreateSuccess,
                            CreateFailed,
                            orderId,
                            strDescription);
                } else
                    Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_no_internet));
            }
        };
        return aRunnable;
    }
    private Runnable CreateSuccess = new Runnable() {
        public void run() {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.msg_claim_success), Toast.LENGTH_LONG).show();
            MainActivity.mView      = GeneralConstant.FRAGMENT_ORDER;
            MainActivity.navigationView.setCheckedItem(R.id.nav_order);
            MainActivity.mFragment  = new OrderFragment();
            MainActivity.mFragmentManager.beginTransaction().replace(R.id.FragmentContent, new OrderFragment()).commit();
        }
    };
    private Runnable  CreateFailed = new Runnable() {
        public void run() {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.msg_claim_failed), Toast.LENGTH_LONG).show();
        }
    };
}
