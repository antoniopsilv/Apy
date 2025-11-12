package br.edu.ifsp.apy.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifsp.apy.data.PlacesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    private val _places = MutableStateFlow<List<String>>(emptyList())
    val places = _places.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun fetchNearbyDermatologists(apiKey: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(PlacesService::class.java)

                val response = service.buscarDermatologistas(
                    location = "$latitude,$longitude",
                    radius = 3000,
                    type = "health",
                    keyword = "dermatologista",
                    language = "pt-BR",
                    apiKey = apiKey
                )

                if (response.isSuccessful) {
                    _places.value = response.body()?.results?.map {
                        "${it.name} - ${it.vicinity}"
                    } ?: emptyList()
                } else {
                    _error.value = "Erro: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Erro desconhecido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun clearPlaces() {
        _places.value = emptyList()
        _error.value = null
    }

}

