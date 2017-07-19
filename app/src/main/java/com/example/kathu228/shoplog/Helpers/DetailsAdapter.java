package com.example.kathu228.shoplog.Helpers;

import android.view.View;
import android.view.ViewGroup;

import com.example.kathu228.shoplog.Fragments.ListDetailsFragment;
import com.example.kathu228.shoplog.Helpers.BaseAdapter.ViewHolder;

import java.util.List;

/**
 * Created by kathu228 on 7/19/17.
 */

public class DetailsAdapter extends BaseAdapter<ViewHolder,String>  {
    ListDetailsFragment fragmentInstance;

    public DetailsAdapter(List<String> mlist, int itemViewReference, ListDetailsFragment fragmentInstance) {
        super(mlist, itemViewReference);
        this.fragmentInstance=fragmentInstance;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflateView(parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }
}
