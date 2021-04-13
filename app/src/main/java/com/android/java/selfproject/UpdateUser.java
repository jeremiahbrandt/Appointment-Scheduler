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
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateUser extends AppCompatActivity {
    Button btnUpdateName, btnUpdateEmail, btnUpdatePhone, btnUpdatePwd;
    EditText etName, etEmail, etPhone, etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        btnUpdateName = findViewById(R.id.btnUpdateName);
        btnUpdateEmail = findViewById(R.id.btnUpdateEmail);
        btnUpdatePhone = findViewById(R.id.btnUpdatePhone);
        btnUpdatePwd = findViewById(R.id.btnUpdatePwd);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnUpdatePwd = findViewById(R.id.btnUpdatePwd);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        findViewById(R.id.etEmail);

        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement
            }
        });

        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement
            }
        });

        btnUpdatePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement
            }
        });

        btnUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement
            }
        });

    }

}