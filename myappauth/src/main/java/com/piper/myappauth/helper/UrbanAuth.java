package com.piper.myappauth.helper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.piper.myappauth.activity.SigninActivity;


/**
 * Created by developers on 16/01/18.
 */

public class UrbanAuth extends AppCompatActivity {

    private static Context context;
    private static FirebaseAuth firebaseAuth;
    private static AuthenticationActivity authenticationActivity;

    public static void initialize(Context context) {
        UrbanAuth.context = context;
        UrbanAuth.firebaseAuth = FirebaseAuth.getInstance();
    }


    /**
     * Register a callback to know the status of the callback
     *
     * @param authenticationActivity
     */
    public static void registerCallback(AuthenticationActivity authenticationActivity) {
        UrbanAuth.authenticationActivity = authenticationActivity;

        if (checkIfUserAlreadyLoggedIn()) {
            authenticationActivity.isUserAuthenticated(true);
        } else {
            //TODO open singin Activity
            //Open Signin Activity
            context.startActivity(new Intent(context, SigninActivity.class));
        }

    }


    public static boolean checkIfUserAlreadyLoggedIn() {
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


    /**
     * Getter Setter
     */

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

    public AuthenticationActivity getAuthenticationActivity() {
        return authenticationActivity;
    }

    public void setAuthenticationActivity(AuthenticationActivity authenticationActivity) {
        this.authenticationActivity = authenticationActivity;
    }
}
