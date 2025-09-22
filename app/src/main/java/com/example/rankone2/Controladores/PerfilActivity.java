package com.example.rankone2.Controladores;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.rankone2.Modelo.Constantes;
import com.example.rankone2.Modelo.Competidor;
import com.example.rankone2.Modelo.Usuario;
import com.example.rankone2.Modelo.UsuarioConcreto;
import com.example.rankone2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.android.volley.toolbox.JsonObjectRequest;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.app.AlertDialog;

public class PerfilActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 201;

    private ImageView ivFotoPerfil;
    private ImageButton btnCambiarFoto;
    private TextInputEditText etNombreCompleto;
    private TextInputEditText etNacionalidad, etDni, etFechaNacimiento;
    private TextInputEditText etArteMarcial, etGrado, etSexo;
    private MaterialButton btnGuardar;
    private RequestQueue requestQueue;
    private Bitmap fotoPerfil;
    private Usuario usuarioActual;
    private Competidor competidorActual;
    private String currentPhotoPath;
    // Variables de campos removidas: toolbar, etUsuario, etLocalidad, etTelefono
    private int userId;
    private SimpleDateFormat dateFormatDisplay;
    private SimpleDateFormat dateFormatDatabase;
    private Calendar fechaNacimientoCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        inicializarVistas();
        requestQueue = Volley.newRequestQueue(this);
        cargarDatosUsuario();
    }

    private void inicializarVistas() {
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);
        etNombreCompleto = findViewById(R.id.etNombreCompleto);
        etNacionalidad = findViewById(R.id.etNacionalidad);
        etDni = findViewById(R.id.etDni);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etArteMarcial = findViewById(R.id.etArteMarcial);
        etGrado = findViewById(R.id.etGrado);
        etSexo = findViewById(R.id.etSexo);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Inicializar formatos de fecha
        dateFormatDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormatDatabase = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        fechaNacimientoCalendar = Calendar.getInstance();

        // Configurar listeners
        btnCambiarFoto.setOnClickListener(v -> mostrarOpcionesFoto());
        btnGuardar.setOnClickListener(v -> guardarCambios());
        etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());
        etSexo.setOnClickListener(v -> mostrarSelectorGenero());

        // Obtener ID del usuario actual
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void mostrarOpcionesFoto() {
        String[] opciones = {"Tomar foto", "Elegir de galería"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Seleccionar foto");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                verificarPermisosCamara();
            }else {
                verificarPermisosGaleria();
            }
        });
        builder.show();
    }

    private void verificarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void verificarPermisosGaleria() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            abrirSelectorImagen();
        }
    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.rankone2.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirSelectorImagen();
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a la galería",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a la cámara",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == PICK_IMAGE && data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        fotoPerfil = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        if (fotoPerfil != null) {
                            ivFotoPerfil.setImageBitmap(fotoPerfil);
                        }
                    }
                } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    File photoFile = new File(currentPhotoPath);
                    if (photoFile.exists()) {
                        fotoPerfil = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(photoFile));
                        if (fotoPerfil != null) {
                            ivFotoPerfil.setImageBitmap(fotoPerfil);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarDatosUsuario() {
        String url = Constantes.SERVIDOR + "get_usuario.php?userId=" + userId;
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject userData = response.getJSONObject("data");
                        
                        // Cargar todos los campos disponibles
                        etNombreCompleto.setText(userData.optString("nombreCompleto", ""));
                        etDni.setText(userData.optString("dni", ""));
                        etNacionalidad.setText(userData.optString("nacionalidad", ""));
                        
                        // Manejar la fecha de nacimiento
                        String fechaNacimientoStr = userData.optString("fechaNacimiento", "");
                        if (!fechaNacimientoStr.isEmpty()) {
                            try {
                                // Si viene en formato yyyy-MM-dd de la base de datos
                                Date fechaDB = dateFormatDatabase.parse(fechaNacimientoStr);
                                fechaNacimientoCalendar.setTime(fechaDB);
                                // Mostrar en formato dd/MM/yyyy al usuario
                                etFechaNacimiento.setText(dateFormatDisplay.format(fechaDB));
                            } catch (Exception e) {
                                // Si falla, intentar con el formato de display
                                try {
                                    Date fechaDisplay = dateFormatDisplay.parse(fechaNacimientoStr);
                                    fechaNacimientoCalendar.setTime(fechaDisplay);
                                    etFechaNacimiento.setText(fechaNacimientoStr);
                                } catch (Exception e2) {
                                    // Si ambos fallan, dejar vacío
                                    etFechaNacimiento.setText("");
                                }
                            }
                        }
                        
                        etArteMarcial.setText(userData.optString("arteMarcial", ""));
                        etGrado.setText(userData.optString("grado", ""));
                        etSexo.setText(userData.optString("sexo", ""));
                        
                        // Cargar foto de perfil si existe
                        String fotoPerfil = userData.optString("fotoPerfil", "");
                        if (!fotoPerfil.isEmpty()) {
                            cargarFotoPerfil(fotoPerfil);
                        }
                        
                        // Mostrar si ya es luchador
                        boolean esLuchador = userData.optBoolean("esLuchador", false);
                        if (esLuchador) {
                            Toast.makeText(this, "Perfil de luchador cargado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Datos básicos cargados - Complete perfil de luchador", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        );

        requestQueue.add(request);
    }

    private void guardarCambios() {
        if (!validarCampos()) {
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId);
            
            // Datos de usuario
            jsonBody.put("nombreCompleto", etNombreCompleto.getText().toString().trim());
            
            // Datos de luchador
            jsonBody.put("dni", etDni.getText().toString().trim());
            jsonBody.put("nacionalidad", etNacionalidad.getText().toString().trim());
            
            // Convertir fecha al formato de base de datos (yyyy-MM-dd)
            String fechaNacimientoParaDB = "";
            if (!etFechaNacimiento.getText().toString().trim().isEmpty()) {
                fechaNacimientoParaDB = dateFormatDatabase.format(fechaNacimientoCalendar.getTime());
            }
            jsonBody.put("fechaNacimiento", fechaNacimientoParaDB);
            
            jsonBody.put("arteMarcial", etArteMarcial.getText().toString().trim());
            jsonBody.put("grado", etGrado.getText().toString().trim());
            jsonBody.put("sexo", etSexo.getText().toString().trim());
            
            // Convertir foto a base64 si existe
            if (fotoPerfil != null) {
                String imagenBase64 = convertirBitmapABase64(fotoPerfil);
                jsonBody.put("fotoPerfil", imagenBase64);
                jsonBody.put("tieneNuevaFoto", true);
            } else {
                jsonBody.put("tieneNuevaFoto", false);
            }

            String url = Constantes.SERVIDOR + "update_usuario.php";
            JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                }
            );

            requestQueue.add(request);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al preparar los datos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos() {
        if (etNombreCompleto.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Por favor, complete el nombre completo", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void mostrarDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                fechaNacimientoCalendar.set(year, month, dayOfMonth);
                // Mostrar la fecha en formato dd/MM/yyyy al usuario
                etFechaNacimiento.setText(dateFormatDisplay.format(fechaNacimientoCalendar.getTime()));
            },
            fechaNacimientoCalendar.get(Calendar.YEAR),
            fechaNacimientoCalendar.get(Calendar.MONTH),
            fechaNacimientoCalendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Establecer fecha máxima (hoy) para evitar fechas futuras
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        
        // Establecer fecha mínima (hace 100 años)
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -100);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        
        datePickerDialog.show();
    }

    private void mostrarSelectorGenero() {
        String[] generos = {"Masculino (M)", "Femenino (F)"};
        String[] valores = {"M", "F"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Género");
        builder.setItems(generos, (dialog, which) -> {
            etSexo.setText(valores[which]);
        });
        builder.show();
    }
    
    private String convertirBitmapABase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(byteArray);
        } else {
            return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
        }
    }
    
    private void cargarFotoPerfil(String nombreArchivo) {
        if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
            String urlFoto = Constantes.SERVIDOR + "fotosperfil/" + nombreArchivo;
            // Usar una librería como Picasso o Glide para cargar la imagen
            // Por ahora, implementaremos una carga básica
            cargarImagenDesdeURL(urlFoto);
        }
    }
    
    private void cargarImagenDesdeURL(String url) {
        // Implementación básica para cargar imagen desde URL
        Thread thread = new Thread(() -> {
            try {
                java.net.URL imageUrl = new java.net.URL(url);
                Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                runOnUiThread(() -> {
                    if (bitmap != null) {
                        ivFotoPerfil.setImageBitmap(bitmap);
                        fotoPerfil = bitmap;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                // No mostrar error si no hay foto, es normal
            }
        });
        thread.start();
    }

    // onOptionsItemSelected removido - no hay toolbar
}
