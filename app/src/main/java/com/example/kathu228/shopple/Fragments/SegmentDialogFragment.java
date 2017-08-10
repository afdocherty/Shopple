package com.example.kathu228.shopple.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kathu228.shopple.Models.Item;
import com.example.kathu228.shopple.Models.Segment;
import com.example.kathu228.shopple.Models.ShopList;
import com.example.kathu228.shopple.R;

/**
 * Created by kathu228 on 7/23/17.
 * Pops up when adding a segment and asks for name of new segment
 */
//TODO: Check if category is adding when dialog "add" is clicked
public class SegmentDialogFragment extends DialogFragment {
    private EditText etNewSegment;
    private Button bCancelSegment;
    private Button bAddSegment;
    private ShopList shopList;

    // Defines the listener interface
    public interface SegmentDialogListener {
        void onFinishSegmentDialog(Item segHeader);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Item segHeader) {

        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        SegmentDialogListener listener = (SegmentDialogListener) getTargetFragment();
        listener.onFinishSegmentDialog(segHeader);
        dismiss();
    }


    public SegmentDialogFragment(){
    }

    public static SegmentDialogFragment newInstance(String shopListObjectId) {
        SegmentDialogFragment frag = new SegmentDialogFragment();
        Bundle args = new Bundle();
        args.putString("shopListId", shopListObjectId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_segment_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etNewSegment = (EditText) view.findViewById(R.id.etNewSegment);
        bCancelSegment = (Button) view.findViewById(R.id.bCancelSegment);
        bAddSegment = (Button) view.findViewById(R.id.bAddSegment);
        // Fetch arguments from bundle and find shoplist
        String shopListID = getArguments().getString("shopListId");
        shopList = shopList.getShopListById(shopListID);
        // Dismisses dialog when cancel
        bCancelSegment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bAddSegment.setEnabled(false);
                bCancelSegment.setEnabled(false);
                dismiss();
            }
        });
        // Adds new segment
        bAddSegment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bAddSegment.setEnabled(false);
                bCancelSegment.setEnabled(false);
                String segmentName = etNewSegment.getText().toString();
                if (!segmentName.replaceAll("\\s","").equals("")){
                    addSegment(segmentName);
                }
            }
        });
        etNewSegment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String segmentName = etNewSegment.getText().toString();
                if (!segmentName.replaceAll("\\s","").equals("")) {
                    if (EditorInfo.IME_ACTION_DONE == actionId) {
                        // Return input text back to activity through the implemented listener
                        // Close the dialog and return back to the parent activity
                        addSegment(segmentName);
//                        return true;
                    }
//                    return false;

                }
                return false;
            }
        });
        // Show soft keyboard automatically and request focus to field
        etNewSegment.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }
    public void addSegment(String segmentName){
        shopList.addSegment(segmentName, new Segment.SegmentCallback() {
            @Override
            public void done(Segment segment) {
                sendBackResult(segment.getHeader());
            }
        });
    }

}
