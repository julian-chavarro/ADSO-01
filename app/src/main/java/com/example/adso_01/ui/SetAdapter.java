package com.example.adso_01.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adso_01.R;
import com.example.adso_01.model.Serie;

import java.util.List;

/**
 * Adaptador para el RecyclerView de series dentro de un ejercicio activo.
 * <p>
 * Muestra cada serie individual con su número, repeticiones objetivo,
 * campo de peso ingresado por el usuario, y botón "Completar".
 * Al completar una serie, notifica al listener ({@link OnSetCompleteListener})
 * que dispara el guardado en Firestore a través del ViewModel.
 * </p>
 *
 * <h3>Estados visuales:</h3>
 * <ul>
 *   <li><b>Pendiente:</b> botón "Completar" habilitado, fondo normal.</li>
 *   <li><b>Completada:</b> botón "✓ Listo" deshabilitado, fondo verde.</li>
 * </ul>
 */
public class SetAdapter extends RecyclerView.Adapter<SetAdapter.SetViewHolder> {

    /** Lista de series del ejercicio actual. */
    private final List<Serie> series;

    /** Listener para notificar cuando una serie es completada. */
    private final OnSetCompleteListener listener;

    /**
     * Callback invocado cuando el usuario completa una serie.
     * Recibe la serie y el peso ingresado (como String) para que
     * el ViewModel lo guarde en Firestore.
     */
    public interface OnSetCompleteListener {
        void onComplete(Serie serie, String weight);
    }

    /**
     * Construye el adaptador con la lista de series y el listener de completado.
     *
     * @param series   Lista de series del ejercicio actual.
     * @param listener Listener para guardar la serie al completarla.
     */
    public SetAdapter(List<Serie> series, OnSetCompleteListener listener) {
        this.series = series;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_set, parent, false);
        return new SetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        Serie serie = series.get(position);

        // Mostrar número de serie y repeticiones objetivo
        holder.tvSetNumber.setText(String.valueOf(serie.getNumero()));
        holder.tvTargetReps.setText(String.valueOf(serie.getRepeticiones()));

        // Rellenar peso si ya fue ingresado previamente
        holder.etWeight.setText(serie.getPeso() > 0 ? String.valueOf(serie.getPeso()) : "");

        // Aplicar estilo según estado de completado
        if (serie.isCompletada()) {
            holder.btnComplete.setText(holder.itemView.getContext().getString(R.string.set_done));
            holder.btnComplete.setEnabled(false);
            holder.layoutSetRow.setBackgroundResource(R.drawable.set_row_completed_background);
        } else {
            holder.btnComplete.setText(holder.itemView.getContext().getString(R.string.set_complete));
            holder.btnComplete.setEnabled(true);
            holder.layoutSetRow.setBackgroundResource(R.drawable.set_row_background);
        }

        // Botón para marcar la serie como completada
        holder.btnComplete.setOnClickListener(v -> {
            String weightStr = holder.etWeight.getText().toString().trim();
            listener.onComplete(serie, weightStr);
        });
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    /**
     * ViewHolder para una fila de serie individual.
     * Contiene el número de serie, repeticiones objetivo,
     * campo de texto para el peso, botón de completado,
     * y el contenedor principal para el background.
     */
    static class SetViewHolder extends RecyclerView.ViewHolder {
        TextView tvSetNumber, tvTargetReps;
        EditText etWeight;
        Button btnComplete;
        View layoutSetRow;

        SetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetNumber = itemView.findViewById(R.id.tvSetNumber);
            tvTargetReps = itemView.findViewById(R.id.tvTargetReps);
            etWeight = itemView.findViewById(R.id.etWeight);
            btnComplete = itemView.findViewById(R.id.btnCompleteSet);
            layoutSetRow = itemView.findViewById(R.id.layoutSetRow);
        }
    }
}
