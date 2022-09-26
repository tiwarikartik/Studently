package com.kartik.homework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kartik.homework.onboarding.IntroActivity;
import com.kartik.homework.student.StudentActivity;
import com.kartik.homework.teacher.TeacherActivity;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    String uId;
    public static final String classDiv = "class", preferenceName = "userDetails", id = "uId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        uId = preferences.getString(id, null);

        if (uId == null) {
            loadActivity();
        } else {
            databaseQuery();
        }
    }

    private void databaseQuery() {

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    String type = null;

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot doc = task.getResult();

                        SharedPreferences preferences = getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString(classDiv, doc.getString(classDiv));
                        editor.apply();

                        type = doc.getString("type");

                        if (type.equals("Teacher")) {
                            startActivity(new Intent(MainActivity.this, TeacherActivity.class));
                            finish();
                        } else if (type.equals("Student")) {
                            startActivity(new Intent(MainActivity.this, StudentActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadActivity() {
        startActivity(new Intent(MainActivity.this, IntroActivity.class));
        finish();
    }
}