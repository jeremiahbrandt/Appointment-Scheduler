package com.android.java.selfproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Models.Appointment;
import Models.Client;
import Models.Professional;
import util.ApiEndpointProvider;

import static java.security.AccessController.getContext;

public class ClientHomeActivity extends AppCompatActivity {
    private String token;

    EditText findProName;

    private TextView firstName;
    private TextView lastName;

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
        firstName = findViewById(R.id.firstName);
        lastName= findViewById(R.id.lastName);

        findPro.setOnClickListener((v) -> {
            startActivity(new Intent(ClientHomeActivity.this, ProHomeClientView.class));
            finish();
        });

        update();
    }

    private void update() {
        // Get token
        FirebaseAuth.getInstance().getAccessToken(true).addOnCompleteListener(task -> {
            token = task.getResult().getToken();
            // Call our api
            new ApiRequest().execute();
        });
    }

    private class ApiRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        public String doInBackground(String... params){
            String result = "";

            String endpoint ="/client";
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
                Client client = new Client(new JSONObject(s));

                // Set UI values
                firstName.setText(client.getFirstName());
                lastName.setText(client.getLastName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}