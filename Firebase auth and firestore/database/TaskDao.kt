package com.sun.todo.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM user_task")
    fun deleteAll()

    @Query("SELECT * FROM user_task ORDER BY taskdate,timeHour,timeMin")
    fun getAllTask(): LiveData<List<Task>>

    @Query("SELECT * FROM user_task ORDER BY priority DESC,timeHour,timeMin")
    fun getAllTaskByPriority(): LiveData<List<Task>>

    @Query("SELECT * FROM user_task WHERE title LIKE :searchQuery ORDER BY taskdate,timeHour,timeMin")
    fun searchData(searchQuery: String): LiveData<List<Task>>

    @Query("SELECT COUNT(*) FROM user_task WHERE state=1 UNION ALL SELECT COUNT(*) FROM user_task WHERE state=0")
    fun progressOfTasks():LiveData<List<Int>>

    @Query("SELECT * FROM user_task WHERE taskdate=:toDay AND state=0 ORDER BY timeHour,timeMin")
    fun getTodayIncompleteTask(toDay: Long): LiveData<List<Task>>

    @Query("SELECT * FROM user_task WHERE taskdate=:toDay AND state=1 ORDER BY taskdate,timeHour,timeMin")
    fun getTodayCompletedTasks(toDay: Long): LiveData<List<Task>>

    @Query("SELECT * FROM user_task WHERE taskdate=:toDay ORDER BY timeHour,timeMin")
    fun getTodayAllTask(toDay: Long): LiveData<List<Task>>

    @Query("SELECT * FROM user_task WHERE taskdate=:toDay ORDER BY priority DESC,timeHour,timeMin")
    fun getTodayAllPriority(toDay: Long): LiveData<List<Task>>

    @Query("SELECT * FROM user_task WHERE taskdate=:toDay AND title LIKE :searchQuery ORDER BY taskdate,timeHour,timeMin")
    fun searchTodayData(searchQuery: String,toDay: Long): LiveData<List<Task>>

    @Query("DELETE FROM user_task WHERE taskdate=:toDay")
    fun deleteTodayAll(toDay: Long)

    @Query("SELECT * FROM user_task WHERE state=1 ORDER BY taskdate,timeHour,timeMin")
    fun getCompletedTasks(): LiveData<List<Task>>

    @Query("DELETE FROM user_task WHERE state=1")
    fun deleteCompletedAll()

    @Query("SELECT * FROM user_task WHERE state=1 ORDER BY priority DESC,taskdate,timeHour,timeMin")
    fun getCompletedByPriority(): LiveData<List<Task>>

    @Query("SELECT * FROM user_task WHERE state=0 ORDER BY taskdate,timeHour,timeMin")
    fun getIncompleteTask(): LiveData<List<Task>>

    @Query("DELETE FROM user_task WHERE state=0")
    fun deleteIncompleteAll()

    @Query("SELECT * FROM user_task WHERE state=0 ORDER BY priority DESC,taskdate,timeHour,timeMin")
    fun getIncompleteTaskByPriority(): LiveData<List<Task>>

}