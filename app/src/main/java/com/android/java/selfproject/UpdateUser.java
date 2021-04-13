package com.android.java.selfproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateUser extends AppCompatActivity {

    @Override
    UpdateUser(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        FirebaseAuth.getInstance().currentUser?.let { firebaseUser ->
                // if the user is logged in, display their info on the screen
                etEmail.setText(firebaseUser.email)
            etName.setText(firebaseUser.displayName)
            etPhone.setText(firebaseUser.phoneNumber)
//            Picasso.get().load(firebaseUser.photoUrl).into(ivUserImage)
        }

//        btnUpdateImage.setOnClickListener {
//            // update the profile image here
//        }

        btnUpdateName.setOnClickListener {
            // update the name here
        }

        btnUpdateEmail.setOnClickListener {
            // update the email here
        }

        btnUpdatePhone.setOnClickListener {
            // update the phone number here
        }

        btnUpdatePwd.setOnClickListener {
            // update the password here
        }

    }

}