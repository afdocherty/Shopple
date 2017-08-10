package com.example.kathu228.shopple.Helpers;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by fmonsalve on 7/12/17.
 */

public abstract class BaseAdapter<VH extends BaseAdapter.ViewHolder,ListType> extends RecyclerView.Adapter<VH> {

    public List<ListType> mlist;
    private AppCompatActivity context;

    protected int itemViewReference;

    //pass in the Tweets array into constructor
    //itemView reference would be R.layout.___ for reference to layout that
    //holds the items tht will be populated by the recyclerview
    public BaseAdapter(List<ListType> mlist, int itemViewReference){
        this.mlist=mlist;
        this.itemViewReference=itemViewReference;
    }

    //makes it easier to implement OnCreateViewHolder
    public View inflateView(ViewGroup parent) {
        context=(AppCompatActivity) parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(itemViewReference,parent,false);
    }

    //required method for all adapters
    @Override
    public int getItemCount() {
        return mlist.size();
    }

    //any chanche to be made to all ViewHolder items can be made here
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    // Easy access to the context object in the recyclerview
    public Context getContext() {
        return context;
    }

}
