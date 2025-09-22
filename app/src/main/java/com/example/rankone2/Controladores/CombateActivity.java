package com.example.rankone2.Controladores;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.rankone2.Adaptadores.CombateAdapter;
import com.example.rankone2.Modelo.Combate;
import com.example.rankone2.Modelo.Constantes;
import com.example.rankone2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CombateActivity extends AppCompatActivity {

    private TextView tvTituloCombates;
    private RecyclerView recyclerViewCombates;
    private CombateAdapter combateAdapter;
    private List<Combate> listaCombates;
    private RequestQueue requestQueue;
    
    private int competicionId;
    private String nombreCompeticion;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combate);

        // Inicializar vistas
        inicializarVistas();
        
        // Configurar toolbar
        configurarToolbar();
        
        // Obtener datos del intent y usuario
        obtenerDatosIntent();
        
        // Configurar RecyclerView
        configurarRecyclerView();
        
        // Cargar combates
        cargarCombates();
    }

    private void inicializarVistas() {
        tvTituloCombates = findViewById(R.id.tvTituloCombates);
        recyclerViewCombates = findViewById(R.id.recyclerViewCombates);
    }

    private void configurarToolbar() {
        // Si hay toolbar en el layout, configurarlo
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Combates");
            }
        }
    }

    private void obtenerDatosIntent() {
        // Obtener datos del intent
        competicionId = getIntent().getIntExtra("competicion_id", -1);
        nombreCompeticion = getIntent().getStringExtra("competicion_nombre");
        
        if (competicionId == -1) {
            Toast.makeText(this, "Error: Competición no encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obtener ID del usuario actual
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Actualizar título
        if (nombreCompeticion != null) {
            tvTituloCombates.setText("Combates de " + nombreCompeticion);
        }
    }

    private void configurarRecyclerView() {
        listaCombates = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        combateAdapter = new CombateAdapter(listaCombates, userId);
        recyclerViewCombates.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCombates.setAdapter(combateAdapter);
    }

    private void cargarCombates() {
        String url = Constantes.GET_COMBATES +
                    "?competicion_id=" + competicionId +
                    "&user_id=" + userId;

        android.util.Log.d("CombateActivity", "URL: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                android.util.Log.d("CombateActivity", "Respuesta recibida: " + response.toString());
                listaCombates.clear();
                try {
                    if (response.length() == 0) {
                        Toast.makeText(this, "No hay combates disponibles para esta competición", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Combate combate = new Combate();
                            
                            combate.setId(jsonObject.getInt("id"));
                            combate.setIdCompeticion(jsonObject.getInt("id_competicion"));
                            combate.setIdLuchador1(jsonObject.getInt("id_luchador1"));
                            combate.setIdLuchador2(jsonObject.getInt("id_luchador2"));
                            combate.setNombreLuchador1(jsonObject.getString("nombre_luchador1"));
                            combate.setNombreLuchador2(jsonObject.getString("nombre_luchador2"));
                            combate.setResultado(jsonObject.optString("resultado", ""));
                            combate.setFase(jsonObject.getString("fase"));
                            combate.setGanadorId(jsonObject.optInt("ganador_id", 0));
                            combate.setNombreGanador(jsonObject.optString("nombre_ganador", ""));
                            
                            // Parsear fecha si está disponible
                            String fechaStr = jsonObject.optString("fecha", "");
                            if (!fechaStr.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                try {
                                    LocalDate fecha = LocalDate.parse(fechaStr);
                                    combate.setFecha(fecha);
                                } catch (Exception e) {
                                    android.util.Log.e("CombateActivity", "Error parsing date: " + e.getMessage());
                                }
                            }
                            
                            listaCombates.add(combate);
                        }
                        android.util.Log.d("CombateActivity", "Combates cargados: " + listaCombates.size());
                    }
                    combateAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    android.util.Log.e("CombateActivity", "Error JSON: " + e.getMessage());
                    Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                android.util.Log.e("CombateActivity", "Error de red: " + error.toString());
                if (error.networkResponse != null) {
                    android.util.Log.e("CombateActivity", "Código de error: " + error.networkResponse.statusCode);
                }
                Toast.makeText(this, "Error al cargar los combates", Toast.LENGTH_SHORT).show();
            }
        );

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 