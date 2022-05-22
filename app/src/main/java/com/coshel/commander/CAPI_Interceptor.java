package com.coshel.commander;

import android.util.Log;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * This class is used to intercept all http connections
 */
public class CAPI_Interceptor implements Interceptor {
    final String tag = "CAPI_Interceptor";
    final CLog _log = CLog.getInstance();
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        _log.d(tag, String.format("--> Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

        _log.d(tag,request.body().contentType().toString());

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        _log.d(tag, String.format("<-- Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        MediaType contentType = response.body().contentType();
        String content = response.body().string();
        _log.d(tag, content);

        ResponseBody wrappedBody = ResponseBody.create(contentType, content);
        return response.newBuilder().body(wrappedBody).build();
    }
}

