package com.piper.myappauth.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.piper.myappauth.R;
import com.piper.myappauth.helper.Keys;

import java.util.Arrays;
import java.util.List;

/**
 * Created by developers on 10/12/17.
 */

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static FirebaseAuth mAuth;
    private LinearLayout googleSigninButton;
    private static Context context;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private static final int RC_PHONE_SIGN_IN = 9002;
    private static final int RC_AUTH_RESULT = 4000;
    private ProgressBar progressBar;
    private Button emailSignin, emailSignup, phoneSignin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //Connect views
        setViews();

        //initiliaze firebase
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    /**
     * Set Views: connect xml with JAVA codes
     */
    public void setViews() {
        googleSigninButton = (LinearLayout) findViewById(R.id.google_siginin_button);
        emailSignin = (Button) findViewById(R.id.email_button_signin);
        emailSignup = (Button) findViewById(R.id.email_button_signup);
        phoneSignin = (Button) findViewById(R.id.sign_with_phone);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        //google login Button click listener
        googleSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //perform google signin
                doGoogleLogin();
            }
        });

        //email singin Button click listener
        emailSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSinginForm();
            }
        });

        //email create new user
        emailSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpForm();
            }
        });

        //Phone Signin
        phoneSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //perform signin using phone number
                doPhoneNumberAuthentication();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Perform Google Login Operation
     */
    public void doGoogleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    /**
     * Email Login Singin
     */
    public void openSinginForm() {
        Intent intent = new Intent(this, EmailLoginFormActivity.class);
        intent.putExtra("Signin", true);
        startActivityForResult(intent, RC_AUTH_RESULT);
    }

    /**
     * Perform Phone Number Authentication
     */
    public void doPhoneNumberAuthentication() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false, true)
                        .setAvailableProviders(providers)
                        .setTheme(R.style.CustomFirebaseTheme)
                        .build(),
                RC_PHONE_SIGN_IN);
    }

    /**
     * Create User
     */
    public void openSignUpForm() {
        Intent intent = new Intent(this, EmailLoginFormActivity.class);
        intent.putExtra("Signin", false);
        startActivityForResult(intent, RC_AUTH_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google login callback
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
                Toast.makeText(SigninActivity.this, "Signin Cancelled", Toast.LENGTH_SHORT).show();
            }
        }//Phone Sign callback
        else if (requestCode == RC_PHONE_SIGN_IN) {

            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == ResultCodes.OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(SigninActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

                //navigate to home
                navigateAfterLogin();
            } else {
                // Sign in failed, check response for error code
                // ...
                Toast.makeText(SigninActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RC_AUTH_RESULT) {
            navigateAfterLogin();
        }
    }

    /**
     * Get credential from Firebase for Google Login
     *
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Login Successful; Now navigate to home page
                            Toast.makeText(SigninActivity.this, "Welcome " + user.getDisplayName() + " !!", Toast.LENGTH_SHORT).show();

                            navigateAfterLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Navigate after login
     */
    public void navigateAfterLogin() {
        Intent intent = new Intent();
        intent.putExtra(Keys.USER_AUTHENTICATED, true);
        //moved to home
        setResult(RC_AUTH_RESULT, intent);
        finish();
    }

    public static FirebaseAuth getmAuth() {
        return mAuth;
    }

    public static void setmAuth(FirebaseAuth mAuth) {
        SigninActivity.mAuth = mAuth;
    }
}
