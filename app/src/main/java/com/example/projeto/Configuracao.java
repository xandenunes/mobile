package com.example.projeto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Configuracao extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
private SharedPreferences sharedPrefs;
private SharedPreferences.Editor sharedPrefsEditor;
private RadioButton rd_1, rd_2, rd_3, rdKM, rdMH,rdNen, rdNorth, rdCourse, rdVetor, rdImg;
private ToggleButton togInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);
        sharedPrefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        //Info
        togInfo = (ToggleButton) findViewById(R.id.togInfo);
        togInfo.setOnCheckedChangeListener(this);

        togInfo.setChecked(sharedPrefs.getBoolean("Informacao",false));

        //Grupo Coordenada
        rd_1 = (RadioButton) findViewById(R.id.rd_1);
        rd_2 = (RadioButton) findViewById(R.id.rd_2);
        rd_3 = (RadioButton) findViewById(R.id.rd_3);
        rd_1.setOnCheckedChangeListener(this);
        rd_2.setOnCheckedChangeListener(this);
        rd_3.setOnCheckedChangeListener(this);

        rd_1.setChecked(sharedPrefs.getBoolean("Coordenada_1", false));
        rd_2.setChecked(sharedPrefs.getBoolean("Coordenada_2", false));
        rd_3.setChecked(sharedPrefs.getBoolean("Coordenada_3", false));


        //Grupo Unidade
        rdKM = (RadioButton) findViewById(R.id.rdKM);
        rdMH = (RadioButton) findViewById(R.id.rdMH);
        rdKM.setOnCheckedChangeListener(this);
        rdMH.setOnCheckedChangeListener(this);

        rdKM.setChecked(sharedPrefs.getBoolean("KM", false));
        rdMH.setChecked(sharedPrefs.getBoolean("MH", false));


        //Grupo Orientação
        rdNen = (RadioButton) findViewById(R.id.rdNen);
        rdNorth =(RadioButton) findViewById(R.id.rdNorth);
        rdCourse = (RadioButton) findViewById(R.id.rdCourse);
        rdNen.setOnCheckedChangeListener(this);
        rdNorth.setOnCheckedChangeListener(this);
        rdCourse.setOnCheckedChangeListener(this);

        rdNen.setChecked(sharedPrefs.getBoolean("Nenhum", false));
        rdNorth.setChecked(sharedPrefs.getBoolean("North", false));
        rdCourse.setChecked(sharedPrefs.getBoolean("Course", false));

        //Grupo Tipo
        rdVetor = (RadioButton) findViewById(R.id.rdVetor);
        rdImg = (RadioButton) findViewById(R.id.rdImg);
        rdVetor.setOnCheckedChangeListener(this);
        rdImg.setOnCheckedChangeListener(this);

        rdVetor.setChecked(sharedPrefs.getBoolean("Vetor", false));
        rdImg.setChecked(sharedPrefs.getBoolean("Imagem", false));

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /*
        if(buttonView.getId()==R.id.togInfo && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu Ligado", Toast.LENGTH_SHORT).show();

        //Grupo Coordenada
        if(buttonView.getId()==R.id.rd_1 && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu DDDºDDDDD", Toast.LENGTH_SHORT).show();

        if(buttonView.getId()==R.id.rd_2 && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu DDDº MM.MMM", Toast.LENGTH_SHORT).show();

        if(buttonView.getId()==R.id.rd_3 && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu DDDº MM' SS", Toast.LENGTH_SHORT).show();

        //Grupo Unidade
        if(buttonView.getId()==R.id.rdKM && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu KM/h", Toast.LENGTH_SHORT).show();

        if(buttonView.getId()==R.id.rdMH && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu Mph", Toast.LENGTH_SHORT).show();

        //Grupo Orientação
        if(buttonView.getId()==R.id.rdNen && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu Nenhuma", Toast.LENGTH_SHORT).show();

        if(buttonView.getId()==R.id.rdNorth && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu North", Toast.LENGTH_SHORT).show();

        if(buttonView.getId()==R.id.rdCourse && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu Course", Toast.LENGTH_SHORT).show();

        //Grupo Tipo
        if(buttonView.getId()==R.id.rdVetor && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu Vetorial", Toast.LENGTH_SHORT).show();

        if(buttonView.getId()==R.id.rdImg && buttonView.isChecked())
            Toast.makeText(this, "Você escolheu Imagem de Satélite", Toast.LENGTH_SHORT).show();

         */
    }

    @Override
    protected void onPause(){
        super.onPause();
        sharedPrefsEditor = sharedPrefs.edit();
        if(sharedPrefsEditor!=null){
            if(togInfo.isChecked()){
                sharedPrefsEditor.putBoolean("Informacao", true);
            }
            if(!togInfo.isChecked()){
                sharedPrefsEditor.putBoolean("Informacao", false);
            }
            if(rd_1.isChecked()){
                sharedPrefsEditor.putBoolean("Coordenada_1", true);
                sharedPrefsEditor.putBoolean("Coordenada_2", false);
                sharedPrefsEditor.putBoolean("Coordenada_3", false);
            }

            if(rd_2.isChecked()) {
                sharedPrefsEditor.putBoolean("Coordenada_1", false);
                sharedPrefsEditor.putBoolean("Coordenada_2", true);
                sharedPrefsEditor.putBoolean("Coordenada_3", false);
            }

            if(rd_3.isChecked()) {
                sharedPrefsEditor.putBoolean("Coordenada_1", false);
                sharedPrefsEditor.putBoolean("Coordenada_2", false);
                sharedPrefsEditor.putBoolean("Coordenada_3", true);
            }

            if(rdKM.isChecked()){
                sharedPrefsEditor.putBoolean("KM", true);
                sharedPrefsEditor.putBoolean("MH", false);
            }

            if(rdMH.isChecked()){
                sharedPrefsEditor.putBoolean("KM", false);
                sharedPrefsEditor.putBoolean("MH", true);
            }

            if(rdNen.isChecked()){
                sharedPrefsEditor.putBoolean("Nenhum", true);
                sharedPrefsEditor.putBoolean("North", false);
                sharedPrefsEditor.putBoolean("Course", false);
            }

            if(rdNorth.isChecked()){
                sharedPrefsEditor.putBoolean("Nenhum", false);
                sharedPrefsEditor.putBoolean("North", true);
                sharedPrefsEditor.putBoolean("Course", false);
            }

            if(rdCourse.isChecked()){
                sharedPrefsEditor.putBoolean("Nenhum", false);
                sharedPrefsEditor.putBoolean("North", false);
                sharedPrefsEditor.putBoolean("Course", true);
            }

            if(rdVetor.isChecked()){
                sharedPrefsEditor.putBoolean("Vetor", true);
                sharedPrefsEditor.putBoolean("Imagem", false);
            }

            if(rdImg.isChecked()){
                sharedPrefsEditor.putBoolean("Vetor", false);
                sharedPrefsEditor.putBoolean("Imagem", true);
            }
            sharedPrefsEditor.commit();
        }
    }
}