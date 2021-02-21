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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import util.BookingApi;

public class ClientLoginActivity extends AppCompatActivity {
    
    private Button clientLogin;
    private Button prevPage;
    private Button registerAcct;
    
    private EditText email;
    private EditText password;
    
    private ProgressBar progressBar;
    
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Clients");
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.client_login_progressBar);
        
        firebaseAuth = FirebaseAuth.getInstance();
        
        email = findViewById(R.id.client_email);
        password = findViewById(R.id.client_password);
        clientLogin = findViewById(R.id.client_login_button);
        prevPage = findViewById(R.id.client_back_button_la);
        registerAcct = findViewById(R.id.client_register);
        
        registerAcct.setOnClickListener((v) -> {
            startActivity(new Intent(ClientLoginActivity.this, ClientRegisterAccountActivity.class));
            finish();
        });
        
        prevPage.setOnClickListener((v) -> {
            startActivity(new Intent(ClientLoginActivity.this, MainActivity.class));
            finish();
        });
        
        clientLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailPasswordUser(email.getText().toString().trim(),
                        password.getText().toString().trim());
            }
        });
    }
    
    private void loginEmailPasswordUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            final String currentUserId = user.getUid();

                            collectionReference
                                    .whereEqualTo("userId", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                            @Nullable FirebaseFirestoreException e) {

                                            if (e != null) {
                                            }
                                            assert queryDocumentSnapshots != null;
                                            if (!queryDocumentSnapshots.isEmpty()) {

                                                progressBar.setVisibility(View.INVISIBLE);
                                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                    BookingApi bookingApi = BookingApi.getInstance();
                                                    bookingApi.setUsername(snapshot.getString("username"));
                                                    bookingApi.setUserId(snapshot.getString("userId"));


                                                    startActivity(new Intent(ClientLoginActivity.this,
                                                            ClientHomeActivity.class));


                                                }
                                            }

                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });



        }else {

            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(ClientLoginActivity.this,
                    "Please enter email and password",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
    
}