package com.piper.urbandemo.home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.piper.urbandemo.R;
import com.piper.urbandemo.UrbanApplication;
import com.piper.urbandemo.authentication.SigninActivity;
import com.piper.urbandemo.helper.Keys;
import com.piper.urbandemo.helper.PreferenceManager;
import com.piper.urbandemo.network.Response.ResponseTopStoryId;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize Preference Manager
        preferenceManager = new PreferenceManager(this);

        requestTopStories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_signout) {
            //do signout operation
            doSignout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void doSignout() {

        //if user Logged in Via Google
        if (preferenceManager.getBoooleanPref(Keys.GOOGLE_LOGIN)) {
            preferenceManager.setBooleanPref(Keys.GOOGLE_LOGIN, false);
            FirebaseAuth.getInstance().signOut();
            navigateToLoginScreen();
        }
        //if user Logged in via Email
        else if (preferenceManager.getBoooleanPref(Keys.EMAIL_LOGIN)) {
            preferenceManager.setBooleanPref(Keys.EMAIL_LOGIN, false);
            FirebaseAuth.getInstance().signOut();
            navigateToLoginScreen();
        }
        //If user Logged in  via Phone
        else if (preferenceManager.getBoooleanPref(Keys.PHONE_LOGIN)) {
            preferenceManager.setBooleanPref(Keys.PHONE_LOGIN, false);
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            navigateToLoginScreen();
                        }
                    });
        }
    }

    /**
     * Move to Login Screen
     */
    public void navigateToLoginScreen() {
        //move to login screen
        Intent intent = new Intent(this, SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    /**
     * DO Network call and fetch Top Stories ID
     */
    public void requestTopStories() {

        UrbanApplication.getAPIService().
                fetchTopStoriesId("pretty")
                .enqueue(new Callback<ResponseTopStoryId>() {
                    @Override
                    public void onResponse(Call<ResponseTopStoryId> call, Response<ResponseTopStoryId> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                ResponseTopStoryId responseTopStoryId = response.body();
                                ArrayList<Long> topStoriesId = responseTopStoryId;
                                Toast.makeText(HomeActivity.this, "Size - " + topStoriesId.size(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseTopStoryId> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, "Failed - " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
