package com.gts.toc.view.spinner;

import android.content.Context;

import com.gts.toc.object.ObjSpinnerItem;

import java.util.List;

/**
 * @author angelo.marchesin
 */

public class NiceSpinnerAdapter extends NiceSpinnerBaseAdapter {

    private final List<ObjSpinnerItem> mItems;

    public NiceSpinnerAdapter(Context context, List<ObjSpinnerItem> items) {
        super(context, items);
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size() - 1;
    }

    @Override
    public ObjSpinnerItem getItem(int position) {
        if (position >= mSelectedIndex) {
            return mItems.get(position + 1);
        } else {
            return mItems.get(position);
        }
    }

    @Override
    public ObjSpinnerItem getItemInDataset(int position) {
        return mItems.get(position);
    }
}