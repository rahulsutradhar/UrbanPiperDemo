package com.piper.urbandemo.helper;

import android.util.Log;
import android.widget.Toast;

import com.piper.urbandemo.UrbanApplication;
import com.piper.urbandemo.model.Comment;
import com.piper.urbandemo.model.TopStory;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by developers on 14/12/17.
 */

public class DatabaseHelper {

    /**
     * Add All Top Stories
     */
    public static void addTopStories(final List<TopStory> topStories) {

        Realm realm = UrbanApplication.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(topStories);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        //TODO

                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        //TODO
                    }
                });
    }

    /**
     * Get All Top stories
     */
    public static ArrayList<TopStory> getTopStories() {
        ArrayList<TopStory> topStories = new ArrayList<>();

        RealmResults<TopStory> realmResults = UrbanApplication.getRealm().where(TopStory.class).equalTo("type", "story").greaterThan("id", 0).findAll();
        if (realmResults.isLoaded()) {
            if (realmResults.size() > 0) {
                topStories.addAll(realmResults.subList(0, realmResults.size()));
            }
        }
        return topStories;
    }

    /**
     * Get a Top Story for the ID
     */
    public static TopStory getTopStory(long id) {
        TopStory topStory = null;
        try {
            Realm realm = UrbanApplication.getRealm();
            realm.beginTransaction();
            topStory = realm.where(TopStory.class).equalTo("id", id).equalTo("type", "story").findFirst();
            realm.commitTransaction();

        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return topStory;
    }

    /**
     * Get the Length of Top Storeis saved
     */
    public static int getSizeTopStories() {
        int size = 0;
        try {
            size = UrbanApplication.getRealm().where(TopStory.class).equalTo("type", "story").greaterThan("id", 0).findAll().size();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * Get ALL Comment for the ids
     */
    public static RealmList<Comment> getCommentForIds(RealmList<Long> commentIds, int maxItem) {
        RealmList<Comment> comments = new RealmList<>();

        RealmQuery q = UrbanApplication.getRealm().where(Comment.class);
        for (int i = 0; i < maxItem; i++) {
            long id = commentIds.get(i);
            q = q.equalTo("id", id);
        }

        RealmResults<Comment> realmResults = q.findAll();
        if (realmResults.isLoaded()) {
            if (realmResults.size() > 0) {
                comments.addAll(realmResults);
            }
        }
        return comments;
    }


    /**
     * Clear All Cache Data
     */
    public static void clearTopStories() {
        Realm realm = UrbanApplication.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //delete all top stories
                        RealmResults<TopStory> realmResults = realm.where(TopStory.class).equalTo("type", "story").findAll();
                        if (realmResults.size() > 0) {
                            realmResults.deleteAllFromRealm();
                        }

                        //delete all comments
                        RealmResults<Comment> realmResults1 = realm.where(Comment.class).equalTo("type", "comment").findAll();
                        if (realmResults1.size() > 0) {
                            realmResults1.deleteAllFromRealm();
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        //TODO
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        //TODO
                    }
                });
    }
}
