package com.theis.agrinovis.data.remote

import io.github.jan.supabase.storage.storage

object StorageService {
    private const val BUCKET_NAME = "lettuce-images"

    suspend fun uploadLettuceImage(fileName: String, imageBytes: ByteArray): String? {
        return try {
            val bucket = SupabaseClient.client.storage.from(BUCKET_NAME)
            bucket.upload(fileName, imageBytes) {
                upsert = true
            }
            bucket.publicUrl(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
