package com.example.kathu228.shoplog.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
    private ArrayList<Boolean> isChecked;

    private RelativeLayout rlSuccess;
    private RelativeLayout rlFailure;
    private RelativeLayout editView;
    private RelativeLayout btns;

    private EditText etEditItemName;

    private CheckBox allSelected;

    private Button cancelBtn;
    private Button addBtn;
    private Button retryBtn;
    private Button cancelEditBtn;
    private Button doneEditBtn;

    private OcrItemListDialogListener mListener;

    public void setListener(OcrItemListDialogListener listener) {
        mListener = listener;
    }

    // Defines the listener interface
    public interface OcrItemListDialogListener {
        void onFinishOcrItemListDialog(List<String> addItems);
        void onRetryOcr();
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

        isChecked = new ArrayList<Boolean>();

        rvOcrItems = (RecyclerView) v.findViewById(R.id.rvOCRItem);

        rlFailure = (RelativeLayout) v.findViewById(R.id.rlFailure);
        rlSuccess = (RelativeLayout) v.findViewById(R.id.rlAddItems);

        btns = (RelativeLayout) v.findViewById(R.id.rlButtons);
        cancelBtn = (Button) v.findViewById(R.id.bCancelAdd);
        addBtn = (Button) v.findViewById(R.id.bAdd);
        retryBtn = (Button) v.findViewById(R.id.bRetry);

        allSelected = (CheckBox) v.findViewById(R.id.cbSelect);

        editView = (RelativeLayout) v.findViewById(R.id.rlEditItem);
        etEditItemName = (EditText) v.findViewById(R.id.etEditItem);
        cancelEditBtn = (Button) v.findViewById(R.id.bCancelEdit);
        doneEditBtn = (Button) v.findViewById(R.id.bConfirmEdit);

        if (items.size()==0){
            rlSuccess.setVisibility(View.GONE);
            rlFailure.setVisibility(View.VISIBLE);
        }
        else{
            rlSuccess.setVisibility(View.VISIBLE);
            rlFailure.setVisibility(View.GONE);

            for (String item: items){
                isChecked.add(Boolean.TRUE);
            }

            //setadapter
            adapter = new OcrItemAdapter(items, isChecked, R.layout.item_ocr, OcrItemListDialogFragment.this, getActivity());
            rvOcrItems.setLayoutManager(new LinearLayoutManager(getContext()));
            rvOcrItems.setAdapter(adapter);
        }



        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBtn.setEnabled(false);
                addBtn.setEnabled(false);
                dismiss();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBtn.setEnabled(false);
                addBtn.setEnabled(false);
                sendBackResult(adapter.getAddedItems());
//                addItems();
//                sendBackResult(itemsAdded);
            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRetryOcr();
                dismiss();
            }
        });

        allSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedText(allSelected.isChecked());
                adapter.selectUnselect(allSelected.isChecked());
            }
        });

        return v;
    }

    // makes editing layout visible for editing item
    public void editMode(final String item){
        etEditItemName.setText(item);
        editView.setVisibility(View.VISIBLE);
        rlSuccess.setVisibility(View.GONE);
        btns.setVisibility(View.GONE);
        etEditItemName.requestFocus();
        etEditItemName.selectAll();
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etEditItemName, InputMethodManager.SHOW_IMPLICIT);

        // Itemclicklistener on done and cancel
        // If press cancel, go back to view with items
        cancelEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(etEditItemName.getWindowToken(), 0);
                editView.setVisibility(View.GONE);
                rlSuccess.setVisibility(View.VISIBLE);
                btns.setVisibility(View.VISIBLE);

            }
        });
        // If press done, go back to view with item changed
        doneEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etEditItemName.getText().toString();
                if (!body.equals("")){
                    adapter.changeItemName(item,body);
                    imm.hideSoftInputFromWindow(etEditItemName.getWindowToken(), 0);
                    editView.setVisibility(View.GONE);
                    rlSuccess.setVisibility(View.VISIBLE);
                    btns.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void changeSelectedText(Boolean isSelected){
        if (isSelected){
            allSelected.setText("Unselect All");
        }
        else{
            allSelected.setText("Select All");
        }

    }

    public void unselect(){
        allSelected.setChecked(false);
        allSelected.setText("Select All");
    }
}
