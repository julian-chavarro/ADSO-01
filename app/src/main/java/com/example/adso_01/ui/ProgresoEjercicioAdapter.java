package com.example.adso_01.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adso_01.R;
import com.example.adso_01.model.ProgresoEjercicio;

import java.util.List;

public class ProgresoEjercicioAdapter extends RecyclerView.Adapter<ProgresoEjercicioAdapter.ProgresoViewHolder> {

    private final List<ProgresoEjercicio> progresoList;

    public ProgresoEjercicioAdapter(List<ProgresoEjercicio> progresoList) {
        this.progresoList = progresoList;
    }

    @NonNull
    @Override
    public ProgresoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progreso_ejercicio, parent, false);
        return new ProgresoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgresoViewHolder holder, int position) {
        ProgresoEjercicio p = progresoList.get(position);
        holder.tvNombre.setText(p.getNombreEjercicio());
        holder.tvUltimoPeso.setText(
                holder.itemView.getContext().getString(R.string.progress_last_weight, p.getPesoMaximo()));
        holder.tvMaxPeso.setText(
                holder.itemView.getContext().getString(R.string.progress_max_weight, p.getPesoMaximo()));
        holder.tvVolumenTotal.setText(
                holder.itemView.getContext().getString(R.string.progress_total_volume, p.getVolumenTotal()));
        holder.tvRepsTotales.setText(
                holder.itemView.getContext().getString(R.string.progress_total_reps, p.getRepeticionesTotales()));
    }

    @Override
    public int getItemCount() {
        return progresoList.size();
    }

    static class ProgresoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUltimoPeso, tvMaxPeso, tvVolumenTotal, tvRepsTotales;

        ProgresoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvProgNombre);
            tvUltimoPeso = itemView.findViewById(R.id.tvProgUltimoPeso);
            tvMaxPeso = itemView.findViewById(R.id.tvProgMaxPeso);
            tvVolumenTotal = itemView.findViewById(R.id.tvProgVolumenTotal);
            tvRepsTotales = itemView.findViewById(R.id.tvProgRepsTotales);
        }
    }
}
