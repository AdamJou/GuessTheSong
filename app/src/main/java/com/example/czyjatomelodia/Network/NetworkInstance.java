package com.example.czyjatomelodia.Network;

import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;


public class NetworkInstance {


    private static  NetworkInstance mInstance;
    private static  final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    private static Retrofit retrofit;



    public  NetworkInstance(){

    retrofit = new Retrofit.Builder() .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
           .build();

    }

    public  static synchronized  NetworkInstance getInstance() {
        if(mInstance== null){
            mInstance = new NetworkInstance();
        }
        return  mInstance;
    }

    public GetDatService getAPI(){
        return retrofit.create(GetDatService.class);
    }



}
