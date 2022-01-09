package ru.desh.structuralfunctionalmodelcalculator.model.entity



data class TechnicalSystem(
    val systemId: Long,
    val systemName: String?,
    val systemIndicators: List<SFMIndicator>,
    var systemPriority: Int
)