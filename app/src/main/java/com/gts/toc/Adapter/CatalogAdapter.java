package com.gts.toc.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gts.toc.R;
import com.gts.toc.object.ObjCatalog;
import com.gts.toc.utility.MarginDecoration;
import com.gts.toc.utility.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by war on 11/13/2016.
 */

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private Context mContext;
    private List<ObjCatalog> mItems;
    private int mColoumn;
    private int mLayOutWidth;
    private int mLayOutHeight;
    private int RecyclerPadding = new MarginDecoration().getmHorizontalmargin();
    private ViewHolder viewHolder;

    public CatalogAdapter(Context Context, List<ObjCatalog> itemData, int Coloumn) {
        super();
        mContext                = Context;
        mItems                  = itemData;
        mColoumn                = Coloumn;
        int mWasteHorizontal    = ( mColoumn + 2 ) * RecyclerPadding;
        mLayOutWidth            = (int) (0.75 * ( (Utility.getScreenWidth() - mWasteHorizontal)/mColoumn ));
        mLayOutHeight           = (int) ( mLayOutWidth);
    }

    @Override
    public CatalogAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v      = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalog_item, viewGroup, false);
        viewHolder  = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final ObjCatalog Catalog        = mItems.get(position);
        Picasso.with(mContext)
                .load(Catalog.getmCatalogImage())
                .resize(mLayOutWidth, mLayOutHeight)
                .centerCrop()
                .into(viewHolder.menuIcon);
//        viewHolder.menuIcon.setImageDrawable(Catalog.getmCatalogImage());
        viewHolder.menuTitle.setText(Catalog.getmCatalogtitle());
    }

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
            menuIcon        = (ImageView)itemView.findViewById(R.id.menu_image);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mLayOutWidth, mLayOutHeight);
            menuIcon.setLayoutParams(layoutParams);
            menuTitle       = (TextView)itemView.findViewById(R.id.menu_title);
        }
    }
}
