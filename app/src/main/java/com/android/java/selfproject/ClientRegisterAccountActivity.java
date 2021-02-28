package com.android.java.selfproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ClientRegisterAccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Clients");

    private EditText userName;
    private EditText userEmail;
    private EditText userPass;
    private ProgressBar progressBar;
    private Button registerAccountButton;
    private Button prevActivityButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();

        registerAccountButton = findViewById(R.id.register_button_ra);
        prevActivityButton = findViewById(R.id.back_button_ra);
        progressBar = findViewById(R.id.register_progressBar);
        userName = findViewById(R.id.username_ra);
        userEmail = findViewById(R.id.email_ra);
        userPass = findViewById(R.id.password_ra);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null) {
                    // User is already logged in
                    Toast.makeText(ClientRegisterAccountActivity.this, "User Logged in", Toast.LENGTH_SHORT).show();
                }else {
                    // User is not logged in yet
                    Toast.makeText(ClientRegisterAccountActivity.this, "Log in", Toast.LENGTH_SHORT).show();
                }
            }
        };

        prevActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prevPageIntent = new Intent(ClientRegisterAccountActivity.this, ClientLoginActivity.class);
                startActivity(prevPageIntent);
            }
        });

        registerAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(userEmail.getText().toString())
                    && !TextUtils.isEmpty(userPass.getText().toString())
                    && !TextUtils.isEmpty(userName.getText().toString())) {

                    String email = userEmail.getText().toString().trim();
                    String password = userPass.getText().toString().trim();
                    String username = userName.getText().toString().trim();
                    createUserEmailAccount(email, password, username);

                }else {
                    Toast.makeText(ClientRegisterAccountActivity.this, "Empty Fields Not Allowed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createUserEmailAccount(String email, String password, String username) {
        if(!TextUtils.isEmpty(email)
            && !TextUtils.isEmpty(password)
            && !TextUtils.isEmpty(username)) {

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        // Take user to their home page
                        currentUser = firebaseAuth.getCurrentUser();
                        String currentUserId = currentUser.getUid();

                        // Map user
                        Map<String, String> userObject = new HashMap<>();
                        userObject.put("userId", currentUserId);
                        userObject.put("username", username);

                        // Save User to Firebase database
                        collectionReference.add(userObject).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.getResult().exists()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            String name = task.getResult().getString("username");

                                            Intent intent = new Intent(ClientRegisterAccountActivity.this, ClientLoginActivity.class);
                                            intent.putExtra("username", name);
                                            intent.putExtra("userId", currentUserId);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                    }else {
                        // Something went wrong
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }else {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}