package com.example.asitakipson.config;

import android.app.Application;
import android.content.Intent;

import com.example.asitakipson.MainActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            startActivity(new Intent( Home.this, MainActivity.class));
        }
    }
}
