package com.example.monlau_tracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class AccessDeniedActivity extends AppCompatActivity {

    private static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_denied);

        // Crea un nuevo MediaPlayer y carga el sonido de validación
        mediaPlayer = MediaPlayer.create(this, R.raw.access_denied);

        // Configura un OnCompletionListener para liberar los recursos cuando el audio haya terminado
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopValidationSound();
                Intent intent = new Intent(AccessDeniedActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // Reproduce el sonido
        mediaPlayer.start();
    }

    private void stopValidationSound() {
        // Detiene la reproducción si hay un sonido en reproducción
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}