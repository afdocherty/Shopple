package com.example.kathu228.shoplog.Fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kathu228.shoplog.R;

/**
 * Created by kathu228 on 7/30/17.
 */

public class DeleteCompletedDialogFragment extends DialogFragment{
    private TextView tvTitle;
    private TextView tvQuestion;
    private Button bYes;
    private Button bNo;

    // Defines the listener interface
    public interface DeleteCompletedDialogListener {
        void onFinishDeleteCompletedDialog(Boolean deleteCompleted);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Boolean deleteCompleted) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        DeleteCompletedDialogListener listener = (DeleteCompletedDialogListener) getTargetFragment();
        listener.onFinishDeleteCompletedDialog(deleteCompleted);
        dismiss();
    }

    public DeleteCompletedDialogFragment(){
    }

    public static DeleteCompletedDialogFragment newInstance(String title) {
        DeleteCompletedDialogFragment frag = new DeleteCompletedDialogFragment();
        Bundle args = new Bundle();
        args.putString(YesNoDialogFragment.TITLE, title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yesno_dialog, container);
    }



}
