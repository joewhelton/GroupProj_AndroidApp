package com.example.finaiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
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

public class HouseDataActivity extends AppCompatActivity {
    //variable declarations
    private EditText saleYr, saleMonth, saleDay, bedroom, bathroom, sqFtLivingSpace, sqFtLoftSpace, floor,
            sqFtAboveGround, sqFtBasement, yrBuilt, yrRenovated, zipCode, lati, longti, sqFtLiving15, sqFtLot15;
    private Button buttonHousePrices;
    private Spinner spinnerWaterfront, spinnerView, spinnerCondition, spinnerGrade;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private Interpreter interpreter;
    private float[][] houseInputData;
    private float prediction;
    private TextView predictPrice;

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
        predictPrice = (TextView) findViewById(R.id.predictedPrice);
        float[][] houseInputData;

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
            @Override
            public void onClick(View v) {
                HouseData houseData = getUserInputs();
                writeHouseData(houseData);
            }
        });
    }

    //get user inputs and instantiate HousePrice class
    private HouseData getUserInputs() {
        int sale_yr, sale_month, sale_day, bedrooms, sqft_living, sqft_lot, waterfront, viewt, condition, grade,
                sqft_above, sqft_basement, yr_built, yr_renovated, zipcode, sqft_living15, sqft_lot15;
        float bathrooms, floors, lat, longt, prediction;

                //get user inputs, check if empty and load default data if so
        if (hasContent(saleYr)) { sale_yr = Integer.parseInt(saleYr.getText().toString());
        } else { sale_yr = 2014;
        }
        if (hasContent(saleMonth)) { sale_month = Integer.parseInt(saleMonth.getText().toString());
        } else { sale_month = 6;
        }
        if (hasContent(saleDay)) { sale_day = Integer.parseInt(saleDay.getText().toString());
        } else { sale_day = 15;
        }
        if (hasContent(bedroom)) { bedrooms = Integer.parseInt(bedroom.getText().toString());
        } else { bedrooms = 3;
        }
        if (hasContent(bathroom)) { bathrooms = Float.parseFloat(bathroom.getText().toString());
        } else { bathrooms = 2;
        }
        if (hasContent(sqFtLivingSpace)) { sqft_living = Integer.parseInt(sqFtLivingSpace.getText().toString());
        } else { sqft_living = 2079;
        }
        if (hasContent(sqFtLoftSpace)) { sqft_lot = Integer.parseInt(sqFtLoftSpace.getText().toString());
        } else { sqft_lot = 15157;
        }
        if (hasContent(floor)) { floors = Float.parseFloat(floor.getText().toString());
        } else { floors = 1;
        }
        if (hasContentSpin(spinnerWaterfront)) { waterfront = Integer.parseInt(spinnerWaterfront.getSelectedItem().toString());
        } else { waterfront = 0;
        }
        if (hasContentSpin(spinnerView)) { viewt = Integer.parseInt(spinnerView.getSelectedItem().toString());
        } else { viewt = 0;
        }
        if (hasContentSpin(spinnerCondition)) { condition = Integer.parseInt(spinnerCondition.getSelectedItem().toString());
        } else { condition = 3;
        }
        if (hasContentSpin(spinnerGrade)) { grade = Integer.parseInt(spinnerGrade.getSelectedItem().toString());
        } else { grade = 7;
        }
        if (hasContent(sqFtAboveGround)) { sqft_above = Integer.parseInt(sqFtAboveGround.getText().toString());
        } else { sqft_above = 1789;
        }
        if (hasContent(sqFtBasement)) { sqft_basement = Integer.parseInt(sqFtBasement.getText().toString());
        } else { sqft_basement = 290;
        }
        if (hasContent(yrBuilt)) { yr_built = Integer.parseInt(yrBuilt.getText().toString());
        } else { yr_built = 1971;
        }
        if (hasContent(yrRenovated)) { yr_renovated = Integer.parseInt(yrRenovated.getText().toString());
        } else { yr_renovated = 0;
        }
        if (hasContent(zipCode)) { zipcode = Integer.parseInt(zipCode.getText().toString());
        } else { zipcode = 98077;
        }
        if (hasContent(lati)) { lat = Float.parseFloat(lati.getText().toString());
        } else { lat = 47;
        }
        if (hasContent(longti)) { longt = Float.parseFloat(longti.getText().toString());
        } else { longt = 122;
        }
        if (hasContent(sqFtLiving15)) { sqft_living15 = Integer.parseInt(sqFtLiving15.getText().toString());
        } else { sqft_living15 = 1987;
        }
        if (hasContent(sqFtLot15)) { sqft_lot15 = Integer.parseInt(sqFtLot15.getText().toString());
        } else { sqft_lot15 = 12944;
        }
        prediction = 0;

        //create 2d array for inference to model with mean values
        houseInputData = new float[][] { {sale_yr, sale_month, sale_day, bedrooms, bathrooms, sqft_living, sqft_lot, floors,
                waterfront, viewt, condition, grade, sqft_above, sqft_basement, yr_built, yr_renovated, zipcode, lat, longt, sqft_living15, sqft_lot15}};
        try {
            prediction = doInference(houseInputData);
            predictPrice.setText(Float.toString(prediction));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(HouseDataActivity.this, "No inference", Toast.LENGTH_LONG).show();

        }
        //instantiate House Price class using user inputs
        return new HouseData(sale_yr, sale_month, sale_day, bedrooms, bathrooms,
                sqft_living, sqft_lot, floors, waterfront, viewt, condition,
                grade, sqft_above, sqft_basement, yr_built, yr_renovated, zipcode, lat, longt, sqft_living15, sqft_lot15, prediction);
    }

    private boolean hasContent(EditText et) {
        // Check if text input has content
        boolean bHasContent = false;
        if (et.getText().toString().trim().length() > 0) {
            bHasContent = true;
        }
        return bHasContent;
    }
    private boolean hasContentSpin(Spinner spinner) {
        //check if spinner input has content
        boolean bHasContent = false;
        if (spinner.getSelectedItem().toString().trim().length() > 0) {
            bHasContent = true;
        }
        return bHasContent;
    }

    //query the model with array created by inputs
    public float doInference(float[][] input){

        float[][] outputval = new float[1][1];
        interpreter.run(input, outputval);
        //get back data and put into a float to return.
        float inferredValue = outputval[0][0];
        return inferredValue;
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