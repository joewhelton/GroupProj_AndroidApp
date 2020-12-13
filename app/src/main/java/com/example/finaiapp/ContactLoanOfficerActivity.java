package com.example.finaiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finaiapp.model.FinancialInstitution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactLoanOfficerActivity extends AppCompatActivity {
//    private static final String LO_ID = "Pu9RVLXJSWO3hopm40tdYBaiHxd2";
//    private static final String USER_ID = "3z7Jt7gduUWofFXOnTqrKky91hz2";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbUserProfile;

    private TextView tv_lo_name;
    private TextView tv_lo_phone;
    private TextView tv_lo_bank;

    private String currentUserId;


    private String loanOfficerId;
    private String loanOfficerFirstName;
    private String loanOfficerLastName;
    private String loanOfficerBankName;
    private String loanOfficerPhone;

    private Button button_lo_phone;

//    private User loanOfficer;

    private List<FinancialInstitution> fiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_loan_officer);

        // initialize views
        tv_lo_name = (TextView) findViewById(R.id.textview_lo_name);
        tv_lo_bank = (TextView) findViewById(R.id.textview_lo_bank);
        button_lo_phone = (Button) findViewById(R.id.button_lo_phone);

        // hardcoded strings:
//        loanOfficerFirstName = "Joseph";
//        loanOfficerLastName = "Lastname";
//        loanOfficerLastName = "Jim's Bank";
//        loanOfficerPhone = "0861111111";

//        tv_lo_name.setText(loanOfficerFirstName + " " + loanOfficerLastName);

//        button_lo_phone.setText(loanOfficerPhone);

        // make sure we are logged in
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // get database reference
        database = FirebaseDatabase.getInstance();
        dbUserProfile = database.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("profile");


        dbUserProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("loanOfficerId")) {
                    Log.d("CLO", "found " + "loanOfficerId");
                    Log.d("CLO", snapshot.child("loanOfficerId").getValue().toString());

                    String loID = snapshot.child("loanOfficerId").getValue().toString();
                    Log.d("CLO", "LOID = " + loID);

                    DatabaseReference dbLoanOfficer = database.getReference().child("users").child(loID);
                    dbLoanOfficer.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            loanOfficerFirstName = snapshot.child("firstName").getValue().toString();
                            loanOfficerLastName = snapshot.child("surname").getValue().toString();
                            loanOfficerPhone = snapshot.child("profile").child("mobile").getValue().toString();
                            String financialInstitutionId = snapshot.child("profile").child("financialInstitutionID").getValue().toString();
                            tv_lo_name.setText(loanOfficerFirstName + " " + loanOfficerLastName);

                            button_lo_phone.setText(loanOfficerPhone);

                            DatabaseReference dbFin = database.getReference().child("financialInstitutions").child(financialInstitutionId);
                            dbFin.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String loanOfficerBank = snapshot.child("name").getValue().toString();
                                    tv_lo_bank.setText(loanOfficerBank);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"An unknown error has occurred", Toast.LENGTH_SHORT);

            }
        });
        button_lo_phone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                makeCall();
            }
        });

    }

    private void makeCall() {
        String phone = button_lo_phone.getText().toString();
        String dialString = "tel:" + phone ;
        Log.i("Dial: ", dialString);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(dialString));
        try {
            startActivity(intent);
            finish();
            Log.i("Dial: ", "launching dial activity via intent");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Call failed, please try again later.", Toast.LENGTH_SHORT).show();
        }

    }
}
