package com.example.finaiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;


import java.util.regex.Pattern;

public class Register1Activity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonNext;
    private EditText editTextFirstName, editTextSurname;
    private TextView viewLogin;
    private AwesomeValidation awesomeValidation;
    private ProgressDialog progressDialog;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" + "(?=.*[0-9])" +
            "(?=.*[a-z])" + "(?=.*[A-Z])" + "(?=.*[a-zA-Z])" + "(?=.*[!@#$%^&+=])" + "(?=\\S+$)" +
            ".{8,}" +"$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        initView();
    }
    //initializing view objects
    private void initView(){
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextSurname = (EditText) findViewById(R.id.editTextSurname);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        viewLogin = (TextView) findViewById(R.id.viewLogin);
        buttonNext.setOnClickListener(this);
        viewLogin.setOnClickListener(this);
        addValidationToViews();

    }
    //adding validation to views
    private void addValidationToViews() {
        awesomeValidation.addValidation(this, R.id.editTextFirstName, NAME_PATTERN, R.string.invalid_firstName);
        awesomeValidation.addValidation(this, R.id.editTextSurname, NAME_PATTERN, R.string.invalid_surname);
    }
    //checking validation
    private void checkValidation() {
        if (awesomeValidation.validate()) {
            //getting first name, surname from edit texts
            final String firstname = editTextFirstName.getText().toString().trim();
            final String surname = editTextSurname.getText().toString().trim();
            Intent intent = new Intent(getApplicationContext(), Register2Activity.class);
            intent.putExtra("name", firstname);
            intent.putExtra("surname", surname);
            System.out.println(firstname);
            startActivity(intent);
        }
    }
    @Override
    public void onClick(View view) {
        if(view == buttonNext)
            checkValidation();



        if(view == viewLogin){
            //open login activity when user clicks on the already registered textview
            startActivity(new Intent(this, LoginActivityFirebaseUi.class));
        }


    }}