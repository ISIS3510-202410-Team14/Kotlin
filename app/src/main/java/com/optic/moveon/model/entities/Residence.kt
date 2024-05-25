package com.optic.moveon.model.entities

import java.io.Serializable

data class Residence(
    val id: Int ?= null,
    val name: String?= null,
    val description: String?= null,
    val price: Int ?= null,
    val country: String ?= null,
    val image: String ?= null,
    val siteUrl: String? = null

): Serializable