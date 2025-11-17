package br.edu.ifsp.apy.model.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun buscarDermatologistas(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String = "health",
        @Query("keyword") keyword: String = "dermatologista",
        @Query("language") language: String = "pt-BR",
        @Query("key") apiKey: String
    ): Response<PlacesResponse>
}
