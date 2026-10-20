package cl.reymago.georegistro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    EditText etTitulo;
    EditText etDescripcion;
    EditText etFechaHora;
    EditText etLatitud;
    EditText etLongitud;
    Spinner spCategoria;

    // GPS
    TextView tvEstadoGps;
    Button btnUbicacion;
    FusedLocationProviderClient clienteUbicacion;

    // GPS: recibe la respuesta del usuario cuando el sistema le pregunta por el permiso
    ActivityResultLauncher<String[]> lanzadorPermisos = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> resultado) {
                    Boolean precisa = resultado.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    Boolean aproximada = resultado.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                    if (Boolean.TRUE.equals(precisa) || Boolean.TRUE.equals(aproximada)) {
                        buscarUbicacion();
                    } else {
                        tvEstadoGps.setText("Permiso de ubicación denegado. Escriba las coordenadas a mano "
                                + "o habilite el permiso en Ajustes > Apps > GeoRegistro > Permisos.");
                        Toast.makeText(RegistroActivity.this,
                                "Permiso de ubicación denegado", Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etFechaHora = findViewById(R.id.etFechaHora);
        etLatitud = findViewById(R.id.etLatitud);
        etLongitud = findViewById(R.id.etLongitud);
        spCategoria = findViewById(R.id.spCategoria);

        // GPS
        tvEstadoGps = findViewById(R.id.tvEstadoGps);
        btnUbicacion = findViewById(R.id.btnUbicacion);
        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this);

        ponerFechaActual();
    }

    // GPS: se ejecuta al tocar "Obtener ubicación actual"
    public void obtenerUbicacionActual(View view) {
        if (tienePermiso()) {
            buscarUbicacion();
        } else {
            lanzadorPermisos.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    // GPS: true si el usuario ya dio permiso de ubicación (precisa o aproximada)
    private boolean tienePermiso() {
        boolean precisa = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean aproximada = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return precisa || aproximada;
    }

    // GPS: pide la ubicación actual. Solo se llama cuando ya hay permiso.
    @SuppressLint("MissingPermission")
    private void buscarUbicacion() {
        tvEstadoGps.setText("Obteniendo ubicación...");
        btnUbicacion.setEnabled(false);

        clienteUbicacion.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location ubicacion) {
                        btnUbicacion.setEnabled(true);

                        // Puede ser null si el GPS está apagado o no hay señal
                        if (ubicacion == null) {
                            tvEstadoGps.setText("No se pudo obtener la ubicación. "
                                    + "Active el GPS e intente de nuevo, o escriba las coordenadas a mano.");
                            return;
                        }

                        // Locale.US para que el decimal lleve punto y no coma
                        String lat = String.format(Locale.US, "%.6f", ubicacion.getLatitude());
                        String lng = String.format(Locale.US, "%.6f", ubicacion.getLongitude());

                        etLatitud.setText(lat);
                        etLongitud.setText(lng);
                        tvEstadoGps.setText("Ubicación actual: " + lat + ", " + lng);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        btnUbicacion.setEnabled(true);
                        tvEstadoGps.setText("Error al obtener la ubicación: " + e.getMessage());
                    }
                });
    }

    public void guardar(View view) {
        boolean valido = true;
        double latitud = 0;
        double longitud = 0;

        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String latitudTexto = etLatitud.getText().toString().trim();
        String longitudTexto = etLongitud.getText().toString().trim();

        if (titulo.isEmpty()) {
            etTitulo.setError("El título es obligatorio");
            valido = false;
        }

        if (descripcion.isEmpty()) {
            etDescripcion.setError("La descripción es obligatoria");
            valido = false;
        }

        // La posición 0 es el texto "Seleccione una categoría"
        if (spCategoria.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            valido = false;
        }

        if (latitudTexto.isEmpty()) {
            etLatitud.setError("La latitud es obligatoria");
            valido = false;
        } else {
            try {
                latitud = Double.parseDouble(latitudTexto);
                if (latitud < -90 || latitud > 90) {
                    etLatitud.setError("Debe estar entre -90 y 90");
                    valido = false;
                }
            } catch (NumberFormatException e) {
                etLatitud.setError("Número no válido");
                valido = false;
            }
        }

        if (longitudTexto.isEmpty()) {
            etLongitud.setError("La longitud es obligatoria");
            valido = false;
        } else {
            try {
                longitud = Double.parseDouble(longitudTexto);
                if (longitud < -180 || longitud > 180) {
                    etLongitud.setError("Debe estar entre -180 y 180");
                    valido = false;
                }
            } catch (NumberFormatException e) {
                etLongitud.setError("Número no válido");
                valido = false;
            }
        }

        if (!valido) {
            Toast.makeText(this, "Revise los campos marcados", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si no hay internet, avisamos y no intentamos guardar
        if (!Red.hayInternet(this)) {
            Toast.makeText(this, "Sin conexión a internet. No se pudo guardar.", Toast.LENGTH_LONG).show();
            return;
        }

        // Si todavía no hay sesión anónima, la reintentamos
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Sin sesión. Intente nuevamente en unos segundos.", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signInAnonymously();
            return;
        }

        String categoria = spCategoria.getSelectedItem().toString();
        String fechaHora = etFechaHora.getText().toString();

        Incidencia incidencia = new Incidencia(titulo, descripcion, categoria, fechaHora, latitud, longitud);
        incidencia.setTimestamp(System.currentTimeMillis());

        // Desactivamos el botón para que no se guarde dos veces con un doble toque
        view.setEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("incidencias").add(incidencia)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RegistroActivity.this, "Incidencia guardada", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                        view.setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(RegistroActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        view.setEnabled(true);
                    }
                });
    }

    public void volver(View view) {
        finish();
    }

    private void ponerFechaActual() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        etFechaHora.setText(formato.format(new Date()));
    }

    private void limpiarFormulario() {
        etTitulo.setText("");
        etDescripcion.setText("");
        etLatitud.setText("");
        etLongitud.setText("");
        spCategoria.setSelection(0);
        tvEstadoGps.setText(R.string.gps_inicial);
        ponerFechaActual();
        etTitulo.requestFocus();
    }
}