package com.kartik.homework.student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;
import com.kartik.homework.databinding.ActivityCompletedViewBinding;

import java.util.HashMap;

public class CompletedView extends StudentDrawer {

    ActivityCompletedViewBinding binding;
    SharedPreferences sp;
    String className, uId, homeworkId, pdfPath, fileName;
    TextView title, content, time, pdfTitle;
    ImageButton delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompletedViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Homework View");

        sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        uId = sp.getString(MainActivity.id, null);

        homeworkId = getIntent().getStringExtra("ID");

        title = (TextView) findViewById(R.id.missedTextView);
        content = (TextView) findViewById(R.id.textView7);
        time = (TextView) findViewById(R.id.completedTextView);
        pdfTitle = (TextView) findViewById(R.id.pdfTitle);

        delete = (ImageButton) findViewById(R.id.delete);

        title.setText(getIntent().getStringExtra("TITLE"));
        time.setText(getIntent().getStringExtra("TIME"));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSubmission();
            }
        });

        getContent(homeworkId);
    }

    private void deleteSubmission() {
        String id = sp.getString("ID", null);
        if (!id.isEmpty()) {

            String ref = id + "/" + uId + "/" + fileName;
            System.out.println(ref);
            FirebaseStorage.getInstance().getReference(className)
                    .child(ref)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println("File Removed from Cloud Storage");
                        }
                    });

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Homework Uploaded", false);
            hashMap.put("Link", "");
            FirebaseFirestore.getInstance()
                    .collection("Homework")
                    .document(id)
                    .collection("Actions")
                    .document(uId)
                    .set(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println("Record of file deleted");
                        }
                    });
        }
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
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ID", id);
        editor.apply();
        FirebaseFirestore.getInstance()
                .collection("Homework")
                .document(id)
                .collection("Actions")
                .document(uId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snap = task.getResult();
                        pdfPath = snap.getString("link");
                        if (!pdfPath.isEmpty()) {

                            CardView uploadedCardView = (CardView) findViewById(R.id.uploadedCardView);
                            uploadedCardView.setVisibility(View.VISIBLE);
                            uploadedCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (pdfPath != null) {
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.parse(pdfPath), "application/pdf");
                                        startActivity(Intent.createChooser(intent, "Choose an Application:"));
                                    }
                                }
                            });
                            StorageReference pdfRef = FirebaseStorage.getInstance().getReferenceFromUrl(pdfPath);
                            fileName = pdfRef.getName();
                            pdfTitle.setText(" " + fileName);
                        }
                    }
                });
    }
}