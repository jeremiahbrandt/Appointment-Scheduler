package com.android.java.selfproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import Models.Professional;
import util.ApiEndpointProvider;

public class ProHomeClientView extends AppCompatActivity {
    private String token;

    private TextView professionalName;
    private TextView professionalAddress;
    private Button calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_home_client_view);

        professionalName = findViewById(R.id.client_pro_page_professionalName);
        professionalAddress = findViewById(R.id.client_pro_page_professionalAddress);
        calendar = findViewById(R.id.calendar);

        String shareableCode = (String)getIntent().getSerializableExtra("shareableCode");
        // Get token
        FirebaseAuth.getInstance().getAccessToken(true).addOnCompleteListener(task -> {
            token = task.getResult().getToken();
            // Call our api
            new LookupProfessionalRequest().execute(shareableCode);
        });

        calendar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.TITLE, professionalName.getText().toString());
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, professionalAddress.getText().toString());
            intent.putExtra(CalendarContract.Events.ALL_DAY, false);

            startActivity(intent);

           /* if (intent.resolveActivity(getPackageManager()) != null) {
            } else {
                Toast.makeText(ProHomeClientView.this, "Download Google Calendar", Toast.LENGTH_SHORT).show();
            }*/
        });
    }

    private class LookupProfessionalRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        public String doInBackground(String... params) {
            String result = "";

            String endpoint ="/professional";
            String inputLine;

            try {
                String url = ApiEndpointProvider.url;

                HttpURLConnection connection = (HttpURLConnection) new URL(url + endpoint + "?code="+params[0]).openConnection();

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
                Professional professional = new Professional(new JSONObject(s));
                String name = professional.getFirstName() + " " + professional.getLastName() + " - " + professional.getProfession();
                String address = professional.getStreetNumber() + " " + professional.getStreetName() + "\n" + professional.getCity() + ", " + professional.getState() + professional.getZipCode();

                professionalName.setText(name);
                professionalAddress.setText(address);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}