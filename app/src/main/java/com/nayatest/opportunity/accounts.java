package com.nayatest.opportunity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class accounts extends AppCompatActivity {
ListView mainlist;
ArrayList<String> myArrayList=new ArrayList<>();
DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        mainlist=findViewById(R.id.accountslist);

        final ArrayAdapter<String> myArrayAdapter=new ArrayAdapter<>(accounts.this, android.R.layout.simple_list_item_1,myArrayList);
        mRef=FirebaseDatabase.getInstance().getReference();
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                 String value=dataSnapshot.child("Users").getValue(String.class);
                 myArrayList.add(value);
                 myArrayAdapter.notifyDataSetChanged();
                mainlist.setAdapter(myArrayAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}