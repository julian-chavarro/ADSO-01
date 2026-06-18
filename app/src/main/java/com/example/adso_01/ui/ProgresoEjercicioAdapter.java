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

/**
 * Adaptador para el RecyclerView de progreso por ejercicio en
 * {@link ProgressActivity}.
 * <p>
 * Muestra las métricas agregadas de cada ejercicio: nombre,
 * peso máximo alcanzado, volumen total acumulado, y repeticiones
 * totales realizadas. Los datos provienen de la lista agregada
 * por {@link ProgressActivity#agregarProgreso(List)}.
 * </p>
 *
 * @see ProgresoEjercicio
 */
public class ProgresoEjercicioAdapter
        extends RecyclerView.Adapter<ProgresoEjercicioAdapter.ProgresoViewHolder> {

    /** Lista de progreso agregado por ejercicio. */
    private final List<ProgresoEjercicio> progresoList;

    /**
     * Construye el adaptador con la lista de progreso de ejercicios.
     *
     * @param progresoList Lista agregada de {@link ProgresoEjercicio}.
     */
    public ProgresoEjercicioAdapter(List<ProgresoEjercicio> progresoList) {
        this.progresoList = progresoList;
    }

    @NonNull
    @Override
    public ProgresoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progreso_ejercicio, parent, false);
        return new ProgresoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgresoViewHolder holder, int position) {
        ProgresoEjercicio p = progresoList.get(position);

        // Nombre del ejercicio
        holder.tvNombre.setText(p.getNombreEjercicio());

        // Métricas: peso máximo, volumen total, repeticiones totales
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

    /**
     * ViewHolder para una fila de progreso de ejercicio.
     * Muestra nombre, último peso, peso máximo, volumen total
     * y repeticiones totales del ejercicio.
     */
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
