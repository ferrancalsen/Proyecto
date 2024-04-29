package com.example.monlau_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GetCompleteNameTask extends AsyncTask<String, Void, String> {

    private Context context;
    private Exception exception;

    public GetCompleteNameTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String username = params[0];
        String url = "http://172.17.127.254:8200/webservice/rest/get_complete_name_by_username.php";
        String resultado = null;

        try {
            // Creamos la conexión HTTP
            URL direccion = new URL(url);
            HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setDoOutput(true);
            conexion.setConnectTimeout(100);

            // Creamos los datos del formulario
            String datos = "username=" + username;

            // Escribimos los datos del formulario en la solicitud HTTP
            OutputStream salida = conexion.getOutputStream();
            byte[] bytes = datos.getBytes(StandardCharsets.UTF_8);
            salida.write(bytes);
            salida.flush();
            salida.close();

            // Leemos la respuesta del servidor
            InputStream entrada = conexion.getInputStream();
            BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
            StringBuilder respuesta = new StringBuilder();
            String linea;

            while ((linea = lector.readLine()) != null) {
                respuesta.append(linea);
            }

            // Cerramos la conexión HTTP
            entrada.close();
            conexion.disconnect();

            // Procesamos la respuesta del servidor
            resultado = respuesta.toString();
        }
        catch (Exception e) {
            // Capturamos la excepción
            exception = e;
        }
        return resultado;
    }

    @Override
    protected void onPostExecute(String resultado) {
        super.onPostExecute(resultado);
        // Comprobamos si hay una excepción y mostramos un Toast en el hilo principal
        if (exception != null) {
            mostrarError(exception);
        }
        else{
            // Guardar el resultado obtenido en SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("completeName", resultado);
            editor.apply();
        }

    }

    private void mostrarError(final Exception e) {
        // Utilizamos un Handler para mostrar el Toast en el hilo principal
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // Mostramos el Toast con el mensaje de error
                Toast.makeText(context, "No se ha podido establecer conexión con la base de datos.", Toast.LENGTH_LONG).show();
            }
        });
    }
}