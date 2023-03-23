package com.adrianj.tugenius;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.adrianj.tugenius.BBDD.BBDD_Helper;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.edit.EditRequest;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    String model = "text-davinci-003";

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static String API_KEY = "";
    private SpeechRecognizer speechRecognizer;

    TextToSpeech t1;
    static String textoRespuesta = "";
    String texto;
    static TextView respuesta;
    MultiAutoCompleteTextView pregunta;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkRecordPermission();
        checkInternetPermission();

        /*================================================================================

                                    CARGAR LA API DE LA BBDD

         ==================================================================================*/

        BBDD_Helper dbHelper = new BBDD_Helper(this);
        String clave = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Api ORDER BY Id DESC LIMIT 1", null);

        if(c.moveToFirst()){

            clave = c.getString(1);
        }

        if(clave != null){   API_KEY = clave;     }

        /*================================================================================

                               INICIA EL SPEECHRECOGNIZER

         ================================================================================*/

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        tts = new TextToSpeech(this, this);
        tts.setLanguage(new Locale("es", "ES"));

         /*================================================================================

                                        VIEW Y BOTONES

        ==================================================================================*/
        pregunta = findViewById(R.id.pregunta);
        respuesta = findViewById(R.id.respuesta);

        // con esta función también podemos hacer la petición a la api de openAI pero más personalizada
        //new OpenAICompletionTask(respuesta, "Receta de macarrones con tomate y atun").execute();

        Button botonGrabar = findViewById(R.id.botonGrabar);
        botonGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasarAudioATexto();
            }
        });

        /*===============================================================================

                               BOTÓN QUE MANDA PETICIÓN A LA API

         ===============================================================================*/

        Button boton = findViewById(R.id.boton);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(getApplicationContext(), "Espera mientras pienso tu respuesta", Toast.LENGTH_LONG).show();

                texto = pregunta.getText().toString();

                PeticionIA p = new PeticionIA(texto, respuesta, API_KEY);
                p.execute();

            }
        });


        /*===============================================================================

                               BOTÓN QUE REPRODUCE LA RESPUESTA

         ===============================================================================*/

        Button botonReproducir = findViewById(R.id.botonReproducir);
        botonReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        String textoRepro = respuesta.getText().toString();
                        int longPregunta = texto.length();
                        String textoTratado = textoRepro.substring(longPregunta);

                        tts.speak(textoTratado, TextToSpeech.QUEUE_FLUSH, null);

            }
        });

        /*===============================================================================

                                MENSAJE CUANDO NO HAY API DEFINIDA

         ===============================================================================*/

        if(clave == null){
            respuesta.setText("Antes de nada, pincha en el menú superior derecha e introduce la API Key, puedes seguir las instrucciones de la opción: Como conseguirla");
        }
    }

    /*===============================================================================

                                        PERMISOS

     ===============================================================================*/

    private void checkRecordPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 225);
        }
    }
    private void checkInternetPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 225);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    /*===============================================================================

                               MENU OPCIONES SUPERIOR

     ===============================================================================*/
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.apiKey:

                startActivity(new Intent(getApplicationContext(), ApiActivity.class));
                return true;

            case R.id.comoConseguirla:

                startActivity(new Intent(getApplicationContext(), ConseguirlaActivity.class));
                return true;
        }

        return false;
    }

    /*===============================================================================

                           INICIALIZACION MOTOR DE VOZ

      ===============================================================================*/
    @Override
    public void onInit(int i) {

        if(i != TextToSpeech.ERROR){
            tts.setLanguage(new Locale("es","ES"));
        }else{
            Toast.makeText(this, "Fallo en la inicialización del motor de síntesis de voz", Toast.LENGTH_SHORT).show();
        }
    }

    public void pasarAudioATexto() {

        // Configura el objeto SpeechRecognizer para utilizar el reconocimiento de voz de Google
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di algo...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Lo siento, el reconocimiento de voz no está disponible en tu dispositivo.", Toast.LENGTH_SHORT).show();
        }

        // Agrega un RecognitionListener al objeto SpeechRecognizer para procesar los resultados del reconocimiento de voz
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // El dispositivo está listo para escuchar
            }

            @Override
            public void onBeginningOfSpeech() {
                // El usuario ha comenzado a hablar
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // El nivel de volumen del habla ha cambiado
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // El dispositivo está recibiendo audio
            }

            @Override
            public void onEndOfSpeech() {
                // El usuario ha dejado de hablar
            }

            @Override
            public void onError(int error) {
                // Ha ocurrido un error durante el reconocimiento de voz
            }

            @Override
            public void onResults(Bundle results) {
                // Se han recibido resultados del reconocimiento de voz
                ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults != null) {
                    // Obtiene el primer resultado de la lista y lo muestra en un TextView
                    String text = voiceResults.get(0);
                    //TextView textView = findViewById(R.id.textView);
                    //textView.setText(text);
                    pregunta.setText(text);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // Se han recibido resultados parciales del reconocimiento de voz
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Ha ocurrido un evento durante el reconocimiento de voz
            }
        });
    }

    /*===============================================================================

                 CODIGO PARA PASAR DE VOZ A TEXTO Y MOSTRARLO EN UN TEXTVIEW

      ===============================================================================*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Procesa los resultados del reconocimiento de voz
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> voiceResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (voiceResults != null) {

                String text = voiceResults.get(0);

                pregunta.setText(text);

                // Utiliza el resultado del reconocimiento de voz
                procesarTexto(text);
            }
        }
    }

    private void procesarTexto(String text) {
        // Aquí puedes agregar el código para procesar el texto reconocido por voz
    }

}