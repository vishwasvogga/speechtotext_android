package com.coshel.commander;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * This class is used to capture the audio
 */
public class CAudioRecorder {
    private static final CAudioRecorder ourInstance = new CAudioRecorder();

    /**
     * Get singleton instance
     * @return CAudioRecorder
     */
    public static CAudioRecorder getInstance() {
        return ourInstance;
    }

    private CAudioRecorder() {
    }

    private AudioRecord recorder;
    private static final int SAMPLE_RATE = 8000;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, AUDIO_FORMAT)*10;
    private Thread recordingThread;
    private static final AtomicBoolean recordingInProgress = new AtomicBoolean(false);
    private String tag="CAudioRecorder";
    final CLog _log = CLog.getInstance();

    private CAudioRecorderInterface cAudioRecorderInterface = null;


    /**
     * Get to know if recording is on progress
     * @return AtomicBoolean
     */
    AtomicBoolean isRecordingInProgress() {
        return recordingInProgress;
    }

    /**
     * Start recording
     * @param context context
     * @param _cAudioRecorderInterface CAudioRecorderInterface to get the updates from server
     */
    public void startRecording(Context context,CAudioRecorderInterface _cAudioRecorderInterface) {
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            _log.v(tag,"Recording started"+" Buffer size is "+BUFFER_SIZE);
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL, AUDIO_FORMAT, BUFFER_SIZE);
            cAudioRecorderInterface = _cAudioRecorderInterface;
            recorder.startRecording();
            recordingInProgress.set(true);
            recordingThread = new Thread(new StreamRecordingRunnable(), "Stream Recording Thread");
            recordingThread.start();
            return;
        }else{
            Toast.makeText(context,"Audio recording permission has been not granted",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Stop recording
     */
    public void stopRecording() {
        if (recorder == null) return;

        _log.v(tag,"Recording stopped");
        recordingInProgress.set(false);
        recorder.stop();
        recorder.release();
        recorder = null;
        recordingThread = null;
    }

    /**
     * Destroy the recorder obj and thread
     */
    public void destroy(){
        if(recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        if(recordingThread != null){
            recordingThread.interrupt();
            recordingThread = null;
        }
    }


    /*
     * ADD a Runnable to record and stream the voice data to Wit
     */
    private class StreamRecordingRunnable implements Runnable {
        @Override
        public void run() {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            //request body
            RequestBody requestBody = new RequestBody() {
                @Override
                public MediaType contentType() {
                    return MediaType.parse("audio/raw;encoding=signed-integer;bits=16;rate=8000;endian=little");
                }


                @Override
                public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                    _log.v(tag,"Fetching bytes");
                    while (recordingInProgress.get()) {

                        int result = recorder.read(buffer, BUFFER_SIZE);
                        if (result < 0) {
                            throw new RuntimeException("Reading of audio buffer failed: " +
                                    getBufferReadFailureReason(result));
                        }
                        bufferedSink.write(buffer);
                        buffer.clear();
                    }
                }
            };

            //send the request to endpoint
            Request request = CAPI.getInstance().getRequestBuilderForSpeechReco().post(requestBody).build();
            try{
                _log.v(tag,"Creating request");
                Response response = CAPI.getInstance().getHttpClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    _log.d(tag, responseData);
                    //send response to main thread
                    if(cAudioRecorderInterface != null){
                        CBasicResponse basicResponse = new CBasicResponse();
                        basicResponse.parseStringResponse(responseData);
                        cAudioRecorderInterface.speechRecognisionResponse(basicResponse);
                    }
                }
            } catch (IOException e) {
                _log.e("Streaming Resp error", e.getMessage());
                //send error to main thread
                if(cAudioRecorderInterface != null){
                    CBasicResponse basicResponse = new CBasicResponse();
                    cAudioRecorderInterface.speechRecognisionResponse(basicResponse);
                }
            }
        }

        private String getBufferReadFailureReason(int errorCode) {
            switch (errorCode) {
                case AudioRecord.ERROR_INVALID_OPERATION:
                    return "ERROR_INVALID_OPERATION";
                case AudioRecord.ERROR_BAD_VALUE:
                    return "ERROR_BAD_VALUE";
                case AudioRecord.ERROR_DEAD_OBJECT:
                    return "ERROR_DEAD_OBJECT";
                case AudioRecord.ERROR:
                    return "ERROR";
                default:
                    return "Unknown (" + errorCode + ")";
            }
        }
    }
}


