package com.sun.todo.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.RadioGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sun.todo.R
import com.sun.todo.adapters.TaskAdapter
import com.sun.todo.adapters.TaskClickListener
import com.sun.todo.databinding.FragmentAllTaskBinding
import com.sun.todo.viewmodel.TaskViewModel


class AllTaskFragmentFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        val binding = FragmentAllTaskBinding.inflate(inflater)

        binding.lifecycleOwner=this
        binding.viewModel=viewModel

        adapter= TaskAdapter(TaskClickListener { task ->
            findNavController().navigate(AllTaskFragmentFragmentDirections.actionAllTaskFragmentToUpdateFragment(task))
        })

        viewModel.getAllTask.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        binding.apply {
            binding.taskList.adapter=adapter
            fabBtn.setOnClickListener {
                findNavController().navigate(AllTaskFragmentFragmentDirections.actionAllTaskFragmentToAddFragment())
            }
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
        }).attachToRecyclerView(binding.taskList)
        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.task_menu,menu)
        val searchItem=menu.findItem(R.id.actionSearch)
        val doneItem = menu.findItem(R.id.actionDone)
        val todoItem = menu.findItem(R.id.actionTodo)
        doneItem.isVisible=true
        todoItem.isVisible=true
        val searchView=searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newTxt: String?): Boolean {
                if (newTxt!=null){
                    runQuery(newTxt)
                }
                return true
            }

        })
    }

    private fun runQuery(query: String) {
        val searchQuery= "%$query%"
        viewModel.searchData(searchQuery).observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.actionDefault -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    viewModel.getAllTask.observe(viewLifecycleOwner) { tasks ->
                        adapter.submitList(tasks)
                    }
                }
            }

            R.id.actionPriority -> {
                if(!item.isChecked){
                    item.isChecked = true
                    viewModel.getAllTaskByPriority.observe(viewLifecycleOwner) { tasks ->
                        adapter.submitList(tasks)
                    }
                }
            }

            R.id.actionDone ->{
                if(!item.isChecked){
                    item.isChecked = true
                    viewModel.getCompletedTasks.observe(viewLifecycleOwner) { tasks ->
                        adapter.submitList(tasks)
                    }
                }
            }

            R.id.actionTodo ->{
                if(!item.isChecked){
                    item.isChecked = true
                    viewModel.getIncompleteTask.observe(viewLifecycleOwner) { tasks ->
                        adapter.submitList(tasks)
                    }
                }
            }

            R.id.actionDelete -> deleteAllTasks()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllTasks() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes"){dialog, _ ->
                viewModel.deleteAll()
                dialog.dismiss()
            }.setNegativeButton("No"){dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }
}