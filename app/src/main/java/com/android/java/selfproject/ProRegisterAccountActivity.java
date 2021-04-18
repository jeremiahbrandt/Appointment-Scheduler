package com.android.java.selfproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Models.Professional;
import io.grpc.internal.JsonUtil;
import util.ApiEndpointProvider;
import util.BookingApi;

public class ProRegisterAccountActivity extends AppCompatActivity {
    private String token;

    private EditText email;
    private EditText password;
    private EditText username;
    private ProgressBar progressBar;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Check Cloud Firestore for user data
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Professionals");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_register_account);

        firebaseAuth = FirebaseAuth.getInstance();

        Button registerButton = findViewById(R.id.pro_register_button_ra);
        Button prevPageButton = findViewById(R.id.pro_back_button_ra);
        email = findViewById(R.id.pro_email_ra);
        password = findViewById(R.id.pro_password_ra);
        username = findViewById(R.id.pro_username_ra);
        progressBar = findViewById(R.id.pro_register_progressBar);

        authStateListener = (FirebaseAuth.AuthStateListener) (firebaseAuth) -> {
            currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                // Being invoked when not supposed to
                // user is already logged in
                Toast.makeText(ProRegisterAccountActivity.this, "User logged in", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(ProRegisterAccountActivity.this, ProLoginActivity.class);
//                startActivity(intent);
            }else {
                // no user yet
                Toast.makeText(ProRegisterAccountActivity.this, "Continue to register", Toast.LENGTH_SHORT).show();
            }

        };

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email.getText().toString())
                        && !TextUtils.isEmpty(password.getText().toString())
                        && !TextUtils.isEmpty(username.getText().toString())) {

                    String pro_email = email.getText().toString().trim();
                    String pro_pass = password.getText().toString().trim();
                    String pro_username = username.getText().toString().trim();

                    createUserEmailAccount(pro_email, pro_pass, pro_username);

                } else {
                    Toast.makeText(ProRegisterAccountActivity.this, "Empty Fields Not Allowed", Toast.LENGTH_LONG).show();
                }
            }
        });

        prevPageButton.setOnClickListener((v) -> {
            startActivity(new Intent(ProRegisterAccountActivity.this, ProLoginActivity.class));
            finish();
        });
    }

    private void createUserEmailAccount(String email, String password, String username) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Take user to their home page
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                String currentUserId = currentUser.getUid();

                                FirebaseAuth.getInstance().getAccessToken(true).addOnCompleteListener(registerInApitask -> {
                                    token = registerInApitask.getResult().getToken();
                                    // Call our api
                                    new ApiRequest().execute();
                                });

                                // Map user
                                Map<String, String> userObj = new HashMap<>();
                                userObj.put("userId", currentUserId);
                                userObj.put("username", username);
                                userObj.put("email", email);
                                userObj.put("password", password);

                                // Save User to Firestore database
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.getResult().exists()) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    String name = task.getResult()
                                                            .getString("username");

                                                    BookingApi bookingApi = BookingApi.getInstance();
                                                    bookingApi.setUserId(currentUserId);
                                                    bookingApi.setUsername(name);

                                                    Intent intent = new Intent(ProRegisterAccountActivity.this,
                                                            ProLoginActivity.class);
                                                    intent.putExtra("username", name);
                                                    intent.putExtra("userId", currentUserId);
                                                    startActivity(intent);
                                                    
                                                }else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        });
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProRegisterAccountActivity.this, "Failed to make account",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                // Something went wrong
                                Toast.makeText(ProRegisterAccountActivity.this, "Failed to make account",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProRegisterAccountActivity.this, "Failed to make account",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public class ApiRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        public String doInBackground(String... params){
            String result = "";

            String endpoint ="/professional/register";
            String inputLine;
            try {
                String url = ApiEndpointProvider.url;

                HttpURLConnection connection = (HttpURLConnection) new URL(url + endpoint).openConnection();

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");

                RegistrationRequestBody body = new RegistrationRequestBody();
                String jsonInput = new Gson().toJson(body);
                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                connection.connect();

                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }

                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();

                //Set our result equal to our stringBuilder
                result += stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    // TODO: Set these values
    class RegistrationRequestBody {
        String FirstName;
        String LastName;
        String Occupation;
        int StreetNumber;
        String StreetName;
        String City;
        String State;
        int ZipCode;

        public RegistrationRequestBody() {
            this.FirstName = "Default_FirstName";
            this.LastName = "Default_LastName";
            this.Occupation = "Default_Occupation";
            this.StreetNumber = -1;
            this.StreetName = "Default_StreetName";
            this.City = "Default_City";
            this.State = "Default_State";
            this.ZipCode = -1;
        }
    }
}