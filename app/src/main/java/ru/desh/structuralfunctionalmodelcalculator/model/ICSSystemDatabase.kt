package ru.desh.structuralfunctionalmodelcalculator.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.desh.structuralfunctionalmodelcalculator.model.dao.ICSDao
import ru.desh.structuralfunctionalmodelcalculator.model.entity.*

private const val DATABASE_NAME = "ics-calculator"

@Database (entities = [ICS::class], version = 2)
@TypeConverters(Converters::class)
abstract class ICSSystemDatabase: RoomDatabase() {
    abstract fun icsDao(): ICSDao
    companion object {

        @Volatile
        private var db: ICSSystemDatabase? = null

        fun getDB(applicationContext: Context): ICSSystemDatabase {
            return if(db == null) {
                synchronized(this) {
                    db = Room.databaseBuilder(
                        applicationContext,
                        ICSSystemDatabase::class.java,
                        DATABASE_NAME
                    //TODO doesn't allow main thread queries
                    ).allowMainThreadQueries().build()
                    db!!
                }
            } else {
                db!!
            }
        }
    }
}