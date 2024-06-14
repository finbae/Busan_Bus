package com.cookandroid.myproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class activity_sub extends AppCompatActivity {
    Intent intent;
    SpeechRecognizer speechRecognizer;
    final int PERMISSION = 1;

    boolean recording = false;
    TextView recordTextView;
    EditText contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        final Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(10000,100));

        CheckPermission();

        //UI
        recordTextView=findViewById(R.id.recordTextView);
        contents=findViewById(R.id.contentsTextView);

        //RecognizerIntent 객체 생성
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");   //한국어

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);
        speechRecognizer.stopListening();


    }

    // 음성 인식 리스너
    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            StartRecord();
            // 사용자의 음성 인식 준비가 완료되었을 때 처리할 작업
            Toast.makeText(getApplicationContext(),"음성인식 시작", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
            // 사용자가 말하기 시작할 때 처리할 작업
        }

        @Override
        public void onRmsChanged(float v) {
            // 음성의 RMS 값이 변할 때 처리할 작업
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            // 음성 입력이 버퍼로 전달될 때 처리할 작업
        }

        @Override
        public void onEndOfSpeech() {
            // 사용자가 말하기를 멈췄을 때 처리할 작업
            StopRecord();
        }

        @Override
        public void onError(int error) {
            speechRecognizer.stopListening();   //녹음 중지
            StopRecord();
//             음성 인식 오류 발생 시 처리할 작업
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    speechRecognizer.stopListening();   //녹음 중지

                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    speechRecognizer.stopListening();   //녹음 중지
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    speechRecognizer.stopListening();   //녹음 중지
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    speechRecognizer.stopListening();   //녹음 중지
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    speechRecognizer.stopListening();   //녹음 중지
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    speechRecognizer.stopListening();   //녹음 중지
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER 가 바쁨";
                    speechRecognizer.stopListening();   //녹음 중지
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    speechRecognizer.stopListening();   //녹음 중지
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    speechRecognizer.stopListening();   //녹음 중지
                    break;
                default:
                    message = "알 수 없는 오류임";
                    speechRecognizer.stopListening();   //녹음 중지
                    break;

            }

            Toast.makeText(getApplicationContext(), "에러 발생 : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < matches.size() ; i++){
                contents.setText(matches.get(i));
            }

        }

        @Override
        public void onPartialResults(Bundle bundle) {
            // 부분적인 음성 인식 결과가 준비되었을 때 처리할 작업
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            // 이벤트가 발생했을 때 처리할 작업
        }
    };

    //녹음 시작
    void StartRecord() {
        recording = true;

        recordTextView.setText("음성 녹음 중지");

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);
    }

    //녹음 중지
    void StopRecord() {
        recording = false;
        //마이크 이미지와 텍스트 변경
        recordTextView.setText("음성 녹음 시작");

        speechRecognizer.stopListening();   //녹음 중지
        Toast.makeText(getApplicationContext(), "음성 기록을 중지합니다.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(activity_sub.this, MainActivity.class));
    }

    // 퍼미션 체크
    void CheckPermission() {
        // 안드로이드 버전이 6.0 이상인 경우
        if (Build.VERSION.SDK_INT >= 23) {
            // 인터넷 및 녹음 권한이 없는 경우 권한 요청
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.INTERNET,
                                android.Manifest.permission.RECORD_AUDIO}, PERMISSION);
            }
        }
    }

}
