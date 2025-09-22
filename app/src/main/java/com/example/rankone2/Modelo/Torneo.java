package com.example.rankone2.Modelo;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Torneo {
    private int id;
    private String nombre;
    private String arteMarcial;
    private String pais;
    private String localidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer idOrganizador;
    private String ganador;
    private String estadoInscripcion;

    public Torneo() {
    }

    public Torneo(int id, String nombre, String arteMarcial, String pais, String localidad,
                  LocalDateTime fechaInicio, LocalDateTime fechaFin, Integer idOrganizador, String ganador) {
        this.id = id;
        this.nombre = nombre;
        this.arteMarcial = arteMarcial;
        this.pais = pais;
        this.localidad = localidad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.idOrganizador = idOrganizador;
        this.ganador = ganador;
        this.estadoInscripcion = "No inscrito";
    }

    public Torneo(int id, String nombre, String arteMarcial, String pais, String localidad, 
                 LocalDateTime fechaInicio, LocalDateTime fechaFin, Integer idOrganizador) {
        this.id = id;
        this.nombre = nombre;
        this.arteMarcial = arteMarcial;
        this.pais = pais;
        this.localidad = localidad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.idOrganizador = idOrganizador;
        this.ganador = ganador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArteMarcial() {
        return arteMarcial;
    }

    public void setArteMarcial(String arteMarcial) {
        this.arteMarcial = arteMarcial;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getIdOrganizador() {
        return idOrganizador;
    }

    public void setIdOrganizador(Integer idOrganizador) {
        this.idOrganizador = idOrganizador;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public String getEstadoInscripcion() {
        return estadoInscripcion;
    }

    public void setEstadoInscripcion(String estadoInscripcion) {
        this.estadoInscripcion = estadoInscripcion;
    }

    @Override
    public String toString() {
        return "Torneo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", arteMarcial='" + arteMarcial + '\'' +
                ", pais='" + pais + '\'' +
                ", localidad='" + localidad + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", idOrganizador=" + idOrganizador +
                ", ganador='" + ganador + '\'' +
                '}';
    }

    // Métodos para obtener fechas formateadas para mostrar en la tabla
    public String getFechaInicioFormateada() {
        if (fechaInicio != null) {
            DateTimeFormatter formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return fechaInicio.format(formatter);
            }
        }
        return "";
    }

    public String getFechaFinFormateada() {
        if (fechaFin != null) {
            DateTimeFormatter formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return fechaFin.format(formatter);
            }
        }
        return "";
    }

    // Método para obtener la cantidad de participantes restantes
    public int getParticipantesRestantes() {
        // TODO: Implementar lógica para contar participantes reales
        return 0; // Valor temporal
    }
}