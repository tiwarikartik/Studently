package com.kartik.homework.teacher;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;
import com.kartik.homework.databinding.ActivityProfileTBinding;

public class ProfileT extends TeacherDrawer {

    ActivityProfileTBinding activityProfileBinding;
    SharedPreferences sp;
    String uId, className;
    TextView name, standard, id, ugDegree, pgDegree, subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = ActivityProfileTBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());
        allocateActivityTitle("Profile");

        sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        uId = sp.getString(MainActivity.id, null);

        name = (TextView) findViewById(R.id.txtName);
        standard = (TextView) findViewById(R.id.textView8);
        id = (TextView) findViewById(R.id.txtTeacherId);
        ugDegree = (TextView) findViewById(R.id.txtUgDegree);
        pgDegree = (TextView) findViewById(R.id.txtPgDegree);
        subjects = (TextView) findViewById(R.id.txtSubjects);
        getDetails();
    }

    private void getDetails() {
        FirebaseFirestore.getInstance().collection("Standards").document(className)
                .collection("Teachers").document(uId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        name.setText(snapshot.getString("name"));
                        standard.setText(snapshot.getString("className"));
                        id.setText(snapshot.getLong("id").toString());
                        ugDegree.setText(snapshot.getString("ugDegree"));
                        pgDegree.setText(snapshot.getString("pgDegree"));
                        subjects.setText(snapshot.getString("subjects"));
                    }
                });
    }
}