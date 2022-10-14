package com.kartik.homework.student;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;
import com.kartik.homework.databinding.ActivityProfileSBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileS extends StudentDrawer {

    ActivityProfileSBinding activityProfileSBinding;
    SharedPreferences sp;
    String className, uId;
    TextView txtName, txtRollNumber, txtEmail, txtPhone, txtBloodGroup;
    String name, email, phone, bloodGroup, image;
    ImageView imgProfilePhoto;
    TextInputLayout editEmail, editPhone;
    ImageButton editBtn;
    ProgressBar progressBar;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileSBinding = ActivityProfileSBinding.inflate(getLayoutInflater());
        setContentView(activityProfileSBinding.getRoot());
        allocateActivityTitle("Profile");

        sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        uId = sp.getString(MainActivity.id, null);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        editEmail = (TextInputLayout) findViewById(R.id.editEmail);
        editPhone = (TextInputLayout) findViewById(R.id.editPhone);

        txtName = (TextView) findViewById(R.id.txtName);
        txtRollNumber = (TextView) findViewById(R.id.txtTeacherId);
        txtEmail = (TextView) findViewById(R.id.txtUgDegree);
        txtPhone = (TextView) findViewById(R.id.txtPgDegree);
        txtBloodGroup = (TextView) findViewById(R.id.txtBloodGroup);
        imgProfilePhoto = (ImageView) findViewById(R.id.imgProfilePhoto);

        editBtn = (ImageButton) findViewById(R.id.editBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDetails();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDetails();
            }
        });

        getDetails();
    }

    private void getDetails() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println(user.getUid());
        FirebaseFirestore.getInstance()
                .collection("Standards")
                .document(className)
                .collection("Students")
                .document(uId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = documentSnapshot.getString("name");
                        email = documentSnapshot.getString("emailAddress");
                        bloodGroup = documentSnapshot.getString("bloodGroup");
                        phone = documentSnapshot.getString("phoneNumber");
                        image = documentSnapshot.getString("image");
                        String rollNo = documentSnapshot.getLong("rollNumber").toString();

                        txtName.setText(name);
                        txtRollNumber.setText(rollNo);
                        txtEmail.setText(email);
                        txtBloodGroup.setText(bloodGroup);
                        txtPhone.setText(phone);
                        Picasso.get().load(image).into(imgProfilePhoto);
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }

    private void editDetails() {
        Toast.makeText(this, "Fields that are replaced by Input can be edited", Toast.LENGTH_SHORT).show();

        txtPhone.setVisibility(View.INVISIBLE);
        txtEmail.setVisibility(View.INVISIBLE);

        editPhone.setVisibility(View.VISIBLE);
        editEmail.setVisibility(View.VISIBLE);

        submitBtn.setVisibility(View.VISIBLE);
    }

    private boolean validate() {
        boolean isValid = true;

        email = editEmail.getEditText().getText().toString().trim();
        phone = editPhone.getEditText().getText().toString().trim();
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Matcher emailMatcher = Pattern.compile(regex).matcher(email);

        if (email.isEmpty()) {
            editEmail.setError("This field cannot be empty");
            isValid = false;
        } else if (!emailMatcher.matches()) {
            editEmail.setError("Wrong Email ID");
            isValid = false;
        } else {
            editEmail.setErrorEnabled(false);
        }

        if (phone.isEmpty()) {
            editPhone.setError("This field cannot be empty");
            isValid = false;
        } else if (phone.length() > 10) {
            editPhone.setError("Phone Number length cannot be greater than 10");
            isValid = false;
        } else if (phone.length() < 10) {
            editPhone.setError("Phone Number length cannot be less than 10");
            isValid = false;
        } else {
            editPhone.setErrorEnabled(false);
        }

        return isValid;
    }

    private void submitDetails() {
        if (validate()) {
            if (email == null || phone == null) {
                Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("emailId", email);
                hashMap.put("phoneNumber", phone);

                FirebaseFirestore.getInstance()
                        .collection("Standards")
                        .document(className)
                        .collection("Students")
                        .document(uId).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                editEmail.setVisibility(View.INVISIBLE);
                                editPhone.setVisibility(View.INVISIBLE);
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                txtPhone.setVisibility(View.VISIBLE);
                                txtEmail.setVisibility(View.VISIBLE);
                                submitBtn.setVisibility(View.INVISIBLE);
                                getDetails();
                            }
                        });
            }
        }

    }
}