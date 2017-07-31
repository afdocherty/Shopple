package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

    public void setListener(YesNoDialogListener listener) {
        mListener = listener;
    }

    // Defines the listener interface
    public interface YesNoDialogListener {
        void onFinishYesNoDialog(Boolean yes, String title);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Boolean yes, String title) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        YesNoDialogListener listener = (YesNoDialogListener) getTargetFragment();
        listener.onFinishYesNoDialog(yes, title);
        dismiss();
    }

    public YesNoDialogFragment(){
    }

    public static YesNoDialogFragment newInstance(String title, String question) {
        YesNoDialogFragment frag = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("question", question);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        // Fetch arguments from bundle and set title/question
        final String title = getArguments().getString("title");
        String question = getArguments().getString("question");
        mTitle.setText(title);
        mQuestion.setText(question);
        // See if user selects yes or no
        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(false, title);
            }
        });
        mYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult(true, title);
            }
        });
    }

}
