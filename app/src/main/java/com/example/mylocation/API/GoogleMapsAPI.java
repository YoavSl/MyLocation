package com.example.mylocation.API;

import com.example.mylocation.Entities.PlacePredictions;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GoogleMapsAPI {

    @GET("place/autocomplete/json")
    public Call<PlacePredictions> getPlaceAutoComplete(
            @Query("input") String input,
            @Query("types") String types,
            @Query("language") String language,
            @Query("key") String key
    );
}
