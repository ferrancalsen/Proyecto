package com.example.monlau_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        Intent intent = new Intent(MapActivity.this, HomeActivity.class);
        MapActivity.this.startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double DESTINATION_LATITUDE = 41.42311729609058;
        double DESTINATION_LONGITUDE = 2.190582451348262;

        // Agregar un marcador en la ubicación de destino
        LatLng destinationLocation = new LatLng(DESTINATION_LATITUDE, DESTINATION_LONGITUDE);
        mMap.addMarker(new MarkerOptions().position(destinationLocation).title("Destino"));

        // Mover la cámara al marcador de destino
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 15));

        // Mostrar indicaciones de navegación desde la ubicación actual del usuario hasta el destino
        String directionURL = "https://www.google.com/maps/dir/?api=1&destination="
                + DESTINATION_LATITUDE + "," + DESTINATION_LONGITUDE;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(directionURL));
        startActivity(intent);
    }
}