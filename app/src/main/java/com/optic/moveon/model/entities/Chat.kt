package com.optic.moveon.model.entities

import java.io.Serializable


data class Chat(
    val name: String?= null,
    val mensaje: String ?= null,
    val hora: Long ?= null,
    var id: String ?= null,
    var enviado: Boolean = true
): Serializable


