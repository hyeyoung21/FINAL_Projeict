package com.example.ma01_20200942;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchFromNameActivity extends AppCompatActivity {

    EditText tvText;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_from_name);
        tvText = findViewById(R.id.edText);
    }

    public void onNameButtonClick(View v) throws IOException {
        if (v.getId() == R.id.button) {
            String locationName = tvText.getText().toString();

            if (locationName != null) {
                Geocoder geocoder = new Geocoder(SearchFromNameActivity.this, Locale.getDefault());

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(locationName, 3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                double lat = addresses.get(0).getLatitude();
                double lng = addresses.get(0).getLongitude();
                Log.e(TAG, lat + ", "+ lng);

                Intent newIntent = new Intent(SearchFromNameActivity.this, WeatherForecastActivity.class);
                newIntent.putExtra("lat", lat);
                newIntent.putExtra("lng", lng);
                startActivity(newIntent);
            }
        }
    }
}

