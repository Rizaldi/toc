package com.gts.toc.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gts.toc.Activity.LoginActivity;
import com.gts.toc.Activity.MainActivity;
import com.gts.toc.Activity.ProfileActivity;
import com.gts.toc.Fragment.HistoryFragment;
import com.gts.toc.Fragment.OrderFragment;
import com.gts.toc.R;
import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.object.ObjMenu;
import com.gts.toc.object.ObjUser;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.MarginDecoration;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.util.List;

/**
 * Created by war on 11/13/2016.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private DatabaseHandler mDataBase = new DatabaseHandler();
    private ObjUser mUserinfo   = new ObjUser();
    private Context mContext;
    private List<ObjMenu> mItems;
    private int mColoumn;
    private int mLayOutWidth;
    private int mLayOutHeight;
    private int RecyclerPadding = new MarginDecoration().getmHorizontalmargin();
    private ViewHolder viewHolder;

    public MenuAdapter(Context Context, List<ObjMenu> itemData, int Coloumn) {
        super();
        mContext                = Context;
        mItems                  = itemData;
        mColoumn                = Coloumn;
        int mWasteHorizontal    = ( mColoumn + 3 ) * RecyclerPadding;
        mLayOutWidth            = (int) ( Utility.getScreenWidth() - mWasteHorizontal) / mColoumn;
        mLayOutHeight           = (int) ( mLayOutWidth);
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v      = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_item, viewGroup, false);
        viewHolder  = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final ObjMenu Menu        = mItems.get(position);
        viewHolder.menuIcon.setImageDrawable(Menu.getmMenuImage());
        viewHolder.menuTitle.setText(Menu.getmMenutitle());
        viewHolder.menuLayOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Menu.getmMenuId().contentEquals("4")){
                    Dialog.ConfirmationDialog(mContext, mContext.getResources().getString(R.string.label_logout),
                            mContext.getResources().getString(R.string.btn_yes), mLogOut,
                            mContext.getResources().getString(R.string.btn_cancel));
                }else{
                    switch(Menu.getmMenuId()) {
                        case "1":
                            Intent intentProfile  = new Intent(mContext, ProfileActivity.class);
                            ActivityTransitionLauncher.with((Activity) mContext).from(viewHolder.menuLayOut).launch(intentProfile);
                            break;
                        case "2":
                            MainActivity.mView      = GeneralConstant.FRAGMENT_ORDER;
                            MainActivity.navigationView.setCheckedItem(R.id.nav_order);
                            MainActivity.mFragment  = new OrderFragment();
                            MainActivity.mFragmentManager.beginTransaction().replace(R.id.FragmentContent, new OrderFragment()).commit();
                            break;
                        case "3":
                            MainActivity.mView      = GeneralConstant.FRAGMENT_HISTORY;
                            MainActivity.navigationView.setCheckedItem(R.id.nav_history);
                            MainActivity.mFragment  = new HistoryFragment();
                            MainActivity.mFragmentManager.beginTransaction().replace(R.id.FragmentContent, new HistoryFragment()).commit();
                            break;
                    }
                }
            }
        });
    }

    private Runnable mLogOut = new Runnable() {
        @Override
        public void run() {
            mDataBase.DeleteUser(mUserinfo.UserID);
            Intent intentLogout  = new Intent(mContext, LoginActivity.class);
            ActivityTransitionLauncher.with((Activity) mContext).from(viewHolder.menuLayOut).launch(intentLogout);
        }
    };

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout menuLayOut;
        public ImageView menuIcon;
        public TextView menuTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            menuLayOut      = (RelativeLayout)itemView.findViewById(R.id.image_layout);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mLayOutWidth, mLayOutHeight);
            menuLayOut.setLayoutParams(layoutParams);
            menuIcon        = (ImageView)itemView.findViewById(R.id.menu_image);
            menuTitle       = (TextView)itemView.findViewById(R.id.menu_title);
        }
    }
}
