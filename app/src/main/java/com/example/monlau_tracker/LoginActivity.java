package com.example.monlau_tracker;

import static java.lang.Thread.sleep;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Comprobar si el usuario ya tiene la sesión iniciada, buscando sus credenciales en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        if (!email.isEmpty() && !password.isEmpty()){
            // Enviar al usuario automáticamente a la página de inicio, utilizando las credenciales guardadas
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            LoginActivity.this.startActivity(intent);
        }
        //else {
            // No hay credenciales almacenadas, el usuario debe iniciar sesión manualmente
        //}

        setContentView(R.layout.activity_login);

        // Configurar un Callback para que no se pueda volver atrás con el Botón de Retroceso
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };

        // Registrar el Callback con el Dispatcher del botón de retroceso
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Agregamos un Listener al botón
        Button btnLogIn = findViewById(R.id.buttonLogin);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Obtener las credenciales que ha introducido el usuario
                TextView inputUsuario = (TextView) findViewById(R.id.inputUsuario);
                TextView inputPassword = (TextView) findViewById(R.id.inputPassword);
                TextView textViewError = (TextView) findViewById(R.id.textViewError);
                String username = inputUsuario.getText().toString();
                String password = inputPassword.getText().toString();

                // Quitar el foco de ambos campos
                inputUsuario.clearFocus();
                inputPassword.clearFocus();

                if(inputUsuario.getText().toString().isEmpty()){
                    inputUsuario.setBackground(getResources().getDrawable(R.drawable.input_background_error));
                    inputUsuario.setHintTextColor(Color.parseColor("#BB0000"));
                    textViewError.setText("Debes completar todos los campos.");
                }
                else if(inputPassword.getText().toString().isEmpty()){
                    inputPassword.setBackground(getResources().getDrawable(R.drawable.input_background_error));
                    inputPassword.setHintTextColor(Color.parseColor("#BB0000"));
                    textViewError.setText("Debes completar todos los campos.");
                }
                else{
                    try {
                        // Consultar si las credenciales son válidas
                        ValidateUserTask validateUserTask = (ValidateUserTask) new ValidateUserTask(LoginActivity.this).execute(username, password);
                    }
                    catch (Exception e) {
                    }
                }




                /*
                // Cifrar la contraseña antes de almacenarla
                String encryptedPassword = null;
                try {
                    // Generar una clave secreta AES de 256 bits
                    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                    keyGenerator.init(256);
                    SecretKey secretKey = keyGenerator.generateKey();

                    // Convertir la clave secreta a bytes
                    byte[] secretKeyBytes = secretKey.getEncoded();

                    // Crear un objeto SecretKeySpec a partir de los bytes de la clave secreta
                    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");

                    // Inicializar un objeto Cipher para cifrar
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

                    // Cifrar la contraseña
                    byte[] encryptedPasswordBytes = cipher.doFinal(password.getBytes());

                    // Convertir el resultado cifrado a una cadena Base64 para almacenarlo en SharedPreferences
                    encryptedPassword = Base64.encodeToString(encryptedPasswordBytes, Base64.DEFAULT);
                } catch (Exception e) {
                }

                // Obtener la instancia de SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Guardar las credenciales del usuario
                editor.putString("username", username);
                editor.putString("password", encryptedPassword);
                //editor.putString("status", "out");

                // Aplicar los cambios
                editor.apply();

                try {
                    // Consultar el nombre completo asignado a este usuario y guardarlo en SharedPreferences
                    GetCompleteNameTask getCompleteNameTask = (GetCompleteNameTask) new GetCompleteNameTask(LoginActivity.this).execute(username);
                    getCompleteNameTask.get();

                    // Consultar si este usuario se encuentra en la escuela y guardarlo en SharedPreferences
                    GetCurrentStatusTask getCurrentStatusTask = (GetCurrentStatusTask) new GetCurrentStatusTask(LoginActivity.this).execute(username);
                    getCurrentStatusTask.get();
                } catch (Exception e) {
                }

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                LoginActivity.this.startActivity(intent);
                */

            }
        });

        TextView inputUsuario = (TextView) findViewById(R.id.inputUsuario);
        TextView inputPassword = (TextView) findViewById(R.id.inputPassword);

        // Agregamos un OnFocusChangeListener al campo del usuario
        inputUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    inputUsuario.setBackground(getResources().getDrawable(R.drawable.input_background));
                    inputUsuario.setHintTextColor(Color.parseColor("#FFFFFF"));
                    inputPassword.setBackground(getResources().getDrawable(R.drawable.input_background));
                    inputPassword.setHintTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        });

        // Agregamos un OnFocusChangeListener al campo de la contraseña
        inputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    inputUsuario.setBackground(getResources().getDrawable(R.drawable.input_background));
                    inputUsuario.setHintTextColor(Color.parseColor("#FFFFFF"));
                    inputPassword.setBackground(getResources().getDrawable(R.drawable.input_background));
                    inputPassword.setHintTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        });

        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Este método se llama antes de que el texto cambie
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Este método se llama cuando el texto cambia
                if(s.toString().length() >= 1){
                    inputPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye, 0);
                }
                else{
                    inputPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Este método se llama después de que el texto cambia
            }
        });

        inputPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (inputPassword.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                        if (event.getRawX() >= (inputPassword.getRight() - inputPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                            if (inputPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                inputPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye_crossed, 0);
                            }
                            else {
                                inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                inputPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye, 0);

                            }

                            return true;
                        }
                    }
                }
                return false;
            }
        });


    }

    public void validateResult(boolean correct) {
        if (correct) {

            // Localizar todos los elementos del Layout
            FrameLayout frameLayoutLoading = findViewById(R.id.frameLayoutLoading);
            ImageView imageViewLogoMonlau = findViewById(R.id.imageViewLogoMonlau);
            TextView title = (TextView) findViewById(R.id.title);
            TextView inputUsuario = (TextView) findViewById(R.id.inputUsuario);
            TextView inputPassword = (TextView) findViewById(R.id.inputPassword);
            TextView textViewError = (TextView) findViewById(R.id.textViewError);
            Button btnLogIn = findViewById(R.id.buttonLogin);

            // Esconder todos los elementos, excepto la barra circular que está cargando
            imageViewLogoMonlau.setVisibility(View.INVISIBLE);
            title.setVisibility(View.INVISIBLE);
            inputUsuario.setVisibility(View.INVISIBLE);
            inputPassword.setVisibility(View.INVISIBLE);
            textViewError.setVisibility(View.INVISIBLE);
            btnLogIn.setVisibility(View.INVISIBLE);
            frameLayoutLoading.setVisibility(View.VISIBLE);

            // Retrasar el cambio de actividad por un segundo
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Obtener las credenciales que ha introducido el usuario
                String username = inputUsuario.getText().toString();
                String password = inputPassword.getText().toString();

                // Cifrar la contraseña antes de almacenarla
                String encryptedPassword = null;
                try {
                    // Generar una clave secreta AES de 256 bits
                    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                    keyGenerator.init(256);
                    SecretKey secretKey = keyGenerator.generateKey();

                    // Convertir la clave secreta a bytes
                    byte[] secretKeyBytes = secretKey.getEncoded();

                    // Crear un objeto SecretKeySpec a partir de los bytes de la clave secreta
                    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");

                    // Inicializar un objeto Cipher para cifrar
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

                    // Cifrar la contraseña
                    byte[] encryptedPasswordBytes = cipher.doFinal(password.getBytes());

                    // Convertir el resultado cifrado a una cadena Base64 para almacenarlo en SharedPreferences
                    encryptedPassword = Base64.encodeToString(encryptedPasswordBytes, Base64.DEFAULT);
                }
                catch (Exception e) {
                }

                // Obtener la instancia de SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Guardar las credenciales del usuario
                editor.putString("username", username);
                editor.putString("password", encryptedPassword);

                // Aplicar los cambios
                editor.apply();

                try {
                    // Consultar el nombre completo asignado a este usuario y guardarlo en SharedPreferences
                    GetCompleteNameTask getCompleteNameTask = (GetCompleteNameTask) new GetCompleteNameTask(LoginActivity.this).execute(username);
                    getCompleteNameTask.get();

                    // Consultar si este usuario se encuentra en la escuela y guardarlo en SharedPreferences
                    GetCurrentStatusTask getCurrentStatusTask = (GetCurrentStatusTask) new GetCurrentStatusTask(LoginActivity.this).execute(username);
                    getCurrentStatusTask.get();
                }
                catch (Exception e) {
                }

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                LoginActivity.this.startActivity(intent);
            }, 1000);
        }
        else{
            TextView inputUsuario = (TextView) findViewById(R.id.inputUsuario);
            TextView inputPassword = (TextView) findViewById(R.id.inputPassword);
            TextView textViewError = (TextView) findViewById(R.id.textViewError);
            inputUsuario.setBackground(getResources().getDrawable(R.drawable.input_background_error));
            inputPassword.setBackground(getResources().getDrawable(R.drawable.input_background_error));
            textViewError.setText("Tus credenciales son incorrectas.");
        }
    }
}