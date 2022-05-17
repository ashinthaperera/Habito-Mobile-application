package com.sun.todo.fragments
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
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
import com.sun.todo.databinding.FragmentTodayTasksBinding
import com.sun.todo.viewmodel.TaskViewModel
class TodayTasksFragment : Fragment() {
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter
    private val toDay = MaterialDatePicker.todayInUtcMilliseconds()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTodayTasksBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getTodayAllTask(toDay).observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }
        adapter = TaskAdapter(TaskClickListener { task ->
            findNavController().navigate(TodayTasksFragmentDirections.actionTodayTasksFragmentToUpdateFragment(task))
        })

        binding.apply {
            binding.todayTaskRecycler.adapter=adapter
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
        }).attachToRecyclerView(binding.todayTaskRecycler)
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.task_menu,menu)
        val todoItem = menu.findItem(R.id.actionTodo)
        val searchItem=menu.findItem(R.id.actionSearch)
        val doneItem = menu.findItem(R.id.actionDone)
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
        viewModel.searchTodayData(searchQuery,toDay).observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.actionDefault -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    viewModel.getTodayAllTask(toDay).observe(viewLifecycleOwner) { tasks ->
                        adapter.submitList(tasks)
                    }
                }
            }

            R.id.actionPriority -> {
                if(!item.isChecked){
                    item.isChecked = true
                    viewModel.getTodayAllPriority(toDay).observe(viewLifecycleOwner) { tasks ->
                        adapter.submitList(tasks)
                    }
                }
            }

            R.id.actionTodo ->{
                if(!item.isChecked){
                    item.isChecked = true
                    viewModel.getTodayIncompleteTask(toDay).observe(viewLifecycleOwner) { tasks ->
                        adapter.submitList(tasks)
                    }
                }
            }

            R.id.actionDone ->{
                if(!item.isChecked){
                    item.isChecked = true
                    viewModel.getTodayCompletedTasks(toDay).observe(viewLifecycleOwner) { tasks ->
                        adapter.submitList(tasks)
                    }
                }
            }

            R.id.actionDelete -> deleteTodayAllTasks()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteTodayAllTasks() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes"){dialog, _ ->
                viewModel.deleteTodayAll(toDay)
                dialog.dismiss()
            }.setNegativeButton("No"){dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }
}