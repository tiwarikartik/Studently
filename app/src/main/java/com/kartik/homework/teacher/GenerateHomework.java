package com.kartik.homework.teacher;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kartik.homework.utils.DatePicker;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;
import com.kartik.homework.databinding.ActivityGenerateHomeworkBinding;
import com.kartik.homework.utils.Actions;
import com.kartik.homework.utils.Homework;

import java.util.Date;
import java.util.List;

public class GenerateHomework extends TeacherDrawer implements DatePickerDialog.OnDateSetListener {

    String className, authorStr, uId, title, content;
    TextInputLayout titleComp, contentComp, dateComp, monthComp, yearComp;
    Button publishComp, pickDate;
    String date, month, year;
    ActivityGenerateHomeworkBinding activityGenerateHomeworkBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGenerateHomeworkBinding = ActivityGenerateHomeworkBinding.inflate(getLayoutInflater());
        setContentView(activityGenerateHomeworkBinding.getRoot());
        allocateActivityTitle("Assign new Homework");

        titleComp = (TextInputLayout) findViewById(R.id.title);
        contentComp = (TextInputLayout) findViewById(R.id.content);
        dateComp = (TextInputLayout) findViewById(R.id.date);
        monthComp = (TextInputLayout) findViewById(R.id.month);
        yearComp = (TextInputLayout) findViewById(R.id.year);

        publishComp = (Button) findViewById(R.id.publish);
        pickDate = (Button) findViewById(R.id.pickDate);

        setPreferences();

        publishComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = dateComp.getEditText().getText().toString();
                month = monthComp.getEditText().getText().toString();
                year = yearComp.getEditText().getText().toString();
                title = titleComp.getEditText().getText().toString().trim();
                content = contentComp.getEditText().getText().toString().trim();

                if (validate()) {
                    getList();
                }
            }
        });

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker dialogFragment;
                dialogFragment = new DatePicker();
                dialogFragment.show(getSupportFragmentManager(), "PICK A DATE");
            }
        });
    }

    private boolean validate() {
        boolean isValid = true;

        if(title.isEmpty()) {
            titleComp.setError("This field is compulsory");
            isValid = false;
        } else if (title.length() > 16) {
            titleComp.setError("Title length cannot be greater than 16");
            isValid = false;
        } else if (title.length() < 4) {
            titleComp.setError("Title length cannot be less than 4");
            isValid = false;
        } else {
            titleComp.setErrorEnabled(false);
        }

        if(content.isEmpty()) {
            contentComp.setError("This field is compulsory");
            isValid = false;
        } else if (content.length() > 1000) {
            contentComp.setError("Title length cannot be greater than 1000");
            isValid = false;
        } else if (content.length() < 4) {
            contentComp.setError("Title length cannot be less than 4");
            isValid = false;
        } else {
            contentComp.setErrorEnabled(false);
        }

        TextView error = (TextView) findViewById(R.id.dateError);
        if(date.isEmpty() || month.isEmpty() || year.isEmpty()) {
            error.setText("This field is compulsory");
            isValid = false;
        } else if (Integer.parseInt(date) > 31 ) {
            error.setText("Day cannot be greater than 31");
            isValid = false;
        } else if (Integer.parseInt(month) > 12) {
            error.setText("Month cannot be greater than 12");
            isValid = false;
        } else {
            error.setText("");
        }

        return isValid;
    }

    private void setPreferences() {
        SharedPreferences preferences = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = preferences.getString(MainActivity.classDiv, null);
        uId = preferences.getString(MainActivity.id, null);
        System.out.println(className + " " + uId);
        setAuthor();
    }

    private void setAuthor() {

        FirebaseFirestore.getInstance()
                .collection("Standards")
                .document(className)
                .collection("Teacher")
                .document(uId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        TextInputLayout author = (TextInputLayout) findViewById(R.id.author);
                        authorStr = task.getResult().getString("name");
                        author.getEditText().setText(authorStr);
                    }
                });
    }

    private void getList() {

        long time = System.currentTimeMillis();

        FirebaseFirestore.getInstance()
                .collection("Standards")
                .document(className).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    Homework homework;
                    List<String> studentList;

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                studentList = (List<String>) document.get("StudentList");
                            }

                            homework = new Homework(title, content, authorStr, className, uId, new Date(time), new Date(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date)), studentList);

                            FirebaseFirestore.getInstance()
                                    .collection("Homework")
                                    .document(uId + "_" + time)
                                    .set(homework).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Log.d("Homework: ", "Inserted");
                                            createActions(studentList, time);
                                            Toast.makeText(GenerateHomework.this, "Homework Published : " + homework, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }
                });
    }

    public void createActions(List<String> studentList, long time) {

        Actions actions = new Actions(false, false, "");

        for (int i = 0; i < studentList.size(); i++) {
            FirebaseFirestore.getInstance()
                    .collection("Homework")
                    .document(uId + "_" + time)
                    .collection("Actions")
                    .document(studentList.get(i))
                    .set(actions).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(GenerateHomework.this, "Got this as output", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        startActivity(new Intent(GenerateHomework.this, TeacherActivity.class));

    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        dateComp.getEditText().setText(dayOfMonth + "");
        monthComp.getEditText().setText(month + 1 + "");
        yearComp.getEditText().setText(year + "");
    }
}