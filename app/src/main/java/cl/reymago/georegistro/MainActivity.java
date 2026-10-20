package cl.reymago.georegistro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously();
        }
    }

    public void irARegistro(View view) {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }

    public void irAListado(View view) {
        Intent intent = new Intent(this, ListadoActivity.class);
        startActivity(intent);
    }

    public void irAMapa(View view) {
        Intent intent = new Intent(this, MapaActivity.class);
        startActivity(intent);
    }

    public void irASensor(View view) {
        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);
    }
}