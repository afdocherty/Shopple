package com.example.kathu228.shoplog.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

/**
 * Created by kathu228 on 8/1/17.
 */

public class EdittextDialogFragment extends DialogFragment{
    private TextView mTitle;
    private EditText mText;
    private Button mOk;
    private Button mCancel;
    private EdittextDialogListener mListener;

    public void setListener(EdittextDialogListener listener) {
        mListener = listener;
    }

    // Defines the listener interface
    public interface EdittextDialogListener {
        void onFinishEdittextDialog(Boolean yes, String title, ShopList mshopList, String newName);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Boolean yes, String title, ShopList mshopList, String newName) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        //YesNoDialogListener listener = (YesNoDialogListener) getTargetFragment();
        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mText.getWindowToken(), 0);
        mListener.onFinishEdittextDialog(yes, title, mshopList, newName);
        dismiss();
    }

    public EdittextDialogFragment(){
    }

    public static EdittextDialogFragment newInstance(String title, ShopList mshopList) {
        EdittextDialogFragment frag = new EdittextDialogFragment();
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
        return inflater.inflate(R.layout.fragment_edittext_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mTitle = (TextView) view.findViewById(R.id.tvTitle);
        mText = (EditText) view.findViewById(R.id.etName);
        mCancel = (Button) view.findViewById(R.id.bCancel);
        mOk = (Button) view.findViewById(R.id.bOk);
        // Fetch arguments from bundle and set title
        final String title = getArguments().getString(YesNoDialogFragment.TITLE);
        final ShopList shopList = getArguments().getParcelable(YesNoDialogFragment.SHOPLIST);
        mText.setText(shopList.getName());
        mTitle.setText(title);
        //mText.requestFocus();
        mText.setSelectAllOnFocus(true);
        mText.selectAll();
        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        //Adds item from edittext if press enter or done on keyboard
        mText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String name = mText.getText().toString();
                    if (!(name.replaceAll("\\s+","")).equals("")) {
                        sendBackResult(true, title, shopList, name);
                    }
                }
                return false;
            }
        });
        // See if user selects yes or no
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mText.getText().toString();
                sendBackResult(false, title, shopList,name);
            }
        });
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOk.setEnabled(false);
                String name = mText.getText().toString();
                if (!(name.replaceAll("\\s+","")).equals("")) {
                    sendBackResult(true, title, shopList, name);
                }
            }
        });
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        final String title = getArguments().getString(YesNoDialogFragment.TITLE);
        final ShopList shopList = getArguments().getParcelable(YesNoDialogFragment.SHOPLIST);
        //Adds item from edittext if press enter or done on keyboard
        mText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String name = mText.getText().toString();
                    if (!(name.replaceAll("\\s+","")).equals("")) {
                        sendBackResult(true, title, shopList, name);
                    }
                }
                return false;
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mText.getText().toString();
                sendBackResult(false,title,shopList,name);
            }
        });
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOk.setEnabled(false);
                String name = mText.getText().toString();
                if (!(name.replaceAll("\\s+","")).equals("")) {
                    sendBackResult(true, title, shopList, name);
                }
            }
        });
    }
}
