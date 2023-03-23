package com.adrianj.tugenius;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConseguirlaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conseguirla);
        Button botonIr = findViewById(R.id.botonWeb);
        botonIr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://platform.openai.com/signup");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });

        String texto = "Para obtener una API Key, sigue estos pasos:\n" +
                "\n" +
                "\t1) Visita el sitio web https://platform.openai.com/signup, (Puedes pulsar el boton superior para ir al sitio web directamente).\n" +
                "\n" +
                "\t2) Crea una cuenta o inicia sesión si ya tienes una (puedes hacerlo rápidamente con tu cuenta de Google).\n" +
                "\n" +
                "\t3) Dirigete a tu usuario (pulsa en las opciones superiores a la derecha y en el menú desplegable al final si lo haces desde el móvil) y elige la opción View API Keys.\n" +
                "\n" +
                "\t4) Pulsa en el boton + Create new secret key.\n" +
                "\n" +
                "\t5) Copia el código generado y vuelve a la apps, en el menú superior derecha hay dos opciones, selecciona API key.\n" +
                "\n" +
                "\t6) Pega la Key dentro del recuadro habilitado para ello y pulsa en guardar.\n" +
                "\n" +
                "\t7) Cierra la aplicación y vuelve a abrirla y listo.\n" +
                "\nMuchas gracias.";

        TextView instrucciones = findViewById(R.id.textoInstrucciones);
        instrucciones.setText(texto);



    }
}