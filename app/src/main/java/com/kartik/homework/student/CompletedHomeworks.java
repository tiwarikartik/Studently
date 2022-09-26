package com.kartik.homework.student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.kartik.homework.databinding.ActivityCompletedHomeworksBinding;
import com.kartik.homework.recyclerview.completed.Adapter;
import com.kartik.homework.recyclerview.completed.Completed;
import com.kartik.homework.recyclerview.completed.Interface;

import java.util.ArrayList;

public class CompletedHomeworks extends StudentDrawer implements Interface {

    ActivityCompletedHomeworksBinding binding;
    SharedPreferences sp;
    String className, uId;
    RecyclerView recyclerView;
    boolean uploaded;
    TextView txtNothing;
    ProgressBar progressBar;
    ArrayList<Completed> completedHomeworks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCompletedHomeworksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Completed Assignments");

        sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        uId = sp.getString(MainActivity.id, null);
        recyclerView = (RecyclerView) findViewById(R.id.missedRecyclerView);
        txtNothing = (TextView) findViewById(R.id.nothing);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        completeRecyclerView();
    }

    private void completeRecyclerView() {
        try {
            FirebaseFirestore.getInstance().collection("Homework")
                    .whereEqualTo("className", className)
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
                                                        String title = snap.getString("title");
                                                        String author = snap.getString("author");
                                                        long time = snap.getDate("timeStamp").getTime();

                                                        completedHomeworks.add(new Completed(title, author, id, time));
                                                        recyclerView = findViewById(R.id.completedRecyclerView);
                                                        Adapter adapter = new Adapter(CompletedHomeworks.this, CompletedHomeworks.this, completedHomeworks, R.layout.recycler_view_row_completed_homework);
                                                        recyclerView.setHasFixedSize(true);
                                                        recyclerView.setAdapter(adapter);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(CompletedHomeworks.this));
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
        } finally {
            if (completedHomeworks.size() == 0) {
                txtNothing.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCompletedClick(int position) {
        Intent intent = new Intent(CompletedHomeworks.this, CompletedView.class);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("HomeworkId", completedHomeworks.get(position).getId());
        editor.apply();

        intent.putExtra("TITLE", completedHomeworks.get(position).getTitle());
        intent.putExtra("TIME", completedHomeworks.get(position).getTime());
        intent.putExtra("ID", completedHomeworks.get(position).getId());
        startActivity(intent);
    }
}