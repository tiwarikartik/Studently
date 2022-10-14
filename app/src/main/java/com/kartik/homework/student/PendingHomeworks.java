package com.kartik.homework.student;

import android.content.Context;
import android.content.Intent;
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
import com.kartik.homework.databinding.ActivityPendingHomeworksBinding;
import com.kartik.homework.recyclerview.pending.Adapter;
import com.kartik.homework.recyclerview.pending.Interface;
import com.kartik.homework.recyclerview.pending.Pending;

import java.util.ArrayList;
import java.util.Date;

public class PendingHomeworks extends StudentDrawer implements Interface {

    ActivityPendingHomeworksBinding binding;
    SharedPreferences sp;
    String className, uId;
    RecyclerView recyclerView;
    boolean uploaded;
    TextView txtNothing;
    ArrayList<Pending> pendingHomeworks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPendingHomeworksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Pending Assignments");

        sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        uId = sp.getString(MainActivity.id, null);
        recyclerView = (RecyclerView) findViewById(R.id.pendingRecyclerView);
        txtNothing = (TextView) findViewById(R.id.nothing);

        pendingRecyclerView();
    }

    private void pendingRecyclerView() {
        try {
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
                                                    try {
                                                        uploaded = documentSnapshot.getBoolean("uploaded");

                                                        if (uploaded == false) {
                                                            String title = snap.getString("title");
                                                            String author = snap.getString("author");
                                                            long time = snap.getDate("timeStamp").getTime();

                                                            pendingHomeworks.add(new Pending(title, author, id, time));
                                                            recyclerView = findViewById(R.id.pendingRecyclerView);
                                                            Adapter adapter = new Adapter(PendingHomeworks.this, PendingHomeworks.this, pendingHomeworks, R.layout.recycler_view_row_pending_homework);
                                                            recyclerView.setHasFixedSize(true);
                                                            recyclerView.setAdapter(adapter);
                                                            recyclerView.setLayoutManager(new LinearLayoutManager(PendingHomeworks.this));
                                                        }
                                                    } catch (NullPointerException e) {
                                                        System.out.println("Met an Error");
                                                    }
                                                }
                                            });
                                }
                            } else {
                                System.out.println("Task Exception: " + task.getException());
                            }
                        }
                    })
                            .

                    addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println(e);
                        }
                    });
        } finally {
            if (pendingHomeworks.size() == 0) {
                txtNothing.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPreviewClick(int position) {
        Intent intent = new Intent(PendingHomeworks.this, PendingView.class);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("HomeworkId", pendingHomeworks.get(position).getId());
        editor.apply();

        intent.putExtra("TITLE", pendingHomeworks.get(position).getTitle());
        intent.putExtra("TIME", pendingHomeworks.get(position).getTime());
        intent.putExtra("ID", pendingHomeworks.get(position).getId());
        startActivity(intent);
    }
}