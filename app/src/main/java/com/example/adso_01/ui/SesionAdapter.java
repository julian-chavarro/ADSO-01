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

/**
 * Adaptador para el RecyclerView del historial de sesiones en
 * {@link ProgressActivity}.
 * <p>
 * Muestra cada entrenamiento completado con su fecha, cantidad de
 * ejercicios realizados, duración total, y estado (completado / en progreso).
 * La fecha se formatea desde formato ISO ({@code yyyy-MM-dd}) a un formato
 * legible como "lunes, 15 mayo 2026".
 * </p>
 *
 * @see ResumenSesion
 */
public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.SesionViewHolder> {

    /** Lista de resúmenes de sesiones de entrenamiento. */
    private final List<ResumenSesion> sesiones;

    /**
     * Construye el adaptador con la lista de sesiones.
     *
     * @param sesiones Lista de {@link ResumenSesion} a mostrar.
     */
    public SesionAdapter(List<ResumenSesion> sesiones) {
        this.sesiones = sesiones;
    }

    @NonNull
    @Override
    public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sesion, parent, false);
        return new SesionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SesionViewHolder holder, int position) {
        ResumenSesion sesion = sesiones.get(position);

        // Fecha formateada desde ISO yyyy-MM-dd a formato legible
        String fechaFormateada = formatearFecha(sesion.getFecha());
        holder.tvFecha.setText(fechaFormateada);

        // Cantidad de ejercicios realizados
        holder.tvEjercicios.setText(
                holder.itemView.getContext().getString(R.string.progress_exercises_format,
                        sesion.getTotalEjercicios()));

        // Duración calculada a partir de los timestamps de inicio y fin
        if (sesion.getHoraInicio() > 0 && sesion.getHoraFin() > 0) {
            long duracionMs = sesion.getHoraFin() - sesion.getHoraInicio();
            long duracionMin = TimeUnit.MILLISECONDS.toMinutes(duracionMs);
            holder.tvDuracion.setText(
                    holder.itemView.getContext().getString(R.string.progress_duration_format,
                            duracionMin));
        } else {
            holder.tvDuracion.setText("—");
        }

        // Estado visual de la sesión con color indicativo
        holder.tvEstado.setText(sesion.isCompletada()
                ? holder.itemView.getContext().getString(R.string.sesion_completed)
                : holder.itemView.getContext().getString(R.string.sesion_in_progress));
        holder.tvEstado.setTextColor(
                sesion.isCompletada()
                        ? holder.itemView.getContext().getColor(android.R.color.holo_green_dark)
                        : holder.itemView.getContext().getColor(android.R.color.holo_orange_dark));

        // Volumen oculto (campo reservado para uso futuro)
        holder.tvVolumen.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return sesiones.size();
    }

    /**
     * Convierte una fecha en formato ISO ({@code yyyy-MM-dd}) a un formato
     * legible en el locale del dispositivo.
     * <p>
     * Ejemplo: {@code "2026-05-31"} → {@code "domingo, 31 mayo 2026"}.
     * </p>
     *
     * @param fechaYMD Fecha en formato ISO (yyyy-MM-dd).
     * @return Fecha formateada para mostrar, o el valor original si falla el parseo.
     */
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

    /**
     * ViewHolder para una fila de sesión de entrenamiento.
     * Muestra fecha, cantidad de ejercicios, duración, estado,
     * y volumen (oculto actualmente, reservado).
     */
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
