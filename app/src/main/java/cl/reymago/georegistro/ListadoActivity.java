package cl.reymago.georegistro;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListadoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tvVacio;
    ArrayList<Incidencia> lista = new ArrayList<>();
    IncidenciaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        recyclerView = findViewById(R.id.recyclerIncidencias);
        tvVacio = findViewById(R.id.tvVacio);

        adapter = new IncidenciaAdapter(lista);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        cargarIncidencias();
    }

    private void cargarIncidencias() {
        if (!Red.hayInternet(this)) {
            tvVacio.setText("Sin conexión a internet. No se pudo cargar el historial.");
            tvVacio.setVisibility(View.VISIBLE);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("incidencias")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot resultado) {
                        lista.clear();
                        for (QueryDocumentSnapshot documento : resultado) {
                            Incidencia incidencia = documento.toObject(Incidencia.class);
                            lista.add(incidencia);
                        }
                        adapter.notifyDataSetChanged();

                        if (lista.isEmpty()) {
                            tvVacio.setText("Aún no hay incidencias registradas.");
                            tvVacio.setVisibility(View.VISIBLE);
                        } else {
                            tvVacio.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        tvVacio.setText("Error al cargar: " + e.getMessage());
                        tvVacio.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void volver(View view) {
        finish();
    }
}