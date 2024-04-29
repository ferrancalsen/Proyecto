package com.example.monlau_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_GEOLOCALIZACION = 1;
    private static final int REQUEST_NOTIFICATIONS = 2;

    private static String[] PERMISSIONS_GEOLOCALIZACION = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static String[] PERMISSIONS_NOTIFICATIONS = {
            Manifest.permission.POST_NOTIFICATIONS
    };

    public Context settingsContext;
    public SwitchCompat switchGeolocalizacion, switchNotificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsContext = this;

        // Agregamos un Listener al SwitchCompat de la geolocalización
        switchGeolocalizacion = findViewById(R.id.switchGeolocalizacion);
        switchGeolocalizacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Acción cuando se activa el Switch
                    Toast.makeText(getApplicationContext(), "Switch activado", Toast.LENGTH_SHORT).show();

                    // Solicitar los permisos de geolocalización al usuario
                    if (ActivityCompat.checkSelfPermission(settingsContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(settingsContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SettingsActivity.this, PERMISSIONS_GEOLOCALIZACION, REQUEST_GEOLOCALIZACION);
                    }
                    else{
                        // Activar el servicio de geolocalización
                    }

                } else {
                    // Acción cuando se desactiva el switch
                    Toast.makeText(getApplicationContext(), "Switch desactivado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Agregamos un Listener al SwitchCompat de las notificaciones
        switchNotificaciones = findViewById(R.id.switchNotificaciones);
        switchNotificaciones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Acción cuando se activa el Switch
                    Toast.makeText(getApplicationContext(), "Switch activado", Toast.LENGTH_SHORT).show();

                    // Solicitar los permisos de notificaciones al usuario
                    if (ActivityCompat.checkSelfPermission(settingsContext, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SettingsActivity.this, PERMISSIONS_NOTIFICATIONS, REQUEST_NOTIFICATIONS);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Ya estaban activadas las notificaciones.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Acción cuando se desactiva el switch
                    Toast.makeText(getApplicationContext(), "Switch desactivado", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_GEOLOCALIZACION) {
            // Verificar si se han concedido los permisos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Los permisos fueron concedidos y podemos continuar con la lógica de la aplicación

                Toast.makeText(this, "Se aceptaron los permisos de GEOLOCALIZACION.", Toast.LENGTH_LONG).show();

                // Activar el servicio de geolocalización
            }
            else {
                // Desactivamos el SwitchCompat, ya que no se han aceptado los permisos de geolocalización
                switchGeolocalizacion.setChecked(false);
            }
        }
        if (requestCode == REQUEST_NOTIFICATIONS) {
            // Verificar si se han concedido los permisos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Los permisos fueron concedidos y podemos continuar con la lógica de la aplicación
                Toast.makeText(this, "Se aceptaron los permisos de NOTIFICACIONES.", Toast.LENGTH_LONG).show();
            }
            else {
                // Desactivamos el SwitchCompat, ya que no se han aceptado los permisos de notificaciones
                switchNotificaciones.setChecked(false);
            }
        }
    }
}