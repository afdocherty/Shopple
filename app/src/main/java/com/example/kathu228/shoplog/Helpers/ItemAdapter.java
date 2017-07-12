package com.example.kathu228.shoplog.Helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.R;

import java.util.List;

/**
 * Created by kathu228 on 7/12/17.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.Viewholder> {
    // Store a member variable for the items
    private List<Item> mItems;
    private ItemAdapterListener mListener;
    // Store the context for easy access
    Context mContext;

    // inflate layout from XML and returns the holder
    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context= parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View itemView = inflater.inflate(R.layout.item, parent, false);

        // Return a new holder instance
        Viewholder viewholder = new Viewholder(itemView);
        return viewholder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {
        // get position of item for data
        Item item = mItems.get(position);

        // populate the item views with data
        holder.cbItem.setText(item.getBody());
        holder.cbItem.setPressed(true); // TODO - get checked boolean from item instance

        // TODO - handle click event to change item instance
//        // puts onclicklistener onto checkbox for each item
//        // unchecks if checked and checks if unchecked
//        holder.cbItem.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                if (item.checked){
//                    item.checked = false;
//                    holder.cbItem.setPressed(false);
//                }
//                else{
//                    item.checked = true;
//                    holder.cbItem.setPressed(true);
//                }
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // define an interface required by the ViewHolder
    public interface ItemAdapterListener {
        public void onItemSelected(View view, int position);
    }

    // pass in the items array into the constructor
    public ItemAdapter(List<Item> items, ItemAdapterListener listener){
        mItems = items;
        mListener = listener;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class Viewholder extends RecyclerView.ViewHolder{
        public CheckBox cbItem;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public Viewholder(View itemView){
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            cbItem = (CheckBox) itemView.findViewById(R.id.cbItem);
        }
    }
}
