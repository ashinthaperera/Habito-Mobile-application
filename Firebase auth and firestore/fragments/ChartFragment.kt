package com.sun.todo.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.sun.todo.R
import com.sun.todo.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlinx.android.synthetic.main.fragment_chart.view.*
import java.text.DecimalFormat
class ChartFragment : Fragment() {
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var chart: AnyChartView
    val state = listOf("Completed","Incomplete")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        val pie: Pie = AnyChart.pie()
        val dataPieChart: MutableList<DataEntry> = mutableListOf()
        chart= view.findViewById(R.id.pieChart)

        viewModel.progressOfTasks.observe(viewLifecycleOwner) { cont->
            val list = listOf(cont[0],cont[1])
            for(index in list.indices){
                dataPieChart.add(ValueDataEntry(state.elementAt(index),list.elementAt(index) ))
            }
            pie.data(dataPieChart)
           // pie.title("Progress")
            chart.setChart(pie)
        }
        viewModel.progressOfTasks.observe(viewLifecycleOwner){ cont->
            Total.text= DecimalFormat("00").format(cont[0]+cont[1])
            Complete.text= DecimalFormat("00").format(cont[0])
            Incomplete.text= DecimalFormat("00").format(cont[1])
        }

        view.refresh.setOnClickListener {
            //findNavController().navigate(ChartFragmentDirections.actionChartFragmentSelf())
        }
        return view

    }

}