package com.example.projeto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.GnssStatus;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class circuloGnssView extends View {
    private GnssStatus novoStatus;
    private int raio;
    private int altura,largura;


    public circuloGnssView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public void onSatelliteStatusChanged(GnssStatus status) {
        novoStatus=status;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        largura=getMeasuredWidth();
        altura=getMeasuredHeight();

        //definição do raio
        if (largura<altura)
            raio=(int)(largura/2*0.9);
        else
            raio=(int)(altura/2*0.9);

        Paint circulo=new Paint();
        circulo.setStyle(Paint.Style.STROKE);
        circulo.setStrokeWidth(5);
        circulo.setColor(Color.BLACK);

        int radius=raio;
        canvas.drawCircle(eixoXc(0), eixoYc(0), radius, circulo);
        radius=(int)(radius*Math.cos(Math.toRadians(45)));
        canvas.drawCircle(eixoXc(0), eixoYc(0), radius, circulo);
        radius=(int)(radius*Math.cos(Math.toRadians(60)));
        canvas.drawCircle(eixoXc(0), eixoYc(0), radius, circulo);

        canvas.drawLine(eixoXc(0),eixoYc(-raio),eixoXc(0),eixoYc(raio),circulo);
        canvas.drawLine(eixoXc(-raio),eixoYc(0),eixoXc(raio),eixoYc(0),circulo);

        circulo.setColor(Color.RED);
        circulo.setStyle(Paint.Style.FILL);

        //mostrando os satélites
        if (novoStatus!=null) {
            for(int i=0;i<novoStatus.getSatelliteCount();i++) {
                float az=novoStatus.getAzimuthDegrees(i);
                float el=novoStatus.getElevationDegrees(i);
                float x=(float)(raio*Math.cos(Math.toRadians(el))*Math.sin(Math.toRadians(az)));
                float y=(float)(raio*Math.cos(Math.toRadians(el))*Math.cos(Math.toRadians(az)));
                canvas.drawCircle(eixoXc(x), eixoYc(y), 10, circulo);
                circulo.setTextAlign(Paint.Align.LEFT);
                circulo.setTextSize(30);
                String satID=novoStatus.getSvid(i)+"";
                canvas.drawText(satID, eixoXc(x)+10, eixoYc(y)+10, circulo);
            }
        }
    }
    private int eixoXc(double x) {
        return (int)(x+largura/2);
    }
    private int eixoYc(double y) {
        return (int)(-y+altura/2);
    }

}
