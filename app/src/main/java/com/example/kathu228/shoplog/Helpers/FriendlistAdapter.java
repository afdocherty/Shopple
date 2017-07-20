package com.example.kathu228.shoplog.Helpers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.kathu228.shoplog.Fragments.FriendlistFragment;
import com.example.kathu228.shoplog.Models.Query;
import com.example.kathu228.shoplog.R;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by fmonsalve on 7/12/17.
 *
 */

public class FriendlistAdapter extends BaseAdapter<FriendlistAdapter.ViewHolder,ParseUser> {

    FriendlistFragment fragmentInstance;

    public FriendlistAdapter(List<ParseUser> mlist, int itemViewReference, FriendlistFragment fragmentInstance) {
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
        holder.cbName.setText(Query.getNameOfUser(user));
    }

    // ViewHolder Class
    public class ViewHolder extends BaseAdapter.ViewHolder{

        public CheckBox cbName;

        public ViewHolder(View itemView){
            super(itemView);

            cbName = (CheckBox) itemView.findViewById(R.id.cbName);
            cbName.setChecked(false);

            cbName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox)v).isChecked()){
                        cbName.setChecked(true);
                        fragmentInstance.addPerson(mlist.get(getAdapterPosition()));
                    }
                    else{
                        cbName.setChecked(false);
                        fragmentInstance.removePerson(mlist.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
