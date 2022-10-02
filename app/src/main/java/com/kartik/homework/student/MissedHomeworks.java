package com.kartik.homework.student;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import com.kartik.homework.databinding.ActivityMissedHomeworksBinding;
import com.kartik.homework.recyclerview.missed.Adapter;
import com.kartik.homework.recyclerview.missed.Missed;

import java.util.ArrayList;
import java.util.Date;

public class MissedHomeworks extends StudentDrawer {

    ActivityMissedHomeworksBinding binding;
    SharedPreferences sp;
    String className, uId;
    RecyclerView recyclerView;
    boolean uploaded;
    TextView txtNothing;
    ArrayList<Missed> missedHomeworks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMissedHomeworksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Missed Assignments");

        sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        uId = sp.getString(MainActivity.id, null);
        recyclerView = (RecyclerView) findViewById(R.id.missedRecyclerView);
        txtNothing = (TextView) findViewById(R.id.nothing);

        missedRecyclerView();
    }

    private void missedRecyclerView() {
        try {
            FirebaseFirestore.getInstance().collection("Homework")
                    .whereEqualTo("className", className)
                    .whereLessThan("dueDate", new Date())
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
                                                        String title = snap.getString("title");
                                                        String author = snap.getString("author");
                                                        long time = snap.getDate("timeStamp").getTime();

                                                        missedHomeworks.add(new Missed(title, author, id, time));
                                                        recyclerView = findViewById(R.id.missedRecyclerView);
                                                        Adapter adapter = new Adapter(MissedHomeworks.this, missedHomeworks, R.layout.recycler_view_row_missed_homework);
                                                        recyclerView.setHasFixedSize(true);
                                                        recyclerView.setAdapter(adapter);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(MissedHomeworks.this));
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
        } finally {
            if (missedHomeworks.size() == 0) {
                txtNothing.setVisibility(View.VISIBLE);
            }
        }

    }
}