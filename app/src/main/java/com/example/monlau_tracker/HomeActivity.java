package com.example.monlau_tracker;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class HomeActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String password = sharedPreferences.getString("password", "");
        String encryptedPassword = sharedPreferences.getString("password", "");
        String secretKeyString = sharedPreferences.getString("key", "");

        // Convertir la cadena Base64 de vuelta a bytes
        byte[] secretKeyBytes = Base64.decode(secretKeyString, Base64.DEFAULT);

        // Crear un objeto SecretKeySpec a partir de los bytes
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");

        String decryptedPassword = null;
        try {
            // Convertir la cadena Base64 cifrada de vuelta a bytes
            byte[] encryptedPasswordBytes = Base64.decode(encryptedPassword, Base64.DEFAULT);

            // Inicializar un objeto Cipher para descifrar
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            // Descifrar la contraseña
            byte[] decryptedPasswordBytes = cipher.doFinal(encryptedPasswordBytes);

            // Convertir los bytes descifrados de vuelta a una cadena de texto
            decryptedPassword = new String(decryptedPasswordBytes);
        }
        catch (Exception e) {
        }


        TextView textPrueba = (TextView) findViewById(R.id.textPrueba);
        textPrueba.setText(decryptedPassword + " / " + password);*/

        // Configurar un Callback para que no se pueda volver atrás con el Botón de Retroceso
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };

        // Registrar el Callback con el Dispatcher del botón de retroceso
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Mostrar el nombre completo de este usuario
        SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String completeName = sharedPreferences.getString("completeName", "");
        TextView textViewNombre = (TextView) findViewById(R.id.textViewNombre);
        textViewNombre.setText(completeName.toUpperCase());

        // Comprobar si el alumno se encuentra en la escuela para mostrarlo en la cabecera
        String status = sharedPreferences.getString("status", "");
        ImageView imageViewEstado = findViewById(R.id.imageViewEstado);
        TextView textViewEstado = findViewById(R.id.textViewEstado);
        if(status.equals("out")){
            imageViewEstado.setImageResource(R.drawable.red_circle);
            textViewEstado.setText("Ausente");
        }
        else if(status.equals("in")){
            imageViewEstado.setImageResource(R.drawable.green_circle);
            textViewEstado.setText("Presente");
        }

        // Agregamos un Listener al botón "Lector"
        CardView tarjeta = findViewById(R.id.tarjeta);
        tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CardActivity.class);
                HomeActivity.this.startActivity(intent);
            }
        });

        // Agregamos un Listener al botón "Mapa"
        CardView mapa = findViewById(R.id.mapa);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                HomeActivity.this.startActivity(intent);
            }
        });

        // Agregamos un Listener al botón "Horario"
        CardView horario = findViewById(R.id.horario);
        horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ScheduleActivity.class);
                HomeActivity.this.startActivity(intent);
            }
        });

        // Agregamos un Listener al botón "Historial"
        CardView historial = findViewById(R.id.historial);
        historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RecordActivity.class);
                HomeActivity.this.startActivity(intent);
            }
        });

        // Agregamos un Listener al botón "Ajustes"
        CardView ajustes = findViewById(R.id.ajustes);
        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                HomeActivity.this.startActivity(intent);
            }
        });

        // Agregamos un Listener al botón "Salir"
        CardView salir = findViewById(R.id.salir);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion();
            }
        });
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CERRAR SESIÓN");
        builder.setMessage("Estás a punto de cerrar la sesión actual.\n¿Deseas continuar?");
        builder.setNegativeButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Aquí puedes ejecutar la acción para cerrar la sesión
                cerrarSesion();
            }
        });
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada si el usuario elige no cerrar la sesión
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cerrarSesion() {

        // Obtener la instancia de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        // Obtener el editor de SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Borrar los datos sensibles
        editor.remove("username");
        editor.remove("password");
        editor.remove("completeName");
        editor.remove("status");
        //editor.remove("key");

        // Aplicar los cambios
        editor.apply();

        // Redirigir al usuario a la pantalla de inicio de sesión
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        HomeActivity.this.startActivity(intent);
    }
}