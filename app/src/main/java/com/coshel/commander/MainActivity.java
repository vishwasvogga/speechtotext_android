package com.coshel.commander;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CAudioRecorderInterface {

    Button btnRecorder;
    final String TAG="Main activity";
    int retryAudioPermissionLimit=3;
    boolean isSpeakButtonLongPressed= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialise UI refrenses
        initialiseuUi();
        //initialise UI controls
        initialiseUiControls();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check audio permissions
        audioPermissionCheck();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //clear audio recorder instance
        CAudioRecorder.getInstance().destroy();
        //clear text to speech instance
        CTTs.getInstance().destroy();
    }

    void initialiseuUi(){
        //get record button refrence
        btnRecorder=findViewById(R.id.btn_recordAudio);
    }

    //check audio permission
    void audioPermissionCheck(){
        retryAudioPermissionLimit=3;
        String[] permissions = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted , so request permission
            ActivityCompat.requestPermissions(this,permissions,91);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted , so request permission
            ActivityCompat.requestPermissions(this,permissions,92);
        }
    }

    //On request permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 91 || requestCode == 92) {

            // Checking whether user granted the permission or not.
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //check retry limit if within limit request permission again
                retryAudioPermissionLimit--;
                if(retryAudioPermissionLimit>0){
                    this.audioPermissionCheck();
                }
            }

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    void initialiseUiControls(){
        //for long click signal
        btnRecorder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isSpeakButtonLongPressed = true;
                if(CAudioRecorder.getInstance().isRecordingInProgress().get()==false){
                    CAudioRecorder.getInstance().startRecording(MainActivity.this,MainActivity.this);
                    btnRecorder.setText(R.string.release_to_record);
                    btnRecorder.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                return true;
            }
        });

        //for long click depress signal
        btnRecorder.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View pView, MotionEvent pEvent) {
                pView.onTouchEvent(pEvent);
                // We're only interested in when the button is released.
                if (pEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (isSpeakButtonLongPressed) {
                        //Stop recording when the button is released.
                        isSpeakButtonLongPressed = false;
                        CAudioRecorder.getInstance().stopRecording();
                        btnRecorder.setText(R.string.start_record);
                        btnRecorder.setTextColor(getResources().getColor(R.color.dark));
                    }
                }
                return false;
            }
        });

    }


    /**
     * This function will be called when there is a speech recognistion response
     */
    @Override
    public void speechRecognisionResponse(CBasicResponse response) {
        CLog.getInstance().v(TAG,response.toString());
    }
}
