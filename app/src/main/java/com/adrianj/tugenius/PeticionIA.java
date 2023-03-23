package com.adrianj.tugenius;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.adrianj.tugenius.BBDD.BBDD_Helper;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;

import java.util.List;
import java.util.stream.Collectors;

public class PeticionIA extends AsyncTask<Void, Void, String> {

    String pregunta;
    TextView respuesta;
    String api = "";

    public PeticionIA(String pregunta, TextView respuesta, String api){

        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.api = api;
    }
    @Override
    protected String doInBackground(Void... voids) {

        try{

            // gpt-3.5-turbo        "text-davinci-003"
            OpenAiService s = new OpenAiService(api);
            CompletionRequest c = CompletionRequest.builder()
                    .prompt(pregunta)
                    .model("text-davinci-003")
                    .maxTokens(1900)
                    .echo(true)
                    .build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                s.createCompletion(c).getChoices().forEach(System.out::println);

                List<CompletionChoice> lista = s.createCompletion(c).getChoices();

                MainActivity.textoRespuesta = "";
                String texto = "";

                for(CompletionChoice r : lista){

                    MainActivity.textoRespuesta += r.getText().toString() + "\n";
                    texto += r.getText().toString() + "\n";
                    Log.d("Hola", MainActivity.textoRespuesta);
                }
            }

        }catch (Exception e){

                respuesta.setText("Upss, ha ocurrido un error por favor inténtelo de nuevo");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

       if(MainActivity.textoRespuesta.length() > 3){

           respuesta.setText(MainActivity.textoRespuesta);
       }else{

           respuesta.setText("Error, por favor pruebe otra pregunta o intentelo otra vez");
       }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }
}
