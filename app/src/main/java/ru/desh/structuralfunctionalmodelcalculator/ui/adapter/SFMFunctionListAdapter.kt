package ru.desh.structuralfunctionalmodelcalculator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.ui.fragment.*

class SFMFunctionListAdapter(private val sfmFunctionsController: SFMFunctionsFragment.SFMFunctionsController) :
RecyclerView.Adapter<SFMFunctionListAdapter.SFMFunctionViewHolder>() {

    class SFMFunctionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var functionName: TextView? = null
        var functionDescription: TextView? = null
        var functionUnit: TextView? = null
        var functionPriority: TextView? = null
        var buttonDeleteFunction: ImageButton? = null
        var buttonUpFunctionPriority: ImageButton? = null
        var buttonDownFunctionPriority: ImageButton? = null

        init {
            functionName = itemView.findViewById(R.id.sfm_function_name)
            functionUnit = itemView.findViewById(R.id.sfm_function_unit_value)
            functionDescription = itemView.findViewById(R.id.sfm_function_description)
            functionPriority = itemView.findViewById(R.id.function_priority)
            buttonDeleteFunction = itemView.findViewById(R.id.button_delete_function)
            buttonUpFunctionPriority = itemView.findViewById(R.id.button_up_function_priority)
            buttonDownFunctionPriority = itemView.findViewById(R.id.button_down_function_priority)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SFMFunctionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sfm_function, parent, false)
        return SFMFunctionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SFMFunctionViewHolder, position: Int) {
        val function = sfmFunctionsController.getFunctionsList()[position]
        holder.functionName?.text = function.functionName
        holder.functionDescription?.text = function.functionDescription
        holder.functionUnit?.text = function.structuralDivision.divisionName
        holder.functionPriority?.text = function.functionPriority.toString()

        if (sfmFunctionsController.isBaseSFM()) {
            holder.buttonDownFunctionPriority?.visibility = View.VISIBLE
            holder.buttonUpFunctionPriority?.visibility = View.VISIBLE
            holder.buttonDeleteFunction?.visibility = View.VISIBLE

            holder.buttonDeleteFunction?.setOnClickListener {sfmFunctionsController.deleteFunction(function)}
            holder.buttonUpFunctionPriority?.setOnClickListener {sfmFunctionsController.increaseFunctionPriority(position)}
            holder.buttonDownFunctionPriority?.setOnClickListener {sfmFunctionsController.decreaseFunctionPriority(position)}
        }
        holder.itemView.setOnClickListener {
            val action = SFMFunctionsFragmentDirections.actionSFMFunctionsToTechnicalSystems()
            action.argIsBaseSfm = sfmFunctionsController.isBaseSFM()
            if (!action.argIsBaseSfm) {
                action.argRealSfmId = sfmFunctionsController.getCurrentSFM()!!.realSFMId
            }
            action.argSfmFunctionId = function.functionId
            action.argCurrentIcsId = sfmFunctionsController.getICSId()

            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return sfmFunctionsController.getFunctionsList().size
    }
}