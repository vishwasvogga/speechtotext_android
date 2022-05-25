package com.coshel.commander;

import android.content.Context;
import android.text.Spannable;
import android.text.format.DateFormat;
import android.text.style.TtsSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * This class is used to convert server actions to local actions/replies
 */
public class CReplyToAction {
    private static final CReplyToAction ourInstance = new CReplyToAction();

    /**
     * Get singleton instance
     * @return CReplyToAction
     */
    public static CReplyToAction getInstance() {
        return ourInstance;
    }

    private CReplyToAction() {
    }


    /**
     * Local reply from server action
     * @param action
     * @return JSONObject [id,text,type("tts")]
     * @throws JSONException
     */
    JSONObject getReplyFromAction(String action,String utter) throws JSONException {
        JSONObject obj = new JSONObject();
        //Random ID
        String id= String.valueOf(getRandomNumber(0,10000)) ;
        obj.put("id",id);
        if(utter != null){
            obj.put("utter",utter);
        }

        switch (action){
            case "tell_time" : obj.put("text",getTime());obj.put("type","tts");obj.put("type","tts"); break;
            default:obj.put("text","I could not recognise, please try again");obj.put("type","tts");break;
        }

        return obj;
    }


    /**
     * get Random ID between
     * @param min min number
     * @param max Max number
     * @return random int between max and min
     */
    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


    /**
     * Get time as string
     * @return
     */
    private String getTime(){
       return Calendar.getInstance().getTime().toString();
    }
}
