package com.example.kathu228.shoplog.Helpers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.R;

import java.util.List;

/**
 * Created by kathu228 on 7/12/17.
 */

public class ItemAdapter extends BaseAdapter<ItemAdapter.ViewHolder,Item> {


    public ItemAdapter(List<Item> mlist, int itemViewReference) {
        super(mlist, itemViewReference);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflateView(parent);
        return new ViewHolder(view);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // get position of item for data
        Item item = mlist.get(position);

        // populate the item views with data
        holder.cbItem.setText(item.getBody());
        holder.cbItem.setChecked(false); // TODO - get checked boolean from item instance

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

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends BaseAdapter.ViewHolder{
        public CheckBox cbItem;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView){
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            cbItem = (CheckBox) itemView.findViewById(R.id.cbItem);
        }
    }
}
