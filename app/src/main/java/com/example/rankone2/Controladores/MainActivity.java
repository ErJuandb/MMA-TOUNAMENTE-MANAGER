package com.example.rankone2.Controladores;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.rankone2.R;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    private RequestQueue requestQueue;
    private Button btnResultados, btnCompeticiones;
    private ImageButton btnPerfil, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_principal_layout);

        // Inicializar RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Inicializar botones
        btnResultados = findViewById(R.id.btnResultados);
        btnCompeticiones = findViewById(R.id.btnCompeticiones);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Configurar listeners
        btnResultados.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MisResultadosActivity.class);
            startActivity(intent);
        });

        btnCompeticiones.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompeticionesActivity.class);
            startActivity(intent);
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        // Nuevo listener para cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            cerrarSesion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion() {
        // Limpiar SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        
        // Preguntar si quiere mantener la autenticación biométrica
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Cerrar Sesión");
        builder.setMessage("¿Deseas mantener habilitado el acceso con huella dactilar?");
        builder.setPositiveButton("Mantener", (dialog, which) -> {
            // Mantener datos biométricos pero resetear la pregunta para futuros usuarios
            resetBiometricQuestion();
            redirectToLogin();
        });
        builder.setNegativeButton("Desactivar", (dialog, which) -> {
            // Limpiar datos biométricos completamente
            SharedPreferences biometricPrefs = getSharedPreferences("BiometricData", MODE_PRIVATE);
            SharedPreferences.Editor biometricEditor = biometricPrefs.edit();
            biometricEditor.clear();
            biometricEditor.apply();
            redirectToLogin();
        });
        builder.setCancelable(false);
        builder.show();
    }
    
    /**
     * Muestra el diálogo para cerrar sesión desde el botón principal
     * Ofrece opciones más claras para el usuario
     */
    private void mostrarDialogoCerrarSesion() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("🚪 Cerrar Sesión");
        builder.setMessage("¿Qué deseas hacer con tu sesión?");
        
        // Opción 1: Cerrar sesión pero mantener huella dactilar
        builder.setPositiveButton("🔐 Mantener Huella", (dialog, which) -> {
            cerrarSesionManteniendoBiometria();
        });
        
        // Opción 2: Cerrar sesión completamente
        builder.setNegativeButton("🗑️ Cerrar Todo", (dialog, which) -> {
            cerrarSesionCompleta();
        });
        
        // Opción 3: Cancelar
        builder.setNeutralButton("❌ Cancelar", null);
        
        builder.show();
    }
    
    /**
     * Cierra la sesión actual pero mantiene los datos biométricos
     * Permite que el mismo usuario pueda volver a entrar con huella
     */
    private void cerrarSesionManteniendoBiometria() {
        // Limpiar solo la sesión temporal
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        
        // Resetear la pregunta biométrica para futuros usuarios diferentes
        resetBiometricQuestion();
        
        android.widget.Toast.makeText(this, "✅ Sesión cerrada. Huella dactilar mantenida", android.widget.Toast.LENGTH_SHORT).show();
        
        redirectToLogin();
    }
    
    /**
     * Cierra la sesión completamente eliminando tanto datos temporales como biométricos
     * El usuario tendrá que configurar todo de nuevo
     */
    private void cerrarSesionCompleta() {
        // Limpiar sesión temporal
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        
        // Limpiar datos biométricos completamente
        SharedPreferences biometricPrefs = getSharedPreferences("BiometricData", MODE_PRIVATE);
        SharedPreferences.Editor biometricEditor = biometricPrefs.edit();
        biometricEditor.clear();
        biometricEditor.apply();
        
        android.widget.Toast.makeText(this, "🗑️ Sesión cerrada completamente", android.widget.Toast.LENGTH_SHORT).show();
        
        redirectToLogin();
    }
    
    /**
     * Resetea la pregunta biométrica para permitir que se vuelva a preguntar
     * a futuros usuarios que inicien sesión
     */
    private void resetBiometricQuestion() {
        SharedPreferences biometricPrefs = getSharedPreferences("BiometricData", MODE_PRIVATE);
        SharedPreferences.Editor editor = biometricPrefs.edit();
        editor.putBoolean("hasAskedForBiometric", false);
        editor.apply();
    }
    
    private void redirectToLogin() {
        // Redirigir al login
        Intent intent = new Intent(MainActivity.this, InicioSesionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    // Método para verificar si el usuario está logueado
    private boolean verificarSesion() {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        return userId != -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!verificarSesion()) {
            // Si no hay sesión activa, redirigir al login
            Intent intent = new Intent(this, InicioSesionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
