package com.example.adso_01.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adso_01.R;
import com.example.adso_01.model.ResumenSesion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.SesionViewHolder> {

    private final List<ResumenSesion> sesiones;

    public SesionAdapter(List<ResumenSesion> sesiones) {
        this.sesiones = sesiones;
    }

    @NonNull
    @Override
    public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sesion, parent, false);
        return new SesionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SesionViewHolder holder, int position) {
        ResumenSesion sesion = sesiones.get(position);

        String fechaFormateada = formatearFecha(sesion.getFecha());
        holder.tvFecha.setText(fechaFormateada);

        holder.tvEjercicios.setText(
                holder.itemView.getContext().getString(R.string.progress_exercises_format,
                        sesion.getTotalEjercicios()));

        if (sesion.getHoraInicio() > 0 && sesion.getHoraFin() > 0) {
            long duracionMs = sesion.getHoraFin() - sesion.getHoraInicio();
            long duracionMin = TimeUnit.MILLISECONDS.toMinutes(duracionMs);
            holder.tvDuracion.setText(
                    holder.itemView.getContext().getString(R.string.progress_duration_format,
                            duracionMin));
        } else {
            holder.tvDuracion.setText("—");
        }

        holder.tvEstado.setText(sesion.isCompletada() ? "Completado" : "En progreso");
        holder.tvEstado.setTextColor(
                sesion.isCompletada() ?
                        holder.itemView.getContext().getColor(android.R.color.holo_green_dark) :
                        holder.itemView.getContext().getColor(android.R.color.holo_orange_dark));

        holder.tvVolumen.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return sesiones.size();
    }

    private String formatearFecha(String fechaYMD) {
        if (fechaYMD == null || fechaYMD.isEmpty()) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(fechaYMD);
            SimpleDateFormat output = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
            return output.format(date);
        } catch (Exception e) {
            return fechaYMD;
        }
    }

    static class SesionViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvEjercicios, tvDuracion, tvEstado, tvVolumen;

        SesionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvSesionFecha);
            tvEjercicios = itemView.findViewById(R.id.tvSesionEjercicios);
            tvDuracion = itemView.findViewById(R.id.tvSesionDuracion);
            tvEstado = itemView.findViewById(R.id.tvSesionEstado);
            tvVolumen = itemView.findViewById(R.id.tvSesionVolumen);
        }
    }
}
