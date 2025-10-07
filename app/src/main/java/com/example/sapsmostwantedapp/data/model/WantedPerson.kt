package com.example.sapsmostwantedapp.data.model

data class WantedPerson(
    val id: String?,
    val caption: String?,
    val schema: String?,
    val properties: Properties?,
    val target: Boolean? = false
)

data class Properties(
    val alias: List<String>?,
    val gender: List<String>?,
    val firstName: List<String>?,
    val secondName: List<String>?,
    val lastName: List<String>?,
    val name: List<String>?,
    val notes: List<String>?,
    val country: List<String>?,
    val sourceUrl: List<String>?,
    val topics: List<String>?,
    val hairColor: String?  )
