package com.piper.urbandemo.activity.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.piper.urbandemo.R;
import com.piper.urbandemo.adapter.ViewPagerAdapter;
import com.piper.urbandemo.fragment.ArticleFragment;
import com.piper.urbandemo.fragment.CommentFragment;
import com.piper.urbandemo.model.TopStory;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by developers on 12/12/17.
 */

public class StoryDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TopStory topStory;
    private TabLayout tabLayout;
    private ViewPager pager;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView titleText, urlText, usernameText, timestampText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_details);


        //toolbar settings
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

        /*getSupportActionBar().setTitle(topStory.getTitle());*/

        //initilaize views
        setViews();
    }


    public void setViews() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        pager = (ViewPager) findViewById(R.id.pager);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_layout);
        titleText = (TextView) findViewById(R.id.title_text);
        urlText = (TextView) findViewById(R.id.url);
        usernameText = (TextView) findViewById(R.id.username);
        timestampText = (TextView) findViewById(R.id.time_stamp);


        //setup viewpager
        setupViewPagerAndTabs();

        collapsingToolbarLayout.setTitle(topStory.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.fui_transparent)); // transperent color = #00000000
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.rgb(0, 0, 0));

        //set values
        titleText.setText(topStory.getTitle());
        if (topStory.getUrl() != null) {
            urlText.setText(topStory.getUrl());
        } else {
            urlText.setText("");
        }
        usernameText.setText(topStory.getUserName());
        

    }


    public void setupViewPagerAndTabs() {

        ArrayList<String> listTitle = new ArrayList<>();
        listTitle.add(String.valueOf(topStory.getTotalCommentCount()) + "  COMMENTS");
        listTitle.add("ARTICLE");

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), this, listTitle);

        //create first fragment
        CommentFragment commentFragment = new CommentFragment();
        adapter.addFragment(commentFragment);

        //create Second frgament
        ArticleFragment articleFragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putCharSequence("URL", topStory.getType());
        articleFragment.setArguments(args);
        adapter.addFragment(articleFragment);

        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.setCurrentItem(0);
        pager.setOffscreenPageLimit(2);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab newTab = tabLayout.getTabAt(i);
            newTab.setCustomView(adapter.getTabView(i));
        }

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
}