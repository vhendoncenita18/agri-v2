package com.theis.agrinovis.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await

object LocationHelper {
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(context: Context): Location? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return try {
            val locationTask: Task<Location> = fusedLocationClient.lastLocation
            locationTask.await()
        } catch (e: Exception) {
            null
        }
    }
}
