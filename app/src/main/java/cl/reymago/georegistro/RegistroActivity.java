package cl.reymago.georegistro;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        // La fecha y hora se llena sola con el momento actual
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        etFechaHora.setText(formato.format(new Date()));
    }

    public void guardar(View view) {
        boolean valido = true;

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
                double latitud = Double.parseDouble(latitudTexto);
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
                double longitud = Double.parseDouble(longitudTexto);
                if (longitud < -180 || longitud > 180) {
                    etLongitud.setError("Debe estar entre -180 y 180");
                    valido = false;
                }
            } catch (NumberFormatException e) {
                etLongitud.setError("Número no válido");
                valido = false;
            }
        }

        if (valido) {
            // Paso 3: aquí se guardará en Firestore
            Toast.makeText(this, "Formulario válido", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Revise los campos marcados", Toast.LENGTH_SHORT).show();
        }
    }

    public void volver(View view) {
        finish();
    }
}