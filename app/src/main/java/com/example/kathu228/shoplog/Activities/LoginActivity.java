package com.example.kathu228.shoplog.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.Segment;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;

    // Init variables for MVP
    ShopList list1;
    ShopList listTest;
    Segment seg1;
    Segment segTest;
    Item item1;
    Item itemTest;
    ParseUser userTest;
    ShopList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Context context = this;

//        // Check if there is already a user logged in
//        ParseUser user = ParseUser.getCurrentUser();
//        if (user != null) {
//            Toast.makeText(context, "Welcome back " + user.getUsername(), Toast.LENGTH_SHORT).show();
//            launchAppWithUser();
//            //mvpInit();
//        }
//        // Otherwise initiate login
//        else {
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
                                        Log.d("MyApp", "Uh oh. Error occurred " + err.toString());
                                    } else if (user == null) {
                                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                                    } else if (user.isNew()) {
                                        Log.d("MyApp", "User signed up and logged in through Facebook!");

                                        // Add the user to the MVP ShopList
                                        addToShopList(user);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT)
                                                .show();
                                        Log.d("MyApp", "User logged in through Facebook!");
                                    }
                                }
                            });
                }
            });
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

        launchAppWithUser();
        //mvpInit();
    }

    private void launchAppWithUser() {
        Intent intent = new Intent(LoginActivity.this, ShoplistActivity.class);
        startActivity(intent);
    }

    // Initialize data for MVP
    private void mvpInit() {
//        Intent intent = new Intent(LoginActivity.this, ShoplistActivity.class);
//        startActivity(intent);

//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.findInBackground(new FindCallback<ParseUser>() {
//            public void done(java.util.ShopList<ParseUser> objects, ParseException e) {
//                if (e == null) {
//                    userTest = objects.get(0);
//                    Log.d("LoginActivity", "User found");
//                } else {
//                    Log.d("LoginActivity", "User not found. Error: " + e.toString());
//                    e.printStackTrace();
//                }
//            }
//        });
        item1 = new Item();
        item1.setBody("French Toast");
        item1.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Item saved");
                    seg1 = new Segment();
                    seg1.setName("Segment 1");
                    ParseRelation<ParseObject> relationSegmentToItem = seg1.getRelation("items");
                    relationSegmentToItem.add(item1);
                    seg1.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("LoginActivity", "Segment saved");
                                list1 = new ShopList();
                                list1.setName("ShopList 1");
                                ParseRelation<ParseObject> relationListToSegment = list1.getRelation("segments");
                                relationListToSegment.add(seg1);
                                list1.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d("LoginActivity", "ShopList saved");
                                            //queryList();
                                        } else {
                                            Log.d("LoginActivity", "ShopList not saved. Error: " + e.toString());
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                Log.d("LoginActivity", "Segment not saved. Error: " + e.toString());
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Log.d("LoginActivity", "Item not saved. Error: " + e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    private void addToShopList(ParseUser user) {
        // Find the ShopList
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopList");
        query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> results, ParseException e) {
                            if (e == null) {
                                Log.d("LoginActivity", "ShopList found");
                                list = (ShopList) results.get(0);
                            } else {
                                Log.d("LoginActivity", "ShopList not found. Error: " + e.toString());
                            }
                        }
        });

        // Add the user to the list
        list.addUser(user);
    }
//    private void queryList() {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopList");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(java.util.ShopList<ParseObject> results, ParseException e) {
//                if (e == null) {
//                    Log.d("LoginActivity", "ShopList found");
//                    listTest = (ShopList) results.get(0);
//                    Log.d("LoginActivity", "listTest name: " + listTest.getName());
//
//                    ParseRelation<ParseObject> relationListToSegment = listTest.getRelation("segments");
//                    relationListToSegment.getQuery().findInBackground(new FindCallback<ParseObject>() {
//                        public void done(java.util.ShopList<ParseObject> results, ParseException e) {
//                            if (e != null) {
//                                // There was an error
//                            } else {
//                                // results have all the segments in the list
//                                segTest = (Segment) results.get(0);
//                                Log.d("LoginActivity", "listTest segment name: " + segTest.getName());
//                                ParseRelation<ParseObject> relationSegmentToItem = segTest.getRelation("items");
//                                relationSegmentToItem.getQuery().findInBackground(new FindCallback<ParseObject>() {
//                                    @Override
//                                    public void done(java.util.ShopList<ParseObject> results, ParseException e) {
//                                        if (e != null) {
//                                            // There was an error
//                                        } else {
//                                            // results have all the items in the segment
//                                            itemTest = (Item) results.get(0);
//                                            Log.d("LoginActivity", "segTest item name: " + itemTest.getBody());
//                                        }
//                                    }
//                                });
//
//                            }
//                        }
//                    });
//                } else {
//                    Log.d("LoginActivity", "ShopList not found");
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
}
