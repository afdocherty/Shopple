package com.example.kathu228.shoplog.Helpers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathu228.shoplog.Fragments.YesNoDialogFragment;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.Segment;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

import java.util.List;

import static android.support.design.widget.Snackbar.make;
import static com.example.kathu228.shoplog.R.layout.item;
import static com.example.kathu228.shoplog.R.layout.item_header;

/**
 * Created by kathu228 on 7/12/17.
 */

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter, YesNoDialogFragment.YesNoDialogListener{

    public Context context;

    private List<Item> mlist;
    ShopList listTest;
    View mview;
    Segment categorySegment;
    Item categoryHeader;
    Boolean isEditing;

    public ItemAdapter(List<Item> mlist, ShopList listTest, View v) {
        this.mlist = mlist;
        this.listTest = listTest;
        this.mview = v;
        this.categorySegment = null;
        this.categoryHeader = null;
        this.isEditing = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = inflateView(parent);
        context = parent.getContext();
        View view;

        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(item, parent, false);
                return new ItemViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(item_header, parent, false);
                return new HeaderViewHolder(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_header, parent, false);
                return new CompletedHeaderViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid viewtype," + viewType);
        }
//        return new ViewHolder(view);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Item item = mlist.get(position);

        if (item != null) {
            switch (item.getType()) {
                case 0:
                    ((ItemViewHolder) holder).cbItem.setText(item.getBody());
                    ((ItemViewHolder) holder).cbItem.setChecked(item.isChecked());
                    break;
                case 1:
                    ((HeaderViewHolder) holder).tvHeader.setText(item.getBody());
                    //((HeaderViewHolder) holder).etHeader.setText(item.getBody());
                    int colorNum = item.getSegment().getColorNum();
                    ((HeaderViewHolder) holder).vColor.setBackgroundColor(ContextCompat.getColor(context, ColorPicker.getColor(colorNum)));
                    break;
                case 2:
                    ((CompletedHeaderViewHolder) holder).tvCompletedHeader.setText(item.getBody());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid itemtype," + item.getType());
            }
        }

    }


    @Override
    public int getItemViewType(int position) {
        if (mlist != null) {
            Item item = mlist.get(position);
            if (item != null) {
                return item.getType();
            }
        }
        return 0;
    }

    // Allows user to move items into categories by dragging
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    // Allows user to remove checked item or check an unchecked item by swiping
    @Override
    public void onItemDismiss(final int position) {
        final Item item = mlist.get(position);
        // only items allowed
        if (item.isItem()) {
            // if item is checked, delete
            // else, move under completed
            if (item.isChecked()) {
                undoDelete(item, position, mview);
                item.setVisible(false, null);
                listTest.removeItem(item, null);
                deleteItem(position);
//            deleteItemFromList(item, position);
            } else {
                item.setChecked(true, null);
                mlist.remove(position);
                notifyItemRemoved(position);
                int toPosition = 1+getItemIndex(listTest.getCompletedHeader());
                mlist.add(toPosition,item);
                notifyItemInserted(toPosition);
            }
        }
        else if (item.isHeader()){
            showYesNoDialog("Clear Category", "Are you sure you want to delete the category, "+item.getBody()+"?", item);
        }

    }

    @Override
    public int getItemCount() {
        if (mlist == null)
            return 0;
        return mlist.size();
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ItemViewHolder extends BaseAdapter.ViewHolder {
        public CheckBox cbItem;
        private RippleDrawable rippleDrawable;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cbItem = (CheckBox) itemView.findViewById(R.id.cbItem);
            rippleDrawable = (RippleDrawable) cbItem.getBackground();


            //adds onclicklistener to checkbox, to update object's state once you check it
            cbItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (categorySegment != (null)){
                        cbItem.setChecked(!cbItem.isChecked());

                        int segColor = ColorPicker.getColor(categorySegment.getColorNum());
                        rippleDrawable.setColor(ColorStateList.valueOf(ContextCompat.getColor(context,segColor)));

                        int adapterPos = getAdapterPosition();
                        if (adapterPos<0) {
                            Toast.makeText(context, String.format("ERROR: There was a problem with the " +
                                    "adapter. Position %s doesn't exist.", adapterPos), Toast.LENGTH_LONG).show();
                        }else {
                            handleCategorizing(mlist.get(adapterPos), adapterPos);
                        }
                    }
                    else {
                        rippleDrawable.setColor(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.colorPrimaryLight)));
                        int adapterPos = getAdapterPosition();
                        if (adapterPos<0) {
                            Toast.makeText(context, String.format("ERROR: There was a problem with the " +
                                    "adapter. Position %s doesn't exist.", adapterPos), Toast.LENGTH_LONG).show();
                        }else {
                            handleCheckbox(mlist.get(adapterPos), adapterPos);
                        }
                    }
                }
            });

        }

        // adds item to segment if different segment, removes item if same segment
        // cannot add complete
        public void handleCategorizing(Item item, int fromPos){
            if (!item.isChecked()){
                Segment itemSeg = item.getSegment();
                String itemSegName = itemSeg.getName();
                String segName = categorySegment.getName();
                Segment newSeg;
                int newPos;
                if (itemSegName.equals(segName)){
                    mlist.remove(fromPos);
                    newSeg = listTest.getUncategorizedSegment();
                    newPos = 0;
                }
                else{
                    // TODO: replace with finding header position
                    mlist.remove(fromPos);
                    newSeg= categorySegment;
                    newPos = getItemIndex(categorySegment.getHeader())+1;
                    //newPos=mlist.indexOf(categoryHeader)+1;

                }
                item.setSegment(newSeg,null);
                mlist.add(newPos,item);
                notifyItemRemoved(fromPos);
                notifyItemInserted(newPos);
            }
        }

        // checks and unchecks checkbox, while saving the object's boolean state on server
        public void handleCheckbox(final Item item, final int position){
            int toPosition;
            mlist.remove(position);
            notifyItemRemoved(position);
            item.setChecked(!item.isChecked(),null);
            // If item is now unchecked, move to previous segment
            // Else, move to top of completed
            if (!item.isChecked()){
                toPosition=newSegPosition(item);
                cbItem.setChecked(false);
            }
            else{
                toPosition = 1+getItemIndex(listTest.getCompletedHeader());
                cbItem.setChecked(true);

            }
            addItem(item,toPosition);
        }
    }

    public class HeaderViewHolder extends BaseAdapter.ViewHolder{
        public TextView tvHeader;
        //public ViewSwitcher switcher;
        //public EditText etHeader;
        public ImageView ivCategorize;
        public View vColor;
        public HeaderViewHolder(View itemView) {
            super(itemView);

            tvHeader = (TextView)itemView.findViewById(R.id.tvHeader);
            //switcher = (ViewSwitcher)itemView.findViewById(R.id.vsHeaderSwitcher);
            //etHeader = (EditText)itemView.findViewById(R.id.etHeader);
            ivCategorize = (ImageView) itemView.findViewById(R.id.ivCategorize);
            vColor = itemView.findViewById(R.id.vColor);

            // can edit category when click button
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (categorySegment ==null){
                        categoryHeader = mlist.get(getAdapterPosition());
                        categorySegment = categoryHeader.getSegment();
                        int segColor = ColorPicker.getColor(categorySegment.getColorNum());
                        ivCategorize.setColorFilter(ContextCompat.getColor(context,segColor));
                        categorizing(categorySegment.getName(),ivCategorize,mview);
                    }
                    else {
                        return;
                    }


                }
            });

