package com.example.projeto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;


public class Configuracao extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
private SharedPreferences sharedPrefs;
private SharedPreferences.Editor sharedPrefsEditor;
private RadioButton rd_1, rd_2, rd_3, rdKM, rdMH,rdNen, rdNorth, rdCourse, rdVetor, rdImg;
private SwitchCompat switchInfo;
private boolean confere2, confere3, unidadeMh, orient2, orient3, tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_configuracao);
        sharedPrefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        //Info
        switchInfo = (SwitchCompat) findViewById(R.id.switchInfo);
        switchInfo.setOnCheckedChangeListener(this);
        switchInfo.setChecked(sharedPrefs.getBoolean("Informacao",false));

        //Grupo Coordenada
        rd_1 = (RadioButton) findViewById(R.id.rd_1);
        rd_2 = (RadioButton) findViewById(R.id.rd_2);
        rd_3 = (RadioButton) findViewById(R.id.rd_3);
        rd_1.setOnCheckedChangeListener(this);
        rd_2.setOnCheckedChangeListener(this);
        rd_3.setOnCheckedChangeListener(this);

        confere2 = sharedPrefs.getBoolean("Coordenada_2", false);
        confere3 = sharedPrefs.getBoolean("Coordenada_3", false);

        //testa se há alguma opção ativa, caso não, a opção rd_1 é padrão
        if(confere2 == false && confere3 == false){
            rd_1.setChecked(true);
        }

        if(confere2 == true){
            rd_2.setChecked(true);
        }

        if(confere3 == true){
            rd_3.setChecked(true);
        }


        //Grupo Unidade
        rdKM = (RadioButton) findViewById(R.id.rdKM);
        rdMH = (RadioButton) findViewById(R.id.rdMH);
        rdKM.setOnCheckedChangeListener(this);
        rdMH.setOnCheckedChangeListener(this);


        unidadeMh = sharedPrefs.getBoolean("MH", false);

        //testa se há alguma opção ativa, caso não, a opção rdKM é padrão
        if(unidadeMh == false){
            rdKM.setChecked(true);
        }
        else{
            rdMH.setChecked(true);
        }

        //Grupo Orientação
        rdNen = (RadioButton) findViewById(R.id.rdNen);
        rdNorth =(RadioButton) findViewById(R.id.rdNorth);
        rdCourse = (RadioButton) findViewById(R.id.rdCourse);
        rdNen.setOnCheckedChangeListener(this);
        rdNorth.setOnCheckedChangeListener(this);
        rdCourse.setOnCheckedChangeListener(this);

        orient2 = sharedPrefs.getBoolean("North", false);
        orient3 = sharedPrefs.getBoolean("Course", false);

        //testa se há alguma opção ativa, caso não, a opção rdNen é padrão
        if(orient2 == false && orient3 == false){
            rdNen.setChecked(true);
        }
        if(orient2 == true){
            rdNorth.setChecked(true);
        }
        if(orient3 == true){
            rdCourse.setChecked(true);
        }

        //Grupo Tipo
        rdVetor = (RadioButton) findViewById(R.id.rdVetor);
        rdImg = (RadioButton) findViewById(R.id.rdImg);
        rdVetor.setOnCheckedChangeListener(this);
        rdImg.setOnCheckedChangeListener(this);

        tipo = sharedPrefs.getBoolean("Imagem", false);

        //testa se há alguma opção ativa, caso não, a opção rdVetor é padrão
        if(tipo == false){
            rdVetor.setChecked(true);
        }
        else{
            rdImg.setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /*
        if(buttonView.getId()==R.id.switchInfo && buttonView.isChecked())
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

            if(switchInfo.isChecked()){
                sharedPrefsEditor.putBoolean("Informacao", true);
            }
            if(!switchInfo.isChecked()){
                sharedPrefsEditor.putBoolean("Informacao", false);
            }

            //Grupo Coordenada
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

            //Grupo unidade
            if(rdKM.isChecked()){
                sharedPrefsEditor.putBoolean("KM", true);
                sharedPrefsEditor.putBoolean("MH", false);
            }
            if(rdMH.isChecked()){
                sharedPrefsEditor.putBoolean("KM", false);
                sharedPrefsEditor.putBoolean("MH", true);
            }

            //Grupo orientação
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

            //Grupo tipo
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