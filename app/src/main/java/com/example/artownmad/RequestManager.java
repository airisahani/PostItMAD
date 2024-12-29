package com.example.artownmad;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.artownmad.Models.NewsApiResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class RequestManager {
    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .client(new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "Bearer " + context.getString(R.string.api_key))
                                .build();
                        Log.d("Request Headers", request.headers().toString());
                        return chain.proceed(request);
                    })
                    .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public void getNewsHeadlines(OnFetchDataListener<NewsApiResponse> listener, String category, String query){
        CallNewsApi callNewsApi = retrofit.create(CallNewsApi.class);
        Call<NewsApiResponse> call = callNewsApi.callHeadlines("us", category, query, context.getString(R.string.api_key));

        Log.d("API URL", call.request().url().toString());

        try{
            call.enqueue(new Callback<NewsApiResponse>() {
                @Override
                public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
//                    if(!response.isSuccessful() || response.body() == null || response.body().getArticles() == null){
//                        Toast.makeText(context, "Error!! Unable to fetch data", Toast.LENGTH_SHORT).show();
//                    }

                    if (!response.isSuccessful()) {
                        Toast.makeText(context, "Error: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (response.body() == null) {
                        Toast.makeText(context, "Error: Response body is null", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (response.body().getArticles() == null) {
                        Toast.makeText(context, "Error: Articles list is null", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    listener.onFetchData(response.body().getArticles(), response.message());

                }

                @Override
                public void onFailure(Call<NewsApiResponse> call, Throwable throwable) {
                    listener.onError("Request Failed");

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public RequestManager(Context context){ this.context = context; }

    public interface CallNewsApi {
        @GET("top-headlines")
        Call<NewsApiResponse> callHeadlines(
                @Query("country") String country,
                @Query("category") String category,
                @Query("q") String query,
                @Query("apiKey") String api_key
        );
    }
}
