package com.example.finaiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.finaiapp.model.FinancialInstitution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoanOfficerActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbFin, dbUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_officer);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        dbFin = database.getReference("financialInstitutions");
        dbUsers = database.getReference("users");

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

//        final List<FinancialInstitution> banks = new ArrayList<FinancialInstitution>();
        final List<FinancialInstitution> banks = new ArrayList<FinancialInstitution>();


        dbFin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                GenericTypeIndicator<List<FinancialInstitution>> bank = new GenericTypeIndicator<List<FinancialInstitution>>() {};



                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    FinancialInstitution fi = childDataSnapshot.getValue(FinancialInstitution.class);
                    banks.add(fi);
                    Log.d("Banks",""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.d("Banks",""+ childDataSnapshot.child("name").getValue());   //gives the value for given keyname
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}