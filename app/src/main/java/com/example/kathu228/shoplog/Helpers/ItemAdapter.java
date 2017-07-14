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
        Item item = mlist.get(position);

        holder.cbItem.setText(item.getBody());
        holder.cbItem.setChecked(item.isChecked());

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends BaseAdapter.ViewHolder{
        public CheckBox cbItem;

        public ViewHolder(View itemView){
            super(itemView);
            cbItem = (CheckBox) itemView.findViewById(R.id.cbItem);

            //adds onclicklistener to checkbox, to update object's state once you check it
            cbItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCheckbox(mlist.get(getAdapterPosition()));
                }
            });
        }

        //checks and unchecks checkbox, while saving the object's boolean state on server
        public void handleCheckbox(Item item){
            if (item.isChecked()){
                cbItem.setChecked(false);
                item.setChecked(false);
                item.saveInBackground();
            }
            else{
                cbItem.setChecked(true);
                item.setChecked(true);
                item.saveInBackground();
            }
        }
    }
}
