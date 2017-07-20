package com.example.kathu228.shoplog.Helpers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kathu228.shoplog.Activities.ItemListActivity;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

import java.util.List;

/**
 * Created by afdoch on 7/13/17.
 */

public class ShoplistAdapter extends BaseAdapter<ShoplistAdapter.ViewHolder, ShopList> {

    public Context context;

    public ShoplistAdapter(List<ShopList> mlist, int itemViewReference) {
        super(mlist, itemViewReference);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflateView(parent);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShopList shopList = mlist.get(position);

        holder.tvListName.setText(shopList.getName());
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the list layout for fast access
    public class ViewHolder extends BaseAdapter.ViewHolder{

        public TextView tvListName;

        public ViewHolder(View itemView){
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvListName = (TextView) itemView.findViewById(R.id.tvListName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gets ShopList position
                    int position = getAdapterPosition();
                    // Make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the tweet at the position, this won't work if the class is static
                        final ShopList shopList = mlist.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, ItemListActivity.class);
                        // Pass in ShopList ObjectId
                        intent.putExtra(ShopList.SHOPLIST_TAG, shopList.getObjectId());
                        intent.putExtra("listName", shopList.getName());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
