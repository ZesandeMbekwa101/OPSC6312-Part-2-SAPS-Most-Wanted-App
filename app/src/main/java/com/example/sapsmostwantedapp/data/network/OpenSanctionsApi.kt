package com.example.sapsmostwantedapp.data.network

import retrofit2.Response
import retrofit2.http.GET

interface OpenSanctionsApi {
    // returns raw NDJSON as a single string; we'll parse it line-by-line
    @GET("datasets/20251006/za_wanted/targets.nested.json")
    suspend fun getAllWantedPersons(): Response<String>
}