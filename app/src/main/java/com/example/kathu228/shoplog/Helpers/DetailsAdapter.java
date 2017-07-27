package com.example.kathu228.shoplog.Helpers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kathu228.shoplog.Fragments.ListDetailsFragment;
import com.example.kathu228.shoplog.Models.Query;
import com.example.kathu228.shoplog.R;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by kathu228 on 7/19/17.
 */

public class DetailsAdapter extends BaseAdapter<DetailsAdapter.ViewHolder,ParseUser>  {

    ListDetailsFragment fragmentInstance;

    public DetailsAdapter(List<ParseUser> mlist, int itemViewReference, ListDetailsFragment fragmentInstance) {
        super(mlist, itemViewReference);
        this.fragmentInstance=fragmentInstance;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflateView(parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParseUser user = mlist.get(position);
        holder.tvCollab.setText(Query.getNameOfUser(user));
    }

    // ViewHolder Class
    public class ViewHolder extends BaseAdapter.ViewHolder{

        public TextView tvCollab;

        public ViewHolder(View itemView){
            super(itemView);

            tvCollab = (TextView) itemView.findViewById(R.id.tvCollab);
        }
    }
}
