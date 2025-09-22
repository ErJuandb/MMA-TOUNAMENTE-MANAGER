package com.example.rankone2.Modelo;

public class UsuarioConcreto extends Usuario {
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private String foto;
    private String rol;
    private boolean activo;

    public UsuarioConcreto() {
        super();
        this.nombre = "";
        this.apellidos = "";
        this.email = "";
        this.password = "";
        this.foto = "";
        this.rol = "";
        this.activo = true;
    }

    public UsuarioConcreto(int id, String nombre, String apellidos, String email, String password, String foto, String rol) {
        super(id);
        this.nombre = nombre != null ? nombre : "";
        this.apellidos = apellidos != null ? apellidos : "";
        this.email = email != null ? email : "";
        this.password = password != null ? password : "";
        this.foto = foto != null ? foto : "";
        this.rol = rol != null ? rol : "";
        this.activo = true;
    }

    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "");
    }

    public String getNombre() {
        return nombre != null ? nombre : "";
    }

    public void setNombre(String nombre) {
        this.nombre = nombre != null ? nombre : "";
    }

    public String getApellidos() {
        return apellidos != null ? apellidos : "";
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos != null ? apellidos : "";
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email != null ? email : "";
    }

    public String getPassword() {
        return password != null ? password : "";
    }

    public void setPassword(String password) {
        this.password = password != null ? password : "";
    }

    public String getFoto() {
        return foto != null ? foto : "";
    }

    public void setFoto(String foto) {
        this.foto = foto != null ? foto : "";
    }

    public String getRol() {
        return rol != null ? rol : "";
    }

    public void setRol(String rol) {
        this.rol = rol != null ? rol : "";
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "UsuarioConcreto{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", apellidos='" + getApellidos() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", rol='" + getRol() + '\'' +
                ", activo=" + activo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioConcreto that = (UsuarioConcreto) o;
        return getId() == that.getId() &&
                activo == that.activo &&
                getNombre().equals(that.getNombre()) &&
                getApellidos().equals(that.getApellidos()) &&
                getEmail().equals(that.getEmail()) &&
                getRol().equals(that.getRol());
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getNombre().hashCode();
        result = 31 * result + getApellidos().hashCode();
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + getRol().hashCode();
        result = 31 * result + (activo ? 1 : 0);
        return result;
    }
} 