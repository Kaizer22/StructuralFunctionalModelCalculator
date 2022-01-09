package ru.desh.structuralfunctionalmodelcalculator.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class ICS(
    @PrimaryKey
    val icsSystemId: Long,
    val projectName: String,
    val icsTasks: List<ICSTask>,
    val cashFlow: Float,
    val estimatedPeriodDays: Int,
    val creationDate: Date,
    val systemIndicators: List<SFMIndicator>,
    val baseSFM: MutableList<SFMFunction>,
    var realSFMs: MutableList<RealSFM>,
)