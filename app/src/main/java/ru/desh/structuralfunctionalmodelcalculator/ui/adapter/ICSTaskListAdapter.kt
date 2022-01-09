package ru.desh.structuralfunctionalmodelcalculator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.ui.fragment.CreateICSFragment
import ru.desh.structuralfunctionalmodelcalculator.ui.fragment.ICSListFragment

class ICSTaskListAdapter(private val icsCreationDataController: CreateICSFragment.ICSCreationDataController) :
    RecyclerView.Adapter<ICSTaskListAdapter.ICSTaskViewHolder>() {

    class ICSTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskName: TextView? = null
        var taskDescription: TextView? = null
        var buttonDeleteTask: ImageButton? = null

        init {
            taskName = itemView.findViewById(R.id.ics_task_name)
            taskDescription = itemView.findViewById(R.id.ics_task_description)
            buttonDeleteTask = itemView.findViewById(R.id.button_delete_ics_task)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ICSTaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ics_task, parent, false)
        return ICSTaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ICSTaskViewHolder, position: Int) {
        val task = icsCreationDataController.taskList[position]
        holder.taskName?.text = task.taskTitle
        holder.taskDescription?.text = task.taskDescription
        holder.buttonDeleteTask?.setOnClickListener { icsCreationDataController.deleteTask(task) }
    }

    override fun getItemCount(): Int {
        return icsCreationDataController.taskList.size
    }
}