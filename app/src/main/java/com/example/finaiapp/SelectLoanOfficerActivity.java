package com.example.finaiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finaiapp.model.FinancialInstitution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectLoanOfficerActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbRoot;
    private DatabaseReference dbUserProfile;

    private Spinner spinnerFinancial;
    private Spinner spinnerLoanOfficer;
    private Button saveButton;

    private final List<FinancialInstitution> bankList = new ArrayList<>();
    private final List<String> bankNameList = new ArrayList<String>();
    private final List<String> bankIdList = new ArrayList<String>();
    private final List<String> loNameList = new ArrayList<String>();
    private final List<String> loIdList = new ArrayList<String>();

    private ArrayAdapter<String> loNameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_loan_officer);


        // make sure we are logged in
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        // get database reference
        database = FirebaseDatabase.getInstance();
        dbRoot = database.getReference();
        dbUserProfile = database.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());

        spinnerFinancial = (Spinner) findViewById(R.id.spinner_financial);
        spinnerLoanOfficer = (Spinner) findViewById(R.id.spinner_loan_officer);
        saveButton = (Button) findViewById(R.id.button_lo_save);


        //adding an onclicklistener to button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addArtist()
                //the method is defined below
                //this method is actually performing the write operation
                updateProfile();
            }
        });


        // get banks for bank spinner
        populateBankSpinner();

    }

    @Override
    protected void onStart() {
        super.onStart();

        loNameAdapter = new ArrayAdapter<String>(SelectLoanOfficerActivity.this, android.R.layout.simple_spinner_item, loNameList);
        loNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoanOfficer.setAdapter(loNameAdapter);


        spinnerFinancial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("BankNameList", Integer.toString(i));
                Log.d("BankNameList", bankNameList.toString());
                String testBankName = bankNameList.get(i);
                String testBankId = bankIdList.get(i);
                Log.d("spinner1select: ", testBankId + " " + testBankName);
                populateLoSpinner(testBankId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void populateLoSpinner(String selectedFinId) {
        Query query3 = dbRoot.child("users").orderByChild("profile/financialInstitutionID").equalTo(selectedFinId);
        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loNameList.clear();
                loIdList.clear();

                List<String> loTempNameList = new ArrayList<>();
                List<String> loTempIdList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        String loFirstName = user.child("firstName").getValue(String.class);
                        String loSurname = user.child("surname").getValue(String.class);
                        String loId = user.getKey();
                        if (loFirstName != null) {
                            loTempNameList.add(loFirstName + " " + loSurname);
                            loTempIdList.add(loId);
                        }
                    }
                    // add the items to the original list so adapter doesn't lose reference.
                    loNameList.addAll(loTempNameList);
                    loIdList.addAll(loTempIdList);
                }

                TextView tvmsg = findViewById(R.id.textview_select_msg);

                if (loNameList.size() == 0) {
                    tvmsg.setText("Bank has no loan officers");
                    Log.d("Spinner2", "lonamelist.size == 0");
                } else {
                    tvmsg.setText("");
                    Log.d("Spinner2", "lonamelist.size > 0");

                }
                // notify adapter
                loNameAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void populateBankSpinner() {

        dbRoot.child("financialInstitutions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array

                // TODO: I think if a new bank is added while this is up we'll get double banks.
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String bankName = child.child("name").getValue(String.class);
                    String bankId = child.getKey();
                    if (bankName != null) {
                        bankNameList.add(bankName);
                        bankIdList.add(bankId);
                    }
                }

                ArrayAdapter<String> bankNameAdapter = new ArrayAdapter<String>(SelectLoanOfficerActivity.this, android.R.layout.simple_spinner_item, bankNameList);
                bankNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFinancial.setAdapter(bankNameAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    //        // get all financial Institutions
//        Query query = dbRoot.child("financialInstitutions").orderByChild("name");
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // dataSnapshot is the "issue" node with all children with id 0
//                    for (DataSnapshot finSnapshot : dataSnapshot.getChildren()) {
////                        Log.d("Query", issue.;
//                        FinancialInstitution fi = finSnapshot.getValue(FinancialInstitution.class);
//                        fi.setKey(finSnapshot.getKey());
//                        Log.d("FI", fi.toString());
//                        bankList.add(fi);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        // get all users with a profile/financialInstitionId
//        Query query2 = dbRoot.child("users").orderByChild("profile/financialInstitutionId");
//        query2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // dataSnapshot is the "issue" node with all children with id 0
//                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
//                        Log.d("Query", "Query");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    private void updateProfile() {

        // get fields from views
        int loanOfficerPos = spinnerLoanOfficer.getSelectedItemPosition();
        String loanOfficerKey = loIdList.get(loanOfficerPos);


        //Writing Hashmap
        Map<String, Object> mHashmap = new HashMap<>();

        //TODO hardcoded LO ID and client user role
        mHashmap.put("profile/loanOfficerId", loanOfficerKey);

        Log.d("Save", dbUserProfile.toString());
        dbUserProfile.updateChildren(mHashmap);

        startActivity(new Intent(this, ProfileActivity.class));
    }

}

