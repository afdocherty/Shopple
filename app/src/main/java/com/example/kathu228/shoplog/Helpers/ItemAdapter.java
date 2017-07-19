package com.example.kathu228.shoplog.Helpers;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter{

    private List<Item> mlist;
    public ItemAdapter(List<Item> mlist) {
        this.mlist = mlist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = inflateView(parent);
        View view;

        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                return new ItemViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_headers, parent, false);
                return new HeaderViewHolder(view);
        }
        return null;
//        return new ViewHolder(view);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Item item = mlist.get(position);

//        holder.cbItem.setText(item.getBody());
//        holder.cbItem.setChecked(item.isChecked());
        if (item != null) {
            switch (item.getType()) {
                case 0:
                    ((ItemViewHolder) holder).cbItem.setText(item.getBody());
                    ((ItemViewHolder) holder).cbItem.setChecked(item.isChecked());
                    break;
                case 1:
                    ((HeaderViewHolder) holder).tvHeader.setText(item.getBody());
                    break;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mlist != null) {
            Item item = mlist.get(position);
            if (item != null) {
                return item.getType();
            }
        }
        return 0;
    }

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

    // Allows user to remove checked item or check an unchecked item by swiping
    @Override
    public void onItemDismiss(int position) {
        Item item = mlist.get(position);
        if (item.isChecked() && item.getType()==0){
            deleteItemFromList(item, position);
        }
        else{
            deleteItem(item);
            addItem(item, mlist.size());
            item.setChecked(true);
            item.saveInBackground();
        }


    }

    @Override
    public int getItemCount() {
        if (mlist == null)
            return 0;
        return mlist.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ItemViewHolder extends BaseAdapter.ViewHolder{
        public CheckBox cbItem;

        public ItemViewHolder(View itemView){
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
                item.setChecked(false);
                item.saveInBackground();
                mlist.remove(position);
                mlist.add(0,item);
                cbItem.setChecked(false);
                notifyItemMoved(position, 0);
            }
            else{
                item.setChecked(true);
                item.saveInBackground();
                mlist.remove(position);
                mlist.add(item);
                cbItem.setChecked(true);
                notifyItemMoved(position, mlist.size()-1);

            }
        }
    }

    public class HeaderViewHolder extends BaseAdapter.ViewHolder{
        public TextView tvHeader;
        public ImageButton ibDelete;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            tvHeader = (TextView) itemView.findViewById(R.id.tvHeader);
            ibDelete = (ImageButton) itemView.findViewById(R.id.ibDelete);

            // asks user if want to delete all items in completed
            ibDelete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

                    // set title
                    alertDialogBuilder.setTitle("Delete completed");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Are you sure you want to delete all completed items?")
                            .setCancelable(false)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, delete all completed items
                                    deleteItems(getAdapterPosition(), v);
                                }
                            })
                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            });
        }

        //deletes all items under position
        public void deleteItems(final int position, final View v){
            for (int pos = position+1; pos<mlist.size(); pos++){
                deleteItemFromList(mlist.get(pos), position+1);
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
    private void deleteItemFromList (final Item item, final int position) {
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
//                                deleteItem(item);
                                mlist.remove(position);
                                notifyItemRemoved(position);
//                                notifyItemRangeChanged(position, getItemCount());
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

    //adds item through adapter
    public void addItem(final Item item, final int position){
        mlist.add(position, item);
        notifyItemInserted(position);
    }

    // Add an item to the MVP list
    private void addItemToList (final Item item, final int position) {
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
                                item.setChecked(false);
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

                        addItemToList(item, position);

                        Snackbar snackbar1 = Snackbar.make(v, item.getBody()+" restored!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                })
                .show();
    }


}
