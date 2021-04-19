package com.android.java.selfproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Models.Client;
import Models.Professional;
import util.ApiEndpointProvider;

public class ClientHomeActivity extends AppCompatActivity {
    private String token;

    EditText findProName;

    private TextView name;
    private ListView upcomingAppointments;

    private ArrayAdapter<String> arrayAdapter;
    private List<String> appointmentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        Button findPro = findViewById(R.id.find_pro_button);
        findProName = findViewById(R.id.find_pro_EditText);
        name = findViewById(R.id.name);
        upcomingAppointments = findViewById(R.id.client_list_view);

        appointmentsList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, appointmentsList);
        upcomingAppointments.setAdapter(arrayAdapter);

        findPro.setOnClickListener((v) -> {
            EditText shareableCodeEditText = findViewById(R.id.find_pro_EditText);
            String code = shareableCodeEditText.getText().toString();

            if(code != null && code.length() == 6) {
                Intent intent = new Intent(ClientHomeActivity.this, ProHomeClientView.class);
                intent.putExtra("shareableCode", code);
                startActivity(intent);
                finish();
            }
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
                name.setText(client.getFirstName() + " " + client.getLastName());
                appointmentsList.addAll(client.getAppointmentsStringList());
                arrayAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}