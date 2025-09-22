package com.example.rankone2.Modelo;

public class Organizador extends Usuario {
    private String cif;
    private String federacion;

    public Organizador() {
    }

    public Organizador(String cif, String federacion) {
        this.cif = cif;
        this.federacion = federacion;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getFederacion() {
        return federacion;
    }

    public void setFederacion(String federacion) {
        this.federacion = federacion;
    }

    @Override
    public String toString() {
        return "Organizador{" +
                super.toString() + '\'' +
                "cif='" + cif + '\'' +
                ", federacion='" + federacion + '\'' +
                '}';
    }
}
