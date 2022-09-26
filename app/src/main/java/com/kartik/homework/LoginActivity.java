package com.kartik.homework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private TextInputEditText email, password;
    private TextInputLayout lyEmail, lyPassword;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (TextInputEditText) findViewById(R.id.email);
        password = (TextInputEditText) findViewById(R.id.password);
        lyEmail = (TextInputLayout) findViewById(R.id.lyEmail);
        lyPassword = (TextInputLayout) findViewById(R.id.lyPassword);
        Button logIn = (Button) findViewById(R.id.logIn);
        auth = FirebaseAuth.getInstance();

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void validate() {
        boolean isValid = true;
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailStr);

        System.out.println(emailStr + passwordStr + " " + pattern.matcher(emailStr).matches());
        if (emailStr.isEmpty()) {
            lyEmail.setError("This field is mandatory");
            isValid = false;
        } else if (!matcher.matches()) {
            lyEmail.setError("Incorrect Email Format");
            isValid = false;
        } else {
            lyEmail.setErrorEnabled(false);
        }

        if (passwordStr.isEmpty()) {
            lyPassword.setError("This field is mandatory");
            isValid = false;
        } else if (passwordStr.length() < 4) {
            lyPassword.setError("Password too Short");
            isValid = false;
        }  else if (passwordStr.length() > 8) {
            lyPassword.setError("Password too Long");
            isValid = false;
        } else {
            lyPassword.setErrorEnabled(false);
        }

        if (isValid) {
            auth.signInWithEmailAndPassword(emailStr, passwordStr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    FirebaseUser user = authResult.getUser();

                    preferences = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(MainActivity.id, user.getUid());
                    editor.commit();

                    Toast.makeText(LoginActivity.this, "Shared Preferences Updated Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            });
        }
    }
}