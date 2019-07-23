package com.jadaperkasa.mifaredesfire.service.rest;

//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class retroofitInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://www-stud.uni-due.de/~sjdiless/api/";


    public static Retrofit getRetrofitInstance() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).
                            addCallAdapterFactory(RxJava2CallAdapterFactory.create()).
                            build();
        }
        return retrofit;
    }



}
