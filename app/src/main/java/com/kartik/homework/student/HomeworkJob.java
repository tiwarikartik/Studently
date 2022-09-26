package com.kartik.homework.student;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;

public class HomeworkJob extends JobService {

    private long NOTIFY_ID;
    private final String TAG = "NotificationJobService";
    private String CHANNEL_ID, CHANNEL_NAME, TITLE, CONTENT, className, uId;
    private boolean jobCancelled = false;
    SharedPreferences sharedPreferences;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        doBackgroundWork(params);
        return false;
    }

    private void doBackgroundWork(final JobParameters params) {

        sharedPreferences = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sharedPreferences.getString(MainActivity.classDiv, null);
        uId = sharedPreferences.getString(MainActivity.id, null);
        Log.d(TAG, "User ID: " + uId + " Class: " + className);

        getNotifications();
        jobFinished(params, false);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job Cancelled before finishing the task");
        jobCancelled = true;
        return true;
    }

    private void getNotifications() {
        FirebaseFirestore.getInstance()
                .collection("Homework")
                .whereEqualTo("className", className)
                .whereArrayContains("notSeenBy", uId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot snap : task.getResult()) {

                            String channelId = snap.getString("authorId");
                            long notificationId = snap.getDate("timeStamp").getTime();

                            Log.d(TAG, channelId + " - " + notificationId);
                            getData(channelId, notificationId);
                        }
                    }
                });
    }

    private void getData(String channelId, long notificationId) {

        FirebaseFirestore.getInstance()
                .collection("Homework")
                .document(channelId + "_" + notificationId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        CHANNEL_ID = channelId;
                        CHANNEL_NAME = "Homework Notifications from " + snapshot.get("author");
                        TITLE = snapshot.getString("title");
                        CONTENT = snapshot.getString("content");
                        NOTIFY_ID = notificationId;
                        Log.d(TAG, CHANNEL_ID + ", " + CHANNEL_NAME + ", " + NOTIFY_ID + ", " + TITLE + ", " + CONTENT);

                        generateNotification();
                    }
                });
    }

    private void generateNotification() {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel();
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_home_work_24)
                    .setContentTitle(TITLE)
                    .setContentText(CONTENT)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true);
            Log.d(TAG, "Notification Builder Created");

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            Log.d(TAG, "Notification Manager Created");

            notificationManager.notify((int) NOTIFY_ID, builder.build());
            Log.d(TAG, "Notification Pushed: " + NOTIFY_ID);

            notificationReceived();
        } catch (Exception e) {
            if (jobCancelled) {
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void notificationReceived() {

        DocumentReference actions = FirebaseFirestore.getInstance()
                .collection("Homework")
                .document(CHANNEL_ID + "_" + NOTIFY_ID)
                .collection("Actions")
                .document(uId);
        System.out.println(uId);

        actions.update("notified", true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Notified", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseFirestore.getInstance()
                .collection("Homework")
                .whereEqualTo("authorId", CHANNEL_ID)
                .whereArrayContains("notSeenBy", uId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snap : task.getResult()) {
                            snap.getReference().update("notSeenBy", FieldValue.arrayRemove(uId));
                        }
                    }
                });
    }
}