package ru.desh.structuralfunctionalmodelcalculator.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

data class StructuralDivision(
    val divisionId: Long,
    val divisionName: String?,
    val divisionDescription: String?
)
