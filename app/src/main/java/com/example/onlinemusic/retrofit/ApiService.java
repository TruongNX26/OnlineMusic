package com.example.onlinemusic.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("/songs")
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part data,
            @Part("name") String name,
            @Part("singer") String singer);

}
