package ru.desh.structuralfunctionalmodelcalculator.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.databinding.FragmentCreateIcsBinding
import ru.desh.structuralfunctionalmodelcalculator.model.ICSSystemDatabase
import ru.desh.structuralfunctionalmodelcalculator.model.dao.ICSDao
import ru.desh.structuralfunctionalmodelcalculator.model.entity.*
import ru.desh.structuralfunctionalmodelcalculator.ui.adapter.ICSTaskListAdapter
import ru.desh.structuralfunctionalmodelcalculator.ui.adapter.SFMIndicatorListAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class CreateICSFragment : Fragment() {
    private lateinit var binding: FragmentCreateIcsBinding
    private lateinit var taskList: MutableList<ICSTask>
    private lateinit var indicatorList: MutableList<SFMIndicator>


    private lateinit var icsDataController: ICSCreationDataController
    class ICSCreationDataController(
        private val icsDao: ICSDao,
        private val createICSFragment: CreateICSFragment,
        var taskList: MutableList<ICSTask>,
        var indicatorList: MutableList<SFMIndicator>
    )
    {
        fun createICS(ics: ICS) {
            icsDao.insertAll(ics)
        }

        fun deleteTask(task: ICSTask) {
            taskList.remove(task)
            createICSFragment.binding.taskList.adapter?.notifyItemRemoved(0)
            createICSFragment.updateTaskList()
        }

        fun deleteIndicator(sfmIndicator: SFMIndicator) {
            indicatorList.remove(sfmIndicator)
            var j = 0
            for (i in indicatorList) {
                i.indicatorPriority = j++
            }
            createICSFragment.binding.indicatorsList.adapter?.notifyItemRemoved(sfmIndicator.indicatorPriority)
            createICSFragment.updateIndicatorList()
        }

        fun upIndicatorPriority(position: Int) {
            if (position != 0) {
                val buf = indicatorList[position - 1]
                indicatorList[position - 1] = indicatorList[position]
                indicatorList[position] = buf

                indicatorList[position].indicatorPriority = position
                indicatorList[position - 1].indicatorPriority = position - 1

                createICSFragment.updateIndicatorList()
            } else {
                Toast.makeText(createICSFragment.requireContext(),
                    "Невозможно поднять приоритет показателя", Toast.LENGTH_LONG).show()
            }
        }

        fun downIndicatorPriority(position: Int) {
            if (position != indicatorList.size - 1) {
                val buf = indicatorList[position + 1]
                indicatorList[position + 1] = indicatorList[position]
                indicatorList[position] = buf

                indicatorList[position].indicatorPriority = position
                indicatorList[position + 1].indicatorPriority = position + 1

                createICSFragment.updateIndicatorList()
            } else {
                Toast.makeText(createICSFragment.requireContext(),
                    "Невозможно снизить приоритет показателя", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun updateTaskList() {
        binding.taskList.adapter!!.notifyDataSetChanged()
    }

    private fun updateIndicatorList() {
        binding.indicatorsList.adapter!!.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateIcsBinding.inflate(inflater, container, false)
        taskList = ArrayList()
        indicatorList = ArrayList()
        icsDataController = ICSCreationDataController(
            ICSSystemDatabase.getDB(requireContext()).icsDao(),
            this,
            taskList, indicatorList
        )
        initInteractions()
        return binding.root
    }

    private fun initInteractions() {
        binding.indicatorsList.adapter = SFMIndicatorListAdapter(icsDataController)
        binding.indicatorsList.layoutManager = LinearLayoutManager(requireContext())
        binding.taskList.adapter = ICSTaskListAdapter(icsDataController)
        binding.taskList.layoutManager = LinearLayoutManager(requireContext())

        binding.buttonAddTask.setOnClickListener {
            showCreateTaskDialog()
        }
        binding.buttonAddIndicator.setOnClickListener {
            showCreateIndicatorDialog()
        }
        binding.buttonCreateIcs.setOnClickListener {
            buildAndCreateICS()
        }
    }

    private fun showCreateTaskDialog() {
        val taskDialog = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val v = inflater.inflate(R.layout.dialog_create_task, null)
        val taskNameField = v.findViewById<EditText>(R.id.create_task_name_field)
        val taskDescription = v.findViewById<EditText>(R.id.create_task_description_field)

        taskDialog
            .setView(v)
            .setTitle("Описание задачи ИС")
            .setPositiveButton("Добавить") { dialog: DialogInterface?, which: Int ->
                val name = taskNameField.text.toString()
                val description = taskDescription.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(),
                        "Введите наименование задачи", Toast.LENGTH_LONG).show()
                } else {
                    val task = ICSTask(
                        0,
                        name,
                        description,
                    )
                    taskList.add(taskList.size, task)
                    binding.taskList.adapter?.notifyItemInserted(taskList.size - 1)
                    //updateIndicatorList()
                }
            }
            .setNegativeButton("Отмена") { dialog: DialogInterface?, _: Int -> dialog?.cancel()}
        taskDialog.create()
        taskDialog.show()
    }

    private fun showCreateIndicatorDialog() {
        val indicatorDialog = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val v = inflater.inflate(R.layout.dialog_create_indicator, null)
        val indicatorNameField = v.findViewById<EditText>(R.id.create_indicator_name_field)
        val indicatorDescription = v.findViewById<EditText>(R.id.create_indicator_description_field)

        indicatorDialog
            .setView(v)
            .setTitle("Создание параметра ИС")
            .setPositiveButton("Добавить") { dialog: DialogInterface?, which: Int ->
                val name = indicatorNameField.text.toString()
                val description = indicatorDescription.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(),
                        "Введите название параметра", Toast.LENGTH_LONG).show()
                } else {
                    val indicator = SFMIndicator(
                        Random.nextLong(),
                        name,
                        description,
                        indicatorList.size,
                        0F,
                        0F
                    )
                    indicatorList.add(indicatorList.size, indicator)
                    binding.indicatorsList.adapter?.notifyItemInserted(indicatorList.size)
                    updateIndicatorList()
                }
            }
            .setNegativeButton("Отмена") { dialog: DialogInterface?, _: Int -> dialog?.cancel()}
        indicatorDialog.create()
        indicatorDialog.show()
    }

    private fun buildAndCreateICS() {
        val icsName = binding.icsNameField.text.toString()
        val expectedCashFlow = binding.resourceConsumptionField.text.toString().toFloat()
        val estimatedPeriod = binding.estimatedTimeFrameField.text.toString().toInt()
        icsDataController.createICS(
            ICS(
                Random.nextLong(),
                icsName,
                taskList,
                expectedCashFlow,
                estimatedPeriod,
                Date(),
                indicatorList,
                mutableListOf(),
                mutableListOf()
            )
        )

        Toast.makeText(requireContext(), "Проект ИКС успешно создан", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.ICSListFragment)
    }
}
