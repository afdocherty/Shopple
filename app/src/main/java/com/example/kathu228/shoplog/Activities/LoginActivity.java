package com.example.kathu228.shoplog.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.kathu228.shoplog.Models.Query;
import com.example.kathu228.shoplog.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseSession;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

// Login Activity
public class LoginActivity extends AppCompatActivity {

    public Button loginButton;
    public ProgressBar pbLoading;
    private TextView tvTitle;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Fetch widgets
        loginButton = (Button) findViewById(R.id.login);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        animationView = (LottieAnimationView) findViewById(R.id.ivLogo);

        // Set button invisible and progress bar visible
        loginButton.setVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.VISIBLE);

        // Set the font of the title & subtitle
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Billabong.ttf");
        tvTitle.setTypeface(font);

        // Try login, in case already logged in
        attemptLoginSavedSession();

        // Set loginbutton click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Try login, in case user pressed back on the screen
                //attemptLoginSavedSession();

                // Otherwise initiate login
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

    private void attemptLoginSavedSession() {
        // Check if there is already a user logged in
        final ParseUser user = ParseUser.getCurrentUser();
        ParseSession.getCurrentSessionInBackground(new GetCallback<ParseSession>() {
            @Override
            public void done(ParseSession object, ParseException e) {
                // If user is already logged in, welcome them back and launch app
                if (user != null && object != null) {
                    launchAppWithUser();
                }
                // Otherwise show the login button and hide the progress bar
                else {
                    loginButton.setVisibility(View.VISIBLE);
                    pbLoading.setVisibility(View.INVISIBLE);
                }
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
        finish();
    }

    private void handleFacebookLoginResponse(ParseUser user, ParseException err){
        //ParseInstallation installation = getCurrentInstallation();
        if (err != null) {
            Log.d("MyApp", "Uh oh. Error occurred " + err.toString());
        } else if (user == null) {
            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
        } else if (user.isNew()) {
            //TODO- Push (after Tuesday)
//            installation.put("user",ParseUser.getCurrentUser());
//            installation.saveInBackground();
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.intro_new), Toast.LENGTH_SHORT).show();

            fillUserInfo(user);

        } else {
            //TODO- Push (after Tuesday)
//            installation.put("user",ParseUser.getCurrentUser());
//            installation.saveInBackground();
//            ParsePush.subscribeInBackground("test");
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.intro_login) + " " + Query.getNameOfUser(user), Toast.LENGTH_SHORT).show();

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
}