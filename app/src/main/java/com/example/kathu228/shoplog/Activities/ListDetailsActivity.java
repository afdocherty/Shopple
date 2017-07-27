package com.example.kathu228.shoplog.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.kathu228.shoplog.Fragments.CollaboratorsDialogFragment;
import com.example.kathu228.shoplog.Fragments.ListDetailsFragment;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.ParseUser;

import java.util.List;

public class ListDetailsActivity extends AppCompatActivity implements CollaboratorsDialogFragment.CollaboratorsDialogListener{

    private ListDetailsFragment listDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_details));

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment, containing the ShopList Object ID
        listDetailsFragment = ListDetailsFragment.newInstance(getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
        ft.replace(R.id.details_frame, listDetailsFragment);
        // Complete the changes added above
        ft.commit();
    }

    // Access the data result passed to the activity here
    @Override
    public void onFinishCollaboratorsDialog(List<ParseUser> newPeople) {
        listDetailsFragment.addNewPeople(newPeople);
    }
}
