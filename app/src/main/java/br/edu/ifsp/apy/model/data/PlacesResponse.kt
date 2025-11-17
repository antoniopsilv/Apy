package br.edu.ifsp.apy.model.entity

data class PlacesResponse(
    val results: List<Place>
)

data class Place(
    val name: String,
    val vicinity: String
)