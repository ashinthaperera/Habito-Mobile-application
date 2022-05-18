package com.sun.todo.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sun.todo.database.Task
import com.sun.todo.database.TaskDatabase
import com.sun.todo.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = TaskDatabase.getDatabase(application).taskDao()
    private val repository : TaskRepository = TaskRepository(taskDao)
    val getAllTask:LiveData<List<Task>> = repository.getAllTask()
    val getAllTaskByPriority:LiveData<List<Task>> = repository.getAllTaskByPriority()
    val getIncompleteTask:LiveData<List<Task>> = repository.getIncompleteTask()
    val getCompletedTasks:LiveData<List<Task>> = repository.getCompletedTasks()
    val getIncompleteTaskByPriority:LiveData<List<Task>> = repository.getIncompleteTaskByPriority()
    val getCompletedByPriority:LiveData<List<Task>> = repository.getCompletedByPriority()
    val progressOfTasks: LiveData<List<Int>> = repository.progressOfTasks()

    fun insert(task: Task){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertData(task)
        }
    }
    fun update(task: Task){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateData(task)
        }
    }
    fun delete(task: Task){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteItem(task)
        }
    }
    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllData()
        }
    }
    fun searchData(searchQuery: String):LiveData<List<Task>>{
        return repository.searchData(searchQuery)
    }
    fun searchTodayData(searchQuery: String,toDay: Long): LiveData<List<Task>>{
        return repository.searchTodayData(searchQuery,toDay)
    }
    fun getTodayIncompleteTask(toDay: Long):LiveData<List<Task>>{
        return  repository.getTodayIncompleteTask(toDay)
    }
    fun getTodayCompletedTasks(toDay: Long):LiveData<List<Task>>{
        return  repository.getTodayCompletedTasks(toDay)
    }
    fun getTodayAllTask(toDay: Long):LiveData<List<Task>>{
        return  repository.getTodayAllTask(toDay)
    }
    fun getTodayAllPriority(toDay: Long):LiveData<List<Task>>{
        return  repository.getTodayAllPriority(toDay)
    }
    fun deleteTodayAll(toDay: Long){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteTodayAll(toDay)
        }
    }
    fun deleteCompletedAll(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteCompletedAll()
        }
    }
    fun deleteIncompleteAll(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteIncompleteAll()
        }
    }

}