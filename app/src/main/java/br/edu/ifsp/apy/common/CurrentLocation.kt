package br.edu.ifsp.apy.common

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest

fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
}

suspend fun getCurrentLocation(context: Context): LatLng {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    return try {
        if (hasLocationPermission(context)) {
            val location = fusedLocationClient.lastLocation.await()
            //         Avaliar a instrução acima que está retornando location dos USA.
/*               if (location != null) {
                    LatLng(location.latitude, location.longitude)
               } else {*/
                   LatLng(-23.55052, -46.633308) // fallback
/*                }*/
        } else {
            // Permissão não concedida, fallback
            LatLng(-23.55052, -46.633308)
        }
    } catch (e: SecurityException) {
        // Caso a permissão seja revogada em tempo de execução
        LatLng(-23.55052, -46.633308)
    } catch (e: Exception) {
        LatLng(-23.55052, -46.633308)
    }
}
