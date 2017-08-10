package com.example.kathu228.shopple.Fragments;

import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.kathu228.shopple.R;

/**
 * Created by kathu228 on 8/2/17.
 */

public class TripDialogFragment extends DialogFragment {
    private RelativeLayout rlNearby;
    private RelativeLayout rlFavStore;
    private ImageView btnClose;
    private TripDialogListener mListener;
    private RippleDrawable rippleDrawable1;
    private RippleDrawable rippleDrawable2;

    public void setListener(TripDialogListener listener) {
        mListener = listener;
    }

    // Defines the listener interface
    public interface TripDialogListener {
        void onFinishTripDialog(Boolean favStore);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Boolean favStore) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        // TripDialogListener listener = (TripDialogListener) getTargetFragment();
        mListener.onFinishTripDialog(favStore);
        dismiss();
    }


    public TripDialogFragment(){
    }

    public static TripDialogFragment newInstance() {
        TripDialogFragment frag = new TripDialogFragment();
        Bundle args = new Bundle();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        rlNearby = (RelativeLayout) view.findViewById(R.id.rlNearbyStores);
        rlFavStore = (RelativeLayout) view.findViewById(R.id.rlMyStore);
        btnClose = (ImageView) view.findViewById(R.id.ivCloseDialog);
        rippleDrawable1 = (RippleDrawable) rlNearby.getBackground();
        rippleDrawable2 = (RippleDrawable) rlFavStore.getBackground();
        rlNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleDrawable1.setColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.lightGray)));
                sendBackResult(false);
            }
        });
        rlFavStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleDrawable2.setColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.lightGray)));
                sendBackResult(true);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        rlNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlNearby.setClickable(false);
                rlFavStore.setClickable(false);
                rippleDrawable1.setColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.lightGray)));
                sendBackResult(false);
            }
        });
        rlFavStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlFavStore.setClickable(false);
                rlNearby.setClickable(false);
                rippleDrawable2.setColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.lightGray)));
                sendBackResult(true);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
