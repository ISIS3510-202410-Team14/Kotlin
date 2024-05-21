package com.optic.moveon.model.entities

import java.io.Serializable

data class Requerimiento(
    val cupos: Int?= null,
    val idioma: String?= null,
    val info: String?= null,
    val promedio: Double?= null,
    val visa: Boolean?= null
): Serializable