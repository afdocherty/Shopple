package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

/**
 * Created by kathu228 on 7/30/17.
 */

public class YesNoDialogFragment extends DialogFragment{
    private TextView mTitle;
    private TextView mQuestion;
    private Button mYes;
    private Button mNo;
    private YesNoDialogListener mListener;
    private ImageView mIcon;

    public static final String TITLE = "title";
    public static final String QUESTION = "question";
    public static final String ITEM = "item";
    public static final String SHOPLIST = "shoplist";
    public static final String TYPE = "type";

    public static final int DELETECOMPLETED = 0;
    public static final int LOGOUT = 1;
    public static final int LEAVELIST = 2;
    public static final int CLEARCATEGORY = 3;

    public void setListener(YesNoDialogListener listener) {
        mListener = listener;
    }

    // Defines the listener interface
    public interface YesNoDialogListener {
        void onFinishYesNoDialog(Boolean yes, int type, Item mitem, ShopList mshopList);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Boolean yes, int type, @Nullable Item mitem, @Nullable ShopList mshopList) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        //YesNoDialogListener listener = (YesNoDialogListener) getTargetFragment();
        mListener.onFinishYesNoDialog(yes, type, mitem, mshopList);
        dismiss();
    }

//    public YesNoDialogFragment(){
//    }

    public static YesNoDialogFragment newInstance(String title, String question, @Nullable Item mitem, @Nullable ShopList mshopList, int mtype) {
        YesNoDialogFragment frag = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(QUESTION, question);
        args.putParcelable(ITEM, mitem);
        args.putParcelable(SHOPLIST, mshopList);
        args.putInt(TYPE, mtype);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.fragment_yesno_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mTitle = (TextView) view.findViewById(R.id.tvTitle);
        mQuestion = (TextView) view.findViewById(R.id.tvQuestion);
        mNo = (Button) view.findViewById(R.id.bNo);
        mYes = (Button) view.findViewById(R.id.bYes);
        mIcon = (ImageView) view.findViewById(R.id.ivIcon);
        // Fetch arguments from bundle and set title/question
        final String title = getArguments().getString(TITLE);
        String question = getArguments().getString(QUESTION);
        final Item item = getArguments().getParcelable(ITEM);
        final ShopList shopList = getArguments().getParcelable(SHOPLIST);
        final int type = getArguments().getInt(TYPE);
        mTitle.setText(title);
        mQuestion.setText(question);
        switch (getArguments().getInt(TYPE)){
            case 0:
                mIcon.setImageResource(R.drawable.ic_delete_sweep);
                break;
            case 1:
                mIcon.setImageResource(R.drawable.logout_variant);
                break;
            case 2:
                mIcon.setImageResource(R.drawable.account_remove);
                break;
            case 3:
                mIcon.setImageResource(R.drawable.folder_remove);
                break;
        }
        mIcon.setColorFilter(ContextCompat.getColor(getActivity(),R.color.white));
        // See if user selects yes or no
        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(false, type, item, shopList);
            }
        });
        mYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(true, type, item, shopList);
            }
        });
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        final String title = getArguments().getString(TITLE);
        final Item item = getArguments().getParcelable(ITEM);
        final ShopList shopList = getArguments().getParcelable(SHOPLIST);
        final int type = getArguments().getInt(TYPE);
        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(false,type,item,shopList);
                //mListener.onFinishYesNoDialog(false,title, item);
            }
        });
        mYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(true,type,item,shopList);
//                mListener.onFinishYesNoDialog(true, title, item);
            }
        });
    }

}
