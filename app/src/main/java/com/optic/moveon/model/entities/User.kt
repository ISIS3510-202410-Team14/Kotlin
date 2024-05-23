package com.optic.moveon.model.entities

import java.io.Serializable

data class User(
    val id: String ?= null,
    val name: String ?= null,
    val email: String ?= null,
    val password: String ?= null,
    val areaOfStudy: String? = null,
    val homeUniversity: String? = null,
    val targetUniversity: String? = null,
    val languages: String? = null,
    val profileImageUrl: String? = null // URL de la imagen de perfil
): Serializable