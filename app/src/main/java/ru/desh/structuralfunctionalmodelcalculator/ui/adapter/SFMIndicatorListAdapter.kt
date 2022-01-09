package ru.desh.structuralfunctionalmodelcalculator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.ui.fragment.CreateICSFragment

class SFMIndicatorListAdapter(private val icsCreationDataController: CreateICSFragment.ICSCreationDataController) :
    RecyclerView.Adapter<SFMIndicatorListAdapter.SFMIndicatorViewHolder>() {

    class SFMIndicatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var indicatorName: TextView? = null
        var indicatorDescription: TextView? = null
        var indicatorPriority: TextView? = null
        var buttonDeleteIndicator: ImageButton? = null
        var buttonUpIndicatorPriority: ImageButton? = null
        var buttonDownIndicatorPriority: ImageButton? = null

        init {
            indicatorName = itemView.findViewById(R.id.sfm_indicator_name)
            indicatorDescription = itemView.findViewById(R.id.sfm_indicator_description)
            indicatorPriority = itemView.findViewById(R.id.indicator_priority)
            buttonDeleteIndicator = itemView.findViewById(R.id.button_delete_indicator)
            buttonUpIndicatorPriority = itemView.findViewById(R.id.button_up_indicator_priority)
            buttonDownIndicatorPriority = itemView.findViewById(R.id.button_down_indicator_priority)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SFMIndicatorViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sfm_indicator, parent, false)
        return SFMIndicatorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SFMIndicatorViewHolder, position: Int) {
        val indicator = icsCreationDataController.indicatorList[position]
        holder.indicatorName?.text = indicator.indicatorName
        holder.indicatorDescription?.text = indicator.indicatorDescription
        holder.indicatorPriority?.text = indicator.indicatorPriority.toString()

        holder.buttonDeleteIndicator?.setOnClickListener { icsCreationDataController.deleteIndicator(indicator)}
        holder.buttonUpIndicatorPriority?.setOnClickListener { icsCreationDataController.upIndicatorPriority(position)}
        holder.buttonDownIndicatorPriority?.setOnClickListener {icsCreationDataController.downIndicatorPriority(position)}
    }

    override fun getItemCount(): Int {
        return icsCreationDataController.indicatorList.size
    }
}