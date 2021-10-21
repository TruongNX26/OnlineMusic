package com.example.onlinemusic.retrofit;

public class ApiUtils {

    private static String BASE_URL = "http://10.0.2.2:8080";

    public static ApiService getService() {
        return RetrofitClient.getRetrofit(BASE_URL).create(ApiService.class);
    }
}