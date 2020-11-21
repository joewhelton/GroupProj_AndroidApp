package com.example.finaiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HouseDataActivity extends AppCompatActivity {
    //variable declarations
    private EditText saleYr, saleMonth, saleDay, bedroom, bathroom, sqFtLivingSpace, sqFtLoftSpace, floor,
            sqFtAboveGround, sqFtBasement, yrBuilt, yrRenovated, zipCode, lati, longti, sqFtLiving15, sqFtLot15;
    private Button buttonHousePrices;
    private Spinner spinnerWaterfront, spinnerView, spinnerCondition, spinnerGrade;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_data);
        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUtil.openFbReference("houseData");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        //initialise views
        saleYr = (EditText) findViewById(R.id.saleYr);
        saleMonth = (EditText) findViewById(R.id.saleMonth);
        saleDay = (EditText) findViewById(R.id.saleDay);
        buttonHousePrices = (Button) findViewById(R.id.buttonViewReport);
        bedroom = (EditText) findViewById(R.id.noOfBedrooms);
        bathroom = (EditText) findViewById(R.id.noOfBathrooms);
        sqFtLivingSpace = (EditText) findViewById(R.id.sqFtLivingSpace);
        sqFtLoftSpace = (EditText) findViewById(R.id.sqFtLoftSpace);
        floor = (EditText) findViewById(R.id.noOfFloors);
        sqFtAboveGround = (EditText) findViewById(R.id.sqFtAboveGround);
        sqFtBasement = (EditText) findViewById(R.id.sqFtBasement);
        yrBuilt = (EditText) findViewById(R.id.yrBuilt);
        yrRenovated = (EditText) findViewById(R.id.yrRenovated);
        zipCode = (EditText) findViewById(R.id.zipcode);
        lati = (EditText) findViewById(R.id.latitude);
        longti = (EditText) findViewById(R.id.longtitude);
        sqFtLiving15 = (EditText) findViewById(R.id.sqFtLiving15);
        sqFtLot15 = (EditText) findViewById(R.id.sqFtLot15);
        spinnerWaterfront = (Spinner) findViewById(R.id.spinnerWaterfront);
        spinnerView = (Spinner) findViewById(R.id.spinnerView);
        spinnerCondition = (Spinner) findViewById(R.id.spinnerCondition);
        spinnerGrade = (Spinner) findViewById(R.id.spinnerGrade);

        //if the objects getcurrentuser method is null
        //means user is not logged in
        if (firebaseAuth.getCurrentUser() == null) {
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        //onClick method for View House Price report
        buttonHousePrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HouseData houseData = getUserInputs();
                writeHouseData(houseData);
            }
        });
    }

    //get user inputs and instantiate HousePrice class
    private HouseData getUserInputs() {
        //get user inputs
        int sale_yr = Integer.parseInt(saleYr.getText().toString());
        int sale_month = Integer.parseInt(saleMonth.getText().toString());
        int sale_day = Integer.parseInt(saleDay.getText().toString());
        int bedrooms = Integer.parseInt(bedroom.getText().toString());
        float bathrooms = Float.parseFloat(bathroom.getText().toString());
        float floors = Float.parseFloat(floor.getText().toString());
        int sqft_living = Integer.parseInt(sqFtLivingSpace.getText().toString());
        int sqft_lot = Integer.parseInt(sqFtLoftSpace.getText().toString());
        int sqft_above = Integer.parseInt(sqFtAboveGround.getText().toString());
        int sqft_basement = Integer.parseInt(sqFtBasement.getText().toString());
        int yr_Built = Integer.parseInt(yrBuilt.getText().toString());
        int yr_Renovated = Integer.parseInt(yrRenovated.getText().toString());
        int zipcode = Integer.parseInt(zipCode.getText().toString());
        float lat = Float.parseFloat(lati.getText().toString());
        float longt = Float.parseFloat(longti.getText().toString());
        int sqft_living15 = Integer.parseInt(sqFtLiving15.getText().toString());
        int sqft_lot15 = Integer.parseInt(sqFtLot15.getText().toString());
        //get spinner inputs
        String item1 = spinnerWaterfront.getSelectedItem().toString();
        String item2 = spinnerView.getSelectedItem().toString();
        String item3 = spinnerCondition.getSelectedItem().toString();
        String item4 = spinnerGrade.getSelectedItem().toString();
        //instantiate House Price class using user inputs
        return new HouseData(sale_yr, sale_month, sale_day, bedrooms, bathrooms,
                sqft_living, sqft_lot, floors, Integer.parseInt(item1), Integer.parseInt(item2), Integer.parseInt(item3),
                Integer.parseInt(item4),
                sqft_above, sqft_basement, yr_Built, yr_Renovated, zipcode, lat, longt, sqft_living15, sqft_lot15);

    }

    //write data to Firebase database using house object
    private void writeHouseData(HouseData house) {
        mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(house).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(HouseDataActivity.this, "Data saved", Toast.LENGTH_LONG).show();
            }
        });
    }


}