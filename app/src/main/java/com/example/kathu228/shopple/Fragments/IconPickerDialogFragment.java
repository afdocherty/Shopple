package com.example.kathu228.shopple.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kathu228.shopple.Models.ShopList;
import com.example.kathu228.shopple.R;

/**
 * Created by afdoch on 8/3/17.
 */

public class IconPickerDialogFragment extends DialogFragment {
    private TextView mTitle;
    private Button mConfirm;
    private Button mCancel;
    private IconPickerDialogListener mListener;
    private ImageView groceryBagIcon;
    private ImageView soloCupIcon;
    private ImageView bbqGrillIcon;
    private int iconNum;

    public static final int ALPHA_FULL = 255;
    public static final int ALPHA_DIMMED = 86;
    public static final int ICON_NUM_GROCERY = 0;
    public static final int ICON_NUM_PARTY = 1;
    public static final int ICON_NUM_BBQ = 2;

    public void setListener(IconPickerDialogListener listener) {
        mListener = listener;
    }

    // Defines the listener interface
    public interface IconPickerDialogListener {
        void onFinishIconPickerDialog(Boolean yes, int iconNum, String title, ShopList mshopList);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Boolean yes, int iconNum, String title, @Nullable ShopList mshopList) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        //YesNoDialogListener listener = (YesNoDialogListener) getTargetFragment();
        mListener.onFinishIconPickerDialog(yes, iconNum, title, mshopList);
        dismiss();
    }

    public static IconPickerDialogFragment newInstance(String title, @Nullable ShopList mshopList) {
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
        // Fetch arguments from bundle and set title
        final String title = getArguments().getString(YesNoDialogFragment.TITLE);
        final ShopList shopList = getArguments().getParcelable(YesNoDialogFragment.SHOPLIST);

        // Get field from view
        mTitle = (TextView) view.findViewById(R.id.tvTitle);
        mCancel = (Button) view.findViewById(R.id.bCancel);
        mConfirm = (Button) view.findViewById(R.id.bConfirm);

        // Get icon number
        iconNum = shopList.getIconNum();
        groceryBagIcon = (ImageView) view.findViewById(R.id.icon_groceries);
        groceryBagIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusToGrocery();
            }
        });
        soloCupIcon = (ImageView) view.findViewById(R.id.icon_party);
        soloCupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusToParty();
            }
        });
        bbqGrillIcon = (ImageView) view.findViewById(R.id.icon_bbq);
        bbqGrillIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusToBBQ();
            }
        });
        setCurrentFocus(iconNum);

        mTitle.setText(title);
        // See if user selects yes or no
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(false, iconNum, title, shopList);
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirm.setEnabled(false);
                sendBackResult(true, iconNum, title, shopList);
            }
        });
    }

//    public void onActivityCreated(Bundle savedInstanceState){
//        super.onActivityCreated(savedInstanceState);
//        final String title = getArguments().getString(YesNoDialogFragment.TITLE);
//        final ShopList shopList = getArguments().getParcelable(YesNoDialogFragment.SHOPLIST);
//
//        // viewcreated code
//        mCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendBackResult(false, iconNum, title,shopList);
//                //mListener.onFinishYesNoDialog(false,title, item);
//            }
//        });
//        mConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendBackResult(true, iconNum, title,shopList);
////                mListener.onFinishYesNoDialog(true, title, item);
//            }
//        });
//    }

    private void setCurrentFocus(int currIconNum) {
        switch (currIconNum) {
            case ICON_NUM_GROCERY:
                setFocusToGrocery();
                break;
            case ICON_NUM_PARTY:
                setFocusToParty();
                break;
            case ICON_NUM_BBQ:
                setFocusToBBQ();
                break;
        }
    }

    private void setFocusToGrocery() {
        groceryBagIcon.setImageAlpha(ALPHA_FULL);
        soloCupIcon.setImageAlpha(ALPHA_DIMMED);
        bbqGrillIcon.setImageAlpha(ALPHA_DIMMED);
        iconNum = ICON_NUM_GROCERY;
    }

    private void setFocusToParty() {
        groceryBagIcon.setImageAlpha(ALPHA_DIMMED);
        soloCupIcon.setImageAlpha(ALPHA_FULL);
        bbqGrillIcon.setImageAlpha(ALPHA_DIMMED);
        iconNum = ICON_NUM_PARTY;
    }

    private void setFocusToBBQ() {
        groceryBagIcon.setImageAlpha(ALPHA_DIMMED);
        soloCupIcon.setImageAlpha(ALPHA_DIMMED);
        bbqGrillIcon.setImageAlpha(ALPHA_FULL);
        iconNum = ICON_NUM_BBQ;
    }

}
