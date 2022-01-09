package ru.desh.structuralfunctionalmodelcalculator.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.desh.structuralfunctionalmodelcalculator.model.entity.ICSTask
import ru.desh.structuralfunctionalmodelcalculator.model.entity.RealSFM
import ru.desh.structuralfunctionalmodelcalculator.model.entity.SFMFunction
import ru.desh.structuralfunctionalmodelcalculator.model.entity.SFMIndicator
import java.util.*

class Converters {
        @TypeConverter
        fun icsTasksFromString(value: String): List<ICSTask>?{
            val itemType = object : TypeToken<List<ICSTask>>() {}.type
            return Gson().fromJson(value, itemType)
        }

        @TypeConverter
        fun icsTaskToString(value: List<ICSTask>): String? {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun sfmIndicatorsFromString(value: String): List<SFMIndicator>?{
            val itemType = object : TypeToken<List<SFMIndicator>>() {}.type
            return Gson().fromJson(value, itemType)
        }

        @TypeConverter
        fun sfmIndicatorsToString(value: List<SFMIndicator>): String? {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun sfmFunctionsFromString(value: String): List<SFMFunction>?{
            val itemType = object : TypeToken<List<SFMFunction>>() {}.type
            return Gson().fromJson(value, itemType)
        }

        @TypeConverter
        fun sfmFunctionsToString(value: List<SFMFunction>): String? {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun dateFromTimestamp(value: Long?): Date? {
            return value?.let { Date(it) }
        }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? {
            return date?.time
        }

        @TypeConverter
        fun realSFMToString(value: List<RealSFM>): String? {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun realSFMFromString(value: String): List<RealSFM>? {
            val itemType = object : TypeToken<List<RealSFM>>() {}.type
            return Gson().fromJson(value, itemType)
        }

}