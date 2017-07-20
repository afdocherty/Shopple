package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by afdoch on 7/20/17.
 */

public class CollaboratorsDialogFragment extends DialogFragment {

    private ArrayList<ParseUser> otherUsers;

    public static CollaboratorsDialogFragment newInstance(String shopListObjectId) {
        CollaboratorsDialogFragment dialogFragment = new CollaboratorsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ShopList.SHOPLIST_TAG, shopListObjectId);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collab_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ShopList.getShopListById(getArguments().getString(ShopList.SHOPLIST_TAG));
    }
}
