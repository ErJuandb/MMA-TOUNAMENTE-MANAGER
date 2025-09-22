package com.example.rankone2.Modelo;

import java.time.LocalDate;

public class Combate {
    private int id;
    private int idCompeticion;
    private int idLuchador1;
    private int idLuchador2;
    private String nombreLuchador1;
    private String nombreLuchador2;
    private String resultado;
    private LocalDate fecha;
    private String fase;
    private int ganadorId;
    private String nombreGanador;

    public Combate() {
    }

    public Combate(int id, int idCompeticion, int idLuchador1, int idLuchador2, 
                   String resultado, LocalDate fecha, String fase, int ganadorId) {
        this.id = id;
        this.idCompeticion = idCompeticion;
        this.idLuchador1 = idLuchador1;
        this.idLuchador2 = idLuchador2;
        this.resultado = resultado;
        this.fecha = fecha;
        this.fase = fase;
        this.ganadorId = ganadorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCompeticion() {
        return idCompeticion;
    }

    public void setIdCompeticion(int idCompeticion) {
        this.idCompeticion = idCompeticion;
    }

    public int getIdLuchador1() {
        return idLuchador1;
    }

    public void setIdLuchador1(int idLuchador1) {
        this.idLuchador1 = idLuchador1;
    }

    public int getIdLuchador2() {
        return idLuchador2;
    }

    public void setIdLuchador2(int idLuchador2) {
        this.idLuchador2 = idLuchador2;
    }

    public String getNombreLuchador1() {
        return nombreLuchador1;
    }

    public void setNombreLuchador1(String nombreLuchador1) {
        this.nombreLuchador1 = nombreLuchador1;
    }

    public String getNombreLuchador2() {
        return nombreLuchador2;
    }

    public void setNombreLuchador2(String nombreLuchador2) {
        this.nombreLuchador2 = nombreLuchador2;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public int getGanadorId() {
        return ganadorId;
    }

    public void setGanadorId(int ganadorId) {
        this.ganadorId = ganadorId;
    }

    public String getNombreGanador() {
        return nombreGanador;
    }

    public void setNombreGanador(String nombreGanador) {
        this.nombreGanador = nombreGanador;
    }

    public String getLuchadoresVs() {
        return nombreLuchador1 + " vs " + nombreLuchador2;
    }

    public boolean esGanador(int userId) {
        return ganadorId == userId;
    }

    public boolean participaUsuario(int userId) {
        return idLuchador1 == userId || idLuchador2 == userId;
    }

    @Override
    public String toString() {
        return "Combate{" +
                "id=" + id +
                ", idCompeticion=" + idCompeticion +
                ", idLuchador1=" + idLuchador1 +
                ", idLuchador2=" + idLuchador2 +
                ", nombreLuchador1='" + nombreLuchador1 + '\'' +
                ", nombreLuchador2='" + nombreLuchador2 + '\'' +
                ", resultado='" + resultado + '\'' +
                ", fecha=" + fecha +
                ", fase='" + fase + '\'' +
                ", ganadorId=" + ganadorId +
                ", nombreGanador='" + nombreGanador + '\'' +
                '}';
    }
} 