package com.example.rankone2.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rankone2.Modelo.Torneo;
import com.example.rankone2.R;

import java.util.List;

public class ResultadoAdapter extends RecyclerView.Adapter<ResultadoAdapter.ResultadoViewHolder> {

    private List<Torneo> torneos;
    private OnVerCombatesClickListener listener;

    public interface OnVerCombatesClickListener {
        void onVerCombatesClick(Torneo torneo);
    }

    public ResultadoAdapter(List<Torneo> torneos) {
        this.torneos = torneos;
    }

    public ResultadoAdapter(List<Torneo> torneos, OnVerCombatesClickListener listener) {
        this.torneos = torneos;
        this.listener = listener;
    }

    public void setOnVerCombatesClickListener(OnVerCombatesClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resultado, parent, false);
        return new ResultadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultadoViewHolder holder, int position) {
        Torneo torneo = torneos.get(position);
        holder.tvNombre.setText(torneo.getNombre());
        holder.tvFecha.setText(torneo.getFechaInicioFormateada());
        holder.tvUbicacion.setText(torneo.getLocalidad());
        holder.tvCategoria.setText(torneo.getArteMarcial());
        
        if (torneo.getGanador() != null && !torneo.getGanador().isEmpty()) {
            holder.tvGanador.setVisibility(View.VISIBLE);
            holder.tvGanador.setText("Ganador: " + torneo.getGanador());
        } else {
            holder.tvGanador.setVisibility(View.GONE);
        }

        // Configurar el botón "Ver Combates"
        holder.btnVerCombates.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerCombatesClick(torneo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return torneos.size();
    }

    public static class ResultadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvFecha, tvUbicacion, tvCategoria, tvGanador;
        Button btnVerCombates;

        public ResultadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvUbicacion = itemView.findViewById(R.id.tvLugar);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvGanador = itemView.findViewById(R.id.tvGanador);
            btnVerCombates = itemView.findViewById(R.id.button2);
        }
    }
} 