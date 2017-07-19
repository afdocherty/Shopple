package com.example.kathu228.shoplog.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kathu228.shoplog.Models.Query;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseSession;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //try to log in with sa
        attemptLoginSavedSession();

        // Otherwise initiate login
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //in case user pressed back on the screen
                attemptLoginSavedSession();

                String [] permissions = {"public_profile", "email", "user_friends"};
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, Arrays.asList(permissions), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        handleFacebookLoginResponse(user,err);
                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void launchAppWithUser() {
        Intent intent = new Intent(LoginActivity.this, ShopListsActivity.class);
        startActivity(intent);
    }

    private void attemptLoginSavedSession(){
        // Check if there is already a user logged in
        final ParseUser user = ParseUser.getCurrentUser();
        ParseSession.getCurrentSessionInBackground(new GetCallback<ParseSession>() {
            @Override
            public void done(ParseSession object, ParseException e) {
                if (user != null && object != null) {
                    Toast.makeText(LoginActivity.this, "Welcome back " + Query.getNameOfUser(user), Toast.LENGTH_SHORT).show();
                    launchAppWithUser();
                }
            }
        });
    }

    private void handleFacebookLoginResponse(ParseUser user, ParseException err){
        if (err != null) {
            Log.d("MyApp", "Uh oh. Error occurred " + err.toString());
        } else if (user == null) {
            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
        } else if (user.isNew()) {
            fillUserInfo(user);

            // Add the user to the MVP ShopList
            addToShopList(user);
        } else {
            Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT)
                    .show();
            Log.d("MyApp", String.format("User %s logged in through Facebook!", Query.getNameOfUser(user)));
            launchAppWithUser();
        }
    }

    private void fillUserInfo(final ParseUser user) {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            user.setEmail(object.getString("email"));
                            user.put("name", object.getString("name"));
                            user.saveInBackground();
                            Log.d("MyApp", String.format("User %s signed up and logged in through Facebook!", Query.getNameOfUser(user)));
                            launchAppWithUser();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //TODO Remove the user from the server
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name, email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void addToShopList(final ParseUser user) {
        // Find the ShopList
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopList");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "ShopList found");
                    ((ShopList) results.get(0)).addUser(user);
                } else {
                    Log.d("LoginActivity", "ShopList not found. Error: " + e.toString());
                }
            }
        });
    }
}