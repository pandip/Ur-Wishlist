package com.app.dtsfinaltask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.app.dtsfinaltask.adapterslide.Slide;
import com.app.dtsfinaltask.slide.FirstSlide;
import com.app.dtsfinaltask.slide.SecondSlide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeSlide extends AppCompatActivity {

    Slide adapterSlide;
    ViewPager viewPager;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_slide);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        adapterSlide = new Slide(getSupportFragmentManager());
        adapterSlide.addFragment(new FirstSlide(), "");
        adapterSlide.addFragment(new SecondSlide(), "");

        viewPager.setAdapter(adapterSlide);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void goLogin(View view){
        Intent i = new Intent(WelcomeSlide.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}