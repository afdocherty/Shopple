package com.example.kathu228.shopple.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.kathu228.shopple.Fragments.CollaboratorsDialogFragment;
import com.example.kathu228.shopple.Models.Query;
import com.example.kathu228.shopple.R;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by afdoch on 7/27/17.
 */

public class ModalFriendListAdapter extends BaseAdapter<ModalFriendListAdapter.ViewHolder,ParseUser> {

    CollaboratorsDialogFragment fragmentInstance;
    private Context ownerActivity;

    public ModalFriendListAdapter(List<ParseUser> mlist, int itemViewReference, CollaboratorsDialogFragment fragmentInstance, Context ownerActivity) {
        super(mlist, itemViewReference);
        this.fragmentInstance=fragmentInstance;
        this.ownerActivity = ownerActivity;
    }

    @Override
    public View inflateView(ViewGroup parent) {
            //context=(AppCompatActivity) parent.getContext();

            LayoutInflater inflater = LayoutInflater.from(ownerActivity);
            return inflater.inflate(itemViewReference,parent,false);
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
