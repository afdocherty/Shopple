package com.example.kathu228.shoplog.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

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
    private boolean isEditing;

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
        holder.etItem.setText(name);
        holder.etItem.setVisibility(View.GONE);
        holder.ibEdit.setVisibility(View.VISIBLE);
        holder.ibEditing.setVisibility(View.GONE);

        if (isSelected){
            holder.cbItem.setChecked(true);
        }
        else{
            holder.cbItem.setChecked(false);
        }
    }


    public class ViewHolder extends BaseAdapter.ViewHolder {
        public CheckBox cbItem;
        public EditText etItem;
        public ImageButton ibEdit;
        public ImageButton ibEditing;

        public ViewHolder(View view) {
            super(view);

            cbItem = (CheckBox) itemView.findViewById(R.id.cbItem);
            cbItem.setChecked(true);

            etItem = (EditText) itemView.findViewById(R.id.etChangeItem);


            ibEdit = (ImageButton)  itemView.findViewById(R.id.ibMarker1);
            ibEditing = (ImageButton) itemView.findViewById(R.id.ibMarker2);

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

            ibEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ibEditing.setVisibility(View.VISIBLE);
                    etItem.setVisibility(View.VISIBLE);
                    etItem.requestFocus();
                    etItem.selectAll();
                    InputMethodManager imm = (InputMethodManager) fragmentInstance.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etItem, InputMethodManager.SHOW_IMPLICIT);
                    ibEdit.setVisibility(View.INVISIBLE);

                }
            });

            ibEditing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String body = etItem.getText().toString();
                    if (!body.equals("")){
                        int pos = getAdapterPosition();
                        fragmentInstance.changeItem(pos,etItem.getText().toString());
                        mlist.remove(pos);
                        mlist.add(pos,body);
                        ibEdit.setVisibility(View.VISIBLE);
                        notifyItemChanged(pos);
                        ibEditing.setVisibility(View.GONE);
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
