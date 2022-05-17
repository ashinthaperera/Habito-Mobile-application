package com.sun.todo.repository
import androidx.lifecycle.LiveData
import com.sun.todo.database.Task
import com.sun.todo.database.TaskDao
class TaskRepository(private val taskDao: TaskDao) {

    suspend fun insertData(task: Task)=taskDao.insert(task)
    suspend fun updateData(task: Task)=taskDao.update(task)
    suspend fun deleteItem(task: Task)=taskDao.delete(task)
    fun deleteAllData(){
        taskDao.deleteAll()
    }
    fun getAllTask(): LiveData<List<Task>> = taskDao.getAllTask()
    fun getAllTaskByPriority(): LiveData<List<Task>> = taskDao.getAllTaskByPriority()
    fun getIncompleteTask(): LiveData<List<Task>> = taskDao.getIncompleteTask()
    fun getCompletedTasks(): LiveData<List<Task>> = taskDao.getCompletedTasks()
    fun getIncompleteTaskByPriority(): LiveData<List<Task>> = taskDao.getIncompleteTaskByPriority()
    fun getCompletedByPriority(): LiveData<List<Task>> = taskDao.getCompletedByPriority()
    fun searchData(searchQuery: String): LiveData<List<Task>>{
        return taskDao.searchData(searchQuery)
    }
    fun searchTodayData(searchQuery: String,toDay: Long): LiveData<List<Task>>{
        return taskDao.searchTodayData(searchQuery,toDay)
    }
    fun progressOfTasks(): LiveData<List<Int>> = taskDao.progressOfTasks()
    fun getTodayIncompleteTask(toDay: Long):LiveData<List<Task>>{
        return taskDao.getTodayIncompleteTask(toDay)
    }
    fun getTodayCompletedTasks(toDay: Long):LiveData<List<Task>>{
        return taskDao.getTodayCompletedTasks(toDay)
    }
    fun getTodayAllTask(toDay: Long):LiveData<List<Task>>{
        return taskDao.getTodayAllTask(toDay)
    }
    fun getTodayAllPriority(toDay: Long):LiveData<List<Task>>{
        return taskDao.getTodayAllPriority(toDay)
    }
    fun deleteTodayAll(toDay: Long){
        taskDao.deleteTodayAll(toDay)
    }
    fun deleteCompletedAll(){
        taskDao.deleteCompletedAll()
    }
    fun deleteIncompleteAll(){
        taskDao.deleteIncompleteAll()
    }
}
