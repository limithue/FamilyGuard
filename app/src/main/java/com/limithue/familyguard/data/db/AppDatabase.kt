package com.limithue.familyguard.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.limithue.familyguard.data.model.Alert
import com.limithue.familyguard.data.model.FamilyMember
import com.limithue.familyguard.data.model.Geofence
import com.limithue.familyguard.data.model.LocationRecord
import com.limithue.familyguard.data.model.Reminder

@Database(
    entities = [
        FamilyMember::class,
        LocationRecord::class,
        Alert::class,
        Geofence::class,
        Reminder::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun familyDao(): FamilyDao
    abstract fun locationDao(): LocationDao
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "familyguard.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
