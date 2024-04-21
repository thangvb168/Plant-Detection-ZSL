package com.example.plansdetection.service;

import com.example.plansdetection.constant.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {
    class AllAPIResponse implements Serializable {
        @SerializedName("status")
        String status;
        @SerializedName("totalResults")
        int totalResults;
        @SerializedName("results")
        List<ResponseAPI> results;

        public AllAPIResponse(String status, int totalResults, List<ResponseAPI> results) {
            this.status = status;
            this.totalResults = totalResults;
            this.results = results;
        }

        public String getStatus() {
            return status;
        }

        public int getTotalResults() {
            return totalResults;
        }

        public List<ResponseAPI> getResults() {
            return results;
        }
    }
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();
    APIService apiService = new Retrofit.Builder()
            .baseUrl(Constant.API_PATH)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(APIService.class);

    @GET("news")
    Call<AllAPIResponse> getAllNews(
            @Query("apikey") String apikey,
            @Query("country") String country,
            @Query("category") String category,
            @Query("q") String search
    );
}
