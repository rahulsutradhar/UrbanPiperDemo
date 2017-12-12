package com.piper.urbandemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.piper.urbandemo.R;
import com.piper.urbandemo.model.TopStory;

import io.realm.RealmList;

/**
 * Created by developers on 12/12/17.
 */

public class TopStoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private RealmList<TopStory> topStories = new RealmList<>();

    public RealmList<TopStory> getTopStories() {
        return topStories;
    }

    public void setTopStories(RealmList<TopStory> topStories) {
        this.topStories = topStories;
    }

    /**
     * Constructor
     *
     * @param context
     */
    public TopStoryAdapter(Context context, RealmList<TopStory> topStories) {
        this.context = context;
        setTopStories(topStories);
    }

    /**
     * Update data
     *
     * @param topStories
     */
    public void setData(RealmList<TopStory> topStories) {
        topStories.clear();
        setTopStories(topStories);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_top_stories, parent, false);
        return new TopStoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TopStoryViewHolder topStoryViewHolder = (TopStoryViewHolder) holder;
        topStoryViewHolder.setViews(topStories.get(position), position);
    }

    @Override
    public int getItemCount() {
        return topStories.size();
    }

    private class TopStoryViewHolder extends RecyclerView.ViewHolder {

        private TextView title, score, url, username, timeStamp, commentCount;
        View itemView;

        /**
         * Constructor
         *
         * @param itemView
         */
        public TopStoryViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            score = (TextView) itemView.findViewById(R.id.score);
            url = (TextView) itemView.findViewById(R.id.url);
            username = (TextView) itemView.findViewById(R.id.username);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);
        }

        /**
         * Set Data to views
         *
         * @param topStory
         * @param position
         */
        public void setViews(final TopStory topStory, int position) {
            if (topStory != null) {
                try {
                    if (topStory.getTitle() != null) {
                        title.setText(topStory.getTitle());
                    }
                    score.setText(String.valueOf(topStory.getScore()));
                    if (url != null) {
                        url.setText(topStory.getUrl());
                    } else {
                        url.setVisibility(View.GONE);
                    }
                    if (topStory.getUserName() != null) {
                        username.setText(topStory.getUserName());
                    } else {
                        username.setVisibility(View.INVISIBLE);
                    }

                    commentCount.setText(String.valueOf(topStory.getTotalCommentCount()));

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            navigateToStoryDetailsActivity(topStory);
                        }
                    });

                } catch (NullPointerException npe) {

                }
            }
        }

        /**
         * Navigate to Details Activity
         */
        public void navigateToStoryDetailsActivity(TopStory topStory) {
            //TODO: pass thie object and fetch comments
        }
    }
}

