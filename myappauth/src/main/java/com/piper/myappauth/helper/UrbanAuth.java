package com.piper.myappauth.helper;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by developers on 16/01/18.
 */

public class UrbanAuth {

    private static Context context;
    private static FirebaseAuth firebaseAuth;
    private AuthenticationActivity authenticationActivity;

    public void initialize(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }


    public void registerCallback(AuthenticationActivity authenticationActivity) {
        this.authenticationActivity = authenticationActivity;

        if (checkIfUserAlreadyLoggedIn()) {
            authenticationActivity.isUserAuthenticated(true);
        } else {
            //TODO open singin Activity
        }

    }


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        UrbanAuth.context = context;
    }

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public static void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        UrbanAuth.firebaseAuth = firebaseAuth;
    }

    public boolean checkIfUserAlreadyLoggedIn() {
        boolean isLoggedIn = false;

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = getFirebaseAuth().getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                Toast.makeText(context, "Logged in as " + currentUser.getDisplayName() + " !!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Welcome", Toast.LENGTH_SHORT).show();
            }
            isLoggedIn = true;
        }

        return isLoggedIn;
    }
}
