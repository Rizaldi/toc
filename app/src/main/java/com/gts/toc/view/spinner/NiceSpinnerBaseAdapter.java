package com.gts.toc.view.spinner;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gts.toc.R;
import com.gts.toc.object.ObjSpinnerItem;

import java.util.List;

/**
 * @author angelo.marchesin
 */

@SuppressWarnings("unused")
public abstract class NiceSpinnerBaseAdapter extends BaseAdapter {
    protected Context mContext;
    protected int mSelectedIndex;
    private final List<ObjSpinnerItem> mItems;

    public NiceSpinnerBaseAdapter(Context context, List<ObjSpinnerItem> items) {
        mContext    = context;
        mItems      = items;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.spinner_listitem, null);
            textView = (TextView) convertView.findViewById(R.id.tv_tinted_spinner);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.selector));
            }

            convertView.setTag(new ViewHolder(textView));
        } else {
            textView = ((ViewHolder) convertView.getTag()).textView;
        }

        textView.setText(getItem(position).getItemName().toString());

        return convertView;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void notifyItemSelected(int index) {
        mSelectedIndex = index;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ObjSpinnerItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public abstract int getCount();

    public ObjSpinnerItem getItemInDataset(int position) {
        return mItems.get(position);
    }

    protected static class ViewHolder {

        public TextView textView;

        public ViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}
