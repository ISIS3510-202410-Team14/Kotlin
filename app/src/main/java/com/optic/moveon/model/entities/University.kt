package com.optic.moveon.model.entities

data class University(
    val id: Int ?= null,
    val name: String?= null,
    val description: String?= null,
    val rating: Int ?= null,
    val country: String ?= null,
    val image: String ?= null

)