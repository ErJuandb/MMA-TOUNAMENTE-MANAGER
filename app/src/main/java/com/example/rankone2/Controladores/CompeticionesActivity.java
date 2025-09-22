package com.example.rankone2.Controladores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rankone2.Adaptadores.TorneoAdapter;
import com.example.rankone2.Modelo.Constantes;
import com.example.rankone2.Modelo.Torneo;
import com.example.rankone2.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CompeticionesActivity extends AppCompatActivity implements TorneoAdapter.OnTorneoClickListener {

    private RecyclerView recyclerView;
    private TorneoAdapter adapter;
    private List<Torneo> listaTorneos;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competiciones);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaTorneos = new ArrayList<>();
        adapter = new TorneoAdapter(listaTorneos, this);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        loadCompeticiones();
    }

    private void loadCompeticiones() {
        // Obtener ID del usuario actual para filtrar inscripciones
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        
        String url = Constantes.LISTALL_COMPETICIONES;
        if (userId > 0) {
            url += "?userId=" + userId;
        }
        Log.d("Competiciones", "Iniciando carga desde URL: " + url);
        
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    Log.d("Competiciones", "Respuesta recibida: " + response.toString());
                    boolean status = response.getBoolean("status");
                    String message = response.getString("message");
                    
                    if (status) {
                        JSONArray competicionesArray = response.getJSONArray("data");
                        Log.d("Competiciones", "Número de competiciones: " + competicionesArray.length());
                        listaTorneos.clear();
                        
                        for (int i = 0; i < competicionesArray.length(); i++) {
                            try {
                                JSONObject competicion = competicionesArray.getJSONObject(i);
                                Log.d("Competiciones", "Procesando competición " + i + ": " + competicion.toString());
                                
                                // Validar campos requeridos
                                if (!competicion.has("id") || !competicion.has("nombre")) {
                                    Log.e("Competiciones", "Campos requeridos faltantes en competición " + i);
                                    continue;
                                }
                                
                                int id = competicion.getInt("id");
                                String nombre = competicion.getString("nombre");
                                
                                // Campos opcionales con valores por defecto
                                String arteMarcial = competicion.optString("arteMarcial", "No especificado");
                                String pais = competicion.optString("pais", "No especificado");
                                String localidad = competicion.optString("localidad", "No especificado");
                                String fechaInicioStr = competicion.optString("fechaInicio", "");
                                String fechaFinStr = competicion.optString("fechaFin", "");
                                String ganador = competicion.optString("ganador", "No hay ganador");
                                String estadoInscripcion = competicion.optString("estadoInscripcion", "No inscrito");
                                
                                // Manejar idOrganizador
                                Integer idOrganizador = null;
                                if (competicion.has("idOrganizador") && !competicion.isNull("idOrganizador")) {
                                    idOrganizador = competicion.getInt("idOrganizador");
                                }
                                
                                // Manejar fechas
                                LocalDateTime fechaInicio = null;
                                LocalDateTime fechaFin = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    if (!fechaInicioStr.isEmpty()) {
                                        try {
                                            fechaInicio = LocalDateTime.parse(fechaInicioStr, formatter);
                                        } catch (Exception e) {
                                            Log.e("Competiciones", "Error al parsear fechaInicio: " + fechaInicioStr);
                                        }
                                    }
                                    if (!fechaFinStr.isEmpty()) {
                                        try {
                                            fechaFin = LocalDateTime.parse(fechaFinStr, formatter);
                                        } catch (Exception e) {
                                            Log.e("Competiciones", "Error al parsear fechaFin: " + fechaFinStr);
                                        }
                                    }
                                }
                                
                                // Crear objeto Torneo
                                Torneo torneo = new Torneo(id, nombre, arteMarcial, pais, localidad,
                                    fechaInicio, fechaFin, idOrganizador, ganador);
                                torneo.setEstadoInscripcion(estadoInscripcion);

                                // Verificar que el objeto se creó correctamente
                                Log.d("Competiciones", "Torneo creado: " + torneo.toString());
                                
                                listaTorneos.add(torneo);
                            } catch (JSONException e) {
                                Log.e("Competiciones", "Error al procesar competición " + i + ": " + e.getMessage());
                                continue;
                            }
                        }
                        
                        Log.d("Competiciones", "Total de torneos cargados: " + listaTorneos.size());
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Competiciones", "Error en respuesta: " + message);
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                    
                } catch (JSONException e) {
                    Log.e("Competiciones", "Error al procesar respuesta: " + e.getMessage());
                    Log.e("Competiciones", "Respuesta completa: " + response.toString());
                    Toast.makeText(this, "Error al procesar los datos: " + e.getMessage(), 
                                 Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                String errorMessage;
                if (error instanceof TimeoutError) {
                    errorMessage = "Error de timeout. Por favor, verifica tu conexión.";
                } else if (error instanceof NoConnectionError) {
                    errorMessage = "No hay conexión a internet.";
                } else if (error instanceof AuthFailureError) {
                    errorMessage = "Error de autenticación.";
                } else if (error instanceof ServerError) {
                    errorMessage = "Error del servidor.";
                } else if (error instanceof NetworkError) {
                    errorMessage = "Error de red.";
                } else if (error instanceof ParseError) {
                    errorMessage = "Error al procesar la respuesta.";
                } else {
                    errorMessage = "Error desconocido: " + error.getMessage();
                }
                Log.e("Competiciones", "Error de red: " + errorMessage);
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
            15000, // 15 segundos de timeout
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    @Override
    public void onTorneoClick(Torneo torneo) {
        // Verificar el estado de inscripción
        String estado = torneo.getEstadoInscripcion();
        
        if ("Pendiente".equals(estado)) {
            Toast.makeText(this, "Ya hay una solicitud pendiente para esta competición", Toast.LENGTH_LONG).show();
            return;
        } else if ("Rechazada".equals(estado)) {
            Toast.makeText(this, "Tu solicitud anterior fue rechazada. Contacta al organizador", Toast.LENGTH_LONG).show();
            return;
        } else if ("Aceptada".equals(estado)) {
            Toast.makeText(this, "Ya estás inscrito en esta competición", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Si no está inscrito, verificar si es luchador y proceder
        verificarEsLuchador(torneo);
    }
    
    private void verificarEsLuchador(Torneo torneo) {
        // Obtener ID del usuario actual
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String url = Constantes.VERIFICAR_LUCHADOR + "?userId=" + userId;
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    if (response.getBoolean("success")) {
                        boolean esLuchador = response.getBoolean("esLuchador");
                        if (esLuchador) {
                            // Usuario es luchador, puede inscribirse
                            procesarInscripcion(torneo);
                        } else {
                            // Usuario no es luchador
                            Toast.makeText(this, "Debe completar su perfil de luchador para inscribirse en competiciones", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String message = response.getString("message");
                        Toast.makeText(this, "Error al verificar perfil: " + message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Error al procesar verificación", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Toast.makeText(this, "Error de conexión al verificar perfil", Toast.LENGTH_SHORT).show();
            }
        );
        
        requestQueue.add(request);
    }
    
    private void procesarInscripcion(Torneo torneo) {
        // Obtener ID del usuario actual
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Crear JSON con los datos de inscripción
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId);
            jsonBody.put("competicionId", torneo.getId());
            
            String url = Constantes.INSCRIBIR_COMPETICION;
            JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            String message = response.getString("message");
                            String competicion = response.getString("competicion");
                            String estado = response.getString("estado");
                            
                            Toast.makeText(this, message + "\nEstado: " + estado, Toast.LENGTH_LONG).show();
                            
                            // Recargar la lista para mostrar el nuevo estado
                            loadCompeticiones();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al procesar la respuesta de inscripción", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Error de conexión al procesar inscripción";
                    if (error.getMessage() != null) {
                        errorMessage += ": " + error.getMessage();
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            );
            
            // Agregar timeout
            request.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 segundos
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            
            requestQueue.add(request);
            
        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar datos de inscripción", Toast.LENGTH_SHORT).show();
        }
    }
}
