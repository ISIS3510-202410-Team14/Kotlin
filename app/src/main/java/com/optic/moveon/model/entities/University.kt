package com.optic.moveon.model.entities

data class University(
    val id: Int,
    val name: String,
    val description: String,
    val rating: Float,
    val country: String,
    val imagen: String
)