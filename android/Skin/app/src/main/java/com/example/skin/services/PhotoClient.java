package com.example.skin.services;

import com.example.skin.models.SkinImage;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PhotoClient {

    @Multipart
    @POST("/image/upload")
    Call<SkinImage> uploadPhoto(@Part MultipartBody.Part photo);
}
