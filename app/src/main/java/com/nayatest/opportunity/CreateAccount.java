package com.nayatest.opportunity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nayatest.opportunity.activities.HomeActivity;

public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    public void existing(View view) {
        startActivity(new Intent(this, HomeActivity.class));
    }

    public void newacc(View view) {
        startActivity(new Intent(this, NewAccount.class));
    }
}