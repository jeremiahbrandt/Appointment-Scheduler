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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import util.ApiEndpointProvider;

public class ClientRegisterAccountActivity extends AppCompatActivity {
    private String token;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Clients");

    private EditText userName;
    private EditText userEmail;
    private EditText userFirstName;
    private EditText userLastName;
    private EditText userPass;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();

        Button registerAccountButton = findViewById(R.id.register_button_ra);
        Button prevActivityButton = findViewById(R.id.back_button_ra);
        progressBar = findViewById(R.id.register_progressBar);
        userName = findViewById(R.id.username_ra);
        userEmail = findViewById(R.id.email_ra);
        userFirstName = findViewById(R.id.firstName_ra);
        userLastName = findViewById(R.id.lastName_ra);
        userPass = findViewById(R.id.password_ra);

        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();

            if(currentUser != null) {
                // User is already logged in
                Toast.makeText(ClientRegisterAccountActivity.this, "User Logged in", Toast.LENGTH_SHORT).show();
            }else {
                // User is not logged in yet
                Toast.makeText(ClientRegisterAccountActivity.this, "Log in", Toast.LENGTH_SHORT).show();
            }
        };

        prevActivityButton.setOnClickListener(v -> {
            Intent prevPageIntent = new Intent(ClientRegisterAccountActivity.this, ClientLoginActivity.class);
            startActivity(prevPageIntent);
        });

        registerAccountButton.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(userEmail.getText().toString()) && !TextUtils.isEmpty(userPass.getText().toString()) && !TextUtils.isEmpty(userName.getText().toString())) {
                String email = userEmail.getText().toString().trim();
                String password = userPass.getText().toString().trim();
                String username = userName.getText().toString().trim();
                String firstName = userFirstName.getText().toString();
                String lastName = userLastName.getText().toString();

                createUserEmailAccount(email, password, username, firstName, lastName);
            }else {
                Toast.makeText(ClientRegisterAccountActivity.this, "Empty Fields Not Allowed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createUserEmailAccount(String email, String password, String username, String firstName, String lastName) {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)  && !TextUtils.isEmpty(firstName)  && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(username)) {

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    // Take user to their home page
                    currentUser = firebaseAuth.getCurrentUser();
                    assert currentUser != null;
                    String currentUserId = currentUser.getUid();

                    FirebaseAuth.getInstance().getAccessToken(true).addOnCompleteListener(registerTask -> {
                        token = registerTask.getResult().getToken();
                        // Call our api
                        new ApiRequest().execute(firstName, lastName);
                    });

                    // Map user
                    Map<String, String> userObject = new HashMap<>();
                    userObject.put("userId", currentUserId);
                    userObject.put("username", username);
                    userObject.put("email", email);
                    userObject.put("password", password);

                    // Save User to Firebase database
                    collectionReference.add(userObject).addOnSuccessListener(documentReference -> documentReference.get().addOnCompleteListener(task1 -> {
                        if(task1.getResult().exists()) {
                            startActivity(new Intent(ClientRegisterAccountActivity.this, ClientHomeActivity.class));
                            finish();
                        }
                    })).addOnFailureListener(e -> Toast.makeText(ClientRegisterAccountActivity.this, "Failed to make account", Toast.LENGTH_SHORT).show());

                }else {
                    // Something went wrong
                    Toast.makeText(ClientRegisterAccountActivity.this, "Failed to make account", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(ClientRegisterAccountActivity.this, "Failed to make account", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private class ApiRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        public String doInBackground(String... params){
            String result = "";

            String endpoint ="/client/register";
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

                RegistrationRequestBody body = new RegistrationRequestBody(params[0], params[1]);
                String jsonInput = new Gson().toJson(body);
                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
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

    private class RegistrationRequestBody {
        private String FirstName;
        private String LastName;

        protected RegistrationRequestBody(String firstName, String lastName) {
            this.FirstName = firstName;
            this.LastName = lastName;
        }
    }
}