package com.adrianj.tugenius;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.*;

public class PeticionPersonalizada extends AsyncTask<String, Void, String> {
    // Introducir api key o cargarla de la BBDD
    private static final String OPENAI_API_KEY = "";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    String responseBody;
    TextView respuesta;
    String prompt;
    public PeticionPersonalizada(TextView respuesta, String prompt) {

        this.respuesta = respuesta;
        this.prompt = prompt;
    }

    protected String doInBackground(String... prompts) {

        String url = "https://api.openai.com/v1/completions";
        String json = "{\"prompt\":\"" + prompt + "\"," +
                "\"temperature\":0.7," +
                "\"model\":\"text-davinci-003\"," +
                "\"max_tokens\":1024}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            responseBody = response.body().string();
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(String result) {
        // Do something with the completion result

        if(result != null){

            Gson textoArchivo = new Gson();
            //textoArchivo.toJson(result); esto pasa un string a json
            Object o = textoArchivo.fromJson(result, Object.class);
            String texto = ((Map<String, Object>) o).get("choices").toString();


            Log.d("OpenAI", texto);
            respuesta.setText(texto);
        }else{

            Log.d("OpenAI", "resultado nulo");
        }
       }

    }
