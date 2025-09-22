package com.example.rankone2.Modelo;

public interface Constantes {

    // URL ficheros .php para el CRUD de Luchadores y Competiciones
    String  SERVIDOR = "http://10.0.2.2/webservice/Rankone_php/";

    String CREATE_LUCHADOR = SERVIDOR + "create_luchador.php";
    String READ_LUCHADOR = SERVIDOR + "read_luchador.php";
    String UPDATE_LUCHADOR = SERVIDOR + "update_luchador.php";
    String DELETE_LUCHADOR = SERVIDOR + "delete_luchador.php";
    String LISTALL_LUCHADORES = SERVIDOR + "list_all_luchador.php";
    String CREATE_COMPETICION = SERVIDOR + "create_competicion.php";
    String READ_COMPETICION = SERVIDOR + "read_competicion.php";
    String UPDATE_COMPETICION = SERVIDOR + "update_competicion.php";
    String DELETE_COMPETICION = SERVIDOR + "delete_competicion.php";
    String LISTALL_COMPETICIONES = SERVIDOR + "list_all_competicion.php";
    String INICIO_SESION = SERVIDOR + "inicio_sesion.php";
    String REGISTRO = SERVIDOR + "registro.php";
    String GET_LUCHADORES_DISPONIBLES = SERVIDOR +"get_luchadores_disponibles.php";
    String INSCRIBIR_LUCHADOR = SERVIDOR + "inscribir_luchador.php";
    String LISTALL_LUCHADORES_COMPETICION = SERVIDOR + "list_all_luchadores.php";
    String ELIMINAR_INSCRIPCION = SERVIDOR + "eliminar_inscripcion.php";
    public static final String CREATE_COMPETIDOR = SERVIDOR + "create_competidor.php";
    String VERIFICAR_LUCHADOR = SERVIDOR + "verificar_luchador.php";
    String INSCRIBIR_COMPETICION = SERVIDOR + "inscribir_competicion.php";

    String CARGAR_RESULTADOS = SERVIDOR + "get_resultados.php";
    String GET_COMBATES = SERVIDOR + "get_combates.php";
}
