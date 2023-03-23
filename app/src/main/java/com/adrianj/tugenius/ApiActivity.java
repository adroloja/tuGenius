package com.adrianj.tugenius;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adrianj.tugenius.BBDD.BBDD_Helper;
import com.adrianj.tugenius.BBDD.Estructura_BBDD_Api;

public class ApiActivity extends AppCompatActivity {

    BBDD_Helper dbHelper;
    Button botonGuardar;
    EditText entradaApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        entradaApi = findViewById(R.id.entradaApikey);

        dbHelper = new BBDD_Helper(this);

        /*===================================================================================

                                            CARGAR DATOS

         ===================================================================================*/
        String clave = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Api ORDER BY Id DESC LIMIT 1", null);

        if(c.moveToFirst()){

            clave = c.getString(1);
        }

        if(clave != null){   entradaApi.setText(clave);     }

        /*===================================================================================

                                               BOTON GUARDAR

         ===================================================================================*/

        botonGuardar = findViewById(R.id.botonGuardar);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              try{

                  String clave = entradaApi.getText().toString();

                  SQLiteDatabase db = dbHelper.getWritableDatabase();

                  ContentValues v = new ContentValues();
                  v.put(Estructura_BBDD_Api.NOMBRE_COLUMNA1, clave);

                  long newRowId = db.insert(Estructura_BBDD_Api.TABLE_NAME, null, v);
                  Toast.makeText(getApplicationContext(),"Api Key guardada correctamente, reinicia la aplicación, gracias.", Toast.LENGTH_LONG).show();

              }catch (Exception e){

                  Toast.makeText(getApplicationContext(),"Ha ocurrido un error", Toast.LENGTH_LONG).show();
              }
            }
        });


    }
}