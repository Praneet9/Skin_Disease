package com.example.skin.services;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PhotoClient {

    @Multipart
    @POST("/image/upload")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part photo);
}
