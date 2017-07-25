package com.example.kathu228.shoplog.Helpers;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.Segment;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

import java.util.List;

import static com.example.kathu228.shoplog.R.layout.item;
import static com.example.kathu228.shoplog.R.layout.item_header;

/**
 * Created by kathu228 on 7/12/17.
 */

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private List<Item> mlist;
    ShopList listTest;

    public ItemAdapter(List<Item> mlist, ShopList listTest) {
        this.mlist = mlist;
        this.listTest = listTest;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = inflateView(parent);
        View view;

        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(item, parent, false);
                return new ItemViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(item_header, parent, false);
                return new HeaderViewHolder(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_header, parent, false);
                return new CompletedHeaderViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid viewtype," + viewType);
        }
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
                case 2:
                    ((CompletedHeaderViewHolder) holder).tvCompletedHeader.setText(item.getBody());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid itemtype," + item.getType());
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

    // Allows user to move items into categories by dragging
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Item currentItem = mlist.get(fromPosition);
        Item newSegItem = mlist.get(toPosition);
        if (currentItem.isItem()){
            int newPosition = 0;
            // cannot move checked items
            if (currentItem.isChecked()){
                return;
            }

            // if toPosition is in completed, make item checked
            if (newSegItem.isChecked()){
                currentItem.setChecked(true, null);
                newPosition = mlist.size();
                mlist.remove(fromPosition);
                notifyItemRemoved(fromPosition);
            }
            else{
                Segment newSeg = newSegItem.getSegment();
                Segment currentSeg = currentItem.getSegment();
                // if already same categories, do nothing
                if (newSeg==currentSeg){
                    return;
                }
                mlist.remove(fromPosition);
                notifyItemRemoved(fromPosition);
                // change item's segment
                currentItem.setSegment(newSeg,null);
                // if new segment is uncategorized, add to beginning
                // else, add right under the segment header
                if (newSeg.getName().equals("uncategorized_segment")){
                    newPosition = 0;
                }
                else{
                    newPosition = 1 + mlist.indexOf(newSeg.getHeader());
                }
            }

            mlist.add(newPosition,currentItem);
            notifyItemInserted(newPosition);
        }

    }

    // Allows user to remove checked item or check an unchecked item by swiping
    @Override
    public void onItemDismiss(int position) {
        Item item = mlist.get(position);
        // only items allowed
        if (item.isItem()) {
            // if item is checked, delete
            // TODO: snackbar undo
            // else, move under completed
            if (item.isChecked()) {
//            undoDelete(item, position, View.inflate(,R.layout.item,item));
//            deleteItem(item);
                item.setVisible(false, null);
                listTest.removeItem(item, null);
                deleteItem(position);
//            deleteItemFromList(item, position);
            } else {
                item.setChecked(true, null);
                mlist.add(item);
                mlist.remove(position);
                notifyItemMoved(position,mlist.size()-1);
            }
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
    public class ItemViewHolder extends BaseAdapter.ViewHolder {
        public CheckBox cbItem;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cbItem = (CheckBox) itemView.findViewById(R.id.cbItem);

            //adds onclicklistener to checkbox, to update object's state once you check it
            cbItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCheckbox(mlist.get(getAdapterPosition()), getAdapterPosition(), v);
                }
            });


        }

        // checks and unchecks checkbox, while saving the object's boolean state on server

        public void handleCheckbox(final Item item, final int position, final View v){
            if (item.isChecked()){
                item.setChecked(false, null);
                mlist.remove(position);
                mlist.add(0, item);
                cbItem.setChecked(false);
                notifyItemMoved(position, 0);
            }
            else{
                item.setChecked(true,null);
                mlist.remove(position);
                mlist.add(item);
                cbItem.setChecked(true);
                notifyItemMoved(position, mlist.size() - 1);

            }
        }
    }

    public class HeaderViewHolder extends BaseAdapter.ViewHolder{
        public TextView tvHeader;
        public HeaderViewHolder(View itemView) {
            super(itemView);

            tvHeader = (TextView)itemView.findViewById(R.id.tvHeader);

        }
    }
    public class CompletedHeaderViewHolder extends BaseAdapter.ViewHolder {
        public TextView tvCompletedHeader;
        public ImageButton ibDelete;

        public CompletedHeaderViewHolder(View itemView) {
            super(itemView);

            tvCompletedHeader = (TextView) itemView.findViewById(R.id.tvCompletedHeader);
            ibDelete = (ImageButton) itemView.findViewById(R.id.ibDelete);

            // asks user if want to delete all items in completed
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

                    // set title
                    alertDialogBuilder.setTitle("Delete completed");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Are you sure you want to delete all completed items?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, delete all completed items
                                    deleteItems(getAdapterPosition() + 1);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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

        //deletes all items starting at position
        public void deleteItems(final int position) {
            int len = mlist.size();
            for (int i = position; i < len; i++) {
                Item deletedItem = mlist.get(position);
                deleteItemFromList(deletedItem);
                deleteItem(position);
            }
        }
    }

    // Remove an item from mList at position
    public void deleteItem(final int position) {
        mlist.remove(position);
        notifyItemRemoved(position);
        //undoDelete(item, position, v);

    }

    // Remove an item from the shoplist
    private void deleteItemFromList(final Item item) {
        listTest.removeItem(item,null);
    }

    // Insert item to mList at position
    public void addItem(final Item item, final int position){
        mlist.add(position, item);
        notifyItemInserted(position);
    }


    // Add an item to the shoplist
    private void addItemToList (final Item item, final int position) {
        item.setVisible(true,null);
    }

    // if click undo in snackbar, item will reappear in list unchecked
    public void undoDelete(final Item item, final int position, View v){
        Snackbar.make(v, item.getBody()+" deleted", Snackbar.LENGTH_LONG)
                .addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (!mlist.contains(item)){
                            deleteItemFromList(item);
                        }
                    }
                })
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addItem(item, 0);
                        Snackbar snackbar1 = Snackbar.make(v, item.getBody()+" restored!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                })
                .show();
    }


}
