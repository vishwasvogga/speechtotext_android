package com.coshel.commander;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CAPI {
    private static final CAPI ourInstance = new CAPI();

    public static CAPI getInstance() {
        return ourInstance;
    }

    private CAPI() {
        httpClient = new OkHttpClient();
    }

    private OkHttpClient httpClient;
    private HttpUrl.Builder httpBuilder;
    private Request.Builder httpRequestBuilder;


    /**
     * Request builder for Speech recognisation, ie sending audio data
     * @return Request.Builder
     */
    public Request.Builder getRequestBuilderForSpeechReco(){
        //httpBuilder = HttpUrl.parse("https://api.wit.ai/speech").newBuilder(); //Testing by hitting wit directly, you need to change the auth token accordingly
        httpBuilder = HttpUrl.parse("http://192.168.225.64:3001/api/v1/audio/file/submit").newBuilder();
        httpRequestBuilder = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Basic RilKQE5jUmZValhuMnI1dTplVGhWbVlxM3Q2dzl6JEMmRilKQE5jUmZValhuWnI0dQ=")
                .header("Content-Type", "audio/raw;encoding=signed-integer;bits=16;rate=8000;endian=little")
                .header("Transfer-Encoding", "chunked");
        return httpRequestBuilder;
    }

    /**
     * Ok http client
     * @return OkHttpClient
     */
    public OkHttpClient getHttpClient(){
        if(BuildConfig.DEBUG){
            //if debug, add interceptor
            return httpClient.newBuilder().addInterceptor(new CAPI_Interceptor()).build();
        }else{
            //if not debug remove interceptor
            return httpClient.newBuilder().build();
        }

    }
}




