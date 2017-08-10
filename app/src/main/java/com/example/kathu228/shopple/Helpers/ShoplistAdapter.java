package com.example.kathu228.shopple.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kathu228.shopple.Activities.ItemListActivity;
import com.example.kathu228.shopple.Fragments.IconPickerDialogFragment;
import com.example.kathu228.shopple.Fragments.YesNoDialogFragment;
import com.example.kathu228.shopple.Models.Item;
import com.example.kathu228.shopple.Models.ShopList;
import com.example.kathu228.shopple.R;
import com.parse.ParseUser;

import java.util.List;

import static com.example.kathu228.shopple.Models.ShopList.SHOPLIST_TAG;

/**
 * Created by afdoch on 7/13/17.
 */

public class ShoplistAdapter extends BaseAdapter<ShoplistAdapter.ViewHolder, ShopList> implements ItemTouchHelperAdapter, YesNoDialogFragment.YesNoDialogListener{

    public Context context;

    public ShoplistAdapter(List<ShopList> mlist, int itemViewReference) {
        super(mlist, itemViewReference);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflateView(parent);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ShopList shopList = mlist.get(position);
        String peopleInList = shopList.getUsersString();
        if (peopleInList.equals("Personal")) {
            holder.ivCollaborators.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person_outline));
        }else {
            holder.ivCollaborators.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_people_outline));
        }
        holder.tvCollaborators.setText(peopleInList);
        holder.tvListName.setText(shopList.getName());
        setListIcon(shopList, holder);
    }

    private void setListIcon(ShopList shopList, ViewHolder holder) {
        switch (shopList.getIconNum()) {
            case IconPickerDialogFragment.ICON_NUM_GROCERY:
                holder.ivListIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_grocery_bag));
                break;
            case IconPickerDialogFragment.ICON_NUM_PARTY:
                holder.ivListIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_solo_cup));
                break;
            case IconPickerDialogFragment.ICON_NUM_BBQ:
                holder.ivListIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bbq_grill));
                break;
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        return;
    }

    @Override
    public void onItemDismiss(final int position) {
        showYesNoDialog("Leave List", "Are you sure you want to leave "+mlist.get(position).getName()+"?",mlist.get(position));
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the list layout for fast access
    public class ViewHolder extends BaseAdapter.ViewHolder{

        public TextView tvListName;

        public LinearLayout llList;

        public ImageView ivListIcon;

        public TextView tvCollaborators;

        public ImageView ivCollaborators;

        private RippleDrawable rippleDrawable;

        public ViewHolder(final View itemView){
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvListName = (TextView) itemView.findViewById(R.id.tvListName);

            llList = (LinearLayout) itemView.findViewById(R.id.rlItem) ;
            rippleDrawable = (RippleDrawable) llList.getBackground();

            ivListIcon = (ImageView) itemView.findViewById(R.id.ivListIcon);

            tvCollaborators = (TextView) itemView.findViewById(R.id.tvCollaborators);

            ivCollaborators = (ImageView) itemView.findViewById(R.id.ivCollaborators);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rippleDrawable.setColor(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.lightGray)));
                    // Gets ShopList position
                    int position = getAdapterPosition();
                    // Make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the list at the position, this won't work if the class is static
                        final ShopList shopList = mlist.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, ItemListActivity.class);
                        // Pass in ShopList ObjectId
                        intent.putExtra(SHOPLIST_TAG, shopList.getObjectId());
                        intent.putExtra(ShopList.SHOPLIST_NEW_TAG, false);
//                        ActivityOptionsCompat options = ActivityOptionsCompat.
//                                makeSceneTransitionAnimation((Activity)context, (View)tvListName, "listname");
//                        context.startActivity(intent,options.toBundle());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    // shows yes/no dialog
    public void showYesNoDialog(String title, String question, final ShopList mshopList){
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        YesNoDialogFragment yesNoDialogFragment = YesNoDialogFragment.newInstance(title,question,null,mshopList,YesNoDialogFragment.LEAVELIST);
        yesNoDialogFragment.setListener(ShoplistAdapter.this);
        yesNoDialogFragment.show(fm, "fragment_yesno_dialog");
    }
    @Override
    public void onFinishYesNoDialog(Boolean yes, int type, Item mitem, ShopList mshopList) {
        int position = mlist.indexOf(mshopList);
        if (yes){
            mshopList.removeUser(ParseUser.getCurrentUser(),null);
            mlist.remove(position);
            notifyItemRemoved(position);
        }
        else {
            notifyItemChanged(position);
        }
    }

}
