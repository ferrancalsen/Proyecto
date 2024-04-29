package com.example.monlau_tracker;

public class Session {
    String nombre;
    String dia_semanal;
    String hora_inicio;
    String hora_final;

    public Session(String nombre, String dia_semanal, String hora_inicio, String hora_final) {
        this.nombre = nombre;
        this.dia_semanal = dia_semanal;
        this.hora_inicio = hora_inicio;
        this.hora_final = hora_final;
    }
}