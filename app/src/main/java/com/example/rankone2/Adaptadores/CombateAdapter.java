package com.example.rankone2.Adaptadores;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rankone2.Modelo.Combate;
import com.example.rankone2.R;

import java.util.List;

public class CombateAdapter extends RecyclerView.Adapter<CombateAdapter.CombateViewHolder> {

    private List<Combate> combates;
    private int userId;

    public CombateAdapter(List<Combate> combates, int userId) {
        this.combates = combates;
        this.userId = userId;
    }

    @NonNull
    @Override
    public CombateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_combate, parent, false);
        return new CombateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CombateViewHolder holder, int position) {
        Combate combate = combates.get(position);
        
        // Establecer la fase del combate
        holder.tvFaseCombate.setText(combate.getFase());
        
        // Establecer los luchadores
        holder.tvLuchadores.setText(combate.getLuchadoresVs());
        
        // Establecer el ganador
        if (combate.getNombreGanador() != null && !combate.getNombreGanador().isEmpty()) {
            holder.tvGanadorCombate.setText("Ganador: " + combate.getNombreGanador());
            
            // Resaltar si el usuario actual es el ganador
            if (combate.esGanador(userId)) {
                holder.tvGanadorCombate.setTextColor(Color.parseColor("#4CAF50")); // Verde
            } else {
                holder.tvGanadorCombate.setTextColor(Color.parseColor("#F44336")); // Rojo
            }
        } else {
            holder.tvGanadorCombate.setText("Sin determinar");
            holder.tvGanadorCombate.setTextColor(Color.GRAY);
        }
        
        // Establecer el resultado
        if (combate.getResultado() != null && !combate.getResultado().isEmpty()) {
            holder.tvResultado.setText(combate.getResultado());
        } else {
            holder.tvResultado.setText("Resultado pendiente");
        }

        // Cambiar el fondo de la tarjeta si el usuario participó en este combate
        if (combate.participaUsuario(userId)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E3F2FD")); // Azul claro
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return combates.size();
    }

    public static class CombateViewHolder extends RecyclerView.ViewHolder {
        TextView tvFaseCombate, tvLuchadores, tvGanadorCombate, tvResultado;

        public CombateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFaseCombate = itemView.findViewById(R.id.tvFaseCombate);
            tvLuchadores = itemView.findViewById(R.id.tvLuchadores);
            tvGanadorCombate = itemView.findViewById(R.id.tvGanadorCombate);
            tvResultado = itemView.findViewById(R.id.tvResultado);
        }
    }
} 