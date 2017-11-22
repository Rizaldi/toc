package com.gts.toc.rest;

import com.gts.toc.utility.GeneralConstant;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by warsono on 12/16/16.
 */
public class ApiInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original    = chain.request();
        Request request     = original.newBuilder()
                .header("X-App-Key", GeneralConstant.APP_KEY)
                .header("User-Agent", GeneralConstant.API_HEADER)
                .method(original.method(), original.body())
                .build();
        Response response   = chain.proceed(request);
        String bodyString   = response.body().string();

        return response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), bodyString))
                .build();
    }
}
