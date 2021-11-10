package com.example.projeto;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BancoDados extends SQLiteOpenHelper {

    private static final @Nullable int  VERSÃO_BD = 1;
    private static final @Nullable String BANCO_LOCAL = "bd_local";

    private static final @Nullable String TABELA_LOCALIZACAO = "tb_localizacao";
    private static final @Nullable String COLUNA_CODIGO = "codigo";
    private static final @Nullable String COLUNA_LATITUDE = "latitude";
    private static final @Nullable String COLUNA_LONGITUDE = "longitude";
    private static final @Nullable String COLUNA_DATA_HORA = "data_hora";

    public BancoDados(@Nullable Context context) {
        super(context, BANCO_LOCAL, null, VERSÃO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CRIA_TABELA = "CREATE TABLE " + TABELA_LOCALIZACAO + "("
                + COLUNA_CODIGO + " INTEGER PRIMARY KEY, "
                + COLUNA_LATITUDE + " DOUBLE, "
                + COLUNA_LONGITUDE + " DOUBLE, "
                + COLUNA_DATA_HORA + " TEXT)";

        db.execSQL(CRIA_TABELA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public void addLocalizacao(double lat, double longi, String data_hora){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUNA_LATITUDE, lat);
        values.put(COLUNA_LONGITUDE, longi);
        values.put(COLUNA_DATA_HORA, data_hora);
        db.insert(TABELA_LOCALIZACAO, null, values);
        db.close();
    }

    public void deletaBD(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELA_LOCALIZACAO,null,null);
    }

    public List<Local> lerDados(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Local> retorno = new ArrayList<Local>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA_LOCALIZACAO, null);
        if (cursor.moveToFirst()) {
            do{
                Local local = new Local();
                local.setCodigo(Integer.parseInt(cursor.getString(0)));
                local.setLatitude(cursor.getDouble(1));
                local.setLongitude(cursor.getDouble(2));
                local.setData(cursor.getString(3));
                retorno.add(local);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return retorno;
    }
}
