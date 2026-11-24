package com.limithue.familyguard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 家庭成员 */
@Entity(tableName = "family_members")
data class FamilyMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val role: String = ROLE_MEMBER,        // admin / member / cared
    val avatarText: String = "亲",
    val isOnline: Boolean = true,
    val battery: Int = 100,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val lastUpdate: Long = System.currentTimeMillis()
) {
    companion object {
        const val ROLE_ADMIN = "admin"
        const val ROLE_MEMBER = "member"
        const val ROLE_CARED = "cared"
    }
}

/** 位置记录 */
@Entity(tableName = "location_records")
data class LocationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long = 0L,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val altitude: Double = 0.0,
    val battery: Int = 100,
    val timestamp: Long = System.currentTimeMillis()
)

/** 告警记录 */
@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String = TYPE_FENCE,          // fence / night / still / offline / sos
    val level: Int = LEVEL_NORMAL,          // 0 普通 1 重要 2 紧急
    val memberName: String = "家庭成员",
    val content: String,
    val isHandled: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_FENCE = "fence"
        const val TYPE_NIGHT = "night"
        const val TYPE_STILL = "still"
        const val TYPE_OFFLINE = "offline"
        const val TYPE_SOS = "sos"

        const val LEVEL_NORMAL = 0
        const val LEVEL_IMPORTANT = 1
        const val LEVEL_URGENT = 2
    }
}

/** 电子围栏 */
@Entity(tableName = "geofences")
data class Geofence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float = 200f,
    val enabled: Boolean = true
)

/** 健康提醒 */
@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String = "medicine",          // medicine / water / checkup
    val timeOfDay: String = "08:00",
    val repeatRule: String = "daily",
    val enabled: Boolean = true
)
