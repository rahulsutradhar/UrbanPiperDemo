package com.piper.urbandemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.piper.urbandemo.R;
import com.piper.urbandemo.UrbanApplication;
import com.piper.urbandemo.adapter.CommentAdapter;
import com.piper.urbandemo.helper.CoreGsonUtils;
import com.piper.urbandemo.model.Comment;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developers on 13/12/17.
 */

public class CommentFragment extends Fragment {

    private RealmList<Long> commentIds = new RealmList<>();
    private RealmList<Comment> comments = new RealmList<>();
    private FrameLayout mainContent, noItemFound, networkLayout, progressBar;
    private View rootView;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private int MAX_ITEM_FETCH = 15;
    private int index = 0;
    private boolean commentExist = false;
    private boolean trackNetwork = false;

    /**
     * Constructor
     */
    public CommentFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comments, container, false);

        commentExist = getArguments().getBoolean("COMMENT_EXIST");
        if (commentExist) {
            String commentidsStr = getArguments().getString("COMMENT_IDS");
            commentIds = CoreGsonUtils.fromJsontoRealmList(commentidsStr, Long.class);
        }

        setViews();
        return rootView;
    }

    /**
     * Initilize views
     */
    public void setViews() {
        mainContent = (FrameLayout) rootView.findViewById(R.id.main_content);
        noItemFound = (FrameLayout) rootView.findViewById(R.id.noitemfound);
        networkLayout = (FrameLayout) rootView.findViewById(R.id.network_problem);
        progressBar = (FrameLayout) rootView.findViewById(R.id.progressLayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_comment);

        networkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCommentData();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (commentExist) {
            if (commentIds.size() < MAX_ITEM_FETCH) {
                MAX_ITEM_FETCH = commentIds.size();
            }
            mainContent.setVisibility(View.GONE);
            noItemFound.setVisibility(View.GONE);
            networkLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            //request data from server
            requestCommentData();
        } else {
            mainContent.setVisibility(View.GONE);
            noItemFound.setVisibility(View.VISIBLE);
            networkLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Fetch data
     */
    public void requestCommentData() {
        if (index >= MAX_ITEM_FETCH) {
            displayComment();
        } else {

            String id = String.valueOf(commentIds.get(index)) + ".json";
            fetchIndividualComment(id);
            index++;
        }
    }

    /**
     * Fetch Individual Comment
     *
     * @param commentId
     */
    public void fetchIndividualComment(String commentId) {

        UrbanApplication.getAPIService()
                .fetchComment(commentId, "pretty")
                .enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Comment comment = response.body();
                                comments.add(comment);
                                trackNetwork = false;

                                //fetch next top stories
                                requestCommentData();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {
                        trackNetwork = true;
                        requestCommentData();
                    }
                });

    }

    /**
     * Display List
     */
    public void displayComment() {

        if (commentIds.size() > 0) {
            mainContent.setVisibility(View.VISIBLE);
            noItemFound.setVisibility(View.GONE);
            networkLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            Toast.makeText(getActivity(), "Fetched " + comments.size() + " comments", Toast.LENGTH_SHORT).show();

            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new CommentAdapter(getActivity(), comments);
            recyclerView.setAdapter(adapter);
        } else {
            if (trackNetwork) {
                noItemFound.setVisibility(View.GONE);
                networkLayout.setVisibility(View.VISIBLE);
            } else {
                noItemFound.setVisibility(View.VISIBLE);
                networkLayout.setVisibility(View.GONE);
            }
            mainContent.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
