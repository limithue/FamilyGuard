package com.limithue.familyguard.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.limithue.familyguard.data.model.Alert
import com.limithue.familyguard.data.model.FamilyMember
import com.limithue.familyguard.data.model.Geofence
import com.limithue.familyguard.data.model.LocationRecord
import com.limithue.familyguard.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
    @Query("SELECT * FROM family_members ORDER BY id ASC")
    fun getAll(): Flow<List<FamilyMember>>

    @Query("SELECT COUNT(*) FROM family_members")
    suspend fun count(): Int

    @Query("SELECT * FROM family_members WHERE id = :id")
    suspend fun getById(id: Long): FamilyMember?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: FamilyMember): Long

    @Update
    suspend fun update(member: FamilyMember)

    @Delete
    suspend fun delete(member: FamilyMember)
}

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: LocationRecord): Long

    @Query("SELECT * FROM location_records WHERE memberId = :memberId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getByMember(memberId: Long, limit: Int = 200): List<LocationRecord>

    @Query("SELECT * FROM location_records ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 200): List<LocationRecord>

    @Query("DELETE FROM location_records WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)
}

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Alert>>

    @Query("SELECT COUNT(*) FROM alerts WHERE isHandled = 0")
    fun unhandledCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: Alert): Long

    @Query("UPDATE alerts SET isHandled = 1 WHERE id = :id")
    suspend fun markHandled(id: Long)

    @Query("DELETE FROM alerts")
    suspend fun clearAll()
}

@Dao
interface GeofenceDao {
    @Query("SELECT * FROM geofences")
    fun getAll(): Flow<List<Geofence>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(geofence: Geofence): Long

    @Delete
    suspend fun delete(geofence: Geofence)
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders")
    fun getAll(): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Delete
    suspend fun delete(reminder: Reminder)
}
