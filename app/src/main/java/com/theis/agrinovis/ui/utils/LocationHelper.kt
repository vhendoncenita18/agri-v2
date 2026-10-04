package com.theis.agrinovis.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

object LocationHelper {
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(context: Context): Location? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return try {
            // Attempt fresh GPS fix; if null or delayed, instantly fallback to device's lastKnownLocation cache
            val currentLocation = try {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            } catch (_: Exception) {
                null
            }
            currentLocation ?: fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }
}
