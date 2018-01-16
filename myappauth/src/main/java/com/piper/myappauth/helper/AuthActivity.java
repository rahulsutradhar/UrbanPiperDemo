package com.piper.myappauth.helper;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by developers on 16/01/18.
 */

public class AuthActivity {

    private static FirebaseAuth firebaseAuth;
    private static Context context;

    /**
     * Inititialize
     */
    public static void initialize(Context context) {
        AuthActivity.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Check if user is Already Authenticated
     *
     * @return
     */
    public static boolean isAuthenticated() {
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


    public static FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    public static void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        AuthActivity.firebaseAuth = firebaseAuth;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        AuthActivity.context = context;
    }

    public static void signOut() {
        getFirebaseAuth().signOut();
    }
}
