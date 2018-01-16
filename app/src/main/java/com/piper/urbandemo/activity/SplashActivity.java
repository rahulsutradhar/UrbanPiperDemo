package com.piper.urbandemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.piper.myappauth.activity.SigninActivity;
import com.piper.myappauth.helper.AuthenticationActivity;
import com.piper.myappauth.helper.Keys;
import com.piper.myappauth.helper.UrbanAuth;
import com.piper.urbandemo.R;
import com.piper.urbandemo.UrbanApplication;
import com.piper.urbandemo.activity.home.HomeActivity;

/**
 * Created by developers on 13/01/18.
 */

public class SplashActivity extends AppCompatActivity {

    private static final int RC_AUTH_RESULT = 4000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SigninActivity.initialize(this);

        if (SigninActivity.isAuthenticated()) {
            navigateToHome();
        } else {
            //start activity for authentication
            startActivityForResult(new Intent(this, SigninActivity.class), RC_AUTH_RESULT);
        }
    }

    /**
     * Navigate to Home
     */
    public void navigateToHome() {
        //move to login screen
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_AUTH_RESULT) {
            if (data != null) {
                if (data.getBooleanExtra(Keys.USER_AUTHENTICATED, false)) {
                    navigateToHome();
                }
            } else {
                finish();
            }
        }
    }
}
