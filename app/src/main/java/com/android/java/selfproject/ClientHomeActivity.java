package com.android.java.selfproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import static java.security.AccessController.getContext;

public class ClientHomeActivity extends AppCompatActivity {
    EditText findProName;

//  Appointments added here
    String[] mobileList = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_client_home, mobileList);

        ListView listView = (ListView) findViewById(R.id.client_list_view);
        listView.setAdapter(adapter);

        Button findPro = findViewById(R.id.find_pro_button);
        findProName = findViewById(R.id.find_pro_EditText);

        findPro.setOnClickListener((v) -> {
            startActivity(new Intent(ClientHomeActivity.this, ProHomeClientView.class));
            finish();
        });
    }
}