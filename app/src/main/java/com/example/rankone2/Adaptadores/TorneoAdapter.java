package com.example.rankone2.Adaptadores;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rankone2.Modelo.Torneo;
import com.example.rankone2.R;
import java.util.List;

public class TorneoAdapter extends RecyclerView.Adapter<TorneoAdapter.TorneoViewHolder> {
    private List<Torneo> torneos;
    private OnTorneoClickListener listener;

    public interface OnTorneoClickListener {
        void onTorneoClick(Torneo torneo);
    }

    public TorneoAdapter(List<Torneo> torneos, OnTorneoClickListener listener) {
        this.torneos = torneos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TorneoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_competicion, parent, false);
        return new TorneoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TorneoViewHolder holder, int position) {
        Torneo torneo = torneos.get(position);
        
        // Establecer el nombre
        holder.tvNombre.setText(torneo.getNombre());
        
        // Establecer el arte marcial
        holder.tvCategoria.setText("Arte Marcial: " + torneo.getArteMarcial());

        // Establecer la fecha
        String fechaStr = "Fecha: ";
        if (torneo.getFechaInicio() != null) {
            fechaStr += torneo.getFechaInicioFormateada();
            if (torneo.getFechaFin() != null) {
                fechaStr += " - " + torneo.getFechaFinFormateada();
            }
        } else {
            fechaStr += "No especificada";
        }
        holder.tvFecha.setText(fechaStr);
        
        // Establecer la ubicación
        String ubicacionStr = "Ubicación: ";
        if (!torneo.getLocalidad().isEmpty() && !torneo.getPais().isEmpty()) {
            ubicacionStr += torneo.getLocalidad() + ", " + torneo.getPais();
        } else if (!torneo.getLocalidad().isEmpty()) {
            ubicacionStr += torneo.getLocalidad();
        } else if (!torneo.getPais().isEmpty()) {
            ubicacionStr += torneo.getPais();
        } else {
            ubicacionStr += "No especificada";
        }
        holder.tvUbicacion.setText(ubicacionStr);
        
        // Establecer el ganador
        if (torneo.getGanador() != null && !torneo.getGanador().isEmpty() && !torneo.getGanador().equals("No hay ganador")) {
            holder.tvGanador.setText("Ganador: " + torneo.getGanador());
            holder.tvGanador.setVisibility(View.VISIBLE);
        } else {
            holder.tvGanador.setVisibility(View.GONE);
        }
        
        // Configurar el botón según el estado de inscripción
        String estadoInscripcion = torneo.getEstadoInscripcion();
        
        if ("Pendiente".equals(estadoInscripcion)) {
            holder.btnInscribirse.setText("PENDIENTE");
            holder.btnInscribirse.setBackgroundColor(Color.parseColor("#FFA500")); // Amarillo/Naranja
            holder.btnInscribirse.setTextColor(Color.WHITE);
            holder.btnInscribirse.setEnabled(false); // No permite otra solicitud
        } else if ("Rechazada".equals(estadoInscripcion)) {
            holder.btnInscribirse.setText("RECHAZADA");
            holder.btnInscribirse.setBackgroundColor(Color.parseColor("#F44336")); // Rojo
            holder.btnInscribirse.setTextColor(Color.WHITE);
            holder.btnInscribirse.setEnabled(false); // No permite otra solicitud
        } else if ("Aceptada".equals(estadoInscripcion)) {
            holder.btnInscribirse.setText("ACEPTADA");
            holder.btnInscribirse.setBackgroundColor(Color.parseColor("#4CAF50")); // Verde
            holder.btnInscribirse.setTextColor(Color.WHITE);
            holder.btnInscribirse.setEnabled(false); // No permite otra solicitud
        } else {
            holder.btnInscribirse.setText("INSCRIBIRSE");
            // Restaurar color original del botón
            holder.btnInscribirse.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
            holder.btnInscribirse.setTextColor(Color.WHITE);
            holder.btnInscribirse.setEnabled(true);
        }
        
        holder.btnInscribirse.setOnClickListener(v -> listener.onTorneoClick(torneo));
    }

    @Override
    public int getItemCount() {
        return torneos.size();
    }

    public static class TorneoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvFecha, tvUbicacion, tvCategoria, tvGanador;
        Button btnInscribirse;
        
        public TorneoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCompeticion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvGanador = itemView.findViewById(R.id.tvGanador);
            btnInscribirse = itemView.findViewById(R.id.btnInscribirse);
        }
    }
} 