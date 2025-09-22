package com.example.rankone2.Controladores;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.example.rankone2.Adaptadores.ResultadoAdapter;
import com.example.rankone2.Modelo.Torneo;
import com.example.rankone2.Modelo.Constantes;
import com.example.rankone2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MisResultadosActivity extends AppCompatActivity implements ResultadoAdapter.OnVerCombatesClickListener {

    private Toolbar toolbar;
    private TextView tvFechaInicio, tvFechaFin;
    private RecyclerView rvResultados;
    private TextView tvNoResultados;
    private ResultadoAdapter adapter;
    private List<Torneo> listaResultados;
    private Calendar fechaInicio, fechaFin;
    private RequestQueue requestQueue;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat serverDateFormat;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_resultados);

        // Inicializar componentes
        toolbar = findViewById(R.id.toolbar);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        tvFechaFin = findViewById(R.id.tvFechaFin);
        rvResultados = findViewById(R.id.rvResultados);
        tvNoResultados = findViewById(R.id.tvNoResultados);

        // Configurar toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mis Resultados");
        }

        // Inicializar variables
        listaResultados = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        adapter = new ResultadoAdapter(listaResultados, this);
        rvResultados.setLayoutManager(new LinearLayoutManager(this));
        rvResultados.setAdapter(adapter);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        serverDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Obtener ID del usuario actual
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar fechas iniciales
        fechaInicio = Calendar.getInstance();
        fechaInicio.add(Calendar.MONTH, -1);
        fechaFin = Calendar.getInstance();
        actualizarFechasUI();

        // Configurar listeners
        tvFechaInicio.setOnClickListener(v -> mostrarDatePicker(true));
        tvFechaFin.setOnClickListener(v -> mostrarDatePicker(false));

        // Cargar datos iniciales
        cargarResultados();
    }

    private void actualizarFechasUI() {
        tvFechaInicio.setText(dateFormat.format(fechaInicio.getTime()));
        tvFechaFin.setText(dateFormat.format(fechaFin.getTime()));
    }

    private void mostrarDatePicker(boolean esFechaInicio) {
        Calendar calendar = esFechaInicio ? fechaInicio : fechaFin;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar fechaSeleccionada = Calendar.getInstance();
                fechaSeleccionada.set(year, month, dayOfMonth);
                
                if (esFechaInicio) {
                    if (fechaSeleccionada.after(fechaFin)) {
                        Toast.makeText(this, "La fecha de inicio no puede ser posterior a la fecha fin", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fechaInicio = fechaSeleccionada;
                } else {
                    if (fechaSeleccionada.before(fechaInicio)) {
                        Toast.makeText(this, "La fecha fin no puede ser anterior a la fecha inicio", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fechaFin = fechaSeleccionada;
                }
                actualizarFechasUI();
                cargarResultados();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void cargarResultados() {
        String url = Constantes.SERVIDOR + "get_resultados.php" +
                    "?fecha_inicio=" + serverDateFormat.format(fechaInicio.getTime()) +
                    "&fecha_fin=" + serverDateFormat.format(fechaFin.getTime()) +
                    "&user_id=" + userId;

        android.util.Log.d("MisResultados", "URL: " + url);
        android.util.Log.d("MisResultados", "UserId: " + userId);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                android.util.Log.d("MisResultados", "Respuesta recibida: " + response.toString());
                listaResultados.clear();
                try {
                    if (response.length() == 0) {
                        android.util.Log.d("MisResultados", "No hay resultados para el período seleccionado");
                        Toast.makeText(this, "No hay resultados para el período seleccionado", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Torneo torneo = new Torneo();
                            torneo.setId(jsonObject.getInt("id"));
                            torneo.setNombre(jsonObject.getString("nombre"));
                            
                            // Parsear la fecha
                            String fechaStr = jsonObject.getString("fechaInicio");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                LocalDateTime fecha = LocalDateTime.parse(fechaStr, formatter);
                                torneo.setFechaInicio(fecha);
                            }

                            torneo.setLocalidad(jsonObject.getString("lugar"));
                            torneo.setArteMarcial(jsonObject.getString("arte_marcial"));
                            torneo.setGanador(jsonObject.getString("ganador"));
                            listaResultados.add(torneo);
                        }
                        android.util.Log.d("MisResultados", "Resultados cargados: " + listaResultados.size());
                    }
                    adapter.notifyDataSetChanged();
                    tvNoResultados.setVisibility(listaResultados.isEmpty() ? View.VISIBLE : View.GONE);
                } catch (JSONException e) {
                    android.util.Log.e("MisResultados", "Error JSON: " + e.getMessage());
                    Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    android.util.Log.e("MisResultados", "Error procesando fechas: " + e.getMessage());
                    Toast.makeText(this, "Error al procesar las fechas", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                android.util.Log.e("MisResultados", "Error de red: " + error.toString());
                if (error.networkResponse != null) {
                    android.util.Log.e("MisResultados", "Código de error: " + error.networkResponse.statusCode);
                    try {
                        String errorBody = new String(error.networkResponse.data, "UTF-8");
                        android.util.Log.e("MisResultados", "Respuesta de error: " + errorBody);
                    } catch (Exception e) {
                        android.util.Log.e("MisResultados", "No se pudo leer el cuerpo del error");
                    }
                }
                Toast.makeText(this, "Error al cargar los resultados. Verifique su conexión.", Toast.LENGTH_SHORT).show();
                tvNoResultados.setVisibility(View.VISIBLE);
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

    @Override
    public void onVerCombatesClick(Torneo torneo) {
        Intent intent = new Intent(this, CombateActivity.class);
        intent.putExtra("competicion_id", torneo.getId());
        intent.putExtra("competicion_nombre", torneo.getNombre());
        startActivity(intent);
    }
}
