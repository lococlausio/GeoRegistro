package cl.reymago.georegistro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.ViewHolder> {

    private List<Incidencia> lista;

    public IncidenciaAdapter(List<Incidencia> lista) {
        this.lista = lista;
    }

    // Guarda las vistas de una fila para no buscarlas cada vez
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        TextView tvCategoria;
        TextView tvDescripcion;
        TextView tvFechaHora;
        TextView tvCoordenadas;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvCoordenadas = itemView.findViewById(R.id.tvCoordenadas);
        }
    }

    // Crea la vista de una fila a partir de item_incidencia.xml
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_incidencia, parent, false);
        return new ViewHolder(vista);
    }

    // Llena la fila con los datos de la incidencia en esa posición
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Incidencia incidencia = lista.get(position);
        holder.tvTitulo.setText(incidencia.getTitulo());
        holder.tvCategoria.setText("Categoría: " + incidencia.getCategoria());
        holder.tvDescripcion.setText(incidencia.getDescripcion());
        holder.tvFechaHora.setText("Fecha: " + incidencia.getFechaHora());
        holder.tvCoordenadas.setText("Lat: " + incidencia.getLatitud()
                + "  Lng: " + incidencia.getLongitud());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}