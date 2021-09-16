package com.example.projeto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Configuracao extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);
        RadioButton rd_1, rd_2, rd_3;
        RadioButton rdKM, rdMH;
        RadioButton rdNen, rdNorth, rdCourse;
        RadioButton rdVetor, rdImg;
        ToggleButton togInfo;
        togInfo = (ToggleButton) findViewById(R.id.togInfo);
        togInfo.setOnCheckedChangeListener(this);


        //Grupo Coordenada
        rd_1 = (RadioButton) findViewById(R.id.rd_1);
        rd_2 = (RadioButton) findViewById(R.id.rd_2);
        rd_3 = (RadioButton) findViewById(R.id.rd_3);
        rd_1.setOnCheckedChangeListener(this);
        rd_2.setOnCheckedChangeListener(this);
        rd_3.setOnCheckedChangeListener(this);

        //Grupo Unidade
        rdKM = (RadioButton) findViewById(R.id.rdKM);
        rdMH = (RadioButton) findViewById(R.id.rdMH);
        rdKM.setOnCheckedChangeListener(this);
        rdMH.setOnCheckedChangeListener(this);

        //Grupo Orientação
        rdNen = (RadioButton) findViewById(R.id.rdNen);
        rdNorth =(RadioButton) findViewById(R.id.rdNorth);
        rdCourse = (RadioButton) findViewById(R.id.rdCourse);
        rdNen.setOnCheckedChangeListener(this);
        rdNorth.setOnCheckedChangeListener(this);
        rdCourse.setOnCheckedChangeListener(this);

        //Grupo Tipo
        rdVetor = (RadioButton) findViewById(R.id.rdVetor);
        rdImg = (RadioButton) findViewById(R.id.rdImg);
        rdVetor.setOnCheckedChangeListener(this);
        rdImg.setOnCheckedChangeListener(this);
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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



    }
}