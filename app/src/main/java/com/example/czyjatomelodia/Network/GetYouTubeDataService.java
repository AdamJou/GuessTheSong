package com.example.czyjatomelodia.Network;

import com.example.czyjatomelodia.Model.VideoDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetYouTubeDataService {

    @GET("search")
    Call<VideoDetails> getVideoData(
            @Query("part") String part,
            @Query("q") String q,
            @Query("key") String key,
            @Query("order") String order,
            @Query("maxResults") int maxResult



    );


}
