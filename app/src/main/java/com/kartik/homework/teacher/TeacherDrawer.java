package com.kartik.homework.teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;

public class TeacherDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.nav_drawer_teacher, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), ProfileT.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.homeworks:
                startActivity(new Intent(getApplicationContext(), TeacherActivity.class));
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.assign_homework:
                startActivity(new Intent(getApplicationContext(), GenerateHomework.class));
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.add_student:
                startActivity(new Intent(getApplicationContext(), AddStudent.class));
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Share our App");
                i.putExtra(Intent.EXTRA_TEXT, "https://semv.netlify.app/");
                startActivity(Intent.createChooser(i, "Share URL"));
                break;
            case R.id.contact:
                Toast.makeText(getApplicationContext(), "Contact Us", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                SharedPreferences preferences = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(MainActivity.classDiv);
                editor.remove(MainActivity.id);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
        }
        return false;
    }

    protected void allocateActivityTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}

