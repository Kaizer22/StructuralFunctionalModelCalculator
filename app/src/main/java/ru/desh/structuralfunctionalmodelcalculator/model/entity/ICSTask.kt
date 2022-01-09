package ru.desh.structuralfunctionalmodelcalculator.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ICSTask(
    val taskId: Long,
    val taskTitle: String?,
    val taskDescription: String?)