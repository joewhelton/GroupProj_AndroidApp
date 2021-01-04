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

import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;

import org.tensorflow.lite.Interpreter;
import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoanEligibilityActivity extends AppCompatActivity {
    private TextView et_personal_first, et_personal_surname, et_personal_gender,
            et_personal_marital, et_personal_dependents, et_personal_education,
            et_personal_selfemployed, et_personal_applicantincome, et_personal_coapplicantincome,
            et_personal_credithistory;
    private EditText et_loan_amount, et_loan_term;
    private float appIncomef;
    private float coappIncomef, amountf, genderf, marriedf, dependentsf, educationf, selfemployedf, historyf, propertyareaf = 0;
    private float termf = 180;
    //new strings introduced for loans
    private String propertyArea;
    private String loanAmount;
    private String loanTerm;
    private Button btn_check_eligibility, btn_save, btn_apply;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private Interpreter interpreter;
    private float[][] loanData;
    private float answer;
    private TextView displans;

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
        //et_propertyarea = findViewById(R.id.edittext_propertyarea);
        et_loan_amount = findViewById(R.id.edittext_loan_amount);
        et_loan_term = findViewById(R.id.edittext_loan_term);
        btn_check_eligibility = findViewById(R.id.button_check_eligibility);
        btn_save = findViewById(R.id.button_save);
        btn_apply = findViewById(R.id.button_apply);
        displans = findViewById(R.id.tv_displAns);

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
                         Log.d("Profile", "Found profile for " + et_personal_first);
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
                     Toast.makeText(getApplicationContext(), "An unknown error has occurred", Toast.LENGTH_SHORT).show();
                 }
             }
        );

        //Download model or use model in app
        FirebaseCustomRemoteModel remoteModel =
                new FirebaseCustomRemoteModel.Builder("LoanPredictor").build();
        FirebaseModelManager.getInstance().getLatestModelFile(remoteModel)
                .addOnCompleteListener(new OnCompleteListener<File>() {
                    @Override
                    public void onComplete(@NonNull Task<File> task) {
                        File modelFile = task.getResult();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                        } else {
                            try {
                                InputStream inputStream = getAssets().open("loan.tflite");
                                byte[] model = new byte[inputStream.available()];
                                inputStream.read(model);
                                ByteBuffer buffer = ByteBuffer.allocateDirect(model.length)
                                        .order(ByteOrder.nativeOrder());
                                buffer.put(model);
                                interpreter = new Interpreter(buffer);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LoanEligibilityActivity.this, "Model not read", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

        //adding an onclicklistener to button
        btn_check_eligibility.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                writeLoanDetails();
            }
        });
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                apply();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void apply() {
        saveDetails();
        finish();
        startActivity(new Intent(this, SelectLoanOfficerActivity.class));
    }
    //Writes details to loanApplications in database

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writeLoanDetails() {
        //String propertyArea = "";
        String loanAmount = "";
        String loanTerm = "";
        //get new data from details entered here and put them into the querying array.

        loanAmount = et_loan_amount.getText().toString().trim();
        while (loanAmount.isEmpty()) {
            try {
                Float.parseFloat(loanAmount);
            } catch (Exception e) {
                Toast.makeText(LoanEligibilityActivity.this, "Please enter amount in digits", Toast.LENGTH_LONG).show();
                loanAmount = "";
                loanAmount = et_loan_amount.getText().toString().trim();
            }
        }
        amountf = Float.parseFloat(loanAmount);
        amountf = (float) Math.log(amountf);

        loanTerm = et_loan_term.getText().toString().trim();
        //here I have assumed that all loan terms are 10 -30 years
        //loan terms from 10 - 22years are taken as 180 months and 23-30years are taken as 360 months
        do {
            try {
                Float.parseFloat(loanTerm);
            } catch (Exception e) {
                Toast.makeText(LoanEligibilityActivity.this, "Please enter a term between 10 & 30 years", Toast.LENGTH_LONG).show();
                loanTerm = "";
            }
            termf = Float.parseFloat(loanTerm);
            if ((termf < 9) && (termf > 31)) {
                Toast.makeText(LoanEligibilityActivity.this, "Please enter a term between 10 & 30 years", Toast.LENGTH_LONG).show();
                loanTerm = "";
            }
        }
        while (loanTerm.isEmpty());
        //convert to months
        termf = termf * 12;

        // Gender = Male/Female/"" (empty string)
        // Married = Yes/No/"" (empty string)
        // Dependents = 0/1/2/3+
        // Graduate = Graduate/NotGraduate
        // SelfEmployed  Yes/No/""
        // Applicant Income (number)
        // Co-applicant income (number)
        // Credit History "" (empty string)/1/0

        //if any other data is missing it will redirect back to profile before querying the model
        try {
            if (et_personal_gender.getText().toString().equalsIgnoreCase("Female")) {
                genderf = 1;
            }
            if ((et_personal_marital.getText().toString().equalsIgnoreCase("Yes"))) marriedf = 1;

            switch (et_personal_dependents.getText().toString()) {
                case "0":
                    dependentsf = 0;
                    break;
                case "1":
                    dependentsf = 1;
                    break;
                case "2":
                    dependentsf = 2;
                    break;
                case "3+":
                    dependentsf = 3;
                    break;
            }

//            dependentsf = Float.parseFloat((et_personal_dependents.getText().toString()));
//            if (dependentsf > 2) dependentsf = 3;

            if ((et_personal_education.getText().toString()).equalsIgnoreCase("No"))
                educationf = 1;

            if ((et_personal_selfemployed.getText().toString()).equalsIgnoreCase("Yes"))
                selfemployedf = 1;

            appIncomef = Float.parseFloat((et_personal_applicantincome.getText().toString()));
            appIncomef = (float) Math.log(appIncomef);

            coappIncomef = Float.parseFloat((et_personal_coapplicantincome.getText().toString()));
            coappIncomef = (float) Math.log(appIncomef);

            if ((et_personal_credithistory.getText().toString()).equalsIgnoreCase("Yes"))
                historyf = 1;

        } catch (Exception e) {
            Toast.makeText(LoanEligibilityActivity.this, "Please complete your profile first", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, PersonalInfo.class));
            finish();
        }

        loanData = new float[][]{{genderf, marriedf, dependentsf, educationf, selfemployedf,
                appIncomef, coappIncomef, amountf, termf, historyf, propertyareaf}};

        //check if array is complete
        for (int col = 0; col < loanData[0].length; col++) {
            if (Objects.isNull(loanData[0][col])) {
                Toast.makeText(LoanEligibilityActivity.this, "Please complete your profile and application", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(this, PersonalInfo.class));
            }
        }

        //query the model with the input array
        try {
            answer = doInference(loanData);
            displans.setText(Float.toString(answer));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(LoanEligibilityActivity.this, "No answer", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveDetails() {
        loanAmount = et_loan_amount.getText().toString();
        loanTerm = et_loan_term.getText().toString();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        FirebaseUtil.openFbReference("loanApplications");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        //Writing Hashmap
        Map<String, Object> mHashmap = new HashMap<>();
        mHashmap.put("createdDate", dtf.format(now));
        mHashmap.put("clientId", firebaseAuth.getCurrentUser().getUid());
        mHashmap.put("married", et_personal_marital.getText().toString());
        mHashmap.put("dependents", et_personal_dependents.getText().toString());
        mHashmap.put("education", et_personal_education.getText().toString());
        mHashmap.put("selfemployed", et_personal_selfemployed.getText().toString());
        mHashmap.put("applicantIncome", et_personal_applicantincome.getText().toString());
        mHashmap.put("coappIncome", et_personal_coapplicantincome.getText().toString());
        mHashmap.put("credithistory", et_personal_credithistory.getText().toString());
        mHashmap.put("propertyArea", propertyArea);
        mHashmap.put("amount", loanAmount);
        mHashmap.put("term", loanTerm);
        mHashmap.put("Loan Model Answer", answer);
        mDatabaseReference.push().setValue(mHashmap);
        Toast.makeText(LoanEligibilityActivity.this, "Loan Details saved", Toast.LENGTH_LONG).show();
        finish();
        //startActivity(new Intent(this, ProfileActivity.class));
    }

    //query the model with array created by inputs
    public float doInference(float[][] input) {

        float[][] outputval = new float[1][1];
        interpreter.run(input, outputval);
        //get back data and put into a float to return.
        float inferredValue = outputval[0][0];
        return inferredValue;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rural:
                if (checked) {
                    propertyareaf = 0;
                    propertyArea = "rural";
                }
                break;
            case R.id.urban:
                if (checked) {
                    propertyareaf = 2;
                    propertyArea = "urban";
                }
                break;
            case R.id.semirural:
                if (checked) {
                    propertyareaf = 1;
                    propertyArea = "semirural";
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onPause() {
        super.onPause();
        saveDetails();
    }

    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(LoanEligibilityActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

}