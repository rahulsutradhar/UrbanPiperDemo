package com.piper.urbandemo.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.piper.urbandemo.R;
import com.piper.urbandemo.home.HomeActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by developers on 11/12/17.
 */

public class EmailLoginFormActivity extends AppCompatActivity {

    private static final String TAG = "EmailSinginActivity";
    private EditText emailEditText, passwordEditText;
    private Toolbar toolbar;
    private String emailText, passwordText;
    private Button signinButton;
    private FirebaseAuth mAuth;
    private boolean operationSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login_form);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        operationSignin = getIntent().getExtras().getBoolean("Signin");

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (operationSignin) {
            getSupportActionBar().setTitle("Sign In with Email");
        } else {
            getSupportActionBar().setTitle("Sign Up with Email");
        }

        //set views
        setViews();

        //Firebase Instance
        mAuth = FirebaseAuth.getInstance();

    }

    public void setViews() {
        emailEditText = (EditText) findViewById(R.id.email_text);
        passwordEditText = (EditText) findViewById(R.id.password_text);
        signinButton = (Button) findViewById(R.id.singin_button);

        if (operationSignin) {
            signinButton.setText("SIGN IN");
        } else {
            signinButton.setText("SIGN UP");
        }

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for empty form
                checkAndVerifyForm();
            }
        });
    }

    /**
     * Verify Form Data
     */
    public void checkAndVerifyForm() {
        if (verifyEmail()) {
            if (verifyPassword()) {
                if (operationSignin) {
                    //do sign in
                    doSignIn();
                } else {
                    //create new User
                    doSingUp();
                }
            } else {
                Toast.makeText(this, "Invalid Password. Must be atleast 6 character", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Verify Email
     */
    public boolean verifyEmail() {
        boolean flag = false;

        if (emailEditText.getText() != null) {
            emailText = emailEditText.getText().toString();
            if (!emailText.isEmpty()) {
                flag = emailPatternMatcher(emailText);
            }
        }

        return flag;
    }

    public boolean emailPatternMatcher(String email) {
        String pttn = "^\\D.+@.+\\.[a-z]+";
        Pattern p = Pattern.compile(pttn);
        Matcher m = p.matcher(email);

        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Verify Password
     */
    public boolean verifyPassword() {
        boolean flag = false;

        if (passwordEditText.getText() != null) {
            passwordText = passwordEditText.getText().toString();
            if (!passwordText.isEmpty()) {
                if (passwordText.length() >= 6) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * Proceed to signUP
     */
    public void doSingUp() {

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Login Successful; Now navigate to home page
                            Toast.makeText(EmailLoginFormActivity.this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

                            //navigate to home
                            navigateAfterLogin(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailLoginFormActivity.this, "Authentication failed. " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            //TODO
                        }

                        // ...
                    }
                });
    }

    /**
     * Do Sign In
     */

    public void doSignIn() {
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Login Successful; Now navigate to home page
                            Toast.makeText(EmailLoginFormActivity.this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

                            //navigate to home
                            navigateAfterLogin(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailLoginFormActivity.this, "Authentication failed. " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    /**
     * Navigate after login
     */
    public void navigateAfterLogin(FirebaseUser user) {
        //moved to home
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}

