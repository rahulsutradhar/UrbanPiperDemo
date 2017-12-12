package com.piper.urbandemo.home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.piper.urbandemo.R;
import com.piper.urbandemo.UrbanApplication;
import com.piper.urbandemo.adapter.TopStoryAdapter;
import com.piper.urbandemo.authentication.SigninActivity;
import com.piper.urbandemo.helper.Keys;
import com.piper.urbandemo.helper.PreferenceManager;
import com.piper.urbandemo.model.TopStory;
import com.piper.urbandemo.network.Response.ResponseTopStory;
import com.piper.urbandemo.network.Response.ResponseTopStoryId;

import java.util.ArrayList;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private PreferenceManager preferenceManager;
    private RealmList<TopStory> topStoriesList;
    private TextView title, subtitle;
    private FrameLayout noItemFound, mainContent, progressBar;
    private RecyclerView recyclerView;
    private TopStoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setViews();
        setVaribles();

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

    /**
     * Connect Views
     */
    public void setViews() {
        title = (TextView) findViewById(R.id.title);
        subtitle = (TextView) findViewById(R.id.sub_title);
        noItemFound = (FrameLayout) findViewById(R.id.noitemfound);
        mainContent = (FrameLayout) findViewById(R.id.main_content);
        progressBar = (FrameLayout) findViewById(R.id.progressLayout);
        recyclerView = (RecyclerView) findViewById(R.id.list_top_stories);
    }

    /**
     * Initialize variables
     */
    public void setVaribles() {
        topStoriesList = new RealmList<>();
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
                                parseStoriesIdAndFetchStory(responseTopStoryId);
                                Toast.makeText(HomeActivity.this, "Size - " + responseTopStoryId.size(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseTopStoryId> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, "Failed - " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Parse Each Story
     */
    public void parseStoriesIdAndFetchStory(ArrayList<Long> topStoriesId) {
        topStoriesList.clear();
        for (int i = 0; i < 5; i++) {
            String id = String.valueOf(topStoriesId.get(i)) + ".json";
            fetchIndividualStory(id, i);
        }
    }

    /**
     * Fetch Individual Story
     */
    public void fetchIndividualStory(String topStoryId, final int index) {

        UrbanApplication.getAPIService()
                .fetchTopStory(topStoryId, "pretty")
                .enqueue(new Callback<ResponseTopStory>() {
                    @Override
                    public void onResponse(Call<ResponseTopStory> call, Response<ResponseTopStory> response) {

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                ResponseTopStory responseTopStory = response.body();
                                topStoriesList.add(responseTopStory);
                                Log.d("INDEX", index + "");

                                if (index == 4) {
                                    updateUI();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseTopStory> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, "Failed - " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateUI() {
        Toast.makeText(HomeActivity.this, "Size of Real List " + topStoriesList.size(), Toast.LENGTH_SHORT).show();
        displayTopStory();
    }


    /**
     * Create Tope Story List
     */
    public void displayTopStory() {

        mainContent.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        noItemFound.setVisibility(View.GONE);

        Toast.makeText(HomeActivity.this, "Display List " + topStoriesList.size(), Toast.LENGTH_SHORT).show();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new TopStoryAdapter(this, topStoriesList);
        recyclerView.setAdapter(adapter);
        /*adapter.setData(topStoriesList);*/
    }

}
