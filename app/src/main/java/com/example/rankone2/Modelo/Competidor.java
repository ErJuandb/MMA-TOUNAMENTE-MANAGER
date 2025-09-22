package com.example.rankone2.Modelo;

import java.time.LocalDate;

public class Competidor extends Usuario {
    private String nacionalidad;
    private String dni;
    private LocalDate fechaNacimiento;
    private String arteMarcial;
    private String grado;
    private String sexo;

    public Competidor() {
    }

    public Competidor(String nacionalidad, String dni, LocalDate fechaNacimiento, 
                     String arteMarcial, String grado, String sexo) {
        this.nacionalidad = nacionalidad;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.arteMarcial = arteMarcial;
        this.grado = grado;
        this.sexo = sexo;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getArteMarcial() {
        return arteMarcial;
    }

    public void setArteMarcial(String arteMarcial) {
        this.arteMarcial = arteMarcial;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    @Override
    public String toString() {
        return "Competidor{" +
                super.toString() + '\'' +
                ", nacionalidad='" + nacionalidad + '\'' +
                ", dni='" + dni + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", arteMarcial='" + arteMarcial + '\'' +
                ", grado='" + grado + '\'' +
                ", sexo='" + sexo + '\'' +
                '}';
    }
}
