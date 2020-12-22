package com.example.finaiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PersonalInfo extends AppCompatActivity {

    // views from activity
    private TextView et_personal_first;
    private TextView et_personal_surname;
    private TextView et_personal_email;
    private TextView et_personal_address1;
    private TextView et_personal_address2;
    private TextView et_personal_city;
    private TextView et_personal_state;
    private TextView et_personal_mobile;
    private TextView et_personal_dob;
    private TextView et_personal_gender;
    private TextView et_personal_marital;
    private TextView et_personal_dependents;
    private TextView et_personal_education;
    private TextView et_personal_selfemployed;
    private TextView et_personal_applicantincome;
    private TextView et_personal_coapplicantincome;
    private TextView et_personal_credithistory;

    private Spinner spin_personal_gender;
    private Spinner spin_personal_marital;
    private Spinner spin_personal_dependents;
    private Spinner spin_personal_education;
    private Spinner spin_personal_selfemployed;
    private Spinner spin_personal_credithistory;

    private Button btn_personal_save;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;

    // profile fields
    private String firstName;
    private String surname;
    private String email;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String mobile;
    private String dob;
    private String gender;
    private String marital;
    private String dependents;
    private String education;
    private String selfemployed;
    private String applicantincome;
    private String coapplicantincome;
    private String credithistoryStored;
    private String credithistoryDisplay;
    private int creditHistoryPos;

    DatePickerDialog picker_dob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUtil.openFbReference("users");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        // initializing views
        et_personal_first = findViewById(R.id.edittext_personal_first);
        et_personal_surname = findViewById(R.id.edittext_personal_surname);
        et_personal_email = findViewById(R.id.edittext_personal_email);
        et_personal_address1 = findViewById(R.id.edittext_personal_address1);
        et_personal_address2 = findViewById(R.id.edittext_personal_address2);
        et_personal_city = findViewById(R.id.edittext_personal_city);
        et_personal_state = findViewById(R.id.edittext_personal_state);
        et_personal_mobile = findViewById(R.id.edittext_personal_mobile);
        et_personal_dob = findViewById(R.id.edittext_personal_dob);
        et_personal_gender = findViewById(R.id.edittext_personal_gender);
        et_personal_marital = findViewById(R.id.edittext_personal_marital);
        et_personal_dependents = findViewById(R.id.edittext_personal_dependents);
        et_personal_education = findViewById(R.id.edittext_personal_education);
        et_personal_selfemployed = findViewById(R.id.edittext_personal_selfemployed);
        et_personal_applicantincome = findViewById(R.id.edittext_personal_applicantincome);
        et_personal_coapplicantincome = findViewById(R.id.edittext_personal_coapplicantincome);
        et_personal_credithistory = findViewById(R.id.edittext_personal_credithistory);

        // spinners
        spin_personal_gender = findViewById(R.id.spinner_personal_gender);
        spin_personal_marital = findViewById(R.id.spinner_personal_marital);
        spin_personal_dependents = findViewById(R.id.spinner_personal_dependents);
        spin_personal_education = findViewById(R.id.spinner_personal_education);
        spin_personal_selfemployed = findViewById(R.id.spinner_personal_selfemployed);
        spin_personal_credithistory = findViewById(R.id.spinner_personal_credithistory);

        btn_personal_save = findViewById(R.id.button_personal_save);

        //adding an onclicklistener to button
        btn_personal_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addArtist()
                //the method is defined below
                //this method is actually performing the write operation
                updateProfile();
            }
        });
        //if the user is not logged in
        //that means current user will return null
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }


        et_personal_dob.setInputType(InputType.TYPE_NULL);
        et_personal_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                // date picker dialog
                picker_dob = new DatePickerDialog(PersonalInfo.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                et_personal_dob.setText((monthOfYear + 1) + "/" + (dayOfMonth) + "/" + year);
                            }
                        }, year, month, day);
                picker_dob.show();
            }
        });

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        mDatabaseReference = mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firstName = snapshot.child("firstName").getValue().toString();
                surname = snapshot.child("surname").getValue().toString();
                email = snapshot.child("email").getValue().toString();

                //displaying logged in user name
                et_personal_first.setText(firstName);
                et_personal_surname.setText(surname);
                et_personal_email.setText(email);


                if (snapshot.hasChild("profile")) {
                    Log.d("Profile", "Found profile for " + firstName);
                    Log.d("Profile", snapshot.child("profile").getValue().toString());

                    address1 = snapshot.child("profile").child("address1").getValue().toString();
                    address2 = snapshot.child("profile").child("address2").getValue().toString();
                    city = snapshot.child("profile").child("city").getValue().toString();
                    state = snapshot.child("profile").child("state").getValue().toString();
                    mobile = snapshot.child("profile").child("mobile").getValue().toString();
                    dob = snapshot.child("profile").child("dob").getValue().toString();

                    gender = snapshot.child("profile").child("gender").getValue().toString();
                    String[] genderVals = getResources().getStringArray(R.array.spinner_profile_gender);
                    spin_personal_gender.setSelection(Arrays.asList(genderVals).indexOf(gender));

                    marital = snapshot.child("profile").child("marital").getValue().toString();
                    String[] maritalVals = getResources().getStringArray(R.array.yes_no_empty);
                    spin_personal_marital.setSelection(Arrays.asList(maritalVals).indexOf(marital));

                    dependents = snapshot.child("profile").child("dependents").getValue().toString();
                    String[] dependentVals = getResources().getStringArray(R.array.spinner_profile_dependents);
                    spin_personal_dependents.setSelection(Arrays.asList(dependentVals).indexOf(dependents));

                    education = snapshot.child("profile").child("education").getValue().toString();
                    String[] educationVals = getResources().getStringArray(R.array.spinner_profile_education);
                    spin_personal_education.setSelection(Arrays.asList(educationVals).indexOf(education));

                    selfemployed = snapshot.child("profile").child("selfemployed").getValue().toString();
                    String[] selfemployedVals = getResources().getStringArray(R.array.yes_no_empty);
                    spin_personal_selfemployed.setSelection(Arrays.asList(selfemployedVals).indexOf(selfemployed));

                    applicantincome = snapshot.child("profile").child("applicantincome").getValue().toString();
                    coapplicantincome = snapshot.child("profile").child("coapplicantincome").getValue().toString();

                    credithistoryStored = snapshot.child("profile").child("credithistory").getValue().toString();

                    String[] credithistoryStoredVals = getResources().getStringArray(R.array.spinner_profile_credit);
                    String[] credithistoryDisplayVals = getResources().getStringArray(R.array.spinner_profile_credittext);

                    creditHistoryPos = Arrays.asList(credithistoryStoredVals).indexOf(credithistoryStored);

                    spin_personal_credithistory.setSelection(creditHistoryPos);

                    et_personal_address1.setText(address1);
                    et_personal_address2.setText(address2);
                    et_personal_city.setText(city);
                    et_personal_state.setText(state);
                    et_personal_mobile.setText(mobile);
                    et_personal_dob.setText(dob);
//                    et_personal_gender.setText(gender);
//                    et_personal_marital.setText(marital);
//                    et_personal_dependents.setText(dependents);
//                    et_personal_education.setText(education);
//                    et_personal_selfemployed.setText(selfemployed);
                    et_personal_applicantincome.setText(applicantincome);
                    et_personal_coapplicantincome.setText(coapplicantincome);
//                    et_personal_credithistory.setText(credithistory);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "An unknown error has occurred", Toast.LENGTH_SHORT);

            }
        });


    }

    private void updateProfile() {

        // get fields from views
        address1 = et_personal_address1.getText().toString();
        address2 = et_personal_address2.getText().toString();
        city = et_personal_city.getText().toString();
        state = et_personal_state.getText().toString();
        mobile = et_personal_mobile.getText().toString();
        dob = et_personal_dob.getText().toString();
//        gender = et_personal_gender.getText().toString();
        gender = spin_personal_gender.getSelectedItem().toString();

//        String genderSpinnerValue = "";
//
//        if (hasContentSpin(spin_personal_gender)) {
//            genderSpinnerValue = spin_personal_gender.getSelectedItem().toString();
//            Log.d("SpinnerTest", genderSpinnerValue);
//            Log.d("SpinnerTest", Integer.toString(spin_personal_gender.getSelectedItemPosition()));
//        }


//        marital = et_personal_marital.getText().toString();
        marital = spin_personal_marital.getSelectedItem().toString();

//        dependents = et_personal_dependents.getText().toString();
        dependents = spin_personal_dependents.getSelectedItem().toString();

//        education = et_personal_education.getText().toString();
        education = spin_personal_education.getSelectedItem().toString();

//        selfemployed = et_personal_selfemployed.getText().toString();
        selfemployed = spin_personal_selfemployed.getSelectedItem().toString();

        applicantincome = et_personal_applicantincome.getText().toString();
        coapplicantincome = et_personal_coapplicantincome.getText().toString();

//        credithistory = et_personal_credithistory.getText().toString();
//        credithistoryStored = spin_personal_credithistory.getSelectedItem().toString();

        creditHistoryPos = spin_personal_credithistory.getSelectedItemPosition();

        String[] credithistoryStoredVals = getResources().getStringArray(R.array.spinner_profile_credit);
//        String[] credithistoryDisplayVals = getResources().getStringArray(R.array.spinner_profile_credittext);

        credithistoryStored = Arrays.asList(credithistoryStoredVals).get(creditHistoryPos);


        //Writing Hashmap
        Map<String, Object> mHashmap = new HashMap<>();


        mHashmap.put("profile/address1", address1);
        mHashmap.put("profile/address2", address2);
        mHashmap.put("profile/city", city);
        mHashmap.put("profile/state", state);
        mHashmap.put("profile/mobile", mobile);
        mHashmap.put("profile/dob", dob);
        mHashmap.put("profile/gender", gender);
        mHashmap.put("profile/marital", marital);
        mHashmap.put("profile/dependents", dependents);
        mHashmap.put("profile/education", education);
        mHashmap.put("profile/selfemployed", selfemployed);
        mHashmap.put("profile/applicantincome", applicantincome);
        mHashmap.put("profile/coapplicantincome", coapplicantincome);
        mHashmap.put("profile/credithistory", credithistoryStored);
        //TODO hardcoded LO ID and client user role
        mHashmap.put("profile/loanOfficerId", "Pu9RVLXJSWO3hopm40tdYBaiHxd2");
        mHashmap.put("userRoles/client", true);

        Log.d("Save", mDatabaseReference.toString());
        mDatabaseReference.updateChildren(mHashmap);

        startActivity(new Intent(this, ProfileActivity.class));
    }

    private boolean hasContentSpin(Spinner spinner) {
        //check if spinner input has content
        boolean bHasContent = false;
        if (spinner.getSelectedItem().toString().trim().length() > 0) {
            bHasContent = true;
        }
        return bHasContent;
    }


}