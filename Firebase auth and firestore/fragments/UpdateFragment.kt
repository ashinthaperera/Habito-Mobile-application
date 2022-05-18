package com.sun.todo.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.sun.todo.R
import com.sun.todo.database.Task
import com.sun.todo.databinding.FragmentUpdateBinding
import com.sun.todo.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_update.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class UpdateFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private var newDate: Long? = null
    private var newTimeHour: Int? = null
    private var newTimeMin: Int? = null
    private lateinit var time: Calendar
    private val toDay = MaterialDatePicker.todayInUtcMilliseconds()
    private val  outPutDateFormat= SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone= TimeZone.getTimeZone("UTC")
    }
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding= FragmentUpdateBinding.inflate(inflater)
        val args= UpdateFragmentArgs.fromBundle(requireArguments())
        binding.apply {

            UpdateTask.setText(args.task.title)
            UpdateSpinner.setSelection(args.task.priority)
            TaskStateSpinner.setSelection(args.task.state.compareTo(false))
            UpdateDateInput.text = outPutDateFormat.format(args.task.taskdate)
            UpdateTimeInput.text= DecimalFormat("00:").format(args.task.timeHour)+DecimalFormat("00").format(args.task.timeMin)
            newDate=args.task.taskdate
            newTimeHour=args.task.timeHour
            newTimeMin=args.task.timeMin

            UpdateDateAddBtn.setOnClickListener {
                showDatePicker()
            }
            UpdateTimeAddBtn.setOnClickListener {
                if(newDate==args.task.taskdate){
                    AlertDialog.Builder(requireContext())
                        .setTitle("Warning")
                        .setMessage("Please update the date first!")
                        .setPositiveButton("Ok"){dialog, _ ->
                            dialog.dismiss()
                        }.create().show()
                }else{
                    showTimePicker()
                }
            }
            btnUpdate.setOnClickListener {
                if (TextUtils.isEmpty(UpdateTask.text)){
                    UpdateTask.error = "Task name is empty!"
                    Toast.makeText(requireContext(),"Can't have empty fields!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val taskUpdated=UpdateTask.text
                val newPriority = UpdateSpinner.selectedItemPosition
                val newState: Boolean = TaskStateSpinner.selectedItemPosition != 0
                val task = Task(
                    args.task.id,
                    taskUpdated.toString(),
                    newPriority,
                    newTimeHour!!,
                    newTimeMin!!,
                    newDate!!,
                    newState
                    )
                viewModel.update(task)
                Toast.makeText(requireContext(),"Task Updated", Toast.LENGTH_SHORT).show()
                findNavController().navigate(UpdateFragmentDirections.actionUpdateFragmentToAllTaskFragment())
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
            if(newDate!=toDay){
                newTimeHour= timePicker.hour
                newTimeMin= timePicker.minute
                UpdateTimeInput.text= DecimalFormat("00:").format(newTimeHour)+ DecimalFormat("00").format(newTimeMin)
            }else if(time.get(Calendar.HOUR_OF_DAY)<timePicker.hour){
                newTimeHour= timePicker.hour
                newTimeMin= timePicker.minute
                UpdateTimeInput.text= DecimalFormat("00:").format(newTimeHour)+ DecimalFormat("00").format(newTimeMin)
            }else if(time.get(Calendar.HOUR_OF_DAY)==timePicker.hour && time.get(Calendar.MINUTE)<=timePicker.minute){
                newTimeHour= timePicker.hour
                newTimeMin= timePicker.minute
                UpdateTimeInput.text= DecimalFormat("00:").format(newTimeHour)+ DecimalFormat("00").format(newTimeMin)
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
                newDate=it
                val dateSelected = outPutDateFormat.format(it)
                UpdateDateInput.text = dateSelected
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