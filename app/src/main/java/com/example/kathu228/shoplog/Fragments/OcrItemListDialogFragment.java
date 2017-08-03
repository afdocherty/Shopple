package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.kathu228.shoplog.Helpers.OcrItemAdapter;
import com.example.kathu228.shoplog.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kathu228 on 8/2/17.
 */

public class OcrItemListDialogFragment extends DialogFragment{
    private RecyclerView rvOcrItems;
    private OcrItemAdapter adapter;
    private ArrayList<String> items;

    private Button cancelBtn;
    private Button addBtn;
    private CheckBox selectBox;

    private List<String> namesAdded;

    private OcrItemListDialogListener mListener;

    public void setListener(OcrItemListDialogListener listener) {
        mListener = listener;
    }

    // Defines the listener interface
    public interface OcrItemListDialogListener {
        void onFinishOcrItemListDialog(List<String> addItems);
    }



    // Call this method to send the data back to the parent fragment
    public void sendBackResult(List<String> addItems) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        mListener.onFinishOcrItemListDialog(addItems);
        dismiss();
    }


    public OcrItemListDialogFragment(){
    }

    public static OcrItemListDialogFragment newInstance(ArrayList<String> items) {
        OcrItemListDialogFragment frag = new OcrItemListDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("newItems", items);
        frag.setArguments(args);
        return frag;
    }

    // this method create view for your Dialog
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.fragment_ocritemlist_dialog, container, false);
        // items keeps track of all the new items from ocr
        items = new ArrayList<String>();
        items = getArguments().getStringArrayList("newItems");

        rvOcrItems = (RecyclerView) v.findViewById(R.id.rvOCRItem);

        //setadapter
        adapter = new OcrItemAdapter(items, R.layout.item, OcrItemListDialogFragment.this, getActivity());
        rvOcrItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOcrItems.setAdapter(adapter);

        // namesAdded are all the names of the items selected
        namesAdded = new ArrayList<String>();

        for (String item: items){
            namesAdded.add(item);
        }

        cancelBtn = (Button) v.findViewById(R.id.bCancelAdd);
        addBtn = (Button) v.findViewById(R.id.bAdd);
        selectBox = (CheckBox) v.findViewById(R.id.cbSelect);

        adapter.selectAll();

        selectBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectBox.isChecked()){
                    selectBox.setText("Unselect All");
                    adapter.selectAll();
                }
                else{
                    selectBox.setText("Select All");
                    adapter.unselectAll();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(namesAdded);
//                addItems();
//                sendBackResult(itemsAdded);
            }
        });
        return v;
    }

    // add item name to namesAdded list
    public void addItem(String itemName) {
        namesAdded.add(itemName);
    }

    // change item name
    public void changeItem(int pos, String newName) {
        namesAdded.remove(pos);
        namesAdded.add(pos, newName);
    }

    // remove item from namesAdded list
    public void removeItem(String itemName){
        namesAdded.remove(itemName);
    }

    // if click something, change to unselected
    public void turnSelectOff(){
        selectBox.setChecked(false);
        selectBox.setText("Select All");
    }
}
