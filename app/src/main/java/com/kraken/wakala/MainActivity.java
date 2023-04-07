package com.kraken.wakala;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.kraken.wakala.R;
import com.kraken.wakala.activities.HomeActivity;
import com.kraken.wakala.activities.LoginActivity;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, LoginActivity.class);
//        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}