package com.example.newsapp.Interface;

import com.example.newsapp.Model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ResponseInterface {

    String Base_Url = "https://newsapi.org/v2/";

    @GET("top-headlines")  // this will retreive all the data from the api and send the response to class (News)
    Call<News> getNews(    // this used to get the response

                           // the questionmark in the api url means we need query
                           @Query("country") String country,
                          // @Query("sources") String sources ,
                           @Query("apiKey") String apiKey
    );


    @GET("everything")
    Call<News> getNewsSearch(    // this used to get the response

                                 @Query("q") String keyword ,
                                 @Query("language") String language,
                                 @Query("sortBy") String sortby,
                                 @Query("apiKey") String apikey

    );

}
