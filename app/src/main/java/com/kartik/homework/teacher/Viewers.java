package com.kartik.homework.teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;
import com.kartik.homework.recyclerview.viewers.Adapter;
import com.kartik.homework.recyclerview.viewers.Interface;
import com.kartik.homework.recyclerview.viewers.Viewer;

import java.util.ArrayList;

public class Viewers extends AppCompatActivity implements Interface {

    SharedPreferences sp;
    String className, docId;
    RecyclerView recyclerView;
    String link = "";
    public final String LINK = "LINKS";
    ArrayList<Viewer> viewers = new ArrayList<>();
    ConstraintLayout scrollDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewers);

        sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        docId = sp.getString("HomeworkId", null);

        recyclerView = findViewById(R.id.viewerRecyclerView);
        getUserActions();

        scrollDown = (ConstraintLayout) findViewById(R.id.scrollDownViewers);
        scrollDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void getUserActions() {

        FirebaseFirestore.getInstance()
                .collection("Homework")
                .document(docId)
                .collection("Actions")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snap : task.getResult()) {
                                String id = snap.getId();

                                boolean isNotified = snap.getBoolean("notified");
                                boolean isFileUploaded = snap.getBoolean("uploaded");
                                if (isFileUploaded) {
                                    link = snap.getString("link");
                                }

                                FirebaseFirestore.getInstance().collection("Standards")
                                        .document(className)
                                        .collection("Students")
                                        .document(id).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                String name = documentSnapshot.getString("name");
                                                String emailId = documentSnapshot.getString("emailAddress");
                                                viewers.add(new Viewer(name, emailId, link, isNotified, isFileUploaded));

                                                Adapter adapter = new Adapter(Viewers.this, Viewers.this, viewers);
                                                recyclerView.setHasFixedSize(true);
                                                recyclerView.setAdapter(adapter);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(Viewers.this));
                                            }
                                        });
                            }
                        }
                    }
                });
    }


    @Override
    public void onAttachmentClick(int position) {

        SharedPreferences sf = getSharedPreferences(LINK, Context.MODE_PRIVATE);
        String url = sf.getString("HomeworkLink" + position, null);

        if (url != null) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "application/pdf");
            startActivity(Intent.createChooser(intent, "Choose an Application:"));
        }
    }
}