package com.example.finaiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Register2Activity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonRegister;
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private TextView viewLogin;
    private AwesomeValidation awesomeValidation;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" + "(?=.*[0-9])" +
            "(?=.*[a-z])" + "(?=.*[A-Z])" + "(?=.*[a-zA-Z])" + "(?=.*[!@#$%^&+=])" + "(?=\\S+$)" +
            ".{8,}" +"$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUtil.openFbReference("clients");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);


        initView();
    }
    //initializing view objects
    private void initView(){
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        viewLogin = (TextView) findViewById(R.id.viewLogin);
        progressDialog = new ProgressDialog (this);
        buttonRegister.setOnClickListener(this);
        viewLogin.setOnClickListener(this);
        addValidationToViews();

    }
    //adding validation to views
    private void addValidationToViews() {
        awesomeValidation.addValidation(this, R.id.editTextEmail, Patterns.EMAIL_ADDRESS, R.string.invalid_email);
        awesomeValidation.addValidation(this, R.id.editTextPassword, PASSWORD_PATTERN, R.string.invalid_password);
        awesomeValidation.addValidation(this, R.id.editTextConfirmPassword, R.id.editTextPassword, R.string.invalid_confirm_password);

    }

    private void registerUser(){
        if (awesomeValidation.validate()) {
            //getting first name, surname, email and password from edit texts
            //Intent incoming = new Intent();
            final String firstname = getIntent().getStringExtra("name");
            final String surname = getIntent().getStringExtra("surname");
            final String email = editTextEmail.getText().toString().trim();
            System.out.println(firstname);
            String password = editTextPassword.getText().toString().trim();
            progressDialog.setMessage("Registering user...");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new
                    OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //create client and pass details to Firebase
                                Client client = new Client(firstname, surname, email);
                                mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(client).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Register2Activity.this, "Account created", Toast.LENGTH_LONG).show();
                                        onClick(viewLogin);
                                    }
                                });
                            } else {
                                //display error message here
                                Toast.makeText(Register2Activity.this, "You already have an account - go to Login", Toast.LENGTH_LONG).show();
                                onClick(viewLogin);
                            }
                            progressDialog.dismiss();
                        }
                    });


        }}



    @Override
    public void onClick(View view) {
        if(view == buttonRegister)
            registerUser();



        if(view == viewLogin){
            //open login activity when user clicks on the already registered textview
            startActivity(new Intent(this, LoginActivity.class));
        }


    }



}