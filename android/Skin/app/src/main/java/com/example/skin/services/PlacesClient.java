package com.example.skin.services;

import com.example.skin.models.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesClient {
//    https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=19.0596,72.8295&radius=5000&types=hospital&key=AIzaSyCJsSEeJpAzMLkT-j4F2j1SxzOVJKIVabk

    @GET("/maps/api/place/nearbysearch/json")
    Call<Response> allHospitals(@Query("location") String location, @Query("radius") int radius, @Query("types") String types, @Query("key") String apiKey);
}
