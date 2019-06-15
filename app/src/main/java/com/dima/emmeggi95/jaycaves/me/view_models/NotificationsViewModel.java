package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.dima.emmeggi95.jaycaves.me.entities.NotificationLike;
import com.dima.emmeggi95.jaycaves.me.entities.User;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private static MutableLiveData<List<NotificationLike>> notifications = new MutableLiveData<>();
    private static List<NotificationLike> notificationList = new ArrayList<>();
    public static List<Album> albumList = new ArrayList<>();
    private FirebaseDatabase database;

    public NotificationsViewModel() {
        database = FirebaseDatabase.getInstance();
        database.getReference("notifications").child(User.uid).orderByChild("timestamp").limitToLast(200).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                notificationList.clear();
                for(DataSnapshot d: data){ // fetch notifications
                    NotificationLike newNote = d.getValue(NotificationLike.class);
                    for(NotificationLike n: notificationList){
                        if (n.getReviewId().equals(newNote.getReviewId())){
                            newNote.setLiker(newNote.getLiker()+", "+n.getLiker());
                            notificationList.remove(n);
                        }
                    }
                    notificationList.add(newNote);
                }



                for (NotificationLike n1: notificationList) {

                    String mostRecentLikers = n1.getLiker();
                    char c = ',';
                    int likes = countOccurrences(n1.getLiker(), c,0);
                    if (likes > 3) {
                        String[] likers = n1.getLiker().split(",");
                        mostRecentLikers = likers[0]+", "+likers[1]+" and "+ Integer.toString(likes-2) + " others";
                    }

                    String albumData = n1.getReviewAlbum();
                    String[] parts = albumData.split("@");
                    String part1 = parts[0];
                    String part2 = parts[1];
                    String message = mostRecentLikers + " liked your review of '" + part1 + "', by '" + part2 + "'";
                    n1.setMessage(message);


                }

                Collections.sort(notificationList, NotificationLike.dateComparator); // sort by timestamp
                notifications.postValue(notificationList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public LiveData<List<NotificationLike>> getData(){
        return notifications;
    }

    private static int countOccurrences(
            String someString, char searchedChar, int index) {
        if (index >= someString.length()) {
            return 0;
        }

        int count = someString.charAt(index) == searchedChar ? 1 : 0;
        return count + countOccurrences(
                someString, searchedChar, index + 1);
    }
}
