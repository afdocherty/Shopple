package com.example.kathu228.shopple.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.kathu228.shopple.Fragments.ShopListFragment;
import com.example.kathu228.shopple.Fragments.YesNoDialogFragment;
import com.example.kathu228.shopple.Models.Item;
import com.example.kathu228.shopple.Models.ShopList;
import com.example.kathu228.shopple.R;
import com.parse.ParseUser;

public class ShopListsActivity extends AppCompatActivity implements YesNoDialogFragment.YesNoDialogListener{

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
        showYesNoDialog();
    }

    private void logout(Boolean logOut){
        if (logOut){
            //TODO - Push
            //ParseInstallation.getCurrentInstallation().deleteInBackground();
            //ParseInstallation.getCurrentInstallation().remove("user");
            //ParsePush.unsubscribeInBackground("test");
            ParseUser.logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void showYesNoDialog() {
        FragmentManager fm = getSupportFragmentManager();
        YesNoDialogFragment yesNoDialogFragment = YesNoDialogFragment.newInstance(getString(R.string.log_out), getString(R.string.confirm_logout), null, null,YesNoDialogFragment.LOGOUT);
        yesNoDialogFragment.setListener(this);
        yesNoDialogFragment.show(fm, "fragment_yesno_dialog");
    }

    @Override
    public void onFinishYesNoDialog(Boolean yes, int type, Item mitem, ShopList mshopList) {
        logout(yes);
    }
}
