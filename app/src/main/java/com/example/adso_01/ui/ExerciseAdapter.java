package com.example.adso_01.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adso_01.R;
import com.example.adso_01.model.Ejercicio;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<Ejercicio> exercises;
    private final OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onExerciseClick(Ejercicio ejercicio);
    }

    public ExerciseAdapter(List<Ejercicio> exercises, OnExerciseClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Ejercicio exercise = exercises.get(position);
        holder.tvName.setText(exercise.getNombre());
        holder.tvDetails.setText(exercise.getSeriesObjetivo() + " series - " + exercise.getGrupoMuscular());
        
        holder.itemView.setOnClickListener(v -> listener.onExerciseClick(exercise));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;
        CheckBox cbDone;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvExerciseName);
            tvDetails = itemView.findViewById(R.id.tvExerciseDetails);
            cbDone = itemView.findViewById(R.id.cbExerciseDone);
        }
    }
}
