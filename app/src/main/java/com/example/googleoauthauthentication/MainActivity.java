package com.example.googleoauthauthentication;


import static android.content.ContentValues.TAG;


import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.content.IntentSender;

import android.os.Bundle;

import android.util.Log;

import android.view.View;

import android.widget.TextView;


import com.google.android.gms.auth.api.identity.BeginSignInRequest;

import com.google.android.gms.auth.api.identity.BeginSignInResult;

import com.google.android.gms.auth.api.identity.Identity;

import com.google.android.gms.auth.api.identity.SignInClient;

import com.google.android.gms.auth.api.identity.SignInCredential;

import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private SignInClient oneTapClient;

    private BeginSignInRequest signInRequest;

    private static final int REQ_ONE_TAP = 100;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        textView = findViewById(R.id.textView);

        oneTapClient = Identity.getSignInClient(this);

        signInRequest = BeginSignInRequest.builder()

                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()

                        .setSupported(true)

                        .build())

                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()

                        .setSupported(true)

                        // Your server's client ID, not your Android client ID.

                        .setServerClientId("1089243161418-2pbs7dbumnm2jgai4rt3aafnpt1omdb2.apps.googleusercontent.com") // TODO

                        // Only show accounts previously used to sign in.

                        .setFilterByAuthorizedAccounts(false)

                        .build())

                // Automatically sign in when exactly one credential is retrieved.

                .setAutoSelectEnabled(true)

                .build();

    }

    public void buttonGoogleSignIn(View view) {


        oneTapClient.beginSignIn(signInRequest)

                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {

                    @Override

                    public void onSuccess(BeginSignInResult result) {

                        try {

                            startIntentSenderForResult(

                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,

                                    null, 0, 0, 0);

                        } catch (IntentSender.SendIntentException e) {

                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());

                        }

                    }

                })

                .addOnFailureListener(this, new OnFailureListener() {

                    @Override

                    public void onFailure(@NonNull Exception e) {

                        // No saved credentials found. Launch the One Tap sign-up flow, or

                        // do nothing and continue presenting the signed-out UI.

                        Log.d(TAG, e.getLocalizedMessage());

                    }

                });

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQ_ONE_TAP:

                try {

                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);

                    String idToken = credential.getGoogleIdToken();

                    String username = credential.getId();

                    String password = credential.getPassword();

                    textView.setText("Authentication done.\nUsername is " + username);

                    if (idToken != null) {

                        // Got an ID token from Google. Use it to authenticate

                        // with your backend.

                        Log.d(TAG, "Got ID token.");

                    } else if (password != null) {

                        // Got a saved username and password. Use them to authenticate

                        // with your backend.

                        Log.d(TAG, "Got password.");

                    }

                } catch (ApiException e) {

                    textView.setText(e.toString());

                }

                break;

        }

    }

}