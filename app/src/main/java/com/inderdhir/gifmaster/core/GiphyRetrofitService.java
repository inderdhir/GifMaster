package com.inderdhir.gifmaster.core;


import com.inderdhir.gifmaster.model.GifItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GiphyRetrofitService {

    @GET("trending")
    Call<List<GifItem>> getTrendingGifs(@Query("limit") final int fetchLimit,
                                        @Query("offset") final int offset);

    @GET("search")
    Call<List<GifItem>> searchForGifs(@Query("q") final String searchQuery,
                                      @Query("limit") final int fetchLimit,
                                      @Query("offset") final int offset);
}
