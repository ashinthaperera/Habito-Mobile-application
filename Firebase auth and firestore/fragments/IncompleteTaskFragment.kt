package com.sun.todo.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.sun.todo.R
import com.sun.todo.adapters.TaskAdapter
import com.sun.todo.adapters.TaskClickListener
import com.sun.todo.databinding.FragmentIncompleteTaskBinding
import com.sun.todo.viewmodel.TaskViewModel

class IncompleteTaskFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter
    private val toDay = MaterialDatePicker.todayInUtcMilliseconds()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentIncompleteTaskBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getIncompleteTask.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }

        adapter = TaskAdapter(TaskClickListener { task ->
            findNavController().navigate(IncompleteTaskFragmentDirections.actionIncompleteTaskFragmentToUpdateFragment(task))
        })

        binding.apply {
            binding.incompleteTaskRecycler.adapter=adapter
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback( 0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val task=adapter.currentList[position]
                viewModel.delete(task)

                Snackbar.make(binding.root,"Deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.insert(task)
                    }
                    show()
                }
            }
        }).attachToRecyclerView(binding.incompleteTaskRecycler)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.task_menu,menu)
        //val searchItem=menu.findItem(R.id.actionSearch)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.actionDefault -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    viewModel.getIncompleteTask.observe(viewLifecycleOwner) { tasks ->
                        adapter.submitList(tasks)
                    }
                }
            }

            R.id.actionPriority ->{
                if(!item.isChecked){
                    item.isChecked = true
                    viewModel.getIncompleteTaskByPriority.observe(viewLifecycleOwner) { tasks ->
                        adapter.submitList(tasks)
                    }
                }
            }

            R.id.actionDelete -> deleteIncompleteAllTasks()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteIncompleteAllTasks() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes"){dialog, _ ->
                viewModel.deleteIncompleteAll()
                dialog.dismiss()
            }.setNegativeButton("No"){dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }
}