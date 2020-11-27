package com.example.finaiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonLogout;
    private Button buttonPersonal;
    private Button buttonSearchHousePrices;
    private Button buttonLoanOfficer;
    private TextView textViewUserEmail;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;


    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUtil.openFbReference("users");
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        //if the user is not logged in
        //that means current user will return null
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initializing views
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonPersonal = (Button) findViewById(R.id.buttonPersonal);
        buttonSearchHousePrices = (Button) findViewById(R.id.buttonSearchHousePrices);
        buttonLoanOfficer = (Button) findViewById(R.id.buttonLoanOfficer);
        mDatabaseReference = mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.child("firstName").getValue().toString();
                //displaying logged in user name
                textViewUserEmail.setText("Welcome " + firstName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //adding listener to button
        buttonLogout.setOnClickListener(this);
        buttonPersonal.setOnClickListener(this);
        buttonSearchHousePrices.setOnClickListener(this);
        buttonLoanOfficer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        // perform action on click
        switch (view.getId()) {
            case R.id.buttonLogout:
                // logout is pressed
                //logging out the user
                firebaseAuth.signOut();
                //closing activity
                finish();
                //starting login activity
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.buttonPersonal:
                // move to personal info
                startActivity(new Intent(this, PersonalInfo.class));
                break;

            case R.id.buttonSearchHousePrices:
                finish();
                //starting login activity
                startActivity(new Intent(this, HouseDataActivity.class));
                break;

            case R.id.buttonLoanOfficer:
                //switch to loan officer activity
                startActivity(new Intent(this, LoanOfficerActivity.class));
                break;
        }

    }
}