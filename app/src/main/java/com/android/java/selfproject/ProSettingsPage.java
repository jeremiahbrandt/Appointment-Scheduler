package com.android.java.selfproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import Models.Professional;
import util.ApiEndpointProvider;

public class ProSettingsPage extends AppCompatActivity {
    private String token;

    private EditText firstName;
    private EditText lastName;
    private EditText profession;
    private EditText streetName;
    private EditText streetNumber;
    private EditText city;
    private EditText state;
    private EditText zipCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_professional);

        firstName = findViewById(R.id.pro_update_firstName);
        lastName = findViewById(R.id.pro_update_lastName);
        profession = findViewById(R.id.pro_update_profession);
        streetName = findViewById(R.id.pro_update_streetName);
        streetNumber = findViewById(R.id.pro_update_streetNumber);
        city = findViewById(R.id.pro_update_city);
        state = findViewById(R.id.pro_update_state);
        zipCode = findViewById(R.id.pro_update_zipCode);

        findViewById(R.id.pro_update_button).setOnClickListener(v -> {
            FirebaseAuth.getInstance().getAccessToken(true).addOnCompleteListener(task -> {
                token = task.getResult().getToken();
                // Call our api
                new GetRequest().execute();
            });
        });

        findViewById(R.id.pro_update_back_button).setOnClickListener(v -> {
            startActivity(new Intent(ProSettingsPage.this, ProHome.class));
            finish();
        });

        FirebaseAuth.getInstance().getAccessToken(true).addOnCompleteListener(task -> {
            token = task.getResult().getToken();
            // Call our api
            new GetRequest().execute();
        });
    }

    private class UpdateRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "PUT";
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
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");

                UpdateRequestBody body = createdUpdateRequestBody();
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

        @Override
        protected void onPostExecute(String s) {
            new GetRequest().execute();
        }
    }

    private class GetRequest extends AsyncTask<String, Void, String> {
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

                firstName.setText(professional.getFirstName());
                lastName.setText(professional.getLastName());
                profession.setText(professional.getProfession());
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

    private UpdateRequestBody createdUpdateRequestBody() {
        UpdateRequestBody requestBody = new UpdateRequestBody();

        requestBody.FirstName = firstName.getText().toString() ?? "";
        requestBody.LastName = lastName.getText().toString();
        requestBody.Occupation = profession.getText().toString();
        try {
            requestBody.StreetNumber = Integer.parseInt(streetNumber.getText().toString());
        } catch (Exception ex) {

        }
        requestBody.StreetName = streetName.getText().toString();
        requestBody.City = city.getText().toString();
        requestBody.State = state.getText().toString();
        try {
            requestBody.ZipCode = Integer.parseInt(zipCode.getText().toString());
        } catch (Exception ex) {
            continue;
        }

        return requestBody;
    }

    // TODO: Extract to own reusable class
    private class UpdateRequestBody {
        protected String FirstName;
        protected String LastName;
        protected String Occupation;
        protected int StreetNumber;
        protected String StreetName;
        protected String City;
        protected String State;
        protected int ZipCode;

        protected UpdateRequestBody() { }
    }
}
