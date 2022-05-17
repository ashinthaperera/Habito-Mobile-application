package com.sun.todo.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sun.todo.database.Task
import com.sun.todo.databinding.TaskCardLayoutBinding
class TaskAdapter(private val clickListener: TaskClickListener): ListAdapter<Task, TaskAdapter.ViewHolder>(TaskDiffCallback) {

    companion object TaskDiffCallback: DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task)=oldItem.id==newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task)=oldItem==newItem
    }
    class ViewHolder(private val binding: TaskCardLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(task: Task, clickListener: TaskClickListener){
            binding.task=task
            binding.executePendingBindings()
            binding.clickListner=clickListener
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.ViewHolder {
      return ViewHolder(TaskCardLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    override fun onBindViewHolder(holder: TaskAdapter.ViewHolder, position: Int) {
        val current= getItem(position)
        holder.bind(current,clickListener)
    }
}
class TaskClickListener(val clickListener: (task: Task) -> Unit){
    fun onClick(task: Task) = clickListener(task)
}