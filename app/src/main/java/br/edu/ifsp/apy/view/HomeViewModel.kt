package br.edu.ifsp.apy.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifsp.apy.data.PlacesService
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _places = MutableStateFlow<List<String>>(emptyList())
    val places = _places.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun fetchNearbyDermatologists(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    _error.value = "Permissão de localização não concedida"
                    _isLoading.value = false
                    return@launch
                }

                val location = fusedLocationClient.lastLocation.await()

                if (location != null) {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://maps.googleapis.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val service = retrofit.create(PlacesService::class.java)

                    val response = service.buscarDermatologistas(
                        location = "${location.latitude},${location.longitude}",
                        radius = 3000,
                        type = "doctor",
                        keyword = "dermatologist",
                        apiKey = apiKey
                    )

                    if (response.isSuccessful) {
                        _places.value = response.body()?.results?.map {
                            "${it.name} - ${it.vicinity}"
                        } ?: emptyList()
                    } else {
                        _error.value = "Erro: ${response.message()}"
                    }
                } else {
                    _error.value = "Não foi possível obter a localização"
                }

            } catch (e: SecurityException) {
                _error.value = "Permissão negada: ${e.message}"
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


}

