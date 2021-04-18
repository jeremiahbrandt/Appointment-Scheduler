package com.android.java.selfproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Models.Professional;
import util.ApiEndpointProvider;

public class ProHome extends AppCompatActivity {
    private String token;

    private TextView firstName;
    private TextView lastName;
    private TextView emailAddress;
    private TextView profession;
    private TextView shareableCode;
    private TextView streetName;
    private TextView streetNumber;
    private TextView city;
    private TextView state;
    private TextView zipCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_home);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        emailAddress = findViewById(R.id.pro_email);
        profession = findViewById(R.id.pro_profession);
        shareableCode = findViewById(R.id.shareable_code_ph);
        streetName = findViewById(R.id.streetName);
        streetNumber = findViewById(R.id.streetNumber);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        zipCode = findViewById(R.id.zipCode);


//        Settings Button initialization
        FloatingActionButton settings = findViewById(R.id.settings_button_ph);

        settings.setOnClickListener((v) -> {
            startActivity(new Intent(ProHome.this, SettingsPane.class));
            finish();
        });

        updateProfessional();
    }


    private void updateProfessional() {
        // Get token
        FirebaseAuth.getInstance().getAccessToken(true).addOnCompleteListener(task -> {
            token = task.getResult().getToken();
            // Call our api
            new ApiRequest().execute();
        });
    }

    public class ApiRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        public String doInBackground(String... params){
            String result = "";

            String endpoint ="/professional";
            String inputLine;
            try {
               String url = ApiEndpointProvider.url;

                HttpURLConnection connection = (HttpURLConnection) new URL(url + endpoint).openConnection();

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                connection.setRequestProperty("Authorization", "Bearer " + token);

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

        @Override
        protected void onPostExecute(String s) {
            try {
                // Create a new Professional from JObject
                Professional professional = new Professional(new JSONObject(s));

                // Set UI values
                firstName.setText(professional.getFirstName());
                lastName.setText(professional.getLastName());
                emailAddress.setText(professional.getEmailAddress());
                profession.setText(professional.getProfession());
                shareableCode.setText(professional.getShareableCode());
                streetNumber.setText(String.valueOf(professional.getStreetNumber()));
                streetName.setText(professional.getStreetName());
                city.setText(professional.getCity());
                state.setText(professional.getState());
                zipCode.setText(String.valueOf(professional.getZipCode()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}