package com.example.finaiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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
    private TextView et_personal_marital;
    private TextView et_personal_dependents;
    private Button btn_personal_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

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
        et_personal_marital = findViewById(R.id.edittext_personal_marital);
        et_personal_dependents = findViewById(R.id.edittext_personal_dependents);
        btn_personal_save = findViewById(R.id.button_personal_save);

    }


}