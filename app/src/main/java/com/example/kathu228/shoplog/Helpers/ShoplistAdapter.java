package com.example.kathu228.shoplog.Helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
        holder.etListName.setText(shopList.getName());
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
        alertDialogBuilder.setTitle("Delete completed");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to delete all completed items?")
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
                        notifyDataSetChanged();
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
        public EditText etListName;
        public ViewSwitcher switcher;

        public ViewHolder(View itemView){
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvListName = (TextView) itemView.findViewById(R.id.tvListName);
            switcher = (ViewSwitcher) itemView.findViewById(R.id.vsListSwitcher);
            etListName = (EditText) itemView.findViewById(R.id.etListName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    switcher.showNext();
                    etListName.setSelectAllOnFocus(true);
                    etListName.selectAll();
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null){
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                    etListName.setOnEditorActionListener(new TextView.OnEditorActionListener(){
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if ((event != null) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || (event.getKeyCode()==KeyEvent.KEYCODE_BACK) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                                String body = etListName.getText().toString();
                                // Does not add empty item
                                if (!body.equals("")) {
                                    final ShopList shopList = mlist.get(getAdapterPosition());
                                    shopList.setName(body, null);
                                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(etListName.getWindowToken(), 0);
                                    tvListName.setText(body);
                                    switcher.showPrevious();
                                    etListName.setText(body);
                                }
                            }
                            return false;
                        }
                    });
                    return true;
                }
            });
        }
    }
}
