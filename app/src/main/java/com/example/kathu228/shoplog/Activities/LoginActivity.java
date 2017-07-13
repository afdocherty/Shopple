package com.example.kathu228.shoplog.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.List;
import com.example.kathu228.shoplog.Models.Segment;
import com.example.kathu228.shoplog.R;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;

    List list1;
    List listTest;
    Segment seg1;
    Segment segTest;
    Item item1;
    Item itemTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        generateKeyHash();

        loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> permissions = new ArrayList();
                permissions.add("email");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions,
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                if (err != null) {
                                    Log.d("MyApp", "Uh oh. Error occurred" + err.toString());
                                } else if (user == null) {
                                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                                } else if (user.isNew()) {
                                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                                } else {
                                    Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT)
                                            .show();

                                    Log.d("MyApp", "User logged in through Facebook!");
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

        mvpInit();

//        Intent intent = new Intent(LoginActivity.this, ShoplistActivity.class);
//        startActivity(intent);
    }

    private void generateKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.packagename",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", "PackageManager.NameNotFoundException");
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", "NoSuchAlgorithmException");
        }
    }

    private void addItem() {
        seg1.put("item", item1);
        seg1.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("LoginActivity", "Item added to Segment!");
                addSegment();
            }
        });
    }

    private void addSegment() {
        list1.put("segment", seg1);
        list1.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("LoginActivity", "Segment added to List!");
                addList();
            }
        });
    }

    private void addList() {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("list", list1);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("LoginActivity", "List added to User!");
                fetchList();
            }
        });
    }

    private void fetchList() {


//        ParseUser user = ParseUser.getCurrentUser();
//        user.getParseObject("list").fetchInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if (object != null) {
//                    listTest = (List) object;
//                    Log.d("LoginActivity", "List exists!");
//                    fetchSegment();
//                } else {
//                    Log.d("LoginActivity", "List Nope!");
//                }
//            }
//        });
    }

    private void fetchSegment() {
        listTest.getParseObject("segment").fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (object != null) {
                    segTest = (Segment) object;
                    Log.d("LoginActivity", "Segment exists!");
                    fetchItem();
                } else {
                    Log.d("LoginActivity", "Segment Nope!");
                }
            }
        });
    }

    private void fetchItem() {
        segTest.getParseObject("item").fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (object != null) {
                    itemTest = (Item) object;
                    Log.d("LoginActivity", "Item is: " + itemTest.getBody());
                } else {
                    Log.d("LoginActivity", "Item Nope!");
                }
            }
        });
    }


    private void mvpInit() {
        item1 = new Item();
        seg1 = new Segment();
        list1 = new List();
        item1.setBody("French Toast");
        addItem();


//        ParseRelation<ParseObject> relationSegmentToItem = seg1.getRelation("items");
//        relationSegmentToItem.add(item1);
//        seg1.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                Log.d("LoginActivity", "Item added to Segment!");
//            }
//        });
//
//        ParseRelation<ParseObject> relationListToSegment = list1.getRelation("segments");
//        relationListToSegment.add(seg1);
//        list1.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                Log.d("LoginActivity", "Segment added to List!");
//            }
//        });
//
//        ParseRelation<ParseObject> relationUserToList = user.getRelation("lists");
//        relationUserToList.add(list1);
//        user.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                Log.d("LoginActivity", "List added to User!");
//            }
//        });
//
//        relationUserToList.getQuery().findInBackground(new FindCallback<ParseObject>() {
//                                                           @Override
//                                                           public void done(java.util.List<ParseObject> objects, ParseException e) {
//                                                               listTest = (com.example.kathu228.shoplog.Models.List) objects.get(0);
//                                                               if (listTest != null) {
//                                                                   Log.d("LoginActivity", "List exists!");
//                                                               } else {
//                                                                   Log.d("LoginActivity", "Nope!");
//                                                               }
//                                                           }
//                                                       });
    }
}
