package com.piper.urbandemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.piper.urbandemo.R;
import com.piper.urbandemo.model.Comment;

import io.realm.RealmList;

/**
 * Created by developers on 14/12/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private RealmList<Comment> comments = new RealmList<>();

    public RealmList<Comment> getComments() {
        return comments;
    }

    public void setComments(RealmList<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Constructor
     *
     * @param context
     * @param comments
     */
    public CommentAdapter(Context context, RealmList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    /**
     * Update data
     *
     * @param comments
     */
    public void setData(RealmList<Comment> comments) {
        comments.clear();
        setComments(comments);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_comment, parent, false);

        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        commentViewHolder.setViews(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    private class CommentViewHolder extends RecyclerView.ViewHolder {

        private TextView timeStamp, username, description;

        public CommentViewHolder(View itemView) {
            super(itemView);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            username = (TextView) itemView.findViewById(R.id.username);
            description = (TextView) itemView.findViewById(R.id.description);
        }

        public void setViews(Comment comment) {

            try {
                username.setText(comment.getUserName());
                description.setText(Html.fromHtml(comment.getDescription()));

            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }
    }
}
