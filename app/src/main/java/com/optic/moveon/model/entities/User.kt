package com.optic.moveon.model.entities

import java.io.Serializable

data class User(
    val id: String ?= null,
    val name: String ?= null,
    val email: String ?= null,
    val password: String ?= null
): Serializable