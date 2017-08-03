package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

/**
 * Created by afdoch on 8/3/17.
 */

public class IconPickerDialogFragment extends DialogFragment {
    private TextView mTitle;
    private Button mYes;
    private Button mNo;
    private IconPickerDialogListener mListener;

    public void setListener(IconPickerDialogListener listener) {
        mListener = listener;
    }

    // Defines the listener interface
    public interface IconPickerDialogListener {
        void onFinishYesNoDialog(Boolean yes, String title, ShopList mshopList);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Boolean yes, String title, @Nullable ShopList mshopList) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        //YesNoDialogListener listener = (YesNoDialogListener) getTargetFragment();
        mListener.onFinishYesNoDialog(yes, title, mshopList);
        dismiss();
    }

    public static IconPickerDialogFragment newInstance(String title, String question, @Nullable Item mitem, @Nullable ShopList mshopList) {
        IconPickerDialogFragment frag = new IconPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(YesNoDialogFragment.TITLE, title);
        args.putParcelable(YesNoDialogFragment.SHOPLIST, mshopList);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.fragment_icon_picker_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mTitle = (TextView) view.findViewById(R.id.tvTitle);
        mNo = (Button) view.findViewById(R.id.bNo);
        mYes = (Button) view.findViewById(R.id.bYes);
        // Fetch arguments from bundle and set title/question
        final String title = getArguments().getString(YesNoDialogFragment.TITLE);
        final ShopList shopList = getArguments().getParcelable(YesNoDialogFragment.SHOPLIST);
        mTitle.setText(title);
        // See if user selects yes or no
        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(false, title, shopList);
            }
        });
        mYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(true, title, shopList);
            }
        });
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        final String title = getArguments().getString(YesNoDialogFragment.TITLE);
        final ShopList shopList = getArguments().getParcelable(YesNoDialogFragment.SHOPLIST);
        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(false,title,shopList);
                //mListener.onFinishYesNoDialog(false,title, item);
            }
        });
        mYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(true,title,shopList);
//                mListener.onFinishYesNoDialog(true, title, item);
            }
        });
    }

}
