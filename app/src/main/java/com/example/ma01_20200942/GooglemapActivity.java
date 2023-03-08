package com.example.ma01_20200942;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

import ddwu.mobile.place.placebasic.OnPlaceBasicResult;
import ddwu.mobile.place.placebasic.PlaceBasicManager;
import ddwu.mobile.place.placebasic.pojo.PlaceBasic;



public class GooglemapActivity extends AppCompatActivity {

    private GoogleMap mGoogleMap;
    private PlaceBasicManager placeBasicManager;
    private PlacesClient placesClient;
    double lat;
    double lng;
    Intent intent;
    Spinner txt_conv;
    String convWithLan[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);

        placeBasicManager = new PlaceBasicManager(getString(R.string.google_api_key));
        placeBasicManager.setOnPlaceBasicResult(onPlaceBasicResult);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);
        intent = getIntent();

        // Places 초기화 및 클라이언트 생성
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        placesClient = Places.createClient(this);
     }


    OnPlaceBasicResult onPlaceBasicResult = new OnPlaceBasicResult() {
        @Override
        public void onPlaceBasicResult(List<PlaceBasic> list) {
            for (PlaceBasic place : list) {
                MarkerOptions options = new MarkerOptions()
                        .title(place.getName())
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                Marker marker = mGoogleMap.addMarker(options);
                /*현재 장소의 place_id 를 각각의 마커에 보관*/
                marker.setTag(place.getPlaceId());
            }
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                placeBasicManager = new PlaceBasicManager(getString(R.string.google_api_key));
                searchStart(lat, lng, 200, PlaceTypes.CONVENIENCE_STORE);
                placeBasicManager.setOnPlaceBasicResult(onPlaceBasicResult);
                break;
        }
    }
    private void searchStart(double lat, double lng, int radius, String type) {
        placeBasicManager.searchPlaceBasic(lat, lng, radius, type);

    }

    final OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.setMyLocationEnabled(true);

            lat = intent.getDoubleExtra("lat", 0);
            lng = intent.getDoubleExtra("lng", 0);

            LatLng latLng = new LatLng(lat, lng);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(@NonNull LatLng latLng) {
                    double lat = latLng.latitude;
                    double lng = latLng.longitude;
                    intent = new Intent(GooglemapActivity.this, WeatherForecastActivity.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    startActivity(intent);
                }
            });
        }
    };
}