package com.sun.todo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sun.todo.database.Task
import com.sun.todo.databinding.TaskCardDashboardBinding
import com.sun.todo.databinding.TaskCardLayoutBinding

class DashboardAdapter(private val clickListener: TaskClickListener): ListAdapter<Task, DashboardAdapter.ViewHolder>(TaskDiffCallback) {

    companion object TaskDiffCallback: DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task)=oldItem.id==newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task)=oldItem==newItem
    }

    class ViewHolder(private val binding: TaskCardDashboardBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(task: Task, clickListener: TaskClickListener){
            binding.task=task
            binding.executePendingBindings()
            binding.clickListner=clickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardAdapter.ViewHolder {
      return ViewHolder(TaskCardDashboardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: DashboardAdapter.ViewHolder, position: Int) {
        val current= getItem(position)
        holder.bind(current,clickListener)
    }
}
