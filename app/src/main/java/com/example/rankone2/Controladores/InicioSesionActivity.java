package com.example.rankone2.Controladores;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rankone2.R;
import com.example.rankone2.Modelo.Constantes;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class InicioSesionActivity extends AppCompatActivity {
    private EditText etUsuario, etContrasena;
    private Button btnIniciarSesion;
    private TextView btnRegistrarse;
    private ImageButton btnHuellaDactilar;
    private TextView tvHuellaDactilar;
    
    // Variables para autenticación biométrica
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    
    // Claves para SharedPreferences biométricas
    private static final String BIOMETRIC_PREFS = "BiometricData";
    private static final String KEY_LAST_USER_ID = "lastUserId";
    private static final String KEY_LAST_USERNAME = "lastUsername";
    private static final String KEY_BIOMETRIC_ENABLED = "biometricEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_sesion_layout);

        // Inicializar vistas
        initViews();
        
        // Configurar autenticación biométrica
        setupBiometricAuthentication();
        
        // Verificar si mostrar opción biométrica
        checkBiometricAvailability();
        
        // Preguntar por configuración biométrica al inicio (si es necesario)
        askForBiometricSetup();

        // Configurar listeners
        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesionActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
        btnHuellaDactilar.setOnClickListener(v -> iniciarAutenticacionBiometrica());
    }
    
    private void initViews() {
        etUsuario = findViewById(R.id.txtUsuario);
        etContrasena = findViewById(R.id.txtContrasena);
        btnIniciarSesion = findViewById(R.id.buttonIniciarSesion);
        btnRegistrarse = findViewById(R.id.buttonRegistrarse);
        btnHuellaDactilar = findViewById(R.id.btnHuellaDactilar);
        tvHuellaDactilar = findViewById(R.id.tvHuellaDactilar);
    }
    
    private void setupBiometricAuthentication() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt((FragmentActivity) this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(InicioSesionActivity.this,
                                "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(InicioSesionActivity.this,
                                "Autenticación exitosa", Toast.LENGTH_SHORT).show();
                        loginWithBiometrics();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(InicioSesionActivity.this,
                                "Autenticación fallida", Toast.LENGTH_SHORT).show();
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación Biométrica")
                .setSubtitle("Usa tu huella dactilar para acceder a RankOne")
                .setNegativeButtonText("Cancelar")
                .build();
    }
    
    private void checkBiometricAvailability() {
        BiometricManager biometricManager = BiometricManager.from(this);
        SharedPreferences biometricPrefs = getSharedPreferences(BIOMETRIC_PREFS, MODE_PRIVATE);
        boolean biometricEnabled = biometricPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
        
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                if (biometricEnabled) {
                    showBiometricOption();
                }
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.d("BiometricAuth", "Este dispositivo no tiene sensor biométrico");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.d("BiometricAuth", "El sensor biométrico no está disponible");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No hay huellas dactilares registradas en el dispositivo", 
                        Toast.LENGTH_LONG).show();
                break;
        }
    }
    
    private void showBiometricOption() {
        SharedPreferences biometricPrefs = getSharedPreferences(BIOMETRIC_PREFS, MODE_PRIVATE);
        String lastUsername = biometricPrefs.getString(KEY_LAST_USERNAME, "");
        
        if (!lastUsername.isEmpty()) {
            btnHuellaDactilar.setVisibility(android.view.View.VISIBLE);
            tvHuellaDactilar.setVisibility(android.view.View.VISIBLE);
            tvHuellaDactilar.setText("Acceder como " + lastUsername);
        }
    }
    
    private void iniciarAutenticacionBiometrica() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) 
                == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(this, "Autenticación biométrica no disponible", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loginWithBiometrics() {
        SharedPreferences biometricPrefs = getSharedPreferences(BIOMETRIC_PREFS, MODE_PRIVATE);
        int userId = biometricPrefs.getInt(KEY_LAST_USER_ID, -1);
        String username = biometricPrefs.getString(KEY_LAST_USERNAME, "");
        
        if (userId != -1 && !username.isEmpty()) {
            // Simular login exitoso con datos guardados
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("userId", userId);
            editor.putString("userName", username);
            editor.apply();

            Toast.makeText(this, "Bienvenido, " + username, Toast.LENGTH_SHORT).show();
            
            // Ir a MainActivity
            Intent intent = new Intent(InicioSesionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error: No hay datos de usuario guardados", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciarSesion() {
        final String usuario = etUsuario.getText().toString().trim();
        final String contrasena = etContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(usuario)) {
            etUsuario.setError("Ingrese su usuario");
            return;
        }
        if (TextUtils.isEmpty(contrasena)) {
            etContrasena.setError("Ingrese su contraseña");
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constantes.INICIO_SESION,
                response -> {
                    try {
                        Log.d("LOGIN_RESPONSE", response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean status = jsonResponse.getBoolean("status");
                        String message = jsonResponse.getString("message");

                        if (status) {
                            int userId = jsonResponse.getInt("id");
                            String userName = jsonResponse.getString("nombre");
                            
                            // Guardar datos del usuario
                            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("userId", userId);
                            editor.putString("userName", userName);
                            editor.apply();
                            
                            // Guardar datos para autenticación biométrica (corregido)
                            saveBiometricData(userId, userName);

                            // Ir a MainActivity
                            Intent intent = new Intent(InicioSesionActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(InicioSesionActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("LOGIN_ERROR", "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(InicioSesionActivity.this, 
                            "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e("LOGIN_ERROR", "Error Volley: " + error.toString());
                    Toast.makeText(InicioSesionActivity.this, 
                        "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("contrasena", contrasena);
                return params;
            }
        };

        // Configurar timeout
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
            10000, // 10 segundos
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Añadir a la cola de peticiones
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    
    /**
     * Guarda los datos del usuario para futuras autenticaciones biométricas
     * @param userId ID del usuario
     * @param userName Nombre del usuario
     */
    private void saveBiometricData(int userId, String userName) {
        SharedPreferences biometricPrefs = getSharedPreferences(BIOMETRIC_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = biometricPrefs.edit();
        editor.putInt(KEY_LAST_USER_ID, userId);
        editor.putString(KEY_LAST_USERNAME, userName);
        editor.apply();
        
        Log.d("BiometricAuth", "Datos biométricos guardados para usuario: " + userName + " (ID: " + userId + ")");
    }
    
    /**
     * Pregunta al usuario si desea habilitar la autenticación biométrica
     * Se ejecuta al inicio de la aplicación si cumple las condiciones
     */
    private void askForBiometricSetup() {
        BiometricManager biometricManager = BiometricManager.from(this);
        SharedPreferences biometricPrefs = getSharedPreferences(BIOMETRIC_PREFS, MODE_PRIVATE);
        boolean biometricEnabled = biometricPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
        boolean hasAskedBefore = biometricPrefs.getBoolean("hasAskedForBiometric", false);
        
        // Solo preguntar si:
        // 1. El dispositivo soporta biometría
        // 2. No está habilitada ya
        // 3. No hemos preguntado antes
        // 4. Hay datos de usuario guardados (ha iniciado sesión antes)
        boolean hasUserData = biometricPrefs.getInt(KEY_LAST_USER_ID, -1) != -1;
        
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) 
                == BiometricManager.BIOMETRIC_SUCCESS 
                && !biometricEnabled 
                && !hasAskedBefore 
                && hasUserData) {
            
            String lastUsername = biometricPrefs.getString(KEY_LAST_USERNAME, "Usuario");
            
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("🔒 Configurar Huella Dactilar");
            builder.setMessage("¡Hola " + lastUsername + "!\n\n¿Deseas habilitar el acceso rápido con huella dactilar para futuras sesiones?");
            builder.setPositiveButton("✅ Sí, habilitar", (dialog, which) -> {
                SharedPreferences.Editor editor = biometricPrefs.edit();
                editor.putBoolean(KEY_BIOMETRIC_ENABLED, true);
                editor.putBoolean("hasAskedForBiometric", true);
                editor.apply();
                
                Toast.makeText(this, "🎉 Autenticación biométrica habilitada", Toast.LENGTH_SHORT).show();
                
                // Actualizar la UI para mostrar la opción biométrica
                checkBiometricAvailability();
            });
            builder.setNegativeButton("❌ No, gracias", (dialog, which) -> {
                // Marcar que ya preguntamos para no volver a preguntar
                SharedPreferences.Editor editor = biometricPrefs.edit();
                editor.putBoolean("hasAskedForBiometric", true);
                editor.apply();
            });
            builder.setCancelable(false); // No permitir cancelar tocando fuera
            builder.show();
        }
    }
    
    /**
     * Limpia todos los datos biométricos guardados
     * Útil cuando se quiere desactivar completamente la autenticación biométrica
     */
    public void clearBiometricData() {
        SharedPreferences biometricPrefs = getSharedPreferences(BIOMETRIC_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = biometricPrefs.edit();
        editor.clear();
        editor.apply();
        
        Log.d("BiometricAuth", "Datos biométricos limpiados completamente");
        
        // Ocultar la opción biométrica de la UI
        btnHuellaDactilar.setVisibility(android.view.View.GONE);
        tvHuellaDactilar.setVisibility(android.view.View.GONE);
    }
    
    /**
     * Resetea solo la pregunta biométrica para permitir volver a preguntar
     * Mantiene los datos del usuario guardados
     */
    public void resetBiometricQuestion() {
        SharedPreferences biometricPrefs = getSharedPreferences(BIOMETRIC_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = biometricPrefs.edit();
        editor.putBoolean("hasAskedForBiometric", false);
        editor.apply();
        
        Log.d("BiometricAuth", "Pregunta biométrica reseteada");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Revisar disponibilidad biométrica cuando se vuelve a la pantalla
        checkBiometricAvailability();
    }
}
