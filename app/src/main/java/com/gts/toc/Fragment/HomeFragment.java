package com.gts.toc.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.gts.toc.Adapter.CatalogAdapter;
import com.gts.toc.Adapter.MenuAdapter;
import com.gts.toc.GlobalApplication;
import com.gts.toc.R;
import com.gts.toc.object.ObjCatalog;
import com.gts.toc.object.ObjMenu;
import com.gts.toc.utility.MarginDecoration;
import com.gts.toc.utility.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by warsono on 11/12/16.
 */

public class HomeFragment extends Fragment {

    private Context mContext;
    private ViewGroup mRootView;
    private RecyclerView mRecyclerView;
    private int mColoumn            = 2;
    private List<ObjMenu> mMenuList = new ArrayList<ObjMenu>();
    private MenuAdapter mAdapter;
    private int mBannerWidth;
    private int mBannerHeight;
    private ViewFlipper BannerFlipper;
    private List<ObjCatalog> mCatalogList = new ArrayList<ObjCatalog>();
    private CatalogAdapter mCatalogAdapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView   = (ViewGroup) inflater.inflate(R.layout.fragment_home, null);
        mContext    = getActivity();
        Initialise();
        initBanner();
        GetCatalogView();
        return mRootView;
    }

    private void Initialise(){
        mRecyclerView   = (RecyclerView) mRootView.findViewById(R.id.home_recycler);
        mRecyclerView.addItemDecoration(new MarginDecoration());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(GlobalApplication.getContext(), mColoumn));
    }

    private void initBanner() {
        mBannerWidth    = Utility.getScreenWidth();
        mBannerHeight   = (int) (0.5 * mBannerWidth);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mBannerWidth, mBannerHeight);

        RelativeLayout mBannerLayout    = (RelativeLayout) mRootView.findViewById(R.id.imgBanner);
        BannerFlipper       = new ViewFlipper(mContext);
        mBannerLayout.addView(BannerFlipper);
        mBannerLayout.setLayoutParams(layoutParams);
        BannerFlipper.setLayoutParams(layoutParams);

//        if (Utility.isNetworkConnected())
//            onGetBannerListProgress();
//        else
            showDefaultFlipper();
    }

    void showDefaultFlipper(){
        ImageView BannerImage = new ImageView(mContext);
        BannerFlipper.addView(BannerImage);
        Picasso.with(mContext)
                .load(R.drawable.banner_0)
                .resize(mBannerWidth, mBannerHeight)
                .into(BannerImage);

        BannerImage = new ImageView(mContext);
        BannerFlipper.addView(BannerImage);
        Picasso.with(mContext)
                .load(R.drawable.banner_1)
                .resize(mBannerWidth, mBannerHeight)
                .into(BannerImage);

        BannerFlipper.setFlipInterval(2000);
        BannerFlipper.startFlipping();
    }

    private void GetCatalogView(){
        if (mCatalogList.size() > 0)
            mCatalogList.clear();
        ObjCatalog mCatalog   = new ObjCatalog();
        mCatalog.setmCatalogId("1");
        mCatalog.setmCatalogtitle("Toshiba P55W-C5200X");
        mCatalog.setmCatalogImage(R.drawable.catalog_1);
        mCatalogList.add(mCatalog);
        mCatalog   = new ObjCatalog();
        mCatalog.setmCatalogId("2");
        mCatalog.setmCatalogtitle("Asus ROG GL552VW-DM136T");
        mCatalog.setmCatalogImage(R.drawable.catalog_2);
        mCatalogList.add(mCatalog);
        mCatalog   = new ObjCatalog();
        mCatalog.setmCatalogId("3");
        mCatalog.setmCatalogtitle("Microsoft Office 2016");
        mCatalog.setmCatalogImage(R.drawable.catalog_3);
        mCatalogList.add(mCatalog);
        mCatalog   = new ObjCatalog();
        mCatalog.setmCatalogId("4");
        mCatalog.setmCatalogtitle("Flashdisk Sandisk 16GB");
        mCatalog.setmCatalogImage(R.drawable.catalog_4);
        mCatalogList.add(mCatalog);

        mCatalogAdapter    = new CatalogAdapter(mContext, mCatalogList, mColoumn);
        mRecyclerView.setAdapter(mCatalogAdapter);
    }

    private void GetMenuView(){
        if (mMenuList.size() > 0)
            mMenuList.clear();

        ObjMenu mMenu   = new ObjMenu();
        mMenu.setmMenuId("1");
        mMenu.setmMenutitle(getResources().getString(R.string.nav_profile));
        mMenu.setmBackgroundColor(Color.TRANSPARENT);
        mMenu.setmMenuImage(getResources().getDrawable(R.drawable.ic_person_white_48dp));
        mMenuList.add(mMenu);

        mMenu   = new ObjMenu();
        mMenu.setmMenuId("2");
        mMenu.setmMenutitle(getResources().getString(R.string.nav_order));
        mMenu.setmBackgroundColor(Color.TRANSPARENT);
        mMenu.setmMenuImage(getResources().getDrawable(R.drawable.ic_assignment_black_24dp));
        mMenuList.add(mMenu);

        mMenu   = new ObjMenu();
        mMenu.setmMenuId("3");
        mMenu.setmMenutitle(getResources().getString(R.string.nav_history));
        mMenu.setmBackgroundColor(Color.TRANSPARENT);
        mMenu.setmMenuImage(getResources().getDrawable(R.drawable.ic_history_black_24dp));
        mMenuList.add(mMenu);

        mMenu   = new ObjMenu();
        mMenu.setmMenuId("4");
        mMenu.setmMenutitle(getResources().getString(R.string.nav_logout));
        mMenu.setmBackgroundColor(Color.TRANSPARENT);
        mMenu.setmMenuImage(getResources().getDrawable(R.drawable.ic_power_settings_new_black_24dp));
        mMenuList.add(mMenu);
        mAdapter    = new MenuAdapter(mContext, mMenuList, mColoumn);
        mRecyclerView.setAdapter(mAdapter);
    }
}