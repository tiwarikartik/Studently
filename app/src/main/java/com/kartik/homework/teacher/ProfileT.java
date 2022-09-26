package com.kartik.homework.teacher;

import android.os.Bundle;

import com.kartik.homework.databinding.ActivityProfileTBinding;

public class ProfileT extends TeacherDrawer {

    ActivityProfileTBinding activityProfileBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = ActivityProfileTBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());
        allocateActivityTitle("Profile");
    }
}