package com.android.java.selfproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.view.View;
import android.widget.Button;



public class SettingsPane extends AppCompatActivity {

    private static final String TAG = "";
    Button internet, volume, viewAcctInfo, DeleteUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        internet= findViewById(R.id.internetButton);
        volume = findViewById(R.id.volumeButton);
        viewAcctInfo = findViewById(R.id.viewAcctInfo);

        internet.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view){
                startActivity(new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY));

            }
        });

        volume.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.Panel.ACTION_VOLUME));
            }
        });
        viewAcctInfo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsPane.this, AccountInfoActivity.class));
                finish();
            }
        });
    }
}