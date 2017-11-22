package com.gts.toc.rest;


import com.gts.toc.utility.GeneralConstant;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by warsono on 12/16/16.
 */
public class ApiClient {
    public static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if(retrofit==null){
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new ApiInterceptor()).build();
            retrofit  = new Retrofit.Builder()
                    .baseUrl(GeneralConstant.DOMAIN_SERVER)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
