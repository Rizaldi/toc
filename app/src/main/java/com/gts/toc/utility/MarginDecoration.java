package com.gts.toc.utility;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by war on 11/12/2016.
 */

public class MarginDecoration extends RecyclerView.ItemDecoration {
  private int mHorizontalmargin;
  private int mVerticalmargin;

  public MarginDecoration() {
      mHorizontalmargin = Utility.getScreenWidth() / 50;
      mVerticalmargin   = (int) (1 * mHorizontalmargin);
  }

  @Override
  public void getItemOffsets(
          Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    outRect.set(mHorizontalmargin, mVerticalmargin, mHorizontalmargin, mVerticalmargin );
  }

    public int getmHorizontalmargin(){
        return mHorizontalmargin;
    }
}