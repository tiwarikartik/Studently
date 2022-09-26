package com.kartik.homework.student;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;
import com.kartik.homework.databinding.ActivityStudentBinding;
import com.kartik.homework.recyclerview.completed.Completed;
import com.kartik.homework.recyclerview.missed.Adapter;
import com.kartik.homework.recyclerview.missed.Missed;
import com.kartik.homework.recyclerview.pending.Interface;
import com.kartik.homework.recyclerview.pending.Pending;

import java.util.ArrayList;
import java.util.Date;

public class StudentActivity extends StudentDrawer implements Interface, com.kartik.homework.recyclerview.completed.Interface {

    ActivityStudentBinding activityStudentBinding;
    SharedPreferences sp;
    String className, uId;
    boolean uploaded;
    ArrayList<Missed> missedHomeworks = new ArrayList<>();
    ArrayList<Pending> pendingHomeworks = new ArrayList<>();
    ArrayList<Completed> completedHomeworks = new ArrayList<>();
    Button moreCompleted, morePending, moreMissed;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    public final String TAG = "Homework Job";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStudentBinding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(activityStudentBinding.getRoot());
        allocateActivityTitle("Student Activity");

        sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        uId = sp.getString(MainActivity.id, null);

        moreCompleted = (Button) findViewById(R.id.completedMoreButton);
        morePending = (Button) findViewById(R.id.pendingMoreButton);
        moreMissed = (Button) findViewById(R.id.missedMoreButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        morePending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentActivity.this, PendingHomeworks.class));
            }
        });

        moreCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentActivity.this, CompletedHomeworks.class));
            }
        });

        moreMissed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentActivity.this, MissedHomeworks.class));
            }
        });

        missedHomeworks();
        pendingHomeworks();
        completedHomeworks();
        scheduleJob();
    }

    private void missedHomeworks() {
        FirebaseFirestore.getInstance().collection("Homework")
                .whereEqualTo("className", className)
                .whereLessThan("dueDate", new Date())
                .limit(3)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snap : task.getResult()) {
                                String id = snap.getId();

                                FirebaseFirestore.getInstance()
                                        .collection("Homework")
                                        .document(id)
                                        .collection("Actions")
                                        .document(uId)
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                uploaded = documentSnapshot.getBoolean("uploaded");

                                                if (uploaded == false) {
                                                    findViewById(R.id.missedTextView).setVisibility(View.VISIBLE);
                                                    findViewById(R.id.missedMoreButton).setVisibility(View.VISIBLE);
                                                    String title = snap.getString("title");
                                                    String author = snap.getString("author");
                                                    long time = snap.getDate("timeStamp").getTime();

                                                    missedHomeworks.add(new Missed(title, author, id, time));
                                                    recyclerView = findViewById(R.id.missedRecyclerView);
                                                    Adapter adapter = new Adapter(StudentActivity.this, missedHomeworks, R.layout.recycler_view_column_missed_homework);
                                                    recyclerView.setHasFixedSize(true);
                                                    recyclerView.setAdapter(adapter);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(StudentActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                                }
                                            }
                                        });
                            }
                        } else {
                            System.out.println("Task Exception: " + task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }

    private void pendingHomeworks() {
        FirebaseFirestore.getInstance().collection("Homework")
                .whereEqualTo("className", className)
                .whereGreaterThan("dueDate", new Date())
                .limit(3)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snap : task.getResult()) {
                                String id = snap.getId();

                                FirebaseFirestore.getInstance()
                                        .collection("Homework")
                                        .document(id)
                                        .collection("Actions")
                                        .document(uId)
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                uploaded = documentSnapshot.getBoolean("uploaded");

                                                if (uploaded == false) {
                                                    findViewById(R.id.pendingTextView).setVisibility(View.VISIBLE);
                                                    findViewById(R.id.pendingMoreButton).setVisibility(View.VISIBLE);
                                                    String title = snap.getString("title");
                                                    String author = snap.getString("author");
                                                    long time = snap.getDate("timeStamp").getTime();

                                                    pendingHomeworks.add(new Pending(title, author, id, time));
                                                    recyclerView = findViewById(R.id.pendingRecyclerView);
                                                    com.kartik.homework.recyclerview.pending.Adapter adapter = new com.kartik.homework.recyclerview.pending.Adapter(StudentActivity.this, StudentActivity.this, pendingHomeworks, R.layout.recycler_view_column_pending_homework);
                                                    recyclerView.setHasFixedSize(true);
                                                    recyclerView.setAdapter(adapter);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(StudentActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                                }
                                            }
                                        });
                            }
                        } else {
                            System.out.println("Task Exception: " + task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }

    private void completedHomeworks() {
        FirebaseFirestore.getInstance().collection("Homework")
                .whereEqualTo("className", className)
                .limit(3)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snap : task.getResult()) {
                                String id = snap.getId();

                                FirebaseFirestore.getInstance()
                                        .collection("Homework")
                                        .document(id)
                                        .collection("Actions")
                                        .document(uId)
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                uploaded = documentSnapshot.getBoolean("uploaded");

                                                if (uploaded) {
                                                    findViewById(R.id.completedTextView).setVisibility(View.VISIBLE);
                                                    findViewById(R.id.completedMoreButton).setVisibility(View.VISIBLE);
                                                    String title = snap.getString("title");
                                                    String author = snap.getString("author");
                                                    long time = snap.getDate("timeStamp").getTime();

                                                    completedHomeworks.add(new Completed(title, author, id, time));
                                                    recyclerView = findViewById(R.id.completedRecyclerView);
                                                    com.kartik.homework.recyclerview.completed.Adapter adapter = new com.kartik.homework.recyclerview.completed.Adapter(StudentActivity.this, StudentActivity.this, completedHomeworks, R.layout.recycler_view_column_pending_homework);
                                                    recyclerView.setHasFixedSize(true);
                                                    recyclerView.setAdapter(adapter);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(StudentActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                                }
                                            }
                                        });

                            }
                            progressBar.setVisibility(View.GONE);
                        } else {
                            System.out.println("Task Exception: " + task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }

    public void scheduleJob() {
        ComponentName componentName = new ComponentName(StudentActivity.this, HomeworkJob.class);
        JobInfo info = new JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resCode = scheduler.schedule(info);

        if (resCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job Scheduled");
        } else {
            Log.d(TAG, "Job Scheduling Failed");
        }
    }

    @Override
    public void onPreviewClick(int position) {
        Intent intent = new Intent(StudentActivity.this, PendingView.class);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("HomeworkId", pendingHomeworks.get(position).getId());
        editor.apply();

        intent.putExtra("TITLE", pendingHomeworks.get(position).getTitle());
        intent.putExtra("TIME", pendingHomeworks.get(position).getTime());
        intent.putExtra("ID", pendingHomeworks.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onCompletedClick(int position) {
        Intent intent = new Intent(StudentActivity.this, CompletedView.class);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("HomeworkId", completedHomeworks.get(position).getId());
        editor.apply();

        intent.putExtra("TITLE", completedHomeworks.get(position).getTitle());
        intent.putExtra("TIME", completedHomeworks.get(position).getTime());
        intent.putExtra("ID", completedHomeworks.get(position).getId());
        startActivity(intent);
    }
}