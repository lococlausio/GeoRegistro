package cl.reymago.georegistro;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    EditText etTitulo;
    EditText etDescripcion;
    EditText etFechaHora;
    EditText etLatitud;
    EditText etLongitud;
    Spinner spCategoria;

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

        ponerFechaActual();
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
        ponerFechaActual();
        etTitulo.requestFocus();
    }


}