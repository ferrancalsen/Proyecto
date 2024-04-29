package com.example.monlau_tracker;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefRecord;
import android.nfc.TagLostException;
import android.os.Build;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import pl.droidsonroids.gif.GifImageView;


public class CardActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // Inicializa el adaptador NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Rest of Activity setup
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mNfcAdapter!= null) {
            Bundle options = new Bundle();
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

            // Enable ReaderMode for all types of card and disable platform sounds
            mNfcAdapter.enableReaderMode(this,
                    this,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                    options);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter!= null)
            mNfcAdapter.disableReaderMode(this);
    }

    @Override
    public void onTagDiscovered(Tag tag) {

        // Vibra por 500 milisegundos cuando se detecta una etiqueta NFC
        vibrate(250);

        // Este método se llama cuando se descubre una etiqueta NFC
        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            try {
                ndef.connect();
                NdefMessage ndefMessage = ndef.getNdefMessage();
                if (ndefMessage != null) {
                    // Obtiene el mensaje desde la llave NFC
                    String message = new String(ndefMessage.getRecords()[0].getPayload());

                    // Muestra el mensaje en un Toast
                    //showToastOnUiThread("Mensaje NFC: " + message);

                    try {
                        // Obtener el nombre del usuario de SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                        String username = sharedPreferences.getString("username", "");

                        // Consultar las aulas a las que pertenece este alumno
                        GetClassroomMembershipsTask getClassroomMembershipsTask = (GetClassroomMembershipsTask) new GetClassroomMembershipsTask(CardActivity.this).execute(username);
                        //getClassroomMembershipsTask.get();
                        //sleep(500);
                        String[] aulas = getClassroomMembershipsTask.waitForResult();

                        // Consultar si el alumno pertenece a la clase leída en el NFC
                        boolean encontrado = false;
                        for (String aula : aulas) {
                            if (aula.equals(message.substring(1, message.length()))) {
                                encontrado = true;
                                break;
                            }
                        }
                        if (encontrado) {
                            showToastOnUiThread(Calendar.getInstance().getTime().toString());
                            runOnUiThread(() -> {
                                ConstraintLayout backgroundLayout = findViewById(R.id.backgroundLayout);
                                ImageView imageViewInfo = findViewById(R.id.imageViewInfo);
                                backgroundLayout.setBackgroundColor(Color.parseColor("#139447"));  // VERDE = #139447  y  ROJO = #BB0000
                                imageViewInfo.setVisibility(View.VISIBLE);
                                imageViewInfo.setImageResource(R.drawable.green_tick);
                                GifImageView gifApproach = findViewById(R.id.gifApproach);
                                gifApproach.setVisibility(View.INVISIBLE);
                            });

                            // Retrasa el cambio de actividad por un instante para permitir que se complete el proceso NFC
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                // Cambia a la nueva actividad después de 1 segundo
                                Intent intent = new Intent(this, HomeActivity.class);
                                startActivity(intent);
                                // Finaliza la actividad actual si es necesario
                                finish();
                            }, 1500);

                            // Añadir un nuevo registro de acceso por NFC
                            CreateNewAccessLogTask createNewAccessLogTask = (CreateNewAccessLogTask) new CreateNewAccessLogTask(CardActivity.this).execute(username);
                            createNewAccessLogTask.get();

                            // Consultar si este usuario se encuentra en la escuela y guardarlo en SharedPreferences
                            GetCurrentStatusTask getCurrentStatusTask = (GetCurrentStatusTask) new GetCurrentStatusTask(CardActivity.this).execute(username);
                            getCurrentStatusTask.get();
                        }
                        else {
                            runOnUiThread(() -> {
                                ConstraintLayout backgroundLayout = findViewById(R.id.backgroundLayout);
                                ImageView imageViewInfo = findViewById(R.id.imageViewInfo);
                                backgroundLayout.setBackgroundColor(Color.parseColor("#BB0000"));  // VERDE = #139447  y  ROJO = #BB0000
                                imageViewInfo.setVisibility(View.VISIBLE);
                                imageViewInfo.setImageResource(R.drawable.red_cross);
                                GifImageView gifApproach = findViewById(R.id.gifApproach);
                                gifApproach.setVisibility(View.INVISIBLE);
                            });

                            // Retrasa el cambio de actividad por medio segundo para permitir que se complete el proceso NFC
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                // Cambia a la nueva actividad después de 1 segundo
                                Intent intent = new Intent(this, HomeActivity.class);
                                startActivity(intent);
                                // Finaliza la actividad actual si es necesario
                                finish();
                            }, 1500);
                        }

                    }
                    catch (Exception e) {
                    }



                    /*if (message.substring(1, message.length()).equals("2DAM")) {

                        runOnUiThread(() -> {
                            ConstraintLayout backgroundLayout = findViewById(R.id.backgroundLayout);
                            ImageView imageViewInfo = findViewById(R.id.imageViewInfo);
                            backgroundLayout.setBackgroundColor(Color.parseColor("#139447"));  // VERDE = #139447  y  ROJO = #BB0000
                            imageViewInfo.setVisibility(View.VISIBLE);
                            imageViewInfo.setImageResource(R.drawable.green_tick);
                            GifImageView gifApproach = findViewById(R.id.gifApproach);
                            gifApproach.setVisibility(View.INVISIBLE);
                        });

                        //showToastOnUiThread("HAS ENTRADO A 2 DAM");

                        // Retrasa el cambio de actividad por medio segundo para permitir que se complete el proceso NFC
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            // Cambia a la nueva actividad después de 1 segundo
                            Intent intent = new Intent(this, HomeActivity.class);
                            startActivity(intent);
                            // Finaliza la actividad actual si es necesario
                            finish();
                        }, 1250);

                        // Obtener la instancia de SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String status = sharedPreferences.getString("status", "");

                        if(status.equals("out")){
                            editor.putString("status", "in");
                        }
                        else if(status.equals("in")){
                            editor.putString("status", "out");
                        }

                        // Aplicar los cambios
                        editor.apply();
                    }*/
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    ndef.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
        else{
            showToastOnUiThread("ES UN MOVIL");

            runOnUiThread(() -> {
                ConstraintLayout backgroundLayout = findViewById(R.id.backgroundLayout);
                ImageView imageViewInfo = findViewById(R.id.imageViewInfo);
                backgroundLayout.setBackgroundColor(Color.parseColor("#BB0000"));  // VERDE = #139447  y  ROJO = #BB0000
                imageViewInfo.setVisibility(View.VISIBLE);
                imageViewInfo.setImageResource(R.drawable.red_cross);
                GifImageView gifApproach = findViewById(R.id.gifApproach);
                gifApproach.setVisibility(View.INVISIBLE);
            });

            // Retrasa el cambio de actividad por medio segundo para permitir que se complete el proceso NFC
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Cambia a la nueva actividad después de 1 segundo
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                // Finaliza la actividad actual si es necesario
                finish();
            }, 1500);
        }




        /*
        // Read and or write to Tag here to the appropriate Tag Technology type class
        // in this example the card should be an Ndef Technology Type
        Ndef mNdef = Ndef.get(tag);

        // Check that it is an Ndef capable card
        if (mNdef != null) {
            // Creamos un mensaje NDEF con un solo registro de tipo texto
            NdefRecord mRecord = createTextRecord("2DAM");
            NdefMessage mMsg = new NdefMessage(new NdefRecord[]{mRecord});

            // Catch errors
            try {
                mNdef.connect();
                mNdef.writeNdefMessage(mMsg);

                // Success if got to here
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(),
                            "Write to NFC Success: 2DAM",
                            Toast.LENGTH_SHORT).show();
                });

                // Make a Sound
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                            notification);
                    r.play();
                } catch (Exception e) {
                    // Some error playing sound
                }

            } catch (FormatException e) {
                // if the NDEF Message to write is malformed
            } catch (TagLostException e) {
                // Tag went out of range before operations were complete
            } catch (IOException e) {
                // if there is an I/O failure, or the operation is cancelled
            } catch (SecurityException e) {
                // The SecurityException is only for Android 12L and above
                // The Tag object might have gone stale by the time
                // the code gets to process it, with a new one been
                // delivered (for the same or different Tag)
                // The SecurityException is thrown if you are working on
                // a stale Tag
            } finally {
                // Be nice and try and close the tag to
                // Disable I/O operations to the tag from this TagTechnology object, and release resources.
                try {
                    mNdef.close();
                } catch (IOException e) {
                    // if there is an I/O failure, or the operation is cancelled
                }
            }
        }*/
    }

    // Método para crear un registro de tipo texto con el contenido dado
    private NdefRecord createTextRecord(String text) {
        byte[] textBytes;
        try {
            textBytes = text.getBytes("UTF-8");
            byte[] payload = new byte[textBytes.length + 1];
            // Copia el texto en sí al payload
            System.arraycopy(textBytes, 0, payload, 1, textBytes.length);
            // Crea y devuelve el registro de texto
            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showToastOnUiThread(final String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }

    // Método para vibrar el dispositivo
    private void vibrate(long milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            // Verifica si el dispositivo soporta la vibración
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Si la API es más antigua, usa el método deprecated
                vibrator.vibrate(milliseconds);
            }
        }
    }
}