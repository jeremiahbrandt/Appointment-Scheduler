package com.android.java.selfproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
import util.BookingApi;

public class ProRegisterAccountActivity extends AppCompatActivity {
    private String token;

    private EditText email;
    private EditText password;
    private EditText username;
    private EditText firstName;
    private EditText lastName;
    private EditText occupation;
    private EditText streetNumber;
    private EditText streetName;
    private EditText city;
    private EditText state;
    private EditText zipCode;
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
        firstName = findViewById(R.id.pro_firstName_ra);
        lastName = findViewById(R.id.pro_lastName_ra);
        occupation = findViewById(R.id.pro_occupation_ra);
        streetNumber = findViewById(R.id.pro_streetNumber_ra);
        streetName = findViewById(R.id.pro_streetName_ra);
        city = findViewById(R.id.pro_city_ra);
        state = findViewById(R.id.pro_state_ra);
        zipCode = findViewById(R.id.pro_zipCode_ra);
        progressBar = findViewById(R.id.pro_register_progressBar);

        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                // Being invoked when not supposed to
                // user is already logged in
                Toast.makeText(ProRegisterAccountActivity.this, "User logged in", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(ProRegisterAccountActivity.this, ProLoginActivity.class);
//                startActivity(intent);
            } else {
                // no user yet
                Toast.makeText(ProRegisterAccountActivity.this, "Continue to register", Toast.LENGTH_SHORT).show();
            }
        };

        registerButton.setOnClickListener(v -> {
            EditText[] editTexts = new EditText[]{ email, username, password, firstName, lastName, occupation, streetNumber, streetName, city, state, zipCode };
            if (verifyAllAreNotEmpty(editTexts)) {
                String pro_email = email.getText().toString().trim();
                String pro_pass = password.getText().toString().trim();
                String pro_username = username.getText().toString().trim();

                createUserEmailAccount(pro_email, pro_username, pro_pass);

            } else {
                Toast.makeText(ProRegisterAccountActivity.this, "Empty Fields Not Allowed", Toast.LENGTH_LONG).show();
            }
        });

        prevPageButton.setOnClickListener((v) -> {
            startActivity(new Intent(ProRegisterAccountActivity.this, ProLoginActivity.class));
            finish();
        });
    }

    private void createUserEmailAccount(String email, String username, String password) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Take user to their home page
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                assert currentUser != null;
                String currentUserId = currentUser.getUid();

                // Map user
                Map<String, String> userObj = new HashMap<>();
                userObj.put("userId", currentUserId);
                userObj.put("username", username);
                userObj.put("email", email);
                userObj.put("password", password);

                // Save User to Firestore database
                collectionReference.add(userObj).addOnSuccessListener(documentReference -> documentReference.get().addOnCompleteListener(task1 -> {
                    if (task1.getResult().exists()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        String name = task1.getResult().getString("username");

                        BookingApi bookingApi = BookingApi.getInstance();
                        bookingApi.setUserId(currentUserId);
                        bookingApi.setUsername(name);

                        FirebaseAuth.getInstance().getAccessToken(true).addOnCompleteListener(register -> {
                            token = register.getResult().getToken();
                            // Call our api
                            new ApiRequest().execute();
                        });

                        Intent intent = new Intent(ProRegisterAccountActivity.this, ProLoginActivity.class);
                        intent.putExtra("username", name);
                        intent.putExtra("userId", currentUserId);
                        startActivity(intent);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })).addOnFailureListener(e -> Toast.makeText(ProRegisterAccountActivity.this, "Failed to make account", Toast.LENGTH_SHORT).show());
            } else {
                // Something went wrong
                Toast.makeText(ProRegisterAccountActivity.this, "Failed to make account", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(ProRegisterAccountActivity.this, "Failed to make account", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private class ApiRequest extends AsyncTask<String, Void, String> {
        private final String REQUEST_METHOD = "POST";
        private final int READ_TIMEOUT = 15000;
        private final int CONNECTION_TIMEOUT = 15000;

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

                RegistrationRequestBody body = new RegistrationRequestBody(
                        firstName.getText().toString(),
                        lastName.getText().toString(),
                        occupation.getText().toString(),
                        Integer.parseInt(streetNumber.getText().toString()),
                        streetName.getText().toString(),
                        city.getText().toString(),
                        state.getText().toString(),
                        Integer.parseInt(zipCode.getText().toString())
                );
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
        private String Occupation;
        private int StreetNumber;
        private String StreetName;
        private String City;
        private String State;
        private int ZipCode;

        protected RegistrationRequestBody(String firstName, String lastName, String occupation, int streetNumber, String streetName, String city, String state, int zipCode) {
            FirstName = firstName;
            LastName = lastName;
            Occupation = occupation;
            StreetNumber = streetNumber;
            StreetName = streetName;
            City = city;
            State = state;
            ZipCode = zipCode;
        }
    }

    private boolean verifyAllAreNotEmpty(EditText[] editTexts) {
        for(EditText editText: editTexts) {
            if(TextUtils.isEmpty(editText.getText())) {
                return false;
            }
        }
        return true;
    }
}