package com.example.onlinemusic;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApi {

    @Multipart
    @POST("songs")
    Call<RequestBody> uploadImage(
            @Part MultipartBody.Part data,
            @Part("name") RequestBody name,
            @Part("singer") RequestBody singer);

}
