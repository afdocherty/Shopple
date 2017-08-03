package com.example.kathu228.shoplog.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.kathu228.shoplog.Fragments.OcrItemListDialogFragment;
import com.example.kathu228.shoplog.R;

import java.util.List;

/**
 * Created by kathu228 on 8/2/17.
 */

public class OcrItemAdapter extends BaseAdapter<OcrItemAdapter.ViewHolder, String> {
    OcrItemListDialogFragment fragmentInstance;
    public boolean isSelected;
    public Context context;

    public OcrItemAdapter(List<String> mlist, int itemViewReference, OcrItemListDialogFragment fragmentInstance, Context context) {
        super(mlist, itemViewReference);
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
        if (isSelected){
            holder.cbItem.setChecked(true);
        }
        else{
            holder.cbItem.setChecked(false);
        }
    }


    public class ViewHolder extends BaseAdapter.ViewHolder {
        public CheckBox cbItem;

        public ViewHolder(View view) {
            super(view);

            cbItem = (CheckBox) itemView.findViewById(R.id.cbItem);
            cbItem.setChecked(true);

            cbItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentInstance.turnSelectOff();
                    if (((CheckBox)v).isChecked()){
                        cbItem.setChecked(true);
                        fragmentInstance.addItem(mlist.get(getAdapterPosition()));
                    }
                    else{
                        cbItem.setChecked(false);
                        fragmentInstance.removeItem(mlist.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public void selectAll(){
        isSelected = true;
        notifyDataSetChanged();
    }

    public void unselectAll(){
        isSelected = false;
        notifyDataSetChanged();
    }
}
