package com.kartik.homework.teacher;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;
import com.kartik.homework.databinding.ActivityAddStudentBinding;
import com.kartik.homework.utils.Student;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddStudent extends TeacherDrawer {

    FirebaseAuth auth;
    ActivityAddStudentBinding activityAddStudentBinding;
    ProgressBar progressBar;
    TextInputLayout txtName, txtRollNumber, txtEmailAddress, txtPassword, txtPhoneNumber, txtGender, txtBloodGroup;
    SharedPreferences sp;
    String className, uId, newUid, gender = "";
    Button submit;
    String[] genders = {"Male", "Female", "Others"};
    String[] bloodGroups = {"A Positive (+ve)", "A Negative (-ve)", "B Positive (+ve)", "B Negative (-ve)", "AB Positive (+ve)", "AB Negative (-ve)", "O Positive (+ve)", "O Negative (-ve)"};
    Student student;
    AutoCompleteTextView autoCompleteGender, autoCompleteBloodGroup;
    ArrayAdapter<String> genderItems, bloodGroupItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddStudentBinding = ActivityAddStudentBinding.inflate(getLayoutInflater());
        setContentView(activityAddStudentBinding.getRoot());
        allocateActivityTitle("Add New Student");

        sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        uId = sp.getString(MainActivity.id, null);

        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar2);

        txtName = (TextInputLayout) findViewById(R.id.name);
        txtRollNumber = (TextInputLayout) findViewById(R.id.rollno);
        txtEmailAddress = (TextInputLayout) findViewById(R.id.email);
        txtPassword = (TextInputLayout) findViewById(R.id.password);
        txtPhoneNumber = (TextInputLayout) findViewById(R.id.phone);
        txtGender = (TextInputLayout) findViewById(R.id.genderDropDown);
        txtBloodGroup = (TextInputLayout) findViewById(R.id.BloodGroup);
        submit = (Button) findViewById(R.id.addStudentSubmit);

        autoCompleteGender = (AutoCompleteTextView) findViewById(R.id.auto_complete_gender);
        genderItems = new ArrayAdapter<String>(this, R.layout.list_items, genders);
        autoCompleteGender.setAdapter(genderItems);
        autoCompleteGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }
        });

        autoCompleteBloodGroup = (AutoCompleteTextView) findViewById(R.id.auto_complete_blood_group);
        bloodGroupItems = new ArrayAdapter<String>(this, R.layout.list_items, bloodGroups);
        autoCompleteBloodGroup.setAdapter(bloodGroupItems);
        autoCompleteBloodGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void validate() {
        boolean isValid = true;

        String name = txtName.getEditText().getText().toString().trim();
        String rollNumber = txtRollNumber.getEditText().getText().toString();
        String emailAddress = txtEmailAddress.getEditText().getText().toString().trim();
        String password = txtPassword.getEditText().getText().toString().trim();
        String phoneNumber = txtPhoneNumber.getEditText().getText().toString().trim();
        String bloodGroup = txtBloodGroup.getEditText().getText().toString().trim();
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Matcher emailMatcher = Pattern.compile(regex).matcher(emailAddress);

        if (name.isEmpty()) {
            txtName.setError("This field cannot be empty");
            isValid = false;
        } else {
            txtName.setErrorEnabled(false);
        }

        if (rollNumber.isEmpty()) {
            txtRollNumber.setError("This field cannot be empty");
            isValid = false;
        } else {
            txtRollNumber.setErrorEnabled(false);
        }

        if (emailAddress.isEmpty()) {
            txtEmailAddress.setError("This field cannot be empty");
            isValid = false;
        } else if (!emailMatcher.matches()) {
            txtEmailAddress.setError("Wrong Email ID");
            isValid = false;
        } else {
            txtEmailAddress.setErrorEnabled(false);
        }

        if (password.isEmpty()) {
            txtPassword.setError("This field cannot be empty");
            isValid = false;
        } else if (password.length() < 4) {
            txtPassword.setError("Password length cannot be less than 4");
            isValid = false;
        } else if (password.length() > 8) {
            txtPassword.setError("Password length cannot be greater than 8");
            isValid = false;
        } else {
            txtPassword.setErrorEnabled(false);
        }

        if (phoneNumber.isEmpty()) {
            txtPhoneNumber.setError("This field cannot be empty");
            isValid = false;
        } else if (phoneNumber.length() > 10) {
            txtPhoneNumber.setError("Phone Number length cannot be greater than 10");
            isValid = false;
        } else if (phoneNumber.length() < 10) {
            txtPhoneNumber.setError("Phone Number length cannot be less than 10");
            isValid = false;
        } else {
            txtPhoneNumber.setErrorEnabled(false);
        }

        if (bloodGroup.isEmpty()) {
            txtBloodGroup.setError("This field cannot be empty");
            isValid = false;
        } else {
            txtBloodGroup.setErrorEnabled(false);
        }

        if (gender.isEmpty()) {
            txtGender.setError("Select any one");
            isValid = false;
        } else {
            txtGender.setErrorEnabled(false);
        }

        System.out.println(isValid);
        if (isValid) {

            progressBar.setVisibility(View.VISIBLE);
            student = new Student(name, emailAddress, phoneNumber, gender, bloodGroup, Integer.parseInt(rollNumber), "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT_EMdQsFpPIZnHbnfNnuDSjWW3BGMrNHQCiBLXqICLEg&s");
            auth.createUserWithEmailAndPassword(emailAddress, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                AuthResult res = task.getResult();
                                FirebaseUser user = res.getUser();
                                newUid = user.getUid();
                                addNewUserToDatabase();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Registration Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void addNewUserToDatabase() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("class", className);
        hashMap.put("type", "Student");
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(newUid)
                .set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddStudent.this, "Added to Database User", Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseFirestore.getInstance()
                .collection("Standards")
                .document(className)
                .collection("Students")
                .document(newUid)
                .set(student).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.INVISIBLE);
                        System.out.println("Added to Database Students");
                        Toast.makeText(AddStudent.this, "Added to Database Students", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}