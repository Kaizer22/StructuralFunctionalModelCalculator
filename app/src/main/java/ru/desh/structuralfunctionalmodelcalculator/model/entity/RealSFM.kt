package ru.desh.structuralfunctionalmodelcalculator.model.entity

import java.util.*

class RealSFM (
    val realSFMId: Long,
    val realSFMName: String,
    val creationDate: Date,
    val sfmFunctions: List<SFMFunction>,
    var qualityComprehensiveIndicator: Float)
