package com.piper.urbandemo.activity.home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.piper.urbandemo.helper.DatabaseHelper;
import com.piper.urbandemo.helper.DateHelper;
import com.piper.urbandemo.helper.Keys;
import com.piper.urbandemo.helper.PreferenceManager;
import com.piper.urbandemo.model.TopStory;
import com.piper.urbandemo.network.Response.ResponseTopStoryId;

import java.util.ArrayList;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private PreferenceManager preferenceManager;
    private ArrayList<TopStory> topStoriesList;
    private TextView title, subtitle;
    private FrameLayout noItemFound, mainContent, progressBar, networkProblem;
    private RecyclerView recyclerView;
    private TopStoryAdapter adapter;
    private ArrayList<Long> topStoryIds;

    //for fetching top stories
    private int index = 0;
    private static final int MAX_ITEM_FETCH = 10;
    private boolean trackNetworkFailure = false;
    private boolean isDataFetchedFromCache = false;

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

        //fetch Top Stories
        checkAvailablitiyFromCache();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            //refresh data;
            refreshListData();
        } else if (item.getItemId() == R.id.action_signout) {
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
        networkProblem = (FrameLayout) findViewById(R.id.network_problem);
        recyclerView = (RecyclerView) findViewById(R.id.list_top_stories);

        networkProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try to recall data
                index = 0;
                requestTopStories();
            }
        });
    }

    /**
     * Initialize variables
     */
    public void setVaribles() {
        topStoriesList = new ArrayList<>();
        topStoryIds = new ArrayList<>();
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

        //clear all preferences and database
        DatabaseHelper.clearTopStories();
        preferenceManager.resetPreferences();
    }

    /**
     * Move to Login Screen
     */
    public void navigateToLoginScreen() {
        //move to login screen
        Intent intent = new Intent(this, com.piper.urbandemo.activity.authentication.SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Check if data is Available from Cache else Request Server
     */
    public void checkAvailablitiyFromCache() {
        int size = DatabaseHelper.getSizeTopStories();
        Toast.makeText(this, "Get Size - " + size, Toast.LENGTH_SHORT).show();
        if (size > 0) {
            isDataFetchedFromCache = true;
            topStoriesList.clear();
            topStoriesList.addAll(DatabaseHelper.getTopStories());
            displayTopStory();

            //display laste Update time
            displayLastUpdateTime();
        } else {
            isDataFetchedFromCache = false;
            //request Server
            requestTopStories();
        }
    }

    /**
     * Display Last Update Time
     */
    public void displayLastUpdateTime() {
        String lastUpdateTimeStamp = DateHelper.parseDate(preferenceManager.getStringPref(Keys.LAST_UPDATE_TIMESTAMP));
        subtitle.setText("Updated " + lastUpdateTimeStamp);
    }


    /**
     * DO Network call and fetch Top Stories ID
     */
    public void requestTopStories() {
        //clear top story ids
        topStoryIds.clear();
        //clear this top story list
        topStoriesList.clear();

        //update views
        networkProblem.setVisibility(View.GONE);
        mainContent.setVisibility(View.GONE);
        noItemFound.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        //rest call to fetch data
        UrbanApplication.getAPIService().
                fetchTopStoriesId("pretty")
                .enqueue(new Callback<ResponseTopStoryId>() {
                    @Override
                    public void onResponse(Call<ResponseTopStoryId> call, Response<ResponseTopStoryId> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    ResponseTopStoryId responseTopStoryId = response.body();

                                    if (responseTopStoryId.size() > 0) {
                                        //request with individual id anf fetch top stories
                                        topStoryIds.addAll(responseTopStoryId);

                                        //check fi the activity is not destroyed
                                        if (HomeActivity.this != null) {
                                            parseStoriesIdAndFetchStory();
                                        }
                                    } else {
                                        networkProblem.setVisibility(View.GONE);
                                        mainContent.setVisibility(View.GONE);
                                        noItemFound.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            }
                        } catch (NullPointerException npe) {
                            networkProblem.setVisibility(View.GONE);
                            mainContent.setVisibility(View.GONE);
                            noItemFound.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseTopStoryId> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, "Failed - " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        mainContent.setVisibility(View.GONE);
                        noItemFound.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        networkProblem.setVisibility(View.VISIBLE);

                    }
                });
    }

    /**
     * Parse Each Story
     */
    public synchronized void parseStoriesIdAndFetchStory() {

        if (index >= MAX_ITEM_FETCH) {
            //capture time when last updated
            captureCurrentTime();
            //display the list
            displayTopStory();
            //display the time
            displayLastUpdateTime();
        } else {

            String id = String.valueOf(topStoryIds.get(index)) + ".json";
            fetchIndividualStory(id);
            index++;
        }

    }

    /**
     * Fetch Individual Story
     */
    public synchronized void fetchIndividualStory(String topStoryId) {

        UrbanApplication.getAPIService()
                .fetchTopStory(topStoryId, "pretty")
                .enqueue(new Callback<TopStory>() {
                    @Override
                    public void onResponse(Call<TopStory> call, Response<TopStory> response) {

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                TopStory topStory = response.body();
                                topStoriesList.add(topStory);
                                trackNetworkFailure = false;

                                //check if the activity is not destroyed
                                if (HomeActivity.this != null) {
                                    //fetch next top stories
                                    parseStoriesIdAndFetchStory();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TopStory> call, Throwable t) {
                        trackNetworkFailure = true;

                        //check if the activity is not destroyed
                        if (HomeActivity.this != null) {
                            parseStoriesIdAndFetchStory();
                        }
                    }
                });
    }


    /**
     * Create Tope Story List
     */
    public void displayTopStory() {

        //check if the activity is not destroyed
        if (HomeActivity.this != null) {
            if (topStoriesList.size() > 0) {
                networkProblem.setVisibility(View.GONE);
                mainContent.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                noItemFound.setVisibility(View.GONE);

                Toast.makeText(HomeActivity.this, "Fetched " + topStoriesList.size() + " items", Toast.LENGTH_SHORT).show();

                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new TopStoryAdapter(this, topStoriesList);
                adapter.setFetchedFromCache(isDataFetchedFromCache);
                recyclerView.setAdapter(adapter);

                //data fetched from server save locally
                if (!isDataFetchedFromCache) {
                    DatabaseHelper.addTopStories(topStoriesList);
                }

            } else {
                if (trackNetworkFailure) {
                    networkProblem.setVisibility(View.VISIBLE);
                    mainContent.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    noItemFound.setVisibility(View.GONE);
                } else {
                    networkProblem.setVisibility(View.GONE);
                    mainContent.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    noItemFound.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * Refresh List Data and fetch again from server
     */
    public void refreshListData() {
        index = 0;
        isDataFetchedFromCache = false;
        //remove data from database
        DatabaseHelper.clearTopStories();

        //request data from server
        requestTopStories();

    }

    /**
     * Capture current time in Mili seconds
     */
    public void captureCurrentTime() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String timestamp = tsLong.toString();

        //put the data in preference manager
        preferenceManager.setStringPref(Keys.LAST_UPDATE_TIMESTAMP, timestamp);
    }

}
