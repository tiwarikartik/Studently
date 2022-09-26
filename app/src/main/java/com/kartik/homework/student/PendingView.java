package com.kartik.homework.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kartik.homework.R;
import com.kartik.homework.databinding.ActivityPendingViewBinding;

public class PendingView extends StudentDrawer {

    ActivityPendingViewBinding activityHomeworkViewBinding;
    TextView title, content, time;
    ImageButton submitHomework;
    String homeworkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activityHomeworkViewBinding = ActivityPendingViewBinding.inflate(getLayoutInflater());
        setContentView(activityHomeworkViewBinding.getRoot());
        allocateActivityTitle("Homework Description");

        homeworkId = getIntent().getStringExtra("ID");

        title = (TextView) findViewById(R.id.missedTextView);
        content = (TextView) findViewById(R.id.textView7);
        time = (TextView) findViewById(R.id.completedTextView);

        title.setText(getIntent().getStringExtra("TITLE"));
        time.setText(getIntent().getStringExtra("TIME"));
        getContent(homeworkId);

        submitHomework = (ImageButton) findViewById(R.id.imageButton);
        submitHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PendingView.this, Submission.class);
                intent.putExtra("ID", homeworkId);
                startActivity(intent);
            }
        });
    }

    private void getContent(String id) {
        FirebaseFirestore.getInstance()
                .collection("Homework")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snap = task.getResult();
                        content.setText(snap.getString("content"));
                    }
                });
    }
}