package com.example.kathu228.shoplog.Helpers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kathu228.shoplog.R;

import java.util.List;

/**
 * Created by afdoch on 7/13/17.
 */

public class ListlistAdapter extends BaseAdapter<ListlistAdapter.ViewHolder, com.example.kathu228.shoplog.Models.List> {

    public ListlistAdapter(List<com.example.kathu228.shoplog.Models.List> mlist, int itemViewReference) {
        super(mlist, itemViewReference);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflateView(parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        com.example.kathu228.shoplog.Models.List list = mlist.get(position);

        holder.tvListName.setText(list.getName());
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the list layout for fast access
    public class ViewHolder extends BaseAdapter.ViewHolder{

        public TextView tvListName;

        public ViewHolder(View itemView){
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvListName = (TextView) itemView.findViewById(R.id.tvListName);
        }
    }
}
