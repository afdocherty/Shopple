package com.example.kathu228.shoplog.Helpers;

import android.support.v7.widget.RecyclerView;
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
    private List<Item> mItems;
    private ItemAdapterListener mListener;

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    // bind values
    @Override
    public void onBindViewHolder(Viewholder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    // define an interface required by the ViewHolder
    public interface ItemAdapterListener {
        public void onItemSelected(View view, int position);
    }

    // pass in the items array in the constructor
    public ItemAdapter(List<Item> items, ItemAdapterListener listener){
        mItems = items;
        mListener = listener;
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        public CheckBox cbItem;

        public Viewholder(View itemView){
            super(itemView);
            cbItem = (CheckBox) itemView.findViewById(R.id.cbItem);
        }
    }
}
