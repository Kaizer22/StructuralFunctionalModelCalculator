package ru.desh.structuralfunctionalmodelcalculator.model.dao

import androidx.room.*
import ru.desh.structuralfunctionalmodelcalculator.model.entity.ICS

@Dao
interface ICSDao {
    @Insert
    fun insertAll(vararg ICSs: ICS)

    @Delete
    fun delete(ICS: ICS)

    @Update
    fun update(ICS: ICS)

    @Query("SELECT * FROM ICS")
    fun getAll(): List<ICS>

    @Query("SELECT * FROM ICS WHERE icsSystemId = :icsId")
    fun getICSByID(icsId: Long): ICS
}