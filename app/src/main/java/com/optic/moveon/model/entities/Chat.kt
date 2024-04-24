package com.optic.moveon.model.entities

import java.io.Serializable


data class Chat(
    val name: String?= null,
    val mensaje: String ?= null,
    val hora: Int ?= null,
    val id: String ?= null
): Serializable

