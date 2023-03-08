package com.example.ma01_20200942;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final static int PERMISSION_REQ_CODE = 100;
    FusedLocationProviderClient flpClient;
    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;
    Location loc;
    Intent intent;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        checkPermission();
        flpClient.requestLocationUpdates(
                getLocationReqeust(),
                mLocCallback,
                Looper.getMainLooper()
        );

        createNotificationChannel();
        alarmBroadcastReceiver();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMap:
                intent = new Intent(this, GooglemapActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);
                break;
            case R.id.btnSearchFromName:
                intent = new Intent(this, SearchFromNameActivity.class);
                startActivity(intent);
                break;

            case R.id.btnStart:
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                    Toast.makeText(MainActivity.this, "현재 위치는 "+ addresses.get(0).getAdminArea() + addresses.get(0).getLocality()+ "입니다", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent = new Intent(this, WeatherForecastActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);

        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 획득", Toast.LENGTH_SHORT).show();
            }
            else
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {

            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    LocationCallback mLocCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            lat = locationResult.getLastLocation().getLatitude();
            lng = locationResult.getLastLocation().getLongitude();
            loc = locationResult.getLastLocation();
        }
    };

    private LocationRequest getLocationReqeust() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        return locationRequest;
    }

    public void alarmBroadcastReceiver() {
        Intent alarmBroadcastReceiverintent = new Intent(this, MyReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmBroadcastReceiverintent, PendingIntent.FLAG_MUTABLE);

        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to start at a particular time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),  AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    //    API26(Oreo)+ notification 작동을 위해서는 channel을 생성해야 함
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "날씨 정보";
            String description = "오늘의 날씨를 확인해 보세요";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("channel_id", name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
