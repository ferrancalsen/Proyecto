package com.example.monlau_tracker;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetClassroomMembershipsTask extends AsyncTask<String, Void, String> {

    private Context context;
    private Exception exception;
    public String[] aulas;
    private Object lock = new Object();

    public GetClassroomMembershipsTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String username = params[0];
        String url = "http://172.17.127.254:8200/webservice/rest/get_classroom_memberships_by_username.php";
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
            // Guardamos las aulas a las que pertenece este alumno en un Array
            aulas = resultado.split(",");
            synchronized (lock) {
                lock.notify();
            }
        }

    }

    public String[] waitForResult() {
        synchronized (lock) {
            try {
                lock.wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return aulas;
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