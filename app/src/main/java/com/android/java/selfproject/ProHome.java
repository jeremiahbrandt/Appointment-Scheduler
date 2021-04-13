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

public class ProHome extends AppCompatActivity {
    private String token;

    private TextView proName;
    private TextView shareableCode;
    private TextView emailAddress;
    private TextView profession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_home);

        proName = findViewById(R.id.pro_name);
        emailAddress = findViewById(R.id.pro_email);
        profession = findViewById(R.id.pro_profession);
        shareableCode = findViewById(R.id.shareable_code_ph);

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

            String endpoint ="ProfessionalManagement";
            String inputLine;
            try {
                String url = "http://10.0.2.2:7071/api/";
                HttpURLConnection connection = (HttpURLConnection) new URL(url + endpoint).openConnection();

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                connection.setRequestProperty("Authorization", token);

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
                proName.setText(professional.getAccount().getFirstName());
                emailAddress.setText(professional.getAccount().getEmailAddress());
                profession.setText(professional.getProfession());
                shareableCode.setText(professional.getShareableCode());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}