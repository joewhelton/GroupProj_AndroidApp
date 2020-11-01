package com.example.finaiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewRegister;
    private TextView textViewForgottenPassword;
    private AwesomeValidation awesomeValidation;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" + "(?=.*[0-9])" +
            "(?=.*[a-z])" + "(?=.*[A-Z])" + "(?=.*[a-zA-Z])" + "(?=.*[!@#$%^&+=])" + "(?=\\S+$)" +
            ".{8,}" +"$");

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    //progress dialog
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();


        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if(firebaseAuth.getCurrentUser() != null){
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        textViewRegister  = (TextView) findViewById(R.id.textViewRegister);
        textViewForgottenPassword  = (TextView) findViewById(R.id.textViewForgottenPassword);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonLogin.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);
        textViewForgottenPassword.setOnClickListener(this);
        addValidationToViews();
    }

    //adding validation to views
    private void addValidationToViews() {
        awesomeValidation.addValidation(this, R.id.editTextEmail, Patterns.EMAIL_ADDRESS, R.string.invalid_email);
        awesomeValidation.addValidation(this, R.id.editTextPassword, PASSWORD_PATTERN, R.string.invalid_password);
    }

    //method for user login
    private void userLogin(){
        if (awesomeValidation.validate()) {
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
       //if the email and password are valid
        //displaying a progress dialog
        progressDialog.setMessage("Logging in Please Wait...");
        progressDialog.show();
        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if(task.isSuccessful()){
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }

                     else {
                        //display some message here
                        Toast.makeText(LoginActivity.this, "Login details are incorrect", Toast.LENGTH_LONG).show();

                    }
                    }
                });

    }}

    @Override
    public void onClick(View view) {
        if (view == buttonLogin) {
            userLogin();
        }
        if (view == textViewRegister) {
            finish();
            startActivity(new Intent(getApplicationContext(), Register1Activity.class));
        }

        if (view == textViewForgottenPassword) {
            finish();
            startActivity(new Intent(getApplicationContext(), ForgottenPasswordActivity.class));

    }
}}