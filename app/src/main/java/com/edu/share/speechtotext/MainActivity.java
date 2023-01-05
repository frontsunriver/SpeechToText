package com.edu.share.speechtotext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private final int REQ_CODE_SPEECH = 100;
    private boolean isOnCustomSTT = false;
    private SpeechRecognizer speechRecognizer;
    private Intent intentSpeech;
    private ToggleButton toggleButton;
    private TextView textViewSpeechStatus;
    private RelativeLayout buttonSpeech;
    private TextView textViewSpeechResult;
    private String [] validWordsList = {"yes", "no", "cancel", "restart", "sanitizer", "soap", "dispenser", "level", "please",
                                        "reset", "more soap", "more sanitizer", "demo",
                                        "status", "corporate", "open", "dispenser status",
                                        "dispenser level", "more", "more please",
                                        "help", "tutorial", "clear", "delete", "backspace"};

    private String [] digitsList = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        textViewSpeechStatus = (TextView) findViewById(R.id.textView_speech_status);
        buttonSpeech = (RelativeLayout) findViewById(R.id.button_speech);
        textViewSpeechResult = (TextView) findViewById(R.id.textView_speech_result);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        else {
            init();
        }
    }

    private void init() {
//        customSTT = new CustomSTT(this, mainBinding, Locale.ENGLISH);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle results) {
                String matchString = "";
                String recStr = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                Log.d("Result----------", recStr);
                matchString = getMatchedString(recStr);
                textViewSpeechResult.setText(matchString);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        intentSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

        buttonSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnCustomSTT) {
                    if(speechRecognizer != null && intentSpeech != null) {
                        speechRecognizer.startListening(intentSpeech);
                    }
                }
                else {
                    startNormalSTT(Locale.ENGLISH);
                }
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isOnCustomSTT = isChecked;
                if(!isOnCustomSTT) {
                    if(speechRecognizer != null) {
                        speechRecognizer.stopListening();
                    }
                    textViewSpeechStatus.setText("");
                }
            }
        });
    }

    private void startNormalSTT(Locale language) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        startActivityForResult(intent, REQ_CODE_SPEECH);

    }

//    @Override
//    public void onClick(View v) {
//        if(isOnCustomSTT) {
//            customSTT.startCustomSTT();
//        }
//        else {
//            startNormalSTT(Locale.ENGLISH);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_SPEECH) {
            if(resultCode == RESULT_OK && data != null) {
                String matchString = "";
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Log.d("Result----------", result.get(0));
                matchString = getMatchedString(result.get(0));
//                String [] wordsList = result.get(0).split(" ");
//                for(int i = 0; i < wordsList.length; i++) {
//                    for(int j = 0; j < validWordsList.length; j ++) {
//                        if(wordsList[i].equals(validWordsList[j])) {
//                            matchString += wordsList[i] + " ";
//                        }
//                    }
//                    for(int k = 0; k < digitsList.length; k++) {
//                        if(wordsList[i].equals(digitsList[k])) {
//                            matchString += k + " ";
//                        }
//                    }
//                }
                textViewSpeechResult.setText(matchString);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }
                else {
                    finish();
                }
                break;
        }
    }

//    @Override
//    protected void onDestroy() {
//        if(customSTT != null) {
//            customSTT.stopCustomSTT();
//            customSTT = null;
//        }
//        super.onDestroy();
//    }

    public String getMatchedString (String recStr) {
        String result = "";
        String [] wordsList = recStr.split(" ");
        for(int i = 0; i < wordsList.length; i++) {
            for(int j = 0; j < validWordsList.length; j ++) {
                if(wordsList[i].equals(validWordsList[j])) {
                    result += wordsList[i] + " ";
                }
            }
            for(int k = 0; k < digitsList.length; k++) {
                if(wordsList[i].equals(digitsList[k])) {
                    result += k + " ";
                }
            }
        }
        return result;
    }
}
