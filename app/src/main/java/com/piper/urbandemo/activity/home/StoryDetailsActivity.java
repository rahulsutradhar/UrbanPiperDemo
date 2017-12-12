package com.piper.urbandemo.activity.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.piper.urbandemo.R;
import com.piper.urbandemo.model.TopStory;

/**
 * Created by developers on 12/12/17.
 */

public class StoryDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TopStory topStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        String strTopStory = getIntent().getExtras().getString("TOP_STORY");
        Gson gson = new Gson();
        topStory = gson.fromJson(strTopStory, TopStory.class);

        getSupportActionBar().setTitle(topStory.getTitle());
    }

}
