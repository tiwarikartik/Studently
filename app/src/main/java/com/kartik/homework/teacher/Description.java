package com.kartik.homework.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.MotionEventCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kartik.homework.R;

public class Description extends AppCompatActivity {

    TextView title, content, time;
    ImageButton viewers;
    CardView layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        title = (TextView) findViewById(R.id.missedTextView);
        content = (TextView) findViewById(R.id.textView7);
        time = (TextView) findViewById(R.id.completedTextView);

        title.setText(getIntent().getStringExtra("TITLE"));
        time.setText(getIntent().getStringExtra("TIME"));
        getContent(getIntent().getStringExtra("ID"));

        viewers = (ImageButton) findViewById(R.id.imageButton);
        viewers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Description.this, Viewers.class));
            }
        });

        layout = (CardView) findViewById(R.id.scrollUpViewer);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = MotionEventCompat.getActionMasked(motionEvent);

                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        return true;
                    case (MotionEvent.ACTION_UP):
                        startActivity(new Intent(Description.this, Viewers.class));
                        return true;
                    default:
                        return false;
                }
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