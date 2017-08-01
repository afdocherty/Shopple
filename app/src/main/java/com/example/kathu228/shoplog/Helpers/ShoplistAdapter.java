package com.example.kathu228.shoplog.Helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kathu228.shoplog.Activities.ItemListActivity;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by afdoch on 7/13/17.
 */

public class ShoplistAdapter extends BaseAdapter<ShoplistAdapter.ViewHolder, ShopList> implements ItemTouchHelperAdapter{

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

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        return;
    }

    @Override
    public void onItemDismiss(final int position) {

        final ShopList shopList = mlist.get(position);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Delete list");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to delete "+shopList.getName()+"?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, delete all completed items
                        shopList.removeUser(ParseUser.getCurrentUser(),null);
                        mlist.remove(position);
                        notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        notifyItemChanged(position);
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the list layout for fast access
    public class ViewHolder extends BaseAdapter.ViewHolder{

        public TextView tvListName;
        private RippleDrawable rippleDrawable;

        public ViewHolder(View itemView){
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvListName = (TextView) itemView.findViewById(R.id.tvListName);
            rippleDrawable = (RippleDrawable) tvListName.getBackground();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rippleDrawable.setColor(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.lightGray)));
                    // Gets ShopList position
                    int position = getAdapterPosition();
                    // Make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the list at the position, this won't work if the class is static
                        final ShopList shopList = mlist.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, ItemListActivity.class);
                        // Pass in ShopList ObjectId
                        intent.putExtra(ShopList.SHOPLIST_TAG, shopList.getObjectId());
                        intent.putExtra(ShopList.SHOPLIST_NEW_TAG, false);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
