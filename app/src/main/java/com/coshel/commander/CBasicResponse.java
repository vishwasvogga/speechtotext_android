package com.coshel.commander;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * this class is used to parse the responses from the commander api end points
 */
public class CBasicResponse {

    //basic success/failure
    boolean success = false;
    //contains actuall data
    JSONObject data = null;
    //contains error reason if failure
    String err = "Unknown error";

    String tag = "CBasicResponse";

    /**
     * Parse the responses from the commander api end points
     * @param cresponse String
     */
    public void parseStringResponse(String cresponse){
        try{
            JSONObject jObject = new JSONObject(cresponse);
            success = jObject.getBoolean("success");
            data = jObject.getJSONObject("reply");
            err = jObject.getString("err");
        }catch (JSONException e){
            CLog.getInstance().e(tag,e.toString());
        }
    }

    @Override
    public String toString() {
        return "CBasicResponse{" +
                "success=" + success +
                ", data=" + data +
                ", err='" + err + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
