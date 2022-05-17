package com.sun.todo.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.sun.todo.BaseActivity
import com.sun.todo.R
import com.sun.todo.database.Task
import com.sun.todo.databinding.FragmentAddBinding
import com.sun.todo.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var baseActivity: BaseActivity
    private var date: Long? = null
    private var timeHour: Int? = null
    private var timeMin: Int? = null
    private lateinit var time: Calendar
    private val toDay = MaterialDatePicker.todayInUtcMilliseconds()
    private val  outPutDateFormat= SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone= TimeZone.getTimeZone("UTC")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding= FragmentAddBinding.inflate(inflater)
        val myAdapter=ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.priority)
        )
        binding.apply {
            spinner.adapter=myAdapter

            DateAddBtn.setOnClickListener {
                showDatePicker()
            }
            TimeAddBtn.setOnClickListener {
                if(DateInput.text==""){
                    AlertDialog.Builder(requireContext())
                        .setTitle("Warning")
                        .setMessage("Please select a date first!")
                        .setPositiveButton("Ok"){dialog, _ ->
                            dialog.dismiss()
                        }.create().show()
                }else{
                    showTimePicker()
                }
            }
            btnAdd.setOnClickListener{
                if (TextUtils.isEmpty(TaskTl.text)){
                    TaskTl.error = "Task name is empty!"
                    Toast.makeText(requireContext(),"Can't have empty fields!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(TextUtils.isEmpty(DateInput.text)){
                    DateInput.error = "Task date is empty!"
                    Toast.makeText(requireContext(),"Can't have empty fields!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(TextUtils.isEmpty(TimeInput.text)){
                    TimeInput.error = "Task time is empty!"
                    Toast.makeText(requireContext(),"Can't have empty fields!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                    val title = TaskTl.text.toString()
                    val priori = spinner.selectedItemPosition
                    val task = Task(
                        0,
                        title,
                        priori,
                        timeHour!!,
                        timeMin!!,
                        date!!,
                        false
                    )
                    viewModel.insert(task)
                    Toast.makeText(requireContext(), "Task is added", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(AddFragmentDirections.actionAddFragmentToAllTaskFragment())
            }

        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker() {

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Time")
            .build()
        timePicker.addOnPositiveButtonClickListener {
            time= Calendar.getInstance()
            if(date!=toDay){
                timeHour= timePicker.hour
                timeMin= timePicker.minute
                TimeInput.text= DecimalFormat("00:").format(timeHour)+ DecimalFormat("00").format(timeMin)
            }else if(time.get(Calendar.HOUR_OF_DAY)<timePicker.hour){
                timeHour= timePicker.hour
                timeMin= timePicker.minute
                TimeInput.text= DecimalFormat("00:").format(timeHour)+ DecimalFormat("00").format(timeMin)
            }else if(time.get(Calendar.HOUR_OF_DAY)==timePicker.hour && time.get(Calendar.MINUTE)<=timePicker.minute){
                timeHour= timePicker.hour
                timeMin= timePicker.minute
                TimeInput.text= DecimalFormat("00:").format(timeHour)+ DecimalFormat("00").format(timeMin)
            }else{
                AlertDialog.Builder(requireContext())
                    .setTitle("Invalid Time")
                    .setMessage("Please select a valid one!")
                    .setPositiveButton("Ok"){dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            }
        }
        timePicker.addOnNegativeButtonClickListener {
            Toast.makeText(requireContext(), "No time is selected!", Toast.LENGTH_SHORT).show()
        }
        timePicker.show(childFragmentManager,"Time Picker")

    }

    private fun showDatePicker() {
       val datePicker = MaterialDatePicker.Builder.datePicker()
           .setTitleText("Select Date")
           .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
           .build()

        datePicker.addOnPositiveButtonClickListener {
            if(it>=toDay) {
                date = it
                val dateSelected = outPutDateFormat.format(it)
                DateInput.text = dateSelected
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("Invalid Date")
                    .setMessage("Please select a valid one!")
                    .setPositiveButton("Ok"){dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            }
           }
        datePicker.addOnNegativeButtonClickListener {
            Toast.makeText(requireContext(), "No date is selected!", Toast.LENGTH_SHORT).show()
        }
        datePicker.show(childFragmentManager,"Date Picker")
    }
}