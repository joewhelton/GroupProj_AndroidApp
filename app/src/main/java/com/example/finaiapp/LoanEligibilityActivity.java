package com.example.finaiapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;



public class LoanEligibilityActivity extends AppCompatActivity {
    private EditText et_personal_first, et_personal_surname, et_personal_gender,
            et_personal_marital, et_personal_dependents, et_personal_education,
            et_personal_selfemployed, et_personal_applicantincome, et_personal_coapplicantincome,
            et_personal_credithistory, et_propertyarea, et_loan_amount, et_loan_term;
    private Button btn_check_eligibility;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_eligibility);
        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUtil.openFbReference("users");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        // initializing views
        et_personal_first = findViewById(R.id.edittext_personal_first);
        et_personal_surname = findViewById(R.id.edittext_personal_surname);
        et_personal_gender = findViewById(R.id.edittext_personal_gender);
        et_personal_marital = findViewById(R.id.edittext_personal_marital);
        et_personal_dependents = findViewById(R.id.edittext_personal_dependents);
        et_personal_education = findViewById(R.id.edittext_personal_education);
        et_personal_selfemployed = findViewById(R.id.edittext_personal_selfemployed);
        et_personal_applicantincome = findViewById(R.id.edittext_personal_applicantincome);
        et_personal_coapplicantincome = findViewById(R.id.edittext_personal_coapplicantincome);
        et_personal_credithistory = findViewById(R.id.edittext_personal_credithistory);
        et_propertyarea = findViewById(R.id.edittext_propertyarea);
        et_loan_amount = findViewById(R.id.edittext_loan_amount);
        et_loan_term = findViewById(R.id.edittext_loan_term);
        btn_check_eligibility = findViewById(R.id.button_check_eligibility);
        //adding an onclicklistener to button
        btn_check_eligibility.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                writeLoanDetails();
            }
        });
        //if the objects getcurrentuser method is null
        //means user is not logged in
        if (firebaseAuth.getCurrentUser() == null) {
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //retrieves user profile details from database and displays relevant details on screen
        mDatabaseReference = mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                         et_personal_first.setText(snapshot.child("firstName").getValue().toString());
                                                         et_personal_surname.setText(snapshot.child("surname").getValue().toString());

                                                         if (snapshot.hasChild("profile")) {
                                                             Log.d("Profile", "Found profile for " +  et_personal_first);
                                                             Log.d("Profile", snapshot.child("profile").getValue().toString());
                                                             et_personal_gender.setText(snapshot.child("profile").child("gender").getValue().toString());
                                                             et_personal_marital.setText(snapshot.child("profile").child("marital").getValue().toString());
                                                             et_personal_dependents.setText(snapshot.child("profile").child("dependents").getValue().toString());
                                                             et_personal_education.setText(snapshot.child("profile").child("education").getValue().toString());
                                                             et_personal_selfemployed.setText(snapshot.child("profile").child("selfemployed").getValue().toString());
                                                             et_personal_applicantincome.setText(snapshot.child("profile").child("applicantincome").getValue().toString());
                                                             et_personal_coapplicantincome.setText(snapshot.child("profile").child("coapplicantincome").getValue().toString());
                                                             et_personal_credithistory.setText(snapshot.child("profile").child("credithistory").getValue().toString());
                                                         }
                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError error) {
                                                         Toast.makeText(getApplicationContext(),"An unknown error has occurred", Toast.LENGTH_SHORT);
                                                     }
                                                 }
        );

    }
    //Writes details to loanApplications in database (do we need to have these as floats for model??

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writeLoanDetails(){
        String propertyArea = et_propertyarea.getText().toString().trim();
        String loanAmount = et_loan_amount.getText().toString().trim();
        String loanTerm = et_loan_term.getText().toString().trim();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        FirebaseUtil.openFbReference("loanApplications");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        //Writing Hashmap
        Map<String, Object> mHashmap = new HashMap<>();
        mHashmap.put("createdDate", dtf.format(now));
        mHashmap.put("clientId", firebaseAuth.getCurrentUser().getUid() );
        mHashmap.put("propertyArea", propertyArea );
        mHashmap.put("amount", loanAmount );
        mHashmap.put("term", loanTerm );
        mDatabaseReference.push().setValue(mHashmap);
        Toast.makeText(LoanEligibilityActivity.this, "Loan Details saved", Toast.LENGTH_LONG).show();

        startActivity(new Intent(this, ProfileActivity.class));
    }

}