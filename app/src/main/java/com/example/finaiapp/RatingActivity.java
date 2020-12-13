package com.example.finaiapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RatingActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbFeedback;
    private EditText feedback;
    private RatingBar ratingBar;
    private Button btnSave;
    private int ratingValue;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // get database reference
        database = FirebaseDatabase.getInstance();
        dbFeedback = database.getReference().child("feedback");

        // make sure we are logged in
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        ratingValue = 0;
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        feedback = (EditText) findViewById(R.id.edittext_rating_feedback);
        btnSave = (Button) findViewById(R.id.button_rating_save);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                TextView ratingText = (TextView) findViewById(R.id.textview_rating_text);
                ratingValue = ((int)ratingBar.getRating());
                ratingText.setText(String.valueOf(v));
                switch (ratingValue) {
                    case 1:
                        ratingText.setText("Poor");
                        break;
                    case 2:
                        ratingText.setText("Needs improvement");
                        break;
                    case 3:
                        ratingText.setText("Good");
                        break;
                    case 4:
                        ratingText.setText("Great");
                        break;
                    case 5:
                        ratingText.setText("I love it!");
                        break;
                    default:
                        ratingText.setText("");
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFeedback();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveFeedback() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        // get fields from views
        String feedbackMsg = feedback.getText().toString().trim();

        //Writing Hashmap
        Map<String, Object> mHashmap = new HashMap<>();


        mHashmap.put("uid", uid );
        mHashmap.put("rating", ratingValue );
        mHashmap.put("msg", feedbackMsg );
        mHashmap.put("createdDate", dtf.format(now));

        dbFeedback.push().setValue(mHashmap);


        feedback.setText("");
        ratingBar.setRating(0);
        Toast.makeText(RatingActivity.this, "Thank you for sharing your feedback", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, ProfileActivity.class));

    }
}