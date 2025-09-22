package com.example.rankone2.Controladores;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rankone2.Modelo.Constantes;
import com.example.rankone2.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombreCompleto, etUsuario, etLocalidad, etTelefono, etPassword, etRepetirPassword;
    private MaterialButton btnRegistrarse;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_layout);

        inicializarVistas();
        requestQueue = Volley.newRequestQueue(this);

        btnRegistrarse.setOnClickListener(v -> registrarUsuario());

    }

    @SuppressLint("WrongViewCast")
    private void inicializarVistas() {
        etNombreCompleto = findViewById(R.id.etNombreCompleto);
        etUsuario = findViewById(R.id.etUsuario);
        etLocalidad = findViewById(R.id.etLocalidad);
        etTelefono = findViewById(R.id.etTelefono);
        etPassword = findViewById(R.id.etContrasena);
        etRepetirPassword = findViewById(R.id.etConfirmContrasena);
        btnRegistrarse = findViewById(R.id.btnRegistrar);
    }

    private void registrarUsuario() {
        String nombreCompleto = etNombreCompleto.getText().toString().trim();
        String usuario = etUsuario.getText().toString().trim();
        String localidad = etLocalidad.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String repetirPassword = etRepetirPassword.getText().toString().trim();

        if (validarCampos(nombreCompleto, usuario, localidad, telefono, password, repetirPassword)) {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("nombreCompleto", nombreCompleto);
                jsonBody.put("usuario", usuario);
                jsonBody.put("localidad", localidad);
                jsonBody.put("telefono", telefono);
                jsonBody.put("contrasena", password);

                JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Constantes.REGISTRO,
                    jsonBody,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(RegistroActivity.this, 
                                    "Registro exitoso", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistroActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegistroActivity.this,
                                    response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(RegistroActivity.this,
                                "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String errorMessage = "Error de conexión";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                JSONObject errorJson = new JSONObject(new String(error.networkResponse.data));
                                errorMessage = errorJson.getString("message");
                            } catch (JSONException e) {
                                errorMessage = "Error al procesar la respuesta del servidor";
                            }
                        }
                        Toast.makeText(RegistroActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                );

                requestQueue.add(request);
            } catch (JSONException e) {
                Toast.makeText(this, "Error al preparar los datos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validarCampos(String nombreCompleto, String usuario, String localidad,
                                String telefono, String password, String repetirPassword) {
        if (nombreCompleto.isEmpty()) {
            etNombreCompleto.setError("El nombre completo es obligatorio");
            return false;
        }
        if (usuario.isEmpty()) {
            etUsuario.setError("El usuario es obligatorio");
            return false;
        }
        if (localidad.isEmpty()) {
            etLocalidad.setError("La localidad es obligatoria");
            return false;
        }
        if (telefono.isEmpty()) {
            etTelefono.setError("El teléfono es obligatorio");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("La contraseña es obligatoria");
            return false;
        }
        if (repetirPassword.isEmpty()) {
            etRepetirPassword.setError("Debe repetir la contraseña");
            return false;
        }
        if (!password.equals(repetirPassword)) {
            etRepetirPassword.setError("Las contraseñas no coinciden");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        return true;
    }
}

