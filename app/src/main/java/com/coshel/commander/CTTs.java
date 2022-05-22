package com.coshel.commander;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * This class helps in converting text to speech
 */
public class CTTs implements TextToSpeech.OnInitListener  {
    private static final CTTs ourInstance = new CTTs();

    public static CTTs getInstance() {
        return ourInstance;
    }

    private CTTs() {
    }

    TextToSpeech tts;
    Boolean isOkay=false;

    /**
     * Initialse
     * @param context context
     */
    void init(Context context){
        tts = new TextToSpeech(context, this);
    }


    /**
     * Clear tts obj
     */
    void destroy(){
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
    }


    /**
     * Convert text to speech
     * @param text text which is to be converted to speech
     * @param id id
     */
    void speak(String text,String id){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null,id);
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                CLog.getInstance().e("TTS", "This Language is not supported");
            }else{
                isOkay=true;
            }

        } else {  CLog.getInstance().e("TTS", "Initilization Failed!");}
    }
}
