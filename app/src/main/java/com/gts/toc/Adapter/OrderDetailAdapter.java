package com.gts.toc.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gts.toc.R;
import com.gts.toc.object.ObjOrderDetail;
import com.gts.toc.utility.Utility;

import java.util.List;

/**
 * Created by war on 11/13/2016.
 */

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private Context mContext;
    private List<ObjOrderDetail> mItems;

    public OrderDetailAdapter(Context Context, List<ObjOrderDetail> itemData) {
        super();
        mContext                = Context;
        mItems                  = itemData;
    }

    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v      = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orderdetail_item, viewGroup, false);
        ViewHolder viewHolder  = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final ObjOrderDetail Order    = mItems.get(position);
        viewHolder.logDate.setText(Utility.localizeTime(Order.getOrderTime()));
        viewHolder.logDesc.setText(Order.getOrderDesc());
        if ( (Order.getOrderMessage() != null) && (!Order.getOrderMessage().contentEquals("")) ){
            viewHolder.logMessage.setVisibility(View.VISIBLE);
            viewHolder.logMessage.setText(Order.getOrderMessage());
        }else
            viewHolder.logMessage.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView logDate;
        public TextView logDesc;
        public TextView logMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            logDate         = (TextView)itemView.findViewById(R.id.log_date);
            logDesc         = (TextView)itemView.findViewById(R.id.log_desc);
            logMessage      = (TextView)itemView.findViewById(R.id.log_message);
        }
    }
}
