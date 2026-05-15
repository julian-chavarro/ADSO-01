package com.example.adso_01.ui;

import android.graphics.Color;
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

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.SetViewHolder> {

    private final List<Serie> series;
    private final OnSetCompleteListener listener;

    public interface OnSetCompleteListener {
        void onComplete(Serie serie, String weight);
    }

    public SetAdapter(List<Serie> series, OnSetCompleteListener listener) {
        this.series = series;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set, parent, false);
        return new SetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        Serie serie = series.get(position);
        holder.tvSetNumber.setText(String.valueOf(serie.getNumero()));
        holder.tvTargetReps.setText(String.valueOf(serie.getRepeticiones()));
        holder.etWeight.setText(serie.getPeso() > 0 ? String.valueOf(serie.getPeso()) : "");

        if (serie.isCompletada()) {
            holder.btnComplete.setText("✓ Listo");
            holder.btnComplete.setEnabled(false);
            holder.layoutSetRow.setBackgroundResource(R.drawable.set_row_completed_background);
        } else {
            holder.btnComplete.setText("Completar");
            holder.btnComplete.setEnabled(true);
            holder.layoutSetRow.setBackgroundResource(R.drawable.set_row_background);
        }

        holder.btnComplete.setOnClickListener(v -> {
            String weightStr = holder.etWeight.getText().toString();
            listener.onComplete(serie, weightStr);
        });
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    static class SetViewHolder extends RecyclerView.ViewHolder {
        TextView tvSetNumber, tvTargetReps;
        EditText etWeight;
        Button btnComplete;
        View layoutSetRow;

        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetNumber = itemView.findViewById(R.id.tvSetNumber);
            tvTargetReps = itemView.findViewById(R.id.tvTargetReps);
            etWeight = itemView.findViewById(R.id.etWeight);
            btnComplete = itemView.findViewById(R.id.btnCompleteSet);
            layoutSetRow = itemView.findViewById(R.id.layoutSetRow);
        }
    }
}
