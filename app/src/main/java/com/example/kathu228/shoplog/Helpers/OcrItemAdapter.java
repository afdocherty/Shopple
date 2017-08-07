package com.example.kathu228.shoplog.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.example.kathu228.shoplog.Fragments.OcrItemListDialogFragment;
import com.example.kathu228.shoplog.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kathu228 on 8/2/17.
 */

public class OcrItemAdapter extends BaseAdapter<OcrItemAdapter.ViewHolder, String> {
    OcrItemListDialogFragment fragmentInstance;
    public Context context;
    public ArrayList<Boolean> isChecked;

    public Boolean lastItemIsChecked;
    public CheckBox lastItem;

    public OcrItemAdapter(List<String> mlist, ArrayList<Boolean> checked, int itemViewReference, OcrItemListDialogFragment fragmentInstance, Context context) {
        super(mlist, itemViewReference);
        this.isChecked = checked;
        this.fragmentInstance=fragmentInstance;
        this.context = context;
    }

    @Override
    public View inflateView(ViewGroup parent) {
        //context=(AppCompatActivity) parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(itemViewReference,parent,false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflateView(parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = mlist.get(position);
        holder.cbItem.setText(name);
        holder.ibEdit.setVisibility(View.VISIBLE);
        holder.cbItem.setChecked(isChecked.get(position));
    }


    public class ViewHolder extends BaseAdapter.ViewHolder {
        public CheckBox cbItem;
        public ImageButton ibEdit;

        public ViewHolder(View view) {
            super(view);

            cbItem = (CheckBox) itemView.findViewById(R.id.cbItem);
            //cbItem.setChecked(true);


            ibEdit = (ImageButton)  itemView.findViewById(R.id.ibMarker1);

            cbItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentInstance.unselect();
                    if (((CheckBox)v).isChecked()){
                        cbItem.setChecked(true);
                        isChecked.set(getAdapterPosition(),true);
                    }
                    else{
                        cbItem.setChecked(false);
                        isChecked.set(getAdapterPosition(),false);
                    }
                }
            });

            ibEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentInstance.editMode(mlist.get(getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public void changeItemName(String oldItem, String newItem){
        int pos = mlist.indexOf(oldItem);
        mlist.remove(pos);
        mlist.add(pos,newItem);
        notifyItemChanged(pos);
    }

    public void selectUnselect(Boolean allSelected){
        for (int i = 0; i < isChecked.size(); i++){
            isChecked.set(i,allSelected);
        }
        notifyDataSetChanged();
    }

    public ArrayList<String> getAddedItems(){
        ArrayList<String> addedItems = new ArrayList<String>();
        for (int i = 0; i < mlist.size(); i++){
            if (isChecked.get(i)){
                addedItems.add(0,mlist.get(i));
            }
        }
        return addedItems;
    }
}
