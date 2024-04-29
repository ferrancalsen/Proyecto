package com.example.monlau_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Obtener el nombre del usuario de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        try {
            // Consultar el calendario semanal de este usuario
            GetScheduleTask getScheduleTask = (GetScheduleTask) new GetScheduleTask(ScheduleActivity.this).execute(username);
        }
        catch (Exception e) {
        }

    }

    public void showSessionsOnScreen(ArrayList<Session> arrayOfSessions) {

        //Toast.makeText(ScheduleActivity.this, String.valueOf(arrayOfSessions.size()), Toast.LENGTH_LONG).show();
        //Toast.makeText(ScheduleActivity.this, arrayOfSessions.get(0).nombre + " " + arrayOfSessions.get(0).dia_semanal + " " + arrayOfSessions.get(0).hora_inicio + " " + arrayOfSessions.get(0).hora_final, Toast.LENGTH_LONG).show();

        //for(Session session : arrayOfSessions){
        //    System.out.println();
        //}

        // Contamos los valores únicos de "hora_inicio", ya que representará la cantidad de filas de la tabla
        HashSet<String> uniqueStartTimes = new HashSet<>();
        for (Session session : arrayOfSessions) {
            uniqueStartTimes.add(session.hora_inicio);
        }
        int distinctStartTimesCount = uniqueStartTimes.size();

        // Creamos la tabla dinámicamente
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackground(getResources().getDrawable(R.drawable.background_half_edited));
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

        TableLayout table = new TableLayout(this);
        table.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i <= distinctStartTimesCount; i++) {

            TableRow row = new TableRow(this);
            row.setWeightSum(2);
            row.setPadding(1, 50, 1, 1);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1);

            for (int j = 0; j < 6; j++) {

                TableRow.LayoutParams textViewParams = new TableRow.LayoutParams();
                TextView cell = new TextView(this);

                if(i==0){
                    switch (j){
                        case 0:
                            cell.setText("");
                            break;
                        case 1:
                            cell.setText("LUNES");
                            break;
                        case 2:
                            cell.setText("MARTES");
                            break;
                        case 3:
                            cell.setText("MIÉRCOLES");
                            break;
                        case 4:
                            cell.setText("JUEVES");
                            break;
                        case 5:
                            cell.setText("VIERNES");
                            break;
                    }
                    cell.setTypeface(null, Typeface.BOLD);
                }

                else{
                    if(j==0){
                        cell.setText("HORA");
                        cell.setTypeface(null, Typeface.BOLD);
                    }
                    else{
                        cell.setText("cell [" + i + ", " + j + "]");
                    }

                }

                cell.setPadding(6, 4, 6, 4);
                cell.setGravity(Gravity.CENTER);
                cell.setLayoutParams(textViewParams);
                row.addView(cell);
            }

            row.setLayoutParams(params);
            table.addView(row, params);
        }

        table.setStretchAllColumns(true);
        linearLayout.addView(table);
        setContentView(linearLayout);


    }
}