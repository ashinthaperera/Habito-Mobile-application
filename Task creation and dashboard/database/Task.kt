package com.sun.todo.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user_task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var title:String,
    var priority: Int,
    var timeHour: Int,
    var timeMin: Int,
    var taskdate: Long,
    var state: Boolean,
    ):Parcelable