//            tvHeader.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    if (categorySegment !=null){
//                        final String prevHeaderName = tvHeader.getText().toString();
//                        if (prevHeaderName.equals(categorySegment.getName())){
//                            switcher.showNext();
//                            etHeader.setSelectAllOnFocus(true);
//                            etHeader.requestFocus();
//                            final InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.showSoftInput(etHeader, InputMethodManager.SHOW_IMPLICIT);
//                            etHeader.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                                        String body = etHeader.getText().toString();
//                                        if ((!body.equals(""))&& (!body.equals(prevHeaderName))){
//                                            tvHeader.setText(body);
//                                            categorySegment.setName(body, null);
//
//                                        }
//                                        switcher.showPrevious();
//                                    }
//                                    return false;
//                                }
//                            });
//                        }
//                    }
//                }
//            });

        }
    }
    public class CompletedHeaderViewHolder extends BaseAdapter.ViewHolder {
        public TextView tvCompletedHeader;
        public ImageButton ibDelete;

        public CompletedHeaderViewHolder(View itemView) {
            super(itemView);

            tvCompletedHeader = (TextView) itemView.findViewById(R.id.tvCompletedHeader);
            ibDelete = (ImageButton) itemView.findViewById(R.id.ibDelete);

            // asks user if want to delete all items in completed
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    showYesNoDialog("Empty Completed","Are you sure you want to delete all completed items?", mlist.get(getAdapterPosition()));
                }
            });
        }

    }



    //deletes all completed items
    public void deleteItems(final int position) {
        int len = mlist.size();
        for (int i = position; i < len; i++) {
            Item deletedItem = mlist.get(position);
            deleteItemFromList(deletedItem);
            deleteItem(position);
        }
    }

    // Remove an item from mList at position
    public void deleteItem(final int position) {
        mlist.remove(position);
        notifyItemRemoved(position);
        //undoDelete(item, position, v);

    }

    // Remove an item from the shoplist
    private void deleteItemFromList(final Item item) {
        item.setVisible(false, null);
    }

    // Insert item to mList at position
    public void addItem(final Item item, final int position){
        mlist.add(position, item);
        notifyItemInserted(position);
    }


    // Add an item to the shoplist
    private void addItemToList (final Item item, final int position) {
        item.setVisible(true,null);
    }

    // Adding an item back to segment
    private int newSegPosition(Item item){
        Segment curSeg = item.getSegment();
        String curSegname = curSeg.getName();
        if (curSegname.equals("Uncategorized")){
            return (0);
        }
        else {
            Item head = curSeg.getHeader();
            int ind = getItemIndex(head);
            return (1 + ind);
        }
    }

    // if click undo in snackbar, item will reappear in list unchecked
    public void undoDelete(final Item item, final int position, View v){
        make(v, item.getBody()+" deleted", Snackbar.LENGTH_LONG)
                .addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (!mlist.contains(item)){
                            deleteItemFromList(item);
                        }
                    }
                })
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.isChecked()){
                            item.setChecked(false, null);
                        }
                        int toPosition = newSegPosition(item);
                        addItem(item, toPosition);
                        Snackbar snackbar1 = make(v, item.getBody()+" restored!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                })
                .show();
    }

    // opens snackbar to show which category you are currently editing and enables closure
    public void categorizing(final String categoryName, final ImageView ivEdit, View v){
       final Snackbar snackbar = Snackbar.make(v, "Editing "+categoryName+ " category", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Done", new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       ivEdit.setColorFilter(ContextCompat.getColor(context,R.color.lightGray));
                       categorySegment = null;
                       categoryHeader = null;
                   }
               })
                .show();
    }

    private void closeSnackbar(Snackbar snackbar, ImageButton ibEdit){
        snackbar.dismiss();
        ibEdit = null;
    }

    // Removes segment and make all items uncategorized
    private void removeSegment(Item item, int pos) {
        Segment curSegment = item.getSegment();
        deleteItem(pos);
        List<Item> segItems = curSegment.getItems();
        for (Item segItem : segItems) {
            if (segItem.isItem()) {
                int fromPos = getItemIndex(segItem);
                if (fromPos>-1){
                    deleteItem(fromPos);
                    addItem(segItem, 0);
                }
            }
        }
        listTest.removeSegment(curSegment);
    }

    // handles deleting completed items
    private void deleteCompleted(Boolean clear, Item completedHeader){
        int position = getItemIndex(completedHeader);
        if (clear)
            deleteItems(position+1);
    }

    // handles clearing categories
    private void clearCategory(Boolean clear, Item item){
        int position = getItemIndex(item);
        if (clear)
            removeSegment(item,position);
        else
            notifyItemChanged(position);
    }

    // finds index of item in mlist
    private int getItemIndex(Item item){
        for (int i=0; i<mlist.size(); i++){
            if (item.getObjectId().equals(mlist.get(i).getObjectId()))
                return i;
        }
        return -1;
    }

    // shows yes/no dialog
    public void showYesNoDialog(String title, String question, final Item mitem){
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        YesNoDialogFragment yesNoDialogFragment = YesNoDialogFragment.newInstance(title,question,mitem,null);
        yesNoDialogFragment.setListener(ItemAdapter.this);
        yesNoDialogFragment.show(fm, "fragment_yesno_dialog");
    }
    @Override
    public void onFinishYesNoDialog(Boolean yes, String title, Item mitem, ShopList mshopList) {
        if (title.equals("Clear Category"))
            clearCategory(yes,mitem);
        else if (title.equals("Empty Completed"))
            deleteCompleted(yes,mitem);
    }


}
