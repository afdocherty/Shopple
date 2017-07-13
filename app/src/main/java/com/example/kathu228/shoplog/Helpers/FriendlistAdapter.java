package com.example.kathu228.shoplog.Helpers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.kathu228.shoplog.Fragments.FriendlistFragment;
import com.example.kathu228.shoplog.R;

import java.util.List;

/**
 * Created by fmonsalve on 7/12/17.
 *
 */

public class FriendlistAdapter extends BaseAdapter<FriendlistAdapter.ViewHolder,String> {

    FriendlistFragment fragmentInstance;

    public FriendlistAdapter(List mlist, int itemViewReference, FriendlistFragment fragmentInstance) {
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
        String name = mlist.get(position);

        holder.tvName.setText(name);
        holder.cbName.setText(name);
    }

    // ViewHolder Class
    public class ViewHolder extends BaseAdapter.ViewHolder{

        public TextView tvName;
        public CheckBox cbName;

        public ViewHolder(View itemView){
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            cbName = (CheckBox) itemView.findViewById(R.id.cbName);
            cbName.setChecked(false);

            cbName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox)v).isChecked()){
                        cbName.setChecked(true);
                        fragmentInstance.addPerson(cbName.getText().toString());
                    }
                    else{
                        cbName.setChecked(false);
                        fragmentInstance.removePerson(cbName.getText().toString());
                    }
                }
            });
        }
    }
}
