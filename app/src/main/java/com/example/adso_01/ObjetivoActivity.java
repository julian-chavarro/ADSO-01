package com.example.adso_01;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class ObjetivoActivity extends AppCompatActivity {

    Spinner spEdad, spPeso, spEstatura, spSexo, spActividad, spObjetivo;
    Button btnCalcular;
    TextView txtCalorias;
    ImageButton btnVolver;

    FirebaseFirestore db;
    FirebaseAuth auth;

    ArrayList<Integer> edades = new ArrayList<>();
    ArrayList<Integer> pesos = new ArrayList<>();
    ArrayList<Integer> estaturas = new ArrayList<>();

    String[] sexo = {"Masculino","Femenino"};
    String[] actividad = {"Ligera","Moderada","Intensa"};
    String[] objetivo = {"Ganar músculo","Perder grasa","Mantener peso"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetivos);

        spEdad = findViewById(R.id.spEdad);
        spPeso = findViewById(R.id.spPeso);
        spEstatura = findViewById(R.id.spEstatura);
        spSexo = findViewById(R.id.spSexo);
        spActividad = findViewById(R.id.spActividad);
        spObjetivo = findViewById(R.id.spObjetivo);

        btnCalcular = findViewById(R.id.btnCalcular);
        txtCalorias = findViewById(R.id.txtCalorias);
        btnVolver = findViewById(R.id.btnVolver);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cargarSpinners();

        cargarDatosUsuario();

        btnCalcular.setOnClickListener(v -> calcularCalorias());

        btnVolver.setOnClickListener(v -> finish());
    }

    private void cargarSpinners(){

        // SEXO
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,sexo);
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSexo.setAdapter(adapterSexo);


        // EDAD
        for(int i=10;i<=100;i++){
            edades.add(i);
        }

        ArrayAdapter<Integer> adapterEdad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,edades);
        adapterEdad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEdad.setAdapter(adapterEdad);


        // PESO
        for(int i=30;i<=200;i++){
            pesos.add(i);
        }

        ArrayAdapter<Integer> adapterPeso = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,pesos);
        adapterPeso.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPeso.setAdapter(adapterPeso);


        // ESTATURA
        for(int i=120;i<=220;i++){
            estaturas.add(i);
        }

        ArrayAdapter<Integer> adapterEstatura = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,estaturas);
        adapterEstatura.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstatura.setAdapter(adapterEstatura);


        // ACTIVIDAD
        ArrayAdapter<String> adapterActividad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,actividad);
        adapterActividad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActividad.setAdapter(adapterActividad);


        // OBJETIVO
        ArrayAdapter<String> adapterObjetivo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,objetivo);
        adapterObjetivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spObjetivo.setAdapter(adapterObjetivo);
    }

    private void calcularCalorias() {

        int edad = Integer.parseInt(spEdad.getSelectedItem().toString());
        int peso = Integer.parseInt(spPeso.getSelectedItem().toString());
        int estatura = Integer.parseInt(spEstatura.getSelectedItem().toString());

        String sexoSeleccionado = spSexo.getSelectedItem().toString();
        String actividadSeleccionada = spActividad.getSelectedItem().toString();
        String objetivoSeleccionado = spObjetivo.getSelectedItem().toString();

        double calorias;

        if(sexoSeleccionado.equals("Masculino")){
            calorias = (10 * peso) + (6.25 * estatura) - (5 * edad) + 5;
        }else{
            calorias = (10 * peso) + (6.25 * estatura) - (5 * edad) - 161;
        }

        switch (actividadSeleccionada){

            case "Ligera":
                calorias *= 1.3;
                break;

            case "Moderada":
                calorias *= 1.5;
                break;

            case "Intensa":
                calorias *= 1.7;
                break;
        }

        if(objetivoSeleccionado.equals("Ganar músculo")){
            calorias += 300;
        }

        if(objetivoSeleccionado.equals("Perder grasa")){
            calorias -= 300;
        }

        int caloriasFinal = (int) calorias;

        txtCalorias.setText(caloriasFinal + " kcal");

        guardarUsuario(edad,peso,estatura,sexoSeleccionado,actividadSeleccionada,objetivoSeleccionado,caloriasFinal);
    }

    private void guardarUsuario(int edad,int peso,int estatura,
                                String sexo,String actividad,
                                String objetivo,int calorias){

        String userId = auth.getCurrentUser().getUid();

        Usuario usuario = new Usuario(
                edad,
                peso,
                estatura,
                sexo,
                actividad,
                objetivo,
                calorias
        );

        db.collection("usuarios")
                .document(userId)
                .set(usuario)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,"Objetivos guardados",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,"Error al guardar",Toast.LENGTH_SHORT).show());
    }


    private void cargarDatosUsuario(){

        String userId = auth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if(documentSnapshot.exists()){

                        Long edad = documentSnapshot.getLong("edad");
                        Long peso = documentSnapshot.getLong("peso");
                        Long estatura = documentSnapshot.getLong("estatura");
                        String sexoGuardado = documentSnapshot.getString("sexo");
                        String actividadGuardada = documentSnapshot.getString("actividad");
                        String objetivoGuardado = documentSnapshot.getString("objetivo");
                        Long calorias = documentSnapshot.getLong("calorias");

                        if(edad != null)
                            spEdad.setSelection(edades.indexOf(edad.intValue()));

                        if(peso != null)
                            spPeso.setSelection(pesos.indexOf(peso.intValue()));

                        if(estatura != null)
                            spEstatura.setSelection(estaturas.indexOf(estatura.intValue()));

                        if(sexoGuardado != null)
                            spSexo.setSelection(buscarIndex(sexo,sexoGuardado));

                        if(actividadGuardada != null)
                            spActividad.setSelection(buscarIndex(actividad,actividadGuardada));

                        if(objetivoGuardado != null)
                            spObjetivo.setSelection(buscarIndex(objetivo,objetivoGuardado));

                        if(calorias != null)
                            txtCalorias.setText("Calorías recomendadas: "+calorias+" kcal");
                    }

                });
    }

    private int buscarIndex(String[] array,String valor){

        for(int i=0;i<array.length;i++){
            if(array[i].equals(valor)){
                return i;
            }
        }

        return 0;
    }
}