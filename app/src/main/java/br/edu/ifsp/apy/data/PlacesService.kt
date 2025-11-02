package br.edu.ifsp.apy.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun buscarDermatologistas(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String = "doctor",
        @Query("keyword") keyword: String = "dermatologist",
        @Query("key") apiKey: String
    ): Response<PlacesResponse>
}
