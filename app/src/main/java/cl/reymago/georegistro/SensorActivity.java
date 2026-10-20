package cl.reymago.georegistro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

// implements SensorEventListener: Android nos avisa cada vez que el sensor entrega datos nuevos
public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    // Tipo de sensor que usamos. Cambiar esta línea sirve para probar el caso "sin sensor".
    int tipoSensor = Sensor.TYPE_ACCELEROMETER;

    // Fuerza total desde la cual hay sacudida. En reposo es cercana a 9.8 (la gravedad).
    float umbralSacudida = 15;

    SensorManager sensorManager;
    Sensor sensor;

    TextView tvEstadoSensor;
    TextView tvX;
    TextView tvY;
    TextView tvZ;
    TextView tvMagnitud;
    TextView tvMovimiento;
    TextView tvSacudidas;

    long ultimaSacudida = 0;
    int sacudidas = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        tvEstadoSensor = findViewById(R.id.tvEstadoSensor);
        tvX = findViewById(R.id.tvX);
        tvY = findViewById(R.id.tvY);
        tvZ = findViewById(R.id.tvZ);
        tvMagnitud = findViewById(R.id.tvMagnitud);
        tvMovimiento = findViewById(R.id.tvMovimiento);
        tvSacudidas = findViewById(R.id.tvSacudidas);

        tvX.setText("X: --");
        tvY.setText("Y: --");
        tvZ.setText("Z: --");
        tvMagnitud.setText("Fuerza total: --");
        tvSacudidas.setText("Sacudidas detectadas: 0");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(tipoSensor);

        // Si el dispositivo no tiene el sensor, getDefaultSensor devuelve null
        if (sensor == null) {
            tvEstadoSensor.setText("Este dispositivo no tiene acelerómetro. Función no disponible.");
            tvMovimiento.setText("Sensor no disponible");
        } else {
            tvEstadoSensor.setText("Acelerómetro disponible. Mueva o sacuda el dispositivo.");
        }
    }

    // Empezamos a escuchar el sensor cuando la pantalla está visible
    @Override
    protected void onResume() {
        super.onResume();
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    // Dejamos de escuchar al salir de la pantalla, para no gastar batería
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // Se ejecuta cada vez que el sensor entrega una lectura
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        double magnitud = Math.sqrt(x * x + y * y + z * z);

        tvX.setText(String.format(Locale.getDefault(), "X: %.2f", x));
        tvY.setText(String.format(Locale.getDefault(), "Y: %.2f", y));
        tvZ.setText(String.format(Locale.getDefault(), "Z: %.2f", z));
        tvMagnitud.setText(String.format(Locale.getDefault(), "Fuerza total: %.2f", magnitud));

        long ahora = System.currentTimeMillis();

        if (magnitud > umbralSacudida) {
            // Si pasó más de 1 segundo desde la última sacudida, cuenta como una nueva
            if (ahora - ultimaSacudida > 1000) {
                sacudidas = sacudidas + 1;
                tvSacudidas.setText("Sacudidas detectadas: " + sacudidas);
            }
            ultimaSacudida = ahora;
        }

        // El mensaje de movimiento se mantiene 1.5 segundos después de la última sacudida
        if (ahora - ultimaSacudida < 1500) {
            tvMovimiento.setText("¡Movimiento detectado!");
        } else {
            tvMovimiento.setText(R.string.sensor_sin_movimiento);
        }
    }

    // Android exige este método, pero no lo necesitamos
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void volver(View view) {
        finish();
    }
}