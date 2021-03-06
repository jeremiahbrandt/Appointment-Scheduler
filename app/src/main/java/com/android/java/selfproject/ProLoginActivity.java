package com.android.java.selfproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import util.BookingApi;

public class ProLoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Professionals");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_login);
        progressBar = findViewById(R.id.pro_login_progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.pro_email);
        password = findViewById(R.id.pro_password);
        Button proLogin = findViewById(R.id.pro_login_button);
        Button prevPage = findViewById(R.id.pro_back_button_la);
        Button registerAcct = findViewById(R.id.pro_register);

        proLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailPasswordUser(email.getText().toString().trim(), password.getText().toString().trim());
            }
        });

        prevPage.setOnClickListener((v) -> {
            startActivity(new Intent(ProLoginActivity.this, MainActivity.class));
            finish();
        });

        registerAcct.setOnClickListener((v) -> {
            startActivity(new Intent(ProLoginActivity.this, ProRegisterAccountActivity.class));
            finish();
        });
    }

    private void loginEmailPasswordUser(String email, String password) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                assert user != null;
                final String currentUserId = user.getUid();

                collectionReference.whereEqualTo("userId", currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {

                        }
                        assert queryDocumentSnapshots != null;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                BookingApi bookingApi = BookingApi.getInstance();
                                bookingApi.setUsername(snapshot.getString("username"));
                                bookingApi.setUserId(snapshot.getString("userId"));

                                //Go to Post Login Activity
                                startActivity(new Intent(ProLoginActivity.this, ProHome.class));
                            }
                        }
                    }
                });
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(ProLoginActivity.this, "Please enter email and password", Toast.LENGTH_LONG).show();
        }
    }
}
