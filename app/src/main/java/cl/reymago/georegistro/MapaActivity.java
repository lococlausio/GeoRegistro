package cl.reymago.georegistro;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

// implements OnMapReadyCallback: Android nos avisa cuando el mapa ya está listo
public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mapa;
    TextView tvEstadoMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        tvEstadoMapa = findViewById(R.id.tvEstadoMapa);

        SupportMapFragment fragmento = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapa);
        fragmento.getMapAsync(this);
    }

    // Se ejecuta cuando el mapa está listo para usarse
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.getUiSettings().setZoomControlsEnabled(true);
        cargarIncidencias();
    }

    private void cargarIncidencias() {
        if (!Red.hayInternet(this)) {
            tvEstadoMapa.setText("Sin conexión a internet. No se pudieron cargar las incidencias.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("incidencias")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot resultado) {
                        if (resultado.isEmpty()) {
                            tvEstadoMapa.setText("Aún no hay incidencias para mostrar.");
                            return;
                        }

                        boolean esLaPrimera = true;

                        for (QueryDocumentSnapshot documento : resultado) {
                            Incidencia incidencia = documento.toObject(Incidencia.class);
                            LatLng posicion = new LatLng(incidencia.getLatitud(), incidencia.getLongitud());

                            MarkerOptions marcador = new MarkerOptions();
                            marcador.position(posicion);
                            marcador.title(incidencia.getTitulo());
                            marcador.snippet(incidencia.getCategoria() + " - " + incidencia.getFechaHora());
                            mapa.addMarker(marcador);

                            // Centramos el mapa en la incidencia más reciente (la primera de la lista)
                            if (esLaPrimera) {
                                mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 12));
                                esLaPrimera = false;
                            }
                        }

                        tvEstadoMapa.setText("Incidencias en el mapa: " + resultado.size());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        tvEstadoMapa.setText("Error al cargar: " + e.getMessage());
                    }
                });
    }

    public void volver(View view) {
        finish();
    }
}