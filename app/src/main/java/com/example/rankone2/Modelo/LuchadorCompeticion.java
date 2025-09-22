package com.example.rankone2.Modelo;

public class LuchadorCompeticion {
    private int idLuchador;
    private int idCompeticion;

    public LuchadorCompeticion() {
    }

    public LuchadorCompeticion(int idLuchador, int idCompeticion) {
        this.idLuchador = idLuchador;
        this.idCompeticion = idCompeticion;
    }

    public int getIdLuchador() {
        return idLuchador;
    }

    public void setIdLuchador(int idLuchador) {
        this.idLuchador = idLuchador;
    }

    public int getIdCompeticion() {
        return idCompeticion;
    }

    public void setIdCompeticion(int idCompeticion) {
        this.idCompeticion = idCompeticion;
    }

    @Override
    public String toString() {
        return "LuchadorCompeticion{" +
                "idLuchador=" + idLuchador +
                ", idCompeticion=" + idCompeticion +
                '}';
    }
} 