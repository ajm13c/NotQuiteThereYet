package com.example.fixit.facebookexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dialog);
        TextView name = (TextView) findViewById(R.id.UserName);
        TextView interests = (TextView) findViewById(R.id.interestsView);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String uName = bundle.getString("Name");
        String uInt = bundle.getString("Interests");
        name.setText(uName);
        interests.setText(uInt);
    }
}
