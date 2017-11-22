package com.gts.toc.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gts.toc.Activity.MainActivity;
import com.gts.toc.R;

/**
 * Created by warsono on 11/12/16.
 */

public class TermFragment extends Fragment {

    private ViewGroup mRootView;
    private TextView mViewTerm;
    private ProgressBar mProgressBar;
    private RelativeLayout mLayoutProgress;

    public TermFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView   = (ViewGroup) inflater.inflate(R.layout.fragment_term, null);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.nav_term));
        Initialise();
        onHideLoading();

        return mRootView;
    }
    private void Initialise(){
        mViewTerm       = (TextView) mRootView.findViewById(R.id.view_term);
        mProgressBar    = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        mLayoutProgress = (RelativeLayout) mRootView.findViewById(R.id.LayoutProgressBar);
        mViewTerm.setText(Html.fromHtml(getResources().getString(R.string.term_desc)));
    }
    private void onHideLoading(){
        mLayoutProgress.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }
}