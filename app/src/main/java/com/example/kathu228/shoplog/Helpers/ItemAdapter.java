package com.example.kathu228.shoplog.Helpers;

import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
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
                    handleCheckbox(mlist.get(getAdapterPosition()),getAdapterPosition(), v);
                }
            });
        }

        // checks and unchecks checkbox, while saving the object's boolean state on server
        public void handleCheckbox(final Item item, final int position, final View v){
            if (item.isChecked()){
                cbItem.setChecked(false);
                item.setChecked(false);
                item.saveInBackground();
            }
            else{
                cbItem.setChecked(true);
                item.setChecked(true);
                item.saveInBackground();

                // Delays a little after checking box, then deletes
                new CountDownTimer(700, 1000) {
                    public void onFinish() {
                        // When timer is finished
                        // Execute your code here
                        deleteItem(item, position, v);
                    }

                    public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
                ;

            }
        }

        // deletes if checkbox is checked, and allows undo deletion
        public void deleteItem(final Item item, final int position, View v){
            // Todo: update remove item
            mlist.remove(position);
            notifyItemRemoved(position);

            // if click undo in snackbar, item will reappear in list unchecked
            Snackbar.make(v, "Item Deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cbItem.setChecked(false);
                        item.setChecked(false);
                        // Todo: update add item later
                        mlist.add(position, item);
                        notifyItemInserted(position);
                        Snackbar snackbar1 = Snackbar.make(v, "Item Restored!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                })
                .show();

        }


    }
}
