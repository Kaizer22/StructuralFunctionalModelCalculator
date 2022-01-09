package ru.desh.structuralfunctionalmodelcalculator.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

data class SFMIndicator(
    var indicatorId: Long,
    var indicatorName: String,
    var indicatorDescription: String,
    var indicatorPriority: Int,
    var minValue: Float,
    var maxValue: Float
)