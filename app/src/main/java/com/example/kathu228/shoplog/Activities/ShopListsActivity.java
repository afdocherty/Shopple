package com.example.kathu228.shoplog.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.kathu228.shoplog.Fragments.ShopListFragment;
import com.example.kathu228.shoplog.R;
import com.parse.ParseUser;

public class ShopListsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_lists);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_shoplists));

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.shoplist_frame, new ShopListFragment());
        // Complete the changes added above
        ft.commit();
    }

    //ran when user presses the info button on the toolbar (allows user to add people to list)
    public void onLogout(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Log out");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        ParseUser.logOut();
                        ShopListsActivity.this.finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

//        Intent i = new Intent(this, AddPeopleActivity.class);
//        startActivityForResult(i,20);
    }
}
