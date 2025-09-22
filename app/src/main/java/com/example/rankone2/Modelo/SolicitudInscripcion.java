package com.example.rankone2.Modelo;

import java.sql.Date;
import java.time.LocalDateTime;

public class SolicitudInscripcion {
    private final int idSolicitud;
    private final int idLuchador;
    private final int idCompeticion;
    private final String nombreCompleto;
    private final String nacionalidad;
    private final String arteMarcial;
    private final String grado;
    private final Date fechaNacimiento;
    private final String sexo;
    private String estado;
    private final LocalDateTime fechaSolicitud;

    public SolicitudInscripcion(int idSolicitud, int idLuchador, int idCompeticion,
                               String nombreCompleto, String nacionalidad, String arteMarcial,
                               String grado, Date fechaNacimiento, String sexo,
                               String estado, LocalDateTime fechaSolicitud) {
        this.idSolicitud = idSolicitud;
        this.idLuchador = idLuchador;
        this.idCompeticion = idCompeticion;
        this.nombreCompleto = nombreCompleto;
        this.nacionalidad = nacionalidad;
        this.arteMarcial = arteMarcial;
        this.grado = grado;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
    }

    // Getters
    public int getIdSolicitud() { return idSolicitud; }
    public int getIdLuchador() { return idLuchador; }
    public int getIdCompeticion() { return idCompeticion; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getNacionalidad() { return nacionalidad; }
    public String getArteMarcial() { return arteMarcial; }
    public String getGrado() { return grado; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public String getSexo() { return sexo; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }

    // Setter para estado
    public void setEstado(String estado) { this.estado = estado; }
}
