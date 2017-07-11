package com.example.kathu228.shoplog.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.kathu228.shoplog.R;

public class AddPeopleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }
}
