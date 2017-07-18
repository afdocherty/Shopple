package com.example.kathu228.shoplog.Helpers;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.List;

/**
 * Created by kathu228 on 7/12/17.
 */

public class ItemAdapter extends BaseAdapter<ItemAdapter.ViewHolder,Item> implements ItemTouchHelperAdapter{

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

//    @Override
//    public int getItemViewType(int position) {
//
//    }

    // Allows user to move items by dragging
    // Todo: implement order in database?
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mlist, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mlist, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        //return true;
    }

    // Allows user to remove item by swiping
    // Todo: remove item from database
    @Override
    public void onItemDismiss(int position) {
        Item item = mlist.get(position);
        deleteItemFromList(item);
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

//                // Delays a little after checking box, then deletes
//                new CountDownTimer(700, 1000) {
//                    public void onFinish() {
//                        // When timer is finished
//                        // Execute your code here
//                        deleteItemFromList(item);
//                        deleteItem(item, position, v);
//                    }
//
//                    public void onTick(long millisUntilFinished) {
//                        // millisUntilFinished    The amount of time until finished.
//                    }
//                }.start();
//                ;

            }
        }
    }

    // deletes if checkbox is checked, and allows undo deletion
    public void deleteItem(final Item item){
        // Todo: update remove item
        int position = mlist.indexOf(item);
        mlist.remove(position);
        notifyItemRemoved(position);
        //undoDelete(item, position, v);

    }

    // Remove an item from the MVP list
    private void deleteItemFromList (final Item item) {
        // MVP Hack to jump straight to Segment - TODO change
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Segment");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("ItemListFragment", "Segment found");
                    // Grab the first segment (for MVP) - TODO change
                    ParseRelation<ParseObject> relationSegmentToItem = objects.get(0).getRelation("items");
                    relationSegmentToItem.remove(item);
                    objects.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("ItemListFragment", "Item removed!");
                                deleteItem(item);
                            } else {
                                Log.d("ItemListFragment", "Item not removed. Error: " + e.toString());
                                e.printStackTrace();
                            }
                        }
                    });
                    item.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("ItemListFragment", "Item deleted");
                            }
                            else {
                                Log.d("ItemListFragment", "Item not deleted. Error: " + e.toString());
                            }
                        }
                    });

                } else {
                    Log.d("ItemListFragment", "Segment not found. Error: " + e.toString());
                }

            }
        });
    }

    // Add an item to the MVP list
    private void addItemToList (final Item item) {
        // MVP Hack to jump straight to Segment - TODO change
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Segment");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("ItemListFragment", "Segment found");
                    // Grab the first segment (for MVP) - TODO change
                    ParseRelation<ParseObject> relationSegmentToItem = objects.get(0).getRelation("items");
                    relationSegmentToItem.add(item);
                    objects.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("ItemListFragment", "Item added!");
                            } else {
                                Log.d("ItemListFragment", "Item not added. Error: " + e.toString());
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Log.d("ItemListFragment", "Segment not found. Error: " + e.toString());
                }
            }
        });
    }

    // if click undo in snackbar, item will reappear in list unchecked
    public void undoDelete(final Item item, final int position, View v){
        Snackbar.make(v, item.getBody()+" deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        cbItem.setChecked(false);
                        item.setChecked(false);
                        // Todo: update add item later
                        mlist.add(position, item);
                        addItemToList(item);
                        notifyItemInserted(position);
                        Snackbar snackbar1 = Snackbar.make(v, item.getBody()+" restored!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                })
                .show();
    }


}
