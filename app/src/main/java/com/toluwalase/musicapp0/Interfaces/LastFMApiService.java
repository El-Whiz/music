package com.toluwalase.musicapp0.Interfaces;

import com.toluwalase.musicapp0.Customs.ArtistInfoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LastFMApiService {
    @GET("2.0/?method=artist.getinfo&format=json")
    Call<ArtistInfoResponse> getArtistInfo(
            @Query("artist") String artistName,
            @Query("api_key") String apiKey
    );
}
