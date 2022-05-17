package com.sun.todo.fragments
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.sun.todo.MainActivity
import com.sun.todo.R
import com.sun.todo.adapters.DashboardAdapter
import com.sun.todo.adapters.TaskClickListener
import com.sun.todo.databinding.FragmentDashboardBinding
import com.sun.todo.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
class DashboardFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: DashboardAdapter
    private val toDay = MaterialDatePicker.todayInUtcMilliseconds()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentDashboardBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getTodayIncompleteTask(toDay).observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }
        adapter = DashboardAdapter(TaskClickListener { task ->
            findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToUpdateFragment(task))
        })

        binding.apply {
            TodoBtn.setOnClickListener {
                findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToIncompleteTaskFragment())
                //val item = bottomNavigation.menu.findItem(R.id.allTaskFragment)
                //NavigationUI.onNavDestinationSelected(item, navController)
            }
            ProgressBtn.setOnClickListener {
                findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToChartFragment())
            }
            DoneBtn.setOnClickListener {
                findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToCompletedTaskFragment())
            }
            viewAllBtn.setOnClickListener {
                findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToTodayTasksFragment())
            }
            viewModel!!.progressOfTasks.observe(viewLifecycleOwner){ cont->
                Total.text= DecimalFormat("00").format(cont[0]+cont[1])
                Complete.text=DecimalFormat("00").format(cont[0])
                Incomplete.text=DecimalFormat("00").format(cont[1])
            }

            binding.DashboardRecycler.adapter=adapter
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
        }).attachToRecyclerView(binding.DashboardRecycler)
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.task_menu,menu)
        val searchItem=menu.findItem(R.id.actionSearch)
        val sort = menu.findItem(R.id.actionPriority)
        val option= menu.findItem(R.id.actionOption)
        val del = menu.findItem(R.id.actionDelete)
        val def =menu.findItem(R.id.actionDefault)
        searchItem.isVisible=false
        sort.isVisible=false
        del.isVisible=false
        option.isVisible=true
        def.isVisible=false
    }

}