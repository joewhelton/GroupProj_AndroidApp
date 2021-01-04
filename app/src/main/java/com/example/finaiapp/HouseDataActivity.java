package com.example.finaiapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HouseDataActivity extends AppCompatActivity {
    //variable declarations
    private EditText bedroom, sqFtLivingSpace, sqFtLot, sqFtAboveGround, sqFtLot15;
    //private EditText saleYr, saleMonth, saleDay,bathroom, floor, sqFtBasement, yrBuilt, yrRenovated, zipCode, lati, longti, sqFtLiving15;
    private Button buttonHousePrices, buttonPropertyTax;
    private Spinner spinnerGrade;
    //private Spinner spinnerWaterfront, spinnerView, spinnerCondition;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private Interpreter interpreter;
    private float[][] houseInputData;
    private float prediction, inferredValue;
    private TextView predictPrice, propertyTax;

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
        //saleYr = (EditText) findViewById(R.id.saleYr);
        //saleMonth = (EditText) findViewById(R.id.saleMonth);
        //saleDay = (EditText) findViewById(R.id.saleDay);
        buttonHousePrices = (Button) findViewById(R.id.buttonViewReport);
        buttonPropertyTax = (Button) findViewById(R.id.buttonViewTax);
        bedroom = (EditText) findViewById(R.id.noOfBedrooms);
        //bathroom = (EditText) findViewById(R.id.noOfBathrooms);
        sqFtLivingSpace = (EditText) findViewById(R.id.sqFtLivingSpace);
        sqFtLot = (EditText) findViewById(R.id.sqFtLot);
        //floor = (EditText) findViewById(R.id.noOfFloors);
        sqFtAboveGround = (EditText) findViewById(R.id.sqFtAboveGround);
        //sqFtBasement = (EditText) findViewById(R.id.sqFtBasement);
        //yrBuilt = (EditText) findViewById(R.id.yrBuilt);
        //yrRenovated = (EditText) findViewById(R.id.yrRenovated);
        //zipCode = (EditText) findViewById(R.id.zipcode);
        //lati = (EditText) findViewById(R.id.latitude);
        //longti = (EditText) findViewById(R.id.longtitude);
        //sqFtLiving15 = (EditText) findViewById(R.id.sqFtLiving15);
        sqFtLot15 = (EditText) findViewById(R.id.sqFtLot15);
        //spinnerWaterfront = (Spinner) findViewById(R.id.spinnerWaterfront);
        //spinnerView = (Spinner) findViewById(R.id.spinnerView);
        //spinnerCondition = (Spinner) findViewById(R.id.spinnerCondition);
        spinnerGrade = (Spinner) findViewById(R.id.spinnerGrade);
        predictPrice = (TextView) findViewById(R.id.predictedPrice);
        propertyTax = (TextView) findViewById(R.id.propertyTax);

        //if the objects getcurrentuser method is null
        //means user is not logged in
        if (firebaseAuth.getCurrentUser() == null) {
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        //Download model or use model in app
        FirebaseCustomRemoteModel remoteModel =
                new FirebaseCustomRemoteModel.Builder("hsePriceModel").build();
        FirebaseModelManager.getInstance().getLatestModelFile(remoteModel)
                .addOnCompleteListener(new OnCompleteListener<File>() {
                    @Override
                    public void onComplete(@NonNull Task<File> task) {
                        File modelFile = task.getResult();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                        } else {
                            try {
                                InputStream inputStream = getAssets().open("fullHsePrice.tflite");
                                byte[] model = new byte[inputStream.available()];
                                inputStream.read(model);
                                ByteBuffer buffer = ByteBuffer.allocateDirect(model.length)
                                        .order(ByteOrder.nativeOrder());
                                buffer.put(model);
                                interpreter = new Interpreter(buffer);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(HouseDataActivity.this, "Model not read", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

        //onClick method for View House Price report
        buttonHousePrices.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                HouseData houseData = getUserInputs();
                writeHouseData(houseData);
            }
        });
        //predict what property tax will be
        buttonPropertyTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inferredValue!=0.0f) {
                    float taxRate = (float) 0.00180;
                    taxRate = taxRate*inferredValue;
                    String formatTax = String.format("%.02f", taxRate);
                    propertyTax.setText("$"+formatTax);
                }
                else Toast.makeText(HouseDataActivity.this, "Make a prediction first", Toast.LENGTH_LONG).show();
            }
        });
    }

    //get user inputs and instantiate HousePrice class
    private HouseData getUserInputs() {
        float bedrooms, sqft_living, sqft_lot, grade, sqft_above, sqft_lot15;
        String[] mean_hse_values = getResources().getStringArray(R.array.mean_hse_price);
        String[] std_hse_values = getResources().getStringArray(R.array.std_hse_values);
        float prediction;
        //float sale_yr, sale_month, sale_day, waterfront, viewt, condition, sqft_basement, yr_built, yr_renovated, zipcode, sqft_living15;
        //float bathrooms, floors, lat, longt;   removed for improved model

                //get user inputs, check if empty and load default data if so
                //some removed for improved model
        //if (hasContent(saleYr)) { sale_yr = Integer.parseInt(saleYr.getText().toString()); } else { sale_yr = 2014; }
        //if (hasContent(saleMonth)) { sale_month = Integer.parseInt(saleMonth.getText().toString()); } else { sale_month = 6; }
        //if (hasContent(saleDay)) { sale_day = Integer.parseInt(saleDay.getText().toString()); } else { sale_day = 15; }
        if (hasContent(bedroom)) {
            bedrooms = Float.parseFloat(bedroom.getText().toString()) - Float.parseFloat(mean_hse_values[0]);
        } else { bedrooms = Float.parseFloat(mean_hse_values[0]);
        }
        bedrooms = bedrooms /Float.parseFloat(std_hse_values[0]);
        //if (hasContent(bathroom)) { bathrooms = Float.parseFloat(bathroom.getText().toString()); } else { bathrooms = 2; }
        if (hasContent(sqFtLivingSpace)) {
            sqft_living = Float.parseFloat(sqFtLivingSpace.getText().toString()) - Float.parseFloat(mean_hse_values[1]);
        } else { sqft_living = Float.parseFloat(mean_hse_values[1]);
        }
        sqft_living = sqft_living/Float.parseFloat(std_hse_values[1]);
        if (hasContent(sqFtLot)) { sqft_lot = Integer.parseInt(sqFtLot.getText().toString()) - Float.parseFloat(mean_hse_values[2]);
        } else { sqft_lot = Float.parseFloat(mean_hse_values[2]); }
        sqft_lot = sqft_lot/Float.parseFloat(std_hse_values[2]);
        //if (hasContent(floor)) { floors = Float.parseFloat(floor.getText().toString()); } else { floors = 1; }
        //if (hasContentSpin(spinnerWaterfront)) { waterfront = Integer.parseInt(spinnerWaterfront.getSelectedItem().toString()); } else { waterfront = 0; }
        //if (hasContentSpin(spinnerView)) { viewt = Integer.parseInt(spinnerView.getSelectedItem().toString()); } else { viewt = 0; }
        //if (hasContentSpin(spinnerCondition)) { condition = Integer.parseInt(spinnerCondition.getSelectedItem().toString()); } else { condition = 3; }
        grade = spinnerGrade.getSelectedItemPosition();
        if (grade == 0) {grade = Float.parseFloat(mean_hse_values[3]);}
        else {grade = grade+2;
            grade = grade - Float.parseFloat(mean_hse_values[3]);}
        grade = grade/Float.parseFloat(std_hse_values[3]);
        if (hasContent(sqFtAboveGround)) { sqft_above = Integer.parseInt(sqFtAboveGround.getText().toString()) - Float.parseFloat(mean_hse_values[4]); }
        else { sqft_above = Float.parseFloat(mean_hse_values[4]); }
        sqft_above = sqft_above/Float.parseFloat(std_hse_values[4]);
        //if (hasContent(sqFtBasement)) { sqft_basement = Integer.parseInt(sqFtBasement.getText().toString()); } else { sqft_basement = 290; }
        //if (hasContent(yrBuilt)) { yr_built = Integer.parseInt(yrBuilt.getText().toString()); } else { yr_built = 1971; }
        //if (hasContent(yrRenovated)) { yr_renovated = Integer.parseInt(yrRenovated.getText().toString()); } else { yr_renovated = 0; }
        //if (hasContent(zipCode)) { zipcode = Integer.parseInt(zipCode.getText().toString()); } else { zipcode = 98077; }
        //if (hasContent(lati)) { lat = Float.parseFloat(lati.getText().toString()); } else { lat = 47; }
        //if (hasContent(longti)) { longt = Float.parseFloat(longti.getText().toString()); } else { longt = 122; }
        //if (hasContent(sqFtLiving15)) { sqft_living15 = Integer.parseInt(sqFtLiving15.getText().toString()); } else { sqft_living15 = 1987; }
        if (hasContent(sqFtLot15)) {
            sqft_lot15 = Float.parseFloat(sqFtLot15.getText().toString()) - Float.parseFloat(mean_hse_values[5]);
        } else { sqft_lot15 = Float.parseFloat(mean_hse_values[5]); }
        sqft_lot15 = sqft_lot15/Float.parseFloat(std_hse_values[5]);

        prediction = 0;

        //create 2d array for inference to model with mean values
        houseInputData = new float[][] { {bedrooms, sqft_living, sqft_lot, grade, sqft_above, sqft_lot15}};

        //houseInputData = new float[][] { {sale_yr, sale_month, sale_day,  bathrooms, floors, waterfront, viewt, condition, sqft_basement, yr_built, yr_renovated, zipcode, lat, longt, sqft_living15}};
        try {
            prediction = doInference(houseInputData);
            String formatPredict = String.format("%.02f", prediction);
            predictPrice.setText("$"+formatPredict);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(HouseDataActivity.this, "No price prediction", Toast.LENGTH_LONG).show();

        }
        //instantiate House Price class using user inputs
        return new HouseData(bedrooms, sqft_living, sqft_lot, grade, sqft_above, sqft_lot15, prediction);
        //return new HouseData(sale_yr, sale_month, sale_day, bathrooms, floors, waterfront, viewt, condition, sqft_basement, yr_built, yr_renovated, zipcode, lat, longt, sqft_living15);
    }

    private boolean hasContent(EditText et) {
        // Check if text input has content
        boolean bHasContent = false;
        if (et.getText().toString().trim().length() > 0) {
            bHasContent = true;
        }
        return bHasContent;
    }
    //private boolean hasContentSpin(Spinner spinner) {
        //check if spinner input has content
        //boolean bHasContent = false;
        //if (spinner.getSelectedItem().toString().trim().length() > 0) {
            //bHasContent = true;
        //}
        //return bHasContent;
    //}

    //query the model with array created by inputs
    public float doInference(float[][] input){

        float[][] outputval = new float[1][1];
        interpreter.run(input, outputval);
        //get back data and put into a float to return.
        inferredValue = outputval[0][0];
        return inferredValue;
    }

    //write data to Firebase database using house object
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void writeHouseData(HouseData house) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        house.setUid(firebaseAuth.getCurrentUser().getUid());
        house.setCreatedDate(dtf.format(now));
        mDatabaseReference.push().setValue(house).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(HouseDataActivity.this, "Data saved", Toast.LENGTH_LONG).show();
            }
        });
    }
}