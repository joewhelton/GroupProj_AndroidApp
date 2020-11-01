package com.example.finaiapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class LoginActivityFirebaseUi extends AppCompatActivity {

    // FirebaseAuth
    private FirebaseAuth auth;
    // our REQUEST_CODE
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        // get instance of FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // check if there is a user logged in
        if (auth.getCurrentUser() != null) {
            // User is non-null, so we can go to the SignedInActivity
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        } else {
            // we don't have a user, so we need to authenticate one
            authenticateUser();
        }

    }

    private void authenticateUser() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build());

                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(false).build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
//                        .setTheme(R.style.CustomTheme)
//                        .setLogo(R.drawable.logo_vertical)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                REQUEST_CODE);
        // [END auth_fui_create_intent]
    }


    // need to handle the sign-in result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get the response from the intent
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // look for our request code
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in, move to SignedInActivity
                startActivity(new Intent(this, ProfileActivity.class));
                return;
                // ...
            } else {
                if (response == null) {
                    // User cancelled Sign-in
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    // Device has no network connection
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    // Unknown error occurred
                    return;
                }
            }
        }

    }
}

