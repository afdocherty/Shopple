package com.example.kathu228.shoplog.Helpers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kathu228.shoplog.R;

import java.util.List;

/**
 * Created by fmonsalve on 7/12/17.
 *
 */

public class FriendlistAdapter extends BaseAdapter<FriendlistAdapter.ViewHolder,String> {

    public FriendlistAdapter(List mlist, int itemViewReference) {
        super(mlist, itemViewReference);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflateView(parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = mlist.get(position);

        holder.tvName.setText(name);
    }

    // ViewHolder Class
    public class ViewHolder extends BaseAdapter.ViewHolder{

        public TextView tvName;

        public ViewHolder(View itemView){
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }
}
