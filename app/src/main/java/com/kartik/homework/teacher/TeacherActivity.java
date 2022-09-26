package com.kartik.homework.teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;
import com.kartik.homework.databinding.ActivityTeacherBinding;
import com.kartik.homework.recyclerview.teacher.Adapter;
import com.kartik.homework.recyclerview.teacher.Homework;
import com.kartik.homework.recyclerview.teacher.Interface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TeacherActivity extends TeacherDrawer implements Interface {

    ArrayList<Homework> homeworks = new ArrayList<>();
    RecyclerView recyclerView;
    SharedPreferences sp;
    String className, uId;
    ActivityTeacherBinding activityTeacherBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTeacherBinding = ActivityTeacherBinding.inflate(getLayoutInflater());
        setContentView(activityTeacherBinding.getRoot());
        allocateActivityTitle("Homeworks Assigned");

        sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        uId = sp.getString(MainActivity.id, null);

        getHomeworkInfo();
    }

    private void getHomeworkInfo() {
        System.out.println("className : " + className + " uId : " + uId);

        FirebaseFirestore.getInstance()
                .collection("Homework")
                .whereEqualTo("className", className)
                .whereEqualTo("authorId", uId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snap : task.getResult()) {
                                String id = snap.getId();
                                long timeInMillis = (snap.getDate("timeStamp").getTime());
                                java.util.Date date = new java.util.Date(timeInMillis);
                                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                                String result = formatter.format(date);

                                String title = snap.getString("title");
                                String author = snap.getString("author");
                                homeworks.add(new Homework(title, author, result, id));
                            }
                        } else {
                            System.out.println("Task Exception: \n" + task.getException());
                        }

                        recyclerView = findViewById(R.id.mRecyclerView);
                        Adapter adapter = new Adapter(TeacherActivity.this, TeacherActivity.this, homeworks);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(TeacherActivity.this));
                    }
                });
    }

    @Override
    public void onPreviewClick(int position) {
        Intent intent = new Intent(TeacherActivity.this, Description.class);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("HomeworkId", homeworks.get(position).getId());
        editor.apply();

        intent.putExtra("TITLE", homeworks.get(position).getTitle());
        intent.putExtra("TIME", homeworks.get(position).getTime());
        intent.putExtra("ID", homeworks.get(position).getId());
        startActivity(intent);
    }
}