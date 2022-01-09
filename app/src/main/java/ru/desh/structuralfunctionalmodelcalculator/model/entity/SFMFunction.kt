package ru.desh.structuralfunctionalmodelcalculator.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

data class SFMFunction(
    val functionId: Long,
    val functionName: String,
    val functionDescription: String,
    var functionPriority: Int,
    val technicalSystems: MutableList<TechnicalSystem>,
    val structuralDivision: StructuralDivision
)